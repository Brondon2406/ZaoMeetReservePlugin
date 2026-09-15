package co.kozao.jcmsplugin.zaomeetreserve.handler;

import java.io.IOException;
import java.util.Date;

import com.jalios.jcms.Channel;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.manager.BookingAppManager;
import generated.Reservation;
import generated.Room;

public class CreateAndUpdateReservationHandler extends JcmsFormHandler {

    private Channel channel = Channel.getChannel();

    private String reservationId;
    private Reservation reservation;

    private String roomId;
    private String title;
    private Date startDate;
    private Date endDate;
    private boolean recurrente;
    private boolean checkinEffectue;
    private String motif;

    public boolean isUpdateOperation() {
        return Util.notEmpty(reservationId);
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
        this.reservation = BookingAppManager.getInstance().getReservationById(reservationId);
        if (Util.notEmpty(reservation)) {
            this.title = reservation.getTitle();
            this.startDate = reservation.getStartDate();
            this.endDate = reservation.getEndDate();
            this.recurrente = reservation.isRecurrente();
            this.checkinEffectue = reservation.CheckinEffectue();
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

    public void opCreateReservation() throws IOException {
        if (!validate()) {
            Reservation newReservation = new Reservation();
            applyFieldsTo(newReservation);
            channel.createData(newReservation, loggedMember);
        }
    }

    public void opUpdateReservation() throws IOException {
        if (!validate() && Util.notEmpty(reservation)) {
            applyFieldsTo(reservation);
            channel.updateData(reservation, loggedMember);
        }
    }

    private void applyFieldsTo(Reservation target) {
        target.setTitle(title);
        target.setStartDate(startDate);
        target.setEndDate(endDate);
        target.setRecurrente(recurrente);
        target.setCheckinEffectue(checkinEffectue);
        target.setMotif(motif);
        if (Util.notEmpty(roomId)) {
            Room room = BookingAppManager.getInstance().getRoomById(roomId);
            target.setSalle(room);
        }
    }
}