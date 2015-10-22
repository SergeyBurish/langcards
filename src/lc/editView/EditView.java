package lc.editView;

import lc.LCmain;
import lc.utils.LCutils;
import lc.utils.LCutils.LanguageResourceItem;
import lc.cardSet.CardSet;
import lc.cardSet.lngCard.LngCard;
import lc.editView.editCardDlg.EditCardDlg;
import lc.langCardsException.LangCardsException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.logging.Logger;

public class EditView {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	CardSet iSet;
	
	JButton iBtAdd = new JButton(LCutils.string("Add"));
	JButton iBtDel = new JButton(LCutils.string("Delete"));
	JButton iBtEd = new JButton(LCutils.string("Edit"));
	
	JButton iBtStart = new JButton(LCutils.string("Start_lesson"));

	NotEditableTableModel iTableModelState = new NotEditableTableModel();
	JTable iTableState;
	
	NotEditableTableModel iTableModel = new NotEditableTableModel();
	JTable iTable;
	
	JTabbedPane iTabbedPane = new JTabbedPane();
	
	public EditView(CardSet set) {
		iSet = set;

		//----------------------Add----------------------
		iBtAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					LngCard lngCard = new LngCard();

					EditCardDlg newCardDlg = new EditCardDlg(lngCard, new EditCardDlg.EditCardListener() {
						@Override
						public void onSaveCard(LngCard lngCard) {
							try {
								iSet.addNewCard(lngCard);
								updateTable();
								updateStateTable();
								scrollTableToShowRaw(iTableModel.getRowCount() - 1);
							}
							catch (XPathExpressionException e)	{LCmain.mainFrame.showErr(e);}
							catch (LangCardsException e)		{LCmain.mainFrame.showErr(e);}
						}
					});
					newCardDlg.setLanguages(iSet.languageFrst(), iSet.languageScnd());
					newCardDlg.setVisible(true);

				}
				catch (XPathExpressionException e)	{LCmain.mainFrame.showErr(e);}
			}
		});

		//----------------------Delete----------------------
		iBtDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int selectedRow = iTable.getSelectedRow();
					if (selectedRow >= 0) {
						String cardId = (String) iTableModel.getValueAt(selectedRow, 0);
						iSet.deleteCard(cardId);
						updateTable();
						updateStateTable();
					}
				}
				catch (XPathExpressionException e)	{LCmain.mainFrame.showErr(e);}
				catch (LangCardsException e)		{LCmain.mainFrame.showErr(e);}
			}
		});

		//----------------------Edit----------------------
		iBtEd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int selectedRow = iTable.getSelectedRow();
					if (selectedRow >= 0) {
						String cardId = (String) iTableModel.getValueAt(selectedRow, 0);
						LngCard lngCard = iSet.getCard(cardId);

						EditCardDlg newCardDlg = new EditCardDlg(lngCard, new EditCardDlg.EditCardListener() {
							@Override
							public void onSaveCard(LngCard lngCard) {
								try {
									iSet.saveCard(lngCard);
									updateTable();
								}
								catch (XPathExpressionException e)	{LCmain.mainFrame.showErr(e);}
								catch (LangCardsException e)		{LCmain.mainFrame.showErr(e);}
							}
						});
						newCardDlg.setLanguages(iSet.languageFrst(), iSet.languageScnd());
						newCardDlg.setVisible(true);
					}
				}
				catch (XPathExpressionException e)	{LCmain.mainFrame.showErr(e);}
				catch (LangCardsException e)		{LCmain.mainFrame.showErr(e);}
			}
		});

		//----------------------Start_lesson----------------------
		iBtStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!iSet.isSaved()) {

					Object[] options = {LCutils.string("Save_and_start_lesson"),
							LCutils.string("Cancel_and_continue_edit")};
					int result = JOptionPane.showOptionDialog(LCmain.mainFrame,
							LCutils.string("All_changes_should_be_saved_before_the_start_of_the_lesson"),
							LCutils.string("Unsaved_changes_in_the_set"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							options,
							options[0]);

					switch (result) {
						case JOptionPane.YES_OPTION:
							if (!iSet.save() && !LCmain.mainFrame.saveAs()) {
								return;
							}
							break;

						case JOptionPane.NO_OPTION:
						case JOptionPane.CLOSED_OPTION:
							return;
					}
				}

				try {
					LCmain.mainFrame.showLesson();
				}
				catch (XPathExpressionException e)	{LCmain.mainFrame.showErr(e);}
				catch (LangCardsException e)		{LCmain.mainFrame.showErr(e);}
			}
		});
	}

	public void show() throws XPathExpressionException, LangCardsException {
		LCmain.mainFrame.iContainer.removeAll(); // remove all ui controls

		JPanel panState = makeStatePanel();
		iTabbedPane.addTab(LCutils.string("State"), panState);

		JPanel panCards = makeCardsPanel();
		iTabbedPane.addTab(LCutils.string("Cards"), panCards);

		JPanel panSett = makeSettingsPanel();
		iTabbedPane.addTab(LCutils.string("Settings"), panSett);

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
	}
	
	private JPanel makeStatePanel() throws XPathExpressionException{
		JPanel panel = new JPanel(false);
		
		iTableState = new JTable(iTableModelState);
		updateStateTable();
		
		JScrollPane sp = new JScrollPane(iTableState);
		
		panel.setLayout(new GridLayout(1, 1));
		panel.add(sp);
		
		return panel;
	}
	
	private JPanel makeCardsPanel() throws XPathExpressionException, LangCardsException {
		JPanel panel = new JPanel(false);
		
		iTable = new JTable(iTableModel);
		updateTable();
		
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
		
		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, iBtAdd, iBtDel, iBtEd);
		
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
		
		Vector<LanguageResourceItem> langListModel;
		
		try {
			langListModel = LCutils.supportedUILanguages();
		} catch (IOException e1) { // ignore all exceptions, create empty langListModel
			e1.printStackTrace();
			langListModel = new Vector<LanguageResourceItem>();
		} catch (URISyntaxException e) { // ignore all exceptions, create empty langListModel
			e.printStackTrace();
			langListModel = new Vector<LanguageResourceItem>();
		}
		
		int currentLangInd = 0;
		if (langListModel.isEmpty()) {
			langListModel.add(new LanguageResourceItem("English (English)", "en_EN"));
		}
		else {
			currentLangInd = LCutils.getCurrentLocaleIndexOfList(langListModel);
		}
		
		JComboBox<LanguageResourceItem> langListCombobox = new JComboBox<LanguageResourceItem>(langListModel);
		langListCombobox.setSelectedIndex(currentLangInd);
		
		langListCombobox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				JComboBox comboBox = (JComboBox)event.getSource();
				LanguageResourceItem item = (LanguageResourceItem)comboBox.getSelectedItem();
				
				if (!item.localeString().equals(LCutils.currentLocaleString())) {
					LOGGER.info("a new language selected: " + item + "; locale: " + item.localeString());
					changeUiLanguage(item.localeString());
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
	
	private void updateStateTable() throws XPathExpressionException{
		
		Vector<Vector<String>> rows=new Vector<Vector<String>>();
		
		Vector<String> rowVect=new Vector<String>();
		rowVect.addElement(LCutils.string("Total_number"));
		rowVect.addElement(Integer.toString(iSet.cardsCount()));
		rows.addElement(rowVect);
		
		rowVect=new Vector<String>();
		rowVect.addElement(LCutils.string("Learned"));
		rowVect.addElement("0");
		rows.addElement(rowVect);

		rowVect=new Vector<String>();
		rowVect.addElement(LCutils.string("Idle"));
		rowVect.addElement("0");
		rows.addElement(rowVect);
		
		
		Vector<String> columns= new Vector<String>();
		columns.addElement(LCutils.string("Status"));
		columns.addElement(LCutils.string("Quantity"));
		
		iTableModelState.setDataVector(rows, columns);
	}
	
	private void updateTable() throws XPathExpressionException, LangCardsException {
		
		Vector<Vector<String>> rows = iSet.getAllCardsIdFrstScnd();
		Vector<String> columns= new Vector<String>();
		
		columns.addElement("#");
		columns.addElement(iSet.languageFrst());
		columns.addElement(iSet.languageScnd());
		
		iTableModel.setDataVector(rows, columns);
	}
	
	private void scrollTableToShowRaw(int row) {
		Rectangle r = iTable.getCellRect(row, 0, true);
		iTable.scrollRectToVisible(r);
	}
	
	private void changeUiLanguage(String localeString) {
		LCutils.setLocale(localeString);
		
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
		if (iSet.unnamed()) {
			iSet.setName(LCutils.string("Unnamed"));
			LCmain.mainFrame.changeSetNameInTitle(iSet.name());
		}

		LCmain.mainFrame.setFileFilterPrompt(LCutils.string("Language_Cards_file"));
		LCmain.mainFrame.createMenu();
		
		iTabbedPane.setTitleAt(0, LCutils.string("State"));
		iTabbedPane.setTitleAt(1, LCutils.string("Cards"));
		iTabbedPane.setTitleAt(2, LCutils.string("Settings"));
		
		iBtAdd.setText(LCutils.string("Add"));
		iBtDel.setText(LCutils.string("Delete"));
		iBtEd.setText(LCutils.string("Edit"));
		iBtStart.setText(LCutils.string("Start_lesson"));
	}

	private class NotEditableTableModel  extends DefaultTableModel{
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
