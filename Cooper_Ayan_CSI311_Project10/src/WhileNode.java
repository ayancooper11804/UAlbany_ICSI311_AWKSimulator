
public class WhileNode extends StatementNode{
	
	private Node condition; // Condition inside while statement
	private BlockNode statements; // Statements inside while loop
	
	public WhileNode(Node condition, BlockNode statements) {
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
		return "while (" + condition.toString() + ") {\n" + statements.toString() + "}" + " ";
	}

}
