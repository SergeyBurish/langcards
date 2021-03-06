package lc.editView.editCardDlg.exTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;

public class ExTreeNode extends DefaultMutableTreeNode {
	private String defaultString = null;
	private boolean iEditable;
	private boolean changed;
	private JPopupMenu iPopupMenu = null;
	private ExTreeNodeListener listener = null;
	private boolean isTranscription = false;

	public ExTreeNode (Object userObject, boolean editable) {
		this(userObject, null, editable, null);
	}

	public ExTreeNode (Object userObject, String defaultString, boolean editable, ExTreeNodeListener listener) {
		super(userObject);
		this.defaultString = defaultString;
		iEditable = editable;
		this.listener = listener;
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

	public void showPopupMenu(JTree tree, MouseEvent event) {
		if (changed && iPopupMenu != null) {
			iPopupMenu.show(tree, event.getX(), event.getY());
		}
	}

	public void setPopupMenu(JPopupMenu popupMenu) {
		iPopupMenu = popupMenu;
	}

	public void stopNodeEditing() {
		if (listener != null) {
			listener.onStopNodeEditing(this);
		}
	}

	public String getDefaultString() {
		return defaultString;
	}

	public void setIsTranscriptionSign() {
		isTranscription = true;
	}

	public boolean isTranscription() {
		return isTranscription;
	}

	public interface ExTreeNodeListener {
		void onStopNodeEditing(ExTreeNode changedNode);
	}
}
