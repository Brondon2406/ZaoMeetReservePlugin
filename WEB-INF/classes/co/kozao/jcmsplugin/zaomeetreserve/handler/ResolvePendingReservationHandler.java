package co.kozao.jcmsplugin.zaomeetreserve.handler;

import java.io.IOException;

import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import generated.Reservation;


public class ResolvePendingReservationHandler extends JcmsFormHandler {

    private boolean opApprove = false;
    private boolean opReject = false;

    private Long reservationId;
    private String rejectMessage;
    private String redirectUrl;
    private Reservation reservation;

    @Override
    public boolean processAction() throws IOException {
        boolean success = false;
        if (opApprove) {
            success = ZaoMeetReserveManager.getInstance().approveReservation(getReservation());
        } else if (opReject) {
            success = ZaoMeetReserveManager.getInstance().rejectReservation(getReservation(), getRejectMessage());
        } else {
            return super.processAction();
        }

        if (success && Util.notEmpty(getRedirectUrl())) {
            sendRedirect(getRedirectUrl());
            return true;
        }
        return success;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isOpApprove() {
        return opApprove;
    }

    public void setOpApprove(boolean opApprove) {
        this.opApprove = opApprove;
    }

    public boolean isOpReject() {
        return opReject;
    }

    public void setOpReject(boolean opReject) {
        this.opReject = opReject;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
        this.reservation = ZaoMeetReserveManager.getInstance().getReservationById(reservationId);
    }

    public Reservation getReservation() {
        if (Util.isEmpty(reservation) && Util.notEmpty(reservationId)) {
            reservation = ZaoMeetReserveManager.getInstance().getReservationById(reservationId);
        }
        return reservation;
    }

    public String getRejectMessage() {
        return rejectMessage;
    }

    public void setRejectMessage(String rejectMessage) {
        this.rejectMessage = rejectMessage;
    }
}