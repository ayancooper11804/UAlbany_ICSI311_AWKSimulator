
public class ReturnNode extends StatementNode{
	
	private Node returnValue; // Value to be returned
	
	public ReturnNode(Node returnValue) {
		this.returnValue = returnValue;
	}
	
	public Node getReturnValue() {
		return returnValue;
	}
	
	// Used to check if there is a value to be evaluated
	public boolean hasValue() {
		return returnValue != null;
	}
	
	@Override
	public String toString() {
		if (returnValue != null) {
			return "return " + returnValue.toString() + " ";
		}
		else {
			return "return" + " ";
		}
	}

}
