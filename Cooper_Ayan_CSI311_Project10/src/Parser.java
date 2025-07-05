import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser {

		private TokenHandler tokenHandler; // Use's TokenHandler's methods to access tokens
		public LinkedList<Token> tokens; // Store tokens
		
		public Parser(LinkedList<Token> tokens) {
			tokenHandler = new TokenHandler(tokens);
			this.tokens = tokens;
		}
		
		// Accepts any number of separators and returns true if it finds at least one
		public boolean AcceptSeparators() {
			boolean foundSeparator = false;
			while (!tokenHandler.MatchAndRemove(Token.TokenType.SEPARATOR).isEmpty()) {
				foundSeparator = true;
			}
			return foundSeparator;
		}
		
		// Looks for various types of BlockNodes and places them appropriately in the ProgramNode
		public boolean ParseAction(ProgramNode programNode) throws Exception {
			AcceptSeparators();
			if (tokenHandler.MatchAndRemove(Token.TokenType.BEGIN).isPresent()) {
				// Found BEGIN token and returns Begin BlockNode
				BlockNode beginBlock = ParseBlock();
				programNode.addBeginBlock(beginBlock);
				return true;
			}
			else if (tokenHandler.MatchAndRemove(Token.TokenType.END).isPresent()) {
				// Found END token and returns End BlockNode
				BlockNode endBlock = ParseBlock();
				programNode.addEndBlock(endBlock);
				return true;
			}
			else {
				// Found other BlockNodes and returns Other BlockNode
				Optional<Node> operation = ParseOperation();
				BlockNode otherBlock = ParseBlock();
				otherBlock.addCondition(operation);
				programNode.addOtherBlock(otherBlock);
				return true;
			}
		}
		
		// If a function exists, creates a FunctionDefinitionNode, populate it with name and parameters and add 
		// it to the ProgramNode's function list
		public boolean ParseFunction(ProgramNode programNode) throws Exception{
			FunctionDefinitionNode function; // Creates a FunctionDefinitionNode
			
			// Checks if function token exists
			Optional<Token> check = tokenHandler.MatchAndRemove(Token.TokenType.FUNCTION); 
			if (check.isPresent()) {
				check = tokenHandler.MatchAndRemove(Token.TokenType.WORD);
				if (check.isEmpty()) {
					throw new RuntimeException("No name for function");
				}
				function = new FunctionDefinitionNode(check.get().getValue());
				AcceptSeparators(); // Accounts for multiple lines
				
				// Checks for left parenthesis and adds it to FunctionDefinitionNode
				if (tokenHandler.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS).isPresent()) {
					//function.addParameter("("); // Uses addParameter to include parenthesis in input
					
					// Adds parameter(s) and comma(s) to the FunctionDefinitionNode if they exist
					while (true) {
						AcceptSeparators(); // Accounts for multiple lines
						String parameterName = tokenHandler.MatchAndRemove(Token.TokenType.WORD).get().getValue();
						if (!parameterName.isEmpty()) {
							function.addParameter(parameterName);

						}
						if (tokenHandler.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
							String otherParameterNames = tokenHandler.MatchAndRemove(Token.TokenType.WORD).get().getValue();
							if (!otherParameterNames.isEmpty()) {
								//function.addParameter(", "); // Uses addParameter to include commas in input
								//function.addParameter(parameterName);
								function.addParameter(otherParameterNames);
								
							}
							else {
								throw new RuntimeException("Expected a parameter after the comma");
							}
							
						}
						// Checks for right parenthesis and adds it to FunctionDefinitionNode
						if (tokenHandler.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS).isPresent()) {
							//function.addParameter(")"); // Uses addParameter to include parenthesis in input
							break;
						}
						// Throw exception if the FunctionDefinitionNode is not written correctly
						else {
							throw new RuntimeException("Function is not properly declared");
						}
					}
					// Add statements from the BlockNode to the FunctionDefinitionNode's linked list
					// of StatementNod
					
					BlockNode block = ParseBlock();
					
					function.addStatements(block);
					// Add the FunctionDefinitionNode to the ProgramNode
					programNode.addFunctionDefinition(function);
				}
				return true;
			}
			return false;
		}

		// Handles either multi-line blocks or single line blocks
		public BlockNode ParseBlock() throws Exception {
			BlockNode block = new BlockNode();
			if (tokenHandler.MatchAndRemove(Token.TokenType.LEFTCURLYBRACES).isPresent()) {
				// Multi-line block
				AcceptSeparators();
				while (true) {
					Optional<StatementNode> statement = ParseStatement();
					if (statement.isPresent()) {
						block.addStatement(statement.get());
						AcceptSeparators();
					}
					else {
						break;
					}
				}
				
				if (!tokenHandler.MatchAndRemove(Token.TokenType.RIGHTCURLYBRACES).isPresent()) {
					throw new RuntimeException("Expected a closing curly brace '}'");
				}
				return block;
			}
			
			// Single line block
			Optional<StatementNode> statement = ParseStatement();
			if (statement.isPresent()) {
				block.addStatement(statement.get());
				AcceptSeparators();
			}
			
			return block;
		}
		
		// Currently returns ParseAssignment for testing
		public Optional<Node> ParseOperation() throws Exception {
			return ParseAssignment();
		}
		
		public Optional<Node> ParseLValue() throws Exception {
			// Check for $, parses the bottom-level expression if found and return an OperationNode
			if (tokenHandler.MatchAndRemove(Token.TokenType.DOLLARSIGN).isPresent()) {
				Optional<Node> bottomLevel = ParseBottomLevel();
				if (bottomLevel.isPresent()) {
					return Optional.of(new OperationNode(OperationNode.AWKOperation.DOLLAR, bottomLevel));
				}
			}
			
			// Checks for word token and possible array as an operation, parses and returns a VariableReferenceNode
			Optional<Token> wordToken = tokenHandler.MatchAndRemove(Token.TokenType.WORD);
			if (wordToken.isPresent()) {
				if (tokenHandler.MatchAndRemove(Token.TokenType.LEFTSQUAREBRACES).isPresent()) {
					Optional<Node> arrayIndex = ParseOperation();
					if (tokenHandler.MatchAndRemove(Token.TokenType.RIGHTSQUAREBRACES).isPresent()) {
						return Optional.of(new VariableReferenceNode(wordToken.get().getValue(), arrayIndex));
					}
				}
				else {
					return Optional.of(new VariableReferenceNode(wordToken.get().getValue()));
				}
			}
			
			return Optional.empty();
		}
		
		public Optional<Node> ParseBottomLevel() throws Exception {
			// Peek at the next token without removing it
			Optional<Token> token = tokenHandler.Peek(0);
			
			// Check if a token is present
			if (token.isPresent()) {
				Token actualToken = token.get();
				
				// Checks for various operations or tokens (StringLiterals, numbers, patterns, parentheses), parses
				// them, and returns the matching operation or token into the appropriate node
				if (actualToken.getType() == Token.TokenType.STRINGLITERAL) {
					tokenHandler.MatchAndRemove(Token.TokenType.STRINGLITERAL);
					return Optional.of(new ConstantNode(actualToken.getValue()));
				}
				else if (actualToken.getType() == Token.TokenType.NUMBER) {
					tokenHandler.MatchAndRemove(Token.TokenType.NUMBER);
					return Optional.of(new ConstantNode(actualToken.getValue()));
				}
				else if (actualToken.getType() == Token.TokenType.PATTERN) {
					tokenHandler.MatchAndRemove(Token.TokenType.PATTERN);
					return Optional.of(new PatternNode("`" + actualToken.getValue() + "`"));
				}
				else if (actualToken.getType() == Token.TokenType.LEFTPARENTHESIS) {
					tokenHandler.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
					Optional<Node> result = ParseOperation();
					tokenHandler.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
					return result;
				}
				else if (actualToken.getType() == Token.TokenType.EXCLAMATION) {
					tokenHandler.MatchAndRemove(Token.TokenType.EXCLAMATION);
					Optional<Node> result = ParseOperation();
					return Optional.of(new OperationNode(OperationNode.AWKOperation.NOT, result));
				}
				else if (actualToken.getType() == Token.TokenType.SUBTRACT) {
					tokenHandler.MatchAndRemove(Token.TokenType.SUBTRACT);
					Optional<Node> result = ParseOperation();
					return Optional.of(new OperationNode(OperationNode.AWKOperation.UNARYNEG, result));
				}
				else if (actualToken.getType() == Token.TokenType.ADD) {
					tokenHandler.MatchAndRemove(Token.TokenType.ADD);
					Optional<Node> result = ParseOperation();
					return Optional.of(new OperationNode(OperationNode.AWKOperation.UNARYPOS, result));
				}
				else if (actualToken.getType() == Token.TokenType.INCREMENT) {
					tokenHandler.MatchAndRemove(Token.TokenType.INCREMENT);
					Optional<Node> result = ParseOperation();
					OperationNode preIncOp = new OperationNode(OperationNode.AWKOperation.PREINC, result);
					return Optional.of(new AssignmentNode(result.get(), preIncOp));
				}
				else if (actualToken.getType() == Token.TokenType.DECREMENT) {
					tokenHandler.MatchAndRemove(Token.TokenType.DECREMENT);
					Optional<Node> result = ParseOperation();
					OperationNode preDecOp = new OperationNode(OperationNode.AWKOperation.PREDEC, result);
					return Optional.of(new AssignmentNode(result.get(), preDecOp));
				}
				else {
					Optional<Node> specialFunction = ParseSpecialFunctionCall();
					if (specialFunction.isPresent()) {
						return specialFunction;
					}
					Optional<Node> Lvalue = ParseLValue();
					Optional<Node> functionCall = ParseFunctionCall(Lvalue);
					if (functionCall.isPresent()) {
						return Optional.of(functionCall.get());
					}
				}
			}
			return ParseLValue();
		}
		
		// Parser 3 operation methods
		
		public Optional<Node> ParsePost() throws Exception {
			Optional<Node> left = ParseBottomLevel();
			
			Optional<Token> incToken = tokenHandler.MatchAndRemove(Token.TokenType.INCREMENT);
			Optional<Token> decToken = tokenHandler.MatchAndRemove(Token.TokenType.DECREMENT);
			
			// Check for post increment; make a new operation node
			if (incToken.isPresent()) {
				OperationNode postIncOp = new OperationNode(left.get(), OperationNode.AWKOperation.POSTINC);
				return Optional.of(new AssignmentNode(left.get(), postIncOp));
				//return Optional.of(new OperationNode(left.get(), OperationNode.AWKOperation.POSTINC));
			}
			// Check for post decrement; make a new operation node
			else if (decToken.isPresent()) {
				OperationNode postDecOp = new OperationNode(left.get(), OperationNode.AWKOperation.POSTDEC);
				return Optional.of(new AssignmentNode(left.get(), postDecOp));
				//return Optional.of(new OperationNode(left.get(), OperationNode.AWKOperation.POSTDEC));
			}
			
			return left; // If neither post increment/decrement, return ParseBottomLevel()
		}
		
		// Handles exponents in AWK
		// Uses recursion due to right association
		public Optional<Node> ParseExponents() throws Exception {
			Optional<Node> left = ParsePost();
			
			//do {
				Optional<Token> exponentToken = tokenHandler.MatchAndRemove(Token.TokenType.EXPONENT);
				if (exponentToken.isPresent()) {
					Optional<Node> right = ParsePost();
					return Optional.of(new OperationNode(left.get(), OperationNode.AWKOperation.EXPONENT, right));
				}
				return left;
			//} while (true);	
		}
		
		// Checks for number or expression
		public Optional<Node> ParseFactor() throws Exception{
			return ParseBottomLevel();
		}
		
		// Checks for * / or %
		// Should call Exponents
		public Optional<Node> ParseTerm() throws Exception{
			Optional<Node> left = ParseExponents();
			
			//do {
				Optional<Token> opToken = tokenHandler.MatchAndRemove(Token.TokenType.MULTIPLY);
				OperationNode.AWKOperation operation = OperationNode.AWKOperation.MULTIPLY;
				if (opToken.isEmpty()) {
					opToken = tokenHandler.MatchAndRemove(Token.TokenType.DIVIDE);
					operation = OperationNode.AWKOperation.DIVIDE;
				}
				if (opToken.isEmpty()) {
					opToken = tokenHandler.MatchAndRemove(Token.TokenType.MODULUS);
					operation = OperationNode.AWKOperation.MODULO;
				}
				if (opToken.isEmpty()) {
					operation = null;
					return left;
				}
				//Optional<Node> right = ParseFactor();
				Optional<Node> right = ParseExponents();
				return Optional.of(new OperationNode(left.get(), operation, right));
			//} while (true);
		}
		
		// Checks for + or -
		public Optional<Node> ParseExpression() throws Exception {
			Optional<Node> left = ParseTerm();
			
			//do {
				Optional<Token> opToken = tokenHandler.MatchAndRemove(Token.TokenType.ADD);
				OperationNode.AWKOperation operation = OperationNode.AWKOperation.ADD;
				if (opToken.isEmpty()) {
					opToken = tokenHandler.MatchAndRemove(Token.TokenType.SUBTRACT);
					operation = OperationNode.AWKOperation.SUBTRACT;
				}
				if (opToken.isEmpty()) {
					operation = null;
					return left;
				}
				//Optional<Node> right = ParseTerm();
				Optional<Node> right = ParseTerm();
				return Optional.of(new OperationNode(left.get(), operation, right));
			//} while (true);
		}	
		
		// Handler string concatenation in AWK (expr expr)
		public Optional<Node> ParseConcatenation() throws Exception {
			Optional<Node> left = ParseExpression();
			if (left.isEmpty()) {
				return left;
			}
			Optional<Node> right = ParseConcatenation();
			if (right.isEmpty()) {
				return left;
			}
			else {
				return Optional.of(new OperationNode(left.get(), OperationNode.AWKOperation.CONCATENATION, right));
			}
		}
		
		// < <= != == > >=
		// Handles comparison operators in AWK (< <= != == > >=)
		public Optional<Node> ParseBooleanCompare() throws Exception {
			
			Optional<Node> left = ParseConcatenation();
			Optional<Token> compareToken = tokenHandler.MatchAndRemove(Token.TokenType.LEFTARROW);
			OperationNode.AWKOperation compareOp = OperationNode.AWKOperation.LT;
			if (compareToken.isEmpty()) {
				compareToken = tokenHandler.MatchAndRemove(Token.TokenType.LESSEQUALTO);
				compareOp = OperationNode.AWKOperation.LE;
			}
			if (compareToken.isEmpty()) {
				compareToken = tokenHandler.MatchAndRemove(Token.TokenType.NOTEQUALTO);
				compareOp = OperationNode.AWKOperation.NE;
			}
			if (compareToken.isEmpty()) {
				compareToken = tokenHandler.MatchAndRemove(Token.TokenType.COMPARETO);
				compareOp = OperationNode.AWKOperation.EQ;
			}
			if (compareToken.isEmpty()) {
				compareToken = tokenHandler.MatchAndRemove(Token.TokenType.RIGHTARROW);
				compareOp = OperationNode.AWKOperation.GT;
			}
			if (compareToken.isEmpty()) {
				compareToken = tokenHandler.MatchAndRemove(Token.TokenType.GREATEREQUALTO);
				compareOp = OperationNode.AWKOperation.GE;
			}
			if (compareToken.isEmpty()) {
				compareOp = null;
				return left;
			}
			
			Optional<Node> right = ParseConcatenation();
			if (!right.isPresent()) {
				throw new Exception("Expected another expression");
			}
			return Optional.of(new OperationNode(left.get(), compareOp, right));
		}
		
		// Handles ERE Match and ERE Not Match in AWK (expr ~ expr; expr !~ expr)
		public Optional<Node> ParseMatch() throws Exception {
			// ~ !~
			
			Optional<Node> left = ParseBooleanCompare();
			Optional<Token> matchToken = tokenHandler.MatchAndRemove(Token.TokenType.MATCH);
			OperationNode.AWKOperation matchOp = OperationNode.AWKOperation.MATCH;
			if (matchToken.isEmpty()) {
				matchToken = tokenHandler.MatchAndRemove(Token.TokenType.NOTMATCH);
				matchOp = OperationNode.AWKOperation.NOTMATCH;
			}
			if (matchToken.isEmpty()) {
				matchOp = null;
				return left;
			}
			
			Optional<Node> right = ParseBooleanCompare();
			if (!right.isPresent()) {
				throw new Exception("Expected another expression");
			}
			return Optional.of(new OperationNode(left.get(), matchOp, right));
		}
		
		// Handles array membership in AWK (expr in array)
		public Optional<Node> ParseArrayMembership() throws Exception {
			Optional<Node> left = ParseMatch();
			Optional<Token> inToken = tokenHandler.MatchAndRemove(Token.TokenType.IN);
			
			if (inToken.isPresent()) {
				Optional<Node> array = ParseMatch();
				
				if (!array.isPresent()) {
					throw new Exception("Expcedted an array to follow 'in'");
				}
				return Optional.of(new OperationNode(left.get(), OperationNode.AWKOperation.IN, array));
			}
			return left;
		}
		
		// Handles logical AND in AWK (&&)
		public Optional<Node> ParseAND() throws Exception {
			// &&
			Optional<Node> left = ParseArrayMembership();
			//do {
				
			Optional<Token> andToken = tokenHandler.MatchAndRemove(Token.TokenType.LOGICALAND);
			if (andToken.isPresent()) {
				Optional<Node> right = ParseArrayMembership();
				
				if (!right.isPresent()) {
					throw new Exception("Expected another expression");
				}
				return Optional.of(new OperationNode(left.get(), OperationNode.AWKOperation.AND, right));
			}
			else {
				return left;
			}
			//} while (true);
		}
		
		// Handles logical OR in AWK (||)
		public Optional<Node> ParseOR() throws Exception {
			// ||
			Optional<Node> left = ParseAND();
			
			//do {
				
			Optional<Token> orToken = tokenHandler.MatchAndRemove(Token.TokenType.LOGICALOR);
			if (orToken.isPresent()) {
				Optional<Node> right = ParseAND();
				
				if (!right.isPresent()) {
					throw new Exception("Expected another expression");
				}
				return Optional.of(new OperationNode(left.get(), OperationNode.AWKOperation.OR, right));
			}
			else {
				return left;
			}
				
			//} while (true);
		}
		
		// Handles ternary operation in AWK (expr1 ? expr2 : expr3)
		// Uses recursion due to right association
		public Optional<Node> ParseTernary() throws Exception {
			// expr1 ? expr2 : expr3
			// right associativity
			Optional<Node> condition = ParseOR();
			if (condition.isPresent()) {
				Optional<Token> questionToken = tokenHandler.MatchAndRemove(Token.TokenType.QUESTION);
				if (questionToken.isPresent()) {
					Optional<Node> trueCase = ParseOR();
					if (!trueCase.isPresent()) {
						throw new Exception("Expected an expression for the true case");
					}
					Optional<Token> colonToken = tokenHandler.MatchAndRemove(Token.TokenType.COLON);
					if (colonToken.isPresent()) {
						Optional<Node> falseCase = ParseOR();
						if (!falseCase.isPresent()) {
							throw new Exception("Expected an expression for the false case");
						}
						return Optional.of(new TernaryNode(condition.get(), trueCase.get(), falseCase.get()));
					}
					else {
						throw new Exception("Expected a colon (':') after the true case expression");
					}	
				}
			} 
			return condition;
		}
		
		// Handles assignment operators in AWK (^= %= *= /= += -= =)
		// Uses recursion due to right association
		public Optional<Node> ParseAssignment() throws Exception {
			// ^= %= *= /= += -= =
			// right associativity

			Optional<Node> left = ParseTernary();
				
			Optional<Token> assignToken = tokenHandler.MatchAndRemove(Token.TokenType.EXPONENTASSIGNMENT);
			OperationNode.AWKOperation operation = OperationNode.AWKOperation.EXPONENT;
			
			if (assignToken.isEmpty()) {
				assignToken = tokenHandler.MatchAndRemove(Token.TokenType.MODULUSASSIGNMENT);
				operation = OperationNode.AWKOperation.MODULO;
			}
			if (assignToken.isEmpty()) {
				assignToken = tokenHandler.MatchAndRemove(Token.TokenType.MULTIPLYASSIGNMENT);
				operation = OperationNode.AWKOperation.MULTIPLY;
			}
			if (assignToken.isEmpty()) {
				assignToken = tokenHandler.MatchAndRemove(Token.TokenType.DIVIDEASSIGNMENT);
				operation = OperationNode.AWKOperation.DIVIDE;
			}
			if (assignToken.isEmpty()) {
				assignToken = tokenHandler.MatchAndRemove(Token.TokenType.ADDASSIGNMENT);
				operation = OperationNode.AWKOperation.ADD;
			}
			if (assignToken.isEmpty()) {
				assignToken = tokenHandler.MatchAndRemove(Token.TokenType.SUBTRACTASSIGNMENT);
				operation = OperationNode.AWKOperation.SUBTRACT;
			}
			if (assignToken.isEmpty()) {
				if((assignToken = tokenHandler.MatchAndRemove(Token.TokenType.EQUALS)).equals(Optional.empty()))
					return left;
				Optional<Node> right = ParseTernary();
				if (!right.isPresent()) {
					throw new Exception("Expected an expression");
				}
				return Optional.of(new AssignmentNode(left.get(), right.get()));
				//operation = OperationNode.AWKOperation.EQ;
//				if (assignToken.isPresent()) {
//					Optional<Node> right = ParseOperation();
//					if (!right.isPresent()) {
//						throw new Exception("Expected an expression");
//					}
//					return Optional.of(new OperationNode(left.get(),  OperationNode.AWKOperation.EQ, right));
//				}
			}
			if (assignToken.isEmpty()) {
				return left;
			}
			
			//if (assignToken.isPresent()) {
			Optional<Node> right = ParseTernary();
			if (!right.isPresent()) {
				throw new Exception("Expected an expression");
			}
			OperationNode assignOp = new OperationNode(left.get(), operation, right);
			return Optional.of(new AssignmentNode(left.get(), assignOp));
			//}
			//else {
				//return left;
			//}
			
		}
		
		// Tries to parse each of the statement types, returning the first one that succeeds
		public Optional<StatementNode> ParseStatement() throws Exception {
			if (tokenHandler.MatchAndRemove(Token.TokenType.CONTINUE).isPresent()) {
				return ParseContinue();
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.BREAK).isPresent()) {
				return ParseBreak();
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.IF).isPresent()) {
				return ParseIf();
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.FOR).isPresent()) {
				return ParseFor();
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.DELETE).isPresent()) {
				return ParseDelete();
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.WHILE).isPresent()) {
				return ParseWhile();
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.DO).isPresent()) {
				return ParseDoWhile();
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.RETURN).isPresent()) {
				return ParseReturn();
			}
			Optional<Node> operation = ParseOperation();
			if (operation.isPresent()) {
				if(operation.get() instanceof FunctionCallNode) {
					return Optional.of((FunctionCallNode)operation.get());
				}
				if (operation.get() instanceof AssignmentNode) {
					return Optional.of((AssignmentNode) operation.get());
				}
				else if (operation.get() instanceof OperationNode) {
					if (((OperationNode) operation.get()).getOperation().equals(OperationNode.AWKOperation.POSTINC) ) {
						return Optional.of(new AssignmentNode(((OperationNode) operation.get()).getLeft(), operation.get()));
					}
					if (((OperationNode) operation.get()).getOperation().equals(OperationNode.AWKOperation.POSTDEC) ) {
						return Optional.of(new AssignmentNode(((OperationNode) operation.get()).getLeft(), operation.get()));
					}
					if (((OperationNode) operation.get()).getOperation().equals(OperationNode.AWKOperation.PREINC) ) {
						return Optional.of(new AssignmentNode(((OperationNode) operation.get()).getLeft(), operation.get()));
					}
					if (((OperationNode) operation.get()).getOperation().equals(OperationNode.AWKOperation.PREDEC) ) {
						return Optional.of(new AssignmentNode(((OperationNode) operation.get()).getLeft(), operation.get()));
					}
				}
			}
			return Optional.empty();	
		}
		
		// Returns ContinueNode for continue statement
		public Optional<StatementNode> ParseContinue() throws Exception {
			//if (tokenHandler.MatchAndRemove(Token.TokenType.CONTINUE).isPresent()) {
				return Optional.of(new ContinueNode());
			//}
			//return Optional.empty();
		}
		
		// Returns BreakNode for break statement
		public Optional<StatementNode> ParseBreak() throws Exception {
			return Optional.of(new BreakNode());
		}
		
		// Returns IfNode for if, else if, and else statements
		public Optional<StatementNode> ParseIf() throws Exception {
			//if (tokenHandler.MatchAndRemove(Token.TokenType.IF).isPresent()) {
			Optional<Node> condition = ParseOperation();
			BlockNode ifBlock = ParseBlock();
			IfNode ifNode = new IfNode(condition, ifBlock);
			IfNode currentIfNode = ifNode;
			
			while (tokenHandler.MatchAndRemove(Token.TokenType.ELSE).isPresent()) {
				if (tokenHandler.MatchAndRemove(Token.TokenType.IF).isPresent()) {
					Optional<Node> elseIfCondition = ParseOperation();
					BlockNode elseIfBlock = ParseBlock();
					IfNode elseIfNode = new IfNode(elseIfCondition, elseIfBlock);
					currentIfNode.setNextIf(elseIfNode);
					currentIfNode = elseIfNode;
				}
				else {
					BlockNode elseBlock = ParseBlock();
					currentIfNode.setNextIf(new IfNode(Optional.empty(), elseBlock));
					break;
				}
			}
			return Optional.of(ifNode);
			//}
			//return Optional.empty();
		}
		
		// Returns ForNode or ForEachNode for for loops and for each loops
		public Optional<StatementNode> ParseFor() throws Exception {
			if (tokenHandler.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS).isPresent()) {
				Optional<Node> initialize = ParseOperation();
				if (initialize.isPresent()) {
					if (tokenHandler.MatchAndRemove(Token.TokenType.IN).isPresent()) {
						// For each loop
						Optional<Node> iterable = ParseOperation();
						if (tokenHandler.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS).isPresent()) {
							BlockNode forEachBlock = ParseBlock();
							return Optional.of(new ForEachNode(initialize.get(), iterable.get(), forEachBlock));
						}
						else {
							throw new Exception("Expected a closing parenthesis ')'");
						}
					}
					else {
						// Regular for loop
						if (tokenHandler.MatchAndRemove(Token.TokenType.SEPARATOR).isPresent()) {
							Optional<Node> condition = ParseOperation();
							if (tokenHandler.MatchAndRemove(Token.TokenType.SEPARATOR).isPresent()) {
								Optional<Node> update = ParseOperation();
								if (tokenHandler.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS).isPresent()) {
									BlockNode forBlock = ParseBlock();
									return Optional.of(new ForNode(initialize, condition, update, forBlock));
								}
								else {
									throw new Exception("Expected a closing parenthesis ')'");
								}
							}
							else {
								throw new Exception("Expected a semicolon ';' after condition");
							}
						}
						else {
							throw new Exception("Expected a semicolon ';' after initialization");
						}
					}
				}
				else {
					throw new Exception("Expected an expression");
				}
			}
			else {
				throw new Exception("Expected an opening parenthesis '('");
			}
		}
		
		// Returns DeleteNode for delete statement
		public Optional<StatementNode> ParseDelete() throws Exception {
//			Optional<Token> deleteToken = tokenHandler.MatchAndRemove(Token.TokenType.DELETE);
//			if (deleteToken.isPresent()) {
				Optional<Node> target = ParseLValue();
				if (target.isPresent()) {
					return Optional.of(new DeleteNode(target.get()));
				}
				return Optional.empty();
			//}
			//throw new Exception("Expected a valid delete exprrssion");
				
		}
		
		// Returns WhileNode for while statement
		public Optional<StatementNode> ParseWhile() throws Exception {
			//if (tokenHandler.MatchAndRemove(Token.TokenType.WHILE).isPresent()) {
				if (tokenHandler.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS).isPresent()) {
					Optional<Node> condition = ParseOperation();
					if (condition.isPresent()) {
						if (tokenHandler.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS).isPresent()) {
							BlockNode statements = ParseBlock();
							return Optional.of(new WhileNode(condition.get(), statements));
						}
						else {
							throw new RuntimeException("Expected a closing paranthesis ')'");
						}
					}
					else {
						throw new RuntimeException("Expected a conditon for the while loop");
					}
				}
				else {
					throw new RuntimeException("Expected an opening paranthesis '('");
				}
			//}
			//return Optional.empty();
		}
		
		// Returns DoWhileNode for do while statement
		public Optional<StatementNode> ParseDoWhile() throws Exception {
			if (tokenHandler.Peek(0).get().getType() == Token.TokenType.DO) {
				BlockNode statements = ParseBlock();
				if (tokenHandler.MatchAndRemove(Token.TokenType.WHILE).isPresent()) {
					if (tokenHandler.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS).isPresent()) {
						Optional<Node> condition = ParseOperation();
						if (condition.isPresent()) {
							if (tokenHandler.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS).isPresent()) {
								if (tokenHandler.MatchAndRemove(Token.TokenType.SEPARATOR).isPresent()) {
									return Optional.of(new DoWhileNode(condition.get(), statements));
								}
								else {
									throw new RuntimeException("Expected a semicolon ';'");
								}
							}
							else {
								throw new RuntimeException("Expected a closing paranthesis ')'");
							}
						}
						else {
							throw new RuntimeException("Expected a conditon for the while loop");
						}
					}
					else {
						throw new RuntimeException("Expected an opening paranthesis '('");
					}
				}
				else {
					throw new RuntimeException("Expected the keyword while");
				}
			}
			return Optional.empty();
		}
		
		// Return ReturnNode for return statement
		public Optional<StatementNode> ParseReturn() throws Exception {
			//if (tokenHandler.MatchAndRemove(Token.TokenType.RETURN).isPresent()) {
				Optional<Node> returnValue = ParseOperation();
				return Optional.of(new ReturnNode(returnValue.orElse(null)));
			//}
			//throw new Exception("Expected a valid return statement");
		}
		
		// Return FunctionCallNode for function calls
		public Optional<Node> ParseFunctionCall(Optional<Node> word) throws Exception {
			if (tokenHandler.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS).isPresent()) {
				LinkedList<Node> parameters = new LinkedList<>();
				while (true) {
					Optional<Node> parameter = ParseOperation();
					if (parameter.isPresent()) {
						parameters.add(parameter.get());
					}
					else {
						break;
					}
					if (tokenHandler.MatchAndRemove(Token.TokenType.COMMA).isEmpty()) {
						break;
					}
				}
				if (tokenHandler.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS).isPresent()) {
					FunctionCallNode functionCallNode = new FunctionCallNode(word.get().toString(), parameters);
					return Optional.of(functionCallNode);
				}
				else {
					throw new Exception("Expected a closing parenthesis ')'");
				}
			}
			else {
				return word;
			}
		}
		
		public Optional<Node> ParseSpecialFunctionCall() throws Exception {
			if (tokenHandler.MatchAndRemove(Token.TokenType.PRINT).isPresent()) {
				return ParseFunctionCall(Optional.of(new VariableReferenceNode("print")));
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.PRINTF).isPresent()) {
				return ParseFunctionCall(Optional.of(new VariableReferenceNode("printf")));
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.GETLINE).isPresent()) {
				return ParseFunctionCall(Optional.of(new VariableReferenceNode("getline")));
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.NEXT).isPresent()) {
				return ParseFunctionCall(Optional.of(new VariableReferenceNode("next")));
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.NEXTFILE).isPresent()) {
				return ParseFunctionCall(Optional.of(new VariableReferenceNode("nextfile")));
			}
			if (tokenHandler.MatchAndRemove(Token.TokenType.EXIT).isPresent()) {
				return ParseFunctionCall(Optional.of(new VariableReferenceNode("exit")));
			}
			else {
				return Optional.empty();
			}
		}
		
		// While there are more tokens in the TokenHandler, Parse loops calling ParseFunction and ParseAction.
		// If neither method is true, we throw an exception
		public ProgramNode Parse() throws Exception {
			ProgramNode programNode = new ProgramNode();
			while (tokenHandler.MoreTokens()) {
				if (ParseFunction(programNode) || ParseAction(programNode)) {
					// Successfully parsed a function or action, continue parsing
				}
				else {
					throw new RuntimeException("Unexpected token encountered");
				}
			}
			return programNode;
		}		
}
