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
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PatchInfo implements Comparable<PatchInfo> {

	/** 应用名称 */
	private String appName;
	
	/** 补丁版本 */
	private Version version;
	
	/** 补丁目录 */
	private String patchDir;
	
	/** 补丁名称 */
	private String patchName;
	
	/** 补丁发布时间 */
	private String releaseTime;
	
	/** 补丁校验MD5 */
	private String MD5;
	
	/** zip格式补丁的下载路�? */
	private String zipURL;
	
	/** zip格式补丁的名�? */
	private String zipName;
	
	/** txt格式补丁的下载路�? */
	private String txtURL;
	
	/** txt格式补丁的名�? */
	private String txtName;
	
	/** 升级步骤命令�?(有序) */
	private List<UpdateCmd> updateCmds;
	
	/**
	 * 构造函�?
	 */
	public PatchInfo() {
		this.appName = "";
		this.version = Version.NULL;
		this.patchDir = "";
		this.patchName = "";
		this.releaseTime = "";
		this.MD5 = "";
		this.zipURL = "";
		this.zipName = "";
		this.txtURL = "";
		this.txtName = "";
		this.updateCmds = new LinkedList<UpdateCmd>();
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPatchDir() {
		if(StrUtils.isEmpty(patchDir)) {
			patchDir = StrUtils.concat(Config.PATCH_DOWN_DIR, 
					getAppName(), "/", getVersion().VER(), "/");
		}
		return patchDir;
	}
	
	public void setPatchDir(String patchDir) {
		this.patchDir = patchDir;
	}
	
	public String getPatchName() {
		return patchName;
	}
	
	public void setPatchName(String patchName) {
		this.patchName = patchName;
		setZipName(patchName.concat(Params.ZIP_SUFFIX));
		setTxtName(getZipName().concat(Params.TXT_SUFFIX));
	}
	
	public Version getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = new Version(version);
	}
	
	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
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
	
	public List<UpdateCmd> getUpdateCmds() {
		return updateCmds;
	}

	public void addUpdateCmd(UpdateCmd updateCmd) {
		this.updateCmds.add(updateCmd);
	}

	@Override
	public int compareTo(PatchInfo other) {
		return (other == null ? 0 : 
			this.getVersion().compareTo(other.getVersion()));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("APP-NAME : ").append(getAppName()).append("\r\n");
		sb.append("VERSION : ").append(getVersion()).append("\r\n");
		sb.append("PATCH-DIR : ").append(getPatchDir()).append("\r\n");
		sb.append("PATCH-NAME : ").append(getPatchName()).append("\r\n");
		sb.append("RELEAST-TIME : ").append(getReleaseTime()).append("\r\n");
		sb.append("MD5 : ").append(getMD5()).append("\r\n");
		sb.append("ZIP-URL : ").append(getZipURL()).append("\r\n");
		sb.append("ZIP-NAME : ").append(getZipName()).append("\r\n");
		sb.append("TXT-URL : ").append(getTxtURL()).append("\r\n");
		sb.append("TXT-NAME : ").append(getTxtName()).append("\r\n");
		sb.append("UPDATE-STEP : \r\n");
		for(UpdateCmd updateCmd : updateCmds) {
			sb.append("  ").append(updateCmd.toString()).append("\r\n");
		}
		return sb.toString();
	}

}
