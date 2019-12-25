package syntaxAnalysis;

import java.util.ArrayList;

public class FuncTable {
	private ArrayList<Func> funcList = new ArrayList<Func>();

	FuncTable() {
	}

	void addFunc(Func func) {
		funcList.add(func);
	}

	boolean containsFunc(String name) {
		for (Func func : funcList) {
			if (func.name.contentEquals(name)) {
				return true;
			}
		}
		return false;
	}

	Integer getIndex(String name) {
		int i;
		for (i = 0; i < funcList.size(); i++) {
			if (funcList.get(i).name.contentEquals(name)) {
				return i;
			}
		}
		return null;
	}

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
