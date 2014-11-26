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
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LessonView {
	CardSet iSet;
	Lesson iLesson;
	
	JLabel iAnswerStatusLabel = null;
	
	ImageIcon iNegative = null;
	ImageIcon iPondering = null;
	ImageIcon iPositive = null;
	
	ExTree iTree = null;
	JScrollPane iTreeScrollPane = null;
	ExTreeNode iCardNode = new ExTreeNode(null, false);

	private LngCard iCurrentCard = null;
	private JButton iVerifyNextBtn = null;
	private TextPaneWithDefault iAnswerTextPane = null;
	private JTextPane iCorrectAnswersTextPane;

	enum LessonStatus {ANSWER_TYPING, ANSWERED}
	private LessonStatus iLessonStatus = LessonStatus.ANSWER_TYPING;

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

		iVerifyNextBtn = new JButton(LCutils.String("Verify"));
		iVerifyNextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					switch (iLessonStatus) {
						case ANSWER_TYPING:
							VerifyAnswer(iAnswerTextPane.getText());
							break;

						default: // ANSWERED
							NextCard();
					}
				}
				catch (XPathExpressionException e)	{LCmain.mainFrame.ShowErr(e);}
				catch (LangCardsException e)		{LCmain.mainFrame.ShowErr(e);}
				catch (TransformerException e)		{LCmain.mainFrame.ShowErr(e);}
			}
		});

		iAnswerTextPane = new TextPaneWithDefault(LCutils.String("Type_your_variant_of_translation_here"), null);
		iCorrectAnswersTextPane = new JTextPane();
		iCorrectAnswersTextPane.setText(LCutils.String("Correct_answers"));
		iCorrectAnswersTextPane.setEditable(false);

		JButton finishLessonBtn = new JButton(LCutils.String("Finish_Lesson"));
		
		iAnswerStatusLabel = new JLabel(LCutils.String("Question_mark"), iPondering, JLabel.CENTER);
		iAnswerStatusLabel.setVerticalTextPosition(JLabel.TOP);
		iAnswerStatusLabel.setHorizontalTextPosition(JLabel.CENTER);

		JLabel label2 = new JLabel("N cards left in lesson");

		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
						.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(iTreeScrollPane)
										.addComponent(iAnswerTextPane)
										.addComponent(iCorrectAnswersTextPane)
										.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
														.addComponent(iVerifyNextBtn)
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(finishLessonBtn)
										)
						)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(iAnswerStatusLabel)
										.addComponent(label2)
						)
		);

		LCmain.mainFrame.iLayout.setVerticalGroup(
				LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
										.addComponent(iTreeScrollPane)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(iAnswerTextPane)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(iCorrectAnswersTextPane)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(iVerifyNextBtn)
														.addComponent(finishLessonBtn)
										)
						)
						.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
										.addComponent(iAnswerStatusLabel)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(label2)
						)
		);
		
		NextCard();
	}

	private void VerifyAnswer(String answer) throws LangCardsException, XPathExpressionException, TransformerException {
		if (iCurrentCard == null) throw new LangCardsException("No lesson cards");

		String correctAnswers = "";
		boolean correct = false;
		for (int i = 0; i < iCurrentCard.ScndPhraseCount(); i++) {

			final String value = iCurrentCard.GetScndPhrase(i).iValue;
			correctAnswers += String.format("%d) %s; ", i+1, value);

			String s = value.toLowerCase().trim();
			if (s.equals(answer.toLowerCase().trim())) {
				correct = true;
			}
		}

		iCorrectAnswersTextPane.setText(LCutils.String("Correct_answers") + correctAnswers);

		if (correct) {
			iAnswerStatusLabel.setText(LCutils.String("Correct"));
			iAnswerStatusLabel.setIcon(iPositive);
			iLesson.markCorrect(iCurrentCard);
		}
		else {
			iAnswerStatusLabel.setText(LCutils.String("Wrong"));
			iAnswerStatusLabel.setIcon(iNegative);
			iLesson.markWrong(iCurrentCard);
		}

		iVerifyNextBtn.setText(LCutils.String("Next_Card"));
		iLessonStatus = LessonStatus.ANSWERED;
		iAnswerTextPane.hideDefaultString();
		iAnswerTextPane.setEditable(false);
	}

	private void NextCard() throws XPathExpressionException, LangCardsException {
		iCurrentCard = iLesson.NextCard();
		if (iCurrentCard == null) {
			throw new LangCardsException("No lesson cards");
		}
		
		iCardNode.setUserObject(LCutils.String("Card") + " " + iLesson.CurrentCardPos());
		iCardNode.removeAllChildren();
		
		for (int i = 0; i < iCurrentCard.FrstPhraseCount(); i++) {
			ExTreeNode phraseNode = LngPhraseToTreeNode(iCurrentCard.GetFrstPhrase(i));
			iCardNode.add(phraseNode);
		}
		
		iTree.updateUI();
		iTreeScrollPane.getViewport().setPreferredSize(iTree.getPreferredSize());

		iCorrectAnswersTextPane.setText(LCutils.String("Correct_answers"));
		iVerifyNextBtn.setText(LCutils.String("Verify"));
		iLessonStatus = LessonStatus.ANSWER_TYPING;
		iAnswerTextPane.setEditable(true);
		iAnswerTextPane.setText("");
		iAnswerStatusLabel.setText(LCutils.String("Question_mark"));
		iAnswerStatusLabel.setIcon(iPondering);
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
