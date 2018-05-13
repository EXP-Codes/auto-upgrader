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
import exp.au.bean.ldm.PatchInfo;
import exp.au.bean.ldm.Version;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;

// 客户端步骤1：下载升级补丁
public class Down {
	
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(Down.class);
	
	private final static String VER_URL = Config.getInstn().VERSION_URL();
	
	/*
	 * 1.先检查是否存在新版本
	 * 2.检查版本跨度
	 * 3.下载升级包
	 * 4.检查MD5
	 * 5.根据升级指导文件升级（删除、增加、替换文件）
	 */
	protected Down() {}
	
	public static void main(String[] args) {
		testDown();
	}
	
	private static void testDown() {
		// 获取指定应用的升级补丁列表
		String pageSource = HttpURLUtils.doGet(VER_URL);
		List<PatchInfo> patchInfos = getPatchInfos(pageSource, "bilibili-plugin");
		
		// 根据当前版本号筛选升级列表
		final Version CUR_VER = new Version(4, 0);
		Iterator<PatchInfo> patchInfoIts = patchInfos.iterator();
		while(patchInfoIts.hasNext()) {
			PatchInfo patchInfo = patchInfoIts.next();
			if(CUR_VER.compareTo(patchInfo.getVersion()) >= 0) {
				patchInfoIts.remove();
			}
		}

		// 下载升级补丁
		boolean isOk = download(patchInfos);
		System.out.println("下载全部补丁:" + isOk);
	}
	
	/**
	 * 从页面提取应用补丁列表信息
	 * @param pageSource 页面源码
	 * @param appName 应用名称
	 * @return 补丁列表信息
	 */
	@SuppressWarnings("unchecked")
	public static List<PatchInfo> getPatchInfos(String pageSource, String appName) {
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
				String zipURL = combineURL(VER_URL, brackets.get(0));
				String txtURL = combineURL(VER_URL, brackets.get(1));
				String md5URL = combineURL(VER_URL, brackets.get(2));
				
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
	
	private static String combineURL(String prefix, String suffix) {
		return prefix.concat(suffix.replaceFirst("^\\.", "")).replace('\\', '/');
	}
	
	private static String toPatchName(String appName, String version) {
		return StrUtils.concat(appName, "-patch-", version, ".zip");
	}
	
	
	public static boolean download(List<PatchInfo> patchInfos) {
		int cnt = 0;
		for(PatchInfo patchInfo : patchInfos) {
			String saveDir = StrUtils.concat(Config.PATCH_DOWN_DIR, patchInfo.getAppName(), 
					"/", patchInfo.getVersion().VER(), "/");
			String zipSavePath = saveDir.concat(patchInfo.getZipPatchName());
			if(FileUtils.exists(zipSavePath)) {
				cnt++;
				continue;
			}
			
			// 先下载zip版本升级包
			FileUtils.createDir(saveDir);
			boolean isOk = HttpURLUtils.downloadByGet(zipSavePath, patchInfo.getZipURL());
			if(isOk == true) {
				String MD5 = CryptoUtils.toFileMD5(zipSavePath);
				isOk = patchInfo.getMD5().equalsIgnoreCase(MD5);
			}
			System.out.println(patchInfo.getZipURL());
			System.out.println("下载" + zipSavePath + "补丁:" + isOk);
			
			// 若zip版本升级包下载失败, 则下载txt版本升级包
			if(isOk == false) {
				FileUtils.delete(zipSavePath);
				String txtSavePath = saveDir.concat(patchInfo.getTxtPatchName());
				isOk = HttpURLUtils.downloadByGet(txtSavePath, patchInfo.getTxtURL());
				if(isOk == true) {
					isOk = TXTUtils.toFile(txtSavePath, zipSavePath);
					if(isOk == true) {
						String MD5 = CryptoUtils.toFileMD5(zipSavePath);
						isOk = patchInfo.getMD5().equalsIgnoreCase(MD5);
					}
				}
				System.out.println(patchInfo.getTxtURL());
				System.out.println("下载" + txtSavePath + "补丁:" + isOk);
			}
			
			cnt += (isOk ? 1 : 0);
		}
		return (cnt == patchInfos.size());
	}
	
	
	
}
