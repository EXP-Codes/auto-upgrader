package exp.au.api;

import java.io.File;

import exp.au.Config;
import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 导出应用的当前版本信息(接口端用).
 * -----------------------------------------
 *  此方法需植入到应用程序的 main 方法中, 使得每次启动时可以生成当前版本信息.
 *  客户端则根据当前版本信息对应用程序进行升级 (若没有当前版本信息则不执行升级操作)
 *  
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ExportVerInfo {

	/**
	 * 从版本库导出应用程序的当前版本信息.
	 * @return 是否导出成功
	 */
	public static boolean exec() {
		return exec(null);
	}
	
	/**
	 * <pre>
	 * 从版本库导出应用程序的当前版本信息.
	 * -------------------------------------------
	 *  由于版本库中的应用名称并没有做硬性格式要求, 
	 *  同样在制作升级包时也没有对应用名称并没有做硬性格式要求, 
	 *  因此此处允许覆写应用名称, 使之与制作升级包时填写的应用名称一致即可.
	 * </pre>
	 * @param appName 强制指定应用名称
	 * @return 是否导出成功
	 */
	public static boolean exec(String appName) {
		String verInfo = VersionMgr.getVersionInfo(true, false);
		if(StrUtils.isEmpty(appName)) {
			appName = RegexUtils.findFirst(verInfo, "项目名称[ |]*([a-z|\\-]+)");
		}
		String version = RegexUtils.findFirst(verInfo, "版本号[ |]*([\\d|\\.]+)");
		
		File verFile = FileUtils.createFile(Config.LAST_VER_PATH);
		String data = StrUtils.concat(appName, Delimiter.CRLF, version);
		return FileUtils.write(verFile, data, Charset.UTF8, false);
	}
	
}
