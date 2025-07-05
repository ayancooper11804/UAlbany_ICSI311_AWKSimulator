import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
	
	public class LineHandler {
		
		private List<String> lines;
		int NR = 0; // Record number
		int FNR = 0; // File record number
		
		
		public LineHandler(List<String> lines) {
			this.lines = lines;
		}
		
		public List<String> getLines() {
			return lines;
		}
		
		public void setLines(List<String> lines) {
			this.lines = lines;
			NR = 0; // Reset NR
			FNR = 0; // Reset FNR
		}
		
		public int getNR() {
			return NR;
		}
		
		public void setNR(int NR) {
			this.NR = NR;
		}
		
		public int getFNR() {
			return FNR;
		}
		
		public void setFNR(int FNR) {
			this.FNR = FNR;
		}
		
		public Boolean SplitAndAssign() {
			// Check if the record number is beyond the number of lines
			if (NR >= lines.size()) {
				return false; // No more lines to process
			}
			
			String line = lines.get(NR);
			if (line.isBlank()) {
				// Empty line encountered, increment NR and FNR
				NR++;
				FNR++;
				return false;
			}
			
			// Update the global variables with the current line and field separator
			globalVariables.put("$0", new InterpreterDataType(line));
			
			// Split the line into fields using the field separator (FS)
			String[] fields = line.split(globalVariables.get("FS").getValue());
			
			for (int i = 0; i < fields.length; i++) {
				// Update the global variables $1, $2, ..., $NF with field values
				globalVariables.put("$" + i + 1, new InterpreterDataType(fields[i]));
			}
			
			// Update $NF with the value of the last field
			globalVariables.put("$NF", new InterpreterDataType(fields[fields.length - 1]));
			NR++; // Increment record number
			FNR++; // Increment file record number
			return true;
		}

	}

	private LineHandler lineHandler; // Manages lines and record processing
	private HashMap <String, InterpreterDataType> globalVariables = new HashMap<String, InterpreterDataType>(); // Stores global variable
	private HashMap <String, FunctionDefinitionNode> functions = new HashMap<String, FunctionDefinitionNode>(); // Stores function definitions
	
	public Interpreter(ProgramNode programName, Path filePath) throws IOException {
		if (filePath != null) {
			// Load input lines from a file
			LinkedList<String> input = new LinkedList<String>();
			input.addAll(Files.readAllLines(filePath));
			lineHandler = new LineHandler(input); // Initialize line handler with input lines
			globalVariables.put("FILENAME", new InterpreterDataType(filePath.getFileName().toString()));
		}
		else {
			// No file path provided, initialize lineHandler with an empty list
			lineHandler = new LineHandler(new LinkedList<String>());
			globalVariables.put("FILENAME", new InterpreterDataType());
		}
		for(FunctionDefinitionNode fdnode : programName.getFunctions()) {
			functions.put(fdnode.getFuncName(), fdnode);
		}
		initializeGlobalVariables();
		initializeFunctions();
	}
	
	
	// Access LineHandler and it's methods
	public LineHandler getLineHandler() {
		return lineHandler;
	}
	
	// Helper method to populate the globalVariables HashMap in the constructor
	private void initializeGlobalVariables() {
		globalVariables.put("FS", new InterpreterDataType(" "));
		globalVariables.put("OFMT", new InterpreterDataType("%.6g"));
		globalVariables.put("OFS", new InterpreterDataType(" "));
		globalVariables.put("ORS", new InterpreterDataType("\n"));
	}
	
	// Helper method to populate the functions HashMap in the constructor
	private void initializeFunctions() {
		// Produce output with simple, standardized formatting. 
		Function<HashMap<String, InterpreterDataType>, String> printImplementation = (parameters) -> {
			StringBuilder output = new StringBuilder(); 			
			for (String s : parameters.keySet()) {
					output.append(parameters.get(s).toString());
					output.append(" "); // Append the parameter value to the output
			}
			System.out.print(output.toString());
			return ""; // Return an empty string as required by the Function interface
		};
		
		// You can specify the width to use for each item, as well as various formatting choices for numbers
		Function<HashMap<String, InterpreterDataType>, String> printfImplementation = (parameters) -> {
			if (parameters.containsKey("0")) { // Check if a format string is provided
				String format = parameters.get("0").getValue(); // Get the format string
				Object[] args = new Object[parameters.size() - 1]; // Create an array to store argument variables
				int index = 0; // Initialize the index for arguments
				
				// Convert parameter values to objects
				for (int i = 1; ; i++) {
					String parameterKey = String.valueOf(i); // Form the parameter key
					if (parameters.containsKey(parameterKey)) {
						args[index] = parameters.get(parameterKey).getValue(); // Get and store the parameter value
						index++;
					}
					else {
						break; // Exit the loop if no more parameters
					}
				}
				System.out.printf(format, args);
			}
			else {
				System.out.print(""); // Print nothing if format is not provided
			}
			return ""; // Return an empty string as required by the Function interface
		};
		
		// The sprintf() function is similar to the printf() function and uses the same format specifications 
	    // as printf(), with the only difference being that instead of printing the output on the screen, it returns 
		// a string that can be assigned to a variable
		Function<HashMap<String, InterpreterDataType>, String> sprintfImplementation = (parameters) -> {
			String format = parameters.get("0").getValue(); // Get the format string
			Object[] args = new Object[parameters.size() - 1]; // Create an array to store argument variables
			
			// Fill in the args array with parameters, skipping the first one (the format string)
			for (int i = 1; i < parameters.size(); i++) {
				args[i - 1] = parameters.get(String.valueOf(i)).getValue(); // Get and store parameter values
			}
			
			String result = String.format(format, args); // Format the output using the provided format and arguments
			return result; // Return the formatted result as a string
		};
		
		// The getline command returns 1 if it finds a record and 0 if it encounters the end of the file. 
		// If there is some error in getting a record, such as a file that cannot be opened, then getline returns −1.
		Function<HashMap<String, InterpreterDataType>, String> getlineImplementation = (parameters) -> {
			if (parameters != null && parameters.containsKey("0")) {
				throw new RuntimeException("getline does not accept parameters"); // Check if parameters are provided
			}
			
			if (lineHandler.SplitAndAssign()) {
				return "1"; // Successfully read a record
			}
			else if (lineHandler.getNR() <= lineHandler.getLines().size()) {
				return "0"; // End of file
			}
			else {
				return "-1"; // Error reading a record
			}
		};
		
		// The next statement forces awk to immediately stop processing the current record and go on to 
		// the next record. This means that no further rules are executed for the current record, and the 
		// rest of the current rule’s action isn’t executed.
		Function<HashMap<String, InterpreterDataType>, String> nextImplementation = (parameters) -> {
			if (lineHandler.SplitAndAssign()) {
				return "1"; // Successfully moved to the next record
			}
			return "0"; // End of file or error moving to the next record
		};
		
		// Replaces every occurrence of regex with the given string (sub). The third parameter is optional. 
		// If it is omitted, then $0 is used.
		Function<HashMap<String, InterpreterDataType>, String> gsubImplementation = (parameters) -> {
			InterpreterDataType regexTarget = parameters.get("0");
			InterpreterDataType subTarget = parameters.get("1");
			InterpreterDataType stringTarget = parameters.get("2");
			
			if (regexTarget == null || subTarget == null || stringTarget == null) {
				throw new RuntimeException("Incorrect parameters for gsub");
			}
			
			// Get values from parameters
			String regex = regexTarget.getValue();
			String sub = subTarget.getValue();
			String target = stringTarget.getValue();
			
			// Compile regex pattern and perform substitutions
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(target);
			String result = matcher.replaceAll(sub);
			
			return result;
		};
		
		// It returns the index of the first longest match of regex in string str. It returns 0 if no match found.
		Function<HashMap<String, InterpreterDataType>, String> matchImplementation = (parameters) -> {
			InterpreterDataType strTarget = parameters.get("0");
			InterpreterDataType regexTarget = parameters.get("1");
			
			if (strTarget == null || regexTarget == null) {
				throw new RuntimeException("Incorrect parameters for match");
			}
			
			// Get values from parameters
			String str = strTarget.getValue();
			String regex = regexTarget.getValue();
			
			// Compile regex pattern and create a matcher
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			
			if (matcher.find()) {
				return String.valueOf(matcher.start() + 1); // Adding 1 to match AWK's 
			}
			
			return "0"; // No match found
		};
		
		// This function performs a single substitution. It replaces the first occurrence of the regex pattern 
		// with the given string (sub). The third parameter is optional. If it is omitted, $0 is used.
		Function<HashMap<String, InterpreterDataType>, String> subImplementation = (parameters) -> {
			InterpreterDataType regexTarget = parameters.get("0");
			InterpreterDataType subTarget = parameters.get("1");
			InterpreterDataType stringTarget = parameters.get("2");
			
			if (regexTarget == null || stringTarget == null) {
				throw new RuntimeException("Incorrect parameters for sub");
			}
			
			// Get values from parameters
			String regex = regexTarget.getValue();
			String sub = subTarget != null ? subTarget.getValue() : "";
			String target = stringTarget.getValue();
			
			// Compile regex pattern and create a matcher
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(target);
			
			if (matcher.find()) {
				// Perform the substitution
				String result = target.substring(0, matcher.start()) + sub + target.substring(matcher.end());
				return result;
			}
			
			return target; // No substitution made
		};
		
		// This searches the string str1 for the first occurrences of the string str2, and returns the 
		// position in characters where that occurrence begins in the string str1. String indices in awk starts from 1.
		Function<HashMap<String, InterpreterDataType>, String> indexImplementation = (parameters) -> {
			InterpreterDataType str1 = parameters.get("0");
			InterpreterDataType str2 = parameters.get("1");
			
			if (str1 == null || str2 == null) {
				return ""; // Return empty string for null inputs.
			}
			
			// Get values from parameters
			String target = str1.getValue();
			String substring = str2.getValue();
			
			// Use indexOf to find the first occurrence of the substring in the target substring
			int index = target.indexOf(substring);
			
			// Adjust the index to match AWK (starting from 1)
			if (index >= 0) {
				index++;
			}
			return String.valueOf(index);
		};
		
		// Calculates the length of the string
		Function<HashMap<String, InterpreterDataType>, String> lengthImplementation = (parameter) -> {
			// Get the input string from the parameters
			InterpreterDataType stringTarget = parameter.get("0");
			String target = stringTarget.getValue();
			// Calculate the length of the input string and return it as a string
			return String.valueOf(target.length());
		};
		
		// This function splits the string str into fields by regular expression regex and the fields 
		// are loaded into the array arr. If regex is omitted, then FS is used.
		Function<HashMap<String, InterpreterDataType>, String> splitImplementation = (parameters) -> {
			// Get the input string, array, and field separator from the parameters
			InterpreterDataType str = parameters.get("0");
			InterpreterArrayDataType array = (InterpreterArrayDataType) parameters.get("1");
			InterpreterDataType fieldsep = parameters.get("2");
			
			if (str == null || array == null || fieldsep == null) {
				return ""; // Return empty string for null inputs.
			}
			
			// Extract the target string and field separator
			String target = str.getValue();
			String separator = fieldsep.getValue();
			
			// Split the target string into parts using the separator
			String[] parts = target.split(separator);
			
			// Populate the array with split parts
			for (int i = 0; i < parts.length; i++) {
				array.setArrayElements(String.valueOf(i + 1), new InterpreterDataType(parts[i]));
			}
			
			// Return the number of split parts as a string
			return String.valueOf(parts.length);
		};
		
		// Extract substring function from a string. Returns substring of string s at beginning position p 
		// up to a maximum length of n. If n is not supplied, the rest of the string from p is used.
		Function<HashMap<String, InterpreterDataType>, String> substrImplementation = (parameters) -> {
			// Get the input string, starting position, and optional length from the parameters
			InterpreterDataType s = parameters.get("0");
			InterpreterDataType p = parameters.get("1");
			InterpreterDataType n = parameters.get("2");
			
			//Extract the target string and convert the starting position to an integer
			String target = s.getValue();
			int start = Integer.parseInt(p.getValue()) - 1; // AWK string indices start from 1
			int length = (n != null) ? Integer.parseInt(n.getValue()) : target.length() - start;
			
			// Ensure that the starting position is within valid bounds
			if (start < 0) {
				start = 0;
			}
			if (start >= target.length()) {
				return ""; // Return an empty string if the starting position is out of bounds
			}
			// Adjust the length to prevent going beyond the string's end
			if (length > target.length() - start) {
				length = target.length() - start;
			}
			
			// Extract the substring and return it
			return target.substring(start, start + length);
		};
		
		// Translate all lowercase characters in string s to lowercase and returns the new string.
		Function<HashMap<String, InterpreterDataType>, String> tolowerImplementation = (parameter) -> {
			// Get the input string from the parameter
			InterpreterDataType strTarget = parameter.get("0");
			String target = strTarget.getValue();
			
			// Convert the input string to lowercase and return it
			String result = target.toLowerCase();
			return result;
		};
		
		// Translate all uppercase characters in string s to lowercase and returns the new string.
		Function<HashMap<String, InterpreterDataType>, String> toupperImplementation = (parameter) -> {
			// Get the input string from the parameter
			InterpreterDataType strTarget = parameter.get("0");
			String target = strTarget.getValue();
			
			// Convert the input string to uppercase and return it
			String result = target.toUpperCase();
			return result;
		};
		
		functions.put("print", new BIFDN(true, printImplementation));
		functions.put("printf", new BIFDN(true, printfImplementation));
		functions.put("sprintf", new BIFDN(true, sprintfImplementation));
		functions.put("getline", new BIFDN(false, getlineImplementation));
		functions.put("next", new BIFDN(false, nextImplementation));
		functions.put("gsub", new BIFDN(false, gsubImplementation));
		functions.put("match", new BIFDN(false, matchImplementation));
		functions.put("sub", new BIFDN(false, subImplementation));
		functions.put("index", new BIFDN(false, indexImplementation));
		functions.put("length", new BIFDN(false, lengthImplementation));
		functions.put("split", new BIFDN(false, splitImplementation));
		functions.put("substr", new BIFDN(false, substrImplementation));
		functions.put("tolower", new BIFDN(false, tolowerImplementation));
		functions.put("toupper", new BIFDN(false, toupperImplementation));
	}
	
	// Method to execute built-in functions
	public String executeFunction(String keyword, HashMap<String, InterpreterDataType> parameters) {
		// Check if the keyword is a built in function
		if (functions.containsKey(keyword)) {
			// Get the function definition from the function HashMap
			FunctionDefinitionNode functionDefinition = functions.get(keyword);
			
			// Check if it's a built in function
			if (functionDefinition instanceof BIFDN) {
				// Execute the built in function with the provided parameters
				BIFDN BuiltInFunction = (BIFDN) functionDefinition;
				return BuiltInFunction.execute(parameters);
			}
		}
		
		return "Unknown function: " + keyword;
	}
	
	// Evaluates various nodes with possible global/local variables and returns an IDT
	public InterpreterDataType GetIDT(Node node, HashMap<String, InterpreterDataType> localVars) throws Exception {
		
		// Evaluate AssignmentNodes, ensuring the target is either a variable reference or an operation w/ $
		if (node instanceof AssignmentNode) {
			AssignmentNode assignmentNode = (AssignmentNode) node;
			
			Node target = assignmentNode.getTarget();
			if (target instanceof VariableReferenceNode) {
				VariableReferenceNode variable = (VariableReferenceNode) target;
				InterpreterDataType expression = GetIDT(assignmentNode.getExpression(), localVars);
				if (globalVariables.containsKey(variable.getVariableName())) {
					globalVariables.get(variable.getVariableName()).setValue(expression.getValue());
				}
				else {
					localVars.put(variable.getVariableName(), expression);
				}
			}
			else if (target instanceof OperationNode && ((OperationNode) target).getType() == OperationNode.AWKOperation.DOLLAR) {
				String variableIndex = null; 
				InterpreterDataType result = localVars.get("$" + variableIndex);
				if (result == null) {
					throw new RuntimeException("Variable $" + variableIndex + " is not defined");
				}
				
				InterpreterDataType value = GetIDT(assignmentNode.getExpression(), localVars);
				localVars.put("$" + variableIndex, result);
				return result;
			}
			else {
				throw new RuntimeException("Invalid assignment target");
			}
		}
		
		// Returns and IDT with the value set to the ConstantNode's value
		if (node instanceof ConstantNode) {
			ConstantNode constantNode = (ConstantNode) node;
			return new InterpreterDataType(constantNode.getValue());
			
		}
		
		// Evaluates a function call
		if (node instanceof FunctionCallNode) {
			FunctionCallNode functionCallNode = (FunctionCallNode) node;
			String result = RunFunctionCall(functionCallNode, localVars);
			return new InterpreterDataType(result);
		}
		
		// Throws an error because a pattern cannot be passed to a function or assignment
		if (node instanceof PatternNode) {
			throw new RuntimeException("Pattern cannot be passed to a function or assignment");
		}
		
		// Evaluates the boolean condition and returns the true or false case
		if (node instanceof TernaryNode) {
			TernaryNode ternaryNode = (TernaryNode) node;
			InterpreterDataType conditionResult = GetIDT(ternaryNode.getCondition(), localVars);
			
			if (conditionResult.getValue().equals("1")) {
				return GetIDT(ternaryNode.getTrueCase(), localVars);
			}
			else {
				return GetIDT(ternaryNode.getFalseCase(), localVars);
			}
		}
		
		// Evaluates variables and array references
		if (node instanceof VariableReferenceNode) {
			VariableReferenceNode vr = (VariableReferenceNode) node;
			String variableName = vr.getVariableName();
			InterpreterDataType idt;
			if ((idt = globalVariables.get(vr.getVariableName())) != null) {
				if(!(idt instanceof InterpreterArrayDataType)) {
					return idt;
				}
				if (vr.getIndexExpression() == null) {
					return idt;
				}
				InterpreterArrayDataType iadt = (InterpreterArrayDataType) idt;
				return iadt.getArrayElement(GetIDT(vr.getIndexExpression().get(), localVars).toString());
			}
			else if ((idt = localVars.get(vr.getVariableName())) != null) {
				if(!(idt instanceof InterpreterArrayDataType)) {
					return idt;
				}
				if (vr.getIndexExpression() == null) {
					return idt;
				}
				InterpreterArrayDataType iadt = (InterpreterArrayDataType) idt;
				return iadt.getArrayElement(GetIDT(vr.getIndexExpression().get(), localVars).toString());
			}
			return new InterpreterDataType("");
		}
		
		// Handles various operations in AWK
		if (node instanceof OperationNode) {
			
			OperationNode operationNode = (OperationNode) node;
			Node left = operationNode.getLeft();
			InterpreterDataType leftIDT = GetIDT(left, localVars);
			OperationNode.AWKOperation operation = operationNode.getOperation();
			Optional<Node> right = operationNode.getRight();
			InterpreterDataType rightIDT = right.isPresent() ? GetIDT(right.get(), localVars) : null;
			
			
			switch (operation) {
			// Math operations: Add, Subtract, Multiply, Divide, Modulus (Expression & Term)
			case ADD:
			case SUBTRACT:
			case MULTIPLY:
			case DIVIDE:
			case EXPONENT:
			case MODULO:
				float leftValue = 0;
				float rightValue = 0;
				// Convert to float, perform operation, convert to string
				try { leftValue = Float.parseFloat(leftIDT.getValue());}
				catch(Exception e) { leftValue = 0;}
				try{ rightValue = Float.parseFloat(rightIDT.getValue());}
				catch(Exception e) { rightValue = 0;}
				float result = 0;

				switch (operation) {
				case ADD:
					result = leftValue + rightValue;
					break;
				case SUBTRACT:
					result = leftValue - rightValue;
					break;
				case MULTIPLY:
					result = leftValue * rightValue;
					break;
				case DIVIDE:
					result = leftValue / rightValue;
					break;
				case EXPONENT:
					result = (float) Math.pow(leftValue, rightValue);
					break;
				case MODULO:
					result = leftValue % rightValue;
					break;
				}
				return new InterpreterDataType(String.valueOf(result));
				
			// Compares: < <= != == > >=
			case LT:
			case LE:
			case NE:
			case EQ:
			case GT:
			case GE:
				// Compare as floats if both sides convert to float, otherwise compare as strings
				boolean comparisonResult = false;
				
				try {
					
					float leftFloat = Float.parseFloat(leftIDT.getValue());
					float rightFloat = Float.parseFloat(rightIDT.getValue());
					
					switch (operation) {
					case LT:
						comparisonResult = leftFloat < rightFloat;
						break;
					case LE:
						comparisonResult = leftFloat <= rightFloat;
						break;
					case NE:
						comparisonResult = leftFloat != rightFloat;
						break;
					case EQ:
						comparisonResult = leftFloat == rightFloat;
						break;
					case GT:
						comparisonResult = leftFloat > rightFloat;
						break;
					case GE:
						comparisonResult = leftFloat >= rightFloat;
						break;
					}
					
				}
				catch (NumberFormatException e) {
					// Conversion to float failed, compare as strings
					switch (operation) {
					case LT:
						comparisonResult = leftIDT.getValue().compareTo(rightIDT.getValue()) < 0;
						break;
					case LE:
						comparisonResult = leftIDT.getValue().compareTo(rightIDT.getValue()) <= 0;
						break;
					case NE:
						comparisonResult = !leftIDT.getValue().equals(rightIDT.getValue());
						break;
					case EQ:
						comparisonResult = leftIDT.getValue().equals(rightIDT.getValue());
						break;
					case GT:
						comparisonResult = leftIDT.getValue().compareTo(rightIDT.getValue()) > 0;
						break;
					case GE:
						comparisonResult = leftIDT.getValue().compareTo(rightIDT.getValue()) >= 0;
						break;
					}
				}
				
				return new InterpreterDataType(comparisonResult ? "1" : "0");
			
			// Boolean operations: and, or, not
			// Evaluate and convert to boolean using AWK's rules
			case AND:
				float andLeft = Float.parseFloat(leftIDT.getValue());
				float andRight = Float.parseFloat(rightIDT.getValue());
				if (Float.valueOf(andLeft) == andLeft && Float.valueOf(andRight) == andRight) {
					return new InterpreterDataType("1");
				}
				else {
					return new InterpreterDataType("0");
				}
				
				
				
			case OR:
				float orLeft = Float.parseFloat(leftIDT.getValue());
				float orRight = Float.parseFloat(rightIDT.getValue());
				if (Float.valueOf(orLeft) == orLeft || Float.valueOf(orRight) == orRight) {
					return new InterpreterDataType("1");
				}
				else {
					return new InterpreterDataType("0");
				}
					
			case NOT:
				float notLeft = Float.parseFloat(leftIDT.getValue());
				float notRight = Float.parseFloat(rightIDT.getValue());
				if (Float.valueOf(notLeft) != notLeft || Float.valueOf(notRight) != notRight) {
					return new InterpreterDataType("1");
				}
				else {
					return new InterpreterDataType("0");
				}
			
			// Match and NotMatch 
			case MATCH:
				if (right.isPresent() && right.get() instanceof PatternNode) {
					PatternNode patternNode = (PatternNode) right.get();
					String regexPattern = patternNode.getValue();
					
					if (leftIDT.getValue().matches(regexPattern)) {
						return new InterpreterDataType("1");
					}
					else {
						return new InterpreterDataType("0");
					}
				}
				else {
					throw new RuntimeException("Invalid use of match operator. The right side must be a pattern");
				}
				
			case NOTMATCH:
				if (right.isPresent() && right.get() instanceof PatternNode) {
					PatternNode patternNode = (PatternNode) right.get();
					String regexPattern = patternNode.getValue();
					
					if (leftIDT.getValue().matches(regexPattern)) {
						return new InterpreterDataType("0");
					}
					else {
						return new InterpreterDataType("1");
					}
				}
				else {
					throw new RuntimeException("Invalid use of match operator. The right side must be a pattern");
				}
				
			case DOLLAR:
				String dollar = GetIDT(left, localVars).getValue();
				return new InterpreterDataType("$" + dollar);
			
			// Handler pre and unary ++a, --a, +a, -a
			case PREINC:
			case PREDEC:
			case UNARYPOS:
			case UNARYNEG:
				float rightValue1 = Float.parseFloat(rightIDT.getValue());
				float preResult = 0;
				
				switch(operation) {
				case PREINC:
					preResult = rightValue1 + 1;
					break;
					
				case PREDEC:
					preResult = rightValue1 - 1;
					break;
					
				case UNARYPOS:
					preResult = +rightValue1;
					break;
					
				case UNARYNEG:
					preResult = -rightValue1;
					break;
				}
				
				return new InterpreterDataType(String.valueOf(preResult));
			
			// Handle post a++ a--
			case POSTINC:
			case POSTDEC:
				float leftValue1 = Float.parseFloat(leftIDT.getValue());
				//float rightValue1 = Float.parseFloat(rightIDT.getValue());
				float postResult = 0;
				
				switch (operation) {	
				case POSTINC:
					postResult = leftValue1 + 1;
					break;

				case POSTDEC:
					postResult = leftValue1 - 1;
					break;
				}
				
				return new InterpreterDataType(String.valueOf(postResult));
				
				
			case CONCATENATION: 
				// Simple string concatenation
				return new InterpreterDataType(leftIDT.getValue() + rightIDT.getValue());
			
			// Array membership
			case IN:
				if (operation == OperationNode.AWKOperation.IN) {
					if (right.isPresent() && right.get() instanceof VariableReferenceNode) {
						VariableReferenceNode rightVariableNode = (VariableReferenceNode) right.get();
						
						if (rightVariableNode.isArrayReference()) {
							String arrayName = rightVariableNode.getVariableName();
							InterpreterDataType arrayIDT = localVars.get(arrayName);
							if (arrayIDT == null || !(arrayIDT instanceof InterpreterArrayDataType)) {
								throw new RuntimeException("Right-hand side of IN is not an array");
							}
							
							String leftSideValue = leftIDT.getValue();
							InterpreterArrayDataType array = (InterpreterArrayDataType) arrayIDT;
							if (array.getArrayElement(leftSideValue) != null) {
								return new InterpreterDataType("1");
							}
							else {
								return new InterpreterDataType("0");
							}
						}
						else {
							throw new RuntimeException("Right-hand side of IN is not an array reference");
						}
					}
					else {
						throw new RuntimeException("Right-hand side of IN is not a variable reference");
					}
				}
				
			default:
				throw new UnsupportedOperationException("Operation not supported: " + operation);
			}
			
		}
		
		return null; // Default return value
	}
	
	public String RunFunctionCall(FunctionCallNode functionCallNode, HashMap<String, InterpreterDataType> localVars) throws Exception {
		// Find the function definition
		String functionName = functionCallNode.getFunctionName();
		if (functions.get(functionName) == null) {
			throw new RuntimeException("Function '" + functionName + "' not found");
		}
		// HashMap for parameters
		HashMap<String, InterpreterDataType> paramMap = new HashMap<>();
		
		if (functions.get(functionName) instanceof BIFDN) {
			// Check if function is a BIFDN
			BIFDN builtIn = (BIFDN) functions.get(functionName);
			for (int i = 0; i < functionCallNode.getParameterCount(); i++) {
				// Add parameters
				paramMap.put("" + i, GetIDT(functionCallNode.getParameters().get(i), localVars));
			}
			String functions = builtIn.execute(paramMap); // Execute the function
			if (functions == null) {
				throw new RuntimeException("Incorrect parameters");
			}
			return functions;
		}
		FunctionDefinitionNode func = functions.get(functionName);
		// Check if the number of parameters matches
		if (func.getParameterCount() != functionCallNode.getParameterCount()) {
			throw new RuntimeException("Parameter count mismatch for function " + functionName);
		}
		for (int i = 0; i < func.getParameters().size(); i++) {
			paramMap.put(func.getParameters().get(i), GetIDT(functionCallNode.getParameters().get(i), localVars));
			//globalVariables.put(func.getParameters().get(i), GetIDT(functionCallNode.getParameters().get(i), localVars));
		}
		ReturnType statements = InterpretListOfStatements(func.getStatements(), paramMap);
		if (statements == null) {
			return null;
		}
		return statements.getValue();
	}
	
	// Process the various AWK statements
	public ReturnType ProcessStatement(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception {
		if (stmt instanceof AssignmentNode) {
			// Handled in GetIDT, so we simply call it here
			AssignmentNode assignmentNode = (AssignmentNode) stmt;
			GetIDT(assignmentNode, locals);
		}
		
		if (stmt instanceof BreakNode) {
			// Returns with a return type of break
			return new ReturnType(ReturnType.returnTypes.BREAK);
		}
		
		if (stmt instanceof ContinueNode) {
			// Returns with a return type of continue
			return new ReturnType(ReturnType.returnTypes.CONTINUE);
		}
		
		if (stmt instanceof DeleteNode) {
			DeleteNode delete = (DeleteNode) stmt;
			String arrayName = delete.getTarget().toString();
			InterpreterDataType arrayData = locals.get(arrayName);
			
			if (arrayData == null) {
				// Array not found in locals, check globals
				arrayData = globalVariables.get(arrayData);
			}
			
			if (arrayData != null && arrayData instanceof InterpreterArrayDataType) {
				InterpreterArrayDataType array = (InterpreterArrayDataType) arrayData;
				
				LinkedList<ConstantNode> deleteIndices = delete.getIndices();
				if (deleteIndices.isEmpty()) {
					// Delete the entire array
					locals.remove(array);
					globalVariables.remove(array);
				}
				else {
					// Delete the specific indices from the array
					for (ConstantNode index : deleteIndices) {
						array.getElements().remove(index);
					}
				}
			}
			
			return new ReturnType(ReturnType.returnTypes.NONE);
		}
		
		if (stmt instanceof DoWhileNode) {
			DoWhileNode doWhileNode = (DoWhileNode) stmt;
			ReturnType result;
			
			do {
				// Loop over the DoWhile's statements
				result = InterpretListOfStatements(doWhileNode.getStatements().getStatements(), locals);
				if (result.getReturnType() == ReturnType.returnTypes.BREAK) {
					// Break out of the loop if 'break' occurs
					break;
				}
				else {
					return result;
				}
			} while (GetIDT(doWhileNode.getCondition(), locals).getValue().equals("1")); // Evaluate the condition
		}
		
		if (stmt instanceof ForNode) {
			ForNode forNode = (ForNode) stmt;
			
			if (forNode.getInitialization() != null) {
				// Call GetIDT on the initialization of the for loop if it exists
				GetIDT(forNode.getInitialization().get(), locals);
				
			}
			// Use for loop's condition in the while loop to evaluate the list of statements
			while (GetIDT(forNode.getCondition().get(), locals).getValue().equals("1")) {
				ReturnType result = InterpretListOfStatements(forNode.getForBlock().getStatements(), locals);
				
				if (result.getReturnType() == ReturnType.returnTypes.BREAK) {
					// Break out of the loop if 'break' occurs
					break;
				}
				else if (result.getReturnType() == ReturnType.returnTypes.RETURN) {
					return result;
				}
				
				if (forNode.getUpdate() != null) {
					// Call GetIDT on the increment of the for loop if it exists
					GetIDT(forNode.getUpdate().get(), locals);
				}	
			}
			
			return new ReturnType(ReturnType.returnTypes.NONE);
		}
		
		if (stmt instanceof ForEachNode) {
			ForEachNode forEach = (ForEachNode) stmt;
			// Identify the array
			String arrayName = forEach.getIterable().toString();
			InterpreterDataType array = locals.get(arrayName);
			
			if (array == null || !(array instanceof InterpreterArrayDataType)) {
				throw new RuntimeException("For each loop requires an array");
			}
			
			// Array must be an IADT
			InterpreterArrayDataType arrayData = (InterpreterArrayDataType) array;
			
			// Loop over every key in the array's HashMap
			for (String key : arrayData.getElements().keySet()) {
				// Set the variable to the key, then interpret the list of statements in the ForEach loop
				locals.put(forEach.getIterable().toString(), arrayData.getArrayElement(key));
				ReturnType result =  InterpretListOfStatements(forEach.getForEachBlock().getStatements(), locals);
				
				if (result.getReturnType() == ReturnType.returnTypes.BREAK) {
					// Break out of the loop if 'break' occurs
					break;
				}
				else if (result.getReturnType() == ReturnType.returnTypes.RETURN) {
					return result; // Return if the result is not None
				}
			}
			
			return new ReturnType(ReturnType.returnTypes.NONE);
		}
		
//		if (stmt instanceof FunctionCallNode) {
//			// Handled in GetIDT, so we simply call it here
//			FunctionCallNode functionCallNode = (FunctionCallNode) stmt;
//			GetIDT(functionCallNode, locals);
//		}
		
		if (stmt instanceof IfNode) {
			IfNode ifNode = (IfNode) stmt;
			
			// Walk the linked list of IfNodes
			while (ifNode != null) {
				// Check if the condition is empty or evaluates to true
				if (ifNode.getCondition().isEmpty() || GetIDT(ifNode.getCondition().get(), locals).getValue().equals("1")) {
					// Condition is true, execute the statements
					ReturnType result = InterpretListOfStatements(ifNode.getIfBlock().getStatements(), locals);
					
					if (result.getReturnType() != ReturnType.returnTypes.NONE) {
						return result; // Return if the result is not None
					}
				}
				// Move to the next IfNode in the linked list
				ifNode = ifNode.getNextIf().get();
			}
			
			return new ReturnType(ReturnType.returnTypes.NONE);
		}
		
		if (stmt instanceof ReturnNode) {
			ReturnNode returnNode = (ReturnNode) stmt;
			if (returnNode.hasValue()) {
				// If there is a value, evaluate it
				InterpreterDataType result = GetIDT(returnNode.getReturnValue(), locals);
				return new ReturnType(ReturnType.returnTypes.RETURN, result.getValue());
			}
			else {
				// Evaluate without return value since it doesn't exist
				return new ReturnType(ReturnType.returnTypes.RETURN);
			}
		}
		
		if (stmt instanceof WhileNode) {
			// Similar to DoWhile, except a While loop is used instead of a DoWhile loop
			WhileNode whileNode = (WhileNode) stmt;
			ReturnType result;
			
			while (GetIDT(whileNode.getCondition(), locals).getValue().equals("1")) { // Evaluate the condition
				result = InterpretListOfStatements(whileNode.getStatements().getStatements(), locals);
				
				if (result.getReturnType() == ReturnType.returnTypes.BREAK) {
					// Break out of the loop if 'break' occurs
					break;
				}
				else if (result.getReturnType() == ReturnType.returnTypes.RETURN){
					return result;
				}
			}
		}
		
		GetIDT(stmt, locals);
		return new ReturnType(ReturnType.returnTypes.NONE);
		
	}
	
	// Loops over statements, calling ProcessStatement for each one
	public ReturnType InterpretListOfStatements(LinkedList<StatementNode> statements, HashMap<String, InterpreterDataType> locals) throws Exception {
		ReturnType returnType = new ReturnType(ReturnType.returnTypes.NONE);
		for (StatementNode statement : statements) {
			returnType = ProcessStatement(locals, statement);
			
			if (returnType.getReturnType() != ReturnType.returnTypes.NONE) {
				break;
			}
		}
		
		return returnType;
	}
	
	public void InterpretProgram(ProgramNode Program) throws Exception {
		// Run BEGIN blocks
		for (BlockNode beginBlock : Program.getBeginBlocks()) {
			InterpretBlock(beginBlock);
		} 
		
		// Process records
		for (BlockNode otherBlocks : Program.getOtherBlocks()) {
			// Reset NR and FNR to 0
			lineHandler.setNR(0);
			lineHandler.setFNR(0);
			while (lineHandler.SplitAndAssign()) {
				// Run other blocks
				InterpretBlock(otherBlocks);
			}
		}
		
		// Run END blocks
		for (BlockNode endBlock : Program.getEndBlocks()) {
			InterpretBlock(endBlock);
		} 
	}
	
	public void InterpretBlock(BlockNode Block) throws Exception {
		// Check condition
		if (!Block.getCondition().isPresent() || GetIDT(Block.getCondition().get(), globalVariables).getValue().equals("1")) {
			// Interpret the list of statements in each block
			InterpretListOfStatements(Block.getStatements(), globalVariables);
			
		}	
	}
}
