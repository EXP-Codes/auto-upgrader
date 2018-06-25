package exp.au.core.client;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import exp.au.bean.PatchInfo;
import exp.au.ui.client.UpgradeUI;
import exp.au.utils.PatchUtils;
import exp.au.utils.UIUtils;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 根据应用信息下载对应的升级补丁列表
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DownPatch {
	
	/** 私有化构造函�? */
	protected DownPatch() {}
	
	/**
	 * 提取指定应用的补丁列表信�?
	 * @param APP_NAME 应用名称
	 * @return 升级补丁列表信息
	 */
	public static List<PatchInfo> getPatchInfos(final String APP_NAME) {
		final String SERVER_URL = UpgradeUI.getInstn().getServerURL();
		String pageSource = HttpURLUtils.doGet(SERVER_URL);
		return getPatchInfos(APP_NAME, pageSource);
	}
	
	/**
	 * 从页面提取指定应用的补丁列表信息
	 * @param appName 应用名称
	 * @param pageSource 页面源码
	 * @return 升级补丁列表信息
	 */
	@SuppressWarnings("unchecked")
	private static List<PatchInfo> getPatchInfos(String appName, String pageSource) {
		List<PatchInfo> patchInfos = new LinkedList<PatchInfo>();
		try {
			Document doc = DocumentHelper.parseText(pageSource);
			Element html = doc.getRootElement();
			Element body = html.element("body");
			Element div = body.element("div");
			Iterator<Element> divs = div.elementIterator("div");
			while(divs.hasNext()) {
				Element table = divs.next().element("table");
				String name = table.attributeValue("id");
				if(appName.equals(name)) {
					patchInfos = toPatchInfos(table);
					break;
				}
			}
		} catch (Exception e) {
			UIUtils.toConsole("连接到版本管理服务器失败");
		}
		return patchInfos;
	}
	
	/**
	 * 把页面表单元素转换为升级补丁列表对象
	 * @param table 页面表单元素
	 * @return 升级补丁列表对象
	 */
	@SuppressWarnings("unchecked")
	private static List<PatchInfo> toPatchInfos(Element table) {
		List<PatchInfo> patchInfos = new LinkedList<PatchInfo>();
		final String SERVER_URL = UpgradeUI.getInstn().getServerURL();
		final String REGEX = "\\[([^\\]]+)\\] VERSIONS: (.*)";
		String appName = "";
		
		Element tbody = table.element("tbody");
		Iterator<Element> trs = tbody.elementIterator();
		while(trs.hasNext()) {
			Element tr = trs.next();
			List<Element> tds = tr.elements();
			String key = tds.get(0).getTextTrim();
			
			if("SOFTWARE-NAME".equals(key)) {
				appName = tds.get(1).getTextTrim();
				
			} else {
				List<String> groups = RegexUtils.findGroups(key, REGEX);
				String releaseTime = groups.get(1);
				String version = groups.get(2);
				String patchName = PatchUtils.toPatchName(appName, version);
				
				List<String> brackets = RegexUtils.findBrackets(tds.get(1).asXML(), "href=\"([^\"]+)\"");
				String zipURL = combineURL(SERVER_URL, brackets.get(0));
				String txtURL = combineURL(SERVER_URL, brackets.get(1));
				String md5URL = combineURL(SERVER_URL, brackets.get(2));
				
				PatchInfo patchInfo = new PatchInfo();
				patchInfo.setAppName(appName);
				patchInfo.setVersion(version);
				patchInfo.setPatchName(patchName);
				patchInfo.setReleaseTime(releaseTime);
				patchInfo.setMD5(HttpURLUtils.doGet(md5URL));
				patchInfo.setZipURL(zipURL);
				patchInfo.setTxtURL(txtURL);
				patchInfos.add(patchInfo);
			}
		}
		
		Collections.sort(patchInfos);	// 确保升级补丁列表按版本号排序
		return patchInfos;
	}
	
	/**
	 * 合并URL路径
	 * @param prefix 前缀路径�? �?: http://lyy289065406.gitee.io/auto-upgrader/
	 * @param suffix 后缀路径�? �?: ./foo/bar.suffix
	 * @return
	 */
	private static String combineURL(String prefix, String suffix) {
		return prefix.concat(suffix).replace('\\', '/').replace("/./", "/");
	}
	
	/**
	 * 下载单个补丁
	 * @param patchInfo 补丁信息
	 * @return 是否下载成功
	 */
	public static boolean download(PatchInfo patchInfo) {
		String saveDir = patchInfo.getPatchDir();
		String zipPath = PathUtils.combine(saveDir, patchInfo.getZipName());
		
		boolean isOk = true;
		if(FileUtils.exists(zipPath)) {
			// Undo 若已存在则不再重复下�?
			
		} else {
			final String MD5 = patchInfo.getMD5();
			FileUtils.createDir(saveDir);
			
			// 下载zip版本升级�?
			isOk = downZipPatch(patchInfo.getZipURL(), zipPath, MD5);
			
			// 若zip版本升级包下载失�?, 则下载txt版本升级�?
			if(isOk == false) {
				FileUtils.delete(zipPath);
				String txtPath = PathUtils.combine(saveDir, patchInfo.getTxtName());
				isOk = downTxtPatch(patchInfo.getTxtURL(), txtPath, zipPath, MD5);
			}
		}
		
		// 若下载补丁失�?, 则删除该补丁所在的目录
		if(isOk == false) {
			FileUtils.delete(saveDir);
		}
		return isOk;
	}
	
	/**
	 * 下载zip格式的补丁包, 并校验MD5
	 * @param zipURL zip补丁包的下载路径
	 * @param zipPath zip补丁包的保存位置
	 * @param patchMD5 补丁包校验码
	 * @return
	 */
	private static boolean downZipPatch(String zipURL, String zipPath, String patchMD5) {
		boolean isOk = HttpURLUtils.downloadByGet(zipPath, zipURL);
		if(isOk == true) {
			String MD5 = CryptoUtils.toFileMD5(zipPath);
			isOk = patchMD5.equalsIgnoreCase(MD5);
		}
		return isOk;
	}
	
	/**
	 * 下载txt格式的补丁包, 将其转换为zip格式, 并校验MD5
	 * @param txtURL txt补丁包的下载路径
	 * @param txtPath txt补丁包的保存位置
	 * @param zipPath 转存zip补丁包的位置
	 * @param patchMD5 补丁包校验码
	 * @return
	 */
	private static boolean downTxtPatch(String txtURL, String txtPath, 
			String zipPath, String patchMD5) {
		boolean isOk = HttpURLUtils.downloadByGet(txtPath, txtURL);
		if(isOk == true) {
			isOk = TXTUtils.toFile(txtPath, zipPath);
			if(isOk == true) {
				String MD5 = CryptoUtils.toFileMD5(zipPath);
				isOk = patchMD5.equalsIgnoreCase(MD5);
			}
		}
		return isOk;
	}
	
}
