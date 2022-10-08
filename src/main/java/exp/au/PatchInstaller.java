package exp.au;

import exp.au.ui.client.UpgradeUI;
import exp.libs.ui.BeautyEyeUtils;
import exp.libs.utils.os.OSUtils;


/**
 * <PRE>
 * 应用升级包安装器(客户端用).
 * -----------------------------------------
 *   此方法需在指定应用的根目录下运行.
 * </PRE>
 * <br/><B>PROJECT : </B> auto-upgrader
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
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
