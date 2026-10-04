package co.kozao.jcmsplugin.zaomeetreserve.handler.reservation;

import java.io.IOException;

import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import generated.Reservation;

public class DeleteReservationHandler extends JcmsFormHandler {

    private boolean opDelete = false;
    private Long reservationId;
    private Reservation reservation;

    @Override
    public boolean processAction() throws IOException {
        if (opDelete) {
            if (Util.isEmpty(getReservation())) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.delete-reservation.not-found.msg"), request);
                return false;
            }
            if (!ZaoMeetReserveManager.getInstance().deleteReservation(getReservation(), loggedMember)) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.modal.delete-reservation.error.msg"), request);
                return false;
            }
            setInfoMsg(glp("jcmsplugin.zaomeetreserve.modal.delete-reservation.success.msg"), request);
            return true;
        }
        return super.processAction();
    }

    public Reservation getReservation() {
        if (reservation == null && reservationId != null) {
            reservation = ZaoMeetReserveManager.getInstance().getReservationById(reservationId);
        }
        return reservation;
    }

    public String getReservationNameToDelete() {
        Reservation r = getReservation();
        return Util.notEmpty(r) ? r.getTitle() : "";
    }

    public boolean isOpDelete() { return opDelete; }
    public void setOpDelete(boolean opDelete) { this.opDelete = opDelete; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
}