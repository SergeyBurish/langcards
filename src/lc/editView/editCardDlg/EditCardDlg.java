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
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

		if (!lngCard.id().isEmpty()) {
			rootNode.setUserObject("card " + lngCard.id());
		}

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
				addPhrase(lngPhrase, iLngFrstNode);
			}
		}
		else {
			addPhrase(null, iLngFrstNode);
		}

		iLngScndNode = new ExTreeNode(iLangScnd, false);

		int scndPhraseCount = iLngCard.scndPhraseCount();
		if (scndPhraseCount > 0) {
			for (int i = 0; i < scndPhraseCount; i++) {
				LngPhrase lngPhrase = iLngCard.getScndPhrase(i);
				addPhrase(lngPhrase, iLngScndNode);
			}
		}
		else {
			addPhrase(null, iLngScndNode);
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

		iTree.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int button = e.getButton();
				if (button  == MouseEvent.BUTTON3) {
					TreePath selectedPath = iTree.getPathForLocation(e.getX(), e.getY());
					if (selectedPath != null) {
						Object selectedComponent = selectedPath.getLastPathComponent();
						if (selectedComponent instanceof ExTreeNode) {
							ExTreeNode node = (ExTreeNode) selectedComponent;
							node.showPopupMenu(iTree, e);
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		
		iTreeScrollPane = new JScrollPane(iTree);
		
		// correct sizes
		iTreeScrollPane.getViewport().setPreferredSize(iTree.getPreferredSize());
	}

	private void addPhrase(LngPhrase lngPhrase, ExTreeNode lngNode) {
		String phraseValue;
		if (lngPhrase != null) {
			phraseValue = lngPhrase.iValue;
		} else {
			phraseValue = LCutils.string("Type_new_word_or_phrase_here");
		}

		ExTreeNode nodePhrase = new ExTreeNode(phraseValue, true, new ExTreeNode.ExTreeNodeListener() {
			@Override
			public void onStopNodeEditing() {
				addEmptyPhrase(lngNode);
			}
		});

		nodePhrase.setPopupMenu(newPhrasePopupMenu());
		lngNode.add(nodePhrase);
	}

	private void addEmptyPhrase(ExTreeNode lngNode) {

		boolean hasEmpty = false;
		for (int i = 0; i < lngNode.getChildCount(); i++) {
			Object child = lngNode.getChildAt(i);
			if (child instanceof ExTreeNode) {
				ExTreeNode node = (ExTreeNode) child;

				String changedString = node.getChangedString();
				if (changedString.isEmpty()) {
					hasEmpty = true;
					break;
				}
			}
		}

		if (!hasEmpty) {
			ExTreeNode emptyNode = new ExTreeNode(LCutils.string("Type_new_word_or_phrase_here"), true, new ExTreeNode.ExTreeNodeListener() {
				@Override
				public void onStopNodeEditing() {
					addEmptyPhrase(lngNode);
				}
			});

			iModel.insertNodeInto(emptyNode, lngNode, lngNode.getChildCount());
			//iModel.reload(emptyNode);
		}
	}

	private JPopupMenu newPhrasePopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		popup.add(new JMenuItem(LCutils.string("Add_transcription")));
		popup.add(new JMenuItem(LCutils.string("Add_example")));

		return popup;
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