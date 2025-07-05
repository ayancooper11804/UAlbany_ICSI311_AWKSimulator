import java.util.LinkedList;
import java.util.HashMap;

public class Lexer {
	
	private StringHandler stringHandler; // Uses StringHandler's methods to access documents
	private int lineNumber; // Tracks the current line number
	private int linePosition; // Tracks the current character position in the current line
	public LinkedList<Token> tokens; // Store tokens
	
	// HashMap to store AWK keywords
	private HashMap<String, Token.TokenType> keywords = new HashMap<String, Token.TokenType>();
	// HashMap to store AWK two-character symbols
	private HashMap<String, Token.TokenType> twoCharSymbols = new HashMap<String, Token.TokenType>();
	// HashMap to store AWK one-character symbols
	private HashMap<String, Token.TokenType> oneCharSymbols = new HashMap<String, Token.TokenType>();
	
	public Lexer(String input) {
		stringHandler = new StringHandler(input);
		lineNumber = 0;
		linePosition = 0;
		tokens = new LinkedList<Token>();
		initializeKeywords();
		initializeTwoSymbols();
		initializeOneSymbols();
	}
	
	// Helper method to populate the keywords HashMap in the constructor
	private void initializeKeywords() {
		keywords.put("while", Token.TokenType.WHILE);
		keywords.put("if", Token.TokenType.IF);
		keywords.put("do", Token.TokenType.DO);
		keywords.put("for", Token.TokenType.FOR);
		keywords.put("break", Token.TokenType.BREAK);
		keywords.put("continue", Token.TokenType.CONTINUE);
		keywords.put("else", Token.TokenType.ELSE);
		keywords.put("return", Token.TokenType.RETURN);
		keywords.put("BEGIN", Token.TokenType.BEGIN);
		keywords.put("END", Token.TokenType.END);
		keywords.put("print", Token.TokenType.PRINT);
		keywords.put("printf",Token.TokenType.PRINTF);
		keywords.put("next", Token.TokenType.NEXT);
		keywords.put("in", Token.TokenType.IN);
		keywords.put("delete", Token.TokenType.DELETE);
		keywords.put("getline", Token.TokenType.GETLINE);
		keywords.put("exit", Token.TokenType.EXIT);
		keywords.put("nextfile", Token.TokenType.NEXTFILE);
		keywords.put("function", Token.TokenType.FUNCTION);
	}
	
	// Helper method to populate the twoSymbol HashMap in the constructor
	private void initializeTwoSymbols() {
		twoCharSymbols.put(">=", Token.TokenType.GREATEREQUALTO);
		twoCharSymbols.put("++", Token.TokenType.INCREMENT);
		twoCharSymbols.put("--", Token.TokenType.DECREMENT);
		twoCharSymbols.put("<=", Token.TokenType.LESSEQUALTO);
		twoCharSymbols.put("==", Token.TokenType.COMPARETO);
		twoCharSymbols.put("!=", Token.TokenType.NOTEQUALTO);
		twoCharSymbols.put("^=", Token.TokenType.EXPONENTASSIGNMENT);
		twoCharSymbols.put("%=", Token.TokenType.MODULUSASSIGNMENT);
		twoCharSymbols.put("*=", Token.TokenType.MULTIPLYASSIGNMENT);
		twoCharSymbols.put("/=", Token.TokenType.DIVIDEASSIGNMENT);
		twoCharSymbols.put("+=", Token.TokenType.ADDASSIGNMENT);
		twoCharSymbols.put("-=", Token.TokenType.SUBTRACTASSIGNMENT);
		twoCharSymbols.put("!~", Token.TokenType.NOTMATCH);
		twoCharSymbols.put("&&", Token.TokenType.LOGICALAND);
		twoCharSymbols.put(">>", Token.TokenType.RIGHTSHIFT);
		twoCharSymbols.put("||", Token.TokenType.LOGICALOR);
	}
	
