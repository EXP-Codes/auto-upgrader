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
import exp.au.core.server.GenNative;
import exp.au.envm.Params;
import exp.au.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.envm.FileType;
import exp.libs.utils.encode.CompressUtils;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.format.TXTUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.tpl.Template;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.pnl.ADPanel;
import exp.libs.warp.ui.cpt.win.MainWindow;

//升级包制作UI
public class MakeUI extends MainWindow {

	public static void main(String[] args) {
		BeautyEyeUtils.init();
		MakeUI.getInstn()._view();
	}
	
	/** 全局唯一ID */
	private static final long serialVersionUID = 8994503171771453009L;

	private final static String TPL_PATH = "/exp/au/core/server/update.tpl";
	
	private final static int WIDTH = 800;
	
	private final static int HEIGHT = 900;
	
	private JTextField patchDir;
	
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
	
	private static volatile MakeUI instance;
	
	private MakeUI() {
		super("升级包制作", WIDTH, HEIGHT);
	}
	
	public static MakeUI getInstn() {
		if(instance == null) {
			synchronized (MakeUI.class) {
				if(instance == null) {
					instance = new MakeUI();
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.patchDir = new JTextField();
		patchDir.setToolTipText("应用补丁的根目录");
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
						SwingUtils.getEBorderPanel(patchDir, patchBtn), newLabel()), 
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
					patchDir.setText(file.getAbsolutePath());
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
				// TODO 检查参数是否为空
				generatePatch();
			}
		});
	}
	
	/**
	 * 生成补丁文件, 并拷贝到发布目录
	 */
	private boolean generatePatch() {
		// TODO 在控制台显示步骤
		
		boolean isOk = generateCmdFile();
		if(isOk == false) {
			return isOk;
		}
		
		String zipPath = toZipPatch();
		isOk = StrUtils.isNotEmpty(zipPath);
		if(isOk == false) {
			return isOk;
		}
		
		isOk = toTxtAndMD5(zipPath);
		if(isOk == false) {
			return isOk;
		}
		return isOk;
	}
	
	/**
	 * 生成命令文件
	 * @return
	 */
	private boolean generateCmdFile() {
		StringBuilder cmds = new StringBuilder();
		List<_CmdLine> cmdLines = adPanel.getLineComponents();
		for(_CmdLine cmdLine : cmdLines) {
			cmds.append(cmdLine.toXml()).append(Delimiter.CRLF);
		}
		
		Template tmp = new Template(TPL_PATH, Charset.UTF8);
		tmp.set("cmds", cmds.toString());
		
		String savePath = PathUtils.combine(patchDir.getText(), Params.UPDATE_XML);
		return FileUtils.write(savePath, tmp.getContent(), Charset.UTF8, false);
	}
	
	private String toZipPatch() {
		String srcPath = patchDir.getText();
		String snkDir = PathUtils.getParentDir(srcPath);
		String zipName = StrUtils.concat(appNameTF.getText(), "-patch-", verTF.getText(), ".zip");
		String zipPath = PathUtils.combine(snkDir, zipName);
		boolean isOk = CompressUtils.toZip(srcPath, zipPath);
		return (isOk ? zipPath : "");
	}
	
	// FIXME 参数拆开，和上面的方法一起
	private boolean toTxtAndMD5(String zipPath) {
		boolean isOk = false;
		final String REGEX = "(([^/\\\\]*?)-patch-(\\d\\.\\d).zip)";
		List<String> groups = RegexUtils.findGroups(zipPath, REGEX);
		if(groups.size() == 4) {
			String zipName = groups.get(1);
			String appName = groups.get(2);
			String version = groups.get(3);
			String dir = StrUtils.concat(Config.PATCH_PAGE_DIR, appName, "/", version, "/");
			String path = dir.concat(zipName);
			isOk = FileUtils.copyFile(zipPath, path);
			
			String txtPath = TXTUtils.toTXT(path);
			String MD5 = CryptoUtils.toFileMD5(path);
			String MD5Path = PathUtils.combine(PathUtils.getParentDir(txtPath), Params.MD5_HTML);
			isOk &= FileUtils.write(MD5Path, MD5, Charset.ISO, false);
		}
		return isOk;
	}
	
	protected void tips(Object... msgs) {
		String msg = StrUtils.concat(msgs);
		console.setText(StrUtils.concat(
				"<html>", 
				  "<body>", 
				    "<pre>&nbsp;&nbsp;&nbsp;&nbsp;", 
				      msg, 
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
