/**
 * Copyright 2011 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.multibit.hd.ui.platform.builder.mac;

import org.multibit.hd.ui.platform.GenericApplication;
import org.multibit.hd.ui.platform.GenericApplicationSpecification;
import org.multibit.hd.ui.platform.builder.generic.DefaultApplicationBuilder;
import org.multibit.hd.ui.platform.handler.DefaultAboutHandler;
import org.multibit.hd.ui.platform.handler.DefaultOpenURIHandler;
import org.multibit.hd.ui.platform.handler.DefaultPreferencesHandler;
import org.multibit.hd.ui.platform.handler.DefaultQuitHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 1.0.0
 *         
 */
public class MacApplicationBuilder {

  private static final Logger log = LoggerFactory.getLogger(MacApplicationBuilder.class);

  /**
   * <p>Builds either a {@link org.multibit.hd.ui.platform.GenericApplication} if the platform does not provide EAWT support, or a {@link MacApplication} if it does.</p>
   * <p>Requires reflective code to avoid introducing Apple-specific code. The direct equivalent operation is as follows:</p>
   * <pre>
   *
   *  Application application = Application.getApplication();
   *  application.setAboutHandler(new AboutHandler() {
   *      void handleAbout(AppEvent.AboutEvent event) {
   *          // Fire the internal about handler event
   *      }
   *  });
   *
   *  application.setOpenURIHandler(new OpenURIHandler() {
   *      void openURI(AppEvent.OpenURIEvent event) {
   *          // Fire the internal open file event
   *      }
   *  });
   *
   *  application.setOpenFileHandler(new OpenFilesHandler() {
   *      void openFiles(AppEvent.OpenFilesEvent event) {
   *          // Fire the internal open file event
   *      }
   *  });
   *
   *  ...
   *
   * </pre>
   *
   * @param specification The specification containing the listeners
   *
   * @return A {@link org.multibit.hd.ui.platform.GenericApplication}
   */
  @SuppressWarnings("unchecked")
  public GenericApplication build(GenericApplicationSpecification specification) {

    Object nativeApplication;
    Class nativeOpenURIHandlerClass;
    Class nativePreferencesHandlerClass;
    Class nativeAboutHandlerClass;
    Class nativeQuitHandlerClass;

    try {
      // Attempt to locate the Apple Java JDK
      final File file = new File("/System/Library/Java");
      if (file.exists()) {
        // Create a suitable class loader
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        Class systemClassLoaderClass = systemClassLoader.getClass();
        // Determine if class loading by URL is supported
        if (URLClassLoader.class.isAssignableFrom(systemClassLoaderClass)) {
          // Get the addURL method from the class loader
          Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
          addUrl.setAccessible(true);
          // Load the Apple JDK classes
          addUrl.invoke(systemClassLoader, file.toURI().toURL());
        }
      }

      // Must have successfully loaded the Apple JDK or its not there

      // Try to loadContacts the EAWT support classes
      Class nativeApplicationClass = Class.forName("com.apple.eawt.Application");
      // Instantiate through getApplication()
      nativeApplication = nativeApplicationClass.getMethod("getApplication", new Class[0]).invoke(null);

      // Create native instances of the various handlers
      nativeOpenURIHandlerClass = Class.forName("com.apple.eawt.OpenURIHandler");
      nativePreferencesHandlerClass = Class.forName("com.apple.eawt.PreferencesHandler");
      nativeAboutHandlerClass = Class.forName("com.apple.eawt.AboutHandler");
      nativeQuitHandlerClass = Class.forName("com.apple.eawt.QuitHandler");

    } catch (ClassNotFoundException e) {
      return new DefaultApplicationBuilder().build(specification);
    } catch (IllegalAccessException e) {
      return new DefaultApplicationBuilder().build(specification);
    } catch (NoSuchMethodException e) {
      return new DefaultApplicationBuilder().build(specification);
    } catch (InvocationTargetException e) {
      return new DefaultApplicationBuilder().build(specification);
    } catch (MalformedURLException e) {
      return new DefaultApplicationBuilder().build(specification);
    }

    // Must have successfully configured the EAWT library to be here

    log.debug("Found EAWT libraries so building native bridge");
    MacApplication macApplication = new MacApplication();
    macApplication.setApplication(nativeApplication);

    // Open URI handler
    if (!specification.getOpenURIEventListeners().isEmpty()) {
      log.debug("Adding the DefaultOpenURIHandler");
      // Native handler
      macApplication.setOpenURIHandlerClass(nativeOpenURIHandlerClass);
      // Listeners
      DefaultOpenURIHandler openURIHandler = new DefaultOpenURIHandler();
      openURIHandler.addListeners(specification.getOpenURIEventListeners());
      macApplication.addOpenURIHandler(openURIHandler);
    }

    // Preferences handler
    if (!specification.getPreferencesEventListeners().isEmpty()) {
      log.debug("Adding the DefaultPreferencesHandler");
      // Native handler
      macApplication.setPreferencesHandlerClass(nativePreferencesHandlerClass);
      // Listeners
      DefaultPreferencesHandler preferencesHandler = new DefaultPreferencesHandler();
      preferencesHandler.addListeners(specification.getPreferencesEventListeners());
      macApplication.addPreferencesHandler(preferencesHandler);
    }

    // About handler
    if (!specification.getAboutEventListeners().isEmpty()) {
      log.debug("Adding the DefaultAboutHandler");
      // Native handler
      macApplication.setAboutHandlerClass(nativeAboutHandlerClass);
      // Listeners
      DefaultAboutHandler aboutHandler = new DefaultAboutHandler();
      aboutHandler.addListeners(specification.getAboutEventListeners());
      macApplication.addAboutHandler(aboutHandler);
    }

    // Quit handler
    if (!specification.getQuitEventListeners().isEmpty()) {
      log.debug("Adding the DefaultQuitHandler");
      // Native handler
      macApplication.setQuitHandlerClass(nativeQuitHandlerClass);
      // Listeners
      DefaultQuitHandler quitHandler = new DefaultQuitHandler();
      quitHandler.addListeners(specification.getQuitEventListeners());
      macApplication.addQuitHandler(quitHandler);
    }

    return macApplication;

  }

}

