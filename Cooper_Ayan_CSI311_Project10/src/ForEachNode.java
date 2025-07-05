
public class ForEachNode extends StatementNode{
	
	private Node initialize; // Statement before in
	private Node iterable; // Statement after in
	private BlockNode forEachBlock; // Block of code in for each loop
	
	public ForEachNode(Node initialize, Node iterable, BlockNode forEachBlock) {
		this.initialize = initialize;
		this.iterable = iterable;
		this.forEachBlock = forEachBlock;
	}
	
	public Node getIterable() {
		return iterable;
	}
	
	public BlockNode getForEachBlock() {
		return forEachBlock;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("for (");
		sb.append(initialize);
		sb.append(" in ");
		sb.append(iterable.toString());
		sb.append(") {\n");
		sb.append(forEachBlock.toString());
		sb.append("}" + " ");
		return sb.toString();
	}

}
