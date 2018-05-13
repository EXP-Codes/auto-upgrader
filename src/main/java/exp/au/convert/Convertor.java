package exp.au.convert;

import java.io.File;
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
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.tpl.Template;

/**
 * <PRE>
 * 页面/应用信息转换器
 * </PRE>
 * <B>PROJECT：</B> exp-certificate
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Convertor {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(Convertor.class);
	
	private final static String VER_URL = Config.getInstn().VERSION_URL();
	
	public static void main(String[] args) {
		testPage();
//		testDown();
	}
	
	private static void testPage() {
		System.out.println(Convertor.toPage(Config.PATCH_PAGE_DIR));
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
	
	/** 私有化构造函数 */
	protected Convertor() {}
	
	/**
	 * 根据升级补丁目录生成升级导航页面
	 * @param patchDirPath 升级补丁目录路径
	 * @return 升级导航页面
	 */
	public static boolean toPage(String patchDirPath) {
		File patchDir = new File(patchDirPath);
		List<String> tables = toTables(patchDir);
		
		Template tpl = new Template(Config.PAGE_TPL, Config.DEFAULT_CHARSET);
		tpl.set("tables", StrUtils.concat(tables, ""));
		tpl.set("time", TimeUtils.getSysDate());
		return FileUtils.write(Config.PAGE_PATH, 
				tpl.getContent(), Config.DEFAULT_CHARSET, false);
	}
	
	/**
	 * 根据升级补丁目录生成每个应用的升级补丁导航表单
	 * @param patchDir 升级补丁目录
	 * @return 每个应用的升级补丁导航表单
	 */
	private static List<String> toTables(File patchDir) {
		List<String> tables = new LinkedList<String>();
		Template tpl = new Template(Config.TABLE_TPL, Config.DEFAULT_CHARSET);
		
		File[] appDirs = patchDir.listFiles();
		for(File appDir : appDirs) {
			if(appDir.isFile()) {
				continue;
			}
			
			tpl.set("name", appDir.getName());
			tpl.set("rows", StrUtils.concat(toRows(appDir), ""));
			tables.add(tpl.getContent());
		}
		return tables;
	}
	
	/**
	 * 根据某个应用的升级补丁目录生成其每个升级补丁的导航栏
	 * @param appDir 某个应用的升级补丁
	 * @return 每个升级补丁的导航栏
	 */
	private static List<String> toRows(File appDir) {
		List<String> rows = new LinkedList<String>();
		Template tpl = new Template(Config.ROW_TPL, Config.DEFAULT_CHARSET);
		
		File[] verDirs = appDir.listFiles();
		for(File verDir : verDirs) {
			if(verDir.isFile()) {
				continue;
			}
			
			tpl.set("app_name", appDir.getName());
			tpl.set("version", verDir.getName());
			tpl.set("time", TimeUtils.toStr(verDir.lastModified()));
			rows.add(tpl.getContent());
		}
		return ListUtils.reverse(rows);	// 版本倒序
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
	
}
