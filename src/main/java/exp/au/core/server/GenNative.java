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
	
	/**
	 * 
	 * 6. 使用API生成 zip.txt、 MD5, 然后把3个文件放到 patches-for-page/${project}/${version} 目录下
	 * @param zipPatchPath 补丁包所在路径
	 * @return 补丁包所在目录
	 */
	public static boolean toTxtAndMD5(String zipPatchPath) {
		boolean isOk = false;
		List<String> groups = RegexUtils.findGroups(zipPatchPath, REGEX);
		if(groups.size() == 4) {
			String zipName = groups.get(1);
			String appName = groups.get(2);
			String version = groups.get(3);
			String dir = StrUtils.concat(Config.PATCH_PAGE_DIR, appName, "/", version, "/");
			String path = dir.concat(zipName);
			isOk = FileUtils.copyFile(zipPatchPath, path);
			
			String txtPath = TXTUtils.toTXT(path);
			String MD5 = CryptoUtils.toFileMD5(path);
			String MD5Path = PathUtils.combine(PathUtils.getParentDir(txtPath), Params.MD5_HTML);
			isOk &= FileUtils.write(MD5Path, MD5, Charset.ISO, false);
		}
		return isOk;
	}
	
}
