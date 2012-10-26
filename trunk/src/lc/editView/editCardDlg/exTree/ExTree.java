package lc.editView.editCardDlg.exTree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import exTreeNode.ExTreeNode;

public class ExTree extends JTree {
	public ExTree(TreeModel newModel) {
		super(newModel);
	}
	
	// JTree
	@Override
	public boolean isEditable() {
		Object selectedComponent = this.getLastSelectedPathComponent();
		
		if (selectedComponent instanceof ExTreeNode) {
			ExTreeNode node = (ExTreeNode)selectedComponent;
			return node.isEditable();
		}

		return super.isEditable();
	}
}
