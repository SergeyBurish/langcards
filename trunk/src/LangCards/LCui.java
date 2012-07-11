package LangCards;

import java.awt.Component;
import javax.swing.*;

public class LCui extends JFrame {
	private JTextField input = new JTextField("Test", 5);
	private JLabel label = new JLabel();
	
	public LCui() {
		setTitle("Language Cards");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		label.setText("label AAAAAAA");
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(label)
		);
		
		layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {label, input});
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(label)
		);
		
		pack();
		
	}
	
	public static void main(String[] args) {
		LCui ui = new LCui();
		ui.setVisible(true);
	}
}
