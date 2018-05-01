package exp.au.utils;

public class Main {

	public static void main(String[] args) {
		/**
		 * 1.若 version.inf 不存在, 则构造目录下所有文件的 md5+ver，生成version.inf文件
		 *   ver通过md5到远程库确认（SAE版本库，python服务开通ftp和db接口）
		 * 2.首先比对程序总版本号, 若已是最新则不做任何处理
		 * 3.校对本地version.inf各个文件的本地版本号 与 远程库的最新版本号
		 *   若存在更高版本，则下载对应文件到本地某位置打包（addToZip）。
		 * 4.备份本地程序
		 * 5.解包升级
		 * 6.若升级失败，则回滚到备份版本
		 */
	}
}
