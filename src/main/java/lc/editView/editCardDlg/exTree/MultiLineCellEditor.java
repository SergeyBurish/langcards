package lc.editView.editCardDlg.exTree;

import lc.LCmain;
import lc.utils.LCutils;
import lc.controls.TextPaneWithDefault;
import lc.editView.editCardDlg.Keyboard;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class MultiLineCellEditor extends DefaultCellEditor {
	static ExTextPane iTextPane;
	DefaultTreeModel iModel;
	ExTreeNode iNode = null;
	private boolean textChanged;
	private Keyboard iKeyboard;

	public MultiLineCellEditor(DefaultTreeModel model) {
		super(new JTextField());
		iModel = model;
	}

	private ExTextPane createTextPane(String defaultString, String currentValue) {
		ExTextPane textPane = new ExTextPane(
				new ExTextPaneListener() {
					@Override
					public void enterTyped() {
						stopCellEditing();
					}

					@Override
					public void ctrlEnterTyped() {
						try {
							if (iTextPane != null) {
								iTextPane.insertStringAtCaretPosition("\n");
							}
						} catch (BadLocationException e) {
							LCmain.mainFrame.showErr(e);
						}
					}
				}, defaultString, currentValue,
				new TextPaneWithDefault.TypingStateListener() {
					@Override
					public void textChanged(boolean changed) {
						textChanged = changed;
						if (iNode != null) {
							iNode.setChanged(textChanged);
						}
					}
				});

		textPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				updateSizes();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				updateSizes();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				updateSizes();
			}
		});

		return textPane;
	}

	private void updateSizes() {
		iTextPane.updateSize();

		if (iKeyboard != null) {
			iKeyboard.updatePosition(iTextPane);
		}
		
		if (iModel != null && iNode != null) {
			iModel.nodeChanged(iNode);
		}
	}

	@Override
	// TreeCellEditor
	public Object getCellEditorValue() {
		return iTextPane.getText();
	}

	@Override
	// TreeCellEditor
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

		String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, true);
		String defaultString = null;

		if (value instanceof ExTreeNode) {
			iNode = (ExTreeNode)value;
			iNode.setChanged(textChanged);
			defaultString = iNode.getDefaultString();
		}

		if (defaultString.contentEquals(stringValue)) {
			iTextPane = createTextPane(defaultString, null);
		} else {
			iTextPane = createTextPane(defaultString, stringValue);
		}

		if ( iNode != null && iNode.isTranscription() ) {
			if (iKeyboard == null) {

				iKeyboard = new Keyboard(LCutils.string("EnglishTranscription"), new Keyboard.KBListener() {
					@Override
					public void keyPressed(String key) {
						try {
							if (iTextPane != null) {
								iTextPane.insertStringAtCaretPosition(key);
							}
						} catch (BadLocationException e) {
							LCmain.mainFrame.showErr(e);
						}

					}
				});

				iKeyboard.init();
				iKeyboard.setAlwaysOnTop(true);
			}

			iTextPane.addAncestorListener(new AncestorListener() {
				@Override
				public void ancestorAdded(AncestorEvent event) {
					updateSizes();
					iKeyboard.setVisible(true);
				}

				@Override
				public void ancestorRemoved(AncestorEvent event) {
				}

				@Override
				public void ancestorMoved(AncestorEvent event) {
					updateSizes();
				}
			});
		}

		return iTextPane;
	}

	@Override
	public boolean stopCellEditing() {
		if (iNode != null) {
			iNode.stopNodeEditing();
		}

		if (iKeyboard != null) {
			iKeyboard.setVisible(false);
		}

		return super.stopCellEditing();
	}

	@Override
	public void cancelCellEditing() {
		if (iKeyboard != null) {
			iKeyboard.setVisible(false);
		}

		super.cancelCellEditing();
	}
}
