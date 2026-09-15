package co.kozao.jcmsplugin.zaomeetreserve.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

import generated.Room;
import generated.Reservation;

public class BookingAppManager {

    private static BookingAppManager instance;
    private Channel channel = Channel.getChannel();

    private BookingAppManager() {}

    public static BookingAppManager getInstance() {
        if (instance == null) {
            instance = new BookingAppManager();
        }
        return instance;
    }

    public Room getRoomById(String roomId) {
        if (Util.isEmpty(roomId)) return null;
        Object data = channel.getStorable(roomId);
        return (data instanceof Room) ? (Room) data : null;
    }

    public Reservation getReservationById(String reservationId) {
        if (Util.isEmpty(reservationId)) return null;
        Object data = channel.getStorable(reservationId);
        return (data instanceof Reservation) ? (Reservation) data : null;
    }

    public List<Reservation> getReservationsByMember(Member member) {
        List<Reservation> result = new ArrayList<Reservation>();
        if (Util.isEmpty(member)) return result;
        Set<Reservation> all = channel.getAllDataSet(Reservation.class);
        for (Reservation r : all) {
            if (member.equals(r.getAuthor())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<Reservation>(channel.getAllDataSet(Reservation.class));
    }

    public List<Room> getAllRooms() {
        return new ArrayList<Room>(channel.getAllDataSet(Room.class));
    }
}