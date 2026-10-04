package co.kozao.jcmsplugin.zaomeetreserve.handler.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import co.kozao.jcmsplugin.zaomeetreserve.ennum.RoomStatus;
import co.kozao.jcmsplugin.zaomeetreserve.util.CalendarViewModel.MonthCell;
import co.kozao.jcmsplugin.zaomeetreserve.util.CalendarViewModel.TimelineBlock;
import co.kozao.jcmsplugin.zaomeetreserve.util.CalendarViewModel.TimelineRow;
import co.kozao.jcmsplugin.zaomeetreserve.util.ZaoMeetReserveConstants;
import co.kozao.jcmsplugin.zaomeetreserve.util.ZaoMeetReserveUtils;
import generated.Reservation;
import generated.Room;

public class ZaoMeetReserveAppHandler extends JcmsFormHandler {

	private static final Logger LOG = Logger.getLogger(ZaoMeetReserveAppHandler.class);

	/** Formats de date acceptés en entrée (ISO d'abord, puis format du sélecteur de date). */
	private static final String ISO_FORMAT = "yyyy-MM-dd";
	private static final String[] ACCEPTED_DATE_FORMATS = { ISO_FORMAT, "dd/MM/yyyy", "d/M/yyyy" };

	private Channel channel = Channel.getChannel();
	private View view = View.MY_RESERVATIONS;

	private String roomId;
	private Long reservationId;
	private Room salle;
	private Reservation reservation;
	private String searchTerm;
	private String filterByStatus;
	private Date dateheureDebut;
	private Date dateheureFin;
	private boolean displayingAdminViewFilter = false;
	private int roomPage = 1;

	private String catalogAvailabilityFilter; // all | free | busy
	private String catalogLocationFilter;
	private String catalogEquipmentFilter;
	private String catalogSearchTerm;
	private int catalogPage = 1;

	private String toValidateMemberFilter;
	private String toValidateSearchTerm;
	private String toValidateStartDate;
	private String toValidateEndDate;
	private int toValidatePage = 1;

	private String statsPeriod; // WEEK | MONTH | QUARTER | YEAR

	private String calTab; // jour | semaine | mois
	private String calDateStr; // date de référence (ISO ou dd/MM/yyyy en entrée)
	private String calDay; // jour sélectionné pour la modale "réservations du jour"

	// Filtres de doDisplayingReservationsTemplate.jsp (Mes réservations / Toutes les réservations)
	private String filterByReservationStatus = "all";
	private String filterByRoomId;
	private String filterFromDateStr;
	private String filterToDateStr;
	private String filterByMemberId = "all";

	public enum View {
		MY_RESERVATIONS, ALL_RESERVATIONS, CALENDAR, ALL_ROOMS, ROOM_DETAIL, TO_VALIDATE, DASHBOARD, ADMIN, ADMIN_ROOMS,
		ADMIN_CONFIG, SELECT_ROOM
	}

	// ---------------------------------------------------------------------
	// URLs et navigation
	// ---------------------------------------------------------------------

	public String getAppUrl() {
		return "plugins/ZaoMeetReservePlugin/jsp/app/zaoMeetReserveApp.jsp";
	}

	public String getViewUrl(String view) {
		return getAppUrl() + "?view=" + view;
	}

	public String getMyReservationsUrl() { return getViewUrl("MY_RESERVATIONS"); }
	public String getAllReservationsUrl() { return getViewUrl("ALL_RESERVATIONS"); }
	public String getCalendarUrl() { return getViewUrl("CALENDAR"); }
	public String getAllRoomsUrl() { return getViewUrl("ALL_ROOMS"); }
	public String getToValidateUrl() { return getViewUrl("TO_VALIDATE"); }
	public String getDashboardUrl() { return getViewUrl("DASHBOARD"); }
	public String getAdminViewUrl() { return getViewUrl("ADMIN"); }

	public String getBoxDisplayFilter() {
		return view.name();
	}

