package editView;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;
import javax.xml.xpath.XPathExpressionException;

import langCardsExeption.LangCardsExeption;
import lngCard.LngCard;
import LangCards.LCui;
import cardSet.CardSet;
import editCardDlg.EditCardDlg;

public class EditView implements ActionListener {
	private CardSet iSet;
	
	private JButton iBtAdd = new JButton("Add");
	private JButton iBtDel = new JButton("Delete");
	private JButton iBtEd = new JButton("Edit");
	
	private DefaultTableModel iTableModel = new DefaultTableModel();
	
	public EditView(CardSet set) {
		iSet = set;
		
		iBtAdd.addActionListener(this);
		iBtDel.addActionListener(this);
		iBtEd.addActionListener(this);
	}
	
	public void Show() throws XPathExpressionException, LangCardsExeption {
		LCui.mainFrame.setTitle(iSet.Name() + " Language Cards");
		//setJMenuBar(null); // remove menu
		
		LCui.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel panCards = makeCardsPanel();
		tabbedPane.addTab("Cards", panCards);
		
		JPanel panSett = makeSettingsPanel();
		tabbedPane.addTab("Settings", panSett);
		
		LCui.mainFrame.iLayout.setHorizontalGroup(
				LCui.mainFrame.iLayout.createSequentialGroup()
				.addComponent(tabbedPane)
		);
				
		LCui.mainFrame.iLayout.setVerticalGroup(
				LCui.mainFrame.iLayout.createSequentialGroup()
				.addComponent(tabbedPane)
		);
		
		LCui.mainFrame.pack();
	}
	
	private JPanel makeCardsPanel() throws XPathExpressionException, LangCardsExeption {
		JPanel panel = new JPanel(false);
		
		JTable table = new JTable(iTableModel);
		
		iTableModel.addColumn(iSet.LanguageFrom());
		iTableModel.addColumn(iSet.LanguageTo());
		//iTableModel.addRow(new Object[] { "v1", "v2" });
		//iTableModel.addRow(new Object[] { "v3" });
		//iTableModel.addRow(new Object[] { "v4", "v555", "v6" });
		
		JScrollPane sp = new JScrollPane(table);
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		
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
						.addComponent(iBtEd)
				)
		);
		
		return panel;
	}
	
	private JPanel makeSettingsPanel() {
		JPanel panel = new JPanel(false);
		
		JTree tree = new JTree();
		
		panel.setLayout(new GridLayout(1, 1));
		panel.add(tree);
		return panel;
	}
	
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent event) {
		Object srcObject = event.getSource();
		
		if (srcObject instanceof JButton) {
			JButton srcBt = (JButton)srcObject;
			
			String btName = srcBt.getText();
			
			if (btName.compareTo("Add") == 0) {
				try {
					LngCard lngCard = new LngCard();
					
					EditCardDlg newCardDlg = new EditCardDlg(null, lngCard);
					newCardDlg.SetLanguages(iSet.LanguageFrom(), iSet.LanguageTo());
					newCardDlg.setVisible(true);
					
					if (newCardDlg.Accepted()) {
						iSet.AddNewCard(lngCard);
						iTableModel.addRow(new Object[] { lngCard.GetFromPhrase(0), lngCard.GetToPhrase(0)});
					}
					
				} catch (XPathExpressionException e) {
					LCui.mainFrame.ShowErr(e);
				}
				catch (LangCardsExeption e) {
					LCui.mainFrame.ShowErr(e);
				}
				//iTableModel.addRow(new Object[] { btName });
			} else {
				iTableModel.addColumn(btName);
			}
		}
	}
}
