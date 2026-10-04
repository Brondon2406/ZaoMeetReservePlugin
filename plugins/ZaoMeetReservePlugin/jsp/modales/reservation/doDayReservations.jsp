<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title><%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="com.jalios.util.Util"%>
<%@page import="co.zao.jcmsplugin.meetreserve.ZaoMeetReserveConstants"%>
<%@page import="co.zao.jcmsplugin.meetreserve.ZaoMeetReserveReservationManager"%>
<%@page import="generated.ZaoMeetReserveReservation"%>
<%@ include file='/jcore/doInitPage.jspf' %>


<%
String calDay = request.getParameter("calDay");
DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
DateFormat timeFormat = channel.getTimeFormat(userLang);
Date selectedDay = null;
try {
    selectedDay = Util.notEmpty(calDay) ? isoFormat.parse(calDay) : new Date();
} catch (Exception e) {
    selectedDay = new Date();
}
List<Reservation> dayReservations = ZaoMeetReserveManager.getInstance().getReservationsForDay(selectedDay);
DateFormat dayFormat = channel.getDateFormat(userLang);
String modalTitle = glp("jcmsplugin.zaomeetreserve.calendar.day-modal.title", dayFormat.format(selectedDay));
%>

<jalios:modal title="<%= modalTitle %>" css="modal-md">
  <table class="zaomeetreserve-table">
    <thead>
      <tr>
        <th><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.rooms.header.label") %></th>
        <th><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.col.member") %></th>
        <th><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.col.slot") %></th>
        <th><%= glp("jcmsplugin.zaomeetreserve.room.field.status") %></th>
      </tr>
    </thead>
    <tbody>
      <% if (Util.isEmpty(dayReservations)) { %>
      <tr><td colspan="4" class="zaomeetreserve-empty-row"><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.empty") %></td></tr>
      <% }
      for (Reservation r : dayReservations) {
          boolean confirmed = r.getPstatus() == ZaoMeetReserveConstants.RESERVATION_WF_CONFIRMED_PSTATUS;
          String badgeClass = confirmed ? "zaomeetreserve-badge-green" : "zaomeetreserve-badge-orange";
          String statusLabel = confirmed ? glp("jcmsplugin.zaomeetreserve.calendar.legend.confirmed") : glp("jcmsplugin.zaomeetreserve.calendar.legend.pending");
      %>
      <tr>
        <td><%= Util.notEmpty(r.getRoom()) ? r.getRoom().getTitle() : "-" %></td>
        <td><%= Util.notEmpty(r.getAuthor()) ? r.getAuthor().getFullName() : "-" %></td>
        <td>
          <% if (Util.notEmpty(r.getDateStart()) && Util.notEmpty(r.getDateEnd())) { %>
            <%= timeFormat.format(r.getDateStart()) %> &ndash; <%= timeFormat.format(r.getDateEnd()) %>
          <% } else { %>-<% } %>
        </td>
        <td><span class="zaomeetreserve-badge <%= badgeClass %>"><%= statusLabel %></span></td>
      </tr>
      <% } %>
    </tbody>
  </table>
</jalios:modal>
</head>
<body>

</body>
</html>