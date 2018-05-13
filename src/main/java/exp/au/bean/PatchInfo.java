package exp.au.bean;

import java.util.LinkedList;
import java.util.List;

import exp.au.Config;
import exp.au.envm.Params;
import exp.libs.utils.other.StrUtils;

public class PatchInfo implements Comparable<PatchInfo> {

	private String appName;
	
	private Version version;
	
	private String time;
	
	private String MD5;
	
	private String zipURL;
	
	private String zipName;
	
	private String txtURL;
	
	private String txtName;
	
	private String updateURL;
	
	private List<Step> updateSteps;
	
	public PatchInfo() {
		this.appName = "";
		this.version = Version.NULL;
		this.time = "";
		this.MD5 = "";
		this.updateURL = "";
		this.zipURL = "";
		this.zipName = "";
		this.txtURL = "";
		this.txtName = "";
		this.updateURL = "";
		this.updateSteps = new LinkedList<Step>();
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
	
	public String getPatchDir() {
		return StrUtils.concat(Config.PATCH_DOWN_DIR, 
				getAppName(), "/", getVersion().VER(), "/");
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
		setZipName(patchName);
		setTxtName(patchName.concat(Params.TXT_SUFFIX));
	}

	public String getZipName() {
		return zipName;
	}

	private void setZipName(String zipName) {
		this.zipName = zipName;
	}

	public String getTxtName() {
		return txtName;
	}

	private void setTxtName(String txtName) {
		this.txtName = txtName;
	}
	
	public String getUpdateURL() {
		return updateURL;
	}

	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	public List<Step> getUpdateSteps() {
		return updateSteps;
	}

	public void setUpdateSteps(List<Step> updateSteps) {
		this.updateSteps = updateSteps;
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
		sb.append("ZIP_NAME : ").append(getZipName()).append("\r\n");
		sb.append("TXT_URL : ").append(getTxtURL()).append("\r\n");
		sb.append("TXT_NAME : ").append(getTxtName()).append("\r\n");
		sb.append("UPDATE_URL : ").append(getUpdateURL()).append("\r\n");
		sb.append("UPDATE_STEP : \r\n");
		for(Step updateStep : updateSteps) {
			sb.append("  ").append(updateStep.toString()).append("\r\n");
		}
		return sb.toString();
	}

}
