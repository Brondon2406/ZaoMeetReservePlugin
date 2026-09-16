<%@ include file='/jcore/doInitPage.jspf' %>
<%@ include file="/front/app/doAppCommon.jspf" %>

<div class="booking-sidebar is-closed sidebar-tabbed sidebar-component hide" data-jalios-sidebar-direction="right" style="width: 1120px; margin-left:5%; margin-right:20%;"></div>
<div class="ajax-refresh-div" data-jalios-ajax-refresh-url="plugins/ZaoMeetReservePlugin/jsp/app/zaoMeetReserveApp.jsp">
  <%@ include file='/plugins/ZaoMeetReservePlugin/jsp/app/doZaoMeetReserveApp.jspf' %>
</div>

<%@ include file='/jcore/doFooter.jspf' %>



