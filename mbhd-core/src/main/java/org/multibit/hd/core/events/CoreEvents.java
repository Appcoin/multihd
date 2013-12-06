package org.multibit.hd.core.events;

import org.multibit.hd.core.services.CoreServices;

import java.math.BigDecimal;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast core events</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class CoreEvents {

  /**
   * Utilities have a private constructor
   */
  private CoreEvents() {
  }

  /**
   * <p>Broadcast a new exchange rate change event</p>
   *
   * @param rate         The rate in the local currency (e.g. USD)
   * @param exchangeName The exchange name (e.g. "Bitstamp")
   */
  public static void fireExchangeRateChangeEvent(BigDecimal rate, String exchangeName) {

    CoreServices.uiEventBus.post(new ExchangeRateChangeEvent(rate, exchangeName));

  }

}