<%@ include file='/jcore/doInitPage.jspf' %>
<%@ page import="com.jalios.util.Util" %>

<jsp:useBean id="configHandler" scope="page" class="co.kozao.jcmsplugin.zaomeetreserve.handler.SaveConfigHandler">
  <jsp:setProperty name="configHandler" property="request" value="<%= request %>"/>
  <jsp:setProperty name="configHandler" property="response" value="<%= response %>"/>
  <jsp:setProperty name="configHandler" property="*"/>
</jsp:useBean>
<%
  if (configHandler.validate()) {
    String redirectUrl = configHandler.getRedirect();
    if (Util.isEmpty(redirectUrl)) {
      redirectUrl = request.getContextPath() + "/";
    }
    sendRedirect(redirectUrl);
    return;
  }
  String back = request.getHeader("Referer");
  if (Util.isEmpty(back)) {
    back = configHandler.getRedirect();
  }
  sendRedirect(back);
  return;
  
  
%>