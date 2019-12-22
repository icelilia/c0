package syntaxAnalysis;

import lexicalAnalysis.TokenType;

//仅仅是为了用一个value来储存两张表的引用而设置的集合
public class Func {

	// 函数名
	public String name;
	// 函数返回类型
	public TokenType resType;
	// 函数参数个数
	public Integer paraNum;
	// 函数语句文本
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
