<%@page import="java.util.List"%>
<%@page import="com.jalios.util.Util"%>
<%@page import="com.jalios.jcms.taglib.settings.impl.DateSettings"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.util.CalendarViewModel.TimelineRow"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.util.CalendarViewModel.TimelineBlock"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.util.CalendarViewModel.MonthCell"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.util.ZaoMeetReserveConstants"%>

<%
String calTab = appHandler.getCalTab();
if (Util.isEmpty(calTab)) {
  calTab = "jour";
}
boolean isDay = "jour".equalsIgnoreCase(calTab);
boolean isWeek = "semaine".equalsIgnoreCase(calTab);
int tlStart = ZaoMeetReserveConstants.CALENDAR_TIMELINE_START_HOUR;
int tlEnd = ZaoMeetReserveConstants.CALENDAR_TIMELINE_END_HOUR;
String calDateStr = appHandler.getCalDateStr() != null ? appHandler.getCalDateStr() : "";
String calDateParam = encodeForURL(calDateStr);
%>

<div class="zaomeetreserve-subtabs">
  <a class="<%= isDay ? "zaomeetreserve-active" : "" %>"
     href="<%= appHandler.getAppUrl() %>?view=CALENDAR&calTab=jour&calDateStr=<%= calDateParam %>">
    <%= glp("jcmsplugin.zaomeetreserve.calendar.tab.day") %>
  </a>
  <a class="<%= isWeek ? "zaomeetreserve-active" : "" %>"
     href="<%= appHandler.getAppUrl() %>?view=CALENDAR&calTab=semaine&calDateStr=<%= calDateParam %>">
    <%= glp("jcmsplugin.zaomeetreserve.calendar.tab.week") %>
  </a>
  <a class="<%= (!isDay && !isWeek) ? "zaomeetreserve-active" : "" %>"
     href="<%= appHandler.getAppUrl() %>?view=CALENDAR&calTab=mois&calDateStr=<%= calDateParam %>">
    <%= glp("jcmsplugin.zaomeetreserve.calendar.tab.month") %>
  </a>
</div>

<div class="zaomeetreserve-cal-nav-bar">
  <a class="zaomeetreserve-cal-nav-btn" href="<%= appHandler.getCalendarPrevUrl() %>">
    &lsaquo; <%= glp("jcmsplugin.zaomeetreserve.calendar.nav.previous") %>
  </a>

  <form method="get" action="<%= appHandler.getAppUrl() %>" class="form-inline" onchange="this.submit();">
    <input type="hidden" name="view" value="CALENDAR">
    <input type="hidden" name="calTab" value="<%= encodeForHTMLAttribute(calTab) %>">
    <jalios:field name="calDateStr" value="<%= appHandler.getCalDate() %>">
	  <jalios:control settings='<%= new DateSettings() %>' />
	</jalios:field>
    <noscript><button type="submit" class="btn btn-default"><jalios:icon src="search" /></button></noscript>
  </form>

  <a class="zaomeetreserve-cal-nav-btn" href="<%= appHandler.getCalendarNextUrl() %>">
    <%= glp("jcmsplugin.zaomeetreserve.calendar.nav.next") %> &rsaquo;
  </a>
</div>

