package exp.au;

import exp.au.ui.client.UpgradeUI;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;


/**
 * <PRE>
 * 应用升级包安装器(客户端用).
 * -----------------------------------------
 *   此方法需在指定应用的根目录下运行.
 * </PRE>
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PatchInstaller {

	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		BeautyEyeUtils.init();
		UpgradeUI.getInstn()._view();
		// FIXME 检查版本时可以选择是否使用测试服务器
	}
	
}
