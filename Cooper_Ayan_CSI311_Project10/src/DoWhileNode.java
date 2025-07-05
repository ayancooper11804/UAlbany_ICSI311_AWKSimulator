
public class DoWhileNode extends StatementNode{
	
	private Node condition; // Condition inside do while statement
	private BlockNode statements; // Statements inside do while loop
	
	public DoWhileNode(Node condition, BlockNode statements) {
		this.condition = condition;
		this.statements = statements;
	}
	
	public Node getCondition() {
		return condition;
	}
	
	public BlockNode getStatements() {
		return statements;
	}
	
	@Override
	public String toString() {
		return "do {\n" + statements.toString() + "} while (" + condition.toString() + ");" + " ";
	}

}
