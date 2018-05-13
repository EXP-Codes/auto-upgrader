package exp.au.core.server;

import java.util.List;

import exp.au.Config;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

// 服务端步骤1 (研发执行)： 通过ui界面配置升级步骤文件 update.xml
public class GenNative {

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
	
	
	
	/**
	 * 
	 * 6. 使用API生成 zip.txt、 MD5, 然后把3个文件放到 patches-for-page/${project}/${version} 目录下
	 * @param zipPatchPath
	 */
	public static void toTxtAndMD5(String zipPatchPath) {
		final String REGEX = "(([^/\\\\]*?)-patch-(\\d\\.\\d).zip)";	// 补丁有严格的格式要求
		List<String> groups = RegexUtils.findGroups(zipPatchPath, REGEX);
		if(groups.size() == 4) {
			String zipName = groups.get(1);
			String appName = groups.get(2);
			String version = groups.get(3);
			String dir = StrUtils.concat(Config.PATCH_PAGE_DIR, appName, "/", version, "/");
			String path = dir.concat(zipName);
			FileUtils.copyFile(zipPatchPath, path);
			
			String txtPath = TXTUtils.toTXT(path);
			String MD5 = CryptoUtils.toFileMD5(path);
			String MD5Path = PathUtils.combine(PathUtils.getParentDir(txtPath), "MD5.html");
			FileUtils.write(MD5Path, MD5, Charset.ISO, false);
		} else {
			System.out.println("not match");
		}
	}
}
