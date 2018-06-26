package exp.au.api.test;

import exp.au.api.AppVerInfo;

/**
 * <PRE>
 * 测试导出应用的当前版本信息
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestAppVerInfo {

	public static void main(String[] args) {
		AppVerInfo.export();
	}
	
}