	// Helper method to populate the oneSymbol HashMap in the constructor
	private void initializeOneSymbols() {
		oneCharSymbols.put("{", Token.TokenType.LEFTCURLYBRACES);
		oneCharSymbols.put("}", Token.TokenType.RIGHTCURLYBRACES);
		oneCharSymbols.put("[", Token.TokenType.LEFTSQUAREBRACES);
		oneCharSymbols.put("]", Token.TokenType.RIGHTSQUAREBRACES);
		oneCharSymbols.put("(", Token.TokenType.LEFTPARENTHESIS);
		oneCharSymbols.put(")", Token.TokenType.RIGHTPARENTHESIS);
		oneCharSymbols.put("$", Token.TokenType.DOLLARSIGN);
		oneCharSymbols.put("~", Token.TokenType.MATCH);
		oneCharSymbols.put("=", Token.TokenType.EQUALS);
		oneCharSymbols.put("<", Token.TokenType.LEFTARROW);
		oneCharSymbols.put(">", Token.TokenType.RIGHTARROW);
		oneCharSymbols.put("!", Token.TokenType.EXCLAMATION);
		oneCharSymbols.put("+", Token.TokenType.ADD);
		oneCharSymbols.put("^", Token.TokenType.EXPONENT);
		oneCharSymbols.put("-", Token.TokenType.SUBTRACT);
		oneCharSymbols.put("?", Token.TokenType.QUESTION);
		oneCharSymbols.put(":", Token.TokenType.COLON);
		oneCharSymbols.put("*", Token.TokenType.MULTIPLY);
		oneCharSymbols.put("/", Token.TokenType.DIVIDE);
		oneCharSymbols.put("%", Token.TokenType.MODULUS);
		oneCharSymbols.put(";", Token.TokenType.SEPARATOR); // AWK doesn't require semicolons, but allows them
		oneCharSymbols.put("\n", Token.TokenType.SEPARATOR); // New line, so mapped to separator
		oneCharSymbols.put("|", Token.TokenType.OR);
		oneCharSymbols.put(",", Token.TokenType.COMMA);
	}
	
	// Makes a string of letters, numbers, and underscores
	public Token ProcessWord() {
		StringBuilder builder = new StringBuilder();
		while (Character.isLetterOrDigit(stringHandler.Peek(0)) || stringHandler.Peek(0) == '_') {
			builder.append(stringHandler.GetChar());
			linePosition++; // Increment the current character position
		}
		
		String value = builder.toString();
		
		// Check if the value is a keyword, otherwise return a WORD token
		if (keywords.containsKey(value)) {
			return new Token(keywords.get(value), null, lineNumber, linePosition);
		}
		else {
			return new Token(Token.TokenType.WORD, builder.toString(), lineNumber, linePosition);
		}
	}
	
	// Makes numbers, including numbers with a decimal
	public Token ProcessNumber() {
		StringBuilder builder = new StringBuilder();
		boolean hasDecimal = false;
		while (Character.isDigit(stringHandler.Peek(0)) || !hasDecimal && stringHandler.Peek(0) == '.')  {
			char c = stringHandler.GetChar();
			linePosition++; // Increment the current character position
			builder.append(c);
			if (c == '.' ) {
				hasDecimal = true; // Set a flag when a decimal point is encountered.
			}
		}
		
		// Check if the last character is a letter; if so, consider it a word
		if (Character.isLetter(builder.charAt(builder.length() - 1))) {
			return new Token(Token.TokenType.WORD, builder.toString(), lineNumber, linePosition);
		}
		else {
			return new Token(Token.TokenType.NUMBER, builder.toString(), lineNumber, linePosition);
		}
	}
	
	// Handle String Literals
	public Token HandleStringLiteral() {
		StringBuilder builder = new StringBuilder();
		stringHandler.GetChar(); // Move past the opening double quote
		linePosition++;
		
		while (!stringHandler.IsDone()) {
			char currentChar = stringHandler.GetChar();
			linePosition++;
			
			// Handle escaped
			if (currentChar == '\\') {
				char escapedChar = stringHandler.GetChar();
				linePosition++;
				builder.append('\\').append(escapedChar);
			}
			
			// End of String Literal
			else if (currentChar == '"') {
				return new Token(Token.TokenType.STRINGLITERAL, builder.toString(), lineNumber, linePosition);
			}
			else {
				builder.append(currentChar);
			}
		}
		
		throw new RuntimeException("Unclosed String Literal");
	}
	
