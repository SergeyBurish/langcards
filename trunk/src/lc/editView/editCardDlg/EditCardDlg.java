package lc.editView.editCardDlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import lc.LCmain;
import lc.LCutils;

import lc.cardSet.lngCard.LngCard;
import lc.cardSet.lngPhrase.LngPhrase;

import lc.editView.editCardDlg.exTree.ExTree;
import lc.editView.editCardDlg.exTree.MultiLineCellEditor;
import lc.editView.editCardDlg.exTree.MultiLineCellRenderer;
import lc.editView.editCardDlg.exTree.ExTreeNode;
import lc.langCardsException.LangCardsException;

public class EditCardDlg extends JDialog implements TreeSelectionListener {
	ExTreeNode rootNode = new ExTreeNode(LCutils.String("New_Card"), false);
	DefaultTreeModel iModel = new DefaultTreeModel(rootNode);
	ExTree iTree = new ExTree(iModel);
	JButton iOkBtn = new JButton(LCutils.String("Save"));
	JLabel iStatusLbl = new JLabel("Test");
	JScrollPane iTreeScrollPane;
	
	String iLangFrst = "No language set";
	String iLangScnd = "No language set";
	
	ExTreeNode iLngFrstNode;
	ExTreeNode iLngScndNode;
	
	LngCard iLngCard;
	
	Boolean iAccepted = false;
	
	public EditCardDlg(JFrame parent, LngCard lngCard) {
		super(parent, lngCard.id().isEmpty() ? LCutils.String("New_Card") : "card " + lngCard.id(), true);
		iLngCard = lngCard;
		
		LCmain.mainFrame.AddToCloseArray(this);
	}
	
	public void SetLanguages(String langFrst, String langScnd) {
		iLangFrst = langFrst;
		iLangScnd = langScnd;
	}
	
	private void InitControls() throws LangCardsException {
		iLngFrstNode = new ExTreeNode(iLangFrst, false);

		int frstPhraseCount = iLngCard.FrstPhraseCount();
		if (frstPhraseCount > 0) {
			for (int i = 0; i < frstPhraseCount; i++) {
				LngPhrase lngPhrase = iLngCard.GetFrstPhrase(i);
				iLngFrstNode.add(new ExTreeNode(lngPhrase.iValue, true));
			}
		}
		else {
		iLngFrstNode.add(new ExTreeNode(LCutils.String("Type_new_word_or_phrase_here"), true)); // \n" + "3333\n"  + "4444
		}

		
		iLngScndNode = new ExTreeNode(iLangScnd, false);

		int scndPhraseCount = iLngCard.ScndPhraseCount();
		if (scndPhraseCount > 0) {
			for (int i = 0; i < scndPhraseCount; i++) {
				LngPhrase lngPhrase = iLngCard.GetScndPhrase(i);
				iLngScndNode.add(new ExTreeNode(lngPhrase.iValue, true));
			}
		}
		else {
		iLngScndNode.add(new ExTreeNode(LCutils.String("Type_new_word_or_phrase_here"), true));
		}
		
		rootNode.add(iLngFrstNode);
		rootNode.add(iLngScndNode);

		iTree.setCellRenderer(new MultiLineCellRenderer());
		iTree.setCellEditor(new MultiLineCellEditor(iModel));
		//expand all nodes
		for (int i = 0; i < iTree.getRowCount(); i++) {
			iTree.expandRow(i);
		}
		
		//iTree.setToggleClickCount(1);
		iTree.setEditable(true);
		iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		iTree.addTreeSelectionListener(this);
		iTree.setInvokesStopCellEditing(true);
		
		iTreeScrollPane = new JScrollPane(iTree);
		
		// correct sizes
		iTreeScrollPane.getViewport().setPreferredSize(iTree.getPreferredSize());
	}
	
	private void InitLayout() {
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(iTreeScrollPane)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(iOkBtn)
				)
				.addComponent(iStatusLbl)
		);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(iTreeScrollPane)
						.addComponent(iOkBtn)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(iStatusLbl)
		);

		final EditCardDlg self = this;
		
		iOkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Verify(); // - check, display corresponding error;
				iLngCard.clear();

				ExTreeNode node;
				for (int i = 0; i < iLngFrstNode.getChildCount(); i++) {
					node = (ExTreeNode)iLngFrstNode.getChildAt(i);
					iLngCard.AddFrstPhrase(new LngPhrase(node.toString()));
				}

				for (int i = 0; i < iLngScndNode.getChildCount(); i++) {
					node = (ExTreeNode)iLngScndNode.getChildAt(i);
					iLngCard.AddScndPhrase(new LngPhrase(node.toString()));
				}

				iAccepted = true;
				LCmain.mainFrame.RemoveFromCloseArray(self);
				dispose();
			}
		});
		
		pack();
	}
	
	// JDialog
	@Override
	public void setVisible(boolean b) {
		try {
			InitControls();
			InitLayout();
			super.setVisible(b);
		} catch (LangCardsException e) {
			LCmain.mainFrame.ShowErr(e);
		}
	}
	
	public Boolean Accepted() {
		return iAccepted;
	}

	// TreeSelectionListener
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		Object selectedComponent = iTree.getLastSelectedPathComponent();
		
		if (selectedComponent instanceof ExTreeNode) {
			ExTreeNode node = (ExTreeNode)selectedComponent;
			
			if (node.isEditable()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						iTree.startEditingAtPath(iTree.getSelectionPath());
					}
				});
			}
		}
	}
}