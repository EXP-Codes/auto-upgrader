package exp.au.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exp.au.envm.CmdType;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;

public class _CmdLine extends JPanel {

	private static final long serialVersionUID = -1015365465387409580L;

	private final static String FROM_TIPS = " 从: ";
	
	private final static String TO_TIPS = " 到: ";
	
	private final static String PATCH_DIR_TIPS = "{补丁目录}";
	
	private final static String PATCH_DIR = PATCH_DIR_TIPS.concat("/");
	
	private final static String APP_DIR_TIPS = "{应用目录}";
	
	private final static String APP_DIR = APP_DIR_TIPS.concat("/");
	
	private JComboBox cmds;
	
	private JLabel fromTipsLabel;
	
	private JLabel fromLabel;
	
	private JTextField fromTF;
	
	private JLabel toTipsLabel;
	
	private JLabel toLabel;
	
	private JTextField toTF;
	
	public _CmdLine() {
		super(new BorderLayout());
		this.cmds = SwingUtils.getComboBox(
				CmdType.ADD.CH(), CmdType.RPL.CH(), 
				CmdType.MOV.CH(), CmdType.DEL.CH());
		
		this.fromTipsLabel = new JLabel(FROM_TIPS);
		this.fromLabel = new JLabel(PATCH_DIR);
		this.fromTF = new JTextField();
		fromTF.setToolTipText(getToolTips(PATCH_DIR_TIPS));
		
		this.toTipsLabel = new JLabel(TO_TIPS);
		this.toLabel = new JLabel(APP_DIR);
		this.toTF = new JTextField();
		toTF.setToolTipText(getToolTips(APP_DIR_TIPS));
		
		initLayout();
		initListener();
	}
	
	private void initLayout() {
		add(cmds, BorderLayout.WEST);
		add(SwingUtils.getHGridPanel(
				SwingUtils.getPairsPanel(fromTipsLabel, 
						SwingUtils.getWBorderPanel(fromTF, fromLabel)),
				SwingUtils.getPairsPanel(toTipsLabel, 
						SwingUtils.getWBorderPanel(toTF, toLabel))
		), BorderLayout.CENTER);
		SwingUtils.addBorder(this);
	}
	
	private void initListener() {
		cmds.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){  
		            CmdType cmdType = CmdType.toType((String) e.getItem());
		            if(cmdType == CmdType.ADD || cmdType == CmdType.RPL) {
		            	updateFromTips(true);
		            	updateToTips(false);
		            	tips(cmdType, "从 ", PATCH_DIR_TIPS, " 移动一个 [文件/目录] 到 ", 
		            			APP_DIR_TIPS, " 的相同位置 (存在则覆盖)");
		            	
		            } else if(cmdType == CmdType.MOV) {
		            	updateFromTips(false);
		            	updateToTips(false);
		            	tips(cmdType, "从 ", APP_DIR_TIPS, " 移动一个 [文件/目录] 到 ", 
		            			APP_DIR_TIPS, " 的另一个位置");
		            	
		            } else if(cmdType == CmdType.DEL) {
		            	updateFromTips(false);
		            	updateToTips(true);
		            	tips(cmdType, "从 ", APP_DIR_TIPS, " 删除一个 [文件/目录]");
		            }
		        }  
			}
		});
		
		
		fromTF.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				CmdType cmdType = CmdType.toType(cmds.getSelectedItem().toString());
				if(cmdType == CmdType.ADD || cmdType == CmdType.RPL) {
					
					String data = fromTF.getText();
					char ch = e.getKeyChar();
					toTF.setText(StrUtils.concat(data, ch));
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void updateFromTips(boolean isAddCmd) {
		fromLabel.setText(isAddCmd ? PATCH_DIR : APP_DIR);
		fromTF.setToolTipText(getToolTips(isAddCmd ? PATCH_DIR_TIPS : APP_DIR_TIPS));
	}
	
	private void updateToTips(boolean isDelCmd) {
		toTipsLabel.setText(!isDelCmd ? TO_TIPS : " 删除");
    	toLabel.setVisible(!isDelCmd);
    	toTF.setVisible(!isDelCmd);
	}
	
	private String getToolTips(String dir) {
		return StrUtils.concat("相对于 ", dir, " 的 [文件/目录] 路径");
	}
	
	private void tips(CmdType cmdType, String... msgs) {
		String cmd = StrUtils.concat("<font color='red'>【", cmdType.CH(), "】 命令: </font>");
		String msg = StrUtils.concat(msgs);
		MakeUI.getInstn().tips(cmd, "<font color='blue'>", msg, "</font>");
	}
	
}
