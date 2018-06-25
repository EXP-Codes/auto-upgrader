package exp.au.envm;

/**
 * <PRE>
 * 常量参数
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Params {
	
	/** 补丁文件名标�? */
	public final static String PATCH_TAG = "-patch-";
	
	/** 记录补丁发布时间的文件名 */
	public final static String RELEASE_TIME = "release-time";
	
	/** 记录补丁MD5校验码的文件�? */
	public final static String MD5_HTML = "MD5.html";
	
	/** 记录补丁升级步骤的文件名 */
	public final static String UPDATE_CMD = "update.cmd";
	
	/** zip格式补丁的文件名后缀 */
	public final static String ZIP_SUFFIX = ".zip";
	
	/** txt格式补丁的文件名后缀 */
	public final static String TXT_SUFFIX = ".txt";
	
	/** 备份升级文件的目录名*/
	public final static String TMP_DIR = "tmp";
	
	/** jar依赖库目�? */
	public final static String LIB_DIR = "./lib";
	
	/** jar文件后缀 */
	public final static String JAR_SUFFIX = ".jar";
	
}
