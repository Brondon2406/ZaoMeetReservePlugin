package co.kozao.jcmsplugin.zaomeetreserve.handler.app;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.manager.BookingAppManager;
import co.kozao.jcmsplugin.zaomeetreserve.util.BookingAppUtils;
import generated.Room;
import generated.Reservation;


public class BookingAppHandler extends JcmsFormHandler {

    private Channel channel = Channel.getChannel();
    private View view = View.MY_RESERVATIONS;

    private String roomId;
    private String reservationId;
    private Room room;
    private Reservation reservation;
    private String searchTerm;
    private String filterByStatus;

    public enum View {
        MY_RESERVATIONS,      
        ALL_RESERVATIONS,     
        CALENDAR,             
        ALL_ROOMS,          
        ROOM_DETAIL,          
        TO_VALIDATE,          
        DASHBOARD,            
        ADMIN,                
        ADMIN_ROOMS,          
        ADMIN_CONFIG,         
        SELECT_ROOM           
    }

    public String getAppUrl() {
        return "plugins/ZaoMeetReservePlugin/jsp/app/meetReserveApp.jsp";
    }

    public String getAppTitle() {
        if (showMyReservationsView())    return glp("jcmsplugin.zaomeetreserve.app.sidebar.my-reservations.label");
        if (showAllReservationsView())   return glp("jcmsplugin.zaomeetreserve.app.sidebar.all-reservations.label");
        if (showCalendarView())          return glp("jcmsplugin.zaomeetreserve.app.sidebar.calendar.label");
        if (showAllRoomsView())          return glp("jcmsplugin.zaomeetreserve.app.sidebar.all-rooms.label");
        if (showToValidateView())        return glp("jcmsplugin.zaomeetreserve.app.sidebar.to-validate.label");
        if (showDashboardView())         return glp("jcmsplugin.zaomeetreserve.app.sidebar.dashboard.label");
        if (showAdminView())             return glp("jcmsplugin.zaomeetreserve.app.sidebar.admin.label");
        if (showRoomDetailItem() && Util.notEmpty(getRoom())) return getRoom().getTitle();
        return glp("jcmsplugin.zaomeetreserve.app.name");
    }

    public String getBoxDisplayFilter() { return view.name(); }

    public void setView(String v) {
        try { this.view = View.valueOf(v); } catch (IllegalArgumentException ignore) {}
    }

    public String getViewUrl(String view) { return getAppUrl() + "?view=" + view; }

    
    public boolean showMyReservationsView()  { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.MY_RESERVATIONS.name()); 
    }
    public boolean showAllReservationsView() { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.ALL_RESERVATIONS.name()); 
    }
    public boolean showCalendarView()        { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.CALENDAR.name()); 
    }
    public boolean showAllRoomsView()        { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.ALL_ROOMS.name()); 
    }
    public boolean showToValidateView()      { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.TO_VALIDATE.name()); 
    }
    public boolean showDashboardView()       { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.DASHBOARD.name()); 
    }
    public boolean showAdminView()           { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.ADMIN.name()); 
    }

  
    public boolean showRoomDetailItem()  { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.ROOM_DETAIL.name());
    }
    public boolean showAdminRoomsView()  { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.ADMIN_ROOMS.name()); 
    }
    public boolean showAdminConfigView() { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.ADMIN_CONFIG.name()); 
    }
    public boolean showSelectRoomItem()  { 
    	return getBoxDisplayFilter().equalsIgnoreCase(View.SELECT_ROOM.name()); 
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
        room = co.kozao.jcmsplugin.zaomeetreserve.manager.BookingAppManager.getInstance().getRoomById(roomId);
    }
    public Room getRoom() { return room; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
        reservation = BookingAppManager.getInstance().getReservationById(reservationId);
    }
    public Reservation getReservation() { return reservation; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public String getFilterByStatus() { return filterByStatus; }
    public void setFilterByStatus(String filterByStatus) { this.filterByStatus = filterByStatus; }
}