package exp.au.ui.mgr;

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

/**
 * <PRE>
 * 命令行组件
 * </PRE>
 * <B>PROJECT : </B> auto-upgrader
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-20
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _CmdLine extends JPanel {

	private static final long serialVersionUID = -1015365465387409580L;

	private final static String FROM_TIPS = " �?: ";
	
	private final static String TO_TIPS = " �?: ";
	
	private final static String PATCH_DIR_TIPS = "{补丁目录}";
	
	private final static String PATCH_DIR = PATCH_DIR_TIPS.concat("/");
	
	private final static String APP_DIR_TIPS = "{应用目录}";
	
	private final static String APP_DIR = APP_DIR_TIPS.concat("/");
	
	private JComboBox cmdCB;
	
	private JLabel fromTipsLabel;
	
	private JLabel fromLabel;
	
	private JTextField fromTF;
	
	private JLabel toTipsLabel;
	
	private JLabel toLabel;
	
	private JTextField toTF;
	
	public _CmdLine() {
		super(new BorderLayout());
		
		this.cmdCB = SwingUtils.getComboBox(
				CmdType.ADD.CH(), CmdType.RPL.CH(), 
				CmdType.MOV.CH(), CmdType.DEL.CH());
		
		this.fromTipsLabel = new JLabel(FROM_TIPS);
		this.fromLabel = new JLabel(PATCH_DIR);
		this.fromTF = new JTextField(20);	// 限制长度, 避免其根据内容自动延�?
		fromTF.setToolTipText(getToolTips(PATCH_DIR_TIPS));
		
		this.toTipsLabel = new JLabel(TO_TIPS);
		this.toLabel = new JLabel(APP_DIR);
		this.toTF = new JTextField(20);		// 限制长度, 避免其根据内容自动延�?
		toTF.setToolTipText(getToolTips(APP_DIR_TIPS));
		
		initLayout();
		initListener();
	}
	
	private void initLayout() {
		add(cmdCB, BorderLayout.WEST);
		add(SwingUtils.getHGridPanel(
				SwingUtils.getPairsPanel(fromTipsLabel, 
						SwingUtils.getWBorderPanel(fromTF, fromLabel)),
				SwingUtils.getPairsPanel(toTipsLabel, 
						SwingUtils.getWBorderPanel(toTF, toLabel))
		), BorderLayout.CENTER);
		SwingUtils.addBorder(this);
	}
	
	private void initListener() {
		cmdCB.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() != ItemEvent.SELECTED) {  
					return;
				}
				
	            CmdType cmdType = CmdType.toType((String) e.getItem());
	            if(cmdType == CmdType.ADD) {
	            	updateFromTips(true);
	            	updateToTips(false);
	            	tips(cmdType, "�? ", PATCH_DIR_TIPS, " 移动一�? [文件/目录] �? ", 
	            			APP_DIR_TIPS, " 的相同位�? (若存在则替换,不存在则新增)");
	            	
	            } else if(cmdType == CmdType.RPL) {
		            	updateFromTips(true);
		            	updateToTips(false);
		            	tips(cmdType, "�? ", PATCH_DIR_TIPS, " 移动一�? [文件/目录] �? ", 
		            			APP_DIR_TIPS, " 的相同位�? (仅存在时替换,不存在不操作)");
	            	
	            } else if(cmdType == CmdType.MOV) {
	            	updateFromTips(false);
	            	updateToTips(false);
	            	tips(cmdType, "�? ", APP_DIR_TIPS, " 移动一�? [文件/目录] �? ", 
	            			APP_DIR_TIPS, " 的另一个位�?");
	            	
	            } else if(cmdType == CmdType.DEL) {
	            	updateFromTips(false);
	            	updateToTips(true);
	            	tips(cmdType, "�? ", APP_DIR_TIPS, " 删除一�? [文件/目录]");
	            }
			}
		});
		
		
		fromTF.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				CmdType cmdType = getCmdType();
				if(cmdType == CmdType.ADD || cmdType == CmdType.RPL) {
					
					String data = fromTF.getText();
					char ch = e.getKeyChar();
					if(ch < 32 || ch == 127) {
						// UNDO: <32为控制信号字�?, 127为Del
						
					} else {
						data = data + ch;
					}
					toTF.setText(data);
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
		return StrUtils.concat("相对�? ", dir, " �? [文件/目录] 路径");
	}
	
	private void tips(CmdType cmdType, String... msgs) {
		String cmd = StrUtils.concat("<font color='red'>�?", cmdType.CH(), "�? 命令: </font>");
		String msg = StrUtils.concat(msgs);
		MakePatchUI.getInstn().toConsole(cmd, msg);
	}
	
	protected CmdType getCmdType() {
		return CmdType.toType((String) cmdCB.getSelectedItem());
	}
	
	protected String getFromPath() {
		return fromTF.getText();
	}
	
	protected String getToPath() {
		return toTF.getText();
	}
	
	protected String toXml() {
		String cmdXml = "";
		CmdType cmdType = getCmdType();
		if(CmdType.DEL == cmdType) {
			cmdXml = StrUtils.concat("    <", cmdType.EN(), " caption=\"", 
					cmdType.CH(), "命令\" from=\"", getFromPath(), "\" />");
					
		} else {
			cmdXml = StrUtils.concat("    <", cmdType.EN(), " caption=\"", 
					cmdType.CH(), "命令\" from=\"", getFromPath(), 
					"\" to=\"", getToPath(), "\" />");
		}
		return cmdXml;
	}
	
}