	public void setView(String v) {
		try {
			this.view = View.valueOf(v);
		} catch (IllegalArgumentException ignore) {
			// vue inconnue : on garde la vue courante
		}
	}

	public String getNavbarHiddenFields() {
		return getHiddenField("view", getBoxDisplayFilter());
	}

	public boolean showMyReservationsView() { return view == View.MY_RESERVATIONS; }
	public boolean showAllReservationsView() { return view == View.ALL_RESERVATIONS; }
	public boolean showCalendarView() { return view == View.CALENDAR; }
	public boolean showAllRoomsView() { return view == View.ALL_ROOMS; }
	public boolean showToValidateView() { return view == View.TO_VALIDATE; }
	public boolean showDashboardView() { return view == View.DASHBOARD; }
	public boolean showAdminView() { return view == View.ADMIN; }
	public boolean showRoomDetailItem() { return view == View.ROOM_DETAIL; }
	public boolean showAdminRoomsView() { return view == View.ADMIN_ROOMS; }
	public boolean showAdminConfigView() { return view == View.ADMIN_CONFIG; }
	public boolean showSelectRoomItem() { return view == View.SELECT_ROOM; }

	public String getAppTitle() {
		if (showMyReservationsView())
			return glp("jcmsplugin.zaomeetreserve.app.sidebar.my-reservations.label");
		if (showAllReservationsView())
			return glp("jcmsplugin.zaomeetreserve.app.sidebar.all-reservations.label");
		if (showCalendarView())
			return glp("jcmsplugin.zaomeetreserve.app.sidebar.calendar.label");
		if (showAllRoomsView())
			return glp("jcmsplugin.zaomeetreserve.app.sidebar.all-rooms.label");
		if (showToValidateView())
			return glp("jcmsplugin.zaomeetreserve.app.sidebar.to-validate.label");
		if (showDashboardView())
			return glp("jcmsplugin.zaomeetreserve.app.sidebar.dashboard.label");
		if (showAdminView())
			return glp("jcmsplugin.zaomeetreserve.app.sidebar.admin.label");
		if (showRoomDetailItem() && Util.notEmpty(getSalle()))
			return getSalle().getTitle();
		return glp("jcmsplugin.zaomeetreserve.app.name");
	}

	public String getAdminBreadcrumb() {
		StringBuilder sb = new StringBuilder(glp("jcmsplugin.zaomeetreserve.app.breadcrumb.home"));
		if (showAdminRoomsView()) {
			sb.append(" \u203a ").append(glp("jcmsplugin.zaomeetreserve.app.view.admin.header.label"))
			  .append(" \u203a ").append(glp("jcmsplugin.zaomeetreserve.app.view.admin.rooms.header.label"));
		} else if (showAdminConfigView()) {
			sb.append(" \u203a ").append(glp("jcmsplugin.zaomeetreserve.app.view.admin.header.label"))
			  .append(" \u203a ").append(glp("jcmsplugin.zaomeetreserve.app.view.admin.config.header.label"));
		} else if (showAdminView()) {
			sb.append(" \u203a ").append(glp("jcmsplugin.zaomeetreserve.app.view.admin.header.label"));
		}
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	// Droits
	// ---------------------------------------------------------------------

	public boolean canAccessAdmin(Member member) {
		return member != null && member.isAdmin();
	}

	// Source unique de vérité : ZaoMeetReserveUtils.canManageApp()
	public boolean canManageApp() {
		return ZaoMeetReserveUtils.canManageApp(loggedMember);
	}

	// ---------------------------------------------------------------------
	// Salle / réservation courantes
	// ---------------------------------------------------------------------

	public String getSalleId() { return roomId; }
	public String getRoomId() { return roomId; }

	public void setRoomId(String roomId) {
		this.roomId = roomId;
		this.salle = ZaoMeetReserveManager.getInstance().getRoomById(roomId);
	}

	public Room getSalle() { return salle; }

	public Long getReservationId() { return reservationId; }

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
		this.reservation = ZaoMeetReserveManager.getInstance().getReservationById(reservationId);
	}

