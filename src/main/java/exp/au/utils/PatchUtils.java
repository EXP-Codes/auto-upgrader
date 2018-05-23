package exp.au.utils;

import exp.au.envm.Params;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 补丁工具
 * </PRE>
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PatchUtils {

	/** 私有化构造函数 */
	protected PatchUtils() {}
	
	/**
	 * 构造补丁名称
	 * @param appName 应用名称
	 * @param version 补丁版本
	 * @return
	 */
	public static String toPatchName(String appName, String version) {
		return StrUtils.concat(appName, Params.PATCH_TAG, version);
	}
	
}
