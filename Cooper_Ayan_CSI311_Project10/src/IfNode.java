import java.util.Optional;

public class IfNode extends StatementNode{
	
	private Optional<Node> condition; // Condition inside if statement
	private BlockNode ifBlock; // Block of code inside if statement
	private Optional<IfNode> nextIf; // else if or else block
	
	public IfNode(Optional<Node> condition, BlockNode ifBlock) {
		this.condition = condition;
		this.ifBlock = ifBlock;
		this.nextIf = Optional.empty();
	}
	
	public void setNextIf(IfNode nextIf) {
		this.nextIf = Optional.of(nextIf);
	}
	
	public Optional<Node> getCondition() {
		return condition;
	}
	
	public BlockNode getIfBlock() {
		return ifBlock;
	}
	
	public Optional<IfNode> getNextIf() {
		return nextIf;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("if (").append(condition.get().toString()).append(") {\n");
		sb.append(ifBlock.toString()+"}");
		sb.append(" ");
		
		// Handles else if and else
		if (nextIf.isPresent()) {
			sb.append(" else ");
			sb.append(nextIf.get().toString());
		}
		
		return sb.toString();
	}

}
