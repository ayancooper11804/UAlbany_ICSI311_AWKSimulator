import java.util.LinkedList;

public class DeleteNode extends StatementNode{
	
	private Node target; // Target array used by delete
	private LinkedList<ConstantNode> indices; // Indices to be deleted
	
	public DeleteNode(Node target) {
		this.target = target;
	}
	
	public Node getTarget() {
		return target;
	}
	
	public LinkedList<ConstantNode> getIndices() {
		return indices;
	}
	
	@Override
	public String toString() {
		return "delete " + target.toString() + " ";
	}

}
