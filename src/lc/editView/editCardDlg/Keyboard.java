package lc.editView.editCardDlg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by sergey.burish on 19.08.2015.
 */

public class Keyboard extends JFrame {

	private JPanel base = new JPanel(new GridLayout(0, 1));
	private JPanel[] panel;
	private JButton[][] button;
	private String[][] key = null;

	KBListener listener;

	public Keyboard(String keys, KBListener listener) {

		String[] keyRows = keys.split("\\n");
		key = new String[keyRows.length][];

		for (int row = 0; row < keyRows.length; row++) {
			key[row] = keyRows[row].split("");
		}

		this.listener = listener;
	}

	public void init() {
		setUndecorated(true);
		panel = new JPanel[6];
		for (int row = 0; row < key.length; row++) {
			panel[row] = new JPanel();
			button = new JButton[20][20];
			for (int column = 0; column < key[row].length; column++) {
				button[row][column] = new JButton(key[row][column]);
				button[row][column].putClientProperty("column", column);
				button[row][column].putClientProperty("row", row);
				button[row][column].putClientProperty("key", key[row][column]);
				button[row][column].addActionListener(new MyActionListener());
				panel[row].add(button[row][column]);
			}
			base.add(panel[row]);
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(base);
		pack();
	}

	public void updatePosition(Component parent) {
		Point locationOnScreen = parent.getLocationOnScreen();
		int height = parent.getHeight();
		setLocation(locationOnScreen.x, locationOnScreen.y + height + 5);
	}

	private class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			listener.keyPressed("" + btn.getClientProperty("key"));
		}
	}

	public interface KBListener {
		void keyPressed(String key);
	}
}
