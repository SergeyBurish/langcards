package lc.editView.editCardDlg.exTree;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultTreeModel;

import lc.LCmain;

public class MultiLineCellEditor extends DefaultCellEditor implements DocumentListener, CaretListener, ExTextPaneListener{
	static ExTextPane iTextPane;
	DefaultTreeModel iModel;
	ExTreeNode iNode = null;

	public MultiLineCellEditor(DefaultTreeModel model) {
		super(new JTextField());
		iTextPane = new ExTextPane(this);
		iTextPane.addCaretListener(this);
		iTextPane.getDocument().addDocumentListener(this);
		
		iModel = model;
	}
	
	private void UpdateSizes() {
		iTextPane.UpdateSize();
		
		if (iModel != null && iNode != null) {
			iModel.nodeChanged(iNode);
		}
	}

	@Override
	// DocumentListener
	public void changedUpdate(DocumentEvent e) {
		UpdateSizes();
	}

	@Override
	// DocumentListener
	public void insertUpdate(DocumentEvent e) {
		UpdateSizes();
	}

	@Override
	// DocumentListener
	public void removeUpdate(DocumentEvent e) {
		UpdateSizes();
	}

	@Override
	// CaretListener
	public void caretUpdate(CaretEvent e) {
		// TODO Auto-generated method stub
		
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
		iTextPane.setText(stringValue);
		
		if (value instanceof ExTreeNode) {
			iNode = (ExTreeNode)value;
		}
		
		return iTextPane;
	}

	@Override
	// ExTextPaneListener
	public void enterTyped() {
		stopCellEditing();
	}

	@Override
	// ExTextPaneListener
	public void ctrlEnterTyped(){
		try {
			iTextPane.getDocument().insertString(iTextPane.getCaretPosition(), "\n", null);
		} catch (BadLocationException e) {
			LCmain.mainFrame.ShowErr(e);
		}
	}
}
