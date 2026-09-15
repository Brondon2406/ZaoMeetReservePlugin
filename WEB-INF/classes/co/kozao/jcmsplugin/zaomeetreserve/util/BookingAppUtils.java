package co.kozao.jcmsplugin.zaomeetreserve.util;

import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.jcms.workspace.Workspace;
import com.jalios.util.Util;

public class BookingAppUtils {

    private static final String GROUP_EMPLOYEE            = "Kozao_Employes";
    private static final String GROUP_MANAGER              = "Kozao_Managers";
    private static final String GROUP_DIRECTEUR_TECHNIQUE  = "Kozao_DT";
    
    public static boolean canManage(Member member, Workspace ws) {
        return true;
    }

    public static boolean isModuleAdmin(Member member, Workspace ws) {
        return member != null && member.isAdmin(); 
    }

    private static boolean isInGroup(Member member, String groupName) {
        if (Util.isEmpty(member) || Util.isEmpty(member.getDeclaredGroupSet())) {
            return false;
        }
        for (Group g : member.getDeclaredGroupSet()) {
            if (g.getName().equalsIgnoreCase(groupName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmployee(Member member) {
        return isInGroup(member, GROUP_EMPLOYEE);
    }

    public static boolean isManager(Member member) {
        return isInGroup(member, GROUP_MANAGER);
    }

    public static boolean isDirecteurTechnique(Member member) {
        return isInGroup(member, GROUP_DIRECTEUR_TECHNIQUE);
    }

    public static boolean isManagerOrAdmin(Member member) {
        return isManager(member) || isDirecteurTechnique(member);
    }

    public static boolean isAdmin(Member member) {
        return isDirecteurTechnique(member);
    }
}