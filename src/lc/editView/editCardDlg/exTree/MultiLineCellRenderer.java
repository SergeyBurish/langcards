package lc.editView.editCardDlg.exTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import javax.swing.plaf.ColorUIResource;

public class MultiLineCellRenderer extends JPanel implements TreeCellRenderer {
	protected JLabel icon;
	protected TreeTextArea text;
	
	public MultiLineCellRenderer() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		icon = new JLabel() {
			public void setBackground(Color color) {
				if (color instanceof ColorUIResource)
					color = null;
				super.setBackground(color);
			}
		};
		add(icon);
		add(Box.createHorizontalStrut(4));
		add(text = new TreeTextArea());
	}

	@Override
	// TreeCellRenderer
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, isSelected,
				expanded, leaf, row, hasFocus);
		setEnabled(tree.isEnabled());
		text.setText(stringValue);
		text.setSelect(isSelected);
		text.setFocus(hasFocus);
		if (leaf) {
			icon.setIcon(UIManager.getIcon("Tree.leafIcon"));
		} else if (expanded) {
			icon.setIcon(UIManager.getIcon("Tree.openIcon"));
		} else {
			icon.setIcon(UIManager.getIcon("Tree.closedIcon"));
		}
		return this;
	}

	public Dimension getPreferredSize() {
		Dimension iconD = icon.getPreferredSize();
		Dimension textD = text.getPreferredSize();
		int height = iconD.height < textD.height ? textD.height : iconD.height;
		return new Dimension(iconD.width + textD.width, height);
	}

	public void setBackground(Color color) {
		if (color instanceof ColorUIResource)
			color = null;
		super.setBackground(color);
	}

	class TreeTextArea extends JTextArea {
		Dimension preferredSize;
		JTextPane iDummy = new JTextPane(); // to recalculate text size only

		TreeTextArea() {
			setLineWrap(true);
			setWrapStyleWord(true);
			setOpaque(true);
		}

		public void setBackground(Color color) {
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}

		public void setPreferredSize(Dimension d) {
			if (d != null) {
				preferredSize = d;
			}
		}

		public Dimension getPreferredSize() {
			return preferredSize;
		}

		public void setText(String str) {
			iDummy.setText(str);
			Dimension d = iDummy.getPreferredSize();
			setPreferredSize(d);
			
			super.setText(str);
		}

		void setSelect(boolean isSelected) {
			Color bColor;
			if (isSelected) {
				bColor = UIManager.getColor("Tree.selectionBackground");
			} else {
				bColor = UIManager.getColor("Tree.textBackground");
			}
			super.setBackground(bColor);
		}

		void setFocus(boolean hasFocus) {
			if (hasFocus) {
				Color lineColor = UIManager
						.getColor("Tree.selectionBorderColor");
				setBorder(BorderFactory.createLineBorder(lineColor));
			} else {
				setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			}
		}
	}
}
