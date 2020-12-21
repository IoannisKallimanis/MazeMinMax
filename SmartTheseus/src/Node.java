import java.util.ArrayList;

public class Node {
		
	Node parent;
	ArrayList<Node> children;
	int nodeDepth;
	int[] nodeMove;
	Board nodeBoard;
	double nodeEvaluation;
	
	public Node() 
	{
		parent = null;
		children = new ArrayList<Node>();
		nodeDepth = 0;
		nodeMove = new int[0];
		nodeBoard = new Board();
		nodeEvaluation = Double.NEGATIVE_INFINITY;
	}
	
	public Node(Node parent, ArrayList<Node> children, int nodeDepth, int[] nodeMove, Board nodeBoard, double nodeEvaluation)
	{
		this.parent = new Node(parent);
		if(children.size() != 0) {
			this.children = new ArrayList<Node>(children.size());
			for(int i = 0; i < children.size(); i++) {
				this.children.add(children.get(i));
			}
		}
		else {
			this.children = new ArrayList<Node>();
		}
		this.nodeDepth = nodeDepth;
		if(nodeMove.length != 0) {
			this.nodeMove = new int[nodeMove.length];
			for(int i = 0; i < nodeMove.length; i++) {
				this.nodeMove[i] = nodeMove[i];
			}
		}
		else {
			this.nodeMove = new int[0];
		}
		this.nodeBoard = new Board(nodeBoard);
		this.nodeEvaluation = nodeEvaluation;
	}
	
	public Node(Node node)
	{
		if(node.getNodeDepth() == 0)
			parent = null;
		else
			parent = new Node(node.getParent());
		if(node.getChildren().size() != 0) {
			children = new ArrayList<Node>(node.getChildren().size());
			for(int i = 0; i < children.size(); i++) {
				children.add(node.getChildren().get(i));
			}
		}
		else {
			children = new ArrayList<Node>();
		}
		nodeDepth = node.getNodeDepth();
		if(node.getNodeMove().length != 0) {
			nodeMove = new int[node.getNodeMove().length];
			for(int i = 0; i < node.getNodeMove().length; i++) {
				nodeMove[i] = node.getNodeMove()[i];
			}
		}
		else {
			nodeMove = new int[0];
		}
		nodeBoard = new Board(node.getNodeBoard());
		nodeEvaluation = node.getNodeEvaluation();
	}
	
	public Node getParent()
	{
		if(nodeDepth == 0) {
			System.out.println("Error, this node is root, has no parents");
			System.exit(1);
		}
		return parent;
	}
	
	public ArrayList<Node> getChildren()
	{
		return children;
	}
	
	public int getNodeDepth()
	{
		return nodeDepth;
	}
	
	public int[] getNodeMove()
	{
		return nodeMove;
	}
	
	public Board getNodeBoard() 
	{
		return nodeBoard;
	}
	
	public double getNodeEvaluation()
	{
		return nodeEvaluation;
	}
	
	public void setParent(Node parent)
	{
		this.parent = new Node(parent);
	}
	
	public void setChildren(ArrayList<Node> children)
	{
		if(children.size() != 0) {
			this.children = new ArrayList<Node>(children.size());
			for(int i = 0; i < children.size(); i++) {
				this.children.add(children.get(i));
			}
		}
		else {
			this.children = new ArrayList<Node>();
		}
	}
	
	public void setNodeDepth(int nodeDepth)
	{
		this.nodeDepth = nodeDepth;
	}
	
	public void setNodeMove(int[] nodeMove) {
		if(nodeMove.length != 0) {
			this.nodeMove = new int[nodeMove.length];
			for(int i = 0; i < nodeMove.length; i++) {
				this.nodeMove[i] = nodeMove[i];
			}
		}
		else {
			this.nodeMove = new int[0];
		}
	}
	
	public void setNodeBoard(Board nodeBoard)
	{
		this.nodeBoard = new Board(nodeBoard);
	}
	
	public void setNodeEvaluation(double nodeEvaluation)
	{
		this.nodeEvaluation = nodeEvaluation;
	}
	
}
