package exp.au.ui.mgr;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.au.Config;
import exp.au.core.mgr.MakePatch;
import exp.au.envm.CmdType;
import exp.au.utils.UIUtils;
import exp.libs.envm.Colors;
import exp.libs.envm.DateFormat;
import exp.libs.envm.Delimiter;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.warp.thread.ThreadPool;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.pnl.ADPanel;
import exp.libs.warp.ui.cpt.win.MainWindow;

/**
 * <PRE>
 * 升级包制作界面
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MakePatchUI extends MainWindow {

	/** 全局唯一ID */
	private static final long serialVersionUID = 8994503171771453009L;

	private final static int WIDTH = 800;
	
	private final static int HEIGHT = 750;
	
	private JTextField patchDirTF;
	
	private JButton selectBtn;
	
	private JTextField appNameTF;
	
	private JComboBox appNameCB;
	
	private JTextField verTF;
	
	private JButton verBtn;
	
	private _VerWin verWin;
	
	private JTextField timeTF;
	
	private JButton timeBtn;
	
	private JTextField MD5TF;
	
	private JButton copyBtn;
	
	private ADPanel<_CmdLine> adPanel;
	
	private JEditorPane console;
	
	/** 模拟进度条的单选按�? */
	private JRadioButton[] stepPB;
	
	private JButton generateBtn;
	
	private ThreadPool tp;
	
	private static volatile MakePatchUI instance;
	
	private MakePatchUI() {
		super("升级包制�?", WIDTH, HEIGHT);
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
		this.selectBtn = newButton("选择");
		
		this.appNameTF = new JTextField();
		appNameTF.setToolTipText("应用名称 (建议使用英文名称)");
		this.appNameCB = initAppNames();
		
		this.verTF = new JTextField();
		verTF.setEditable(false);
		verTF.setToolTipText("补丁版本, 格式要求�?: \"主版�?.次版本\" (�?: 3.4)");
		this.verBtn = newButton("设置");
		this.verWin = new _VerWin(appNameTF, verTF);
		
		this.timeTF = new JTextField();
		this.timeBtn = newButton("更新");
		
		this.MD5TF = new JTextField();
		MD5TF.setEditable(false);
		MD5TF.setToolTipText("补丁校验MD5 (自动生成)");
		this.copyBtn = newButton("复制");
		
		this.adPanel = new ADPanel<_CmdLine>(_CmdLine.class);
		
		this.console = SwingUtils.getHtmlTextArea();
		console.setEditable(false);
		
		this.stepPB = new JRadioButton[7]; {
			stepPB[0] = newRadioButton("目录转移");
			stepPB[1] = newRadioButton("生成步骤");
			stepPB[2] = newRadioButton("补丁打包");
			stepPB[3] = newRadioButton("补丁备份");
			stepPB[4] = newRadioButton("时间水印");
			stepPB[5] = newRadioButton("生成校验");
			stepPB[6] = newRadioButton("生成页面");
		}
		
		this.generateBtn = new JButton("一 �? �? �? �? �?");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, generateBtn);
		generateBtn.setForeground(Colors.BLACK.COLOR());
		
		this.tp = new ThreadPool(2);
	}
	
	private JComboBox initAppNames() {
		JComboBox cb = new JComboBox();
		cb.setToolTipText("已存在补丁的应用列表");
		cb.addItem("");
		
		File pageDir = new File(Config.PATCH_PAGE_DIR);
		File[] appDirs = pageDir.listFiles();
		for(File appDir : appDirs) {
			if(appDir.isDirectory()) {
				cb.addItem(appDir.getName());
			}
		}
		return cb;
	}
	
	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getNorthPanel(), BorderLayout.NORTH);
		rootPanel.add(getCenterPanel(), BorderLayout.CENTER);
	}
	
	/**
	 * (�?)控制面板
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel ctrlPanel = SwingUtils.getVGridPanel(
				newLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁目录] : "), 
						SwingUtils.getEBorderPanel(patchDirTF, selectBtn), newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [应用名称] : "), 
						SwingUtils.getHGridPanel(appNameTF, appNameCB), newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁版本] : "), 
						SwingUtils.getEBorderPanel(verTF, verBtn), newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [发布时间] : "), 
						SwingUtils.getEBorderPanel(timeTF, timeBtn), newLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [校验 MD5] : "), 
						SwingUtils.getEBorderPanel(MD5TF, copyBtn), newLabel()), 
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
				SwingUtils.getVGridPanel(console, _getProgressBar(), generateBtn));
	}
	
	/**
	 * 进度条面�?
	 * @return
	 */
	private JPanel _getProgressBar() {
		return SwingUtils.addBorder(SwingUtils.getHGridPanel(stepPB));
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		selectBtn.addActionListener(new ActionListener() {
			
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
		
		appNameCB.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() != ItemEvent.SELECTED) {  
					return;
				}
				
				String appName = (String) e.getItem();
				if(StrUtils.isNotTrimEmpty(appName)) {
					appNameTF.setText(appName);
				}
			}
		});
		
		verBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				verWin._view();
			}
		});
		
		timeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				timeTF.setText(TimeUtils.getSysDate(DateFormat.YMDHMS));
			}
		});
		
		copyBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String MD5 = MD5TF.getText();
				if(StrUtils.isNotTrimEmpty(MD5)) {
					OSUtils.copyToClipboard(MD5);
					SwingUtils.info("已复制MD5到剪贴板");
				}
			}
		});
		
		generateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkPatchParams()) {
					return;
				}
				generateBtn.setEnabled(false);
				
				tp.execute(new Runnable() {
					
					@Override
					public void run() {
						MakePatch.generate(
								patchDirTF.getText(), appNameTF.getText(), 
								verTF.getText(), timeTF.getText());
						
						generateBtn.setEnabled(true);
					}
				});
			}
		});
	}
	
	/**
	 * 检查补丁生成参�?
	 * @return
	 */
	private boolean checkPatchParams() {
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
					SwingUtils.warn("�? [", i, "] �? [", cmdType.CH(), "] 命令的源目录不能为空");
					isOk = false;
					break;
				}
				
			} else {
				if(StrUtils.isTrimEmpty(cmdLine.getFromPath())) {
					SwingUtils.warn("�? [", i, "] �? [", cmdType.CH(), "] 命令的源目录不能为空");
					isOk = false;
					break;
					
				} else if(StrUtils.isTrimEmpty(cmdLine.getToPath())) {
					SwingUtils.warn("�? [", i, "] �? [", cmdType.CH(), "] 命令的目标目录不能为�?");
					isOk = false;
					break;
				}
			}
		}
		return isOk;
	}
	
	/**
	 * 提取xml格式的命令列�?
	 * @return
	 */
	public String getXmlCmds() {
		StringBuilder cmds = new StringBuilder();
		List<_CmdLine> cmdLines = adPanel.getLineComponents();
		for(_CmdLine cmdLine : cmdLines) {
			cmds.append(cmdLine.toXml()).append(Delimiter.CRLF);
		}
		return cmds.toString();
	}
	
	/**
	 * 更新MD5�?
	 * @param MD5
	 */
	public void updatMD5(String MD5) {
		MD5TF.setText(MD5 == null ? "" : MD5);
	}
	
	/**
	 * 更新进度条面板的状�?
	 * @param step 执行步骤索引
	 * @param isOk 是否执行成功
	 */
	public void updateProgressBar(int step, boolean isOk) {
		if(step < 0 || step >= stepPB.length) {
			return;
		}
		
		stepPB[step].setSelected(true);
		stepPB[step].setForeground(isOk ? 
				Colors.SEA_GREEN.COLOR() : Colors.PURPLE.COLOR());
	}
	
	/**
	 * 还原进度条面板状�?
	 */
	public void clearProgressBar() {
		for(int i = 0; i < stepPB.length; i++) {
			stepPB[i].setSelected(false);
			stepPB[i].setForeground(Colors.BLACK.COLOR());
		}
	}
	
	@Override
	protected void AfterView() {
		timeBtn.doClick();
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeExit() {
		tp.shutdown();
	}
	
	/**
	 * 打印信息到控制台
	 * @param msgs
	 */
	public void toConsole(Object... msgs) {
		String msg = StrUtils.concat(msgs);
		console.setText(StrUtils.concat(
				"<html>", 
				  "<body>", 
				    "<pre>&nbsp;&nbsp;&nbsp;&nbsp;", 
				      "<font color='blue' size='3'>", msg, "</font>", 
				    "</pre>", 
				  "</body>", 
				"</html>"
		));
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
		btn.setForeground(Colors.BLACK.COLOR());
		return btn;
	}
	
	/**
	 * 创建进度条面板的单选按�?
	 * @param name
	 * @return
	 */
	private JRadioButton newRadioButton(String name) {
		JRadioButton btn = new JRadioButton(name);
		btn.setEnabled(false);
		btn.setForeground(Colors.BLACK.COLOR());
		return btn;
	}
	
}
