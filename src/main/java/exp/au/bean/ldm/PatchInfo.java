package exp.au.bean.ldm;

public class PatchInfo {

	private String appName;
	
	private String time;
	
	private String version;
	
	private String MD5;
	
	private String downZipURL;
	
	private String downTxtURL;
	
	public PatchInfo() {
		this.appName = "";
		this.time = "";
		this.version = "";
		this.MD5 = "";
		this.downZipURL = "";
		this.downTxtURL = "";
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String mD5) {
		MD5 = mD5;
	}

	public String getDownZipURL() {
		return downZipURL;
	}

	public void setDownZipURL(String downZipURL) {
		this.downZipURL = downZipURL;
	}

	public String getDownTxtURL() {
		return downTxtURL;
	}

	public void setDownTxtURL(String downTxtURL) {
		this.downTxtURL = downTxtURL;
	}
	
}
