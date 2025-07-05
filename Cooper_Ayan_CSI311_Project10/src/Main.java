import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the AWK txt file: ");
		Path path = Paths.get(keyboard.nextLine()); // Read the user's input as a file path
		String doc = new String(Files.readAllBytes(path)); // Read the content of the specified file
		
		System.out.println("Enter the text file: ");
		String textFile = keyboard.nextLine();
		
		Lexer lexer = new Lexer(doc);
		lexer.Lex();
		Parser parse = new Parser(lexer.tokens);
		ProgramNode program = parse.Parse();
		//System.out.println(program);
		Interpreter interpret = new Interpreter(program, Paths.get(textFile));
		interpret.InterpretProgram(program);
		
	}
}
