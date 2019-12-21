package syntaxAnalysis;

import java.util.HashMap;

class FuncTable {
	private HashMap<String, Func> funcTable = new HashMap<String, Func>();

	FuncTable() {
	}

	// 返回是否有该函数
	boolean containsKey(String name) {
		return funcTable.containsKey(name);
	}

	// 设置函数的值
	void put(String name, Func value) {
		funcTable.put(name, value);
	}

	// 存在此函数则返回表的引用
	// 不存在则返回null
	Func get(String name) {
		return funcTable.get(name);
	}
}
