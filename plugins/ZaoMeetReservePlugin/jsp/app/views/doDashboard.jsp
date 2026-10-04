<%@page import="java.util.Map"%>
<%@page import="com.jalios.util.Util"%>

<div class="text-right" style="margin-bottom:14px;">
  <form method="get" action="<%= appHandler.getAppUrl() %>" class="form-inline">
    <input type="hidden" name="view" value="DASHBOARD">
    <select name="statsPeriod" class="form-control" onchange="this.form.submit()">
      <option value="WEEK" <%= "WEEK".equalsIgnoreCase(appHandler.getStatsPeriod()) ? "selected" : "" %>><%= glp("jcmsplugin.zaomeetreserve.dashboard.period.week") %></option>
      <option value="MONTH" <%= "MONTH".equalsIgnoreCase(appHandler.getStatsPeriod()) ? "selected" : "" %>><%= glp("jcmsplugin.zaomeetreserve.dashboard.period.month") %></option>
      <option value="QUARTER" <%= "QUARTER".equalsIgnoreCase(appHandler.getStatsPeriod()) ? "selected" : "" %>><%= glp("jcmsplugin.zaomeetreserve.dashboard.period.quarter") %></option>
      <option value="YEAR" <%= "YEAR".equalsIgnoreCase(appHandler.getStatsPeriod()) ? "selected" : "" %>><%= glp("jcmsplugin.zaomeetreserve.dashboard.period.year") %></option>
    </select>
  </form>
</div>

<div class="row">
  <div class="col-sm-4">
    <div class="panel panel-default text-center">
      <div class="panel-body">
        <p class="text-muted"><%= glp("jcmsplugin.zaomeetreserve.dashboard.stat.avg-occupancy") %></p>
        <h2><%= appHandler.getAverageOccupancyRate() %>%</h2>
      </div>
    </div>
  </div>
  <div class="col-sm-4">
    <div class="panel panel-default text-center">
      <div class="panel-body">
        <p class="text-muted"><%= glp("jcmsplugin.zaomeetreserve.dashboard.stat.reservations-this-week") %></p>
        <h2><%= appHandler.getReservationCountThisWeek() %></h2>
      </div>
    </div>
  </div>
  <div class="col-sm-4">
    <div class="panel panel-default text-center">
      <div class="panel-body">
        <p class="text-muted"><%= glp("jcmsplugin.zaomeetreserve.dashboard.stat.most-requested-room") %></p>
        <h3><%= Util.notEmpty(appHandler.getMostRequestedRoomTitle()) ? appHandler.getMostRequestedRoomTitle() : "-" %></h3>
      </div>
    </div>
  </div>
</div>

<div class="row">
  <div class="col-sm-6">
    <div class="panel panel-default">
      <div class="panel-heading"><%= glp("jcmsplugin.zaomeetreserve.dashboard.chart.occupancy-by-room") %></div>
      <div class="panel-body">
        <% for (Map.Entry<String, Integer> entry : appHandler.getOccupancyRateByRoom().entrySet()) { %>
        <p><%= entry.getKey() %></p>
        <div class="progress">
          <div class="progress-bar" role="progressbar" style="width:<%= entry.getValue() %>%;"><%= entry.getValue() %>%</div>
        </div>
        <% } %>
      </div>
    </div>
  </div>

  <div class="col-sm-6">
    <div class="panel panel-default">
      <div class="panel-heading"><%= glp("jcmsplugin.zaomeetreserve.dashboard.chart.reservations-per-week") %></div>
      <div class="panel-body">
        <%
        Map<String, Integer> perWeek = appHandler.getReservationCountPerWeek();
        int maxCount = 1;
        for (int v : perWeek.values()) { maxCount = Math.max(maxCount, v); }
        int weekIndex = 1;
        for (Map.Entry<String, Integer> entry : perWeek.entrySet()) {
            int widthPct = (int) Math.round((entry.getValue() * 100.0) / maxCount);
        %>
        <p><%= glp("jcmsplugin.zaomeetreserve.dashboard.chart.week-label", weekIndex++) %></p>
        <div class="progress">
          <div class="progress-bar progress-bar-info" role="progressbar" style="width:<%= widthPct %>%;"><%= entry.getValue() %></div>
        </div>
        <% } %>
      </div>
    </div>
  </div>
</div>