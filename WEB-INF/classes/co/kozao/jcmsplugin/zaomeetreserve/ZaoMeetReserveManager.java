package co.kozao.jcmsplugin.zaomeetreserve;

import java.util.List;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Member;
import com.jalios.jcms.db.HibernateUtil;
import com.jalios.util.Util;

import generated.Reservation;
import generated.Room;

/**
 * @author Arlette
 * @author brondon FOPA TIWA ( Reviewer )
 */
public class ZaoMeetReserveManager {

	private static final Logger LOG = Logger.getLogger(ZaoMeetReserveManager.class);
	private static Channel channel = Channel.getChannel();

	private static ZaoMeetReserveManager singleton;

	public static ZaoMeetReserveManager getInstance() {
		if (singleton == null) {
			singleton = new ZaoMeetReserveManager();
		}
		return singleton;
	}

	public Room getRoomById(String roomId) {
		if (Util.isEmpty(roomId))
			return null;
		Room room = (Room) channel.getData(roomId);
		return Util.notEmpty(room) ? room : null;
	}

	public Reservation getReservationById(String reservationId) {
		if (Util.isEmpty(reservationId))
			return null;
		Reservation reservation = (Reservation) channel.getData(reservationId);
		return Util.notEmpty(reservation) ? reservation : null;
	}

	public List<Reservation> getReservationsByMember(Member member) {
		if (Util.isEmpty(member))
			return java.util.Collections.emptyList();

		List<Reservation> reservations = HibernateUtil.query(Reservation.class);
		return Util.notEmpty(reservations)
				? reservations.stream().filter(reservation -> reservation.getAuthor().equals(member)).toList()
				: java.util.Collections.emptyList();
	}

	public List<Reservation> getAllReservations() {
		List<Reservation> reservations = HibernateUtil.query(Reservation.class);
		return Util.notEmpty(reservations) ? reservations : java.util.Collections.emptyList();
	}

	public List<Room> getAllRooms() {
		List<Room> rooms = HibernateUtil.query(Room.class);
		return Util.notEmpty(rooms) ? rooms : java.util.Collections.emptyList();
	}

	public boolean save(Reservation reservation, Member member) {
		if (Util.isEmpty(reservation) || Util.isEmpty(member))
			return false;

		try {
			HibernateUtil.beginTransaction();
			ControllerStatus status = reservation.checkAndPerformCreate(member);
			HibernateUtil.commitTransaction();
			return status.isOK();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			return false;
		}
	}
}