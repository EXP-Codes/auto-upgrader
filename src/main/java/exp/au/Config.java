package exp.au;

import exp.libs.envm.Charset;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;


/**
 * <PRE>
 * 程序配置
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	private final static String CONF_PATH = "./conf/au_conf.xml";
	
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
	
}
