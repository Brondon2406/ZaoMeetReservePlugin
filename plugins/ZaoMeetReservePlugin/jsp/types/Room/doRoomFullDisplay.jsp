<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager" %>
<%@ page import="generated.Reservation" %>
<%@ page import="generated.Room" %>
<%
	// Publication affichée (même pattern que les templates générés par JCMS)
	Room obj = (Room) request.getAttribute(PortalManager.PORTAL_PUBLICATION);

	// Statut stocké : value1 / value2 / value3 (voir Room.xml)
	String statut = obj.getStatut();
	String statutKey = "jcmsplugin.zaomeetreserve.room.status.available";
	String statutCss = "zaomeetreserve-badge-green";
	if ("value2".equals(statut)) {
		statutKey = "jcmsplugin.zaomeetreserve.room.status.occupied";
		statutCss = "zaomeetreserve-badge-orange";
	} else if ("value3".equals(statut)) {
		statutKey = "jcmsplugin.zaomeetreserve.room.status.maintenance";
		statutCss = "zaomeetreserve-badge-red";
	}

	ZaoMeetReserveManager manager = ZaoMeetReserveManager.getInstance();
	List<Reservation> history = manager.getReservationsOrderByFields(manager.getReservationsByRoom(obj), "date", true);
	SimpleDateFormat hourFmt = new SimpleDateFormat("HH:mm");
%>

<div class="zaomeetreserve-scope">

	<a href="javascript:history.back()" class="back-btn">
		<jalios:icon src="glyph: icomoon-arrow-left7" /> <%= glp("jcmsplugin.zaomeetreserve.room.detail.back") %>
	</a>

	<div class="zaomeetreserve-full-page-head">
		<div>
			<h2><%= encodeForHTML(obj.getTitle()) %></h2>
			<p>
				<%= encodeForHTML(obj.getLocalisation()) %> ·
				<%= glp("jcmsplugin.zaomeetreserve.room.detail.capacity", obj.getCapacite()) %>
				<span class="zaomeetreserve-badge <%= statutCss %>"><%= glp(statutKey) %></span>
			</p>
		</div>
		<a href="#" class="btn btn-primary modal"
		   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/addReservationModal.jsp?roomId=<%= obj.getId() %>">
			<jalios:icon src="glyph: icomoon-plus3" /> <%= glp("jcmsplugin.zaomeetreserve.room.detail.book") %>
		</a>
	</div>

	<div class="row">
		<div class="col-sm-8">

			<% if (Util.notEmpty(obj.getPhoto())) { %>
				<div style="margin-bottom:16px;">
					<img src="<%= encodeForHTMLAttribute(obj.getPhoto()) %>" alt="<%= encodeForHTMLAttribute(obj.getTitle()) %>"
					     style="width:100%; max-height:220px; object-fit:cover; border-radius:10px;" />
				</div>
			<% } %>

			<div class="panel panel-default">
				<div class="panel-heading"><%= glp("jcmsplugin.zaomeetreserve.room.detail.equipments") %></div>
				<div class="panel-body">
					<% if (Util.notEmpty(obj.getEquipements())) {
						for (String equip : obj.getEquipements().split(",")) {
							if (Util.isEmpty(equip.trim())) continue; %>
							<span class="label label-info" style="margin-right:6px; display:inline-block;"><%= encodeForHTML(equip.trim()) %></span>
					<%  }
					} else { %>
						<span class="text-muted">—</span>
					<% } %>
				</div>
			</div>

			<div class="panel panel-default">
				<div class="panel-heading"><%= glp("jcmsplugin.zaomeetreserve.room.detail.history") %></div>
				<% if (Util.isEmpty(history)) { %>
					<div class="panel-body text-muted"><%= glp("jcmsplugin.zaomeetreserve.room.detail.history.empty") %></div>
				<% } else { %>
					<table class="table">
						<thead>
							<tr>
								<th><%= glp("jcmsplugin.zaomeetreserve.room.detail.history.employee") %></th>
								<th><%= glp("jcmsplugin.zaomeetreserve.room.detail.history.date") %></th>
								<th><%= glp("jcmsplugin.zaomeetreserve.room.detail.history.slot") %></th>
							</tr>
						</thead>
						<tbody>
							<% int shown = 0;
							   for (Reservation r : history) {
								   if (shown++ >= 5) break; %>
								<tr>
									<td><%= Util.notEmpty(r.getAuthor()) ? encodeForHTML(r.getAuthor().getFullName()) : "" %></td>
									<td><jalios:date date="<%= r.getDateheureDebut() %>" format="short" /></td>
									<td>
										<%= Util.notEmpty(r.getDateheureDebut()) ? hourFmt.format(r.getDateheureDebut()) : "" %>
										-
										<%= Util.notEmpty(r.getDateheureFin()) ? hourFmt.format(r.getDateheureFin()) : "" %>
									</td>
								</tr>
							<% } %>
						</tbody>
					</table>
				<% } %>
			</div>
		</div>
	</div>
</div>