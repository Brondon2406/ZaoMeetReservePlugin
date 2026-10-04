<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.jalios.jcms.taglib.settings.impl.EnumerateSettings" %>
<%@ page import="co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager" %>
<%@ page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler" %>
<%@ page import="generated.Reservation" %>
<%@ page import="com.jalios.jcms.taglib.settings.impl.DateSettings" %>
<%
	// VUE "MES RÉSERVATIONS" (maquette : view-mesReservations)
	ZaoMeetReserveAppHandler appHandler = (ZaoMeetReserveAppHandler) request.getAttribute("jcmsplugin.zaomeetreserve.appHandler");
	List<Reservation> reservations = (List<Reservation>) request.getAttribute("jcmsplugin.zaomeetreserve.listReservations");
	if (reservations == null) {
		reservations = new ArrayList<Reservation>();
	}

	String sortField = getUntrustedStringParameter("myres_sort", "startDate");
	boolean descending = Util.toBoolean(getUntrustedStringParameter("myres_reverse", "false"), false);
	reservations = ZaoMeetReserveManager.getInstance().getReservationsOrderByFields(reservations, sortField, descending);
	if (reservations == null) {
		reservations = new ArrayList<Reservation>();
	}

	SimpleDateFormat hourFmt = new SimpleDateFormat("HH'h'mm");
%>

