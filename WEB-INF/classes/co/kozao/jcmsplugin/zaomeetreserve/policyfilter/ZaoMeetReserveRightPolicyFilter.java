package co.kozao.jcmsplugin.zaomeetreserve.policyfilter;

import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.jcms.policy.PolicyFilter;
import com.jalios.jcms.workspace.Workspace;

import generated.Reservation;
import generated.Room;

public class ZaoMeetReserveRightPolicyFilter extends BasicPolicyFilter {

    public boolean isPolicyFilterApplicable(Class<?> clazz) {
        return Room.class.isAssignableFrom(clazz)
            || Reservation.class.isAssignableFrom(clazz);
    }

    
    public Boolean checkCreate(Class<?> clazz, Member mbr, Workspace ws) {
        return mbr != null;   // ou : mbr != null && mbr.isAdmin() pour les salles
    }

    
    public Boolean checkUpdate(Data data, Member mbr) { return mbr != null; }

    
    public Boolean checkDelete(Data data, Member mbr) { return mbr != null; }
}