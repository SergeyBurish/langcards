package lc.editView.editCardDlg;

import lc.LCmain;
import lc.LCutils;
import lc.cardSet.lngCard.LngCard;
import lc.cardSet.lngPhrase.LngPhrase;
import lc.editView.editCardDlg.exTree.ExTree;
import lc.editView.editCardDlg.exTree.ExTreeNode;
import lc.editView.editCardDlg.exTree.MultiLineCellEditor;
import lc.editView.editCardDlg.exTree.MultiLineCellRenderer;
import lc.langCardsException.LangCardsException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditCardDlg extends JDialog {
	ExTreeNode rootNode = new ExTreeNode(LCutils.string("New_Card"), false);
	DefaultTreeModel iModel = new DefaultTreeModel(rootNode);
	ExTree iTree = new ExTree(iModel);
	JButton iOkBtn = new JButton(LCutils.string("Save"));
	JLabel iStatusLbl = new JLabel("Test");
	JScrollPane iTreeScrollPane;
	
	String iLangFrst = "No language set";
	String iLangScnd = "No language set";
	
	ExTreeNode iLngFrstNode;
	ExTreeNode iLngScndNode;
	
	LngCard iLngCard;
	
	Boolean iAccepted = false;
	
	public EditCardDlg(JFrame parent, LngCard lngCard) {
		super(parent, lngCard.id().isEmpty() ? LCutils.string("New_Card") : "card " + lngCard.id(), true);
		iLngCard = lngCard;
		
		LCmain.mainFrame.addToCloseArray(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void setLanguages(String langFrst, String langScnd) {
		iLangFrst = langFrst;
		iLangScnd = langScnd;
	}
	
	private void initControls() throws LangCardsException {
		iLngFrstNode = new ExTreeNode(iLangFrst, false);

		int frstPhraseCount = iLngCard.frstPhraseCount();
		if (frstPhraseCount > 0) {
			for (int i = 0; i < frstPhraseCount; i++) {
				LngPhrase lngPhrase = iLngCard.getFrstPhrase(i);
				iLngFrstNode.add(new ExTreeNode(lngPhrase.iValue, true));
			}
		}
		else {
			iLngFrstNode.add(new ExTreeNode(LCutils.string("Type_new_word_or_phrase_here"), true));
		}

		iLngScndNode = new ExTreeNode(iLangScnd, false);

		int scndPhraseCount = iLngCard.scndPhraseCount();
		if (scndPhraseCount > 0) {
			for (int i = 0; i < scndPhraseCount; i++) {
				LngPhrase lngPhrase = iLngCard.getScndPhrase(i);
				iLngScndNode.add(new ExTreeNode(lngPhrase.iValue, true));
			}
		}
		else {
			iLngScndNode.add(new ExTreeNode(LCutils.string("Type_new_word_or_phrase_here"), true));
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
		iTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
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
		});
		iTree.setInvokesStopCellEditing(true);
		
		iTreeScrollPane = new JScrollPane(iTree);
		
		// correct sizes
		iTreeScrollPane.getViewport().setPreferredSize(iTree.getPreferredSize());
	}
	
	private void initLayout() {
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
				iTree.stopEditing();

				// Verify(); // - check, display corresponding error;
				iLngCard.clear();

				ExTreeNode node;
				for (int i = 0; i < iLngFrstNode.getChildCount(); i++) {
					node = (ExTreeNode)iLngFrstNode.getChildAt(i);
					iLngCard.addFrstPhrase(new LngPhrase(node.getChangedString()));
				}

				for (int i = 0; i < iLngScndNode.getChildCount(); i++) {
					node = (ExTreeNode)iLngScndNode.getChildAt(i);
					iLngCard.addScndPhrase(new LngPhrase(node.getChangedString()));
				}

				iAccepted = true;
				LCmain.mainFrame.removeFromCloseArray(self);
				dispose();
			}
		});
	}

	@Override
	public void dispose() {
		LCutils.Settings settings = LCmain.getSettings();
		if (settings != null) {
			Rectangle frameBounds = getBounds();
			settings.dialogXpos = frameBounds.x;
			settings.dialogYpos = frameBounds.y;
			settings.dialogWidth = frameBounds.width;
			settings.dialogHeight = frameBounds.height;
		}

		super.dispose();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			try {
				initControls();
				initLayout();

				LCutils.Settings settings = LCmain.getSettings();
				if (settings != null && settings.dialogWidth > 0 && settings.dialogHeight > 0) {
					this.setBounds(settings.dialogXpos, settings.dialogYpos, settings.dialogWidth, settings.dialogHeight);
				} else {
					pack();
				}
			} catch (LangCardsException e) {
				LCmain.mainFrame.showErr(e);
			}
		}
		super.setVisible(visible);
	}
	
	public Boolean accepted() {
		return iAccepted;
	}

	public LngCard getLngCard() {
		return iLngCard;
	}
}