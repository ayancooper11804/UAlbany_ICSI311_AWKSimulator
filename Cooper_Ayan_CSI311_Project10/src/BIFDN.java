import java.util.HashMap;
import java.util.function.Function;

public class BIFDN extends FunctionDefinitionNode{

	// Lambda function to execute for the built-in function
	private Function<HashMap<String, InterpreterDataType>, String> Execute;
	// Indicates whether the function is variadic
	private boolean variadic;
	
	public BIFDN(boolean variadic, Function<HashMap<String, InterpreterDataType>, String> execute) {
		this.variadic = variadic;
		this.Execute = execute;
	}
	
	// Check if the function is variadic
	public boolean isVariadic() {
		return variadic;
	}
	
	// Execute the built-in function with it's parameters
	public String execute(HashMap<String, InterpreterDataType> parameters) {
		return Execute.apply(parameters);
	}
	
}
