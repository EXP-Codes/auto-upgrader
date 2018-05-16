package exp.au.ui;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exp.au.envm.CmdType;
import exp.libs.warp.ui.SwingUtils;

public class _CmdLine extends JPanel {

	private static final long serialVersionUID = -1015365465387409580L;

	private JComboBox cmds;
	
	public _CmdLine() {
		super(new BorderLayout());
		this.cmds = SwingUtils.getComboBox(CmdType.ADD.CH(), 
				CmdType.MOV.CH(), CmdType.DEL.CH());
		
		// 布局 (根据命令提示文本框的相对位置是基于补丁还是程序)
		add(cmds, BorderLayout.WEST);
		add(SwingUtils.getHGridPanel(
				SwingUtils.getPairsPanel("从", new JTextField("文件/目录")),
				SwingUtils.getPairsPanel("到", new JTextField("文件/目录"))
		), BorderLayout.CENTER);
		SwingUtils.addBorder(this);
	}
	
	
}
