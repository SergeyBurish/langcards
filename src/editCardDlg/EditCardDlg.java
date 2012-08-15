package editCardDlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import lngCard.LngCard;

import exTree.ExTree;
import exTreeNode.ExTreeNode;

public class EditCardDlg extends JDialog implements TreeSelectionListener, ActionListener {
	ExTreeNode rootNode = new ExTreeNode("New Card", false);
	ExTree iTree = new ExTree(new DefaultTreeModel(rootNode));
	JButton iOkBtn = new JButton("OK");
	JLabel iStatusLbl = new JLabel("Test");
	JScrollPane iTreeScrollPane;
	
	String iLangFrom = "No language set";
	String iLangTo = "No language set";
	
	ExTreeNode iLngFromNode;
	ExTreeNode iLngToNode;
	
	LngCard iLngCard;
	
	Boolean iAccepted = false;
	
	public EditCardDlg(JFrame parent, LngCard lngCard) {
		super(parent, "New Card", true);
		iLngCard = lngCard;
	}
	
	public void SetLanguages(String langFrom, String langTo) {
		iLangFrom = langFrom;
		iLangTo = langTo;
	}
	
	private void InitControls() {
		iLngFromNode = new ExTreeNode(iLangFrom, false);
		iLngFromNode.add(new ExTreeNode("Enter new word or phrase here", true));
		
		iLngToNode = new ExTreeNode(iLangTo, false);
		iLngToNode.add(new ExTreeNode("Enter new word or phrase here", true));
		
		rootNode.add(iLngFromNode);
		rootNode.add(iLngToNode);
		
		//expand all nodes
		for (int i = 0; i < iTree.getRowCount(); i++) {
			iTree.expandRow(i);
		}
		
		//iTree.setToggleClickCount(1);
		iTree.setEditable(true);
		iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		iTree.addTreeSelectionListener(this);
		iTree.setInvokesStopCellEditing(true);
		
		iTreeScrollPane = new JScrollPane(iTree);
		
		// correct sizes
		iTreeScrollPane.getViewport().setPreferredSize(iTree.getPreferredSize());
	}
	
	private void InitLayout() {
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(iTreeScrollPane)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(iOkBtn)
				)
				.addComponent(iStatusLbl)
		);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(iTreeScrollPane)
						.addComponent(iOkBtn)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(iStatusLbl)
		);
		
		iOkBtn.addActionListener(this);
		
		pack();
	}
	
	// JDialog
	@Override
	public void setVisible(boolean b) {
		InitControls();
		InitLayout();
		super.setVisible(b);
	}
	
	public Boolean Accepted() {
		return iAccepted;
	}
	

	// TreeSelectionListener
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		Object selectedComponent = iTree.getLastSelectedPathComponent();
		
		if (selectedComponent instanceof ExTreeNode) {
			ExTreeNode node = (ExTreeNode)selectedComponent;
			
			if (node.isEditable()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						iTree.startEditingAtPath(iTree.getSelectionPath());
					}
				});
			}
		}
	}

	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Verify(); // - check, display corresponding error;
		ExTreeNode node;
		
		for (int i = 0; i < iLngFromNode.getChildCount(); i++) {
			node = (ExTreeNode)iLngFromNode.getChildAt(i);
			iLngCard.AddFromPhrase(node.toString());
		}

		for (int i = 0; i < iLngToNode.getChildCount(); i++) {
			node = (ExTreeNode)iLngToNode.getChildAt(i);
			iLngCard.AddToPhrase(node.toString());
		}
		
		iAccepted = true;
		dispose();
	}
}