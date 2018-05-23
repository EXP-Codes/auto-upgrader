package exp.au.ui.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.au.Config;
import exp.au.bean.PatchInfo;
import exp.au.bean.Version;
import exp.au.core.client.DownPatch;
import exp.au.utils.UIUtils;
import exp.libs.envm.Colors;
import exp.libs.envm.DateFormat;
import exp.libs.envm.Delimiter;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.warp.thread.ThreadPool;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;
import exp.libs.warp.ui.layout.VFlowLayout;

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
	
	private JPanel verPanel;
	
	private JScrollPane scrollPanel;
	
	private Map<PatchInfo, _PatchLine> patches;
	
	private JTextArea consoleTA;
	
	private JButton upgradeBtn;
	
	private ThreadPool tp;
	
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
		
		this.checkBtn = new JButton("   检 查 更 新   ");
		BeautyEyeUtils.setButtonStyle(NormalColor.green, checkBtn);
		checkBtn.setForeground(Colors.BLACK.COLOR());
		
		this.verPanel = new JPanel(new VFlowLayout());
		this.scrollPanel = SwingUtils.addAutoScroll(verPanel);
		
		this.consoleTA = new JTextArea();
		consoleTA.setEditable(false);
		
		this.upgradeBtn = new JButton("一 键 升 级");
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, upgradeBtn);
		upgradeBtn.setForeground(Colors.BLACK.COLOR());
		upgradeBtn.setEnabled(false);
		
		this.tp = new ThreadPool(2);
		this.patches = new LinkedHashMap<PatchInfo, _PatchLine>();
	}
	
	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getNorthPanel(), BorderLayout.NORTH);
		rootPanel.add(getCenterPanel(), BorderLayout.CENTER);
	}
	
	private JPanel getNorthPanel() {
		return SwingUtils.addBorder(
				SwingUtils.getNSBorderPanel(
						newLabel(), 
						SwingUtils.getEBorderPanel(
								SwingUtils.getVGridPanel(
									SwingUtils.getPairsPanel("应用名称", appNameTF), 
									SwingUtils.getPairsPanel("当前版本", appVerTF)
						), checkBtn),
						newLabel()
				), "版本检查");
	}
	
	private JPanel getCenterPanel() {
		return SwingUtils.addBorder(
				SwingUtils.getVGridPanel(
						scrollPanel, 
						SwingUtils.getSBorderPanel(SwingUtils.addBorder(
								SwingUtils.addAutoScroll(consoleTA), "升级日志"), 
						upgradeBtn)
				), "版本列表");
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		checkBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				upgradeBtn.setEnabled(false);
				
				tp.execute(new Runnable() {
					
					@Override
					public void run() {
						
						toConsole("正在从管理页面提取补丁列表信息...");
						updatePatches();	// 更新补丁列表
						filterPatches();	// 过滤旧版本补丁
						toConsole("从管理页面提取补丁列表信息完成, 待升级补丁数: ", patches.size());
						
						// 刷新补丁列表面板
						SwingUtils.repaint(scrollPanel);
						SwingUtils.toEnd(scrollPanel, true);
						upgradeBtn.setEnabled(true);
					}
				});
			}
		});
		
		
		upgradeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(patches.isEmpty()) {
					toConsole("已经是最新版本了, 无需升级");
					SwingUtils.warn("已经是最新版本了, 无需升级");
					return;
				}
				
				upgradeBtn.setEnabled(false);
				tp.execute(new Runnable() {
					
					@Override
					public void run() {
						downPatches();		// 下载补丁包
						installPatches();	// 安装补丁包
						
						upgradeBtn.setEnabled(true);
					}
				});
			}
		});
	}
	
	/**
	 * 更新补丁列表
	 */
	private void updatePatches() {
		patches.clear();
		verPanel.removeAll();
		
		List<PatchInfo> patchInfos = DownPatch.getPatchInfos(appNameTF.getText());
		for(PatchInfo patchInfo : patchInfos) {
			_PatchLine patchLine =newPatchLine(patchInfo);
			
			verPanel.add(patchLine);
			patches.put(patchInfo, patchLine);
		}
	}
	
	/**
	 * 根据当前版本号对旧补丁进行移除
	 */
	private void filterPatches() {
		final Version CUR_VER = new Version(appVerTF.getText());
		Iterator<PatchInfo> patchInfoIts = patches.keySet().iterator();
		while(patchInfoIts.hasNext()) {
			PatchInfo patchInfo = patchInfoIts.next();
			
			// 小于等于应用程序当前版本号的补丁, 进行标记并移除
			if(CUR_VER.compareTo(patchInfo.getVersion()) >= 0) {
				_PatchLine patchLine = patches.get(patchInfo);
				patchLine.markDown(false);
				patchLine.markInstall(false);
				patchInfoIts.remove();	// 移除补丁信息
			}
		}
	}
	
	/**
	 * 下载补丁包
	 */
	private void downPatches() {
		Iterator<PatchInfo> patchInfoIts = patches.keySet().iterator();
		while(patchInfoIts.hasNext()) {
			PatchInfo patchInfo = patchInfoIts.next();
			
			if(DownPatch.download(patchInfo)) {
				_PatchLine patchLine = patches.get(patchInfo);
				patchLine.markDown(true);
			}
		}
	}
	
	/**
	 * 安装补丁包
	 */
	private void installPatches() {
		// FIXME: 若有一个失败, 则不再往后
		// 一边安装一边更新当前版本
	}
	
	@Override
	protected void AfterView() {
		if(taskAppVerInfo() == false) {
			toConsole("提取当前版本信息失败, 无法升级 (请确保程序至少运行过一次)");
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
		tp.shutdown();
	}
	
	/**
	 * 打印信息到控制台
	 * @param msgs
	 */
	public void toConsole(Object... msgs) {
		String time = StrUtils.concat("[", TimeUtils.getSysDate(DateFormat.HMS), "] ");
		String msg = StrUtils.concat(msgs);
		
		consoleTA.append(StrUtils.concat(time, msg, Delimiter.CRLF));
		SwingUtils.toEnd(consoleTA);
	}

	/**
	 * 标记补丁为已下载
	 * @param patchInfo
	 */
	protected void markDown(PatchInfo patchInfo) {
		_PatchLine patchLine = patches.get(patchInfo);
		if(patchLine != null) {
			patchLine.markDown(true);
		}
	}
	
	/**
	 * 标记补丁为已安装
	 * @param patchInfo
	 */
	protected void markInstall(PatchInfo patchInfo) {
		_PatchLine patchLine = patches.get(patchInfo);
		if(patchLine != null) {
			patchLine.markInstall(true);
		}
	}
	
	/**
	 * 创建补丁行组件
	 * @param patchInfo 补丁信息
	 * @return 补丁行组件
	 */
	private _PatchLine newPatchLine(PatchInfo patchInfo) {
		_PatchLine patchLine = new _PatchLine(
				patchInfo.getPatchName(), patchInfo.getReleaseTime());
		return patchLine;
	}
	
	/**
	 * 创建占位用的Label
	 * @return
	 */
	private JLabel newLabel() {
		return UIUtils.newLabel(4);
	}
	
}
