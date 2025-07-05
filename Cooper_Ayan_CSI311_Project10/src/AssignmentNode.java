
public class AssignmentNode extends StatementNode{
	
	// Breaks up assignment operator (a+=5 -> a, OperationNode(a+5))
	private Node target;
	private Node expression;
	
	public AssignmentNode(Node target, Node expression) {
		this.target = target;
		this.expression = expression;
	}
	
	public Node getTarget() {
		return target;
	}
	
	public Node getExpression() {
		return expression;
	}

	@Override
//	public String toString() {
//		//return target.toString() + " = " + expression.toString();
//		String operator = "";
//		if (expression instanceof OperationNode) {
//			OperationNode operationNode = (OperationNode) expression;
//			if (operationNode.getOperation() == OperationNode.AWKOperation.EXPONENT) {
//				operator = " ^= ";
//			}
//			if (operationNode.getOperation() == OperationNode.AWKOperation.MODULO) {
//				operator = " %= ";
//			}
//			if (operationNode.getOperation() == OperationNode.AWKOperation.MULTIPLY) {
//				operator = " *= ";
//			}
//			if (operationNode.getOperation() == OperationNode.AWKOperation.DIVIDE) {
//				operator = " /= ";
//			}
//			if (operationNode.getOperation() == OperationNode.AWKOperation.ADD) {
//				operator = " += ";
//			}
//			if (operationNode.getOperation() == OperationNode.AWKOperation.SUBTRACT) {
//				operator = " -= ";
//			}
//			if (operationNode.getOperation() == OperationNode.AWKOperation.EQ) {
//				operator = " = ";
//			}
//		}
//		return target.toString() + operator + expression.toString();
//	}
	public String toString() {
		return target.toString() + expression.toString();
	}
}
