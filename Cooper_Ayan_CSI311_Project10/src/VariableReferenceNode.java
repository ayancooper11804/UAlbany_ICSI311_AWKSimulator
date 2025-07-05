import java.util.Optional;

public class VariableReferenceNode extends Node{
	
	private String variableName; // Name of the variable
	private Optional<Node> indexExpression; // Optional index expression
	
	// Various constructors for whether index expression exists
	public VariableReferenceNode(String variableName, Optional<Node> indexExpression) {
		this.variableName = variableName;
		this.indexExpression = indexExpression;
	}
	
	public VariableReferenceNode(String variableName) {
		this.variableName = variableName;
		indexExpression = Optional.empty();
	}
	
	// Accessor for variable name
	public String getVariableName() {
		return variableName;
	}
	
	// Accessor for index expression
	public Optional<Node> getIndexExpression() {
		return indexExpression;
	}
	
	public boolean isArrayReference() {
		return indexExpression != null;
	}
	
	// Return variable name and index expression as a string
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(variableName);
		if (indexExpression.isPresent()) {
			sb.append("["+indexExpression.get().toString()+"]");
		}
		return sb.toString();
	}

}
