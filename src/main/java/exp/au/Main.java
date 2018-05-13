package exp.au;

import exp.libs.utils.other.LogUtils;
import exp.libs.warp.net.http.HttpURLUtils;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-04-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	// 在线升级， 升级命令：移动文件，删除文件，添加文件,  压缩包转txt
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		// TODO: 程序入口
		
		boolean isOk = HttpURLUtils.downloadByGet(
				"./log/b.zip", "http://lyy289065406.gitee.io/auto-upgrader/packages/a.zip", null, null);
		System.out.println(isOk);
		
		
		// TODO 1. (用界面？)手工制造软件版本升级包，包括 patch.zip 和 update.xml ( update.xml 在压缩包内)
		// FIN 2. 使用API生成 zip.txt、 MD5, 然后把3个文件放到 patches/${project}/${version} 目录下
		// FIN 3. 更新index.html页面
		
		
		// FIN 4. 运行API检测index.html页面的（多个）新版本并下载 （已下载的不再下载, zip无法下载则下载txt并转码）
		// TODO 5. 校验每个版本的MD5 （若MD5错误则重下该版本）
		// TODO 6. 根据 update.xml步骤进行升级
	}
	
}
