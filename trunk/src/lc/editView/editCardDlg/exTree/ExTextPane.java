package lc.editView.editCardDlg.exTree;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class ExTextPane extends JTextPane {
	ExTextPaneListener listener;
	
	public ExTextPane(ExTextPaneListener tpListener) {
		listener = tpListener;
	}
	
	public void UpdateSize() {
		String str = getText();
		
		FontMetrics fm = getToolkit().getFontMetrics(getFont());
		BufferedReader br = new BufferedReader(new StringReader(str));
		String line;
		int maxWidth = 0, lines = 0;
		try {
			while ((line = br.readLine()) != null) {
				int width = SwingUtilities.computeStringWidth(fm, line);
				if (maxWidth < width) {
					maxWidth = width;
				}
				lines++;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		lines = (lines < 1) ? 1 : lines;
		int height = fm.getHeight() * lines;
		Dimension dm = new Dimension(maxWidth + 6, height+6);
		setSize(dm);
		setPreferredSize(dm);
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