	// Handle regular expressions ( similar to HandleStringLiteral() )
	public Token HandlePattern() {
		StringBuilder builder = new StringBuilder();
		stringHandler.GetChar(); // Move past the opening backtick
		linePosition++;
		
		while (!stringHandler.IsDone()) {
			char currentChar = stringHandler.GetChar();
			linePosition++;
			
			// End of regular expression
			if (currentChar == '`') {
				// Reached the closing backtick, break the loop
				break;
			}
			builder.append(currentChar);
		}
		return new Token(Token.TokenType.PATTERN, builder.toString(), lineNumber, linePosition);
	}
	
	public Token ProcessSymbol() {
		// Use Remainder to get the remaining text in the input, and then we extract the first two characters to
		// check for two-character symbols. This ensures symbols are recognized correctly, even without whitespace
		String remainingText = stringHandler.Remainder();
		String twoCharSymbol = remainingText.substring(0, Math.min(2, remainingText.length()));
		
		if (twoCharSymbols.containsKey(twoCharSymbol)) {
			// Found a two-character symbol
			stringHandler.Swallow(2); // Consume the two characters
			linePosition += 2; // Update internal line position
			return new Token(twoCharSymbols.get(twoCharSymbol), null, lineNumber, linePosition);
		} else {
			char currentChar = stringHandler.Peek(0);
			String oneCharSymbol = String.valueOf(currentChar);
			
			if (oneCharSymbols.containsKey(oneCharSymbol)) {
				// Found a one-character symbol
				stringHandler.Swallow(1); // Consume the one character
				linePosition++; // Update internal line position
				return new Token(oneCharSymbols.get(oneCharSymbol), null, lineNumber, linePosition);
			} else {
				return null;
			}
		}
		
	}
	
	// Tokenizes the input string and populates the 'tokens' list
	public void Lex() {
		while (!stringHandler.IsDone()) {
			char currentChar = stringHandler.Peek(0);
			if (Character.isWhitespace(currentChar)) {
				linePosition++;
				if (currentChar == '\n') {
					tokens.add(new Token(Token.TokenType.SEPARATOR, lineNumber, linePosition)); // Add separator for new line
					lineNumber++;
					linePosition = 0; // Reset the character position for the new line
				}
				stringHandler.GetChar(); // Move past whitespace
			}
			else if (currentChar == '\r') {
				// Ignore carriage return
				stringHandler.GetChar(); // Move past \r
			}
			else if (currentChar == '#') {
				// Ignore comment lines
				while (!stringHandler.IsDone() && stringHandler.Peek(0) != '\n') {
					stringHandler.GetChar();
				}
			}
			else if (currentChar == '"') {
				// Handle String Literals
				Token stringLiteralToken = HandleStringLiteral();
				tokens.add(stringLiteralToken);
			}
			else if (currentChar == '`') {
				// Handle regular expressions
				Token regularExpressionToken = HandlePattern();
				tokens.add(regularExpressionToken);
			}
			else if (Character.isLetter(currentChar)) {
				Token wordToken = ProcessWord(); // ProcessWord returns a token (WORD or keyword)
				tokens.add(wordToken);
			}
			else if (Character.isDigit(currentChar)) {
				tokens.add(ProcessNumber()); // Add NUMBER token
			}
			else {
				// Process symbols using ProcessSymbol
				Token symbolToken = ProcessSymbol();
				if (symbolToken != null) {
					tokens.add(symbolToken);
				}
				else {
					throw new RuntimeException("Unrecognized character: " + currentChar); // Handle unrecognized characters
				}
				
			}
		}
	}
	
	// Returns a string representation of the tokens produced by the lexer
	public String toString() {
		String output = "";
		for(Token t: tokens) 
			output += t.toString() + " "; // Concatenate token strings
		//System.out.println(keywords);
		return output;
	}
}
