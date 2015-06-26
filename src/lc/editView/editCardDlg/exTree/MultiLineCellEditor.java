package lc.editView.editCardDlg.exTree;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultTreeModel;

import lc.LCmain;
import lc.LCutils;
import lc.controls.TextPaneWithDefault;

public class MultiLineCellEditor extends DefaultCellEditor {
	static ExTextPane iTextPane;
	DefaultTreeModel iModel;
	ExTreeNode iNode = null;
	private boolean textChanged;

	public MultiLineCellEditor(DefaultTreeModel model) {
		super(new JTextField());
		iTextPane = createTextPane(LCutils.String("Type_new_word_or_phrase_here"), null);
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
							LCmain.mainFrame.ShowErr(e);
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
				UpdateSizes();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				UpdateSizes();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				UpdateSizes();
			}
		});

		return textPane;
	}

	private void UpdateSizes() {
		iTextPane.UpdateSize();
		
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
		String defaultString = LCutils.String("Type_new_word_or_phrase_here");

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
}
