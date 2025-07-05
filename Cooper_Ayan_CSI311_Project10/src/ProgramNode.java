import java.util.LinkedList;

public class ProgramNode extends Node{
	
	private LinkedList<BlockNode> beginBlocks; // BEGIN blocks
	private LinkedList<BlockNode> endBlocks; // END blocks
	private LinkedList<BlockNode> otherBlocks; // OTHER blocks
	private LinkedList<FunctionDefinitionNode> functionDefinitions; // Functions
	
	public ProgramNode() {
		this.beginBlocks = new LinkedList<>();
		this.endBlocks = new LinkedList<>();
		this.otherBlocks = new LinkedList<>();
		this.functionDefinitions = new LinkedList<>();
	}
	
	public LinkedList<BlockNode> getBeginBlocks() {
		return beginBlocks;
	}
	
	public LinkedList<BlockNode> getEndBlocks() {
		return endBlocks;
	}
	
	public LinkedList<BlockNode> getOtherBlocks() {
		return otherBlocks;
	}
	
	// Adds BEGIN blocks
	public void addBeginBlock(BlockNode block) {
		beginBlocks.add(block);
	}
	
	// Adds END blocks
	public void addEndBlock(BlockNode block) {
		endBlocks.add(block);
	}
	
	// Adds other blocks
	public void addOtherBlock(BlockNode block) {
		otherBlocks.add(block);
	}
	
	public LinkedList<FunctionDefinitionNode> getFunctions() {
		return functionDefinitions;
	}
	
	// Adds functions
	public void addFunctionDefinition(FunctionDefinitionNode function) {
		functionDefinitions.add(function);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// Append function definitions
		for (FunctionDefinitionNode function : functionDefinitions) {
			sb.append(function.toString()).append("\n");
		}
		
		// Append BEGIN blocks
		for (BlockNode beginBlock : beginBlocks) {
			sb.append("BEGIN ");
			sb.append(beginBlock.toString()).append("\n");
		}
		
		// Append END blocks
		for (BlockNode endBlock : endBlocks) {
			sb.append("END ");
			sb.append(endBlock.toString()).append("\n");
		}
		
		// Append other blocks
		for (BlockNode otherBlock : otherBlocks) {
			sb.append(otherBlock.toString()).append(" ");
		}
		
		return sb.toString();
	}
}
