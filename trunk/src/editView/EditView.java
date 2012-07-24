package editView;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.LayoutStyle;

import LangCards.LCui;
import cardSet.CardSet;

public class EditView {
	private CardSet iSet;
	
	private JTabbedPane iTabbedPane = new JTabbedPane();
	private JButton iBtAdd = new JButton("Add");
	private JButton iBtDel = new JButton("Delete");
	private JButton iBtEd = new JButton("Edit");
	
	
	public EditView(CardSet set) {
		iSet = set;
	}
	
	public void Show() {
		LCui.mainFrame.setTitle(iSet.Name() + " Language Cards");
		//setJMenuBar(null); // remove menu
		
		LCui.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		JPanel panCards = makeCardsPanel();
		iTabbedPane.addTab("Cards", panCards);
		
		JPanel panSett = makeSettingsPanel();
		iTabbedPane.addTab("Settings", panSett);
		
		LCui.mainFrame.iLayout.setHorizontalGroup(
				LCui.mainFrame.iLayout.createSequentialGroup()
				.addComponent(iTabbedPane)
		);
				
		LCui.mainFrame.iLayout.setVerticalGroup(
				LCui.mainFrame.iLayout.createSequentialGroup()
				.addComponent(iTabbedPane)
		);
		
		LCui.mainFrame.pack();
	}
	    
    private JPanel makeCardsPanel() {
        JPanel panel = new JPanel(false);
        
        Object[][] cellData = { { "1-1", "1-2" }, { "2-1", "2-2" } };
        String[] columnNames = { "col1", "col2" };
        JTable table = new JTable(cellData, columnNames);
        
        JScrollPane sp = new JScrollPane(table);
        
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
				.addComponent(sp)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(iBtAdd)
						.addComponent(iBtDel)
						.addComponent(iBtEd))
		);
        
        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {iBtAdd, iBtDel, iBtEd});
				
        layout.setVerticalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(sp)
				.addGroup(layout.createSequentialGroup()
						.addComponent(iBtAdd)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(iBtDel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(iBtEd))
		);

        return panel;
    }
    
    private JPanel makeSettingsPanel() {
        JPanel panel = new JPanel(false);
        
        JTree tree = new JTree();
        tree.setAlignmentX(Component.CENTER_ALIGNMENT);
        tree.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        panel.setLayout(new GridLayout(1, 1));
        panel.add(tree);
        return panel;
    }


}
