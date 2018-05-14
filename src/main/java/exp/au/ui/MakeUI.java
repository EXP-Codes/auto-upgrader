package exp.au.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

//升级包制作UI
public class MakeUI extends MainWindow {

	public static void main(String[] args) {
		BeautyEyeUtils.init();
		new MakeUI()._view();
	}
	
	/** 全局唯一ID */
	private static final long serialVersionUID = 8994503171771453009L;

	private final static int WIDTH = 600;
	
	private final static int HEIGHT = 600;
	
	private MakeUI() {
		super("升级包制作", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
		
		rootPanel.add(getNorthPanel(), BorderLayout.NORTH);
	}
	
	private JPanel getNorthPanel() {
		JPanel northPanel = SwingUtils.getVGridPanel(
				new JLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁目录] : "), 
						SwingUtils.getEBorderPanel(new JTextField(), createButton("选择")), createLabel()), 
				new JLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [应用名称] : "), new JTextField(), createLabel()), 
				new JLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [补丁版本] : "), 
						SwingUtils.getEBorderPanel(new JTextField("控制格式"), createButton("设置")), createLabel()), 
				new JLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [发布时间] : "), 
						SwingUtils.getEBorderPanel(new JTextField("自动生成"), createButton("设置")), createLabel()), 
				new JLabel(), 
				SwingUtils.getWEBorderPanel(new JLabel("    [校验MD5] : "), new JTextField("自动生成"), createLabel()), 
				new JLabel()
		);
		SwingUtils.addBorder(northPanel);
		return northPanel;
	}
	
	private JLabel createLabel() {
		return new JLabel(StrUtils.multiChar(' ', 6));
	}
	
	private JButton createButton(String name) {
		return new JButton(StrUtils.concat("  ", name, "  "));
	}
	
	private JPanel getSelectPanel() {
		JTextField patchDirTF = new JTextField();
		JButton selectBtn = new JButton("选择");
		return SwingUtils.getEBorderPanel(
				SwingUtils.getPairsPanel("补丁目录/zip包", patchDirTF), selectBtn);
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
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
