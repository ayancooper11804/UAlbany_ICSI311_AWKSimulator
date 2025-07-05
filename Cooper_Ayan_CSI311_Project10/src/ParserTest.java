import static org.junit.Assert.*;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {
	
	@Test
	public void testAcceptSeparators() {
		var lex = new Lexer(";;;\n\n nonseparator");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		Assert.assertEquals(true, parse.AcceptSeparators());
		Assert.assertEquals(false, parse.AcceptSeparators());
	}
	
	@Test
	public void testMatchAndRemove() {
		var lex = new Lexer("remove these 4 tokens");
		lex.Lex();
		var tokenHandler = new TokenHandler(lex.tokens);
		Assert.assertEquals(tokenHandler.MatchAndRemove(Token.TokenType.WORD).get().toString(), "WORD(remove)");
		Assert.assertEquals(tokenHandler.MatchAndRemove(Token.TokenType.WORD).get().toString(), "WORD(these)");
		Assert.assertEquals(tokenHandler.MatchAndRemove(Token.TokenType.NUMBER).get().toString(), "NUMBER(4)");
		Assert.assertEquals(tokenHandler.MatchAndRemove(Token.TokenType.WORD).get().toString(), "WORD(tokens)");
	}
	
	@Test
	public void testMoreTokens() {
		var lex = new Lexer("woah look at all these tokens");
		lex.Lex();
		var tokenHandler = new TokenHandler(lex.tokens);
		Assert.assertEquals(true, tokenHandler.MoreTokens());
		tokenHandler.MatchAndRemove(Token.TokenType.WORD);
		tokenHandler.MatchAndRemove(Token.TokenType.WORD);
		tokenHandler.MatchAndRemove(Token.TokenType.WORD);
		tokenHandler.MatchAndRemove(Token.TokenType.WORD);
		tokenHandler.MatchAndRemove(Token.TokenType.WORD);
		tokenHandler.MatchAndRemove(Token.TokenType.WORD);
		Assert.assertEquals(false, tokenHandler.MoreTokens());
	}
	
	@Test
	public void testPeek() {
		var lex = new Lexer("peek");
		lex.Lex();
		var tokenHandler = new TokenHandler(lex.tokens);
		Assert.assertTrue(tokenHandler.Peek(0).isPresent());
	}
	
	@Test
	public void testParseFunction() throws Exception {
		var lex = new Lexer("function isName(arg1, arg2)");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		Assert.assertEquals(true, parse.ParseFunction(new ProgramNode()));
		
		var lex2 = new Lexer("isName(arg1, arg2)");
		lex2.Lex();
		var parse2 = new Parser(lex2.tokens);
		Assert.assertEquals(false, parse2.ParseFunction(new ProgramNode()));
		
		var lex3 = new Lexer("function (arg1)");
		lex3.Lex();
		var parse3 = new Parser(lex3.tokens);
		Assert.assertThrows(RuntimeException.class, () -> parse3.ParseFunction(new ProgramNode()));
	}
	
	@Test
	public void testParseAction() throws Exception {
		var lex1 = new Lexer("BEGIN");
		lex1.Lex();
		var parse1 = new Parser(lex1.tokens);
		Assert.assertEquals(true, parse1.ParseAction(new ProgramNode()));
		
		var lex2 = new Lexer("END");
		lex2.Lex();
		var parse2 = new Parser(lex2.tokens);
		Assert.assertEquals(true, parse2.ParseAction(new ProgramNode()));
		
//		var lex3 = new Lexer("continue");
//		lex3.Lex();
//		var parse3 = new Parser(lex3.tokens);
//		Assert.assertEquals(true, parse3.ParseAction(new ProgramNode()));
	}
	
	@Test
	public void testParseLValue() throws Exception {
		var lex1 = new Lexer("$variable");
		lex1.Lex();
		var parse1 = new Parser(lex1.tokens);
		var program1 = parse1.Parse();
		Assert.assertEquals(program1.toString(), "$variable ");
		
		var lex2 = new Lexer("word[3]");
		lex2.Lex();
		var parse2 = new Parser(lex2.tokens);
		var program2 = parse2.Parse();
		Assert.assertEquals(program2.toString(), "word[3] ");
		
		var lex3 = new Lexer("word");
		lex3.Lex();
		var parse3 = new Parser(lex3.tokens);
		var program3 = parse3.Parse();
		Assert.assertEquals(program3.toString(), "word ");
	}
	
	@Test
	public void testParseBottomLevel() throws Exception {
		var lex1 = new Lexer("\"Hello, world!\"");
		lex1.Lex();
		var parse1 = new Parser(lex1.tokens);
		var program1 = parse1.Parse();
		Assert.assertEquals(program1.toString(), "Hello, world! ");
		
		var lex2 = new Lexer("1844");
		lex2.Lex();
		var parse2 = new Parser(lex2.tokens);
		var program2 = parse2.Parse();
		Assert.assertEquals(program2.toString(), "1844 ");
		
		var lex3 = new Lexer("`/Pattern/`");
		lex3.Lex();
		var parse3 = new Parser(lex3.tokens);
		var program3 = parse3.Parse();
		Assert.assertEquals(program3.toString(), "`/Pattern/` ");
		
		var lex4 = new Lexer("-5");
		lex4.Lex();
		var parse4 = new Parser(lex4.tokens);
		var program4 = parse4.Parse();
		Assert.assertEquals(program4.toString(), "-5 ");
		
		var lex5 = new Lexer("+5");
		lex5.Lex();
		var parse5 = new Parser(lex5.tokens);
		var program5 = parse5.Parse();
		Assert.assertEquals(program5.toString(), "+5 ");
		
		var lex6 = new Lexer("++5");
		lex6.Lex();
		var parse6 = new Parser(lex6.tokens);
		var program6 = parse6.Parse();
		Assert.assertEquals(program6.toString(), "5++5 ");
		
		var lex7 = new Lexer("--5");
		lex7.Lex();
		var parse7 = new Parser(lex7.tokens);
		var program7 = parse7.Parse();
		Assert.assertEquals(program7.toString(), "5--5 ");
		
		var lex8 = new Lexer("!true");
		lex8.Lex();
		var parse8 = new Parser(lex8.tokens);
		var program8 = parse8.Parse();
		Assert.assertEquals(program8.toString(), "!true ");
		
		var lex9 = new Lexer("++a");
		lex9.Lex();
		var parse9 = new Parser(lex9.tokens);
		var program9 = parse9.Parse();
		Assert.assertEquals(program9.toString(), "a++a ");
		
		var lex10 = new Lexer("++$b");
		lex10.Lex();
		var parse10 = new Parser(lex10.tokens);
		var program10 = parse10.Parse();
		Assert.assertEquals(program10.toString(), "$b++$b ");
		
		var lex11 = new Lexer("`[abc]`");
		lex11.Lex();
		var parse11 = new Parser(lex11.tokens);
		var program11 = parse11.Parse();
		Assert.assertEquals(program11.toString(), "`[abc]` ");
		
		var lex12 = new Lexer("(5)");
		lex12.Lex();
		var parse12 = new Parser(lex12.tokens);
		var program12 = parse12.Parse();
		Assert.assertEquals(program12.toString(), "5 ");
	}
	
	@Test
	public void testParsePost() throws Exception {
		var lex = new Lexer("a++");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var post = parse.Parse();
		Assert.assertEquals(post.toString(), "aa++ ");
	}
	
	@Test
	public void testParseExpression() throws Exception {
		var lex = new Lexer("1+4/5");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var expr = parse.Parse();
		Assert.assertEquals(expr.toString(), "1+4/5 ");
	}
	
	@Test
	public void testParseTerm() throws Exception {
		var lex = new Lexer("8/4");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var term = parse.Parse();
		Assert.assertEquals(term.toString(), "8/4 ");
	}
	
//	@Test
//	public void testParseFactor() throws Exception {
//		var lex = new Lexer("(2*3)");
//		lex.Lex();
//		var parse = new Parser(lex.tokens);
//		var factor = parse.ParseFactor();
//		Assert.assertEquals(factor.get().toString(), "2*3");
//	}
	
	@Test
	public void testParseExponents() throws Exception {
		var lex = new Lexer("2^3");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var exponents = parse.Parse();
		Assert.assertEquals(exponents.toString(), "2^3 ");
	}
	
	@Test
	public void testParseConcatenation() throws Exception {
		var lex = new Lexer("Hello world");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var concatenation = parse.Parse();
		Assert.assertEquals(concatenation.toString(), "Hello world ");
	}
	
	@Test
	public void testParseBooleanCompare() throws Exception {
		var lex = new Lexer("a < b");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var compare = parse.Parse();
		Assert.assertEquals(compare.toString(), "a < b ");
	}
	
	@Test
	public void testParseMatch() throws Exception {
		var lex = new Lexer("a ~ b");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var match = parse.Parse();
		Assert.assertEquals(match.toString(), "a ~ b ");
	}
	
	@Test
	public void testParseArrayMembership() throws Exception {
		var lex = new Lexer("2 in array");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var array = parse.Parse();
		Assert.assertEquals(array.toString(), "2 in array ");
	}
	
	@Test
	public void testParseAND() throws Exception {
		var lex = new Lexer("a && b");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var AND = parse.Parse();
		Assert.assertEquals(AND.toString(), "a && b ");
	}
	
	@Test
	public void testParseOR() throws Exception {
		var lex = new Lexer("a || b");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var or = parse.Parse();
		Assert.assertEquals(or.toString(), "a || b ");
	}
	
	@Test
	public void testParseTernary() throws Exception {
		var lex = new Lexer("expr1 ? expr2 : expr3");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var ternary = parse.Parse();
		Assert.assertEquals(ternary.toString(), "expr1 ? expr2 : expr3 ");
	}
	
	@Test
	public void testParseAssignment() throws Exception {
		var lex = new Lexer("a += b");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var assignment = parse.Parse();
		//System.out.println(assignment);
		Assert.assertEquals(assignment.toString(), "a += a+b ");
	}
	
	@Test
	public void testContinue() throws Exception {
		var lex = new Lexer("continue");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var checkContinue = parse.Parse();
		Assert.assertEquals(checkContinue.toString(), "continue ");
	}
	//System.out.println(checkContinue);
	@Test
	public void testBreak() throws Exception {
		var lex = new Lexer("break");
		lex.Lex();
		var parse = new Parser(lex.tokens);
		var checkBreak = parse.Parse();
		Assert.assertEquals(checkBreak.toString(), "break ");
	}
//	
	@Test
	public void testParseIf() throws Exception {
		var lex1 = new Lexer("if (a < b) {\n}");
		lex1.Lex();
		var parse1 = new Parser(lex1.tokens);
		var if1 = parse1.Parse();
		Assert.assertEquals(if1.toString(), "if (a < b) {\n} ");
		
//		var lex2 = new Lexer("if (a < b) {\n}");
//		lex2.Lex();
//		var parse2 = new Parser(lex2.tokens);
//		var if2 = parse2.Parse();
//		Assert.assertEquals(if2.toString(), "if (a < b) {\n} ");
	}
//	
	@Test
	public void testParseFor() throws Exception {
		var lex1 = new Lexer("for (a; b; c) {\n}");
		lex1.Lex();
		var parse1 = new Parser(lex1.tokens);
		var for1 = parse1.Parse();
		Assert.assertEquals(for1.toString(), "for (a; b; c) {\n} ");
		
//		var lex2 = new Lexer("for (a in array)");
//		lex2.Lex();
//		var parse2 = new Parser(lex2.tokens);
//		var for2 = parse2.Parse();
//		Assert.assertEquals(for2.toString(), "for (a in array) {\n} ");
	}
	
	@Test
	public void testParseDelete() throws Exception {
		var lex1 = new Lexer("delete array[index]");
		lex1.Lex();
		var parse1 = new Parser(lex1.tokens);
		var delete1 = parse1.Parse();
		Assert.assertEquals(delete1.toString(), "delete array[index] ");
	}
	
	@Test
	public void testParseWhile() throws Exception {
		var lex1 = new Lexer("while (true) {\n}");
		lex1.Lex();
		var parse1 = new Parser(lex1.tokens);
		var while1 = parse1.Parse();
		Assert.assertEquals(while1.toString(), "while (true) {\n} ");
	}
	
//	@Test
//	public void testParseDoWhile() throws Exception {
//		var lex1 = new Lexer("do {\n} while (true);");
//		lex1.Lex();
//		var parse1 = new Parser(lex1.tokens);
//		var doWhile1 = parse1.Parse();
//		Assert.assertEquals(doWhile1.toString(), "do {\n} while (true); ");
//	}
	
//	@Test
//	public void testReturn() throws Exception {
//		var lex1 = new Lexer("return ");
//		lex1.Lex();
//		var parse1 = new Parser(lex1.tokens);
//		var checkReturn1 = parse1.Parse();
//		Assert.assertEquals(checkReturn1.toString(), "return ");
//		
//		var lex2 = new Lexer("return a ");
//		lex2.Lex();
//		var parse2 = new Parser(lex2.tokens);
//		var checkReturn2 = parse2.Parse();
//		Assert.assertEquals(checkReturn2.toString(), "return a ");
//	}
	@Test
	public void testParseFunctionCall() throws Exception{
		Lexer lexer = new Lexer("functionCall(a, b, c) ");
		lexer.Lex();
		Parser parser = new Parser(lexer.tokens);
		//System.out.println(lexer.tokens);
		ProgramNode program = parser.Parse();
		//Assert.assertTrue(program.getOtherBlocks().get(0).getStatements().get(0) instanceof FunctionCallNode);
		Assert.assertEquals(program.toString(), "functionCall(a, b, c)  ");
	}
	
	@Test
	public void testParseOtherFunctionCall() throws Exception{
		Lexer lexer = new Lexer("getline(a, b, c) ");
		lexer.Lex();
		Parser parser = new Parser(lexer.tokens);
		//System.out.println(lexer.tokens);
		ProgramNode program = parser.Parse();
		//Assert.assertTrue(program.getOtherBlocks().get(0).getStatements().get(0) instanceof FunctionCallNode);
		Assert.assertEquals(program.toString(), "getline(a, b, c)  " );
	}
//	@Test
//	public void testParseFunctionCall() throws Exception {
//		var lex1 = new Lexer("myfunc(a);");
//		lex1.Lex();
//		var parse1 = new Parser(lex1.tokens);
//		var call1 = parse1.Parse();
//		Assert.assertEquals(call1.toString(), "myfunc(a); " );
		
//		var lex2 = new Lexer("func(a, b, c);");
//		lex2.Lex();
//		var parse2 = new Parser(lex2.tokens);
//		var call2 = parse2.ParseFunctionCall();
//		Assert.assertEquals(call2.get().toString(), "func(a, b, c);");
//		
//		var lex3 = new Lexer("getline");
//		lex3.Lex();
//		var parse3 = new Parser(lex3.tokens);
//		var call3 = parse3.ParseFunctionCall();
//		Assert.assertEquals(call3.get().toString(), "getline");
//		
//		var lex4 = new Lexer("print");
//		lex4.Lex();
//		var parse4 = new Parser(lex4.tokens);
//		var call4 = parse4.ParseFunctionCall();
//		Assert.assertEquals(call4.get().toString(), "print");
//		
//		var lex5 = new Lexer("printf");
//		lex5.Lex();
//		var parse5 = new Parser(lex5.tokens);
//		var call5 = parse5.ParseFunctionCall();
//		Assert.assertEquals(call5.get().toString(), "printf");
//		
//		var lex6 = new Lexer("exit");
//		lex6.Lex();
//		var parse6 = new Parser(lex6.tokens);
//		var call6 = parse6.ParseFunctionCall();
//		Assert.assertEquals(call6.get().toString(), "exit");
//		
//		var lex7 = new Lexer("nextfile");
//		lex7.Lex();
//		var parse7 = new Parser(lex7.tokens);
//		var call7 = parse7.ParseFunctionCall();
//		Assert.assertEquals(call7.get().toString(), "nextfile");
//		
//		var lex8 = new Lexer("next");
//		lex8.Lex();
//		var parse8 = new Parser(lex8.tokens);
//		var call8 = parse8.ParseFunctionCall();
//		Assert.assertEquals(call8.get().toString(), "next");
//	}
}
