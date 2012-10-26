package lc.editView.editCardDlg.exTree;

import javax.swing.tree.DefaultMutableTreeNode;

public class ExTreeNode extends DefaultMutableTreeNode {
	private boolean iEditable;
	public ExTreeNode (Object userObject, boolean editable) {
		super(userObject);
		iEditable = editable;
	}
	
	public boolean isEditable() {
		return iEditable;
	}
}
