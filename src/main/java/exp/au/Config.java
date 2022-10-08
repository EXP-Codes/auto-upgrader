package exp.au;

import exp.libs.conf.xml.XConfig;
import exp.libs.conf.xml.XConfigFactory;
import exp.libs.envm.Charset;


/**
 * <PRE>
 * 程序配置
 * </PRE>
 * <br/><B>PROJECT : </B> auto-upgrader
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/** 应用名称(由于此项目是被引用的接口, 因此有些地方不能从版本库取应用名称) */
	public final static String APP_NAME = "auto-upgrader";
	
	private final static String APP_PATH = "/exp/au/au_conf.xml";
	
	private final static String USER_PATH = "./conf/au_conf.xml";
	
	public final static String LAST_VER_PATH = "./conf/au.ver";
	
	public final static String PATCH_DOWN_DIR = "./patches/";
	
	public final static String PATCH_PAGE_DIR = "./patches-for-page/";
	
	private final static String TPL_PACKAGE = "/exp/au/core/mgr/";
	
	public final static String PAGE_TPL = TPL_PACKAGE.concat("index.tpl");
	
	public final static String TABLE_TPL = TPL_PACKAGE.concat("table.tpl");
	
	public final static String ROW_TPL = TPL_PACKAGE.concat("row.tpl");
	
	public final static String UPDATE_TPL = TPL_PACKAGE.concat("update.tpl");
	
	public final static String PAGE_PATH = "./index.html";
	
	private static volatile Config instance;
	
	private XConfig xConf;
	
	private Config() {
		this.xConf = XConfigFactory.createConfig("au-conf");
		xConf.loadConfFileInJar(APP_PATH);
		xConf.loadConfFile(USER_PATH);
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
	
	public String STAGING_SERVER() {
		return xConf.getVal("stagingServer");
	}
	
	public String PRODUCE_SERVER() {
		return xConf.getVal("produceServer");
	}
	
}
