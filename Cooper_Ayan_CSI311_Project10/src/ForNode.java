import java.util.Optional;

public class ForNode extends StatementNode{ 
	
	private Optional<Node> initialize; // First statement in for loop
	private Optional<Node> condition; // Second statement in for loop
	private Optional<Node> update; // Third statement in for loop
	private BlockNode forBlock; // Block of code in for loop
	
	public ForNode(Optional<Node> initialize, Optional<Node> condition, Optional<Node> update, BlockNode forBlock) {
		this.initialize = initialize;
		this.condition = condition;
		this.update = update;
		this.forBlock = forBlock;
	}
	
	public Optional<Node> getInitialization() {
		return initialize;
	}
	
	public Optional<Node> getCondition() {
		return condition;
	}
	
	public Optional<Node> getUpdate() {
		return update;
	}
	
	public BlockNode getForBlock() {
		return forBlock;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("for (");
		
		if (initialize.isPresent()) {
			sb.append(initialize.get().toString());
		}
		sb.append("; ");
		
		if (condition.isPresent()) {
			sb.append(condition.get().toString());
		}
		sb.append("; ");
		
		if (update.isPresent()) {
			sb.append(update.get().toString());
		}
		
		sb.append(") {\n");
		sb.append(forBlock.toString());
		sb.append("}" + " ");
		return sb.toString();
	}

}
