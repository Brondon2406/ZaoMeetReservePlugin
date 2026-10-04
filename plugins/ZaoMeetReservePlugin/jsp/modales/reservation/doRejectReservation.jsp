<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jspf' %>

<jsp:useBean id="formHandler" scope="page" class="co.zao.jcmsplugin.meetreserve.handler.ResolvePendingReservationHandler">
  <jsp:setProperty name="formHandler" property="request" value="<%= request %>"/>
  <jsp:setProperty name="formHandler" property="response" value="<%= response %>"/>
  <jsp:setProperty name="formHandler" property="*"/>
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

	<jalios:modal css="zmr-red-modal" op="opReject" formHandler="<%= formHandler %>"
	              button="jcmsplugin.zaomeetreserve.app.view.to-validate.action.reject"
	              title="jcmsplugin.zaomeetreserve.modal.reservation.reject.title"
	              url="plugins/ZaoMeetReservePlugin/jsp/modales/reservtion/doRejectReservation.jsp">
	  <%= formHandler.getFormHiddenFields() %>
	  <jalios:field label="jcmsplugin.zaomeetreserve.modal.reservation.reject.field.message" name="rejectMessage" required="false">
	    <jalios:control type="<%= ControlType.TEXTAREA %>"/>
	  </jalios:field>
	</jalios:modal>