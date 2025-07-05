import java.util.HashMap;

public class InterpreterArrayDataType extends InterpreterDataType{

	// A HashMap to store elements of the array
	private HashMap<String, InterpreterDataType> elements;
	
	public InterpreterArrayDataType(HashMap<String, InterpreterDataType> elements) {
		this.elements = elements;
	}
	
	// Get an array element by it's index
	public InterpreterDataType getArrayElement(String index) {
		return elements.get(index);
	}
	
	// Set an array element at a specific index
	public void setArrayElements(String index, InterpreterDataType value) {
		elements.put(index, value);
	}
	
	public HashMap<String, InterpreterDataType> getElements() {
		return elements;
	}

}
