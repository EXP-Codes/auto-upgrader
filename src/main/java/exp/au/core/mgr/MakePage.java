package exp.au.core.mgr;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import exp.au.Config;
import exp.au.envm.Params;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.warp.tpl.Template;

/**
 * <PRE>
 * 根据补丁目录生成补丁管理页面
 * </PRE>
 * <br/><B>PROJECT : </B> auto-upgrader
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MakePage {

	/** 私有化构造函数 */
	protected MakePage() {}
	
	/**
	 * 更新补丁管理页面
	 * @return
	 */
	public static boolean updatePage() {
		boolean isOk = false;
		try {
			isOk = toPage(Config.PATCH_PAGE_DIR);
		} catch(Exception e) {}
		return isOk;
	}
	
	/**
	 * 根据升级补丁目录生成升级导航页面
	 * @param patchDirPath 升级补丁目录路径
	 * @return 升级导航页面
	 */
	private static boolean toPage(String patchDirPath) {
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
			
			String timePath = PathUtils.combine(verDir.getAbsolutePath(), Params.RELEASE_TIME);
			String time = FileUtils.read(timePath, Config.DEFAULT_CHARSET).trim();
			if(StrUtils.isEmpty(time)) {
				time = TimeUtils.toStr(verDir.lastModified());
			}
			
			tpl.set("app_name", appDir.getName());
			tpl.set("version", verDir.getName());
			tpl.set("time", time);
			rows.add(tpl.getContent());
		}
		return ListUtils.reverse(rows);	// 版本倒序
	}
	
	
}
