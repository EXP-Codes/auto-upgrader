package exp.au;

import exp.au.ui.client.UpgradeUI;
import exp.libs.utils.os.OSUtils;
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
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class PatchInstaller {

	public static void main(String[] args) {
		if(OSUtils.isWin()) {
			BeautyEyeUtils.init();
			UpgradeUI.getInstn()._view();
			
		} else {
			// TODO unix版本
		}
	}
	
}
