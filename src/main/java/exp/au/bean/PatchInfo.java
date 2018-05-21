package exp.au.bean;

import java.util.LinkedList;
import java.util.List;

import exp.au.Config;
import exp.au.envm.Params;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 升级补丁参数对象
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PatchInfo implements Comparable<PatchInfo> {

	/** 应用名称 */
	private String appName;
	
	/** 补丁版本 */
	private Version version;
	
	/** 补丁发布时间 */
	private String time;
	
	/** 补丁校验MD5 */
	private String MD5;
	
	/** zip格式补丁的下载路径 */
	private String zipURL;
	
	/** zip格式补丁的名称 */
	private String zipName;
	
	/** txt格式补丁的下载路径 */
	private String txtURL;
	
	/** txt格式补丁的名称 */
	private String txtName;
	
	/** 升级步骤文件的下载路径 */
	private String updateURL;
	
	/** 升级步骤命令集 */
	private List<UpdateCmd> updateCmds;
	
	/**
	 * 构造函数
	 */
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
		this.updateCmds = new LinkedList<UpdateCmd>();
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

	public List<UpdateCmd> getUpdateCmds() {
		return updateCmds;
	}

	public void setUpdateCmds(List<UpdateCmd> updateCmds) {
		this.updateCmds = updateCmds;
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
		for(UpdateCmd updateCmd : updateCmds) {
			sb.append("  ").append(updateCmd.toString()).append("\r\n");
		}
		return sb.toString();
	}

}
