package exp.au.core.client;

import java.util.List;

import exp.au.bean.PatchInfo;
import exp.au.bean.Version;

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
			
		}
		return null;
	}
	
	private static boolean toDo(PatchInfo patchInfo) {
		
		return true;
	}
	
}
