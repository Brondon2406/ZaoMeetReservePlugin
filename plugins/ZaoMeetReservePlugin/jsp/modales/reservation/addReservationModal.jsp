<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>

<jsp:useBean id='formHandler' scope='page' class='co.kozao.jcmsplugin.zaomeetreserve.handler.reservation.CreateAndUpdateReservationHandler'>
	 <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
	 <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
	 <jsp:setProperty name='formHandler' property='*'/>
</jsp:useBean>
<% if(formHandler.validate()){ %>
	<%@ include file="/jcore/modal/modalRedirect.jspf" %>
<% } %>

<%
	boolean isUpdate = formHandler.isUpdateOperation();
	String title = "jcmsplugin.zaomeetreserve.modal.add-reservation.title";
	if (isUpdate) {
		title = "jcmsplugin.zaomeetreserve.modal.update-reservation.title";
	}
	int step = formHandler.getFormStep();
%>

<jalios:modal title="<%= title %>" formHandler="<%= formHandler %>" css="modal-lg add-reservation"
			url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/addReservationModal.jsp">
	<%@include file='/jcore/toastr/doToastr.jspf' %>

	<div>
		<jalios:if predicate="<%= step == 0 %>">
			<%@ include file="/plugins/ZaoMeetReservePlugin/jsp/modales/reservation/step/doAddReservationField_step_0.jspf" %>
		</jalios:if>

		<jalios:if predicate="<%= step == 1 %>">
			<%@ include file="/plugins/ZaoMeetReservePlugin/jsp/modales/reservation/step/doAddReservationField_step_1.jspf" %>
		</jalios:if>
	</div>
</jalios:modal>