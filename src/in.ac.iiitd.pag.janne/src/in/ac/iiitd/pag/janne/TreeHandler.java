package in.ac.iiitd.pag.janne;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeHandler {
	public static void removeAllNodes(JTree tree) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		root.removeAllChildren();
		tree.updateUI();
	}
	public static void addNodes(JTree tree, String filePath, List<String> nodes) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		root.setUserObject(StringUtil.processRootText(filePath));
		for(String node: nodes) {
			DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(node);
			root.add(contentNode);
		}
		tree.updateUI();
	}
}
