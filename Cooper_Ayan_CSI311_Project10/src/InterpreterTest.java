import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.function.Function;

public class InterpreterTest {
	
	@Test
	public void testSplitAndAssign() throws IOException {
		List<String> inputLines = new LinkedList<>();
		inputLines.add("Line 1");
		inputLines.add("Line 2");
		inputLines.add("Line 3");
		
		Interpreter interpreter = new Interpreter(new ProgramNode(), null);
		Interpreter.LineHandler lineHandler = interpreter.getLineHandler();
		lineHandler.setLines(inputLines);
		
		assertEquals(true, lineHandler.SplitAndAssign());
		assertEquals(1, lineHandler.getNR());
		assertEquals(1, lineHandler.getFNR());
		
		assertEquals(true, lineHandler.SplitAndAssign());
		assertEquals(2, lineHandler.getNR());
		assertEquals(2, lineHandler.getFNR());
		
		assertEquals(true, lineHandler.SplitAndAssign());
		assertEquals(3, lineHandler.getNR());
		assertEquals(3, lineHandler.getFNR());
	}
	
	@Test
	public void testPrint() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));
		
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		parameters.put("0", new InterpreterDataType("Hello, World!"));
		parameters.put("1", new InterpreterDataType("I'm being printed!"));
		String result = interpreter.executeFunction("print", parameters);
		
		System.setOut(originalOut);
		String printedContent = outputStream.toString();
		String expectedOutput = "Hello, World! I'm being printed!";
		assertEquals(expectedOutput, printedContent.trim());
	}
	
	@Test
	public void testPrintf() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));
		
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		parameters.put("0", new InterpreterDataType("Hello 42"));
		
		String result = interpreter.executeFunction("printf", parameters);
		
		System.setOut(originalOut);
		String printedContent = outputStream.toString();
		String expectedOutput = "Hello 42";
		assertEquals(expectedOutput, printedContent.trim());
	}
	
	@Test
	public void testSrintf() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		parameters.put("0", new InterpreterDataType("Result: Hello, Value: 42, Pi: 3.14"));
		String result = interpreter.executeFunction("sprintf", parameters);
		String expectedOutput = "Result: Hello, Value: 42, Pi: 3.14";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testGetline() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		String result = interpreter.executeFunction("getline", new HashMap<>());
		String expectedOutput = "1";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testNext() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		String result = interpreter.executeFunction("next", new HashMap<>());
		String expectedOutput = "1";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testGsub() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		parameters.put("0", new InterpreterDataType("World"));
		parameters.put("1", new InterpreterDataType("Jerry"));
		parameters.put("2", new InterpreterDataType("Hello, World"));
		String result = interpreter.executeFunction("gsub", parameters);
		String expectedOutput = "Hello, Jerry";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testMatch() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		parameters.put("0", new InterpreterDataType("One Two Three"));
		parameters.put("1", new InterpreterDataType("Two"));
		String result = interpreter.executeFunction("match", parameters);
		String expectedOutput = "5";
		System.out.println(result);
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testSub() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		parameters.put("0", new InterpreterDataType("World"));
		parameters.put("1", new InterpreterDataType("Thomas"));
		parameters.put("2", new InterpreterDataType("Hello, World"));
		String result = interpreter.executeFunction("sub", parameters);
		String expectedOutput = "Hello, Thomas";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testIndex() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		parameters.put("0", new InterpreterDataType("Graphic"));
		parameters.put("1", new InterpreterDataType("ph"));
		String result = interpreter.executeFunction("index", parameters);
		String expectedOutput = "4";
		System.out.println(result);
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testLength() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameter = new HashMap<>();
		parameter.put("0", new InterpreterDataType("Hello, World!"));
		String result = interpreter.executeFunction("length", parameter);
		String expectedOutput = "13";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testSplit() throws IOException {
		Path filePath = Path.of("test.txt");
		HashMap<String, InterpreterDataType> parameters = new HashMap<>();
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		InterpreterArrayDataType array = new InterpreterArrayDataType(parameters);
		array.setArrayElements("1", new InterpreterDataType("One"));
		array.setArrayElements("2", new InterpreterDataType("Two"));
		array.setArrayElements("3", new InterpreterDataType("Three"));
		array.setArrayElements("4", new InterpreterDataType("Four"));
		array.setArrayElements("5", new InterpreterDataType("Five"));
		parameters.put("0", new InterpreterDataType("One,Two,Three,Four,Five"));
		parameters.put("1", array);
		parameters.put("2", new InterpreterDataType(","));
		String result = interpreter.executeFunction("split", parameters);
		String expectedOutput = "5";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testSubstr() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameter = new HashMap<>();
		parameter.put("0", new InterpreterDataType("Graphic Era University"));
		parameter.put("1", new InterpreterDataType("9"));
		parameter.put("2", new InterpreterDataType("8"));
		String result = interpreter.executeFunction("substr", parameter);
		String expectedOutput = "Era Univ";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testTolower() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameter = new HashMap<>();
		parameter.put("0", new InterpreterDataType("UPPER TO LOWER"));
		String result = interpreter.executeFunction("tolower", parameter);
		String expectedOutput = "upper to lower";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testToupper() throws IOException {
		Path filePath = Path.of("test.txt");
		Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
		HashMap<String, InterpreterDataType> parameter = new HashMap<>();
		parameter.put("0", new InterpreterDataType("lower to upper"));
		String result = interpreter.executeFunction("toupper", parameter);
		String expectedOutput = "LOWER TO UPPER";
		assertEquals(expectedOutput, result);
	}
	
	@Test
	public void testAssignmentNode() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var target = new VariableReferenceNode("a");
		var expression = new ConstantNode("b");
		
		var assignmentNode = new AssignmentNode(target, expression);
		var IDT = interpreter.GetIDT(assignmentNode, map);
		Assert.assertEquals("b", IDT.getValue());
	}
	
	@Test
	public void testConstantNode() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var constantNode = new ConstantNode("this is a constant node");
		var IDT = interpreter.GetIDT(constantNode, null);
		Assert.assertEquals("this is a constant node", IDT.getValue());
	}
	
	@Test
	public void testFunctionCallNode() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var condition1 = new ConstantNode("i = 0;");
		var condition2 = new ConstantNode("i < 10;");
		var condition3 = new ConstantNode("i++");
		LinkedList<Node> parameters = new LinkedList<>();
		parameters.add(condition1);
		parameters.add(condition2);
		parameters.add(condition3);
		var functionCallNode = new FunctionCallNode("for", parameters);
		var IDT = interpreter.GetIDT(functionCallNode, null);
		Assert.assertEquals("", IDT.getValue());
	}
	
	@Test
	public void testPatternNode() throws IOException {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var patternNode = new PatternNode("this is a pattern");
		Assert.assertThrows(RuntimeException.class, () -> interpreter.GetIDT(patternNode, null));
	}
	
	@Test
	public void testTernaryNode() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var condition = new OperationNode(new ConstantNode("10"), OperationNode.AWKOperation.LT, 
				Optional.of(new ConstantNode("20")));
		var trueCase = new ConstantNode("10");
		var falseCase = new ConstantNode("20");
		var ternaryNode = new TernaryNode(condition, trueCase, falseCase);
		var IDT = interpreter.GetIDT(ternaryNode, null);
		Assert.assertEquals("10", IDT.getValue());
	}
	
	@Test
	public void testVariableReferenceNode() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		InterpreterArrayDataType array = new InterpreterArrayDataType(map);
		array.setArrayElements("0", new InterpreterDataType("2"));
		array.setArrayElements("1", new InterpreterDataType("4"));
		array.setArrayElements("2", new InterpreterDataType("6"));
		array.setArrayElements("3", new InterpreterDataType("8"));
		array.setArrayElements("4", new InterpreterDataType("10"));
		map.put("a", array);
		var variableReference = new VariableReferenceNode("a", Optional.of(new ConstantNode("1")));
		var IDT = interpreter.GetIDT(variableReference, map);
		Assert.assertEquals("4", IDT.getValue());
	}
	
	@Test
	public void testMathOperations() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var constantNode1 = new ConstantNode("8");
		var constantNode2 = new ConstantNode("4");
		
		var operationNode1 = new OperationNode(constantNode1, OperationNode.AWKOperation.ADD, 
				Optional.of(constantNode2));
		var addIDT = interpreter.GetIDT(operationNode1, null);
		Assert.assertEquals("12.0", addIDT.getValue());
		
		var operationNode2 = new OperationNode(constantNode1, OperationNode.AWKOperation.SUBTRACT, 
				Optional.of(constantNode2));
		var subtractIDT = interpreter.GetIDT(operationNode2, null);
		Assert.assertEquals("4.0", subtractIDT.getValue());
		
		var operationNode3 = new OperationNode(constantNode1, OperationNode.AWKOperation.MULTIPLY, 
				Optional.of(constantNode2));
		var multiplyIDT = interpreter.GetIDT(operationNode3, null);
		Assert.assertEquals("32.0", multiplyIDT.getValue());
		
		var operationNode4 = new OperationNode(constantNode1, OperationNode.AWKOperation.DIVIDE, 
				Optional.of(constantNode2));
		var divideIDT = interpreter.GetIDT(operationNode4, null);
		Assert.assertEquals("2.0", divideIDT.getValue());
		
		var operationNode5 = new OperationNode(constantNode1, OperationNode.AWKOperation.EXPONENT, 
				Optional.of(constantNode2));
		var exponentIDT = interpreter.GetIDT(operationNode5, null);
		Assert.assertEquals("4096.0", exponentIDT.getValue());
		
		var operationNode6 = new OperationNode(constantNode1, OperationNode.AWKOperation.MODULO, 
				Optional.of(constantNode2));
		var moduloIDT = interpreter.GetIDT(operationNode6, null);
		Assert.assertEquals("0.0", moduloIDT.getValue());
	}
	
	@Test
	public void testComparisons() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var constantNode1 = new ConstantNode("8");
		var constantNode2 = new ConstantNode("4");
		
		var operationNode1 = new OperationNode(constantNode1, OperationNode.AWKOperation.LT, 
				Optional.of(constantNode2));
		var LTIDT = interpreter.GetIDT(operationNode1, null);
		Assert.assertEquals("0", LTIDT.getValue());
		
		var operationNode2 = new OperationNode(constantNode1, OperationNode.AWKOperation.LE, 
				Optional.of(constantNode2));
		var LEIDT = interpreter.GetIDT(operationNode2, null);
		Assert.assertEquals("0", LEIDT.getValue());
		
		var operationNode3 = new OperationNode(constantNode1, OperationNode.AWKOperation.NE, 
				Optional.of(constantNode2));
		var NEIDT = interpreter.GetIDT(operationNode3, null);
		Assert.assertEquals("1", NEIDT.getValue());
		
		var operationNode4 = new OperationNode(constantNode1, OperationNode.AWKOperation.EQ, 
				Optional.of(constantNode2));
		var EQIDT = interpreter.GetIDT(operationNode4, null);
		Assert.assertEquals("0", EQIDT.getValue());
		
		var operationNode5 = new OperationNode(constantNode1, OperationNode.AWKOperation.GT, 
				Optional.of(constantNode2));
		var GTIDT = interpreter.GetIDT(operationNode5, null);
		Assert.assertEquals("1", GTIDT.getValue());
		
		var operationNode6 = new OperationNode(constantNode1, OperationNode.AWKOperation.GE, 
				Optional.of(constantNode2));
		var GEIDT = interpreter.GetIDT(operationNode6, null);
		Assert.assertEquals("1", GEIDT.getValue());
	}
	
	@Test
	public void testBooleanOps() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var constantNode = new ConstantNode("5");
		var condition1 = new OperationNode(constantNode, OperationNode.AWKOperation.GE, 
				Optional.of(new ConstantNode("0")));
		
		var condition2 = new OperationNode(constantNode, OperationNode.AWKOperation.LE, 
				Optional.of(new ConstantNode("7")));
		
		var operationNode1 = new OperationNode(condition1, OperationNode.AWKOperation.AND, 
				Optional.of(condition2));
		var andIDT = interpreter.GetIDT(operationNode1, null);
		Assert.assertEquals("1", andIDT.getValue());
		
		var operationNode2 = new OperationNode(condition1, OperationNode.AWKOperation.OR, 
				Optional.of(condition2));
		var orIDT = interpreter.GetIDT(operationNode2, null);
		Assert.assertEquals("1", orIDT.getValue());
		
		var operationNode3 = new OperationNode(condition1, OperationNode.AWKOperation.NOT, 
				Optional.of(condition2));
		var notIDT = interpreter.GetIDT(operationNode3, null);
		Assert.assertEquals("0", notIDT.getValue());
	}
	
	@Test
	public void testMatchNotMatch() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		
		var operationNode1 = new OperationNode(new ConstantNode("Hello world"), OperationNode.AWKOperation.MATCH, 
				Optional.of(new PatternNode("Hello")));
		var matchIDT = interpreter.GetIDT(operationNode1, map);
		Assert.assertEquals("1", matchIDT.getValue());
		
		var operationNode2 = new OperationNode(new ConstantNode("Hello world"), OperationNode.AWKOperation.MATCH, 
				Optional.of(new PatternNode("Hello")));
		var notmatchIDT = interpreter.GetIDT(operationNode2, map);
		Assert.assertEquals("0", notmatchIDT.getValue());
	}
	
	@Test
	public void testDollar() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var operationNode = new OperationNode(new ConstantNode("variable"), OperationNode.AWKOperation.DOLLAR);
		var IDT = interpreter.GetIDT(operationNode, null);
		Assert.assertEquals("$variable", IDT.getValue());
	}
	
	@Test
	public void testPrePostUnary() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		
		var operationNode1 = new OperationNode(OperationNode.AWKOperation.PREINC, Optional.of(new ConstantNode("10")));
		var preincIDT = interpreter.GetIDT(operationNode1, null);
		Assert.assertEquals("11.0", preincIDT.getValue());
		
		var operationNode2 = new OperationNode(new ConstantNode("10"), OperationNode.AWKOperation.POSTINC);
		var postincIDT = interpreter.GetIDT(operationNode2, null);
		Assert.assertEquals("11.0", postincIDT.getValue());
		
		var operationNode3 = new OperationNode(OperationNode.AWKOperation.PREDEC, Optional.of(new ConstantNode("10")));
		var predecIDT = interpreter.GetIDT(operationNode3, null);
		Assert.assertEquals("9.0", predecIDT.getValue());
		
		var operationNode4 = new OperationNode(new ConstantNode("10"), OperationNode.AWKOperation.POSTDEC);
		var postdecIDT = interpreter.GetIDT(operationNode4, null);
		Assert.assertEquals("9.0", postdecIDT.getValue());
		
		var operationNode5 = new OperationNode(OperationNode.AWKOperation.UNARYPOS, Optional.of(new ConstantNode("10")));
		var uposIDT = interpreter.GetIDT(operationNode5, null);
		Assert.assertEquals("10.0", uposIDT.getValue());
		
		var operationNode6 = new OperationNode(OperationNode.AWKOperation.UNARYNEG, Optional.of(new ConstantNode("10")));
		var unegIDT = interpreter.GetIDT(operationNode6, null);
		Assert.assertEquals("-10.0", unegIDT.getValue());
	}
	
	@Test
	public void testConcatenation() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		var operationNode = new OperationNode(new ConstantNode("hel"), OperationNode.AWKOperation.CONCATENATION, 
				Optional.of(new ConstantNode("lo")));
		var concatIDT = interpreter.GetIDT(operationNode, null);
		Assert.assertEquals("hello", concatIDT.getValue());
	}
	
	@Test
	public void testIN() throws Exception {
		var interpreter = new Interpreter(new ProgramNode(), null);
		var map = new HashMap<String, InterpreterDataType>();
		InterpreterArrayDataType array = new InterpreterArrayDataType(map);
		array.setArrayElements("0", new InterpreterDataType("2"));
		array.setArrayElements("1", new InterpreterDataType("4"));
		array.setArrayElements("2", new InterpreterDataType("6"));
		map.put("arr", array);
		var operationNode = new OperationNode(new ConstantNode("1"), OperationNode.AWKOperation.IN, 
				Optional.of(new VariableReferenceNode("arr", Optional.of(new ConstantNode("1")))));
		var IDT = interpreter.GetIDT(operationNode, map);
		Assert.assertEquals("1", IDT.getValue());
	}

}
