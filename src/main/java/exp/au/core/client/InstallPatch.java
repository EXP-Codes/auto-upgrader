package exp.au.core.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import exp.au.Config;
import exp.au.bean.PatchInfo;
import exp.au.bean.UpdateCmd;
import exp.au.bean.Version;
import exp.au.envm.CmdType;
import exp.libs.utils.encode.CompressUtils;
import exp.libs.utils.format.XmlUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;

//客户端步骤2：安装升级包 
// (升级前先检查每一个步骤的文件是否存在(推衍)， 若升级失败，则回滚到最后一个成功的备份版本)
public class InstallPatch {

	public static void install(Version curVer, List<PatchInfo> patchInfos) {
		Version lastVer = patchInfos.get(patchInfos.size() - 1).getVersion();
		Version installVer = install(patchInfos);
		if(installVer == null) {
			System.out.println("一个都没升级成功, 停留在当前版本:" + curVer.VER());
			
		} else if(installVer.compareTo(lastVer) == 0) {
			System.out.println("成功升级到最新版");
			
		} else {
			System.out.println("仅升级到版本:" + installVer.VER());
		}
	}
	
	/**
	 * 
	 * @param patchInfos
	 * @return 成功升级到的版本号(若为null则一个版本都没有升级到)
	 */
	private static Version install(List<PatchInfo> patchInfos) {
		
		Version installVer = null;
		for(PatchInfo patchInfo : patchInfos) {
			int step = toDo(patchInfo);
			if(step < patchInfo.getUpdateCmds().size()) {
				boolean isOk = rollback(patchInfo, step);
				break;
				
			} else {
				installVer = patchInfo.getVersion();
			}
		}
		return installVer;
	}
	
	private static List<UpdateCmd> taskUpdateCmd(String updateURL, String updatePath) {
		List<UpdateCmd> updateSteps = new LinkedList<UpdateCmd>();
		boolean isOk = HttpURLUtils.downloadByGet(updatePath, updateURL);
		if(isOk == true) {
			String xml = FileUtils.read(updatePath, Config.DEFAULT_CHARSET);
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
					
					UpdateCmd step = new UpdateCmd(type, from , to);
					updateSteps.add(step);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return updateSteps;
	}
	
	public static boolean install(PatchInfo patchInfo) {
		String patchBaseDir = patchInfo.getPatchDir();
		String zipPath = PathUtils.combine(patchBaseDir, patchInfo.getZipName());
		
		System.out.println(zipPath);
		// ./patches/bilibili-plugin/2.4/bilibili-plugin-patch-2.4.zip
		
		if(CompressUtils.unZip(zipPath)) {
			String patchDir = PathUtils.combine(patchBaseDir, patchInfo.getPatchName());
			System.out.println(patchDir);
			
			// FIXME 提取update.xml步骤
			// ./patches/bilibili-plugin/2.4/bilibili-plugin-patch-2.4
		}
		return true;
	}
	
	/**
	 * 进行版本升级
	 * @param patchInfo
	 * @return 成功执行的步骤数
	 */
	private static int toDo(PatchInfo patchInfo) {
		String patchBaseDir = patchInfo.getPatchDir();	// FIXME 解压后的根目录文件名不一定和zip文件名同名
		String appBaseDir = "./";
		
		int step = 0;
		for(UpdateCmd updateStep : patchInfo.getUpdateCmds()) {
			
			boolean isOk = false;
			if(CmdType.ADD == updateStep.getCmdType() || 
					CmdType.RPL == updateStep.getCmdType()) {
				String from = PathUtils.combine(patchBaseDir, updateStep.getFromPath());
				String to = PathUtils.combine(appBaseDir, updateStep.getToPath());
				
				if(FileUtils.exists(from)) {
					if(FileUtils.isFile(from)) {
						isOk = FileUtils.copyFile(from, to);
						
					} else if(FileUtils.isDirectory(from)) {
						isOk = FileUtils.copyDirectory(from, to);
					}
				}
				
			} else if(CmdType.MOV == updateStep.getCmdType()) {
				String from = PathUtils.combine(appBaseDir, updateStep.getFromPath());
				String to = PathUtils.combine(appBaseDir, updateStep.getToPath());
				
				if(FileUtils.exists(from) && FileUtils.notExists(to)) {
					if(FileUtils.isFile(from)) {
						isOk = FileUtils.moveFile(from, to);
						
					} else if(FileUtils.isDirectory(from)) {
						isOk = FileUtils.moveDirectory(from, to);
					}
				}
				
			} else if(CmdType.DEL == updateStep.getCmdType()) {
				String from = PathUtils.combine(appBaseDir, updateStep.getFromPath());
				String bak = PathUtils.combine(patchBaseDir, updateStep.getToPath());
				
				if(FileUtils.exists(from)) {
					
					if(FileUtils.isFile(from)) {
						isOk = FileUtils.copyFile(from, bak);
						
					} else if(FileUtils.isDirectory(from)) {
						isOk = FileUtils.copyDirectory(from, bak);
						
					} else {
						isOk = true;
					}
					isOk &= FileUtils.delete(from);
					
				} else {
					isOk = true;
				}
			} else {
				isOk = true;
			}
			
			if(isOk == true) {
				step++;
				
			} else {
				break;
			}
		}
		return step;
	}
	
	/**
	 * 升级回滚
	 * @param patchInfo
	 * @param step
	 * @return
	 */
	private static boolean rollback(PatchInfo patchInfo, int step) {
		String patchBaseDir = patchInfo.getPatchDir();
		String appBaseDir = "./";
		
		boolean isAllOk = true;
		List<UpdateCmd> updateSteps = patchInfo.getUpdateCmds();
		for(int i = step - 1; i >= 0; i--) {
			UpdateCmd updateStep = updateSteps.get(i);
			
			boolean isOk = false;
			if(CmdType.ADD == updateStep.getCmdType() || 
					CmdType.RPL == updateStep.getCmdType()) {
				String to = PathUtils.combine(appBaseDir, updateStep.getToPath());
				isOk = FileUtils.delete(to);
				
			} else if(CmdType.MOV == updateStep.getCmdType()) {
				String from = PathUtils.combine(appBaseDir, updateStep.getFromPath());
				String to = PathUtils.combine(appBaseDir, updateStep.getToPath());
				
				if(FileUtils.isFile(to)) {
					isOk = FileUtils.moveFile(to, from);
					
				} else if(FileUtils.isDirectory(to)) {
					isOk = FileUtils.moveDirectory(to, from);
				}
				
			} else if(CmdType.DEL == updateStep.getCmdType()) {
				if(StrUtils.isTrimEmpty(updateStep.getToPath())) {
					isOk = true;
					
				} else {
					String from = PathUtils.combine(appBaseDir, updateStep.getFromPath());
					String bak = PathUtils.combine(patchBaseDir, updateStep.getToPath());
					
					if(FileUtils.exists(bak)) {
						
						if(FileUtils.isFile(bak)) {
							isOk = FileUtils.copyFile(bak, from);
							
						} else if(FileUtils.isDirectory(bak)) {
							isOk = FileUtils.copyDirectory(bak, from);
						}
					}
				}
				
			} else {
				isOk = true;
			}
			
			isAllOk &= isOk;
			if(isAllOk == false) {
				break;
			}
		}
		return isAllOk;
	}
	
}
