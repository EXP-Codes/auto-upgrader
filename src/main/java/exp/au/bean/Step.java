package exp.au.bean;

import exp.au.envm.CmdType;

public class Step {

	private CmdType cmdType;
	
	private String fromPath;
	
	private String toPath;
	
	public Step(CmdType cmdType, String fromPath, String toPath) {
		this.cmdType = (cmdType == null ? CmdType.UNKNOW : cmdType);
		this.fromPath = (fromPath == null ? "" : fromPath.trim());
		this.toPath = (toPath == null ? "" : toPath.trim());
	}

	public CmdType getCmdType() {
		return cmdType;
	}

	public String getFromPath() {
		return fromPath;
	}

	public String getToPath() {
		return toPath;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(cmdType.CMD()).append("] : ");
		if(cmdType == CmdType.DEL) {
			sb.append(" [").append(fromPath).append("]");
			
		} else {
			sb.append(" from [").append(fromPath).append("]");
			sb.append(" to [").append(toPath).append("]");
		}
		return sb.toString();
	}
	
}
