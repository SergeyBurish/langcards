package lc.lessonView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.xpath.XPathExpressionException;

import lc.LCmain;
import lc.cardSet.CardSet;
import lc.cardSet.Lesson;
import lc.cardSet.lngCard.LngCard;
import lc.editView.editCardDlg.exTree.ExTree;
import lc.editView.editCardDlg.exTree.ExTreeNode;
import lc.editView.editCardDlg.exTree.MultiLineCellRenderer;
import lc.langCardsExeption.LangCardsExeption;

public class LessonView implements ActionListener {
	CardSet iSet;
	Lesson iLesson;
	
	public LessonView(CardSet set) throws XPathExpressionException {
		iSet = set;
		iLesson = iSet.newLesson();
	}
	
	public void Show() throws LangCardsExeption, XPathExpressionException {
		LngCard firstLessonCard = iLesson.NextCard(); // iSet.FirstLessonCard();
		if (firstLessonCard == null) {
			throw new LangCardsExeption("No lesson cards");
		}
		
		LCmain.mainFrame.setTitle(iSet.Name() + " Lesson");
		//setJMenuBar(null); // remove menu
		
		LCmain.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		ExTreeNode rootNode = new ExTreeNode("Card 1", false);
		DefaultTreeModel iModel = new DefaultTreeModel(rootNode);
		ExTree iTree = new ExTree(iModel);
		
		iTree.setCellRenderer(new MultiLineCellRenderer());
		iTree.setEditable(true);
		iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		ExTreeNode word1 = new ExTreeNode(firstLessonCard.GetFromPhrase(0), false);
		
//		ExTreeNode trans3 = new ExTreeNode("trans3", false);
//		ExTreeNode examp31 = new ExTreeNode("Exampl31", false);
//		ExTreeNode examp32 = new ExTreeNode("Exampl32", false);
//		
//		ExTreeNode exampVal31 = new ExTreeNode("Exampl Text 31 1111111111111\n111", false);
//		ExTreeNode exampVal32 = new ExTreeNode("Exampl Text 32 2222222222222222\n222\n222222222", false);

		
		rootNode.add(word1);
		
		//expand all nodes
		for (int i = 0; i < iTree.getRowCount(); i++) {
			iTree.expandRow(i);
		}
		
//		word1.add(trans3);
//		word1.add(examp31);
//		word1.add(examp32);
//		
//		examp31.add(exampVal31);
//		examp32.add(exampVal32);
		
		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(iTree)
		);
				
		LCmain.mainFrame.iLayout.setVerticalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(iTree)
		);
		
		LCmain.mainFrame.pack();
	}
	
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent event) {
	}
}
