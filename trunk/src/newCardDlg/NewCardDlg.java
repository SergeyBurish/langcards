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

public class NewCardDlg extends JDialog {
	JTree tree = new JTree();
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
}
