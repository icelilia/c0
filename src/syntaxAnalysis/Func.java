package syntaxAnalysis;

import java.util.ArrayList;

import lexicalAnalysis.TokenType;
import out.Text;

//仅仅是为了用一个value来储存两张表的引用而设置的集合
class Func {

	// 函数返回类型
	TokenType resType;
	// 函数参数列表
	// 判断参数列表相同时用list1.containsAll(list2) && list2.containsAll(list1)
	ArrayList<TokenType> paraList = new ArrayList<TokenType>();
	// 函数语句文本
	Text funcText;

	Func(TokenType resType, ArrayList<TokenType> paraList, Text funcText) {
		this.resType = resType;
		this.paraList = paraList;
		this.funcText = funcText;
	}
}
