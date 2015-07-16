package lc.editView.editCardDlg.exTree;

import lc.LCmain;
import lc.LCutils;
import lc.controls.TextPaneWithDefault;

import javax.swing.*;
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

	public MultiLineCellEditor(DefaultTreeModel model) {
		super(new JTextField());
		iTextPane = createTextPane(LCutils.string("Type_new_word_or_phrase_here"), null);
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
							iTextPane.getDocument().insertString(iTextPane.getCaretPosition(), "\n", null);
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
		String defaultString = LCutils.string("Type_new_word_or_phrase_here");

		if (defaultString.contentEquals(stringValue)) {
			iTextPane = createTextPane(defaultString, null);
		} else {
			iTextPane = createTextPane(defaultString, stringValue);
		}

		if (value instanceof ExTreeNode) {
			iNode = (ExTreeNode)value;
			iNode.setChanged(textChanged);
		}
		
		return iTextPane;
	}

	@Override
	public boolean stopCellEditing() {
		if (iNode != null) {
			iNode.stopNodeEditing();
		}

		return super.stopCellEditing();
	}
}
