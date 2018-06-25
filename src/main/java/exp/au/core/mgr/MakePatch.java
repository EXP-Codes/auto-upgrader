package exp.au.core.mgr;

import exp.au.Config;
import exp.au.envm.Params;
import exp.au.ui.mgr.MakePatchUI;
import exp.au.utils.PatchUtils;
import exp.libs.utils.encode.CompressUtils;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.tpl.Template;
import exp.libs.warp.ui.SwingUtils;

/**
 * <PRE>
 * 根据补丁参数制作补丁, 并放置到补丁管理目录
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MakePatch {

	/** 私有化构造函�? */
	protected MakePatch() {}
	
	/**
	 * 生成升级补丁
	 * @param SRC_DIR 选择的原始补丁目�?
	 * @param APP_NAME 应用名称
	 * @param VERSION 补丁版本
	 * @param RELEASE_TIME 发布时间
	 */
	public static void generate(final String SRC_DIR, final String APP_NAME, 
			final String VERSION, final String RELEASE_TIME) {
		final String PATCH_NAME = PatchUtils.toPatchName(APP_NAME, VERSION);
		final String PATCH_ZIP_NAME = PATCH_NAME.concat(Params.ZIP_SUFFIX);
		final String SNK_DIR = StrUtils.concat(Config.PATCH_PAGE_DIR, APP_NAME, "/", VERSION, "/");
		final String PATCH_DIR = SNK_DIR.concat(PATCH_NAME);
		final String PATCH_ZIP_PATH = SNK_DIR.concat(PATCH_ZIP_NAME);
		
		int step = clearStepStatus();
		if(FileUtils.exists(PATCH_ZIP_PATH) && 
				!SwingUtils.confirm("补丁已存�?, 是否覆盖 ? ")) {
			return;
		}
		
		
		toConsole("正在复制补丁目录�? [", PATCH_DIR, "] ...");
		boolean isOk = FileUtils.copyDirectory(SRC_DIR, PATCH_DIR);
		if(updateStepStatus(step++, isOk) == false) {
			toConsole("复制补丁目录�? [", PATCH_DIR, "] 失败");
			return;
		}
		PatchUtils.patchSleep();
		
		
		toConsole("正在生成 [", Params.UPDATE_CMD, "] 升级步骤文件...");
		isOk = _toUpdateXml(PATCH_DIR, PATCH_ZIP_NAME, RELEASE_TIME);
		if(updateStepStatus(step++, isOk) == false) {
			toConsole("生成 [", Params.UPDATE_CMD, "] 升级步骤文件失败");
			return;
		}
		PatchUtils.patchSleep();
		
		
		toConsole("正在生成补丁目录 [", PATCH_DIR, "] 的压缩文�?...");
		isOk = CompressUtils.toZip(PATCH_DIR, PATCH_ZIP_PATH);
		isOk &= FileUtils.delete(PATCH_DIR);
		if(updateStepStatus(step++, isOk) == false) {
			toConsole("生成补丁目录 [", PATCH_ZIP_NAME, "] 的压缩文件失�?");
			return;
		}
		PatchUtils.patchSleep();
		

		toConsole("正在生成补丁文件 [", PATCH_ZIP_NAME, "] 的备份文�?...");
		String txtPath = PATCH_ZIP_PATH.concat(Params.TXT_SUFFIX);
		isOk = TXTUtils.toTXT(PATCH_ZIP_PATH, txtPath);
		if(updateStepStatus(step++, isOk) == false) {
			toConsole("生成补丁文件 [", PATCH_ZIP_NAME, "] 的备份文件失�?");
			return;
		}
		PatchUtils.patchSleep();
		
		
		toConsole("正在生成补丁文件 [", PATCH_ZIP_NAME, "] 的时间水�?...");
		String timePath = PathUtils.combine(SNK_DIR, Params.RELEASE_TIME);
		isOk = FileUtils.write(timePath, RELEASE_TIME, Config.DEFAULT_CHARSET, false);
		if(updateStepStatus(step++, isOk) == false) {
			toConsole("生成补丁文件 [", PATCH_ZIP_NAME, "] 的时间水印失�?");
			return;
		}
		
		
		toConsole("正在生成补丁文件 [", PATCH_ZIP_NAME, "] 的MD5校验�?...");
		String MD5 = CryptoUtils.toFileMD5(PATCH_ZIP_PATH);
		MakePatchUI.getInstn().updatMD5(MD5);
		String MD5Path = PathUtils.combine(SNK_DIR, Params.MD5_HTML);
		isOk = FileUtils.write(MD5Path, MD5, Config.DEFAULT_CHARSET, false);
		if(updateStepStatus(step++, isOk) == false) {
			toConsole("生成补丁文件 [", PATCH_ZIP_NAME, "] 的MD5校验码失�?");
			return;
		}
		PatchUtils.patchSleep();
		
		
		toConsole("正在更新补丁管理页面...");
		isOk = MakePage.updatePage();
		if(updateStepStatus(step++, isOk) == false) {
			toConsole("更新补丁管理页面失败");
			return;
		}
		
		
		toConsole("生成应用程序 [", APP_NAME, "] 的升级补丁完�? (管理页面已更�?)");
		SwingUtils.info("生成补丁成功");
	}
	
	/**
	 * 生成升级步骤文件
	 * @param patchDir
	 * @param patchName
	 * @param releaseTime 
	 * @return
	 */
	private static boolean _toUpdateXml(String patchDir, String patchName, String releaseTime) {
		Template tmp = new Template(Config.UPDATE_TPL, Config.DEFAULT_CHARSET);
		tmp.set("patch-name", patchName);
		tmp.set("release-time", releaseTime);
		tmp.set("cmds", MakePatchUI.getInstn().getXmlCmds());
		
		String savePath = PathUtils.combine(patchDir, Params.UPDATE_CMD);
		return FileUtils.write(savePath, tmp.getContent(), Config.DEFAULT_CHARSET, false);
	}
	
	/**
	 * 打印信息到界面控制台
	 * @param msgs
	 */
	private static void toConsole(Object... msgs) {
		MakePatchUI.getInstn().toConsole(msgs);
	}
	
	/**
	 * 清空制作补丁步骤状�?
	 * @return 初始步骤索引
	 */
	private static int clearStepStatus() {
		MakePatchUI.getInstn().clearProgressBar();
		return 0;
	}
	
	/**
	 * 更新制作补丁步骤状�?
	 * @param step
	 * @param isOk
	 * @return
	 */
	private static boolean updateStepStatus(int step, boolean isOk) {
		MakePatchUI.getInstn().updateProgressBar(step, isOk);
		return isOk;
	}
	
}
