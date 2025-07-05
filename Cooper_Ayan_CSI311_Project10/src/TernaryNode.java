
public class TernaryNode extends Node{
	
	private Node condition; // expr1
	private Node trueCase; // expr2
	private Node falseCase; // expr3
	
	public TernaryNode (Node condition, Node trueCase, Node falseCase) {
		this.condition = condition;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}
	
	public Node getCondition() {
		return condition;
	}
	
	public Node getTrueCase() {
		return trueCase;
	}
	
	public Node getFalseCase() {
		return falseCase;
	}
	
	@Override
	public String toString() {
		return condition.toString() + " ? " + trueCase.toString() + " : " + falseCase.toString();	
	}

}
