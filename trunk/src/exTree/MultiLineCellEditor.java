package exTree;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import LangCards.LCui;

public class MultiLineCellEditor extends DefaultCellEditor implements DocumentListener, CaretListener, ExTextPaneListener{
	static ExTextPane iTextPane;

	public MultiLineCellEditor() {
		super(new JTextField());
		iTextPane = new ExTextPane(this);
		iTextPane.addCaretListener(this);
		iTextPane.getDocument().addDocumentListener(this);
	}

	@Override
	// DocumentListener
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	// DocumentListener
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	// DocumentListener
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
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
			LCui.mainFrame.ShowErr(e);
		}
	}
}
