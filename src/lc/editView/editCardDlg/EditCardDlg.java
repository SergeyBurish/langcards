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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EditCardDlg extends JFrame {
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
	
	EditCardListener iEditCardListener;
	private MultiLineCellEditor iMultiLineCellEditor;

	public EditCardDlg(LngCard lngCard, EditCardListener editCardListener) {
		super(lngCard.id().isEmpty() ? LCutils.string("New_Card") : "card " + lngCard.id());
		iLngCard = lngCard;
		iEditCardListener = editCardListener;

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
		addPhrase(null, iLngFrstNode);

		iLngScndNode = new ExTreeNode(iLangScnd, false);

		int scndPhraseCount = iLngCard.scndPhraseCount();
		if (scndPhraseCount > 0) {
			for (int i = 0; i < scndPhraseCount; i++) {
				LngPhrase lngPhrase = iLngCard.getScndPhrase(i);
				addPhrase(lngPhrase, iLngScndNode);
			}
		}
		addPhrase(null, iLngScndNode);
		
		rootNode.add(iLngFrstNode);
		rootNode.add(iLngScndNode);

		iTree.setCellRenderer(new MultiLineCellRenderer());
		iMultiLineCellEditor = new MultiLineCellEditor(iModel);
		iTree.setCellEditor(iMultiLineCellEditor);

		//expand all nodes but Transcription and Examples
		Object root = iModel.getRoot();
		DefaultMutableTreeNode treeTop = null;
		if (root instanceof DefaultMutableTreeNode) {
			treeTop = (DefaultMutableTreeNode) root;

			DefaultMutableTreeNode currentNode = treeTop.getNextNode();
			do {
				if (currentNode.getLevel() < 2)
					iTree.expandPath(new TreePath(currentNode.getPath()));
				currentNode = currentNode.getNextNode();
			}
			while (currentNode != null);
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

		ExTreeNode nodePhrase = new ExTreeNode(phraseValue, LCutils.string("Type_new_word_or_phrase_here"), true, new ExTreeNode.ExTreeNodeListener() {
			@Override
			public void onStopNodeEditing(ExTreeNode changedNode) {
				onStopPhraseEditing(lngNode, changedNode);
			}
		});

		//nodePhrase.setPopupMenu(newPhrasePopupMenu());

		iModel.insertNodeInto(nodePhrase, lngNode, lngNode.getChildCount());
		//iModel.reload(nodePhrase);

		if (lngPhrase != null) {
			addTranscriptionAndExamples(nodePhrase, lngPhrase);
		}
	}

	private void onStopPhraseEditing(ExTreeNode lngNode, ExTreeNode changedNode) {
		if (changedNode.getChangedString().isEmpty()) {
			for (int i = changedNode.getChildCount() - 1; i >= 0; i--) {
				Object child = changedNode.getChildAt(i);
				if (child instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) child;
					iModel.removeNodeFromParent(node);
				}
			}
		} else if (changedNode.getChildCount() == 0) {
			addTranscriptionAndExamples(changedNode, null);
		}

		addRemoveEmptyNode(lngNode, changedNode, LCutils.string("Type_new_word_or_phrase_here"), new ExTreeNode.ExTreeNodeListener() {
			@Override
			public void onStopNodeEditing(ExTreeNode changedNode) {
				onStopPhraseEditing(lngNode, changedNode);
			}
		});
	}

	private void onStopExampleEditing(ExTreeNode nodeExamples, ExTreeNode changedNode) {
		addRemoveEmptyNode(nodeExamples, changedNode, LCutils.string("Example_can_be_added_here"), new ExTreeNode.ExTreeNodeListener() {
			@Override
			public void onStopNodeEditing(ExTreeNode changedNode) {
				onStopExampleEditing(nodeExamples, changedNode);
			}
		});
	}

	private void addRemoveEmptyNode(ExTreeNode parentNode, ExTreeNode changedNode, String emptyNodeString, ExTreeNode.ExTreeNodeListener emptyNodeListener) {
		boolean hasEmpty = false;
		for (int i = 0; i < parentNode.getChildCount(); i++) {
			Object child = parentNode.getChildAt(i);
			if (child instanceof ExTreeNode) {
				ExTreeNode node = (ExTreeNode) child;

				String changedString = node.getChangedString();
				if (changedString.isEmpty()) {
					hasEmpty = true;

					if (changedNode.getChangedString().isEmpty() && changedNode != node) {
						iModel.removeNodeFromParent(node);
						//iModel.reload(emptyNode);
						return;
					}
				}
			}
		}

		if (!hasEmpty) {
			ExTreeNode emptyNode = new ExTreeNode(emptyNodeString, emptyNodeString, true, emptyNodeListener);
			//emptyNode.setPopupMenu(newPhrasePopupMenu());

			iModel.insertNodeInto(emptyNode, parentNode, parentNode.getChildCount());
			//iModel.reload(emptyNode);
		}
	}

	private void addTranscriptionAndExamples(ExTreeNode nodePhrase, LngPhrase lngPhrase) {
		// transcription
		String transcription;
		if (lngPhrase != null && lngPhrase.iTranscription != null && !lngPhrase.iTranscription.isEmpty()) {
			transcription = lngPhrase.iTranscription;
		}else {
			transcription = LCutils.string("Transcription_can_be_added_here");
		}

		ExTreeNode nodeTranscription = new ExTreeNode(transcription, LCutils.string("Transcription_can_be_added_here"), true, null);
		nodeTranscription.setIsTranscriptionSign();
		iModel.insertNodeInto(nodeTranscription, nodePhrase, nodePhrase.getChildCount());
		//iModel.reload(nodeTranscription);

		// examples
		ExTreeNode nodeExamples = new ExTreeNode(LCutils.string("Examples"), false);
		iModel.insertNodeInto(nodeExamples, nodePhrase, nodePhrase.getChildCount());
		//iModel.reload(nodeExamples);

		if (lngPhrase != null && lngPhrase.iExamples != null) {
			for (int i = 0; i < lngPhrase.iExamples.size(); i++) {
				ExTreeNode nodeExample = new ExTreeNode(lngPhrase.iExamples.get(i), LCutils.string("Example_can_be_added_here"), true, new ExTreeNode.ExTreeNodeListener() {
					@Override
					public void onStopNodeEditing(ExTreeNode changedNode) {
						onStopExampleEditing(nodeExamples, changedNode);
					}
				});
				iModel.insertNodeInto(nodeExample, nodeExamples, nodeExamples.getChildCount());
				//iModel.reload(nodeExample);
			}
		}

		ExTreeNode nodeExample = new ExTreeNode(LCutils.string("Example_can_be_added_here"), LCutils.string("Example_can_be_added_here"), true, new ExTreeNode.ExTreeNodeListener() {
			@Override
			public void onStopNodeEditing(ExTreeNode changedNode) {
				onStopExampleEditing(nodeExamples, changedNode);
			}
		});
		iModel.insertNodeInto(nodeExample, nodeExamples, nodeExamples.getChildCount());
		//iModel.reload(nodeExample);

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

		iOkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iTree.stopEditing();

				// Verify(); // - check, display corresponding error;
				iLngCard.clear();

				ExTreeNode node;
				for (int i = 0; i < iLngFrstNode.getChildCount(); i++) {
					node = (ExTreeNode)iLngFrstNode.getChildAt(i);
					String changedString = node.getChangedString();
					if (!changedString.isEmpty()) {
						iLngCard.addFrstPhrase(new LngPhrase(changedString));
					}
				}

				for (int i = 0; i < iLngScndNode.getChildCount(); i++) {
					node = (ExTreeNode)iLngScndNode.getChildAt(i);
					String changedString = node.getChangedString();
					if (!changedString.isEmpty()) {
						iLngCard.addScndPhrase(new LngPhrase(changedString));
					}
				}

				if (iEditCardListener != null) {
					iEditCardListener.onSaveCard(iLngCard);
				}

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

		// emulate modal
		LCmain.mainFrame.setEnabled(true);

		LCmain.mainFrame.removeFromCloseArray(this);

		if (iMultiLineCellEditor != null) {
			iMultiLineCellEditor.cancelCellEditing();
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

				// emulate modal
				LCmain.mainFrame.setEnabled(false);

				super.setVisible(visible);

			} catch (LangCardsException e) {
				LCmain.mainFrame.showErr(e);
			}
		}
	}
	
	public LngCard getLngCard() {
		return iLngCard;
	}

	public interface EditCardListener {
		void onSaveCard(LngCard lngCard);
	}
}