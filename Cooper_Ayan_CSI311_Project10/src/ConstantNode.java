
public class ConstantNode extends Node{

private String value; // Holds one value
	
	public ConstantNode(String value) {
		this.value = value;
	}
	
	// Accessor for value
	public String getValue() {
		return value;
	}
	
	// Returns the value as a string
	@Override
	public String toString() {
		return value;
	}
}
