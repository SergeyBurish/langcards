package editView;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

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
import LangCards.LCmain;
import cardSet.CardSet;
import editCardDlg.EditCardDlg;

public class EditView implements ActionListener {
	CardSet iSet;
	
	JButton iBtAdd = new JButton("Add");
	JButton iBtDel = new JButton("Delete");
	JButton iBtEd = new JButton("Edit");
	
	DefaultTableModel iTableModel = new DefaultTableModel();
	JTable iTable;
	
	public EditView(CardSet set) {
		iSet = set;
		
		iBtAdd.addActionListener(this);
		iBtDel.addActionListener(this);
		iBtEd.addActionListener(this);
	}
	
	public void Show() throws XPathExpressionException, LangCardsExeption {
		LCmain.mainFrame.setTitle(iSet.Name() + " Language Cards");
		//setJMenuBar(null); // remove menu
		
		LCmain.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel panCards = makeCardsPanel();
		tabbedPane.addTab("Cards", panCards);
		
		JPanel panSett = makeSettingsPanel();
		tabbedPane.addTab("Settings", panSett);
		
		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(tabbedPane)
		);
				
		LCmain.mainFrame.iLayout.setVerticalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(tabbedPane)
		);
		
		LCmain.mainFrame.pack();
	}
	
	private JPanel makeCardsPanel() throws XPathExpressionException, LangCardsExeption {
		JPanel panel = new JPanel(false);
		
		iTable = new JTable(iTableModel);
		UpdateTable();
		
		JScrollPane sp = new JScrollPane(iTable);
		
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
	
	private void UpdateTable() throws XPathExpressionException, LangCardsExeption {
		
		Vector<Vector<String>> rows = iSet.GetAllCardsIdFromTo();
		Vector<String> columns= new Vector<String>();
		
		columns.addElement("#");
		columns.addElement(iSet.LanguageFrom());
		columns.addElement(iSet.LanguageTo());
		
		iTableModel.setDataVector(rows, columns);
	}
	
	private void ScrollTableToShowRaw(int row) {
		Rectangle r = iTable.getCellRect(row, 0, true);
		iTable.scrollRectToVisible(r);		
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
						UpdateTable();
						ScrollTableToShowRaw(iTableModel.getRowCount() - 1);
					}
					
				} catch (XPathExpressionException e) {
					LCmain.mainFrame.ShowErr(e);
				}
				catch (LangCardsExeption e) {
					LCmain.mainFrame.ShowErr(e);
				}
				//iTableModel.addRow(new Object[] { btName });
			} else {
				iTableModel.addColumn(btName);
			}
		}
	}
}
