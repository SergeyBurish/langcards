package lc.editView.editCardDlg.exTree;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class MultiLineCellRenderer implements TreeCellRenderer {
	private static final int GAP_WIDTH = 4;

	private JPanel panel;
	protected JLabel icon;
	protected TreeTextArea text;

	public MultiLineCellRenderer() {
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		icon = new JLabel() {
			public void setBackground(Color color) {
				if (color instanceof ColorUIResource)
					color = null;
				super.setBackground(color);
			}
		};
		panel.add(icon);
		panel.add(Box.createHorizontalStrut(GAP_WIDTH));
		panel.add(text = new TreeTextArea());

		return panel;
	}

	@Override
	// TreeCellRenderer
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, isSelected,
				expanded, leaf, row, hasFocus);

		panel = createPanel();

		panel.setEnabled(tree.isEnabled());

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

		ExTreeNode node = null;
		String defaultString = null;
		if (value instanceof ExTreeNode) {
			node = (ExTreeNode)value;
			defaultString = node.getDefaultString();
		}

		if (defaultString != null && defaultString.contentEquals(stringValue)) {
			text.setForeground(text.getDisabledTextColor());
			if (node != null) {
				node.setChanged(false);
			}
		} else {
			text.setForeground(text.getSelectedTextColor());
			if (node != null) {
				node.setChanged(true);
			}
		}

		return panel;
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

		panel.setBackground(color);
	}

	class TreeTextArea extends JTextPane {
		Dimension preferredSize = null;
		JTextPane iDummy = new JTextPane(); // to recalculate text size only

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
