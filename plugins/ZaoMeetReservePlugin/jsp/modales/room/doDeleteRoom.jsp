<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/jcore/doInitPage.jspf" %>

<jsp:useBean id='formHandler' scope='page' class='co.kozao.jcmsplugin.zaomeetreserve.handler.room.DeleteRoomHandler'>
	<jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
	<jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
	<jsp:setProperty name='formHandler' property='*'/>
</jsp:useBean>

<% if (formHandler.validate()) { %>
	<jalios:modal title="msg.js.process-in-progress" css="modal-md">
		<jalios:buffer name="MODAL_CONTENT">
			<div class="modal-body text-center">
				<jalios:icon src="wait" />
			</div>
			<jalios:javascript>
				JCMS.window.Modal.close(true);
				location.reload();
			</jalios:javascript>
		</jalios:buffer>
	</jalios:modal>
<% } %>

<jalios:modal css="zaomeetreserve-red-modal"
              op="opDelete"
              formHandler="<%= formHandler %>"
              button="jcmsplugin.zaomeetreserve.app.view.table.item.action.delete"
              title="jcmsplugin.zaomeetreserve.admin.rooms.delete.title"
              url="plugins/ZaoMeetReservePlugin/jsp/modales/room/doDeleteRoom.jsp">

	<input type="hidden" name="roomId" value="<%= encodeForHTMLAttribute(formHandler.getRoomId()) %>" />

	<p><%= glp("jcmsplugin.zaomeetreserve.admin.rooms.delete.intro.msg", encodeForHTML(formHandler.getRoomNameToDelete())) %></p>
</jalios:modal>