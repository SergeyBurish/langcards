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

import exTreeNode.ExTreeNode;

public class NewCardDlg extends JDialog implements TreeSelectionListener {
	ExTreeNode rootNode = new ExTreeNode("New Card", false);
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	JTree tree = new JTree(model);
	JButton ok = new JButton("OK");
	JLabel lbl = new JLabel("Test");
	JScrollPane iTreeScrollPane;
	
	public NewCardDlg(JFrame parent) {
		super(parent, "New Card", true);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		iTreeScrollPane = new JScrollPane(tree);
		
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(iTreeScrollPane)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(ok)
				)
				.addComponent(lbl)
		);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(iTreeScrollPane)
						.addComponent(ok)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(lbl)
		);
		
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}
	
	public void SetLanguages(String langFrom, String langTo) {
		ExTreeNode lngFromNode = new ExTreeNode(langFrom, false);
		lngFromNode.add(new ExTreeNode("Enter new word or phrase here", true));		
		
		ExTreeNode lngToNode = new ExTreeNode(langTo, false);
		lngToNode.add(new ExTreeNode("Enter new word or phrase here", true));
		
		rootNode.add(lngFromNode);
		rootNode.add(lngToNode);
		
		//expand all nodes
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		
		//tree.setToggleClickCount(1);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		tree.addTreeSelectionListener(this);
		
		// correct sizes
		iTreeScrollPane.getViewport().setPreferredSize(tree.getPreferredSize());
		
		pack();
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		Object selectedComponent = tree.getLastSelectedPathComponent();
		
		if (selectedComponent instanceof ExTreeNode) {
			ExTreeNode node = (ExTreeNode)selectedComponent;
			
			if (node.isEditable()) {
				SwingUtilities.invokeLater(new Runnable() {  
		            public void run() {  
		            	tree.startEditingAtPath(tree.getSelectionPath());
		            }  
		        });  
			}
		}
	}
}
