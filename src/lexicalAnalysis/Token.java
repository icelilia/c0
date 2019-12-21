package lexicalAnalysis;

public class Token {
	private TokenType type;
	private String value;

	public Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString() {
		return type + "\t" + value + "\n";
	}

	public TokenType getType() {
		return this.type;
	}

	public String getValue() {
		return this.value;
	}
}
