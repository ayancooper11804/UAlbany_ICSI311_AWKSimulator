import java.util.HashMap;
import java.util.LinkedList;

public class ReturnType {
	
	// Enum of return types
	public enum returnTypes {
		NONE, BREAK, CONTINUE, RETURN
	}
	
	private String value; // Return value
	private returnTypes type; // Return type
	
	// Constructors for only an enum and and enum with a string
	public ReturnType(returnTypes type) {
		this.type = type;
	}
	
	public ReturnType(returnTypes type, String value) {
		this.type = type;
		this.value = value;
	}
	
	// Accessor for the return type
	public returnTypes getReturnType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	
	public String toString() {
		if (type == returnTypes.RETURN) {
			if (value != null) {
				return "return " + value;
			}
			else {
				return "return ";
			}
		}
		else {
			return type.toString();
		}
	}
	
}
