package com.eyelinecom.whoisd.sads2.plugins.bitrix.services.bitrix.handlers;

import java.util.Map;

/**
 * author: Artem Voronov
 */
public interface CommandHandler {

  void handle(Map<String, String[]> parameters);
}