	public Reservation getReservation() { return reservation; }

	public List<Room> getAllRooms() {
		return ZaoMeetReserveManager.getInstance().getAllRooms();
	}

	// Peuple le select "salle" du filtre
	public String[] getAllRoomIdsAsArray() {
		List<Room> rooms = getAllRooms();
		String[] ids = new String[rooms.size() + 1];
		ids[0] = "all";
		for (int i = 0; i < rooms.size(); i++) {
			ids[i + 1] = rooms.get(i).getId();
		}
		return ids;
	}

	public String[] getAllRoomTitlesAsArray() {
		List<Room> rooms = getAllRooms();
		String[] titles = new String[rooms.size() + 1];
		titles[0] = glp("jcmsplugin.zaomeetreserve.app.view.reservations.filter.status.all");
		for (int i = 0; i < rooms.size(); i++) {
			titles[i + 1] = rooms.get(i).getTitle();
		}
		return titles;
	}

	public String[] getAvailableLocalisationsAsArray() {
		return getAllRooms().stream()
			.map(Room::getLocalisation)
			.filter(Util::notEmpty)
			.distinct()
			.sorted()
			.toArray(String[]::new);
	}

	// ---------------------------------------------------------------------
	// Filtres des vues de réservations
	// ---------------------------------------------------------------------

	public String getFilterByReservationStatus() {
		return Util.isEmpty(filterByReservationStatus) ? "all" : filterByReservationStatus;
	}
	public void setFilterByReservationStatus(String v) { this.filterByReservationStatus = v; }

	public String getFilterByMemberId() {
		return Util.isEmpty(filterByMemberId) ? "all" : filterByMemberId;
	}
	public void setFilterByMemberId(String v) { this.filterByMemberId = v; }

	public String getFilterByRoomId() { return filterByRoomId; }
	public void setFilterByRoomId(String v) { this.filterByRoomId = v; }

	public String getFilterFromDateStr() { return filterFromDateStr; }
	public void setFilterFromDateStr(String v) { this.filterFromDateStr = v; }

	public String getFilterToDateStr() { return filterToDateStr; }
	public void setFilterToDateStr(String v) { this.filterToDateStr = v; }

	public String getSearchTerm() { return searchTerm; }
	public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

	public boolean hasSearch() { return Util.notEmpty(getSearchTerm()); }

	public String getFilterByStatus() { return filterByStatus; }
	public void setFilterByStatus(String filterByStatus) { this.filterByStatus = filterByStatus; }

	// Alias "Statut" utilisé par doAdminRooms.jsp
	public String getFilterByStatut() { 
		return Util.isEmpty(filterByStatus) ? "all" : filterByStatus;
	}
	public void setFilterByStatut(String filterByStatut) {
		this.filterByStatus = filterByStatut; 
	}

	public Date getDateheureDebut() { return dateheureDebut; }
	public void setDateheureDebut(Date d) { this.dateheureDebut = d; }

	public Date getDateheureFin() { return dateheureFin; }
	public void setDateheureFin(Date d) { this.dateheureFin = d; }

	public boolean isDisplayingAdminViewFilter() { return displayingAdminViewFilter; }
	public void setDisplayingAdminViewFilter(boolean v) { this.displayingAdminViewFilter = v; }

	// ---------------------------------------------------------------------
	// Statut d'une réservation
	// ---------------------------------------------------------------------

	public String getReservationStatusCode(Reservation r) {
		if (r == null) return "";
		int ps = r.getPstatus();
		if (ps == ZaoMeetReserveConstants.RESERVATION_WF_REJECTED_PSTATUS) {
			return "CANCELLED";
		}
		if (r.getDateheureFin() != null && r.getDateheureFin().before(new Date())) {
			return "PASSED";
		}
		if (ps == ZaoMeetReserveConstants.RESERVATION_WF_CONFIRMED_PSTATUS) {
			return "CONFIRMED";
		}
		return "PENDING";
	}

