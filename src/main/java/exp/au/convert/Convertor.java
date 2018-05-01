package exp.au.convert;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.certificate.Config;
import exp.certificate.bean.AppInfo;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
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
	
	/** 私有化构造函数 */
	protected Convertor() {}
	
	/**
	 * 根据应用信息列表生成授权页面
	 * @param appInfos 应用列表
	 * @return 是否生成成功
	 */
	public static boolean toPage(List<AppInfo> appInfos) {
		List<String> tables = toTables(appInfos);
		Template tpl = new Template(Config.PAGE_TPL, Config.DEFAULT_CHARSET);
		tpl.set("tables", StrUtils.concat(tables, ""));
		tpl.set("time", TimeUtils.getSysDate());
		return FileUtils.write(Config.PAGE_PATH, 
				tpl.getContent(), Config.DEFAULT_CHARSET, false);
	}
	
	/**
	 * 根据应用列表生成对应的&lt;div&gt;模块
	 * @param appInfos 应用信息列表
	 * @return &lt;div&gt;模块
	 */
	private static List<String> toTables(List<AppInfo> appInfos) {
		List<String> tables = new LinkedList<String>();
		Template tpl = new Template(Config.TABLE_TPL, Config.DEFAULT_CHARSET);
		for(AppInfo appInfo : appInfos) {
			tpl.set("name", appInfo.getName());
			tpl.set("versions", CryptoUtils.toDES(appInfo.getVersions()));
			tpl.set("time", CryptoUtils.toDES(appInfo.getTime()));
			tpl.set("blacklist", CryptoUtils.toDES(appInfo.getBlacklist()));
			tpl.set("whitelist", CryptoUtils.toDES(appInfo.getWhitelist()));
			tables.add(tpl.getContent());
		}
		return tables;
	}
	
	/**
	 * 从页面提取应用授权信息
	 * @param pageSource 页面源码
	 * @param appName 应用名称
	 * @return 应用信息对象
	 */
	@SuppressWarnings("unchecked")
	public static AppInfo toAppInfo(final String pageSource, final String appName) {
		AppInfo app = null;
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
					app = Convertor.toAppInfo(table);
					break;
				}
			}
		} catch (Exception e) {
			log.error("从页面提取应用 [{}] 信息失败:\r\n{}", appName, pageSource, e);
		}
		return app;
	}
	
	/**
	 * 根据页面的&lt;table&gt;模块还原对应的应用信息对象
	 * @param table &lt;table&gt;模块
	 * @return 应用信息对象
	 */
	@SuppressWarnings("unchecked")
	private static AppInfo toAppInfo(Element table) {
		String name = "";
		String versions = "";
		String time = "";
		String blacklist = "";
		String whitelist = "";
		
		Element tbody = table.element("tbody");
		Iterator<Element> trs = tbody.elementIterator();
		while(trs.hasNext()) {
			Element tr = trs.next();
			List<Element> ths = tr.elements();
			String key = ths.get(0).getTextTrim();
			String val = ths.get(1).getTextTrim();
			
			if("SOFTWARE-NAME".equals(key)) {
				name = val;
				
			} else if("VERSIONS".equals(key)) {
				versions = CryptoUtils.deDES(val);
				
			} else if("TIME".equals(key)) {
				time = CryptoUtils.deDES(val);
				
			} else if("BLACK-LIST".equals(key)) {
				blacklist = CryptoUtils.deDES(val);
				
			} else if("WHITE-LIST".equals(key)) {
				whitelist = CryptoUtils.deDES(val);
				
			}
		}
		return new AppInfo(name, versions, time, blacklist, whitelist);
	}
	
}
