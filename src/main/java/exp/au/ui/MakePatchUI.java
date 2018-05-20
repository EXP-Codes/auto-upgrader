package exp.au.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.au.Config;
import exp.au.core.server.MakePage;
import exp.au.envm.CmdType;
import exp.au.envm.Params;
import exp.au.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.utils.encode.CompressUtils;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.warp.tpl.Template;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.pnl.ADPanel;
import exp.libs.warp.ui.cpt.win.MainWindow;

/**
 * <PRE>
 * 升级包制作界面
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MakePatchUI extends MainWindow {

	/** 全局唯一ID */
	private static final long serialVersionUID = 8994503171771453009L;

	private final static int WIDTH = 800;
	
	private final static int HEIGHT = 900;
	
	private JTextField patchDirTF;
	
	private JButton patchBtn;
	
	private JTextField appNameTF;
	
	private JTextField verTF;
	
	private JButton verBtn;
	
	private _VerWin verWin;
	
	private JTextField timeTF;
	
	private JTextField md5TF;
	
	private ADPanel<_CmdLine> adPanel;
	
	private JEditorPane console;
	
	private JButton generateBtn;
	
	private static volatile MakePatchUI instance;
	
	private MakePatchUI() {
		super("升级包制作", WIDTH, HEIGHT);
	}
	
	public static MakePatchUI getInstn() {
		if(instance == null) {
			synchronized (MakePatchUI.class) {
				if(instance == null) {
					instance = new MakePatchUI();
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.patchDirTF = new JTextField();
		patchDirTF.setToolTipText("应用补丁的根目录");
		this.patchBtn = newButton("选择");
		
		this.appNameTF = new JTextField();
		appNameTF.setToolTipText("应用名称 (建议使用英文名称)");
		
		this.verTF = new JTextField();
		verTF.setEditable(false);
		verTF.setToolTipText("补丁版本, 格式要求为: \"主版本.次版本\" (如: 3.4)");
		this.verBtn = newButton("设置");
		this.verWin = new _VerWin(verTF);
		
		this.timeTF = new JTextField(TimeUtils.getSysDate());
		timeTF.setEditable(false);
		
		this.md5TF = new JTextField();
		md5TF.setEditable(false);
		md5TF.setToolTipText("补丁校验MD5 (自动生成)");
		
		this.adPanel = new ADPanel<_CmdLine>(_CmdLine.class);
		
		this.console = new JEditorPane();
		console.setEditable(false);
		console.setContentType("text/html");	// 把编辑框设置为支持html的编辑格式
		
		this.generateBtn = new JButton("一 键 生 成 补 丁");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, generateBtn);
		generateBtn.setForeground(Color.BLACK);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getNorthPanel(), BorderLayout.NORTH);
		rootPanel.add(getCenterPanel(), BorderLayout.CENTER);
	}
	
	/**
	 * (北)控制面板
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel ctrlPanel = SwingUtils.getVGridPanel(
				newLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁目录] : "), 
						SwingUtils.getEBorderPanel(patchDirTF, patchBtn), newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [应用名称] : "), appNameTF, newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁版本] : "), 
						SwingUtils.getEBorderPanel(verTF, verBtn), newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [发布时间] : "), timeTF, newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [校验 MD5] : "), md5TF, newLabel()), 
				newLabel()
		);
		return SwingUtils.addBorder(ctrlPanel, "配置补丁信息");
	}
	
	/**
	 * (中心)升级命令编辑面板
	 * @return
	 */
	private JPanel getCenterPanel() {
		return SwingUtils.getSBorderPanel(
				SwingUtils.addBorder(adPanel.getJScrollPanel(), "配置升级步骤"), 
				SwingUtils.getVGridPanel(console, generateBtn));
	}
	
	/**
	 * 创建占位用的Label
	 * @return
	 */
	private JLabel newLabel() {
		return UIUtils.newLabel(6);
	}
	
	/**
	 * 创建控制面板按钮
	 * @param name
	 * @return
	 */
	private JButton newButton(String name) {
		JButton btn = new JButton(StrUtils.concat("  ", name, "  "));
		btn.setForeground(Color.BLACK);
		return btn;
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		patchBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	// 只能选择目录
				if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)) {
					File file = fc.getSelectedFile();
					patchDirTF.setText(file.getAbsolutePath());
				}
			}
		});
		
		verBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				verWin._view();
			}
		});
		
		generateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(chechPatchParams()) {
					generatePatch();
				}
			}
		});
	}
	
	/**
	 * 检查补丁生成参数
	 * @return
	 */
	private boolean chechPatchParams() {
		if(StrUtils.isEmpty(patchDirTF.getText())) {
			SwingUtils.warn("[补丁目录] 不能为空");
			return false;
			
		} else if(StrUtils.isEmpty(appNameTF.getText())) {
			SwingUtils.warn("[应用名称] 不能为空");
			return false;
			
		} else if(StrUtils.isEmpty(verTF.getText())) {
			SwingUtils.warn("[补丁版本] 不能为空");
			return false;
		}
		
		boolean isOk = true;
		List<_CmdLine> cmdLines = adPanel.getLineComponents();
		for(int i = 1, size = cmdLines.size(); i <= size; i++) {
			_CmdLine cmdLine = cmdLines.get(i - 1);
			
			CmdType cmdType = cmdLine.getCmdType();
			if(CmdType.DEL == cmdType) {
				if(StrUtils.isTrimEmpty(cmdLine.getFromPath())) {
					SwingUtils.warn("第 [", i, "] 条 [", cmdType.CH(), "] 命令的源目录不能为空");
					isOk = false;
					break;
				}
				
			} else {
				if(StrUtils.isTrimEmpty(cmdLine.getFromPath())) {
					SwingUtils.warn("第 [", i, "] 条 [", cmdType.CH(), "] 命令的源目录不能为空");
					isOk = false;
					break;
					
				} else if(StrUtils.isTrimEmpty(cmdLine.getToPath())) {
					SwingUtils.warn("第 [", i, "] 条 [", cmdType.CH(), "] 命令的目标目录不能为空");
					isOk = false;
					break;
				}
			}
		}
		return isOk;
	}
	
	/**
	 * 生成升级补丁
	 */
	private void generatePatch() {
		final String APP_NAME = appNameTF.getText();
		final String VERSION = verTF.getText();
		final String PATCH_NAME = StrUtils.concat(APP_NAME, Params.PATCH_TAG, VERSION);
		final String PATCH_ZIP_NAME = PATCH_NAME.concat(Params.ZIP_SUFFIX);
		final String SRC_DIR = patchDirTF.getText();
		final String SNK_DIR = StrUtils.concat(Config.PATCH_PAGE_DIR, APP_NAME, "/", VERSION, "/");
		final String PATCH_DIR = SNK_DIR.concat(PATCH_NAME);
		final String PATCH_ZIP_PATH = SNK_DIR.concat(PATCH_ZIP_NAME);
		
		if(FileUtils.exists(PATCH_ZIP_PATH) && 
				!SwingUtils.confirm("补丁已存在, 是否覆盖 ? ")) {
			return;
		} else {
			timeTF.setText(TimeUtils.getSysDate());
		}
		
		console("正在复制补丁目录到 [", PATCH_DIR, "] ...");
		boolean isOk = FileUtils.copyDirectory(SRC_DIR, PATCH_DIR);
		if(isOk == false) {
			console("复制补丁目录到 [", PATCH_DIR, "] 失败");
			return;
		}
		
		console("正在生成 [", Params.UPDATE_XML, "] 升级步骤文件...");
		isOk = _toUpdateXml(PATCH_DIR, PATCH_ZIP_NAME);
		if(isOk == false) {
			console("生成 [", Params.UPDATE_XML, "] 升级步骤文件失败");
			return;
		}
		
		console("正在生成补丁目录 [", PATCH_DIR, "] 的压缩文件...");
		isOk = CompressUtils.toZip(PATCH_DIR, PATCH_ZIP_PATH);
		isOk &= FileUtils.delete(PATCH_DIR);
		if(isOk == false) {
			console("生成补丁目录 [", PATCH_ZIP_NAME, "] 的压缩文件失败");
			return;
		}

		console("正在生成补丁文件 [", PATCH_ZIP_NAME, "] 的备份文件...");
		String txtPath = PATCH_ZIP_PATH.concat(Params.TXT_SUFFIX);
		isOk = TXTUtils.toTXT(PATCH_ZIP_PATH, txtPath);
		if(isOk == false) {
			console("生成补丁文件 [", PATCH_ZIP_NAME, "] 的备份文件失败");
			return;
		}
		
		console("正在生成补丁文件 [", PATCH_ZIP_NAME, "] 的MD5校验码...");
		String MD5 = CryptoUtils.toFileMD5(PATCH_ZIP_PATH);
		md5TF.setText(MD5);
		String MD5Path = PathUtils.combine(SNK_DIR, Params.MD5_HTML);
		isOk = FileUtils.write(MD5Path, MD5, Charset.ISO, false);
		if(isOk == false) {
			console("生成补丁文件 [", PATCH_ZIP_NAME, "] 的MD5校验码失败");
			return;
		}
		
		console("正在更新补丁管理页面...");
		isOk = MakePage.updatePage();
		if(isOk == false) {
			console("更新补丁管理页面失败");
			return;
		}
		
		console("生成应用程序 [", APP_NAME, "] 的升级补丁完成 (管理页面已更新)");
		SwingUtils.info("生成补丁成功");
	}
	
	/**
	 * 生成升级步骤文件
	 * @param patchDir
	 * @param patchName
	 * @return
	 */
	private boolean _toUpdateXml(String patchDir, String patchName) {
		StringBuilder cmds = new StringBuilder();
		List<_CmdLine> cmdLines = adPanel.getLineComponents();
		for(_CmdLine cmdLine : cmdLines) {
			cmds.append(cmdLine.toXml()).append(Delimiter.CRLF);
		}
		
		Template tmp = new Template(Config.UPDATE_TPL, Charset.UTF8);
		tmp.set("patch-name", patchName);
		tmp.set("cmds", cmds.toString());
		
		String savePath = PathUtils.combine(patchDir, Params.UPDATE_XML);
		return FileUtils.write(savePath, tmp.getContent(), Charset.UTF8, false);
	}
	
	/**
	 * 打印信息到控制台
	 * @param msgs
	 */
	protected void console(Object... msgs) {
		String msg = StrUtils.concat(msgs);
		console.setText(StrUtils.concat(
				"<html>", 
				  "<body>", 
				    "<pre>&nbsp;&nbsp;&nbsp;&nbsp;", 
				      "<font color='blue'>", msg, "</font>", 
				    "</pre>", 
				  "</body>", 
				"</html>"
		));
	}
	
	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeExit() {
		// TODO Auto-generated method stub
		
	}
	
}
