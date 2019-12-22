package syntaxAnalysis;

import java.util.HashMap;

class Block {
	int no;
	int fatherNo;
	private HashMap<String, Integer> constTable = new HashMap<String, Integer>();
	private HashMap<String, Integer> initVarTable = new HashMap<String, Integer>();
	private HashMap<String, Integer> unInitVarTable = new HashMap<String, Integer>();

	Block(int no, int fatherNo) {
		this.no = no;
		this.fatherNo = fatherNo;
	}

	// 返回是否有这个标识符
	boolean containsKey(String name) {
		return constTable.containsKey(name) || initVarTable.containsKey(name) || unInitVarTable.containsKey(name);
	}

	// 返回标识符类型
	Integer getKind(String name) {
		if (constTable.containsKey(name)) {
			return 0;
		} else if (initVarTable.containsKey(name)) {
			return 1;
		} else if (unInitVarTable.containsKey(name)) {
			return -1;
		}
		return null;
	}

	// 判断是否已初始化
	boolean isUnInit(String name) {
		return unInitVarTable.containsKey(name);
	}

	// 判断是否是常量
	boolean isConst(String name) {
		return constTable.containsKey(name);
	}

	// kind种类为两种
	// 0表示常量
	// 1表示变量
	// -1表示为初始化的变量
	void put(int kind, String name, Integer offset) {
		if (kind == 0) {
			constTable.put(name, offset);
		} else if (kind == 1) {
			initVarTable.put(name, offset);
		} else if (kind == -1) {
			unInitVarTable.put(name, offset);
		}
	}

	// 将未初始化变量转为初始化变量
	void change(String name) {
		Integer offset = unInitVarTable.get(name);
		unInitVarTable.remove(name);
		initVarTable.put(name, offset);
	}

	Integer getOffset(String name) {
		// 常量表
		Integer offset = constTable.get(name);
		if (offset != null) {
			return offset;
		}
		// 变量表
		offset = initVarTable.get(name);
		if (offset != null) {
			return offset;
		}
		// 未初始化变量表
		offset = unInitVarTable.get(name);
		if (offset != null) {
			return offset;
		}
		// 都为空就返回null
		return null;
	}

}
