
public class StringHandler {
	
	private String document; // Hold AWK file
	private int index; // Finger position
	
	public StringHandler(String document) {
		this.document = document;
		this.index = 0;
	}
	
	// Method that looks "i" characters ahead and returns that character; doesn't move the index
	public char Peek(int i) {
		if (index + i < document.length()) {
			return document.charAt(index + i);
		}
		return '\0';
	}
	
	// Method that returns a string of the next "i" characters but doesn't move the index
	public String PeekString(int i) {
		if (index + i < document.length()) {
			return document.substring(index, index + i);
		}
		return null;
	}
	
	// Method that returns the next character and moves the index
	public char GetChar() {
		if (index < document.length()) {
			char c = document.charAt(index);
			index++;
			return c;
		}
		return '\0';
	}
	
	// Method that moves the index ahead "i" positions
	public void Swallow(int i) {
		index += i;
	}
	
	// Method that returns true if we are at the end of the document
	public boolean IsDone() {
		return index >= document.length();
	}
	
	// Method that returns the rest of the document as a string
	public String Remainder() {
		return document.substring(index);
	}
}
