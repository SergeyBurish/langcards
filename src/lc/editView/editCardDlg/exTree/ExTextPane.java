package lc.editView.editCardDlg.exTree;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;

public class ExTextPane extends JTextPane {
	ExTextPaneListener listener;
	JTextPane iDummy = new JTextPane(); // to recalculate text size only 
	
	public ExTextPane(ExTextPaneListener tpListener) {
		listener = tpListener;
	}
	
	public void UpdateSize() {
		String str = getText();
		iDummy.setText(str);
		Dimension d = iDummy.getPreferredSize();
		
		setSize(d);
		setPreferredSize(d);
	}
	
	@Override
	// JTextPane
	protected void processKeyEvent(KeyEvent event) {
		if (listener != null &&
			event.getKeyCode() == KeyEvent.VK_ENTER && 
			event.getID() == KeyEvent.KEY_PRESSED) {
			
			if (event.isControlDown()) {
				listener.ctrlEnterTyped();
			} else if (event.getID() == KeyEvent.KEY_PRESSED) {
				listener.enterTyped();
			}
		} else {
			super.processKeyEvent(event);
		}
	}
}
