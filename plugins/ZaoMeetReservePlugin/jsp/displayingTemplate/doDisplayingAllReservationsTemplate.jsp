<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.jalios.jcms.taglib.settings.impl.EnumerateSettings" %>
<%@ page import="co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager" %>
<%@ page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler" %>
<%@ page import="generated.Reservation" %>
<%@ page import="com.jalios.jcms.taglib.settings.impl.DateSettings" %>
<%
	// VUE "TOUTES LES RÉSERVATIONS" (maquette : view-toutesReservations)
	// Lecture seule : pas de bouton "Nouvelle réservation", pas de menu d'actions.
	ZaoMeetReserveAppHandler appHandler = (ZaoMeetReserveAppHandler) request.getAttribute("jcmsplugin.zaomeetreserve.appHandler");
	List<Reservation> reservations = (List<Reservation>) request.getAttribute("jcmsplugin.zaomeetreserve.listReservations");
	if (reservations == null) {
		reservations = new ArrayList<Reservation>();
	}

	// Liste des membres du filtre : auteurs de TOUTES les réservations (non filtrées)
	Map<String, String> authors = new LinkedHashMap<String, String>();
	for (Reservation r : ZaoMeetReserveManager.getInstance().getAllReservations()) {
		if (Util.notEmpty(r.getAuthor())) {
			authors.put(r.getAuthor().getId(), r.getAuthor().getFullName());
		}
	}
	String[] memberIds = new String[authors.size() + 1];
	String[] memberLabels = new String[authors.size() + 1];
	memberIds[0] = "all";
	memberLabels[0] = glp("jcmsplugin.zaomeetreserve.app.view.reservations.filter.status.all");
	int idx = 1;
	for (Map.Entry<String, String> entry : authors.entrySet()) {
		memberIds[idx] = entry.getKey();
		memberLabels[idx] = entry.getValue();
		idx++;
	}

	String sortField = getUntrustedStringParameter("allres_sort", "startDate");
	boolean descending = Util.toBoolean(getUntrustedStringParameter("allres_reverse", "false"), false);
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

	<%-- Filtres : Recherche, Statut, Membre, Date début, Date fin --%>
	<form method="get" action="<%= appHandler.getAppUrl() %>" class="form-vertical">
		<input type="hidden" name="view" value="ALL_RESERVATIONS">
		
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
				<jalios:field name="filterByMemberId"
					label='<%= glp("jcmsplugin.zaomeetreserve.modal.add-reservation.field.member") %>'
					value='<%= appHandler.getFilterByMemberId() %>'>
					<jalios:control settings='<%= new EnumerateSettings().select().enumValues(memberIds).enumLabels(memberLabels) %>' />
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
			<div class="row">
				<div class="col-sm-4">
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
		</div>
	</form>

	<% if (Util.isEmpty(reservations)) { %>
		<jalios:appBodyNoResult text='<%= glp("jcmsplugin.zaomeetreserve.view.reservations.not-found") %>' />
	<% } else { %>

		<jalios:pager name='allResPager' declare='true' action='init' pageSize="10" pageSizes="10,25,50" paramPrefix="allres_" />
		<jalios:pager name='allResPager' size='<%= reservations.size() %>' action='compute' />

		<div class="panel panel-default">
			<table class="table-data table-app">
				<thead>
					<tr>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.view.reservations.field.author") %></th>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.modal.add-reservation.field.room") %><jalios:pager name='allResPager' action='showSort' sort='room' /></th>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.view.reservations.field.date") %><jalios:pager name='allResPager' action='showSort' sort='startDate' /></th>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.view.reservations.field.slot") %></th>
						<th scope="col" class="fit nowrap"><%= glp("jcmsplugin.zaomeetreserve.view.reservations.field.status") %></th>
					</tr>
				</thead>
				<tbody>
					<jalios:foreach name="itReservationItem" type="Reservation" collection="<%= reservations %>"
						skip="<%= allResPager.getStart() %>" max="<%= allResPager.getPageSize() %>">
						<%
							Reservation itReservation = (Reservation) itReservationItem;
							String roomName = Util.notEmpty(itReservation.getSalle()) ? itReservation.getSalle().getTitle() : "";
							String authorName = Util.notEmpty(itReservation.getAuthor()) ? itReservation.getAuthor().getFullName() : "";
						%>
						<tr class="<%= "PASSED".equals(appHandler.getReservationStatusCode(itReservation)) ? "zaomeetreserve-row-past" : "" %>">
							<td class="nowrap"><%= encodeForHTML(authorName) %></td>
							<td class="nowrap"><%= encodeForHTML(roomName) %></td>
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
						</tr>
					</jalios:foreach>
				</tbody>
			</table>
		</div>

		<jalios:pager name='allResPager' template='pqf' />
	<% } %>
</div>