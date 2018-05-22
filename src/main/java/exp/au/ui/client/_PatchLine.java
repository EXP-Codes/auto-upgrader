package exp.au.ui.client;

import java.awt.BorderLayout;
import java.sql.Struct;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import exp.libs.warp.ui.SwingUtils;

class _PatchLine extends JPanel {

	private static final long serialVersionUID = 3115712607344507109L;

	private JLabel patchName;
	
	private JRadioButton downBtn;
	
	private JRadioButton installBtn;
	
	protected _PatchLine(String patchName) {
		super(new BorderLayout());
		
		this.patchName = new JLabel(patchName);
		
		this.downBtn = new JRadioButton("  已下载  ");
		downBtn.setEnabled(false);
		
		this.installBtn = new JRadioButton("  已安装  ");
		installBtn.setEnabled(false);
		
		initLayout();
		initListener();
	}

	private void initLayout() {
		add(patchName, BorderLayout.CENTER);
		add(SwingUtils.getHGridPanel(downBtn, installBtn), BorderLayout.EAST);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		
	}
	
}
