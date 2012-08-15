package newCardDlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import exTree.ExTree;
import exTreeNode.ExTreeNode;

public class NewCardDlg extends JDialog implements TreeSelectionListener, ActionListener {
	ExTreeNode rootNode = new ExTreeNode("New Card", false);
	ExTree iTree = new ExTree(new DefaultTreeModel(rootNode));
	JButton iOkBtn = new JButton("OK");
	JLabel iStatusLbl = new JLabel("Test");
	JScrollPane iTreeScrollPane;
	
	String iLangFrom = "No language set";
	String iLangTo = "No language set";
	
	public NewCardDlg(JFrame parent) {
		super(parent, "New Card", true);
	}
	
	public void SetLanguages(String langFrom, String langTo) {
		iLangFrom = langFrom;
		iLangTo = langTo;
	}
	
	private void InitControls() {
		ExTreeNode lngFromNode = new ExTreeNode(iLangFrom, false);
		lngFromNode.add(new ExTreeNode("Enter new word or phrase here", true));		
		
		ExTreeNode lngToNode = new ExTreeNode(iLangTo, false);
		lngToNode.add(new ExTreeNode("Enter new word or phrase here", true));
		
		rootNode.add(lngFromNode);
		rootNode.add(lngToNode);
		
		//expand all nodes
		for (int i = 0; i < iTree.getRowCount(); i++) {
			iTree.expandRow(i);
		}
		
		//iTree.setToggleClickCount(1);
		iTree.setEditable(true);
		iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		iTree.addTreeSelectionListener(this);
		
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
		dispose();
	}
}
