package exp.au.ui.client;

import javax.swing.JPanel;

import exp.libs.warp.ui.cpt.win.MainWindow;

//升级包管理&安装UI
// 若程序没有启动过导致没有生成当前版本信息, 则不进行升级
// 客户端要留一个命令行版本(用于linux)
public class UpgradeUI extends MainWindow {

	private static final long serialVersionUID = 1802740672496217291L;

	private final static int WIDTH = 700;
	
	private final static int HEIGHT = 700;
	
	
	// 检查版本
	//  版本列表 (历史版本标灰， 当前版本标红， 未升级版本标蓝)
	
	// 下载补丁
	//  版本列表 (历史版本标灰， 当前版本标红， 未升级版本标绿)
	//  版本列表新增两列：下载状态， 安装状态
	
	// 控制台日志(下载时需打印MD5校验情况)
	// 一键升级(升级过程中，当前版本标色变化)
	
	
	@Override
	protected void initComponents(Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
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
