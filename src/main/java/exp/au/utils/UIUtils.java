package exp.au.utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import exp.libs.utils.other.StrUtils;

public class UIUtils {

	protected UIUtils() {}
	
	/**
	 * 创建占位用的Label
	 * @param spaceNum 占位空格个数
	 * @return
	 */
	public static JLabel newLabel(int spaceNum) {
		return new JLabel(StrUtils.multiChar(' ', spaceNum));
	}
	
	/**
	 * 限制文本框的输入内容为数字
	 * @param textField
	 */
	public static void limitNum(final JTextField textField) {
		if(textField == null) {
			return;
		}
		
		textField.addKeyListener(new KeyListener() {

		    @Override
		    public void keyTyped(KeyEvent e) {
		        String text = textField.getText();  // 当前输入框内容
		        char ch = e.getKeyChar();   // 准备附加到输入框的字符

		        // 限制不能输入非数字
		        if(!(ch >= '0' && ch <= '9')) {
		            e.consume();    // 销毁当前输入字符

		        // 限制不能是0开头
		        } else if("".equals(text) && ch == '0') {   
		            e.consume();
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
	
}
