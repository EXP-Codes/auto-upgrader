package exp.au.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exp.au.envm.CmdType;
import exp.libs.warp.ui.SwingUtils;

public class _CmdLine extends JPanel {

	private static final long serialVersionUID = -1015365465387409580L;

	private JComboBox cmds;
	
	private JLabel fromLabel;
	
	private JLabel toLabel;
	
	public _CmdLine() {
		super(new BorderLayout());
		this.cmds = SwingUtils.getComboBox(CmdType.ADD.CH(), CmdType.RPL.CH(), 
				CmdType.MOV.CH(), CmdType.DEL.CH());
		this.fromLabel = new JLabel("{patch-dir}/");
		this.toLabel = new JLabel("{app-dir}/");
		
		// 布局 (根据命令提示文本框的相对位置是基于补丁还是程序)
		add(cmds, BorderLayout.WEST);
		add(SwingUtils.getHGridPanel(
				SwingUtils.getPairsPanel("从", SwingUtils.getWBorderPanel(new JTextField("文件/目录"), fromLabel)),
				SwingUtils.getPairsPanel("到", SwingUtils.getWBorderPanel(new JTextField("文件/目录"), toLabel))
		), BorderLayout.CENTER);
		SwingUtils.addBorder(this);
		
		cmds.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED){  
		            CmdType cmd = CmdType.toType((String) e.getItem());
		            if(cmd == CmdType.ADD) {	// 监听，， 默认ADD/RPL 的 源宿路径是相同的
		            	fromLabel.setText("{patch-dir}/");
		            	toLabel.setText("{app-dir}/");
		            	
		            } else if(cmd == CmdType.MOV) {
		            	fromLabel.setText("{app-dir}/");
		            	toLabel.setText("{app-dir}/");
		            	
		            } else if(cmd == CmdType.DEL) {
		            	fromLabel.setText("{app-dir}/");
		            	toLabel.setText("unuse:");
		            	
		            }
		        }  
			}
		});
	}
	
	
}
