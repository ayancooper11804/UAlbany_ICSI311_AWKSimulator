import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;


public class LexerTest {
	@Test
	public void testSingleLineWordsAndNumbers() {
		String input =  "Hello 18 world 2004";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(Hello) NUMBER(18) WORD(world) NUMBER(2004) ", lex.toString());	
	}
	
	@Test
	public void testMultiLineWordsAndNumbers() {
		String input = "Hello\n18\nworld\n2004";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(Hello) SEPARATOR NUMBER(18) SEPARATOR WORD(world) SEPARATOR NUMBER(2004) ", lex.toString());
	}
	
	@Test
	public void testWordsThenNumbers() {
		String input = "Ayan18 has 5 apples";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(Ayan18) WORD(has) NUMBER(5) WORD(apples) ", lex.toString());
	}
	
	@Test
	public void testNumbersThenWords() {
		String input = "Train 3A has arrived at platform 7";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(Train) NUMBER(3) WORD(A) WORD(has) WORD(arrived) WORD(at) WORD(platform) NUMBER(7) ", lex.toString());
		
	}
	
	@Test
	public void testDecimalNumbers() {
		String input = "40.3 is a number";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("NUMBER(40.3) WORD(is) WORD(a) WORD(number) ", lex.toString());
		
	}
	
	@Test
	public void testCarriageReturn() {
		String input = "Carriage return \r";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(Carriage) RETURN ", lex.toString());
	}
	
	@Test
	public void testKeywords() {
		String input = "while if do for break continue else return BEGIN END "
				+ "print printf next in delete getline exit nextfile function";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WHILE IF DO FOR BREAK CONTINUE ELSE RETURN BEGIN END PRINT PRINTF NEXT IN DELETE "
				+ "GETLINE EXIT NEXTFILE FUNCTION ", lex.toString());
	}
	
	@Test
	public void testComments() {
		String input = "#ignore me please";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("", lex.toString());
	}
	
	@Test
	public void testStringLiteral() {
		String input = "greeting = \"Hello\" ";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(greeting) EQUALS STRINGLITERAL(Hello) ", lex.toString());
	}
	
	@Test
	public void testEscapedStringLiteral() {
		String input = "Escapes: \"He said, \\\"Hello\\\"\" ";
		var lex = new Lexer(input);
		lex.Lex();
		String expected = "WORD(Escapes) COLON STRINGLITERAL(He said, \"Hello\") ";
		assertEquals(expected, lex.toString().replaceAll("\\\\\"", "\""));
		
	}
	
	@Test
	public void testRegularExpression() {
		String input = "pattern = `/regex/` ";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(pattern) EQUALS PATTERN(/regex/) ", lex.toString());
	}
	
	@Test
	public void testSemicolon() {
		String input = "int number;";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(int) WORD(number) SEPARATOR ", lex.toString());
	}
	
	@Test
	public void testMultiSymbolInput() {
		String input = "if (a < b) { };";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("IF LEFTPARENTHESIS WORD(a) LEFTARROW WORD(b) RIGHTPARENTHESIS LEFTCURLYBRACES RIGHTCURLYBRACES SEPARATOR ", lex.toString());
	}
	
	@Test
	public void testTwoCharacterSymbols() {
		String input = ">= ++ -- <= == != ^= %= *= /= += -= !~ && >> ||";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("GREATEREQUALTO INCREMENT DECREMENT LESSEQUALTO COMPARETO NOTEQUALTO EXPONENTASSIGNMENT MODULUSASSIGNMENT MULTIPLYASSIGNMENT DIVIDEASSIGNMENT ADDASSIGNMENT SUBTRACTASSIGNMENT NOTMATCH LOGICALAND RIGHTSHIFT LOGICALOR ", lex.toString());
	}
	
	@Test
	public void testOneCharacterSymbols() {
		String input = "{ } [ ] ( ) $ ~ = < > ! + ^ - ? : * / % ; \n | ,";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("LEFTCURLYBRACES RIGHTCURLYBRACES LEFTSQUAREBRACES RIGHTSQUAREBRACES LEFTPARENTHESIS RIGHTPARENTHESIS DOLLARSIGN MATCH EQUALS LEFTARROW RIGHTARROW EXCLAMATION ADD EXPONENT SUBTRACT QUESTION COLON MULTIPLY DIVIDE MODULUS SEPARATOR SEPARATOR OR COMMA ", lex.toString());
	}
	
	@Test
	public void testTransitions() {
		String input = "x = x + 1;";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("WORD(x) EQUALS WORD(x) ADD NUMBER(1) SEPARATOR ", lex.toString());
	}
	
	@Test
	public void testOneSymbol() {
		String input = ">=";
		var lex = new Lexer(input);
		lex.Lex();
		Assert.assertEquals("GREATEREQUALTO ", lex.toString());
	}
	
}
