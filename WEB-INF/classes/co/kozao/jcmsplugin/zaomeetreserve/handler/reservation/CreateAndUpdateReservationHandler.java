package co.kozao.jcmsplugin.zaomeetreserve.handler.reservation;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import co.kozao.jcmsplugin.zaomeetreserve.util.ZaoMeetReserveConstants;
import generated.Reservation;
import generated.Room;

public class CreateAndUpdateReservationHandler extends JcmsFormHandler {
	
	private static final Logger LOGGER = Logger.getLogger(CreateAndUpdateReservationHandler.class);
    private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";

    private Channel channel = Channel.getChannel();

    private Long reservationId;
    private Reservation reservation;

    private String roomId;
    private String title;
    private String startDateString;
    private String endDateString;
    private Date dateheureDebut;
    private Date dateheureFin;
    private boolean recurrent;
    private boolean checkinEffectue;
    private String motif;

   

    @Override
    public boolean processAction() throws IOException {
        if (opPrevious) {
            --this.formStep;
        } else if (opNext) {
            if (validateLevel()) {
                ++this.formStep;
            }
        } else if (opFinish && validateLevel()) {
            if (isRoomAlreadyBooked()) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.room-conflict"), request);
                return false;
            }
            if (isUpdateOperation()) {
                return updateReservation();
            } else {
                return createReservation();
            }
        }
        return super.processAction();
    }

    public boolean validateLevel() {
        if (this.formStep == 0) {
            return validateFormStep0();
        }
        return true;
    }

    public boolean validateFormStep0() {
        if (Util.isEmpty(title)) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.title-required"), request);
            return false;
        }
        if (Util.isEmpty(roomId)) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.room-required"), request);
            return false;
        }
        if (Util.isEmpty(startDateString) || Util.isEmpty(endDateString)) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.dates-required"), request);
            return false;
        }
        Date start = getDateheureDebut();
        Date end = getDateheureFin();
        if (start == null || end == null) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.dates-required"), request);
            return false;
        }
        if (!start.before(end)) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.dates-invalid"), request);
            return false;
        }
        return true;
    }

    @Override
    public int getFormStepCount() {
        return 2;
    }

    @Override
    public String getFormStepPrefixProp() {
        return "jcmsplugin.zaomeetreserve.modal.add-reservation.step-modal.step";
    }

    @Override
    public String getFormStepFinishLabel() {
        return isUpdateOperation()
                ? "jcmsplugin.zaomeetreserve.modal.update-reservation.btn.title"
                : "jcmsplugin.zaomeetreserve.modal.create-reservation.btn.title";
    }

    @Override
    public boolean showNextButton() {
        return this.formStep < getFormStepCount() - 1;
    }

    @Override
    public boolean showPreviousButton() {
        return this.formStep > 0;
    }

    @Override
    public boolean showFinishButton() {
        return this.formStep == getFormStepCount() - 1;
    }

    @Override
    public String getFormStepHiddenFields() {
        StringBuilder sb = new StringBuilder();

        if (reservationId != null) {
            sb.append(this.getHiddenField("reservationId", reservationId.longValue()));
        }

        if (this.formStep == 1) {
            sb.append(this.getHiddenField("title", getTitle()));
            if (Util.notEmpty(roomId)) sb.append(getHiddenField("roomId", roomId));
            sb.append(this.getHiddenField("startDateString", getStartDateString()));
            sb.append(this.getHiddenField("endDateString", getEndDateString()));
        }

        if (this.formStep == 0) {
            sb.append(this.getHiddenField("motif", getMotif()));
            sb.append(this.getHiddenField("recurrent", String.valueOf(isRecurrent())));
            sb.append(this.getHiddenField("checkinEffectue", String.valueOf(isCheckinEffectue())));
        }

        return sb.toString();
    }
    

    public boolean isUpdateOperation() {
        return Util.notEmpty(reservationId);
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
        this.reservation = ZaoMeetReserveManager.getInstance().getReservationById(reservationId);
        if (Util.notEmpty(reservation)) {
            this.title = reservation.getTitle();
            this.dateheureDebut = reservation.getDateheureDebut();
            this.dateheureFin = reservation.getDateheureFin();
            this.startDateString = formatDate(this.dateheureDebut);
            this.endDateString = formatDate(this.dateheureFin);
            this.recurrent = reservation.getRecurrent();
            this.checkinEffectue = reservation.getCheckinEffectue();
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

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
        this.dateheureDebut = null; 
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
        this.dateheureFin = null;
    }

    public Date getDateheureDebut() {
        if (dateheureDebut == null && Util.notEmpty(startDateString)) {
        	dateheureDebut = parseDate(startDateString);
        }
        return dateheureDebut;
    }

    public Date getDateheureFin() {
        if (dateheureFin == null && Util.notEmpty(endDateString)) {
        	dateheureFin = parseDate(endDateString);
        }
        return dateheureFin;
    }

    public boolean isRecurrent() {
        return recurrent;
    }

    public void setRecurrent(boolean recurrent) {
        this.recurrent = recurrent;
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

    

    private static final String[] DATE_PATTERNS = {
    	    "dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd'T'HH:mm"
    	};

    	private static Date parseDate(String value) {
    	    if (Util.isEmpty(value)) return null;
    	    for (String pattern : DATE_PATTERNS) {
    	        try {
    	            SimpleDateFormat f = new SimpleDateFormat(pattern);
    	            f.setLenient(false);
    	            return f.parse(value.trim());
    	        } catch (ParseException ignore) { }
    	    }
    	    LOGGER.warn("Impossible de parser la date : '" + value + "'");
    	    return null;
    	}

    private static String formatDate(Date value) {
        if (value == null) return "";
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(value);
    }


    private boolean createReservation() {
        Reservation newReservation = new Reservation();
        applyFieldsTo(newReservation);
        newReservation.setPstatus(ZaoMeetReserveConstants.RESERVATION_WF_PENDING_PSTATUS);

        boolean ok = ZaoMeetReserveManager.getInstance().save(newReservation, loggedMember);
        if (!ok) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.create"), request);
            return false;
        }
        return true;
    }
    private boolean updateReservation() {
        if (Util.isEmpty(reservation)) return false;
        Reservation target = (Reservation) reservation.getUpdateInstance();
        applyFieldsTo(target);
        if (!ZaoMeetReserveManager.getInstance().updateReservation(target, loggedMember)) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.add-reservation.error.create"), request);
            return false;
        }
        return true;
    }

    private void applyFieldsTo(Reservation target) {
        target.setTitle(title);
        target.setDateheureDebut(getDateheureDebut());
        target.setDateheureFin(getDateheureFin());
        target.setRecurrent(recurrent);
        target.setCheckinEffectue(checkinEffectue);
        target.setMotif(motif);
        if (Util.notEmpty(roomId)) {
            Room room = ZaoMeetReserveManager.getInstance().getRoomById(roomId);
            target.setSalle(room);
        }
    }

    private boolean isRoomAlreadyBooked() {
        Date start = getDateheureDebut();
        Date end = getDateheureFin();
        Room room = ZaoMeetReserveManager.getInstance().getRoomById(roomId);
        if (Util.isEmpty(room) || start == null || end == null) return false;

        for (Reservation r : ZaoMeetReserveManager.getInstance().getAllReservations()) {
            if (Util.isEmpty(r.getSalle()) || !room.getId().equals(r.getSalle().getId())) continue;
            if (Util.notEmpty(reservation) && reservation.getId().equals(r.getId())) continue;
            if (Util.isEmpty(r.getDateheureDebut()) || Util.isEmpty(r.getDateheureFin())) continue;
            if (r.getPstatus() == ZaoMeetReserveConstants.RESERVATION_WF_REJECTED_PSTATUS
                    || r.getPstatus() == ZaoMeetReserveConstants.RESERVATION_WF_CANCELLED_PSTATUS) continue;
            if (start.before(r.getDateheureFin()) && end.after(r.getDateheureDebut())) return true;
        }
        return false;
    }
}