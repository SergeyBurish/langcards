package lc.editView.editCardDlg.exTree;

import lc.controls.TextPaneWithDefault;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;

public class ExTextPane extends TextPaneWithDefault {
	ExTextPaneListener exTextPaneListener;
	JTextPane iDummy = new JTextPane(); // to recalculate text size only 
	
	public ExTextPane(ExTextPaneListener tpListener, String defaultString, String currentValue, TypingStateListener typingStateListener) {
		super(defaultString, currentValue, typingStateListener);
		exTextPaneListener = tpListener;
	}
	
	public void updateSize() {
		String str = getText();
		iDummy.setText(str);
		Dimension d = iDummy.getPreferredSize();
		
		setSize(d);
		setPreferredSize(d);
	}
	
	@Override
	// JTextPane
	protected void processKeyEvent(KeyEvent event) {
		if (exTextPaneListener != null &&
			event.getKeyCode() == KeyEvent.VK_ENTER && 
			event.getID() == KeyEvent.KEY_PRESSED) {
			
			if (event.isControlDown()) {
				exTextPaneListener.ctrlEnterTyped();
			} else if (event.getID() == KeyEvent.KEY_PRESSED) {
				exTextPaneListener.enterTyped();
			}
		} else {
			super.processKeyEvent(event);
		}
	}
}
