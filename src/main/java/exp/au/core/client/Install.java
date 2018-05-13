package exp.au.core.client;

import java.util.List;

import exp.au.bean.PatchInfo;
import exp.au.bean.Step;
import exp.au.bean.Version;
import exp.au.envm.CmdType;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;

//客户端步骤2：安装升级包 
// (升级前先检查每一个步骤的文件是否存在(推衍)， 若升级失败，则回滚到最后一个成功的备份版本)
public class Install {

	/**
	 * 
	 * @param patchInfos
	 * @return 成功升级到的版本号
	 */
	public static Version install(List<PatchInfo> patchInfos) {
		
		for(PatchInfo patchInfo : patchInfos) {
			int step = toDo(patchInfo);
			if(step < patchInfo.getUpdateSteps().size()) {
				rollback(patchInfo, step);
				break;
			}
		}
		return null;
	}
	
	/**
	 * 进行版本升级
	 * @param patchInfo
	 * @return 成功执行的步骤数
	 */
	private static int toDo(PatchInfo patchInfo) {
		String patchBaseDir = patchInfo.getPatchDir();
		String appBaseDir = "./";
		
		int step = 0;
		for(Step updateStep : patchInfo.getUpdateSteps()) {
			
			boolean isOk = false;
			if(CmdType.ADD == updateStep.getCmdType()) {
				String from = PathUtils.combine(patchBaseDir, updateStep.getFromPath());
				String to = PathUtils.combine(appBaseDir, updateStep.getToPath());
				isOk = FileUtils.copyFile(from, to);	// FIXME: 需要区分文件和文件夹
				
			} else if(CmdType.MOV == updateStep.getCmdType()) {
				String from = PathUtils.combine(appBaseDir, updateStep.getFromPath());
				String to = PathUtils.combine(appBaseDir, updateStep.getToPath());
				
			} else if(CmdType.DEL == updateStep.getCmdType()) {
				String from = PathUtils.combine(appBaseDir, updateStep.getFromPath());
				
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
	
	private static boolean rollback(PatchInfo patchInfo, int step) {
		
		return false;
	}
	
}
