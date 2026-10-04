<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jspf' %>

<jsp:useBean id='formHandler' scope='page' class='co.kozao.jcmsplugin.zaomeetreserve.handler.reservation.DeleteReservationHandler'>
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

<%
  String reservationToDeleteName = formHandler.getReservationNameToDelete();
%>

<jalios:modal css="zaomeetreserve-red-modal" op="opDelete" formHandler="<%=formHandler%>"
	button="jcmsplugin.zaomeetreserve.app.view.table.item.action.delete"
	title="jcmsplugin.zaomeetreserve.modal.delete-reservation.title"
	url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/doDeleteReservationModal.jsp">
	 <input type="hidden" name="reservationId" value="<%= formHandler.getReservationId() != null ? formHandler.getReservationId().toString() : "" %>" />
		<p> <%=glp("jcmsplugin.zaomeetreserve.modal.delete-reservation.intro.msg", reservationToDeleteName)%> </p>
</jalios:modal>