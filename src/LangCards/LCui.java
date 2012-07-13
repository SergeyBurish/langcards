package LangCards;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LCui extends JFrame
					implements ActionListener {
	
	private JTextField input = new JTextField("Test", 5);
	private JLabel label = new JLabel();
	
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	
	JFileChooser fc = new JFileChooser();
	
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
		
		CreateMenu();
		
		pack();
	}
	
	public static void main(String[] args) {
		LCui ui = new LCui();
		ui.setVisible(true);
	}
	
	private void CreateMenu() {
		
		menuBar = new JMenuBar();
		menu = new JMenu("Set");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("New");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Open");
		menuItem.addActionListener(this);
		
		menu.add(menuItem);
		
		this.setJMenuBar(menuBar);
	}

	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
        String actionCmd = arg0.getActionCommand();
        if (actionCmd.equals("New")) {
        	fc.showDialog(this, "New");
        } else if (actionCmd.equals("Open")) {
        	fc.showOpenDialog(this);
        }
		
	}
}
