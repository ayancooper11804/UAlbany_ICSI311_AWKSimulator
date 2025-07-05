import java.util.LinkedList;
import java.util.Optional;

public class FunctionDefinitionNode extends Node{
	private String functionName; // Holds the function name
	private LinkedList<String> parameters; // Collection of parameter name(s)
	private LinkedList<StatementNode> statements; // Collection of statements
	
	public FunctionDefinitionNode(String functionName) {
		this.functionName = functionName;
		this.parameters = new LinkedList<>();
		this.statements = new LinkedList<>();
	}
	
	public FunctionDefinitionNode() {
		this.functionName = null;
		this.parameters = new LinkedList<>();
		this.statements = new LinkedList<>();
	}
	
	// Adds parameter
	public void addParameter(String parameterName) {
		parameters.add(parameterName);
	}
	
	// Add statements
	public void addStatements(BlockNode block) {
		this.statements.addAll(block.getStatements());
	}
	
	public String getFuncName() {
		return functionName;
	}
	
	public LinkedList<String> getParameters() {
		return parameters;
	}
	
	public int getParameterCount() {
		return parameters.size();
	}
	
	public LinkedList<StatementNode> getStatements() {
		return statements;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("function ").append(functionName).append(" (");
		
		// Append parameters
		for (String parameter : parameters) {
			sb.append(parameter).append(", ");
		}
		
		// Remove the trailing comma and space if parameters exist
		if (!parameters.isEmpty()) {
			sb.setLength(sb.length() - 2);
		}
		
		sb.append(") {\n");
		
		// Append statements from the block
		sb.append(statements.toString());
		
		sb.append("\n}");
		
		return sb.toString();
	}
	
}
