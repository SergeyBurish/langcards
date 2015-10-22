package lc.editView.editCardDlg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Keyboard extends JFrame {

	private JPanel base = new JPanel(new GridLayout(0, 1));
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
		for (int row = 0; row < key.length; row++) {
			JPanel panel = new JPanel();
			for (int column = 0; column < key[row].length; column++) {
				JButton button = new JButton(key[row][column]);
				button.putClientProperty("column", column);
				button.putClientProperty("row", row);
				button.putClientProperty("key", key[row][column]);
				button.addActionListener(new MyActionListener());
				panel.add(button);
			}
			base.add(panel);
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		base.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
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
