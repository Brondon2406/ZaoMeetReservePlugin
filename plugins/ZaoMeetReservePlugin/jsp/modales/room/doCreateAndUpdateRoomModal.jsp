<%@page import="com.jalios.jcms.taglib.ControlType"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.ennum.RoomStatus"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ include file="/jcore/doMessageBox.jspf" %>
<%@page import="java.util.ArrayList"%>

<jsp:useBean id='appHandler' scope='page' class='co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler'>
	<jsp:setProperty name='appHandler' property='request' value='<%= request %>'/>
	<jsp:setProperty name='appHandler' property='response' value='<%= response %>'/>
</jsp:useBean>

<jsp:useBean id='formHandler' scope='page' class='co.kozao.jcmsplugin.zaomeetreserve.handler.room.CreateAndUpdateRoomHandler'>
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
<% }
	String title = formHandler.isUpdateOperation()
		? "jcmsplugin.zaomeetreserve.admin.rooms.modal.update.title"
		: "jcmsplugin.zaomeetreserve.admin.rooms.modal.add.title";
%>

<jalios:modal title='<%= title %>'
              url="plugins/ZaoMeetReservePlugin/jsp/modales/room/doCreateAndUpdateRoomModal.jsp"
              op="opAddRoom" button="jcmsplugin.zaomeetreserve.admin.rooms.modal.btn.save"
              formHandler="<%= formHandler %>">

	<jalios:field label="jcmsplugin.zaomeetreserve.admin.rooms.modal.field.name"
		required="true" name='title' value='<%= formHandler.getTitle() %>'>
		<jalios:control type="<%= ControlType.TEXTFIELD %>" />
	</jalios:field>

	<jalios:field label="jcmsplugin.zaomeetreserve.admin.rooms.modal.field.capacity"
		required="true" name="capacite" value='<%= formHandler.getCapacite() %>'>
		<jalios:control type="<%= ControlType.NUMBER %>" />
	</jalios:field>

	<jalios:field
	    name="statut" label="jcmsplugin.zaomeetreserve.admin.rooms.modal.field.status" value='<%= Util.notEmpty(formHandler.getStatut())
	        ? formHandler.getStatut()
	        : "value1" %>'>
	
	    <jalios:control settings='<%= new EnumerateSettings().select()
	        .enumValues(new String[]{
	            "value1",
	            "value2",
	            "value3"
	        })
	        .enumLabels(new String[]{
	            glp("jcmsplugin.zaomeetreserve.room.status.available"),
	            glp("jcmsplugin.zaomeetreserve.room.status.occupied"),
	            glp("jcmsplugin.zaomeetreserve.room.status.maintenance")
	        }) %>' />
	
	</jalios:field>
	
	<jalios:field label="jcmsplugin.zaomeetreserve.admin.rooms.modal.field.location"
		required="true" name='localisation' value='<%= formHandler.getLocalisation() %>'>
		<jalios:control type="<%= ControlType.TEXTFIELD %>" />
	</jalios:field>

	<jalios:field label="jcmsplugin.zaomeetreserve.admin.rooms.modal.field.equipments"
		required="false" name='equipements' value='<%= formHandler.getEquipements() %>'>
		<jalios:control type="<%= ControlType.TEXTFIELD %>" />
	</jalios:field>
	
	<jalios:field label="jcmsplugin.zaomeetreserve.admin.rooms.modal.field.photo"
		required="false" name="photo" value='<%= formHandler.getPhoto() %>'>
		<jalios:control settings='<%= new FileSettings().singleFile() %>' />
	</jalios:field>

	<%= formHandler.getFormStepHiddenFields() %>

</jalios:modal>