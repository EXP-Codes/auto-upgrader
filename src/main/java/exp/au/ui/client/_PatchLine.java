package exp.au.ui.client;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;

/**
 * <PRE>
 * 补丁升级步骤的命令行配置组件
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _PatchLine extends JPanel {

	private static final long serialVersionUID = 3115712607344507109L;

	private final static String UNDOWN = "  未下载  ";
	
	private final static String DOWN = "  已下载  ";
	
	private final static String UNISTALL = "  未安装  ";
	
	private final static String INSTALL = "  已安装  ";
	
	private JLabel patchLabel;
	
	private JRadioButton downBtn;
	
	private JRadioButton installBtn;
	
	protected _PatchLine(String patchName, String releaseTime) {
		super(new BorderLayout());
		
		String tagName = StrUtils.concat("[", releaseTime, "]  ", patchName);
		this.patchLabel = new JLabel(tagName);
		
		this.downBtn = new JRadioButton(UNDOWN);
		downBtn.setEnabled(false);
		
		this.installBtn = new JRadioButton(UNISTALL);
		installBtn.setEnabled(false);
		
		initLayout();
	}

	/**
	 * 初始化布局
	 */
	private void initLayout() {
		add(patchLabel, BorderLayout.CENTER);
		add(SwingUtils.getHGridPanel(downBtn, installBtn), BorderLayout.EAST);
		SwingUtils.addBorder(this);
	}

	/**
	 * 标记为已下载
	 * @param toLog 是否打印日志
	 */
	protected void markDown() {
		downBtn.setText(DOWN);
		downBtn.setSelected(true);
		downBtn.setForeground(Color.BLUE);
	}
	
	/**
	 * 标记为已安装
	 * @param toLog 是否打印日志
	 */
	protected void markInstall() {
		installBtn.setText(INSTALL);
		installBtn.setSelected(true);
		installBtn.setForeground(Color.BLUE);
		patchLabel.setForeground(Color.BLUE);
	}
	
}
