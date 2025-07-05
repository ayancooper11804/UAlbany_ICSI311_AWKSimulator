import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends Node{
	private LinkedList<StatementNode> statements; // Statements
	private Optional<Node> condition; // Conditions
	
	public BlockNode() {
		statements = new LinkedList<>();
		condition = Optional.empty();
	}
	
	// Gets statements
	public LinkedList<StatementNode> getStatements() {
		return statements;
	}
	
	// Gets conditions (future use)
	public Optional<Node> getCondition() {
		return condition;
	}
	
	// Add conditions
	public void addCondition(Optional<Node> conditionNode) {
		condition = conditionNode;
	}
	
	// Adds statements
	public void addStatement(Node statementNode) {
		statements.add((StatementNode) statementNode);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//sb.append("BlockNode:");
		
		// Append conditions if they exist
		if (condition.isPresent()) {
			sb.append("").append(condition.get()).append("");
		}
		
		// Append statements
		for (StatementNode statement : statements) {
			sb.append(statement.toString()).append(" ");
		}
		
		// Remove the trailing comma and space if parameters exist
		if (!statements.isEmpty()) {
			sb.setLength(sb.length() - 2);
		}
		
		return sb.toString();
	}
}
