<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>
<%@page import="generated.Room"%>
<%@page import="java.util.List"%>


<%
if (!appHandler.canAccessAdmin(loggedMember)) {
	sendRedirect(appHandler.getViewUrl("MY_RESERVATIONS"));
	return;
}

icon.jcmsplugin-zaomeetreserve-app: glyph: icomoon-arrow-left7
List<Room> allRooms = appHandler.getAllRooms();
request.setAttribute("jcmsplugin.zaomeetreserve.roomsList", allRooms);
request.setAttribute("jcmsplugin.zaomeetreserve.appHandler", appHandler);
%>

<button class="back-btn" onclick="location.href='<%= appHandler.getViewUrl("ADMIN") %>'">
	<jalios:icon src="glyph: icomoon-arrow-left7" /> <%= glp("jcmsplugin.zaomeetreserve.admin.back-to-hub") %>
</button>

<jalios:include jsp='/plugins/ZaoMeetReservePlugin/jsp/displayingTemplate/doDisplayingRoomTemplate.jsp'/>