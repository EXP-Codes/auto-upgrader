package exp.au.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.au.bean.Version;
import exp.au.utils.UIUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 版本设置窗口
 * </PRE>
 * <B>PROJECT：</B> auto-upgrade-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _VerWin extends PopChildWindow {

	private static final long serialVersionUID = 5311506428590057579L;

	private final static int WIDTH = 300;
	
	private final static int HEIGHT = 260;
	
	private JTextField majorTF;
	
	private JTextField minorTF;
	
	private JButton okBtn;
	
	private JTextField verTF;
	
	protected _VerWin(JTextField verTF) {
		super("版本号设置", WIDTH, HEIGHT, false, verTF);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.majorTF = new JTextField();
		this.minorTF = new JTextField();
		
		this.okBtn = new JButton("确 认");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, okBtn);
		
		this.verTF = (JTextField) args[0];
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.getVGridPanel(
				newLabel(),
				SwingUtils.getWEBorderPanel(new JLabel("  [主版本号] : "), majorTF, newLabel()), 
				newLabel(),
				SwingUtils.getWEBorderPanel(new JLabel("  [次版本号] : "), minorTF, newLabel()),
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
				int major = NumUtils.toInt(majorTF.getText(), 0);
				int minor = NumUtils.toInt(minorTF.getText(), 0);
				Version version = new Version(major, minor);
				verTF.setText(version.VER());
				_hide();
			}
		});
	}

	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
}
