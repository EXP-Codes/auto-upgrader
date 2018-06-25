package exp.au.api;

import java.io.File;
import java.util.List;

import exp.au.Config;
import exp.au.bean.PatchInfo;
import exp.au.bean.Version;
import exp.au.core.client.DownPatch;
import exp.au.envm.Params;
import exp.libs.envm.Delimiter;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.JarUtils;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 导出应用的 [当前版本信息] 以及 [软件升级入口的启动文件].
 * -----------------------------------------
 *  此方法需植入到被升级的应用程序的 main 方法中, 使得该应用程序每次启动时可以生成当前版本信息.
 *  客户端则根据所生成的当前版本信息对该应用程序进行升级 (若没有当前版本信息则不执行升级操作)
 *  
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AppVerInfo {
	
	/** 启动文件的包目录位置 */
	private final static String PACKAGE_PATH = "/exp/au/start/";
	
	/** 复制启动文件的目标位�? */
	private final static String TARGET_DIR = PathUtils.getProjectRootPath();
	
	/** exe启动文件名称 */
	private final static String EXE_FILE_NAME = "软件升级.exe";
	
	/** bat启动文件位置 */
	private final static String BAT_FILE_NAME = "update.bat";
	
	/** sh启动文件位置 */
	private final static String SH_FILE_NAME = "update.sh";
	
	/** 私有化构造函�? */
	protected AppVerInfo() {}
	
	/**
	 * 检查是否存在更新的版本
	 * @return 若不存在新版本则返回null; 否则返回最新的一个版本号
	 */
	public static Version existNewVersion() {
		return existNewVersion(null);
	}
	
	/**
	 * <pre>
	 * 检查是否存在更新的版本
	 * -------------------------------------------
	 *  由于版本库中的应用名称并没有做硬性格式要�?, 
	 *  同样在制作升级包时也没有对应用名称并没有做硬性格式要�?, 
	 *  因此此处允许覆写应用名称, 使之与制作升级包时填写的应用名称一致即�?.
	 * </pre>
	 * @param appName 强制指定应用名称
	 * @return 若不存在新版本则返回null; 否则返回最新的一个版本号
	 */
	public static Version existNewVersion(String appName) {
		if(StrUtils.isEmpty(appName)) {
			appName = VersionMgr.getAppName();
		}
		
		final Version CUR_VER = new Version(VersionMgr.getVersion());
		Version newVer = null;
		
		List<PatchInfo> patches = DownPatch.getPatchInfos(appName);
		for(PatchInfo patch : patches) {
			if(CUR_VER.compareTo(patch.getVersion()) < 0) {
				
				if(newVer == null) {
					newVer = patch.getVersion();
					
				} else if(newVer.compareTo(patch.getVersion()) < 0) {
					newVer = patch.getVersion();
				}
			}
		}
		return newVer;
	}
	
	/**
	 * 从版本库导出应用程序�? [当前版本信息] 以及 [软件升级入口的启动文件].
	 * @return 是否导出成功
	 */
	public static boolean export() {
		return export(null, null);
	}
	
	/**
	 * <pre>
	 * 从版本库导出应用程序�? [当前版本信息] 以及 [软件升级入口的启动文件].
	 * -------------------------------------------
	 *  由于版本库中的应用名称并没有做硬性格式要�?, 
	 *  同样在制作升级包时也没有对应用名称并没有做硬性格式要�?, 
	 *  因此此处允许覆写应用名称, 使之与制作升级包时填写的应用名称一致即�?.
	 * </pre>
	 * @param appName 强制指定应用名称
	 * @return 是否导出成功
	 */
	public static boolean export(String appName) {
		return export(appName, null);
	}
	
	/**
	 * <pre>
	 * 从版本库导出应用程序�? [当前版本信息] 以及 [软件升级入口的启动文件].
	 * -------------------------------------------
	 *   不推荐使用此方法, 版本号最好还是由程序自动生成
	 * </pre>
	 * @param appName 强制指定应用名称
	 * @param version 强制指定应用的当前版�?
	 * @return 是否导出成功
	 */
	public static boolean export(String appName, String version) {
		boolean isOk = false;
		if(isRunByScript()) {
			exportStartFile();	// 该文件可能在使用�?, 无需判断是否导出成功
			
			isOk = exportAppVersion(appName, version);
			if(isOk == false) {
				System.err.println("[auto-upgrader] : 导出版本信息失败");
			}
		} else {
			System.err.println("[auto-upgrader] : 编译环境下禁止导出版本信�?");
		}
		return isOk;
	}
	
	/**
	 * <PRE>
	 * 检查当前程序是否通过 dox/unix 启动脚本启动�?
	 * 原理是检测lib目录下是否存�? auto-upgrader-*.jar 文件 (在Eclipse中运行时lib文件夹不会存在项目jar�?)
	 * </PRE>
	 * @return true:通过启动脚本启动; false:通过Eclipse等编译软件启�?
	 */
	private static boolean isRunByScript() {
		boolean isRunByScript = false;
		File libDir = new File(Params.LIB_DIR);
		File[] jars = libDir.listFiles();
		if(jars == null) {
			return isRunByScript;
		}
		
		for(File jar : jars) {
			if(jar.isDirectory()) {
				continue;
			}
			
			if(jar.getName().startsWith(Config.APP_NAME) && 
					jar.getName().endsWith(Params.JAR_SUFFIX)) {
				isRunByScript = true;
				
				// 复制一个无版本号的 auto-upgrader.jar 供软件升级的启动脚本使用 
				// (启动脚本的依赖中没有带版本号, 并不是所有引入自动升级的应用都会主动在pom的配置中去掉其版本号)
				String copyPath = PathUtils.combine(
						jar.getParentFile().getAbsolutePath(), 
						StrUtils.concat(Config.APP_NAME, Params.JAR_SUFFIX));
				FileUtils.copyFile(jar.getAbsolutePath(), copyPath);
				break;
			}
		}
		return isRunByScript;
	}
	
	/**
	 * <pre>
	 * 从版本库导出应用程序�? [当前版本信息].
	 * -------------------------------------------
	 *   不推荐使用此方法, 版本号最好还是由程序自动生成
	 * </pre>
	 * @param appName 强制指定应用名称
	 * @param version 强制指定应用的当前版�?
	 * @return 是否导出成功
	 */
	private static boolean exportAppVersion(String appName, String version) {
		if(StrUtils.isEmpty(appName)) {
			appName = VersionMgr.getAppName();
		}
		
		if(StrUtils.isEmpty(version)) {
			version = VersionMgr.getVersion();
		}
		
		File verFile = FileUtils.createFile(Config.LAST_VER_PATH);
		String data = StrUtils.concat(appName, Delimiter.CRLF, version);
		return FileUtils.write(verFile, data, Config.DEFAULT_CHARSET, false);
	}
	
	/**
	 * FIXME 编译环境下不生成
	 * 从Jar包导�? [软件升级入口的启动文件]
	 * @return 是否导出成功
	 */
	private static boolean exportStartFile() {
		boolean isOk = true;
		if(OSUtils.isWin()) {
			isOk &= JarUtils.copyFile(
					PACKAGE_PATH.concat(EXE_FILE_NAME), 
					TARGET_DIR.concat(EXE_FILE_NAME));
//			isOk &= JarUtils.copyFile(
//					PACKAGE_PATH.concat(BAT_FILE_NAME), 
//					TARGET_DIR.concat(BAT_FILE_NAME));
			
		} else {
			// TODO 暂不支持linux版本
//			isOk &= JarUtils.copyFile(
//					PACKAGE_PATH.concat(SH_FILE_NAME), 
//					TARGET_DIR.concat(SH_FILE_NAME));
		}
		return isOk;
	}
	
}