	public String getReservationStatusLabelKey(Reservation r) {
		switch (getReservationStatusCode(r)) {
			case "CONFIRMED": return "jcmsplugin.zaomeetreserve.calendar.legend.confirmed";
			case "PENDING":   return "jcmsplugin.zaomeetreserve.calendar.legend.pending";
			case "PASSED":    return "jcmsplugin.zaomeetreserve.reservation.status.past";
			default:          return "jcmsplugin.zaomeetreserve.reservation.status.cancelled";
		}
	}

	public String getReservationStatusLabelClass(Reservation r) {
		switch (getReservationStatusCode(r)) {
			case "CONFIRMED": return "label-success";
			case "PENDING":   return "label-warning";
			case "PASSED":    return "label-default";
			default:          return "label-danger";
		}
	}

	public boolean isReservationPast(Reservation r) {
		return Util.notEmpty(r) && Util.notEmpty(r.getDateheureFin()) && r.getDateheureFin().before(new Date());
	}

	// ---------------------------------------------------------------------
	// Statut d'une salle
	// ---------------------------------------------------------------------

	public boolean isRoomOccupiedNow(Room room) {
		if (Util.isEmpty(room)) return false;
		Date now = new Date();
		for (Reservation r : ZaoMeetReserveManager.getInstance().getAllReservations()) {
			if (Util.notEmpty(r.getSalle()) && r.getSalle().getId().equals(room.getId())
					&& Util.notEmpty(r.getDateheureDebut()) && Util.notEmpty(r.getDateheureFin())
					&& !now.before(r.getDateheureDebut()) && !now.after(r.getDateheureFin())) {
				return true;
			}
		}
		return false;
	}

	public RoomStatus getSalleStatus(Room room) {
		return Util.isEmpty(room) ? RoomStatus.AVAILABLE : RoomStatus.fromRaw(room.getStatut());
	}

	public String getSalleStatusCss(Room room) {
		switch (getSalleStatus(room)) {
			case MAINTENANCE: return "zaomeetreserve-status-maintenance";
			case OCCUPIED:    return "zaomeetreserve-status-occupied";
			default:          return "zaomeetreserve-status-available";
		}
	}

	public String getSalleStatusLabelKey(Room room) {
		switch (getSalleStatus(room)) {
			case MAINTENANCE: return "jcmsplugin.zaomeetreserve.app.view.room.status.maintenance";
			case OCCUPIED:    return "jcmsplugin.zaomeetreserve.app.view.room.status.occupied";
			default:          return "jcmsplugin.zaomeetreserve.app.view.room.status.available";
		}
	}

	// Alias utilisés par doAdminRooms.jsp
	public String getRoomStatutCss(Room room) { return getSalleStatusCss(room); }
	public String getRoomStatutLabelKey(Room room) { return getSalleStatusLabelKey(room); }

	// Badge Bootstrap du catalogue "Toutes les salles"
	public String getRoomStatutLabelClass(Room room) {
		switch (getSalleStatus(room)) {
			case MAINTENANCE: return "label-default";
			case OCCUPIED:    return "label-warning";
			default:          return "label-success";
		}
	}

	// ---------------------------------------------------------------------
	// Gestion des salles (admin) : tri, filtre, pagination
	// ---------------------------------------------------------------------

