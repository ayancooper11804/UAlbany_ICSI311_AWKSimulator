import java.util.LinkedList;

public class FunctionCallNode extends StatementNode{
	
	private String functionName;
	private LinkedList<Node> parameters;
	
	public FunctionCallNode(String functionName, LinkedList<Node> parameters) {
		this.functionName = functionName;
		this.parameters = parameters;
	}
	
	public FunctionCallNode(String functionName) {
		this.functionName = functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public LinkedList<Node> getParameters() {
		return parameters;
	}
	
	public int getParameterCount() {
		return parameters.size();
	}
	
	public void addParameters(LinkedList<Node> params) {
		parameters = params;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(functionName);
		
		if (parameters != null) {
			sb.append("(");
			for (int i = 0; i < parameters.size(); i++) {
				sb.append(parameters.get(i).toString());
				if (i < parameters.size() - 1) {
					sb.append(", ");
				}
			}
			sb.append(")");
			sb.append(" ");
			return sb.toString();
		}
		else {
			return sb.toString();
		}
	}

}
