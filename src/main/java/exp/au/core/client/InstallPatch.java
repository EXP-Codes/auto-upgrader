package exp.au.core.client;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import exp.au.Config;
import exp.au.bean.PatchInfo;
import exp.au.bean.UpdateCmd;
import exp.au.envm.CmdType;
import exp.au.envm.Params;
import exp.au.utils.UIUtils;
import exp.libs.utils.encode.CompressUtils;
import exp.libs.utils.format.XmlUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 根据升级步骤安装补丁
 * </PRE>
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class InstallPatch {
	
	/**
	 * 安装补丁
	 * @param patchInfo 补丁信息
	 * @return 是否安装成功
	 */
	public static boolean install(PatchInfo patchInfo) {
		
		if(!unzip(patchInfo)) {
			UIUtils.toConsole("安装补丁 [", patchInfo.getPatchName(), "] 失败 : 解压异常");
			return false;
		}
		
		if(!takeUpdateCmds(patchInfo)) {
			UIUtils.toConsole("安装补丁 [", patchInfo.getPatchName(), "] 失败 : 提取升级指令异常");
			return false;
		}
		
		int step = execUpdateCmds(patchInfo);
		if(step < patchInfo.getUpdateCmds().size()) {
			UIUtils.toConsole("安装补丁 [", patchInfo.getPatchName(), "] 失败 : 执行升级指令异常");
			
//			UIUtils.toConsole("正在回滚补丁 [", patchInfo.getPatchName(), "] ...");
//			boolean isOk = rollback(patchInfo, step);
//			UIUtils.toConsole("回滚补丁 [", patchInfo.getPatchName(), "] ", (isOk ? "成功" : "失败"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * 解压补丁包
	 * @param patchInfo
	 * @return 是否解压成功
	 */
	private static boolean unzip(PatchInfo patchInfo) {
		String patchDir = patchInfo.getPatchDir();
		String zipPath = PathUtils.combine(patchDir, patchInfo.getZipName());
		
		boolean isOk = CompressUtils.unZip(zipPath);
		if(isOk == true) {
			patchDir = PathUtils.combine(patchDir, patchInfo.getPatchName());
			patchInfo.setPatchDir(patchDir);	// 解压后修正补丁目录
			
		} else {
			UIUtils.toConsole("解压补丁 [", patchInfo.getPatchName(), "] 失败");
		}
		return isOk;
	}
	
	/**
	 * 提取升级步骤
	 * @param patchInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static boolean takeUpdateCmds(PatchInfo patchInfo) {
		String patchDir = patchInfo.getPatchDir();
		String updatePath = PathUtils.combine(patchDir, Params.UPDATE_CMD);
		String xml = FileUtils.read(updatePath, Config.DEFAULT_CHARSET);
		
		boolean isOk = false;
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			Element steps = root.element("steps");
			Iterator<Element> cmds = steps.elementIterator();
			while(cmds.hasNext()) {
				Element cmd = cmds.next();
				CmdType type = CmdType.toType(cmd.getName());
				String from = XmlUtils.getAttribute(cmd, "from");
				String to = XmlUtils.getAttribute(cmd, "to");
				
				patchInfo.addUpdateCmd(new UpdateCmd(type, from , to));
			}
			isOk = true;
			
		} catch (Exception e) {}
		return isOk;
	}
	
	/**
	 * 执行升级指令
	 * @param patchInfo
	 * @return 成功执行的步骤数
	 */
	private static int execUpdateCmds(PatchInfo patchInfo) {
		String patchDir = patchInfo.getPatchDir();
		String appDir = "./";
		
		int step = 0;
		for(UpdateCmd cmd : patchInfo.getUpdateCmds()) {
			
			boolean isOk = false;
			String src = "";
			String snk = "";
			
			if(CmdType.ADD == cmd.TYPE()) {
				src = PathUtils.combine(patchDir, cmd.FROM_PATH());
				snk = PathUtils.combine(appDir, cmd.TO_PATH());
				isOk = _execAdd(src, snk);
				
			} else if(CmdType.RPL == cmd.TYPE()) {
				src = PathUtils.combine(patchDir, cmd.FROM_PATH());
				snk = PathUtils.combine(appDir, cmd.TO_PATH());
				isOk = _execRpl(src, snk);
				
			} else if(CmdType.MOV == cmd.TYPE()) {
				src = PathUtils.combine(appDir, cmd.FROM_PATH());
				snk = PathUtils.combine(appDir, cmd.TO_PATH());
				isOk = _execMov(src, snk);
				
			} else if(CmdType.DEL == cmd.TYPE()) {
				src = PathUtils.combine(appDir, cmd.FROM_PATH());
				isOk = _execDel(src);
				
			} else {
				isOk = true;
			}
			
			if(isOk == true) {
				step++;
				
			} else {
				System.err.println(StrUtils.concat(
						"[auto-upgrader] [ERR] [", cmd.TYPE().EN(), "] [SETP:", 
						(step + 1), "] : [", src, "] => [", snk, "]"));
				break;
			}
		}
		return step;
	}
	
	/**
	 * 执行 add(添加) 命令
	 * @param src 源位置
	 * @param snk 目标位置
	 * @return
	 */
	private static boolean _execAdd(String src, String snk) {
		boolean isOk = false;
		
		if(FileUtils.exists(src)) {
			if(FileUtils.isFile(src)) {
				isOk = FileUtils.copyFile(src, snk);
				
			} else if(FileUtils.isDirectory(src)) {
				isOk = FileUtils.copyDirectory(src, snk);
			}
		}
		return isOk;
	}
	
	/**
	 * 执行  rpl(替换) 命令
	 * @param src 源位置
	 * @param snk 目标位置
	 * @return
	 */
	private static boolean _execRpl(String src, String snk) {
		boolean isOk = false;
		
		if(FileUtils.exists(src) && FileUtils.exists(snk)) {
			if(FileUtils.isFile(src)) {
				isOk = FileUtils.copyFile(src, snk);
				
			} else if(FileUtils.isDirectory(src)) {
				isOk = FileUtils.copyDirectory(src, snk);
			}
		}
		return isOk;
	}
	
	/**
	 * 执行 mov(移动) 命令
	 * @param src 源位置
	 * @param snk 目标位置
	 * @return
	 */
	private static boolean _execMov(String src, String snk) {
		boolean isOk = false;
		
		if(FileUtils.exists(src) && FileUtils.notExists(snk)) {
			if(FileUtils.isFile(src)) {
				isOk = FileUtils.moveFile(src, snk);
				
			} else if(FileUtils.isDirectory(src)) {
				isOk = FileUtils.moveDirectory(src, snk);
			}
		}
		return isOk;
	}
	
	/**
	 * 执行 del(删除) 命令
	 * @param src 源位置
	 * @param bak 备份位置
	 * @return
	 */
	private static boolean _execDel(String src) {
		return FileUtils.delete(src);
	}
	
	/**
	 * 升级回滚: 
	 *   FIXME 暂时没有好的回滚手段, 暂不回滚
	 * @param patchInfo
	 * @param step
	 * @return
	 */
	private static boolean rollback(PatchInfo patchInfo, int step) {
		String patchDir = patchInfo.getPatchDir();
		String appDir = "./";
		
		List<UpdateCmd> cmds = patchInfo.getUpdateCmds();
		while(--step >= 0) {
			UpdateCmd cmd = cmds.get(step);
			
			boolean isOk = false;
			if(CmdType.ADD == cmd.TYPE()) {
				isOk = true;
				
			} else if(CmdType.RPL == cmd.TYPE()) {
				isOk = true;
				
			} else if(CmdType.MOV == cmd.TYPE()) {
				isOk = true;
				
			} else if(CmdType.DEL == cmd.TYPE()) {
				isOk = true;
				
			} else {
				isOk = true;
			}
			
			if(isOk == false) {
				break;
			}
		}
		return (step == 0);
	}
	
	/**
	 * 回滚 add(添加)命令
	 * @param snk 目标位置
	 * @return
	 */
	private static boolean _rollbackAdd(String snk) {
		// TODO
		return true;
	}
	
	/**
	 * 回滚 rpl(替换) 命令
	 * @param snk 目标位置
	 * @return
	 */
	private static boolean _rollbackRpl(String snk) {
		// TODO
		return true;
	}
	
	/**
	 * 回滚 mov(移动) 命令
	 * @param src 源位置
	 * @param snk 目标位置
	 * @return
	 */
	private static boolean _rollbackMov(String src, String snk) {
		boolean isOk = false;
		
		if(FileUtils.isFile(snk)) {
			isOk = FileUtils.moveFile(snk, src);
			
		} else if(FileUtils.isDirectory(snk)) {
			isOk = FileUtils.moveDirectory(snk, src);
		}
		return isOk;
	}
	
	/**
	 * 回滚 del(删除) 命令
	 * @param src 源位置
	 * @param bak 备份位置
	 * @return
	 */
	private static boolean _rollbackDel(String src, String bak) {
		// TODO
		return true;
	}
	
}
