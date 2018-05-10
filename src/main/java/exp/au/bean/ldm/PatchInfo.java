package exp.au.bean.ldm;

public class PatchInfo {

	private String appName;
	
	private String time;
	
	private String version;
	
	private String MD5;
	
	private String zipURL;
	
	private String txtURL;
	
	public PatchInfo() {
		this.appName = "";
		this.time = "";
		this.version = "";
		this.MD5 = "";
		this.zipURL = "";
		this.txtURL = "";
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

	public void setMD5(String MD5) {
		MD5 = MD5;
	}

	public String getZipURL() {
		return zipURL;
	}

	public void setZipURL(String zipURL) {
		this.zipURL = zipURL;
	}

	public String getTxtURL() {
		return txtURL;
	}

	public void setTxtURL(String txtURL) {
		this.txtURL = txtURL;
	}
	
}
