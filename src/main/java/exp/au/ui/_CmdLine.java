package exp.au.ui;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import exp.au.envm.CmdType;
import exp.libs.warp.ui.SwingUtils;

public class _CmdLine extends JPanel {

	private static final long serialVersionUID = -1015365465387409580L;

	private JComboBox cmds;
	
	public _CmdLine() {
		super(new BorderLayout());
		this.cmds = SwingUtils.getComboBox(CmdType.ADD.CMD(), 
				CmdType.MOV.CMD(), CmdType.DEL.CMD());
		
		
		// 布局
//		add(SwingUtils.getHGridPanel(
//				SwingUtils.getEBorderPanel(
//						SwingUtils.getPairsPanel("昵称", usernameTF), loginBtn.getButton()),
//				SwingUtils.getEBorderPanel(
//						SwingUtils.getPairsPanel("投喂房号", roomTF), roomBtn)), 
//				BorderLayout.CENTER);
		add(cmds, BorderLayout.EAST);
		SwingUtils.addBorder(this);
	}
	
	
}