	public List<Room> getAllRoomsOrderByFields(List<Room> rooms, String sortField, boolean descending) {
		if (Util.isEmpty(rooms) || Util.isEmpty(sortField)) {
			return rooms;
		}

		Comparator<Room> comparator;
		switch (sortField) {
			case "title":
				comparator = Comparator.comparing(Room::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
				break;
			case "capacite":
			case "capacity":
				comparator = Comparator.comparing(Room::getCapacite, Comparator.nullsLast(Comparator.naturalOrder()));
				break;
			case "localisation":
				comparator = Comparator.comparing(Room::getLocalisation, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
				break;
			case "cdate":
			default:
				comparator = Comparator.comparing(Room::getCdate, Comparator.nullsLast(Comparator.naturalOrder()));
				break;
		}

		if (descending) {
			comparator = comparator.reversed();
		}

		List<Room> sorted = new ArrayList<>(rooms); // copie : la liste source peut être immuable
		sorted.sort(comparator);
		return sorted;
	}

	public List<Room> getFilteredRooms() {
		List<Room> rooms = new ArrayList<>(ZaoMeetReserveManager.getInstance().getAllRooms());

		if (Util.notEmpty(filterByStatus) && !"all".equalsIgnoreCase(filterByStatus)) {
			try {
				RoomStatus wanted = RoomStatus.valueOf(filterByStatus.toUpperCase());
				rooms.removeIf(r -> getSalleStatus(r) != wanted);
			} catch (IllegalArgumentException ignore) {
				// valeur de filtre inconnue -> pas de filtrage
			}
		}

		// la vue admin envoie "catalogSearchTerm", la vue liste "searchTerm"
		String term = Util.notEmpty(searchTerm) ? searchTerm : catalogSearchTerm;
		if (Util.notEmpty(term)) {
			String search = term.toLowerCase();
			rooms.removeIf(r ->
				!(Util.notEmpty(r.getTitle()) && r.getTitle().toLowerCase().contains(search))
				&& !(Util.notEmpty(r.getLocalisation()) && r.getLocalisation().toLowerCase().contains(search))
			);
		}
		return rooms;
	}

	public int getSallePage() { return roomPage; }
	public void setSallePage(int roomPage) { this.roomPage = roomPage; }

	public List<Room> getSallesForCurrentPage() {
		List<Room> filtered = getFilteredRooms();
		int pageSize = ZaoMeetReserveConstants.ADMIN_ROOMS_PAGE_SIZE;
		int fromIndex = (getSallePage() - 1) * pageSize;
		if (fromIndex >= filtered.size() || fromIndex < 0) {
			return new ArrayList<>();
		}
		return filtered.subList(fromIndex, Math.min(fromIndex + pageSize, filtered.size()));
	}

	public int getSalleTotalPages() {
		int pageSize = ZaoMeetReserveConstants.ADMIN_ROOMS_PAGE_SIZE;
		return Math.max(1, (int) Math.ceil(getFilteredRooms().size() / (double) pageSize));
	}

	// ---------------------------------------------------------------------
	// Toutes les salles (catalogue membre)
	// ---------------------------------------------------------------------

	public List<Room> getCatalogRoomsForCurrentPage() {
		List<Room> rooms = new ArrayList<>(ZaoMeetReserveManager.getInstance().getAllRooms());

		if ("free".equalsIgnoreCase(getCatalogAvailabilityFilter())) {
			rooms.removeIf(r -> getSalleStatus(r) != RoomStatus.AVAILABLE);
		} else if ("busy".equalsIgnoreCase(getCatalogAvailabilityFilter())) {
			rooms.removeIf(r -> getSalleStatus(r) == RoomStatus.AVAILABLE);
		}

		if (Util.notEmpty(getCatalogLocationFilter()) && !"ALL".equalsIgnoreCase(getCatalogLocationFilter())) {
			rooms.removeIf(r -> !getCatalogLocationFilter().equalsIgnoreCase(r.getLocalisation()));
		}

		if (Util.notEmpty(getCatalogEquipmentFilter()) && !"ALL".equalsIgnoreCase(getCatalogEquipmentFilter())) {
			rooms.removeIf(r -> Util.isEmpty(r.getEquipements())
					|| !r.getEquipements().toLowerCase().contains(getCatalogEquipmentFilter().toLowerCase()));
		}

		String search = ZaoMeetReserveUtils.normalizeSearch(getCatalogSearchTerm());
		if (Util.notEmpty(search)) {
			rooms.removeIf(r -> Util.isEmpty(r.getTitle()) || !r.getTitle().toLowerCase().contains(search));
		}

		int pageSize = ZaoMeetReserveConstants.ADMIN_ROOMS_PAGE_SIZE;
		int fromIndex = (getCatalogPage() - 1) * pageSize;
		if (fromIndex >= rooms.size() || fromIndex < 0) {
			return new ArrayList<>();
		}
		return rooms.subList(fromIndex, Math.min(fromIndex + pageSize, rooms.size()));
	}

	public String getCatalogAvailabilityFilter() { return catalogAvailabilityFilter; }
	public void setCatalogAvailabilityFilter(String v) { this.catalogAvailabilityFilter = v; }

	public String getCatalogLocationFilter() { return catalogLocationFilter; }
	public void setCatalogLocationFilter(String v) { this.catalogLocationFilter = v; }

	public String getCatalogEquipmentFilter() { return catalogEquipmentFilter; }
	public void setCatalogEquipmentFilter(String v) { this.catalogEquipmentFilter = v; }

	public String getCatalogSearchTerm() { return catalogSearchTerm; }
	public void setCatalogSearchTerm(String v) { this.catalogSearchTerm = v; }

	public int getCatalogPage() { return catalogPage; }
	public void setCatalogPage(int catalogPage) { this.catalogPage = catalogPage; }

	// ---------------------------------------------------------------------
	// Mes réservations à valider
	// ---------------------------------------------------------------------

	public List<Reservation> getPendingReservationsForCurrentPage() {
		List<Reservation> pending = new ArrayList<>(ZaoMeetReserveManager.getInstance().getPendingReservations());

		if (Util.notEmpty(getToValidateMemberFilter()) && !"ALL".equalsIgnoreCase(getToValidateMemberFilter())) {
			pending.removeIf(r -> Util.isEmpty(r.getAuthor())
					|| !getToValidateMemberFilter().equalsIgnoreCase(r.getAuthor().getId()));
		}

		Date from = parseDate(getToValidateStartDate());
		Date to = parseDate(getToValidateEndDate());
		pending = ZaoMeetReserveManager.getInstance().getReservationsFilteredByDateRange(pending, from, to);

		String search = ZaoMeetReserveUtils.normalizeSearch(getToValidateSearchTerm());
		if (Util.notEmpty(search)) {
			pending.removeIf(r -> !(Util.notEmpty(r.getSalle()) && Util.notEmpty(r.getSalle().getTitle())
					&& r.getSalle().getTitle().toLowerCase().contains(search))
					&& !(Util.notEmpty(r.getAuthor()) && Util.notEmpty(r.getAuthor().getFullName())
					&& r.getAuthor().getFullName().toLowerCase().contains(search)));
		}

		int pageSize = ZaoMeetReserveConstants.TO_VALIDATE_PAGE_SIZE;
		int fromIndex = (getToValidatePage() - 1) * pageSize;
		if (fromIndex >= pending.size() || fromIndex < 0) {
			return new ArrayList<>();
		}
		return pending.subList(fromIndex, Math.min(fromIndex + pageSize, pending.size()));
	}

	public int getPendingReservationsCount() {
		return ZaoMeetReserveManager.getInstance().getPendingReservations().size();
	}

	public int getToValidateTotalPages() {
		int size = ZaoMeetReserveManager.getInstance().getPendingReservations().size();
		int pageSize = ZaoMeetReserveConstants.TO_VALIDATE_PAGE_SIZE;
		return Math.max(1, (int) Math.ceil(size / (double) pageSize));
	}

	public String getToValidateMemberFilter() { return toValidateMemberFilter; }
	public void setToValidateMemberFilter(String v) { this.toValidateMemberFilter = v; }

	public String getToValidateSearchTerm() { return toValidateSearchTerm; }
	public void setToValidateSearchTerm(String v) { this.toValidateSearchTerm = v; }

	public String getToValidateStartDate() { return toValidateStartDate; }
	public void setToValidateStartDate(String v) { this.toValidateStartDate = v; }

	public String getToValidateEndDate() { return toValidateEndDate; }
	public void setToValidateEndDate(String v) { this.toValidateEndDate = v; }

	public int getToValidatePage() { return toValidatePage < 1 ? 1 : toValidatePage; }
	public void setToValidatePage(int toValidatePage) { this.toValidatePage = toValidatePage; }

	// ---------------------------------------------------------------------
	// Dates (parsing tolérant)
	// ---------------------------------------------------------------------

	/** Essaie ISO puis dd/MM/yyyy. Renvoie null si vide ou illisible. */
	private Date parseDate(String str) {
		if (Util.isEmpty(str)) {
			return null;
		}
		String s = str.trim();
		for (String pattern : ACCEPTED_DATE_FORMATS) {
			try {
				SimpleDateFormat fmt = new SimpleDateFormat(pattern);
				fmt.setLenient(false);
				return fmt.parse(s);
			} catch (ParseException e) {
				// format suivant
			}
		}
		LOG.warn("Date illisible : " + str);
		return null;
	}

	private String formatIso(Date d) {
		return new SimpleDateFormat(ISO_FORMAT).format(d);
	}

	// ---------------------------------------------------------------------
	// Calendrier (jour / semaine / mois)
	// ---------------------------------------------------------------------

	public String getCalTab() {
		return Util.isEmpty(calTab) ? "jour" : calTab;
	}

	public void setCalTab(String calTab) {
		this.calTab = calTab;
	}

	/** Toujours renseignée (ISO) : jamais null, pour que les liens et le champ date soient valides. */
	public String getCalDateStr() {
		return formatIso(getCalDate());
	}

	public void setCalDateStr(String calDateStr) {
		this.calDateStr = calDateStr;
	}

	/** Date de référence : paramètre reçu si lisible, sinon aujourd'hui. */
	public Date getCalDate() {
		Date parsed = parseDate(calDateStr);
		return parsed != null ? parsed : new Date();
	}

	public String getCalendarPrevUrl() {
		return getCalendarNavUrl(-1);
	}

	public String getCalendarNextUrl() {
		return getCalendarNavUrl(1);
	}

	private String getCalendarNavUrl(int direction) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCalDate());
		if ("semaine".equalsIgnoreCase(getCalTab())) {
			cal.add(Calendar.WEEK_OF_YEAR, direction);
		} else if ("mois".equalsIgnoreCase(getCalTab())) {
			cal.add(Calendar.MONTH, direction);
		} else {
			cal.add(Calendar.DAY_OF_MONTH, direction);
		}
		return getAppUrl() + "?view=CALENDAR&calTab=" + getCalTab() + "&calDateStr=" + formatIso(cal.getTime());
	}

