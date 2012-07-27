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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class NewCardDlg extends JDialog {
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("New Card");
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	JTree tree = new JTree(model);
	JButton ok = new JButton("OK");
	JLabel lbl = new JLabel("Test");
	
	public NewCardDlg(JFrame parent) {
		super(parent, "New Card", true);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		JScrollPane scrollPane = new JScrollPane(tree);
		
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(scrollPane)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(ok)
				)
				.addComponent(lbl)
		);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(scrollPane)
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
		
		pack();
	}
	
	public void SetLanguages(String lang1, String lang2) {
		DefaultMutableTreeNode lng1Node = new DefaultMutableTreeNode(lang1);
		lng1Node.add(new DefaultMutableTreeNode("lang1 val"));
		
		DefaultMutableTreeNode lng2Node = new DefaultMutableTreeNode(lang2);
		lng2Node.add(new DefaultMutableTreeNode("lang2 val"));
		
		rootNode.add(lng1Node);		
		rootNode.add(lng2Node);
	}
}
