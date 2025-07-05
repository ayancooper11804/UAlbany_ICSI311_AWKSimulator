
public class InterpreterDataType {
	
	private String value; // String value of the data type
	
	// Constructor without initial value supplied
	public InterpreterDataType() {
		this.value = "";
	}
	
	// Constructor with initial value supplied
	public InterpreterDataType(String value) {
		this.value = value;
	}
	
	// Get the string value of the data type
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	public String toString() {
		return value;
	}
}
