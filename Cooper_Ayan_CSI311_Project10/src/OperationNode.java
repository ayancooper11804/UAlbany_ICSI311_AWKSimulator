import java.util.HashMap;
import java.util.Optional;

public class OperationNode extends Node{
	
	// AWK Operations
	public enum AWKOperation {
		EQ, NE, LT, LE, GT, GE, AND, OR, NOT, MATCH, NOTMATCH, DOLLAR,
        PREINC, POSTINC, PREDEC, POSTDEC, UNARYPOS, UNARYNEG, IN,
        EXPONENT, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, CONCATENATION
	}
	
	private AWKOperation type;
	private Node left; // Left node of the operation
	private Optional<Node> right; // Optional right node of the operation
	private AWKOperation operation; // Type of AWK operation
	
	
	// Various constructors for whether left and right nodes exist
	
	public OperationNode(Node left, AWKOperation operation, Optional<Node> right) {
		this.left = left;
		this.operation = operation;
		this.right = right;
		initializeAWKOperations();
	}
	
	public OperationNode(Optional<Node> right, AWKOperation operation, Node left) {
		this.left = left;
		this.operation = operation;
		this.right = right;
		initializeAWKOperations();
	}
	
	public OperationNode(Node left, AWKOperation operation) {
		this.left = left;
		this.operation = operation;
		right = Optional.empty();
		initializeAWKOperations();
	}
	
	public OperationNode(AWKOperation operation, Optional<Node> right) {
		this.operation = operation;
		this.right = right;
		initializeAWKOperations();
	}
	
	public AWKOperation getType() {
		return type;
	}
	
	// Accessor for left node
	public Node getLeft() {
		return left;
	}
	
	// Accessor for right node
	public Optional<Node> getRight() {
		return right;
	}
	
	// Accessor for operation type
	public AWKOperation getOperation() {
		return operation;
	}
	
	// Returns nodes and operations as a string
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (left != null) {
			sb.append(left.toString());
		}
		sb.append(operations.get(operation).toString());
		if (right.isPresent()) {
			sb.append(right.get().toString());
		}
		return sb.toString();
	}
	
	// Map AWKOperation enums to their respective string representations
	public HashMap<OperationNode.AWKOperation, String> operations = new HashMap<OperationNode.AWKOperation, String>(); 
	
	// Method to initialize AWKOperation mapping
	public void initializeAWKOperations() {
		operations.put(OperationNode.AWKOperation.PREINC,"++");
		operations.put(OperationNode.AWKOperation.DOLLAR,"$");
		operations.put(OperationNode.AWKOperation.UNARYNEG,"-");
		operations.put(OperationNode.AWKOperation.PREDEC,"--");
		operations.put(OperationNode.AWKOperation.EQ,"==");
		operations.put(OperationNode.AWKOperation.NE,"!=");
		operations.put(OperationNode.AWKOperation.LT," < ");
		operations.put(OperationNode.AWKOperation.LE,"<=");
		operations.put(OperationNode.AWKOperation.GT," > ");
		operations.put(OperationNode.AWKOperation.GE," >= ");
		operations.put(OperationNode.AWKOperation.AND," && ");
		operations.put(OperationNode.AWKOperation.OR," || ");
		operations.put(OperationNode.AWKOperation.NOT,"!");
		operations.put(OperationNode.AWKOperation.MATCH," ~ ");
		operations.put(OperationNode.AWKOperation.NOTMATCH,"!~");
		operations.put(OperationNode.AWKOperation.POSTINC,"++");
		operations.put(OperationNode.AWKOperation.POSTDEC,"--");
		operations.put(OperationNode.AWKOperation.EXPONENT,"^");
		operations.put(OperationNode.AWKOperation.UNARYPOS,"+");
		operations.put(OperationNode.AWKOperation.ADD,"+");
		operations.put(OperationNode.AWKOperation.IN," in ");
		operations.put(OperationNode.AWKOperation.SUBTRACT,"-");
		operations.put(OperationNode.AWKOperation.MULTIPLY,"*");
		operations.put(OperationNode.AWKOperation.DIVIDE,"/");
		operations.put(OperationNode.AWKOperation.MODULO,"%");
		operations.put(OperationNode.AWKOperation.CONCATENATION," ");
	}
	
	/*
	 * EQ, NE, LT, LE, GT, GE, AND, OR, NOT, MATCH, NOTMATCH, DOLLAR,
        PREINC, POSTINC, PREDEC, POSTDEC, UNARYPOS, UNARYNEG, IN,
        EXPONENT, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, CONCATENATION
	 * 
	 */
}
