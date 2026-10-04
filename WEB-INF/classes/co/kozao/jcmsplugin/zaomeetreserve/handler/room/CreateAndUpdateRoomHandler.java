package co.kozao.jcmsplugin.zaomeetreserve.handler.room;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import co.kozao.jcmsplugin.zaomeetreserve.ennum.RoomStatus;
import generated.Room;

public class CreateAndUpdateRoomHandler extends JcmsFormHandler {

    private static final Logger LOGGER = Logger.getLogger(CreateAndUpdateRoomHandler.class);

    private boolean opAddRoom = false;

    private String roomId;
    private Room room;

    private String title;
    private String capacite;
    private String localisation;
    private String equipements;
    private String statut;
    private String photo;

    @Override
    public boolean processAction() throws IOException {
        if (opAddRoom) {
        	
        	
            if (!validateAddRoom()) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.rooms.modal.fields.invalid.msg"), request);

                return false;
            }

            if (Util.notEmpty(photo)) {
                try {
                    photo = updateUploadedField("photo", photo, true, false);
                } catch (Exception e) {
                    LOGGER.error("Erreur upload photo : " + photo, e);
                    photo = null;
                }
            }
            

            LOGGER.error("DEBUG validate: title=[" + title + "] capacite=[" + capacite + "] localisation=[" + localisation + "] statut=[" + statut + "]");
            boolean success = isUpdateOperation() ? updateRoom() : createRoom();
            if (!success) {
                setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.rooms.modal.error"), request);
                return false;
            }

            setInfoMsg(glp(isUpdateOperation()
                    ? "jcmsplugin.zaomeetreserve.admin.rooms.modal.update.success.msg"
                    : "jcmsplugin.zaomeetreserve.admin.rooms.modal.add.success.msg"), request);
            return true;
        }
        return super.processAction();
    }

    protected boolean validateAddRoom() {
        if (Util.isEmpty(title) || Util.isEmpty(capacite) || Util.isEmpty(localisation) || Util.isEmpty(statut)) {
            return false;
        }

        try {
            int capaciteInt = Integer.parseInt(capacite.trim());
            if (capaciteInt <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("Capacité invalide : " + capacite, e);
            return false;
        }

        return true;
    }

    public boolean isUpdateOperation() {
        return Util.notEmpty(roomId);
    }

    private boolean createRoom() {
        Room newRoom = new Room();
        applyFieldsTo(newRoom);
        return ZaoMeetReserveManager.getInstance().createRoom(newRoom, loggedMember);
    }

    private boolean updateRoom() {
        Room source = getRoom();
        if (Util.isEmpty(source)) return false;
        Room target = (Room) source.getUpdateInstance();
        applyFieldsTo(target);
        return ZaoMeetReserveManager.getInstance().updateRoom(target, loggedMember);
    }

    private void applyFieldsTo(Room target) {
        target.setTitle(title);
        target.setCapacite(Util.toInt(capacite != null ? capacite.trim() : null, 0));
        target.setLocalisation(localisation);
        target.setEquipements(equipements);
        target.setStatut(toJcmsStatut(Util.notEmpty(statut) ? statut : RoomStatus.AVAILABLE.name()));

        if (Util.notEmpty(photo)) {
            target.setPhoto(photo);
        }
    }

    /** Convertit une valeur (enum ou valueN) vers les valeurs du champ enumerate de Room.xml. */
    private String toJcmsStatut(String value) {
        if (Util.isEmpty(value)) {
            return "value1";
        }

        if ("value1".equals(value) || "value2".equals(value) || "value3".equals(value)) {
            return value;
        }

        switch (RoomStatus.fromRaw(value)) {
            case OCCUPIED:
                return "value2";
            case MAINTENANCE:
                return "value3";
            case AVAILABLE:
            default:
                return "value1";
        }
    }

    @Override
    public String getFormStepHiddenFields() {
        StringBuilder sb = new StringBuilder();
        if (Util.notEmpty(roomId)) sb.append(getHiddenField("roomId", roomId));
        return sb.toString();
    }

    // --- getters / setters ---

    public boolean isOpAddRoom() {
        return opAddRoom;
    }

    public void setOpAddRoom(boolean opAddRoom) {
        this.opAddRoom = opAddRoom;
    }

    

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
        this.room = ZaoMeetReserveManager.getInstance().getRoomById(roomId);
    }

    public String getTitle() {
        return (title == null && !opAddRoom && getRoom() != null) ? getRoom().getTitle() : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCapacite() {
        return (capacite == null && !opAddRoom && getRoom() != null) ? String.valueOf(getRoom().getCapacite()) : capacite;
    }

    public void setCapacite(String capacite) {
        this.capacite = capacite;
    }

    public String getLocalisation() {
        return (localisation == null && !opAddRoom && getRoom() != null) ? getRoom().getLocalisation() : localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getEquipements() {
        return (equipements == null && !opAddRoom && getRoom() != null) ? getRoom().getEquipements() : equipements;
    }

    public void setEquipements(String equipements) {
        this.equipements = equipements;
    }

    public String getPhoto() {
        return (photo == null && !opAddRoom && getRoom() != null) ? getRoom().getPhoto() : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatut() {
        if (Util.notEmpty(statut)) return statut;
        if (!opAddRoom && getRoom() != null) return getRoom().getStatut();
        return "value1";
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    private Room getRoom() {
        if (room == null && Util.notEmpty(roomId)) {
            room = ZaoMeetReserveManager.getInstance().getRoomById(roomId);
        }
        return room;
    }
    
}