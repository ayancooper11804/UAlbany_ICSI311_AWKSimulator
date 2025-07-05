import java.util.LinkedList;
import java.util.Optional;

public class TokenHandler {

	private LinkedList<Token> tokens = new LinkedList<Token>(); // Store tokens
	
	public TokenHandler(LinkedList<Token> tokens) {
		this.tokens = tokens;
	}
	
	// Peek j tokens ahead and return the token if we aren't past the end of the token list
	public Optional<Token> Peek(int j) {
		if (j >= 0 && j < tokens.size()) {
			return Optional.of(tokens.get(j));
		}
		return Optional.empty();
	}
	
	// Returns true if the token list is not empty
	public boolean MoreTokens() {
		return !tokens.isEmpty();
	}
	
	// Removed the token at the head of the list, if it is the token passed in. Otherwise, return Optional.empty()
	public Optional<Token> MatchAndRemove(Token.TokenType t) {
		if(MoreTokens()) {
			if(tokens.getFirst().getType() == t) {
				return Optional.of(tokens.pollFirst());
			}	
		}
		return Optional.empty();
	}
}