	public List<TimelineRow> getDayTimelineRows() {
		List<TimelineRow> rows = new ArrayList<>();
		List<Reservation> dayReservations = ZaoMeetReserveManager.getInstance().getReservationsForDay(getCalDate());

		for (Room nextRoom : ZaoMeetReserveManager.getInstance().getAllRooms()) {
			TimelineRow row = new TimelineRow(nextRoom.getTitle());

			if (getSalleStatus(nextRoom) == RoomStatus.MAINTENANCE) {
				row.addBlock(new TimelineBlock(glp("jcmsplugin.zaomeetreserve.room.status.maintenance"),
						ZaoMeetReserveConstants.CALENDAR_TIMELINE_START_HOUR,
						ZaoMeetReserveConstants.CALENDAR_TIMELINE_END_HOUR, "off"));
				rows.add(row);
				continue;
			}

			for (Reservation r : dayReservations) {
				if (Util.isEmpty(r.getSalle()) || !nextRoom.getId().equals(r.getSalle().getId())) {
					continue;
				}
				row.addBlock(toTimelineBlock(r));
			}
			rows.add(row);
		}
		return rows;
	}

	public List<TimelineRow> getWeekTimelineRows() {
		List<TimelineRow> rows = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(getCalDate());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		String[] dayLabelKeys = { "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday" };
		for (String dayKey : dayLabelKeys) {
			TimelineRow row = new TimelineRow(glp("jcmsplugin.zaomeetreserve.calendar.weekday." + dayKey));
			for (Reservation r : ZaoMeetReserveManager.getInstance().getReservationsForDay(cal.getTime())) {
				row.addBlock(toTimelineBlock(r));
			}
			rows.add(row);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return rows;
	}

	private TimelineBlock toTimelineBlock(Reservation r) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(r.getDateheureDebut());
		double startHour = startCal.get(Calendar.HOUR_OF_DAY) + startCal.get(Calendar.MINUTE) / 60.0;

		double endHour = startHour + 1;
		if (Util.notEmpty(r.getDateheureFin())) {
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(r.getDateheureFin());
			endHour = endCal.get(Calendar.HOUR_OF_DAY) + endCal.get(Calendar.MINUTE) / 60.0;
		}

		String type = r.getPstatus() == ZaoMeetReserveConstants.RESERVATION_WF_CONFIRMED_PSTATUS ? "confirmed" : "pending";
		String label = Util.notEmpty(r.getSalle()) ? r.getSalle().getTitle() : r.getTitle();
		return new TimelineBlock(label, startHour, endHour, type, r.getDateheureFin());
	}

