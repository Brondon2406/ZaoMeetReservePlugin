package co.kozao.jcmsplugin.zaomeetreserve.handler;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import generated.Reservation;
import generated.Room;

public class ReservationHandler extends JcmsFormHandler {

	private static final Logger LOG = Logger.getLogger(ReservationHandler.class);

	private String reservationId;
	private Reservation reservation;
	private String roomId;
	private String title;
	private Date startDate;
	private Date endDate;
	private boolean recurrente;
	private boolean checkinEffectue;
	private String motif;

	private boolean opCreate;
	private boolean opUpdate;
	private boolean opDelete;

	@Override
	public boolean processAction() throws IOException {
		if (opCreate || validateFields()) {
			return createReservation();
		}
		return super.processAction();
	}

	public boolean validateFields() {
		if (Util.isEmpty(roomId)) {
			setWarningMsg(glp("jcmsplugin.zaomeetreserve.reservation.field.room-id.message"));
			return false;
		}
		return true;
	}

	// Private Methods
	private boolean createReservation() {
		Reservation newReservation = applyFieldsTo();
		boolean created = ZaoMeetReserveManager.getInstance().save(newReservation, loggedMember);
		if (!created) {
			setWarningMsg(glp("jcmsplugin.zaomeetreserve.reservation.create.message"));
		}
		return created;
	}

	private Reservation applyFieldsTo() {
		Reservation newReservation = new Reservation();
		newReservation.setTitle(title);
		newReservation.setStartDate(startDate);
		newReservation.setEndDate(endDate);
		newReservation.setRecurrente(recurrente);
		newReservation.setCheckinEffectue(checkinEffectue);
		newReservation.setMotif(motif);
		newReservation.setAuthor(loggedMember);
		if (Util.notEmpty(roomId)) {
			Room room = ZaoMeetReserveManager.getInstance().getRoomById(roomId);
			newReservation.setSalle(room);
		}
		return newReservation;
	}

	// Getters and Setters
	public boolean isUpdateOperation() {
		return Util.notEmpty(reservationId);
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
		this.reservation = ZaoMeetReserveManager.getInstance().getReservationById(reservationId);
		if (Util.notEmpty(reservation)) {
			this.title = reservation.getTitle();
			this.startDate = reservation.getStartDate();
			this.endDate = reservation.getEndDate();
			this.motif = reservation.getMotif();
			if (Util.notEmpty(reservation.getSalle())) {
				this.roomId = reservation.getSalle().getId();
			}
		}
	}

	public Reservation getReservation() {
		return reservation;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isRecurrente() {
		return recurrente;
	}

	public void setRecurrente(boolean recurrente) {
		this.recurrente = recurrente;
	}

	public boolean isCheckinEffectue() {
		return checkinEffectue;
	}

	public void setCheckinEffectue(boolean checkinEffectue) {
		this.checkinEffectue = checkinEffectue;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public boolean isOpCreate() {
		return opCreate;
	}

	public void setOpCreate(boolean opCreate) {
		this.opCreate = opCreate;
	}

	public boolean isOpUpdate() {
		return opUpdate;
	}

	public void setOpUpdate(boolean opUpdate) {
		this.opUpdate = opUpdate;
	}

	public boolean isOpDelete() {
		return opDelete;
	}

	public void setOpDelete(boolean opDelete) {
		this.opDelete = opDelete;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
}