<% if (isDay || isWeek) {
     List<TimelineRow> rows = isDay ? appHandler.getDayTimelineRows() : appHandler.getWeekTimelineRows();
%>
<div class="zaomeetreserve-card zaomeetreserve-card-body">
  <div class="zaomeetreserve-timeline-hours">
    <% for (int h = tlStart; h < tlEnd; h++) { %><div class="zaomeetreserve-tl-hour"><%= h %>h</div><% } %>
  </div>
  <% for (TimelineRow row : rows) { %>
  <div class="zaomeetreserve-timeline-row">
    <div class="zaomeetreserve-tl-label"><%= encodeForHTML(row.getLabel()) %></div>
    <div class="zaomeetreserve-tl-track">
      <% for (TimelineBlock block : row.getBlocks()) {
           String pastClass = block.isPast() ? "zaomeetreserve-tl-block-past" : "";
      %>
      <div class="zaomeetreserve-tl-block zaomeetreserve-tl-block-<%= block.getType() %> <%= pastClass %>"
           style="left:<%= block.getLeftPercent() %>%; width:<%= block.getWidthPercent() %>%;"
           title="<%= encodeForHTMLAttribute(row.getLabel()) %> &mdash; <%= encodeForHTMLAttribute(block.getLabel()) %>">
        <%= encodeForHTML(block.getLabel()) %>
      </div>
      <% } %>
    </div>
  </div>
  <% } %>
  <div class="zaomeetreserve-slot-legend">
    <span><i class="zaomeetreserve-dot-confirmed"></i><%= glp("jcmsplugin.zaomeetreserve.calendar.legend.confirmed") %></span>
    <span><i class="zaomeetreserve-dot-pending"></i><%= glp("jcmsplugin.zaomeetreserve.calendar.legend.pending") %></span>
    <% if (isDay) { %>
    <span><i class="zaomeetreserve-dot-off"></i><%= glp("jcmsplugin.zaomeetreserve.calendar.legend.unavailable") %></span>
    <% } %>
    <span><i class="zaomeetreserve-dot-past"></i><%= glp("jcmsplugin.zaomeetreserve.calendar.legend.past") %></span>
  </div>
</div>

<% } else { // mois
     List<MonthCell> monthCells = appHandler.getMonthCells();
%>
<div class="zaomeetreserve-card zaomeetreserve-card-body">
  <div class="zaomeetreserve-cal-grid zaomeetreserve-cal-head-row">
    <div class="zaomeetreserve-cal-head"><%= glp("jcmsplugin.zaomeetreserve.calendar.weekday-short.mon") %></div>
    <div class="zaomeetreserve-cal-head"><%= glp("jcmsplugin.zaomeetreserve.calendar.weekday-short.tue") %></div>
    <div class="zaomeetreserve-cal-head"><%= glp("jcmsplugin.zaomeetreserve.calendar.weekday-short.wed") %></div>
    <div class="zaomeetreserve-cal-head"><%= glp("jcmsplugin.zaomeetreserve.calendar.weekday-short.thu") %></div>
    <div class="zaomeetreserve-cal-head"><%= glp("jcmsplugin.zaomeetreserve.calendar.weekday-short.fri") %></div>
    <div class="zaomeetreserve-cal-head"><%= glp("jcmsplugin.zaomeetreserve.calendar.weekday-short.sat") %></div>
    <div class="zaomeetreserve-cal-head"><%= glp("jcmsplugin.zaomeetreserve.calendar.weekday-short.sun") %></div>
  </div>
  <div class="zaomeetreserve-cal-grid">
    <% for (MonthCell cell : monthCells) {
         String pastClass = cell.getDayNumber() != 0 && cell.isPast() ? "zaomeetreserve-cal-cell-past" : "";
    %>
    <% if (cell.getDayNumber() == 0) { %>
    <div class="zaomeetreserve-cal-cell zaomeetreserve-cal-cell-empty"></div>
    <% } else if (cell.hasReservations()) { %>
    <div class="zaomeetreserve-cal-cell zaomeetreserve-cal-cell-has-res <%= pastClass %>" data-jalios-action="modal"
         data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/doDayReservations.jsp?calDay=<%= cell.getDateStr() %>">
      <div class="zaomeetreserve-cal-cell-day"><%= cell.getDayNumber() %></div>
      <span class="zaomeetreserve-cal-cell-count"><%= cell.getReservationCount() %> <%= glp("jcmsplugin.zaomeetreserve.calendar.month.count-suffix") %></span>
    </div>
    <% } else { %>
    <div class="zaomeetreserve-cal-cell <%= pastClass %>">
      <div class="zaomeetreserve-cal-cell-day"><%= cell.getDayNumber() %></div>
    </div>
    <% } %>
    <% } %>
  </div>
</div>
<% } %>