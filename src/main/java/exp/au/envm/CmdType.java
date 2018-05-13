package exp.au.envm;

public class CmdType {

	public final static CmdType UNKNOW = new CmdType("unknow");
	
	public final static CmdType ADD = new CmdType("add");
	
	public final static CmdType MOV = new CmdType("mov");
	
	public final static CmdType DEL = new CmdType("del");
	
	private String cmd;
	
	public CmdType(String cmd) {
		this.cmd = cmd;
	}
	
	public String CMD() {
		return cmd;
	}
	
	@Override
	public String toString() {
		return CMD();
	}
	
	public static CmdType toType(String cmd) {
		CmdType type = UNKNOW;
		if(ADD.CMD().equalsIgnoreCase(cmd)) {
			type = ADD;
			
		} else if(MOV.CMD().equalsIgnoreCase(cmd)) {
			type = MOV;
			
		} else if(DEL.CMD().equalsIgnoreCase(cmd)) {
			type = DEL;
			
		}
		return type;
	}
	
}
