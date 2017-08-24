
public class BinaryTreeNode {
String value;
BinaryTreeNode Left;
BinaryTreeNode Right;

public BinaryTreeNode(String value){
	this.value=value;
	this.Left=null;
	this.Right=null;	
}
public BinaryTreeNode(String value,BinaryTreeNode Left,BinaryTreeNode Right){
	this.value=value;
	this.Left=Left;
	this.Right=Right;
}
public static boolean isLeaf(BinaryTreeNode node){
	return node.Left == null ? node.Right == null:false;	
}

public static void printleaf(BinaryTreeNode node) {
	if(node == null) {
		return;
	}
	if (isLeaf(node)) {
		System.out.println(node.value);
	}
	
	printleaf(node.Left);
	printleaf(node.Right);
}
}
