<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>


<div class="zaomeetreserve-full-page-head">
  <div>
    <h2><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.header.label") %></h2>
    <p><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.header.description") %></p>
  </div>
</div>

<div class="zaomeetreserve-hub-grid">
  <a href="<%= appHandler.getViewUrl("ADMIN_ROOMS") %>" class="zaomeetreserve-hub-card">
    <div class="zaomeetreserve-hc-ico"><jalios:icon src="glyph: icomoon-office" /></div>
    <div class="zaomeetreserve-hc-title"><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.rooms.header.label") %></div>
    <div class="zaomeetreserve-hc-desc"><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.rooms.header.description") %></div>
  </a>

  <a href="<%= appHandler.getViewUrl("ADMIN_CONFIG") %>" class="zaomeetreserve-hub-card">
    <div class="zaomeetreserve-hc-ico"><jalios:icon src="glyph: icomoon-cog" /></div>
    <div class="zaomeetreserve-hc-title"><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.config.header.label") %></div>
    <div class="zaomeetreserve-hc-desc"><%= glp("jcmsplugin.zaomeetreserve.app.view.admin.config.header.description") %></div>
  </a>
</div>