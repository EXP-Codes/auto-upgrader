package exp.au.api;

import java.util.List;

import exp.au.Config;
import exp.au.bean.ldm.PatchInfo;
import exp.au.bean.ldm.UpdateInfo;
import exp.au.bean.ldm.UpdateVersion;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class API {
	
	
	public static void main(String[] args) {
		String response = HttpURLUtils.doGet(URL, null, null);
		System.out.println(response);
		
		
	}
	
	private final static String URL = "http://lyy289065406.gitee.io/auto-upgrader/";
	
	/*
	 * 1.先检查是否存在新版本
	 * 2.检查版本跨度
	 * 3.下载升级包
	 * 4.检查MD5
	 * 5.根据升级指导文件升级（删除、增加、替换文件）
	 */
	protected API() {}
	
	public static List<PatchInfo> getPatchInfos(String appName) {
		return null;
	}
	
	
	/**
	 * 获取升级信息列表
	 * @return
	 */
	public static UpdateInfo getUpdateInfo() {
		return null;
	}
	
	/**
	 * 检查是否存在新版本
	 * @return
	 */
	public static boolean hasNewVersion() {
		// TODO
		return true;
	}
	
	public static List<UpdateVersion> getUpdateVersions() {
		// 获取版本跨度
		return null;
	}
	
	/**
	 * 根据版本跨度下载升级包
	 * @return
	 */
	public static boolean download() {
		
		return true;
	}
	
	/**
	 * 下载TXT形式的升级包
	 * @return
	 */
	public static String downloadTXT() {
		return "";
	}
	
	/**
	 * toTxtAndMD5("./log/bilibili-plugin-patch-4.2.zip");
	 * 
	 * 2. 使用API生成 zip.txt、 MD5, 然后把3个文件放到 patches/${project}/${version} 目录下
	 * @param zipPatchPath
	 */
	public static void toTxtAndMD5(String zipPatchPath) {
		final String REGEX = "(([^/\\\\]*?)-patch-(\\d\\.\\d).zip)";	// 补丁有严格的格式要求
		List<String> groups = RegexUtils.findGroups(zipPatchPath, REGEX);
		if(groups.size() == 4) {
			String zipName = groups.get(1);
			String appName = groups.get(2);
			String version = groups.get(3);
			String dir = StrUtils.concat(Config.PATCH_DIR, appName, "/", version, "/");
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
