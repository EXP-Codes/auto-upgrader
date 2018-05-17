package exp.au.envm;

import exp.libs.utils.other.StrUtils;

public class CmdType {

	public final static CmdType UNKNOW = new CmdType("unknow", "未知");
	
	public final static CmdType ADD = new CmdType("add", "添加");
	
	public final static CmdType RPL = new CmdType("rpl", "替换");
	
	public final static CmdType MOV = new CmdType("mov", "移动");
	
	public final static CmdType DEL = new CmdType("del", "删除");
	
	private String en;
	
	private String ch;
	
	public CmdType(String en, String ch) {
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
