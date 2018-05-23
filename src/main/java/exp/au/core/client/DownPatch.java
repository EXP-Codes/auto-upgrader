package exp.au.core.client;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.au.Config;
import exp.au.bean.PatchInfo;
import exp.au.bean.UpdateCmd;
import exp.au.bean.Version;
import exp.au.envm.CmdType;
import exp.au.envm.Params;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.format.XmlUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 根据应用程序最后的版本信息下载升级补丁
 * </PRE>
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DownPatch {
	
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(DownPatch.class);
	
	/** 版本管理页面 */
	private final static String VER_MGR_URL = Config.getInstn().VER_MGR_URL();
	
	/** 私有化构造函数 */
	protected DownPatch() {}
	
	public static void download() {
		
		// 提取应用程序最后的版本信息
		List<String> lines = FileUtils.readLines(
				Config.LAST_VER_PATH, Config.DEFAULT_CHARSET);
		if(lines.size() == 2) {
			return;
		}
		
		final String APP_NAME = lines.get(0).trim();
		final Version LAST_VER = new Version(lines.get(1).trim());
		
		
		// 获取指定应用的升级补丁列表
		String pageSource = HttpURLUtils.doGet(VER_MGR_URL);
		List<PatchInfo> patchInfos = getPatchInfos(pageSource, APP_NAME);
		
		// 根据当前版本号筛选升级列表
		Iterator<PatchInfo> patchInfoIts = patchInfos.iterator();
		while(patchInfoIts.hasNext()) {
			PatchInfo patchInfo = patchInfoIts.next();
			if(LAST_VER.compareTo(patchInfo.getVersion()) >= 0) {
				patchInfoIts.remove();
			}
		}

		// 下载升级补丁
		boolean isOk = download(patchInfos);
		System.out.println("下载全部补丁:" + isOk);
		for(PatchInfo patchInfo : patchInfos) {
			System.out.println(patchInfo);
			System.out.println("=======");
		}
	}
	
	/**
	 * 提取指定应用的补丁列表信息
	 * @param appName
	 * @return
	 */
	public static List<PatchInfo> getPatchInfos(String appName) {
		String pageSource = HttpURLUtils.doGet(VER_MGR_URL);
		return getPatchInfos(pageSource, appName);
	}
	
	/**
	 * 从页面提取应用补丁列表信息
	 * @param pageSource 页面源码
	 * @param appName 应用名称
	 * @return 补丁列表信息
	 */
	@SuppressWarnings("unchecked")
	private static List<PatchInfo> getPatchInfos(String pageSource, String appName) {
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
			log.error("从页面提取应用 [{}] 的补丁列表信息失败:\r\n{}", appName, pageSource, e);
		}
		return patchInfos;
	}
	
	@SuppressWarnings("unchecked")
	private static List<PatchInfo> toPatchInfos(Element table) {
		List<PatchInfo> patchInfos = new LinkedList<PatchInfo>();
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
				String time = groups.get(1);
				String version = groups.get(2);
				
				List<String> brackets = RegexUtils.findBrackets(tds.get(1).asXML(), "href=\"([^\"]+)\"");
				String zipURL = combineURL(VER_MGR_URL, brackets.get(0));
				String txtURL = combineURL(VER_MGR_URL, brackets.get(1));
				String md5URL = combineURL(VER_MGR_URL, brackets.get(2));
				
				PatchInfo patchInfo = new PatchInfo();
				patchInfo.setAppName(appName);
				patchInfo.setVersion(version);
				patchInfo.setTime(time);
				patchInfo.setMD5(HttpURLUtils.doGet(md5URL));
				patchInfo.setZipURL(zipURL);
				patchInfo.setTxtURL(txtURL);
				patchInfo.setPatchName(toPatchName(appName, version));
				
				patchInfos.add(patchInfo);
			}
		}
		
		Collections.sort(patchInfos);
		return patchInfos;
	}
	
	/**
	 * FIXME  HTTP合并
	 * @param prefix http://lyy289065406.gitee.io/auto-upgrader/
	 * @param suffix ./foo/bar.suffix
	 * @return
	 */
	private static String combineURL(String prefix, String suffix) {
		return prefix.concat(suffix).replace('\\', '/').replace("/./", "/");
	}
	
	private static String toPatchName(String appName, String version) {
		return StrUtils.concat(appName, Params.PATCH_TAG, version, Params.ZIP_SUFFIX);
	}
	
	private static boolean download(List<PatchInfo> patchInfos) {
		int cnt = 0;
		for(PatchInfo patchInfo : patchInfos) {
			cnt += download(patchInfo) ? 1 : 0;
		}
		return (cnt == patchInfos.size());
	}
	
	public static boolean download(PatchInfo patchInfo) {
		String saveDir = patchInfo.getPatchDir();
		String zipSavePath = saveDir.concat(patchInfo.getZipName());
		
		boolean isOk = true;
		if(!FileUtils.exists(zipSavePath)) {	// 若已存在则不再重复下载(用于断点)
			
			// 先下载zip版本升级包
			FileUtils.createDir(saveDir);
			isOk = downZIP(patchInfo.getZipURL(), 
					zipSavePath, patchInfo.getMD5());
			
			// 若zip版本升级包下载失败, 则下载txt版本升级包
			if(isOk == false) {
				FileUtils.delete(zipSavePath);
				String txtSavePath = saveDir.concat(patchInfo.getTxtName());
				isOk = downTXT(patchInfo.getTxtURL(), 
						txtSavePath, zipSavePath, patchInfo.getMD5());
			}
			
		}
		
		// 下载升级步骤
//		if(isOk == true) {
//			String updatePath = saveDir.concat(Params.UPDATE_XML);
//			List<UpdateCmd> updateCmds = downXML(patchInfo.getUpdateURL(), updatePath);
//			patchInfo.setUpdateCmds(updateCmds);
//		}
		
		if(isOk == false) {
			FileUtils.delete(saveDir);
		}
		return isOk;
	}
	
	private static boolean downZIP(String zipURL, String zipPath, String zipMD5) {
		boolean isOk = HttpURLUtils.downloadByGet(zipPath, zipURL);
		if(isOk == true) {
			String MD5 = CryptoUtils.toFileMD5(zipPath);
			isOk = zipMD5.equalsIgnoreCase(MD5);
		}
		return isOk;
	}
	
	private static boolean downTXT(String txtURL, String txtPath, String zipPath, String zipMD5) {
		boolean isOk = HttpURLUtils.downloadByGet(txtPath, txtURL);
		if(isOk == true) {
			isOk = TXTUtils.toFile(txtPath, zipPath);
			if(isOk == true) {
				String MD5 = CryptoUtils.toFileMD5(zipPath);
				isOk = zipMD5.equalsIgnoreCase(MD5);
			}
		}
		return isOk;
	}
	
	private static List<UpdateCmd> downXML(String updateURL, String updatePath) {
		List<UpdateCmd> updateSteps = new LinkedList<UpdateCmd>();
		boolean isOk = HttpURLUtils.downloadByGet(updatePath, updateURL);
		if(isOk == true) {
			String xml = FileUtils.read(updatePath, Config.DEFAULT_CHARSET);
			try {
				Document doc = DocumentHelper.parseText(xml);
				Element root = doc.getRootElement();
				Element steps = root.element("steps");
				Iterator<Element> cmds = steps.elementIterator();
				while(cmds.hasNext()) {
					Element cmd = cmds.next();
					CmdType type = CmdType.toType(cmd.getName());
					String from = XmlUtils.getAttribute(cmd, "from");
					String to = XmlUtils.getAttribute(cmd, "to");
					
					UpdateCmd step = new UpdateCmd(type, from , to);
					updateSteps.add(step);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return updateSteps;
	}
	
}
