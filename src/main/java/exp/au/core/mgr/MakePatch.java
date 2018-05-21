package exp.au.core.mgr;

import exp.au.Config;
import exp.au.envm.Params;
import exp.au.ui.mgr.MakePatchUI;
import exp.libs.envm.Charset;
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
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MakePatch {

	/** 私有化构造函数 */
	protected MakePatch() {}
	
	/**
	 * 生成升级补丁
	 * @param SRC_DIR 选择的补丁目录
	 * @param APP_NAME 应用名称
	 * @param VERSION 补丁版本
	 */
	public static void generate(final String SRC_DIR, 
			final String APP_NAME, final String VERSION) {
		final String PATCH_NAME = StrUtils.concat(APP_NAME, Params.PATCH_TAG, VERSION);
		final String PATCH_ZIP_NAME = PATCH_NAME.concat(Params.ZIP_SUFFIX);
		final String SNK_DIR = StrUtils.concat(Config.PATCH_PAGE_DIR, APP_NAME, "/", VERSION, "/");
		final String PATCH_DIR = SNK_DIR.concat(PATCH_NAME);
		final String PATCH_ZIP_PATH = SNK_DIR.concat(PATCH_ZIP_NAME);
		
		if(FileUtils.exists(PATCH_ZIP_PATH) && 
				!SwingUtils.confirm("补丁已存在, 是否覆盖 ? ")) {
			return;
		}
		
		console("正在复制补丁目录到 [", PATCH_DIR, "] ...");
		boolean isOk = FileUtils.copyDirectory(SRC_DIR, PATCH_DIR);
		if(isOk == false) {
			console("复制补丁目录到 [", PATCH_DIR, "] 失败");
			return;
		}
		
		console("正在生成 [", Params.UPDATE_XML, "] 升级步骤文件...");
		isOk = _toUpdateXml(PATCH_DIR, PATCH_ZIP_NAME);
		if(isOk == false) {
			console("生成 [", Params.UPDATE_XML, "] 升级步骤文件失败");
			return;
		}
		
		console("正在生成补丁目录 [", PATCH_DIR, "] 的压缩文件...");
		isOk = CompressUtils.toZip(PATCH_DIR, PATCH_ZIP_PATH);
		isOk &= FileUtils.delete(PATCH_DIR);
		if(isOk == false) {
			console("生成补丁目录 [", PATCH_ZIP_NAME, "] 的压缩文件失败");
			return;
		}

		console("正在生成补丁文件 [", PATCH_ZIP_NAME, "] 的备份文件...");
		String txtPath = PATCH_ZIP_PATH.concat(Params.TXT_SUFFIX);
		isOk = TXTUtils.toTXT(PATCH_ZIP_PATH, txtPath);
		if(isOk == false) {
			console("生成补丁文件 [", PATCH_ZIP_NAME, "] 的备份文件失败");
			return;
		}
		
		console("正在生成补丁文件 [", PATCH_ZIP_NAME, "] 的MD5校验码...");
		String MD5 = CryptoUtils.toFileMD5(PATCH_ZIP_PATH);
		MakePatchUI.getInstn().updatMD5(MD5);
		String MD5Path = PathUtils.combine(SNK_DIR, Params.MD5_HTML);
		isOk = FileUtils.write(MD5Path, MD5, Charset.ISO, false);
		if(isOk == false) {
			console("生成补丁文件 [", PATCH_ZIP_NAME, "] 的MD5校验码失败");
			return;
		}
		
		console("正在更新补丁管理页面...");
		isOk = MakePage.updatePage();
		if(isOk == false) {
			console("更新补丁管理页面失败");
			return;
		}
		
		console("生成应用程序 [", APP_NAME, "] 的升级补丁完成 (管理页面已更新)");
		SwingUtils.info("生成补丁成功");
	}
	
	/**
	 * 生成升级步骤文件
	 * @param patchDir
	 * @param patchName
	 * @return
	 */
	private static boolean _toUpdateXml(String patchDir, String patchName) {
		Template tmp = new Template(Config.UPDATE_TPL, Charset.UTF8);
		tmp.set("patch-name", patchName);
		tmp.set("cmds", MakePatchUI.getInstn().getXmlCmds());
		
		String savePath = PathUtils.combine(patchDir, Params.UPDATE_XML);
		return FileUtils.write(savePath, tmp.getContent(), Charset.UTF8, false);
	}
	
	/**
	 * 打印信息到界面
	 * @param msgs
	 */
	public static void console(Object... msgs) {
		MakePatchUI.getInstn().console(msgs);
	}
	
}
