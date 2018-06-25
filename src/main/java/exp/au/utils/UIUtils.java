package exp.au.utils;

import javax.swing.JLabel;

import exp.au.ui.client.UpgradeUI;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * UI工具
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class UIUtils {

	/** 私有化构造函�? */
	protected UIUtils() {}
	
	/**
	 * 打印信息到升级界面的控制�?
	 * @param msgs
	 */
	public static void toConsole(Object... msgs) {
		UpgradeUI.getInstn().toConsole(msgs);
	}
	
	/**
	 * 创建占位用的Label
	 * @param spaceNum 占位空格个数
	 * @return
	 */
	public static JLabel newLabel(int spaceNum) {
		return new JLabel(StrUtils.multiChar(' ', spaceNum));
	}
	
}
