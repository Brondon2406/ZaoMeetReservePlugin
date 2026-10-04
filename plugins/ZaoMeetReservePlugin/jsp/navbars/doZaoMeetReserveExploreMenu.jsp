<%@page import="com.jalios.util.Util"%>
<%@page import="co.kozao.jcmsplugin.zaomeetreserve.handler.app.ZaoMeetReserveAppHandler"%>

<%
    jcmsContext.addCSSHeader("plugins/ZaoMeetReservePlugin/css/navbar/zaomeetreserve-navbar.css");
%>

<div class="navbar navbar-default zaomeetreserve-navbar">
	<div class="container-fluid zaomeetreserve-navbar-child-1">
		<div class="navbar-form navbar-left zaomeetreserve-navbar-child-2">

			<a href="" class="btn btn-default"
			   data-jalios-action="modal"
			   data-jalios-modal-url="plugins/ZaoMeetReservePlugin/jsp/modales/reservation/addReservationModal.jsp"
			   title="<%= glp("jcmsplugin.zaomeetreserve.app.sidebar.btn.add-reservation.label") %>">
				<jalios:icon src="glyph: icomoon-plus3" />
				<%= glp("jcmsplugin.zaomeetreserve.app.sidebar.btn.add-reservation.label") %>
			</a>

			<!-- Boolean to add filter -->
			<jalios:field name="displayingAdminViewFilter"
				label='<%= glp("jcmsplugin.zaomeetreserve.navbar.adding-filters.label") %>'
				value='<%= appHandler.isDisplayingAdminViewFilter() %>'
				css='displaying-filter-admin-view'>
				<jalios:control settings='<%= new BooleanSettings() %>' />
			</jalios:field>
		</div>

		<%-- Search form --%>
		<div class="navbar-form navbar-right" role="search">
			<% String targetAriaLabel = " aria-label=\"ui.com.placeholder.search\" "; %>
			<jalios:field name="searchTerm"
				value="<%= appHandler.getSearchTerm() %>" resource="field-light">
				<jalios:control
					settings='<%= new TextFieldSettings().placeholder("ui.com.placeholder.search").htmlAttributes(targetAriaLabel).onChange("ajax-refresh") %>' />
				<span class="input-group-btn">
					<button class="btn btn-default startSearchButton"
						data-jalios-action="ajax-refresh"
						name="opAppSearch" type="submit" value="true"
						title='<%= glp("ui.com.btn.search") %>'>
						<jalios:icon src="search" />
					</button>
					<% if (appHandler.hasSearch()) { %>
						<button class="btn btn-default ResetSearchButton"
							data-jalios-action="ajax-refresh" type="submit"
							title='<%= glp("ui.com.btn.reset") %>'>
							<jalios:icon src="remove" alt="ui.com.btn.reset" />
						</button>
					<% } %>
				</span>
			</jalios:field>
		</div>
	</div>

	<%-- Filters --%>
	<% if (appHandler.isDisplayingAdminViewFilter()) { %>
		<div class="zaomeetreserve-displaying-filters-contents">

			<jalios:field name="filterByStatus"
				label='<%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.filter.status") %>'
				value='<%= appHandler.getFilterByStatus() %>'>
				<jalios:control
					settings='<%= new EnumerateSettings().select()
                        .enumValues(new String[]{"ALL","CONFIRMEE","RECURRENTE","PASSEE","CANCELLED"})
                        .enumLabels(new String[]{
                            glp("jcmsplugin.zaomeetreserve.status.all"),
                            glp("jcmsplugin.zaomeetreserve.status.confirmed"),
                            glp("jcmsplugin.zaomeetreserve.status.recurring"),
                            glp("jcmsplugin.zaomeetreserve.status.past"),
                            glp("jcmsplugin.zaomeetreserve.status.cancelled")
                        })
                        .onChange("ajax-refresh") %>' />
			</jalios:field>

			<jalios:field name="dateStartStr"
				label='<%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.filter.date-start") %>'
				value='<%= appHandler.getDateStartStr() %>'>
				<jalios:control settings='<%= new DateSettings().onChange("ajax-refresh") %>' />
			</jalios:field>

			<jalios:field name="dateEndStr"
				label='<%= glp("jcmsplugin.zaomeetreserve.app.view.my-reservations.filter.date-end") %>'
				value='<%= appHandler.getDateEndStr() %>'>
				<jalios:control settings='<%= new DateSettings().onChange("ajax-refresh") %>' />
			</jalios:field>

		</div>
	<% } %>
	<%= appHandler.getNavbarHiddenFields() %>
</div>