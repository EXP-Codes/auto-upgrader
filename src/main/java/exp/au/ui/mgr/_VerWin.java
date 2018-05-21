package exp.au.ui.mgr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.au.Config;
import exp.au.bean.Version;
import exp.au.utils.UIUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 版本设置窗口
 * </PRE>
 * <B>PROJECT：</B> auto-upgrader
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _VerWin extends PopChildWindow {

	private static final long serialVersionUID = 5311506428590057579L;

	private final static int WIDTH = 300;
	
	private final static int HEIGHT = 240;
	
	private JTextField majorTF;
	
	private JTextField minorTF;
	
	private JButton okBtn;
	
	private JTextField appNameTF;
	
	private JTextField verTF;
	
	protected _VerWin(JTextField appNameTF, JTextField verTF) {
		super("版本号设置", WIDTH, HEIGHT, false, appNameTF, verTF);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.majorTF = new JTextField();
		this.minorTF = new JTextField();
		
		this.okBtn = new JButton("确 认");
		BeautyEyeUtils.setButtonStyle(NormalColor.green, okBtn);
		okBtn.setForeground(Color.BLACK);
		
		this.appNameTF = (JTextField) args[0];
		this.verTF = (JTextField) args[1];
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.getVGridPanel(
				newLabel(),
				SwingUtils.getWEBorderPanel(
						new JLabel("  [主版本号] : "), majorTF, newLabel()), 
				newLabel(),
				SwingUtils.getWEBorderPanel(
						new JLabel("  [次版本号] : "), minorTF, newLabel()),
				newLabel()
		), BorderLayout.NORTH);
		rootPanel.add(okBtn, BorderLayout.CENTER);
	}
	
	/**
	 * 创建占位用的Label
	 * @return
	 */
	private JLabel newLabel() {
		return UIUtils.newLabel(4);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		UIUtils.limitNum(majorTF);
		UIUtils.limitNum(minorTF);
		
		okBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_hide();
			}
		});
	}

	@Override
	protected void AfterView() {
		if(StrUtils.isNotEmpty(majorTF.getText(), minorTF.getText())) {
			return;
		}
		
		final String APP_NAME = appNameTF.getText();
		if(StrUtils.isTrimEmpty(APP_NAME)) {
			return;
		}
		
		// 提取应用程序当前的补丁版本列表
		File appDir = new File(Config.PATCH_PAGE_DIR.concat(APP_NAME));
		File[] verDirs = appDir.listFiles();
		List<Version> vers = new LinkedList<Version>();
		for(File verDir : verDirs) {
			if(verDir.isDirectory()) {
				vers.add(new Version(verDir.getName()));
			}
		}
		Collections.sort(vers);
		
		// 设置默认的版本号为最后一个版本+1
		if(vers.size() > 0) {
			Version last = vers.get(vers.size() - 1);
			majorTF.setText(String.valueOf(last.MAJOR()));
			minorTF.setText(String.valueOf(last.MINOR() + 1));
		}
	}

	@Override
	protected void beforeHide() {
		int major = NumUtils.toInt(majorTF.getText(), 0);
		int minor = NumUtils.toInt(minorTF.getText(), 0);
		Version version = new Version(major, minor);
		verTF.setText(version.VER());	// 回传版本号
	}
	
}
