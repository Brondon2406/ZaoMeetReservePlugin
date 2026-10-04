<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>
<%@page import="com.jalios.util.Util"%>
<%@page import="generated.Room"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.ennum.RoomStatus"%>

<%
	List<Room> rooms = appHandler.getFilteredRooms();

	String searchTerm  = getUntrustedStringParameter("catalogSearchTerm", "").trim().toLowerCase();
	String statutParam = getUntrustedStringParameter("filterByStatut", "all");

	List<Room> rooms = new ArrayList<Room>();
	if (allRooms != null) {
		for (Room r : allRooms) {
			boolean okSearch = Util.isEmpty(searchTerm)
				|| (r.getTitle() != null && r.getTitle().toLowerCase().contains(searchTerm))
				|| (r.getLocalisation() != null && r.getLocalisation().toLowerCase().contains(searchTerm));

			boolean okStatut = Util.isEmpty(statutParam) || "all".equals(statutParam)
				|| RoomStatus.fromRaw(r.getStatut()).name().equalsIgnoreCase(statutParam);

			if (okSearch && okStatut) rooms.add(r);
		}
	}

	String sortField = getUntrustedStringParameter("room_sort", "cdate");
	boolean descending = Util.toBoolean(getUntrustedStringParameter("room_reverse", "false"), false);
	rooms = appHandler.getAllRoomsOrderByFields(rooms, sortField, descending);
%>


<a href="<%= appHandler.getAdminViewUrl() %>" class="back-btn" data-jalios-action="ajax-refresh">
	<jalios:icon src="glyph: icomoon-arrow-left" /> <%= glp("jcmsplugin.zaomeetreserve.admin.back-to-hub") %>
</a>

<div class="text-right" style="margin-bottom:14px;">
	<a href="#" class="modal btn btn-primary"
	   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/room/doCreateAndUpdateRoomModal.jsp">
		<jalios:icon src="glyph: icomoon-plus3" /> <%= glp("jcmsplugin.zaomeetreserve.admin.rooms.modal.add.title") %>
	</a>
</div>

<form method="get" action="<%= appHandler.getViewUrl("ADMIN_ROOMS") %>" class="form-vertical">
	<div class="row">
		<div class="col-sm-3">
			<jalios:field name="filterByStatut"
				label='<%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.filter.status") %>'
				value='<%= appHandler.getFilterByStatut() %>'>
				<jalios:control settings='<%= new EnumerateSettings().select()
					.enumValues(new String[]{"all", "available", "occupied", "maintenance"})
					.enumLabels(new String[]{
						glp("jcmsplugin.zaomeetreserve.admin.rooms.filter.status.all"),
						glp("jcmsplugin.zaomeetreserve.admin.rooms.filter.status.available"),
						glp("jcmsplugin.zaomeetreserve.admin.rooms.filter.status.occupied"),
						glp("jcmsplugin.zaomeetreserve.admin.rooms.filter.status.maintenance")
					}) %>' />
			</jalios:field>
		</div>
		<div class="col-sm-3">
			<div class="form-group">
				<div class="input-group">
					<input type="text" class="form-control" name="catalogSearchTerm"
					       value="<%= Util.notEmpty(appHandler.getCatalogSearchTerm()) ? appHandler.getCatalogSearchTerm() : "" %>"
					       placeholder="<%= glp("jcmsplugin.zaomeetreserve.app.all-rooms.filter.search.placeholder") %>">
					<span class="input-group-btn">
						<button type="submit" class="btn btn-default"><jalios:icon src="search" /></button>
					</span>
				</div>
			</div>
		</div>
	</div>
</form>

<% if (Util.isEmpty(rooms)) { %>
	<jalios:appBodyNoResult text='<%= glp("jcmsplugin.zaomeetreserve.admin.rooms.not-found") %>' />
<% } else { %>

<jalios:pager name="roomsPager" declare="true" action="init" pageSize="10" pageSizes="10,25,50" paramPrefix="room_" />
<jalios:pager name="roomsPager" size="<%= rooms.size() %>" action="compute" />

<div class="table-responsive" style="overflow-y: visible;">
	<table class="table-data table-app">
		<thead>
			<tr>
				<th>#</th>
				<th scope="col">
					<%= glp("jcmsplugin.zaomeetreserve.admin.rooms.table.column.name") %>
					<jalios:pager name="roomsPager" action="showSort" sort="title" />
				</th>
				<th scope="col">
					<%= glp("jcmsplugin.zaomeetreserve.admin.rooms.table.column.capacity") %>
					<jalios:pager name="roomsPager" action="showSort" sort="capacite" />
				</th>
				<th scope="col"><%= glp("jcmsplugin.zaomeetreserve.admin.rooms.table.column.location") %></th>
				<th scope="col">
					<%= glp("jcmsplugin.zaomeetreserve.admin.rooms.table.column.created") %>
					<jalios:pager name="roomsPager" action="showSort" sort="cdate" />
				</th>
				<th scope="col"><%= glp("jcmsplugin.zaomeetreserve.admin.rooms.table.column.status") %></th>
				<th scope="col" class="fit"></th>
			</tr>
		</thead>
		<tbody>
			<jalios:foreach name="itRoomItem" type="Room" collection="<%= rooms %>"
			                skip="<%= roomsPager.getStart() %>" max="<%= roomsPager.getPageSize() %>">
				<%
					Room itRoom = (Room) itRoomItem;
					String statutCss = appHandler.getRoomStatutCss(itRoom);
				%>
				<tr class="default">
					<td class="fit nowrap"><b><%= itCounter + roomsPager.getStart() %></b></td>
					<td scope="row" class="nowrap"><%= itRoom.getTitle() %></td>
					<td class="nowrap"><%= itRoom.getCapacite() %></td>
					<td class="nowrap"><%= Util.getString(itRoom.getLocalisation(), "") %></td>
					<td class="nowrap"><jalios:date date="<%= itRoom.getCdate() %>" format="dd/MM/yyyy HH:mm" /></td>
					<td class="nowrap">
						<span class="badge <%= statutCss %>"><%= glp(appHandler.getRoomStatutLabelKey(itRoom)) %></span>
					</td>
					<td class="fit actions nowrap">
						<ul class="nav2 navbar-nav navbar-left actions-nav">
							<li class="dropdown">
								<a style="margin: 10px; text-decoration: none;"
								   title="<%= glp("jcmsplugin.zaomeetreserve.admin.rooms.table.do-action.title") %>"
								   href="#" class="dropdown-toggle" data-toggle="dropdown">
									<jalios:icon src="glyph: icomoon-more2" />
								</a>
								<ul class="dropdown-menu" style="border-bottom: 1px solid #ccc;" role="menu">
									<li style="border-bottom: 1px solid #ccc;">
										<a style="margin: 10px;" href="#" class="modal"
										   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/room/doCreateAndUpdateRoomModal.jsp?roomId=<%= itRoom.getId() %>">
											<jalios:icon src="edit" />
											<%= glp("jcmsplugin.zaomeetreserve.app.view.table.item.action.update") %>
										</a>
									</li>
									<li>
										<a style="margin: 10px;" href="#" class="modal"
										   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/room/doDeleteRoom.jsp?roomId=<%= itRoom.getId() %>">
											<jalios:icon src="delete" />
											<%= glp("jcmsplugin.zaomeetreserve.app.view.table.item.action.delete") %>
										</a>
									</li>
								</ul>
							</li>
						</ul>
					</td>
				</tr>
			</jalios:foreach>
		</tbody>
	</table>
</div>

<jalios:pager name="roomsPager" template="pqf" />

<% } %>