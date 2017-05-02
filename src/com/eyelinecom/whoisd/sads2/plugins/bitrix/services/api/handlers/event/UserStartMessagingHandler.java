package com.eyelinecom.whoisd.sads2.plugins.bitrix.services.api.handlers.event;

import com.eyelinecom.whoisd.sads2.plugins.bitrix.PluginContext;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.model.app.Application;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.services.Services;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.services.api.handlers.CommonEventHandler;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.services.api.handlers.EventHandler;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.services.db.dao.ApplicationController;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.utils.EncodingUtils;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.utils.ParamsExtractor;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.utils.TemplateUtils;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.utils.PrettyPrintUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * author: Artem Voronov
 */
public class UserStartMessagingHandler extends CommonEventHandler implements EventHandler {

  private static final Logger logger = Logger.getLogger("BITRIX_PLUGIN");
  private static final Logger loggerMessagingSads = Logger.getLogger("BITRIX_PLUGIN_MESSAGING_WITH_SADS");

  private final ApplicationController applicationController;

  public UserStartMessagingHandler() {
    Services services = PluginContext.getInstance().getServices();
    this.applicationController = services.getApplicationController();
  }

  @Override
  public void processEvent(Map<String, String[]> parameters) {
    final String domain = ParamsExtractor.getDomain(parameters);
    Application application = applicationController.find(domain);
    if (application == null)
      return;

    final String userId = ParamsExtractor.getUserId(parameters);
    final String serviceId = ParamsExtractor.getServiceId(parameters);
    final String protocol = ParamsExtractor.getProtocol(parameters);

    if (logger.isDebugEnabled())
      logger.debug("User start messaging. User ID: " + userId + ". Service: " + serviceId + ". Protocol: " + protocol);
  }

  @Override
  public void sendResponse(Map<String, String[]> parameters, HttpServletResponse response) {
    if (loggerMessagingSads.isDebugEnabled())
      loggerMessagingSads.debug("USER_START_MESSAGING request:\n" + PrettyPrintUtils.toPrettyMap(parameters) + "\n");

    final String deployUrl = PluginContext.getInstance().getPluginUrl();
    final String domain = ParamsExtractor.getDomain(parameters);
    final String userId = ParamsExtractor.getUserId(parameters);
    final String serviceId = ParamsExtractor.getServiceId(parameters);
    final String protocol = ParamsExtractor.getProtocol(parameters);
    final String lang = ParamsExtractor.getLanguage(parameters);

    final String backPageOriginal = ParamsExtractor.getBackPageUrl(parameters);
    final String encodedAndEscapedBackPageOriginal = EncodingUtils.encode(EncodingUtils.escape(backPageOriginal));

    final String redirectBackPageUrl = TemplateUtils.createRedirectBackPageUrl(deployUrl, domain, userId, serviceId, protocol, lang, encodedAndEscapedBackPageOriginal);
    final String escapedRedirectBackPageUrl = EncodingUtils.escape(redirectBackPageUrl);
    final String encodedAndEscapedRedirectBackPageUrl = EncodingUtils.encode(escapedRedirectBackPageUrl);
    final String inputTitle = getLocalizedMessage(lang, "init.message.for.user");
    final String inputUrl = TemplateUtils.createInputUrl(deployUrl, domain, lang, encodedAndEscapedRedirectBackPageUrl);
    final String escapedInputUrl = EncodingUtils.escape(inputUrl);
    final String xml = TemplateUtils.createBasicPage(inputTitle, escapedInputUrl, escapedRedirectBackPageUrl, lang);

    if (loggerMessagingSads.isDebugEnabled())
      loggerMessagingSads.debug("USER_START_MESSAGING response:\n" + PrettyPrintUtils.toPrettyXml(xml));

    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    try {
      try (PrintWriter out = response.getWriter()) {
        out.write(xml);
      }
    } catch (IOException ex) {
      loggerMessagingSads.error("Error during user start messaging event", ex);
    }
  }
}
