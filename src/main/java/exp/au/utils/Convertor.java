package exp.au.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.BODHUtils;

// 任意文件与txt文件互转: 用于在线升级
public class Convertor {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(Convertor.class);
	
	private final static int BUFFER_SIZE = 4096;
	
	public final static String TMP_DIR = "/tmp/";
	
	private final static String TXT_SUFFIX = ".txt";
	
	/** 私有化构造函数 */
	protected Convertor() {}
	
	public static void main(String[] args) {
		System.out.println(toTXTs("./lib"));
		System.out.println(toFiles("./lib/tmp"));
	}
	
	public static boolean toTXTs(String srcPath) {
		boolean isOk = true;
		File dir = new File(srcPath);
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				if(file.isDirectory()) {
					continue;
				}
				isOk &= toTXT(file);
			}
		} else {
			isOk = toTXT(dir);
		}
		return isOk;
	}
	
	private static boolean toTXT(File file) {
		String srcPath = file.getAbsolutePath();
		String txtPath = toTxtPath(srcPath);
		return toTXT(srcPath, txtPath);
	}
	
	private static String toTxtPath(String srcPath) {
		File file = new File(srcPath);
		String parentPath = file.getParent().concat(TMP_DIR);
		String txtName = file.getName().concat(TXT_SUFFIX);
		return parentPath.concat(txtName);
	}
	
	private static boolean toTXT(String srcPath, String txtPath) {
		boolean isOk = false;
		File txtFile = FileUtils.createFile(txtPath);
		try {
			FileInputStream fis = new FileInputStream(new File(srcPath));
			FileOutputStream fos = new FileOutputStream(txtFile);
			byte[] buff = new byte[BUFFER_SIZE];
			int rc = 0;
			while ((rc = fis.read(buff, 0, BUFFER_SIZE)) > 0) {
				String hex = BODHUtils.toHex(buff, 0, rc);
				fos.write(hex.getBytes(Charset.ISO));
			}
			fos.flush();
			fos.close();
			fis.close();
			isOk = true;
			
		} catch (Exception e) {
			log.error("把文件编码为TXT失败: {}.", srcPath, e);
		}
		return isOk;
	}
	
	public static boolean toFiles(String txtPath) {
		boolean isOk = true;
		File dir = new File(txtPath);
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				if(file.isDirectory()) {
					continue;
				}
				isOk &= toFile(file);
			}
		} else {
			isOk = toFile(dir);
		}
		return isOk;
	}
	
	private static boolean toFile(File txt) {
		String txtPath = txt.getAbsolutePath();
		String snkPath = toFilePath(txtPath);
		return toFile(txtPath, snkPath);
	}
	
	private static String toFilePath(String txtPath) {
		File file = new File(txtPath);
		String parentPath = file.getParent().concat(TMP_DIR);
		String fileName = file.getName().replaceFirst(TXT_SUFFIX.concat("$"), "");
		return parentPath.concat(fileName);
	}
	
	private static boolean toFile(String txtPath, String snkPath) {
		boolean isOk = false;
		File snkFile = FileUtils.createFile(snkPath);
		try {
			FileInputStream fis = new FileInputStream(new File(txtPath));
			FileOutputStream fos = new FileOutputStream(snkFile);
			byte[] buff = new byte[BUFFER_SIZE];
			int rc = 0;
			while ((rc = fis.read(buff, 0, BUFFER_SIZE)) > 0) {
				String hex = new String(buff, 0, rc, Charset.ISO);
				fos.write(BODHUtils.toBytes(hex));
			}
			fos.flush();
			fos.close();
			fis.close();
			isOk = true;
			
		} catch (Exception e) {
			log.error("恢复编码的TXT文件失败: {}.", txtPath, e);
		}
		return isOk;
	}
	
}
