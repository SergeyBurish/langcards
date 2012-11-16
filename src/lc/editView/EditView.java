package lc.editView;

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

import lc.langCardsExeption.LangCardsExeption;
import lc.cardSet.lngCard.LngCard;
import lc.LCmain;
import lc.cardSet.CardSet;
import lc.editView.editCardDlg.EditCardDlg;

public class EditView implements ActionListener {
	CardSet iSet;
	
	JButton iBtAdd = new JButton("Add");
	JButton iBtDel = new JButton("Delete");
	JButton iBtEd = new JButton("Edit");
	
	JButton iBtStart = new JButton("Start lesson");
	
	DefaultTableModel iTableModelState = new DefaultTableModel();
	JTable iTableState;
	
	DefaultTableModel iTableModel = new DefaultTableModel();
	JTable iTable;
	
	public EditView(CardSet set) {
		iSet = set;
		
		iBtAdd.addActionListener(this);
		iBtDel.addActionListener(this);
		iBtEd.addActionListener(this);
		
		iBtStart.addActionListener(LCmain.mainFrame);
	}
	
	public void Show() throws XPathExpressionException, LangCardsExeption {
		LCmain.mainFrame.setTitle(iSet.Name() + " Language Cards");
		//setJMenuBar(null); // remove menu
		
		LCmain.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel panState = makeStatePanel();
		tabbedPane.addTab("State", panState);
		
		JPanel panCards = makeCardsPanel();
		tabbedPane.addTab("Cards", panCards);
		
		JPanel panSett = makeSettingsPanel();
		tabbedPane.addTab("Settings", panSett);
		
		tabbedPane.setSelectedIndex(1); // select Cards panel
		
		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(tabbedPane)
				.addComponent(iBtStart)
		);
				
		LCmain.mainFrame.iLayout.setVerticalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(tabbedPane)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(iBtStart)
		);
		
		LCmain.mainFrame.pack();
	}
	
	private JPanel makeStatePanel() throws XPathExpressionException{
		JPanel panel = new JPanel(false);
		
		iTableState = new JTable(iTableModelState);
		UpdateStateTable();
		
		JScrollPane sp = new JScrollPane(iTableState);
		
		panel.setLayout(new GridLayout(1, 1));
		panel.add(sp);
		
		return panel; //makeSettingsPanel();
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
	
	private void UpdateStateTable() throws XPathExpressionException{
		
		Vector<Vector<String>> rows=new Vector<Vector<String>>();
		
		Vector<String> rowVect=new Vector<String>();
		rowVect.addElement("Total number");
		rowVect.addElement(Integer.toString(iSet.CardsCount()));
		rows.addElement(rowVect);
		
		rowVect=new Vector<String>();
		rowVect.addElement("Learned");
		rowVect.addElement("0");
		rows.addElement(rowVect);

		rowVect=new Vector<String>();
		rowVect.addElement("Idle");
		rowVect.addElement("0");
		rows.addElement(rowVect);
		
		
		Vector<String> columns= new Vector<String>();
		columns.addElement("Status");
		columns.addElement("Quantity");
		
		iTableModelState.setDataVector(rows, columns);		
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
		String actionCmd = event.getActionCommand();
		
		if (actionCmd.equals("Add")) {
			try {
				LngCard lngCard = new LngCard();
				
				EditCardDlg newCardDlg = new EditCardDlg(null, lngCard);
				newCardDlg.SetLanguages(iSet.LanguageFrom(), iSet.LanguageTo());
				newCardDlg.setVisible(true);
				
				if (newCardDlg.Accepted()) {
					iSet.AddNewCard(lngCard);
					UpdateTable();
					UpdateStateTable();
					ScrollTableToShowRaw(iTableModel.getRowCount() - 1);
				}
				
			} catch (XPathExpressionException e) {
				LCmain.mainFrame.ShowErr(e);
			}
			catch (LangCardsExeption e) {
				LCmain.mainFrame.ShowErr(e);
			}
			//iTableModel.addRow(new Object[] { btName });
		} else if (actionCmd.equals("Delete")) {
			iTableModel.addColumn(actionCmd);
		} else {
			iTableModel.addColumn(actionCmd);
		}
	}
}
