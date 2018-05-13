package exp.au.bean;

public class PatchInfo implements Comparable<PatchInfo> {

	private String appName;
	
	private Version version;
	
	private String time;
	
	private String MD5;
	
	private String zipURL;
	
	private String txtURL;
	
	private String patchName;
	
	private String zipPatchName;
	
	private String txtPatchName;
	
	public PatchInfo() {
		this.appName = "";
		this.version = Version.NULL;
		this.time = "";
		this.MD5 = "";
		this.zipURL = "";
		this.txtURL = "";
		this.patchName = "";
		this.zipPatchName = "";
		this.txtPatchName = "";
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = new Version(version);
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String MD5) {
		this.MD5 = MD5;
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
	
	public void setPatchName(String patchName) {
		this.patchName = patchName;
		setZipPatchName(patchName);
		setTxtPatchName(patchName.concat(".txt"));	// FIXME
	}

	public String getZipPatchName() {
		return zipPatchName;
	}

	private void setZipPatchName(String zipPatchName) {
		this.zipPatchName = zipPatchName;
	}

	public String getTxtPatchName() {
		return txtPatchName;
	}

	private void setTxtPatchName(String txtPatchName) {
		this.txtPatchName = txtPatchName;
	}

	@Override
	public int compareTo(PatchInfo other) {
		return (other == null ? 0 : 
			this.getVersion().compareTo(other.getVersion()));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("APP_NAME : ").append(getAppName()).append("\r\n");
		sb.append("TIME : ").append(getTime()).append("\r\n");
		sb.append("VERSION : ").append(getVersion()).append("\r\n");
		sb.append("MD5 : ").append(getMD5()).append("\r\n");
		sb.append("ZIP_URL : ").append(getZipURL()).append("\r\n");
		sb.append("TXT_URL : ").append(getTxtURL()).append("\r\n");
		sb.append("ZIP_NAME : ").append(getZipPatchName()).append("\r\n");
		sb.append("TXT_NAME : ").append(getTxtPatchName()).append("\r\n");
		return sb.toString();
	}

}
