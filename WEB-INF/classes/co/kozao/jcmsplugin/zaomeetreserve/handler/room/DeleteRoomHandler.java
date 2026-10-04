package co.kozao.jcmsplugin.zaomeetreserve.handler.room;

import java.io.IOException;
import java.util.List;

import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import generated.Reservation;
import generated.Room;

public class DeleteRoomHandler extends JcmsFormHandler {

    private boolean opDelete = false;
    private String roomId;
    private Room room;

    @Override
    public boolean processAction() throws IOException {
        if (opDelete) {
            ZaoMeetReserveManager manager = ZaoMeetReserveManager.getInstance();
            Room target = getRoom();

            if (Util.isEmpty(target)) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.rooms.delete.not-found.msg"), request);
                return false;
            }

            List<Reservation> reservations = manager.getReservationsByRoom(target);
            if (Util.notEmpty(reservations)) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.rooms.delete.has-reservations.msg",
                        Integer.valueOf(reservations.size())), request);
                return false;
            }

            if (!manager.deleteRoom(roomId, loggedMember)) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.rooms.delete.error.msg"), request);
                return false;
            }

            setInfoMsg(glp("jcmsplugin.zaomeetreserve.admin.rooms.delete.success.msg"), request);
            return true;
        }
        return super.processAction();
    }

    public Room getRoom() {
        if (room == null && Util.notEmpty(roomId)) {
            room = ZaoMeetReserveManager.getInstance().getRoomById(roomId);
        }
        return room;
    }

    public String getRoomNameToDelete() {
        Room r = getRoom();
        return Util.notEmpty(r) ? r.getTitle() : "";
    }

    public boolean isOpDelete() { return opDelete; }
    public void setOpDelete(boolean opDelete) { this.opDelete = opDelete; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}