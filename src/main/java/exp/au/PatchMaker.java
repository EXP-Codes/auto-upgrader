package exp.au;

import exp.au.ui.server.MakePatchUI;
import exp.libs.warp.ui.BeautyEyeUtils;

/**
 * <PRE>
 * 应用升级包制作器(服务端用).
 * -----------------------------------------
 *   此方法在编译环境下运行即可.
 *   目的是生成升级包到管理目录, 并通过SVN上传到管理页.
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PatchMaker {

	public static void main(String[] args) {
		BeautyEyeUtils.init();
		MakePatchUI.getInstn()._view();
	}
	
}
