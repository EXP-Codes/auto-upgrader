package exp.au.utils;

import exp.au.envm.Params;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 补丁工具
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PatchUtils {

	/** 私有化构造函�? */
	protected PatchUtils() {}
	
	/**
	 * 构造补丁名�?
	 * @param appName 应用名称
	 * @param version 补丁版本
	 * @return
	 */
	public static String toPatchName(String appName, String version) {
		return StrUtils.concat(appName, Params.PATCH_TAG, version);
	}
	
	/**
	 * 执行补丁相关操作步骤之间的休�?
	 *  (目的是可以在界面看到提示效果)
	 */
	public static void patchSleep() {
		ThreadUtils.tSleep(200);
	}
	
}
