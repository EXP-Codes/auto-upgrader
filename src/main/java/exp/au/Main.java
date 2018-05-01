package exp.au;

import exp.libs.utils.other.LogUtils;
import exp.libs.warp.net.http.HttpURLUtils;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-04-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	// 在线升级， 升级命令：移动文件，删除文件，添加文件,  压缩包转txt
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		// TODO: 程序入口
		
		boolean isOk = HttpURLUtils.downloadByGet(
				"./log/b.zip", "http://lyy289065406.gitee.io/auto-upgrader/packages/a.zip", null, null);
		System.out.println(isOk);
	}
	
}
