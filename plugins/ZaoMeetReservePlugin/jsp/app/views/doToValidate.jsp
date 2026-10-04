<%@page import="java.util.List"%>
<%@page import="com.jalios.util.Util"%>
<%@page import="generated.Reservation"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>

<%
List<Reservation> pending = appHandler.getPendingReservationsForCurrentPage();
int totalPending = appHandler.getPendingReservationsCount();
int currentPage = appHandler.getToValidatePage();
int totalPages = appHandler.getToValidateTotalPages();

Map<String, String> authors = new LinkedHashMap<>();
for (Reservation r : ZaoMeetReserveManager.getInstance().getPendingReservations()) {
  if (Util.notEmpty(r.getAuthor())) authors.put(r.getAuthor().getId(), r.getAuthor().getFullName());
}

String[] authorIds = new String[authors.size() + 1];
String[] authorLabels = new String[authors.size() + 1];
authorIds[0] = "all";
authorLabels[0] = glp("jcmsplugin.zaomeetreserve.app.view.to-validate.filter.member.all");
int authorIdx = 1;
for (Map.Entry<String, String> entry : authors.entrySet()) {
  authorIds[authorIdx] = entry.getKey();
  authorLabels[authorIdx] = entry.getValue();
  authorIdx++;
}
%>

<form method="get" action="<%= appHandler.getAppUrl() %>">
  <input type="hidden" name="view" value="TO_VALIDATE">
  <div class="row">
    <div class="col-sm-3">
      <jalios:field name="toValidateMemberFilter" label='<%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.filter.member") %>'
                    value='<%= appHandler.getToValidateMemberFilter() %>'>
        <jalios:control settings='<%= new EnumerateSettings().select().enumValues(authorIds).enumLabels(authorLabels) %>' />
      </jalios:field>
    </div>
    <div class="col-sm-3">
      <jalios:field name="toValidateStartDate" label='<%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.filter.date-start") %>'
                    value='<%= appHandler.getToValidateStartDate() %>'>
        <jalios:control settings="<%= new DateSettings() %>" />
      </jalios:field>
    </div>
    <div class="col-sm-3">
      <jalios:field name="toValidateEndDate" label='<%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.filter.date-end") %>'
                    value='<%= appHandler.getToValidateEndDate() %>'>
        <jalios:control settings="<%= new DateSettings() %>" />
      </jalios:field>
    </div>
    <div class="col-sm-3">
      <div class="form-group">
        <div class="input-group">
          <input type="text" class="form-control" name="catalogSearchTerm"
                 value="<%= Util.notEmpty(appHandler.getCatalogSearchTerm()) ? appHandler.getCatalogSearchTerm() : "" %>"
                 placeholder="<%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.filter.search.placeholder") %>">
          <span class="input-group-btn">
            <button type="submit" class="btn btn-default"><jalios:icon src="search" /></button>
          </span>
        </div>
      </div>
   </div>
  </div>
  
</form>

<div class="panel panel-default">
  <div class="panel-heading"><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.header.count", totalPending) %></div>

  <% if (Util.isEmpty(pending)) { %>
    <div class="panel-body"><jalios:appBodyNoResult text='<%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.empty") %>' /></div>
  <% } else { %>
  <div class="table-responsive">
	  <table class="table table-hover">
	    <thead>
	      <tr>
	        <th><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.col.member") %></th>
	        <th><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.col.room") %></th>
	        <th><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.col.date") %></th>
	        <th><%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.col.slot") %></th>
	        <th></th>
	      </tr>
	    </thead> 
	    <tbody>
	      <jalios:foreach name="itReservation" type="Reservation" collection="<%= pending %>">
	      <%
	        Reservation nextReservation = (Reservation) itReservation;
	        boolean isPast = appHandler.isReservationPast(nextReservation);
	      %>
	      <tr class="<%= isPast ? "zaomeetreserve-row-past" : "" %>">
	        <td><%= Util.notEmpty(nextReservation.getAuthor()) ? nextReservation.getAuthor().getFullName() : "-" %></td>
	        <td><%= Util.notEmpty(nextReservation.getSalle()) ? nextReservation.getSalle().getTitle() : "-" %></td>
	        <td><jalios:date date="<%= nextReservation.getDateheureDebut() %>" format="short" /></td>
	        <td>
	          <% if (Util.notEmpty(nextReservation.getDateheureDebut()) && Util.notEmpty(nextReservation.getDateheureFin())) { %>
	         <jalios:date date="<%= nextReservation.getDateheureDebut() %>" format="HH:mm" />
			 <jalios:icon src="glyph: icomoon-minus3" />
		     <jalios:date date="<%= nextReservation.getDateheureFin() %>" format="HH:mm" />
				<% } else { %><jalios:icon src="glyph: icomoon-minus3" /><% } %>
	        </td>
	        <td class="text-right">
	          <div class="dropdown">
	            <button class="btn btn-default btn-xs dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	              <jalios:icon src="glyph: icomoon-more2" />
	            </button>
	            <ul class="dropdown-menu dropdown-menu-right">
	              <li>
	                <form method="post" action="plugins/ZaoMeetReservePlugin/jsp/actions/doApproveReservation.jsp">
	                  <input type="hidden" name="reservationId" value="<%= nextReservation.getId() %>">
	                  <input type="hidden" name="opApprove" value="true">
	                  <button type="submit" class="btn-link"><jalios:icon src="ok" /> <%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.action.approve") %></button>
	                </form>
	              </li>
	              <li role="presentation" class="divider"></li>
	              <li>
	                <a href="#" class="modal text-danger" data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/doRejectReservation.jsp?reservationId=<%= nextReservation.getId() %>">
	                  <jalios:icon src="cancel" /> <%= glp("jcmsplugin.zaomeetreserve.app.view.to-validate.action.reject") %>
	                </a>
	              </li>
	            </ul>
	          </div>
	        </td>
	      </tr>
	      </jalios:foreach>
	    </tbody>
	  </table>
	</div>
  <% if (totalPages > 1) { %>
  <div class="zaomeetreserve-pagination">
    <% if (currentPage > 1) { %>
      <a class="zaomeetreserve-pg-nav-btn" href="<%= appHandler.getAppUrl() %>?view=TO_VALIDATE&toValidatePage=<%= currentPage - 1 %>">
        <jalios:icon src="arrow-left" />
      </a>
    <% } %>
    <% for (int p = 1; p <= totalPages; p++) { %>
      <a class="zaomeetreserve-pg-num <%= p == currentPage ? "zaomeetreserve-pg-num-active" : "" %>"
         href="<%= appHandler.getAppUrl() %>?view=TO_VALIDATE&toValidatePage=<%= p %>"><%= p %></a>
    <% } %>
    <% if (currentPage < totalPages) { %>
      <a class="zaomeetreserve-pg-nav-btn" href="<%= appHandler.getAppUrl() %>?view=TO_VALIDATE&toValidatePage=<%= currentPage + 1 %>">
        <jalios:icon src="arrow-right" />
      </a>
    <% } %>
  </div>
  <% } %>

  <% } %>
</div>