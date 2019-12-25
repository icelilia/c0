package syntaxAnalysis;

import lexicalAnalysis.TokenType;

public class Func {

	public String name;

	public TokenType resType;

	public Integer paraNum;

	public Text text;

	Func(String name, TokenType resType, Integer paraNum) {
		this.name = name;
		this.resType = resType;
		this.paraNum = paraNum;
	}

	public void addText(Text text) {
		this.text = text;
	}
}
