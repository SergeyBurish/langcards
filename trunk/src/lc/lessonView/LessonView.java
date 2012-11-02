package lc.lessonView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;
import lc.LCmain;
import lc.cardSet.CardSet;

public class LessonView implements ActionListener {
	CardSet iSet;
	
	public LessonView(CardSet set) {
		iSet = set;
	}
	
	public void Show() {
		LCmain.mainFrame.setTitle(iSet.Name() + " Lesson");
		//setJMenuBar(null); // remove menu
		
		LCmain.mainFrame.iContainer.removeAll(); // remove all ui controls
		
		JTree tree = new JTree();
		
		LCmain.mainFrame.iLayout.setHorizontalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(tree)
		);
				
		LCmain.mainFrame.iLayout.setVerticalGroup(
				LCmain.mainFrame.iLayout.createSequentialGroup()
				.addComponent(tree)
		);
		
		LCmain.mainFrame.pack();
	}
	
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent event) {
	}
}
