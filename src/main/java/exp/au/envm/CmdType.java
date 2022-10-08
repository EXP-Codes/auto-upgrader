package exp.au.envm;

import exp.libs.utils.str.StrUtils;

/**
 * <PRE>
 * 补丁升级步骤命令类型
 * </PRE>
 * <br/><B>PROJECT : </B> auto-upgrader
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CmdType {

	/** 未知命令 [unknow] */
	public final static CmdType UNKNOW = new CmdType("unknow", "未知");
	
	/** 新增命令 [add] : [新文件/目录] from [补丁包相对位置] to [应用程序相对位置] */
	public final static CmdType ADD = new CmdType("add", "添加");
	
	/** 替换命令 [rpl] : [新文件/目录] from [补丁包相对位置] to [应用程序相对位置] (若存在则替换) */
	public final static CmdType RPL = new CmdType("rpl", "替换");
	
	/** 移动命令 [mov] : [原文件/目录] from [应用程序相对位置(旧)] to [应用程序相对位置(新)] */
	public final static CmdType MOV = new CmdType("mov", "移动");
	
	/** 删除命令 [del] : [原文件/目录] from [应用程序相对位置] 删除 */
	public final static CmdType DEL = new CmdType("del", "删除");
	
	/** 命令英文名 */
	private String en;
	
	/** 命令中文名 */
	private String ch;
	
	/**
	 * 构造函数
	 * @param en 命令英文名
	 * @param ch 命令中文名
	 */
	private CmdType(String en, String ch) {
		this.en = en;
		this.ch = ch;
	}
	
	public String EN() {
		return en;
	}
	
	public String CH() {
		return ch;
	}
	
	@Override
	public String toString() {
		return StrUtils.concat(CH(), "(", EN(), ")");
	}
	
	/**
	 * 根据命令名称转换为命令对象
	 * @param cmdName 命令名称
	 * @return 命令对象
	 */
	public static CmdType toType(String cmdName) {
		CmdType type = UNKNOW;
		if(ADD.EN().equalsIgnoreCase(cmdName) || ADD.CH().equals(cmdName)) {
			type = ADD;
			
		} else if(RPL.EN().equalsIgnoreCase(cmdName) || RPL.CH().equals(cmdName)) {
			type = RPL;
			
		} else if(MOV.EN().equalsIgnoreCase(cmdName) || MOV.CH().equals(cmdName)) {
			type = MOV;
			
		} else if(DEL.EN().equalsIgnoreCase(cmdName) || DEL.CH().equals(cmdName)) {
			type = DEL;
			
		}
		return type;
	}
	
}
