package lc.editView;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;
import javax.xml.xpath.XPathExpressionException;

import lc.LCmain;
import lc.LCutils;
import lc.LCutils.LanguageResourceItem;
import lc.cardSet.CardSet;
import lc.cardSet.lngCard.LngCard;
import lc.editView.editCardDlg.EditCardDlg;
import lc.langCardsExeption.LangCardsExeption;

public class EditView implements ActionListener {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	CardSet iSet;
	
	JButton iBtAdd = new JButton(LCutils.String("Add"));
	JButton iBtDel = new JButton(LCutils.String("Delete"));
	JButton iBtEd = new JButton(LCutils.String("Edit"));
	
	JButton iBtStart = new JButton(LCutils.String("Start_lesson"));
	
	DefaultTableModel iTableModelState = new DefaultTableModel();
	JTable iTableState;
	
	DefaultTableModel iTableModel = new DefaultTableModel();
	JTable iTable;
	
	JTabbedPane iTabbedPane = new JTabbedPane();
	
	boolean iEn = true;
	
	public EditView(CardSet set) {
		iSet = set;
		
		iBtAdd.addActionListener(this);
		iBtDel.addActionListener(this);
		iBtEd.addActionListener(this);
		
		iBtStart.addActionListener(LCmain.mainFrame);
	}
	
	public void Show() throws XPathExpressionException, LangCardsExeption {
		LCmain.mainFrame.ChangeSetNameInTitle(iSet.Name());
		
		LCmain.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		JPanel panState = makeStatePanel();
		iTabbedPane.addTab(LCutils.String("State"), panState);
		
		JPanel panCards = makeCardsPanel();
		iTabbedPane.addTab(LCutils.String("Cards"), panCards);
		
		JPanel panSett = makeSettingsPanel();
		iTabbedPane.addTab(LCutils.String("Settings"), panSett);
		
		iTabbedPane.setSelectedIndex(1); // select Cards panel
		
		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(iTabbedPane)
				.addComponent(iBtStart)
		);
				
		LCmain.mainFrame.iLayout.setVerticalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(iTabbedPane)
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
		
		return panel;
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
		
		Vector<LanguageResourceItem> langListModel = null;
		
		try {
			langListModel = LCutils.supportedUILanguages();
		} catch (IOException e1) { // ignore all exceptions
			e1.printStackTrace();
		} catch (URISyntaxException e) { // ignore all exceptions
			e.printStackTrace();
		}
		
		int currentLangInd = 0;
		if (langListModel == null || langListModel.isEmpty()) {
			langListModel.add(new LanguageResourceItem("English (English)", "en_EN"));
		}
		else {
			currentLangInd = LCutils.GetCurrentLocaleIndexOfList(langListModel);
		}
		
		JComboBox langListCombobox = new JComboBox(langListModel);
		langListCombobox.setSelectedIndex(currentLangInd);
		
		langListCombobox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				JComboBox comboBox = (JComboBox)event.getSource();
				LanguageResourceItem item = (LanguageResourceItem)comboBox.getSelectedItem();
				
				if (!item.localeString().equals(LCutils.CurrentLocaleString())) {
					LOGGER.info("language selected: " + item + "; locale: " + item.localeString());
					ChangeUiLanguage(item.localeString());
				}
			}
		});
		
		JTree tree = new JTree();
		
		panel.setLayout(new GridLayout(0, 1));
		panel.add(tree);
		
		JPanel flow = new JPanel(new FlowLayout( FlowLayout.LEFT));
		flow.add(langListCombobox);
		
		panel.add(flow);
		return panel;
	}
	
	private void UpdateStateTable() throws XPathExpressionException{
		
		Vector<Vector<String>> rows=new Vector<Vector<String>>();
		
		Vector<String> rowVect=new Vector<String>();
		rowVect.addElement(LCutils.String("Total_number"));
		rowVect.addElement(Integer.toString(iSet.CardsCount()));
		rows.addElement(rowVect);
		
		rowVect=new Vector<String>();
		rowVect.addElement(LCutils.String("Learned"));
		rowVect.addElement("0");
		rows.addElement(rowVect);

		rowVect=new Vector<String>();
		rowVect.addElement(LCutils.String("Idle"));
		rowVect.addElement("0");
		rows.addElement(rowVect);
		
		
		Vector<String> columns= new Vector<String>();
		columns.addElement(LCutils.String("Status"));
		columns.addElement(LCutils.String("Quantity"));
		
		iTableModelState.setDataVector(rows, columns);		
	}
	
	private void UpdateTable() throws XPathExpressionException, LangCardsExeption {
		
		Vector<Vector<String>> rows = iSet.GetAllCardsIdFrstScnd();
		Vector<String> columns= new Vector<String>();
		
		columns.addElement("#");
		columns.addElement(iSet.LanguageFrst());
		columns.addElement(iSet.LanguageScnd());
		
		iTableModel.setDataVector(rows, columns);
	}
	
	private void ScrollTableToShowRaw(int row) {
		Rectangle r = iTable.getCellRect(row, 0, true);
		iTable.scrollRectToVisible(r);		
	}
	
	private void ChangeUiLanguage(String localeString) {
		LCutils.SetLocale(localeString);
		
		//recreate all invisible elements
		try {
			JPanel panState = makeStatePanel();
			iTabbedPane.remove(0);
			iTabbedPane.add(panState, 0);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// rename all visible elements
		// if unnamed - translate
		iSet.SetName(LCutils.String("Unnamed"));
		LCmain.mainFrame.ChangeSetNameInTitle(iSet.Name());
		
		LCmain.mainFrame.SetFileFilterPrompt(LCutils.String("Language_Cards_file"));
		LCmain.mainFrame.CreateMenu();
		
		iTabbedPane.setTitleAt(0, LCutils.String("State"));
		iTabbedPane.setTitleAt(1, LCutils.String("Cards"));
		iTabbedPane.setTitleAt(2, LCutils.String("Settings"));
		
		iBtAdd.setText(LCutils.String("Add"));
		iBtDel.setText(LCutils.String("Delete"));
		iBtEd.setText(LCutils.String("Edit"));
		iBtStart.setText(LCutils.String("Start_lesson"));
	}
	
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCmd = event.getActionCommand();
		
		if (actionCmd.equals(LCutils.String("Add"))) {
			try {
				LngCard lngCard = new LngCard();
				
				EditCardDlg newCardDlg = new EditCardDlg(null, lngCard);
				newCardDlg.SetLanguages(iSet.LanguageFrst(), iSet.LanguageScnd());
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
		} else if (actionCmd.equals(LCutils.String("Delete"))) {
			iTableModel.addColumn(actionCmd);
		} else {
			iTableModel.addColumn(actionCmd);
		}
	}
}
