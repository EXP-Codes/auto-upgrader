package exp.au.bean;

import exp.au.envm.CmdType;
import exp.au.envm.Params;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 升级命令对象
 * </PRE>
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class UpdateCmd {

	/** 命令类型 */
	private CmdType cmdType;
	
	/** 命令控制的源路径 */
	private String fromPath;
	
	/** 命令控制的目标路径 */
	private String toPath;
	
	/**
	 * 构造函数
	 * @param cmdType 命令类型
	 * @param fromPath 命令控制的源路径
	 * @param toPath 命令控制的目标路径
	 */
	public UpdateCmd(CmdType cmdType, String fromPath, String toPath) {
		this.cmdType = (cmdType == null ? CmdType.UNKNOW : cmdType);
		this.fromPath = (fromPath == null ? "" : fromPath.trim());
		this.toPath = (toPath == null ? "" : toPath.trim());
		
		// 删除命令的目标路径用于备份被删除的文件（回滚用）
		if(CmdType.DEL == this.cmdType) {
			this.toPath = StrUtils.concat(Params.TMP_DIR, "/", 
					FileUtils.getName(this.fromPath));
		}
	}

	public CmdType TYPE() {
		return cmdType;
	}

	public String FROM_PATH() {
		return fromPath;
	}

	public String TO_PATH() {
		return toPath;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(cmdType.toString()).append("] : ");
		if(cmdType == CmdType.DEL) {
			sb.append(" [").append(fromPath).append("]");
			
		} else {
			sb.append(" from [").append(fromPath).append("]");
			sb.append(" to [").append(toPath).append("]");
		}
		return sb.toString();
	}
	
}
