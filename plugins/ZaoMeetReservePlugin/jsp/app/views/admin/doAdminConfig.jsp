<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>
<%@page import="com.jalios.jcms.taglib.ControlType"%>
<%@ page import="com.jalios.util.Util" %>


<jsp:useBean id="configHandler" scope="page" class="co.kozao.jcmsplugin.zaomeetreserve.handler.SaveConfigHandler">
  <jsp:setProperty name="configHandler" property="request" value="<%= request %>"/>
  <jsp:setProperty name="configHandler" property="response" value="<%= response %>"/>
  <jsp:setProperty name="configHandler" property="*"/>
</jsp:useBean>

<a href="<%= appHandler.getAdminViewUrl() %>" class="back-btn" data-jalios-action="ajax-refresh">
	<jalios:icon src="glyph: icomoon-arrow-left7" /> <%= glp("jcmsplugin.zaomeetreserve.app.view.admin.back-to-admin") %>
</a>



<div class="zaomeetreserve-full-page-head">
  <div>
    <h2><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.config.header.label") %></h2>
    <p><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.config.header.description") %></p>
  </div>
</div>

<div class="zaomeetreserve-card">
  <div class="zaomeetreserve-card-body">

    <form method="post" action="plugins/ZaoMeetReservePlugin/jsp/app/views/admin/doSaveConfig.jsp">

		<input type="hidden" name="redirect" value="<%= appHandler.getViewUrl("ADMIN") %>" />
      	<input type="hidden" name="opSaveConfig" value="true" />

      <div class="zaomeetreserve-card-head zaomeetreserve-card-head-flat"><%= glp("jcmsplugin.zaomeetreserve.admin.config.section.booking-rules") %></div>
      <div class="zaomeetreserve-form-row zaomeetreserve-cols3">
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.opening-hour" name="openinghour"
                      value="<%= configHandler.getOpeninghour() %>" required="true">
          <jalios:control type="<%= ControlType.TEXTFIELD %>"/>
        </jalios:field>
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.closing-hour" name="closinghour"
                      value="<%= configHandler.getClosinghour() %>" required="true">
          <jalios:control type="<%= ControlType.TEXTFIELD %>"/>
        </jalios:field>
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.min-slot-duration" name="minslotduration"
                      value="<%= configHandler.getMinslotduration() %>">
          <jalios:control type="<%= ControlType.NUMBER %>"/>
        </jalios:field>
      </div>

      <div class="zaomeetreserve-form-row zaomeetreserve-cols3">
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.max-slot-duration" name="maxslotduration"
                      value="<%= configHandler.getMaxslotduration() %>">
          <jalios:control type="<%= ControlType.NUMBER %>"/>
        </jalios:field>
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.max-booking-delay" name="maxbookingdelaydays"
                      value="<%= configHandler.getMaxbookingdelaydays() %>">
          <jalios:control type="<%= ControlType.NUMBER %>"/>
        </jalios:field>
      </div>

      <div class="zaomeetreserve-card-head zaomeetreserve-card-head-flat zaomeetreserve-card-head-sep"><%= glp("jcmsplugin.zaomeetreserve.admin.config.section.cancel-checkin") %></div>
      <div class="zaomeetreserve-form-row zaomeetreserve-cols3">
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.auto-cancel" name="autocancelminutes"
                      value="<%= configHandler.getAutocancelminutes() %>">
          <jalios:control type="<%= ControlType.NUMBER %>"/>
        </jalios:field>
      </div>

      <div class="zaomeetreserve-card-head zaomeetreserve-card-head-flat zaomeetreserve-card-head-sep"><%= glp("jcmsplugin.zaomeetreserve.admin.config.section.recurring") %></div>
      <div class="zaomeetreserve-form-row zaomeetreserve-cols3">
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.recurring-enabled" name="recuring"
		              value="<%= configHandler.isRecuring() %>">
		  <jalios:control type="<%= ControlType.BOOLEAN %>"/>
		</jalios:field>
        <jalios:field label="jcmsplugin.zaomeetreserve.admin.config.field.recurring-max-per-week" name="recurringmaxperweek"
                      value="<%= configHandler.getRecurringmaxperweek() %>">
          <jalios:control type="<%= ControlType.NUMBER %>"/>
        </jalios:field>
      </div>

      <div class="zaomeetreserve-modal-footer zaomeetreserve-footer-sep">
        <button type="submit" class="zaomeetreserve-btn-primary"><%= glp("jcmsplugin.zaomeetreserve.admin.config.btn.save") %></button>
      </div>
    </form>
  </div>
</div>