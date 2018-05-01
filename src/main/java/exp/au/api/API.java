package exp.au.api;

import java.util.List;

import exp.au.bean.ldm.UpdateInfo;
import exp.au.bean.ldm.UpdateVersion;

public class API {

	/*
	 * 1.先检查是否存在新版本
	 * 2.检查版本跨度
	 * 3.下载升级包
	 * 4.检查MD5
	 * 5.根据升级指导文件升级（删除、增加、替换文件）
	 */
	protected API() {}
	
	/**
	 * 获取升级信息列表
	 * @return
	 */
	public static UpdateInfo getUpdateInfo() {
		return null;
	}
	
	/**
	 * 检查是否存在新版本
	 * @return
	 */
	public static boolean hasNewVersion() {
		// TODO
		return true;
	}
	
	public static List<UpdateVersion> getUpdateVersions() {
		// 获取版本跨度
		return null;
	}
	
	/**
	 * 根据版本跨度下载升级包
	 * @return
	 */
	public static boolean download() {
		
		return true;
	}
	
	/**
	 * 下载TXT形式的升级包
	 * @return
	 */
	public static String downloadTXT() {
		return "";
	}
	
	
	
}
