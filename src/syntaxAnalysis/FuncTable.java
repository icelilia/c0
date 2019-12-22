package syntaxAnalysis;

import java.util.ArrayList;

public class FuncTable {
	private ArrayList<Func> funcList = new ArrayList<Func>();

	FuncTable() {
	}

	// 添加函数
	void addFunc(Func func) {
		funcList.add(func);
	}

	// 返回是否有该函数
	boolean containsFunc(String name) {
		for (Func func : funcList) {
			if (func.name.contentEquals(name)) {
				return true;
			}
		}
		return false;
	}

	// 返回函数的index
	Integer getIndex(String name) {
		int i;
		for (i = 0; i < funcList.size(); i++) {
			if (funcList.get(i).name.contentEquals(name)) {
				return i;
			}
		}
		return null;
	}

	// 返回函数的引用
	Func getFunc(String name) {
		int i;
		for (i = 0; i < funcList.size(); i++) {
			if (funcList.get(i).name.contentEquals(name)) {
				return funcList.get(i);
			}
		}
		return null;
	}

	public ArrayList<Func> getFuncList() {
		return this.funcList;
	}

}
