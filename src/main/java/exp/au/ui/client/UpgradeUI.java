package exp.au.ui.client;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.au.Config;
import exp.au.utils.UIUtils;
import exp.libs.envm.Colors;
import exp.libs.utils.io.FileUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

//升级包管理&安装UI
// 若程序没有启动过导致没有生成当前版本信息, 则不进行升级
// 客户端要留一个命令行版本(用于linux)

//检查版本
//  版本列表 (历史版本标灰， 当前版本标红， 未升级版本标蓝)

// 下载补丁
//  版本列表 (历史版本标灰， 当前版本标红， 未升级版本标绿)
//  版本列表新增两列：下载状态， 安装状态

// 控制台日志(下载时需打印MD5校验情况)
// 一键升级(升级过程中，当前版本标色变化)
public class UpgradeUI extends MainWindow {

	private static final long serialVersionUID = 1802740672496217291L;

	private final static int WIDTH = 700;
	
	private final static int HEIGHT = 700;
	
	private JTextField appNameTF;
	
	private JTextField appVerTF;
	
	private JButton checkBtn;
	
	private JButton upgradeBtn;
	
	private static volatile UpgradeUI instance;
	
	private UpgradeUI() {
		super("软件升级", WIDTH, HEIGHT);
	}
	
	public static UpgradeUI getInstn() {
		if(instance == null) {
			synchronized (UpgradeUI.class) {
				if(instance == null) {
					instance = new UpgradeUI();
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.appNameTF = new JTextField();
		appNameTF.setEditable(false);
		
		this.appVerTF = new JTextField();
		appVerTF.setEditable(false);
		
		this.checkBtn = new JButton("检 查 版 本");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, checkBtn);
		checkBtn.setForeground(Colors.BLACK.COLOR());
		
		this.upgradeBtn = new JButton("一 键 升 级");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, upgradeBtn);
		upgradeBtn.setForeground(Colors.BLACK.COLOR());
	}
	
	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getNorthPanel(), BorderLayout.NORTH);
	}
	
	private JPanel getNorthPanel() {
		return SwingUtils.getEBorderPanel(
				SwingUtils.getVGridPanel(
						newLabel(),
						SwingUtils.getWEBorderPanel(
								new JLabel("    [应用名称] : "), appNameTF, newLabel()),
						newLabel(),
						SwingUtils.getWEBorderPanel(
								new JLabel("    [当前版本] : "), appVerTF, newLabel()),
						newLabel()
				), SwingUtils.getHGridPanel(checkBtn, newLabel()));
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void AfterView() {
		if(taskAppVerInfo() == false) {
			SwingUtils.warn("提取当前版本信息失败, 无法升级\r\n(请确保程序至少运行过一次)");
			System.exit(0);
		}
	}
	
	/**
	 * 提取应用程序的当前版本信息
	 * @return 是否提取成功
	 */
	private boolean taskAppVerInfo() {
		List<String> lines = FileUtils.readLines(
				Config.LAST_VER_PATH, Config.DEFAULT_CHARSET);
		
		boolean isOk = false;
		if(lines.size() == 2) {
			appNameTF.setText(lines.get(0).trim());
			appVerTF.setText(lines.get(1).trim());
			isOk = true;
		}
		return isOk;
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeExit() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 创建占位用的Label
	 * @return
	 */
	private JLabel newLabel() {
		return UIUtils.newLabel(4);
	}
	
}
