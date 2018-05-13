package exp.au.core.server;

import java.util.List;

import exp.au.Config;
import exp.au.envm.Params;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

// 服务端步骤1 (研发执行)： 通过ui界面配置升级步骤文件 update.xml
public class GenNative {

	private final static String REGEX = "(([^/\\\\]*?)-patch-(\\d\\.\\d).zip)";	// 补丁有严格的格式要求
	
	/*
	 * 1.选择升级压缩包(zip 或 目录)
	 * 2.设置项目名称
	 * 3.设置升级包升级到的版本号
	 * 4.设置升级步骤:
	 *   add : 从升级包 [相对位置] 取一个文件添加到 目标应用 [相对位置]
	 *   del : 在目标应用 [相对位置] 删除一个文件
	 *   mov : 在目标应用 [相对位置] 移动一个文件到 目标应用 [相对位置]
	 *   
	 * 5.根据升级步骤构造升级文件 update.xml, 合并到压缩包/目录（压缩包内外均保留update.xml，外面的用于编辑并同步到内部，内部的用于下载后使用），
	 *    生成标准形式的升级包名字[升级包命名格式：  appName-patch-ver.zip  (要求内含update.xml)]
	 * 6.生成升级包对应的 txt 和 MD5, 放到 patches-for-page 目录
	 */
	
	public static void main(String[] args) {
		testToPatch();
	}
	
	private static void testToPatch() {
		
		String patchDir = toTxtAndMD5("./foo/bar/xxx-patch-a.b.zip");
		toUpdateXml(patchDir);
	}
	
	/**
	 * 
	 * 6. 使用API生成 zip.txt、 MD5, 然后把3个文件放到 patches-for-page/${project}/${version} 目录下
	 * @param zipPatchPath 补丁包所在路径
	 * @return 补丁包所在目录
	 */
	private static String toTxtAndMD5(String zipPatchPath) {
		String dir = "";
		List<String> groups = RegexUtils.findGroups(zipPatchPath, REGEX);
		if(groups.size() == 4) {
			String zipName = groups.get(1);
			String appName = groups.get(2);
			String version = groups.get(3);
			dir = StrUtils.concat(Config.PATCH_PAGE_DIR, appName, "/", version, "/");
			String path = dir.concat(zipName);
			FileUtils.copyFile(zipPatchPath, path);
			
			String txtPath = TXTUtils.toTXT(path);
			String MD5 = CryptoUtils.toFileMD5(path);
			String MD5Path = PathUtils.combine(PathUtils.getParentDir(txtPath), Params.MD5_HTML);
			FileUtils.write(MD5Path, MD5, Charset.ISO, false);
		}
		return dir;
	}
	
	private static boolean toUpdateXml(String dir) {
		// 把update.xml放到补丁目录
		return true;
	}
	
	/**
	 * 获取 [add] 新增命令: 把 [新文件] from [补丁包相对位置] to [应用程序相对位置]
	 * @param fromPath 补丁包相对位置
	 * @param toPath 应用程序相对位置
	 * @return xml格式的add命令
	 */
	private static String getAddCmd(String fromPath, String toPath) {
		return StrUtils.concat("<add caption=\"新增命令\" from=\"", 
				fromPath, "\" to=\"", toPath, "\" />");
	}
	
	/**
	 * 获取 [mov] 移动命令 : 把 [原文件] from [应用程序相对位置(旧)] to [应用程序相对位置(新)]
	 * @param fromPath 应用程序相对位置(旧)
	 * @param toPath 应用程序相对位置(新)
	 * @return xml格式的mov命令
	 */
	private static String getMovCmd(String fromPath, String toPath) {
		return StrUtils.concat("<mov caption=\"移动命令\" from=\"", 
				fromPath, "\" to=\"", toPath, "\" />");
	}
	
	/**
	 * 获取 [del] 移动命令 : 把 [原文件] from [应用程序相对位置] 删除
	 * @param fromPath 应用程序相对位置
	 * @return xml格式的del命令
	 */
	private static String getDelCmd(String fromPath) {
		return StrUtils.concat("<del caption=\"删除命令\" from=\"", 
				fromPath, "\" />");
	}
	
}
