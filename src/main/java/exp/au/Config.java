package exp.au;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;


/**
 * <PRE>
 * 程序配置
 * </PRE>
 * <B>PROJECT：</B> exp-certificate
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	private final static Logger log = LoggerFactory.getLogger(Config.class);
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	private final static String CONF_PATH = "./conf/au_conf.xml";
	
	public final static String PAGE_TPL = "/exp/au/core/index.tpl";
	
	public final static String TABLE_TPL = "/exp/au/core/table.tpl";
	
	public final static String ROW_TPL = "/exp/au/core/row.tpl";
	
	public final static String PAGE_PATH = "./index.html";
	
	private static volatile Config instance;
	
	private XConfig xConf;
	
	private Config() {
		this.xConf = XConfigFactory.createConfig("au-conf");
		xConf.loadConfFile(CONF_PATH);
	}
	
	public static Config getInstn() {
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}
	
	public String VERSION_URL() {
		return xConf.getVal("versionURL");
	}
	
//	@SuppressWarnings("unchecked")
//	public List<AppInfo> getAppInfos() {
//		List<AppInfo> appList = new LinkedList<AppInfo>();
//		try {
//			Element root = xConf.loadConfFile(CONF_PATH);
//			Element apps = root.element("appInfos");
//			Iterator<Element> appIts = apps.elementIterator("appInfo");
//			while(appIts.hasNext()) {
//				Element app = appIts.next();
//				String name = app.elementText("name");
//				String versions = app.elementText("versions");
//				String time = app.elementText("time");
//				String blacklist = app.elementText("blacklist");
//				String whitelist = app.elementText("whitelist");
//				
//				appList.add(new AppInfo(name, versions, time, blacklist, whitelist));
//			}
//			
//		} catch(Exception e) {
//			log.error("加载配置文件失败: {}", CONF_PATH, e);
//		}
//		return appList;
//	}
//	
	
	
}
