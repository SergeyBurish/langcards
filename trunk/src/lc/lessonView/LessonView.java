package lc.lessonView;

import lc.LCmain;
import lc.LCutils;
import lc.cardSet.CardSet;
import lc.cardSet.Lesson;
import lc.cardSet.lngCard.LngCard;
import lc.cardSet.lngPhrase.LngPhrase;
import lc.controls.TextPaneWithDefault;
import lc.editView.editCardDlg.exTree.ExTree;
import lc.editView.editCardDlg.exTree.ExTreeNode;
import lc.editView.editCardDlg.exTree.MultiLineCellRenderer;
import lc.langCardsException.LangCardsException;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.xpath.XPathExpressionException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LessonView {
	CardSet iSet;
	Lesson iLesson;
	
	JLabel iLabel1 = null;
	
	ImageIcon iNegative = null;
	ImageIcon iPondering = null;
	ImageIcon iPositive = null;
	
	ExTree iTree = null;
	JScrollPane iTreeScrollPane = null;
	ExTreeNode iCardNode = new ExTreeNode(null, false);
	
	int z = 0;
	
	public LessonView(CardSet set) throws XPathExpressionException {
		iSet = set;
		iLesson = iSet.newLesson();
		
		iNegative = LCutils.Image("Negative.png");
		iPondering = LCutils.Image("Pondering.png");
		iPositive = LCutils.Image("Positive.png");
	}
	
	public void Show() throws LangCardsException, XPathExpressionException {
		
		LCmain.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		iTree = new ExTree(new DefaultTreeModel(iCardNode));
		
		iTree.setCellRenderer(new MultiLineCellRenderer());
		iTree.setEditable(true);
		iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		iTreeScrollPane = new JScrollPane(iTree);

		// correct sizes
		iTreeScrollPane.getViewport().setPreferredSize(iTree.getPreferredSize());

		final JButton verifyNextBtn = new JButton(LCutils.String("Next_Card"));
		verifyNextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					NextCard();
				}
				catch (XPathExpressionException e)	{LCmain.mainFrame.ShowErr(e);}
				catch (LangCardsException e)		{LCmain.mainFrame.ShowErr(e);}

				switch (z) {
					case 0:
						iLabel1.setText("Pondering");
						iLabel1.setIcon(iPondering);
						z = 1;
						break;

					case 1:
						iLabel1.setText("Positive");
						iLabel1.setIcon(iPositive);
						z = 2;
						break;

					case 2:
						iLabel1.setText("Negative");
						iLabel1.setIcon(iNegative);
						z = 0;
						break;

					default:
						break;
				}

				LCmain.mainFrame.pack();
			}
		});

		TextPaneWithDefault textPane = new TextPaneWithDefault(LCutils.String("Type_your_variant_of_translation_here"),
				new TextPaneWithDefault.TypingStateListener() {
					@Override
					public void typingStarted() {
						verifyNextBtn.setText(LCutils.String("Verify"));
					}

					@Override
					public void typingStopped() {
						verifyNextBtn.setText(LCutils.String("Next_Card"));
					}
				});

		JButton finishLessonBtn = new JButton(LCutils.String("Finish_Lesson"));
		
		iLabel1 = new JLabel("Test1", iNegative, JLabel.CENTER);
		iLabel1.setVerticalTextPosition(JLabel.TOP);
		iLabel1.setHorizontalTextPosition(JLabel.CENTER);
		JLabel label2 = new JLabel("Test2");
		
		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(iTreeScrollPane)
						.addComponent(textPane)
						.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
								.addComponent(verifyNextBtn)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(finishLessonBtn)
								)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(iLabel1)
						.addComponent(label2)
						)
		);
				
		LCmain.mainFrame.iLayout.setVerticalGroup(
				LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
						.addComponent(iTreeScrollPane)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(textPane)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(verifyNextBtn)
								.addComponent(finishLessonBtn)
								)
						)
				.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
						.addComponent(iLabel1)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(label2)
						)
		);
		
		NextCard();
		
		LCmain.mainFrame.pack();
	}

	private void NextCard() throws XPathExpressionException, LangCardsException {
		LngCard nextCard = iLesson.NextCard();
		if (nextCard == null) {
			throw new LangCardsException("No lesson cards");
		}
		
		iCardNode.setUserObject(LCutils.String("Card") + " " + iLesson.CurrentCardPos());
		iCardNode.removeAllChildren();
		
		for (int i = 0; i < nextCard.FrstPhraseCount(); i++) {
			ExTreeNode phraseNode = LngPhraseToTreeNode(nextCard.GetFrstPhrase(i));
			iCardNode.add(phraseNode);
		}
		
		iTree.updateUI();
		iTreeScrollPane.getViewport().setPreferredSize(iTree.getPreferredSize());
	}
	
	private ExTreeNode LngPhraseToTreeNode(LngPhrase phrase) {
		ExTreeNode phraseNode = new ExTreeNode(phrase.iValue, false);
		
		if (phrase.iTranscription != null && !phrase.iTranscription.isEmpty()) {
			ExTreeNode transcrNode = new ExTreeNode(phrase.iTranscription, false);
			phraseNode.add(transcrNode);
		}

		if (phrase.iExamples.size() > 0) {
			ExTreeNode examples = new ExTreeNode(LCutils.String("Examples"), false);
			for (int j = 0; j < phrase.iExamples.size(); j++) {
				ExTreeNode exampleNode = new ExTreeNode(phrase.iExamples.get(j), false);
				examples.add(exampleNode);
			}			
			phraseNode.add(examples);
		}
		
		return phraseNode;
	}
}
