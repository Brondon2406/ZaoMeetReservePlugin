package co.kozao.jcmsplugin.zaomeetreserve.handler;


import java.io.IOException;

import com.jalios.jcms.Member;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;

import co.kozao.jcmsplugin.zaomeetreserve.ZaoMeetReserveManager;
import co.kozao.jcmsplugin.zaomeetreserve.hbm.ZaoMeetReserveConfig;


public class SaveConfigHandler extends JcmsFormHandler {

    private boolean opSaveConfig = false;

    private Long openinghour;
    private Long closinghour;
    private int minslotduration;
    private int maxslotduration;
    private int maxbookingdelaydays;
    private int autocancelminutes;
    private boolean recuring;
    private int recurringmaxperweek;

    private ZaoMeetReserveConfig current;

    private ZaoMeetReserveConfig getCurrent() {
        if (current == null) {
            current = ZaoMeetReserveManager.getConfig();
        }
        return current;
    }
    

    @Override
    public boolean processAction() throws IOException {
        if (opSaveConfig) {
            if (!checkAdmin() || !validateFields()) {
                return false;
            }
            return saveConfig();
        }
        return super.processAction();
    }

    private boolean checkAdmin() {
        Member m = getLoggedMember(); // à vérifier dans ta version
        if (m == null || !m.isAdmin()) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.config.error.forbidden"), request);
            return false;
        }
        return true;
    }

    private boolean validateFields() {
        if (openinghour == null || closinghour == null
                || openinghour < 0 || closinghour > 24 || openinghour >= closinghour) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.config.error.hours-required"), request);
            return false;
        }
        if (minslotduration <= 0 || maxslotduration < minslotduration) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.config.error.slot-duration-range"), request);
            return false;
        }
        if (maxbookingdelaydays < 0 || autocancelminutes < 0 || recurringmaxperweek < 0) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.config.error.negative-value"), request);
            return false;
        }
        return true;
    }

    private boolean saveConfig() {
        try {
            ZaoMeetReserveConfig config = getCurrent();
            config.setOpeninghour(openinghour);
            config.setClosinghour(closinghour);
            config.setMinslotduration(minslotduration);
            config.setMaxslotduration(maxslotduration);
            config.setMaxbookingdelaydays(maxbookingdelaydays);
            config.setAutocancelminutes(autocancelminutes);
            config.setRecuring(recuring);
            config.setRecurringmaxperweek(recurringmaxperweek);

            ZaoMeetReserveManager.saveConfig(config);
            setSuccessMsg(glp("jcmsplugin.zaomeetreserve.admin.config.success-saved"), request);
            return true;
        } catch (Exception e) {
            setWarningMsg(glp("jcmsplugin.zaomeetreserve.admin.config.error.save-failed"), request);
            return false;
        }
    }

    // --- getters (pre-remplissage depuis la config existante) / setters ---

    public Long getOpeninghour() {
        if (Util.isEmpty(openinghour)) {
            ZaoMeetReserveManager.getInstance();
			openinghour = ZaoMeetReserveManager.getConfig().getOpeninghour();
        }
        return openinghour;
    }

    public void setOpeninghour(Long openinghour) {
        this.openinghour = openinghour;
    }

    public Long getClosinghour() {
        if (Util.isEmpty(closinghour)) {
            ZaoMeetReserveManager.getInstance();
			closinghour = ZaoMeetReserveManager.getConfig().getClosinghour();
        }
        return closinghour;
    }

    public void setClosinghour(Long closinghour) {
        this.closinghour = closinghour;
    }

    public int getMinslotduration() {
        ZaoMeetReserveManager.getInstance();
		return minslotduration == 0 ? ZaoMeetReserveManager.getConfig().getMinslotduration() : minslotduration;
    }

    public void setMinslotduration(int minslotduration) {
        this.minslotduration = minslotduration;
    }

    public int getMaxslotduration() {
        ZaoMeetReserveManager.getInstance();
		return maxslotduration == 0 ? ZaoMeetReserveManager.getConfig().getMaxslotduration() : maxslotduration;
    }

    public void setMaxslotduration(int maxslotduration) {
        this.maxslotduration = maxslotduration;
    }

    public int getMaxbookingdelaydays() {
        return maxbookingdelaydays;
    }

    public void setMaxbookingdelaydays(int maxbookingdelaydays) {
        this.maxbookingdelaydays = maxbookingdelaydays;
    }

    public int getAutocancelminutes() {
        return autocancelminutes;
    }

    public void setAutocancelminutes(int autocancelminutes) {
        this.autocancelminutes = autocancelminutes;
    }

    public boolean isRecuring() {
        return recuring;
    }

    public void setRecuring(boolean recuring) {
        this.recuring = recuring;
    }

    public int getRecurringmaxperweek() {
        return recurringmaxperweek;
    }

    public void setRecurringmaxperweek(int recurringmaxperweek) {
        this.recurringmaxperweek = recurringmaxperweek;
    }

    public boolean isOpSaveConfig() {
        return opSaveConfig;
    }

    public void setOpSaveConfig(boolean opSaveConfig) {
        this.opSaveConfig = opSaveConfig;
    }
}