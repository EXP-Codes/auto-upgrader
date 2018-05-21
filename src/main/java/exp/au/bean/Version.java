package exp.au.bean;

import java.util.List;

import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <PRE>
 * 版本对象
 * </PRE>
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Version implements Comparable<Version> {

	/** 默认版本号 */
	public final static Version NULL = new Version(0, 0);
	
	/** 版本号的正则式 */
	private final static String REGEX = "(\\d+)\\.(\\d+)";
	
	/**
	 * 版本号
	 *  格式: major.minor
	 */
	private String ver;
	
	/** 主版本号 (>=0) */
	private int major;
	
	/** 次版本号 (>=0) */
	private int minor;
	
	/**
	 * 构造函数
	 * @param ver 版本号, 格式: major.minor
	 */
	public Version(String ver) {
		List<String> groups = RegexUtils.findGroups(ver, REGEX);
		if(groups.size() == 3) {
			this.ver = ver;
			this.major = NumUtils.toInt(groups.get(1), 0);
			this.minor = NumUtils.toInt(groups.get(2), 0);
			
		} else {
			this.ver = "0.0";
			this.major = 0;
			this.minor = 0;
		}
	}
	
	/**
	 * 构造函数
	 * @param major 主版本号 (>=0)
	 * @param minor 次版本号 (>=0)
	 */
	public Version(int major, int minor) {
		this.major = (major >= 0 ? major : 0);
		this.minor = (minor >= 0 ? minor : 0);
		this.ver = StrUtils.concat(this.major, ".", this.minor);
	}
	
	public String VER() {
		return ver;
	}
	
	public int MAJOR() {
		return major;
	}
	
	public int MINOR() {
		return minor;
	}
	
	/**
	 * 比较两个版本号
	 * @param other 其他版本号
	 * @return 0: this == other
	 *        >0: this > other
	 *        <0: this < other
	 */
	@Override
	public int compareTo(Version other) {
		int  rst = 0;
		if(other != null) {
			rst = this.major - other.major;
			if(rst == 0) {
				rst = (this.minor - other.minor);
			}
		}
		return rst;
	}
	
	@Override
	public String toString() {
		return VER();
	}
	
}
