
public class PrintLeavesOfBinaryTree {
	
	public static void main(String args[]) {
		BinaryTreeNode a = new BinaryTreeNode("5");
		BinaryTreeNode b = new BinaryTreeNode("4");
		BinaryTreeNode c = new BinaryTreeNode("3");
		BinaryTreeNode d = new BinaryTreeNode("2",b,c);
		BinaryTreeNode e = new BinaryTreeNode("1",d,a);
		
		BinaryTreeNode.printleaf(e);
	}
}