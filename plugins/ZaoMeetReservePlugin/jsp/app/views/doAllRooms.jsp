<%@page import="java.util.List"%>
<%@page import="com.jalios.util.Util"%>
<%@page import="generated.Room"%>
<%@page import="com.jalios.jcms.taglib.card.LinkOptions"%>
<%
List<Room> catalogRooms = appHandler.getCatalogRoomsForCurrentPage();
%>

<div class="text-right" style="margin-bottom:14px;">
  <a href="#" class="btn btn-primary modal" data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/addReservationModal.jsp">
    <jalios:icon src="glyph: icomoon-plus3" /> <%= glp("jcmsplugin.zaomeetreserve.app.view.all-rooms.btn.new-reservation") %>
  </a>
</div>

<form method="get" action="<%= appHandler.getAppUrl() %>" class="form-vertical">
  <input type="hidden" name="view" value="ALL_ROOMS">
  <div class="row">
    <div class="col-sm-3">
      <jalios:field name="catalogAvailabilityFilter" label='<%= glp("jcmsplugin.zaomeetreserve.app.view.all-rooms.filter.availability") %>'
                    value='<%= appHandler.getCatalogAvailabilityFilter() %>'>&nbsp;&nbsp;&nbsp;
        <jalios:control settings='<%= new EnumerateSettings().select()
                .enumValues(new String[]{"all", "free", "busy"})
                .enumLabels(new String[]{
                    glp("jcmsplugin.zaomeetreserve.app.view.all-rooms.filter.availability.all"),
                    glp("jcmsplugin.zaomeetreserve.app.view.all-rooms.filter.availability.free"),
                    glp("jcmsplugin.zaomeetreserve.app.view.all-rooms.filter.availability.busy")
                }) %>' />
      </jalios:field>
    </div>

    <div class="col-sm-3">
      <jalios:field name="catalogLocationFilter" label='<%= glp("jcmsplugin.zaomeetreserve.app.view.all-rooms.filter.location") %>'
                    value='<%= appHandler.getCatalogLocationFilter() %>'>&nbsp;&nbsp;&nbsp;
        <jalios:control settings='<%= new TextFieldSettings().placeholder("jcmsplugin.zaomeetreserve.app.view.all-rooms.filter.location.placeholder") %>' />
      </jalios:field>
    </div>

    <div class="col-sm-3">
      <div class="form-group">&nbsp;&nbsp;&nbsp;
        <div class="input-group">
          <input type="text" class="form-control" name="catalogSearchTerm"
                 value="<%= Util.notEmpty(appHandler.getCatalogSearchTerm()) ? appHandler.getCatalogSearchTerm() : "" %>"
                 placeholder="<%= glp("jcmsplugin.zaomeetreserve.app.view.admin.rooms.filter.search.placeholder") %>">
          <span class="input-group-btn">
            <button type="submit" class="btn btn-default"><jalios:icon src="search" /></button>
          </span>
        </div>
      </div>
    </div>
  </div>
</form>

<% if (Util.isEmpty(catalogRooms)) { %>
  <jalios:appBodyNoResult text='<%= glp("jcmsplugin.zaomeetreserve.admin.rooms.not-found") %>' />
<% } else { %>
&nbsp;&nbsp;&nbsp;
<jalios:cards>
  <% for (Room nextRoom : catalogRooms) { %>
    <jalios:cardData linkOptions='<%= new LinkOptions().hrefLink(true) %>' data="<%= nextRoom %>" />
  <% } %>
</jalios:cards>
<% } %>