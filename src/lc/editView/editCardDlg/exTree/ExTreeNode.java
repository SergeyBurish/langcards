package lc.editView.editCardDlg.exTree;

import javax.swing.tree.DefaultMutableTreeNode;

public class ExTreeNode extends DefaultMutableTreeNode {
	private boolean iEditable;
	private boolean changed;

	public ExTreeNode (Object userObject, boolean editable) {
		super(userObject);
		iEditable = editable;
	}
	
	public boolean isEditable() {
		return iEditable;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public String getChangedString() {
		if (changed) {
			return super.toString();
		}
		return "";
	}
}
