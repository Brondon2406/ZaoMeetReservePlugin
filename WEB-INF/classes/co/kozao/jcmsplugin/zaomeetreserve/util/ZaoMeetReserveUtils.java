package co.kozao.jcmsplugin.zaomeetreserve.util;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

/**
 * @author Arlette
 * @author brondon FOPA TIWA ( Reviewer )
 */
public class ZaoMeetReserveUtils {

	private static final Logger LOG = Logger.getLogger(ZaoMeetReserveUtils.class);
	private static Channel channel = Channel.getChannel();

	public static Group getEmployeeGroup() {
		return channel.getGroup(channel.getProperty(ZaoMeetReserveConstants.EMPLOYEE_GROUP));
	}

	public static Group getManagerGroup() {
		return channel.getGroup(channel.getProperty(ZaoMeetReserveConstants.MANAGER_GROUP));
	}

	public static Group getTechnicalDirectorGroup() {
		return channel.getGroup(channel.getProperty(ZaoMeetReserveConstants.TECHNICAL_DIRECTOR_GROUP));
	}

	public static boolean isModuleAdmin(Member member) {
		return Util.notEmpty(member) ? isDirecteurTechnique(member) || member.isAdmin() : false;
	}

	public static boolean isEmployee(Member member) {
		Group group = getEmployeeGroup();
		if (Util.isEmpty(group) || Util.isEmpty(member))
			return false;
		return member.belongsToGroup(group);
	}

	public static boolean isManager(Member member) {
		Group group = getManagerGroup();
		if (Util.isEmpty(group) || Util.isEmpty(member))
			return false;
		return member.belongsToGroup(group);
	}

	public static boolean isDirecteurTechnique(Member member) {
		Group group = getTechnicalDirectorGroup();
		if (Util.isEmpty(group) || Util.isEmpty(member))
			return false;
		return member.belongsToGroup(group);
	}
}