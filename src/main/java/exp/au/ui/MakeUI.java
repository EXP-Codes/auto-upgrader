package exp.au.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
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

	private final static int WIDTH = 800;
	
	private final static int HEIGHT = 900;
	
	private JTextField patchDir;
	
	private JButton patchBtn;
	
	private JTextField appNameTF;
	
	private JTextField verDir;
	
	private JButton verBtn;
	
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
		this.patchBtn = createButton("选择");
		
		this.appNameTF = new JTextField();
		appNameTF.setToolTipText("应用名称 (建议使用英文名称)");
		
		this.verDir = new JTextField();
		verDir.setEditable(false);
		verDir.setToolTipText("补丁版本, 格式要求为: \"主版本.次版本\" (如: 3.4)");
		this.verBtn = createButton("设置");
		
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
		// TODO Auto-generated method stub
		
		rootPanel.add(getNorthPanel(), BorderLayout.NORTH);
		rootPanel.add(getCenterPanel(), BorderLayout.CENTER);
	}
	
	private JPanel getNorthPanel() {
		JPanel panel = SwingUtils.getVGridPanel(
				createLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁目录] : "), 
						SwingUtils.getEBorderPanel(patchDir, patchBtn), createLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [应用名称] : "), appNameTF, createLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁版本] : "), 
						SwingUtils.getEBorderPanel(verDir, verBtn), createLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [发布时间] : "), timeTF, createLabel()), 
				SwingUtils.getWEBorderPanel(new JLabel("    [校验 MD5] : "), md5TF, createLabel()), 
				createLabel()
		);
		return SwingUtils.addBorder(panel, "配置补丁信息");
	}
	
	private JPanel getCenterPanel() {
		JScrollPane cmdPanel = SwingUtils.addBorder(
				adPanel.getJScrollPanel(), "配置升级步骤");
		return SwingUtils.getSBorderPanel(cmdPanel, 
				SwingUtils.getVGridPanel(console, generateBtn));
	}
	
	private JLabel createLabel() {
		return new JLabel(StrUtils.multiChar(' ', 6));
	}
	
	private JButton createButton(String name) {
		JButton btn = new JButton(StrUtils.concat("  ", name, "  "));
		btn.setForeground(Color.BLACK);
		return btn;
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
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
