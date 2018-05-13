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

// 服务端步骤2： 通过升级包生成相关文件，放到 patches-for-page 下的相关目录
// 升级包命名格式：  appName-patch-ver.zip  (要求内含update.xml)
public class GenZip {

	/**
	 * FIXME
	 * toTxtAndMD5("./log/bilibili-plugin-patch-4.2.zip");
	 * 
	 * 2. 使用API生成 zip.txt、 MD5, 然后把3个文件放到 patches-for-page/${project}/${version} 目录下
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