	public List<MonthCell> getMonthCells() {
		List<MonthCell> cells = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCalDate());
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		int isoDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7; // lundi = 0
		for (int i = 0; i < isoDayOfWeek; i++) {
			cells.add(new MonthCell(0, "", 0));
		}

		for (int day = 1; day <= daysInMonth; day++) {
			cal.set(Calendar.DAY_OF_MONTH, day);
			int count = ZaoMeetReserveManager.getInstance().getReservationsForDay(cal.getTime()).size();
			cells.add(new MonthCell(day, formatIso(cal.getTime()), count, cal.getTime()));
		}
		return cells;
	}

	public List<Reservation> getSelectedDayReservations() {
		Date day = parseDate(getCalDay());
		return ZaoMeetReserveManager.getInstance().getReservationsForDay(day != null ? day : getCalDate());
	}

	public String getCalDay() { return calDay; }
	public void setCalDay(String calDay) { this.calDay = calDay; }

	// ---------------------------------------------------------------------
	// Dashboard
	// ---------------------------------------------------------------------

	private Date[] getStatsPeriodRange() {
		Calendar cal = Calendar.getInstance();
		Date to = cal.getTime();
		switch (getStatsPeriod()) {
			case "MONTH":
				cal.add(Calendar.MONTH, -1);
				break;
			case "QUARTER":
				cal.add(Calendar.MONTH, -3);
				break;
			case "YEAR":
				cal.add(Calendar.YEAR, -1);
				break;
			default:
				cal.add(Calendar.WEEK_OF_YEAR, -1);
		}
		return new Date[] { cal.getTime(), to };
	}

	public Map<String, Integer> getOccupancyRateByRoom() {
		Date[] range = getStatsPeriodRange();
		return ZaoMeetReserveManager.getInstance().getOccupancyRateByRoom(range[0], range[1]);
	}

	public int getAverageOccupancyRate() {
		Map<String, Integer> rates = getOccupancyRateByRoom();
		if (Util.isEmpty(rates)) {
			return 0;
		}
		int sum = 0;
		for (int v : rates.values()) {
			sum += v;
		}
		return sum / rates.size();
	}

	public Map<String, Integer> getReservationCountPerWeek() {
		return ZaoMeetReserveManager.getInstance().getReservationCountPerWeek(4);
	}

	public int getReservationCountThisWeek() {
		int last = 0;
		for (int v : getReservationCountPerWeek().values()) {
			last = v;
		}
		return last;
	}

	public String getMostRequestedRoomTitle() {
		Date[] range = getStatsPeriodRange();
		return ZaoMeetReserveManager.getInstance().getMostRequestedRoomTitle(range[0], range[1]);
	}

	public String getStatsPeriod() {
		return Util.isEmpty(statsPeriod) ? "WEEK" : statsPeriod;
	}

	public void setStatsPeriod(String statsPeriod) {
		this.statsPeriod = statsPeriod;
	}
}