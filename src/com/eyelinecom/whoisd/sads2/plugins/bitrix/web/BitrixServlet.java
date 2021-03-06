package com.eyelinecom.whoisd.sads2.plugins.bitrix.web;

import com.eyelinecom.whoisd.sads2.plugins.bitrix.services.bitrix.handlers.event.EventProcessor;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.services.bitrix.model.Event;
import com.eyelinecom.whoisd.sads2.plugins.bitrix.utils.ParamsExtractor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * author: Artem Voronov
 */
public class BitrixServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    handleRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    handleRequest(request, response);
  }

  private static void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Map<String, String[]> params = request.getParameterMap();

    if (isTest(params)) {
      processTestRequest(response);
      return;
    }

    Event event = ParamsExtractor.getEvent(params);
    EventProcessor.process(event, params, response);
  }

  private static boolean isTest(Map<String, String[]> params) {
    return params.containsKey("test_req");
  }

  private static void processTestRequest(HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("text; charset=utf-8");
    try (PrintWriter out = response.getWriter()) {
      out.write("bitrix plugin has started");
    }
  }
}
