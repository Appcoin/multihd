package org.multibit.hd.ui.views.components.text_fields;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.select_recipient.RecipientComboBoxEditor;

import javax.swing.*;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThemeAwareRecipientInputVerifierTest {

  private ContactService contactService;

  private NetworkParameters networkParameters;

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    contactService = mock(ContactService.class);

    networkParameters = BitcoinNetwork.current().get();
  }

  @Test
  public void testVerifyText_NoContacts() throws Exception {

    List<Contact> allContacts = Lists.newArrayList();

    // Arrange
    when(contactService.allContacts()).thenReturn(allContacts);
    when(contactService.filterContactsForSingleMatch(anyString(),anyBoolean())).thenReturn(Optional.<Contact>absent());

    JComboBox<Recipient> comboBox = ComboBoxes.newRecipientComboBox(contactService, BitcoinNetwork.current().get());

    ThemeAwareRecipientInputVerifier testObject = new ThemeAwareRecipientInputVerifier(contactService);

    RecipientComboBoxEditor.RecipientComboBoxTextField comboEditor = ((RecipientComboBoxEditor.RecipientComboBoxTextField) comboBox.getEditor().getEditorComponent());

    // Act
    comboBox.setSelectedItem("");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(" ");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgdfjkt");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYa");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("LUvKN4hTw29NS4wNVZ13RBCCQYurRaoLz9");
    assertThat(testObject.verify(comboEditor)).isTrue();

    // Use a public domain P2SH address
    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tUB");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU");
    assertThat(testObject.verify(comboEditor)).isTrue();


  }

  @Test
  public void testVerifyRecipients() throws Exception {

    final List<Contact> allContacts = Lists.newArrayList();

    // Arrange
    when(contactService.allContacts()).thenReturn(allContacts);

    JComboBox<Recipient> comboBox = ComboBoxes.newRecipientComboBox(contactService, BitcoinNetwork.current().get());

    ThemeAwareRecipientInputVerifier testObject = new ThemeAwareRecipientInputVerifier(contactService);

    RecipientComboBoxEditor.RecipientComboBoxTextField comboEditor = ((RecipientComboBoxEditor.RecipientComboBoxTextField) comboBox.getEditor().getEditorComponent());

    // Act
    comboBox.setSelectedItem(new Recipient(new Address(networkParameters, "LUvKN4hTw29NS4wNVZ13RBCCQYurRaoLz9")));
    assertThat(testObject.verify(comboEditor)).isTrue();


    comboBox.setSelectedItem(new Recipient(new Address(networkParameters, "35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU")));
    assertThat(testObject.verify(comboEditor)).isTrue();

  }

}