<div class="zaomeetreserve-scope">

	<div class="text-right" style="margin-bottom:14px;">
		<a href="#" class="modal btn btn-primary"
		   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/addReservationModal.jsp">
			<jalios:icon src="glyph: icomoon-plus3" /> <%= glp("jcmsplugin.zaomeetreserve.modal.add-reservation.title") %>
		</a>
	</div> 

	<%-- Filtres : Statut, Date début, Date fin, Recherche --%>
	<form method="get" action="<%= appHandler.getAppUrl() %>" class="form-vertical">
		<input type="hidden" name="view" value="MY_RESERVATIONS">
		<div class="row">
			<div class="col-sm-3">
				<jalios:field name="filterByReservationStatus"
					label='<%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.filter.status") %>'
					value='<%= appHandler.getFilterByReservationStatus() %>'>
					<jalios:control settings='<%= new EnumerateSettings().select()
						.enumValues(new String[]{"all", "CONFIRMED", "PENDING", "PASSED", "CANCELLED"})
						.enumLabels(new String[]{
							glp("jcmsplugin.zaomeetreserve.app.view.reservations.filter.status.all"),
							glp("jcmsplugin.zaomeetreserve.calendar.legend.confirmed"),
							glp("jcmsplugin.zaomeetreserve.calendar.legend.pending"),
							glp("jcmsplugin.zaomeetreserve.reservation.status.past"),
							glp("jcmsplugin.zaomeetreserve.reservation.status.cancelled")
						}) %>' />
				</jalios:field>
			</div>
			<div class="col-sm-3">
				<jalios:field name="filterFromDateStr"
					label='<%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.filter.date-start") %>'
					value='<%= appHandler.getFilterFromDateStr() %>'>
					<jalios:control settings='<%= new DateSettings() %>' />
				</jalios:field>
			</div>
			<div class="col-sm-3">
				<jalios:field name="filterToDateStr"
					label='<%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.filter.date-end") %>'
					value='<%= appHandler.getFilterToDateStr() %>'>
					<jalios:control settings='<%= new DateSettings() %>' />
				</jalios:field>
			</div>&nbsp;&nbsp;
			<div class="col-sm-3">
				<div class="form-group">
					<div class="input-group">
						<input type="text" class="form-control" name="searchTerm"
						       value="<%= Util.notEmpty(appHandler.getSearchTerm()) ? encodeForHTMLAttribute(appHandler.getSearchTerm()) : "" %>"
						       placeholder="<%= glp("jcmsplugin.zaomeetreserve.app.view.reservations.filter.search.placeholder") %>">
						<span class="input-group-btn">
							<button type="submit" class="btn btn-default"><jalios:icon src="search" /></button>
						</span>
					</div>
				</div>
			</div>
		</div>
	</form>

	<% if (Util.isEmpty(reservations)) { %>
		<jalios:appBodyNoResult text='<%= glp("jcmsplugin.zaomeetreserve.view.reservations.not-found") %>' />
	<% } else { %>

		<jalios:pager name='myResPager' declare='true' action='init' pageSize="10" pageSizes="10,25,50" paramPrefix="myres_" />
		<jalios:pager name='myResPager' size='<%= reservations.size() %>' action='compute' />
		
		&nbsp;&nbsp;&nbsp; 
		<div class="panel panel-default">
			<div class="panel-heading">
				<%= glp("jcmsplugin.zaomeetreserve.app.view.reservations.count", Integer.valueOf(reservations.size())) %>
			</div>
			<table class="table-data table-app">
				<thead>
					<tr>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.modal.add-reservation.field.room") %><jalios:pager name='myResPager' action='showSort' sort='room' /></th>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.view.reservations.field.date") %><jalios:pager name='myResPager' action='showSort' sort='startDate' /></th>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.view.reservations.field.slot") %></th>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.view.reservations.field.status") %></th>
						<th class="fit"></th>
					</tr>
				</thead>
				<tbody>
					<jalios:foreach name="itReservationItem" type="Reservation" collection="<%= reservations %>"
						skip="<%= myResPager.getStart() %>" max="<%= myResPager.getPageSize() %>">
						<%
							Reservation itReservation = (Reservation) itReservationItem;
							String roomName = Util.notEmpty(itReservation.getSalle()) ? itReservation.getSalle().getTitle() : "";
							String statusCode = appHandler.getReservationStatusCode(itReservation);
							boolean editable = "CONFIRMED".equals(statusCode) || "PENDING".equals(statusCode);
						%>
						<tr class="<%= "PASSED".equals(statusCode) ? "zaomeetreserve-row-past" : "" %>">
							<td class="nowrap"><strong><%= encodeForHTML(roomName) %></strong></td>
							<td class="nowrap"><jalios:date date="<%= itReservation.getDateheureDebut() %>" format="short" /></td>
							<td class="nowrap">
								<%= Util.notEmpty(itReservation.getDateheureDebut()) ? hourFmt.format(itReservation.getDateheureDebut()) : "" %>
								<jalios:icon src="glyph: icomoon-minus2" />
								<%= Util.notEmpty(itReservation.getDateheureFin()) ? hourFmt.format(itReservation.getDateheureFin()) : "" %>
							</td>
							<td class="nowrap">
								<span class="label <%= appHandler.getReservationStatusLabelClass(itReservation) %>">
									<%= glp(appHandler.getReservationStatusLabelKey(itReservation)) %>
								</span>
							</td>
							<td class="fit actions nowrap">
								<% if (editable) { %>
								<div class="dropdown">
									<div class="dropdown-toggle btn co-booking-dropdown-btn" data-toggle="dropdown" aria-expanded="false">
										<jalios:icon src="glyph: icomoon-more2" />
									</div>
									<ul class="dropdown-menu dropdown-menu-right" role="menu">
										<li>
											<a href="#" class="modal"
											   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/addReservationModal.jsp?reservationId=<%= itReservation.getId() %>">
												<jalios:icon src="edit" /> <%= glp("jcmsplugin.zaomeetreserve.app.view.table.item.action.update") %>
											</a>
										</li>
										<li role="presentation" class="divider"></li>
										<li>
											<a href="#" class="modal text-danger"
											   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/doDeleteReservationModal.jsp?reservationId=<%= itReservation.getId() %>">
												<jalios:icon src="trash-empty" /> <%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.action.cancel") %>
											</a>
										</li>
									</ul>
								</div>
								<% } %>
							</td>
						</tr>
					</jalios:foreach>
				</tbody>
			</table>
		</div>

		<jalios:pager name='myResPager' template='pqf' />
	<% } %>
</div>