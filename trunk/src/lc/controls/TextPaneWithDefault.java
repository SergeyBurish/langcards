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

	TypingStateListener typingStateListener = null;

	public TextPaneWithDefault(String defaultString, TypingStateListener listener) {
		this(defaultString, null, listener);
	}

	public TextPaneWithDefault(String defaultString, String currentValue, TypingStateListener listener) {
		if (defaultString != null) this.defaultString = defaultString;
		this.typingStateListener = listener;

		if (currentValue != null && currentValue.length() > 0) {
			setText(currentValue);
			typing = true;
			setForeground(getSelectedTextColor());
		} else {
			setText(this.defaultString);
			typing = false;
			setForeground(getDisabledTextColor());
			setCaretPosition(0);
		}

		addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				if (!typing && e.getDot() != 0) {
					setCaretPosition(0);
				}
			}
		});

		Document doc = getDocument();
		if (doc instanceof AbstractDocument) {
			((AbstractDocument)doc).setDocumentFilter(new DocumentFilter(){
				@Override
				public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
					if (!typing) {
						typing = true;
						attributeSet = attrs;

						super.replace(fb, 0, TextPaneWithDefault.this.getText().length(), text, attrs);
						TextPaneWithDefault.this.setForeground(TextPaneWithDefault.this.getSelectedTextColor());

						if (typingStateListener != null) typingStateListener.typingStarted();
					}
					else {
						super.replace(fb, offset, length, text, attrs);
					}
				}

				@Override
				public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
					if (typing) {
						super.remove(fb, offset, length);

						if (TextPaneWithDefault.this.getText().isEmpty()) {
							super.insertString(fb, 0, TextPaneWithDefault.this.defaultString, TextPaneWithDefault.this.attributeSet);
							TextPaneWithDefault.this.setForeground(TextPaneWithDefault.this.getDisabledTextColor());
							TextPaneWithDefault.this.setCaretPosition(0);

							typing = false;
							if (typingStateListener != null) typingStateListener.typingStopped();
						}
					}
				}
			});
		}
		else {
			LOGGER.warning("fail to properly initialize TextPaneWithDefault");
		}
	}

	public void hideDefaultString() {
		if (!typing) {
			Document doc = getDocument();
			if (doc instanceof AbstractDocument) {
				try {
					((AbstractDocument) doc).replace(0, TextPaneWithDefault.this.defaultString.length(), "", TextPaneWithDefault.this.attributeSet);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
