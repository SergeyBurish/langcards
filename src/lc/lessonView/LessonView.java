package lc.lessonView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
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
	
	JLabel iLabel1 = null;
	
	ImageIcon iNegative = null;
	ImageIcon iPondering = null;
	ImageIcon iPositive = null;
	
	int z = 0;
	
	public LessonView(CardSet set) throws XPathExpressionException {
		iSet = set;
		iLesson = iSet.newLesson();
		
		java.net.URL imgURL = LCmain.class.getResource("/resources/images/Negative.png");
        if (imgURL != null) {
        	iNegative = new ImageIcon(imgURL, "");
        }
        
		imgURL = LCmain.class.getResource("/resources/images/Pondering.png");
        if (imgURL != null) {
        	iPondering = new ImageIcon(imgURL, "");
        }
        
		imgURL = LCmain.class.getResource("/resources/images/Positive.png");
        if (imgURL != null) {
        	iPositive = new ImageIcon(imgURL, "");
        }
	}
	
	public void Show() throws LangCardsExeption, XPathExpressionException {
		LngCard firstLessonCard = iLesson.NextCard(); // iSet.FirstLessonCard();
		if (firstLessonCard == null) {
			throw new LangCardsExeption("No lesson cards");
		}
		
		
		/*
		ImageIcon icon2 = null;
		java.net.URL imgURL = LCmain.class.getResource("resources/images/cert-stamp.jpg");
        if (imgURL != null) {
        	icon2 = new ImageIcon(imgURL, "");
        }
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("resources/images/cert-stamp.jpg");
        
        java.net.URL imgURL2 =classLoader.getResource("resources/images/cert-stamp.jpg");
        Image logo;
        
        if (input != null) {
    		try {
    			logo = ImageIO.read(input);
    			icon = new ImageIcon( logo );
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
		} else {
			icon2 = new ImageIcon(imgURL2, "");
		}*/

		
		
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
		
		JTextPane textPane = new JTextPane();
		textPane.setText("Type your variant of translation here");
		
		JButton checkNextBtn = new JButton("Next Card");
		checkNextBtn.addActionListener(this);
		JButton finishLessonBtn = new JButton("Finish Lesson");
		
		iLabel1 = new JLabel("Test1", iNegative, JLabel.CENTER);
		iLabel1.setVerticalTextPosition(JLabel.TOP);
		iLabel1.setHorizontalTextPosition(JLabel.CENTER);
		JLabel label2 = new JLabel("Test2");
		
		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(iTree)
						.addComponent(textPane)
						.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
								.addComponent(checkNextBtn)
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
						.addComponent(iTree)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(textPane)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(LCmain.mainFrame.iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(checkNextBtn)
								.addComponent(finishLessonBtn)
								)
						)
				.addGroup(LCmain.mainFrame.iLayout.createSequentialGroup()
						.addComponent(iLabel1)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(label2)
						)
		);
		
		LCmain.mainFrame.pack();
	}
	
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCmd = event.getActionCommand();
		if (actionCmd.equals("Next Card")) {
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
	}
}
