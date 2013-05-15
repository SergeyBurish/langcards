package lc.controls;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import java.util.logging.Logger;

// JTextPane with default string which is set when no text is typed (as prompt)
public class TextPaneWithDefault extends JTextPane {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private String defaultString = "";
	private boolean typing = false;
	private AttributeSet attributeSet = null;

	public interface TypingStateListener {
		public void typingStarted();
		public void typingStopped();
	}

	TypingStateListener listener = null;

	public TextPaneWithDefault(String defaultString, TypingStateListener l) {
		if (defaultString != null) this.defaultString = defaultString;
		this.listener = l;

		setText(this.defaultString);
		setForeground(getDisabledTextColor());
		setCaretPosition(0);

		addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				if (!typing && e.getDot() != 0) {
					setCaretPosition(0);
				}
			}
		});

		final TextPaneWithDefault self = this;

		Document doc = getDocument();
		if (doc instanceof AbstractDocument) {
			((AbstractDocument)doc).setDocumentFilter(new DocumentFilter(){
				@Override
				public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
					if (!typing) {
						typing = true;
						attributeSet = attrs;

						super.remove(fb, 0, self.getText().length());
						super.insertString(fb, 0, text, attrs);
						self.setForeground(self.getSelectedTextColor());

						if (listener != null) listener.typingStarted();
					}
					else {
						super.replace(fb, offset, length, text, attrs);
					}
				}

				@Override
				public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
					if (typing) {
						super.remove(fb, offset, length);

						if (self.getText().isEmpty()) {
							super.insertString(fb, 0, self.defaultString, self.attributeSet);
							self.setForeground(self.getDisabledTextColor());
							self.setCaretPosition(0);

							typing = false;
							if (listener != null) listener.typingStopped();
						}
					}
				}
			});
		}
		else {
			LOGGER.warning("fail to properly initialize TextPaneWithDefault");
		}
	}
}
