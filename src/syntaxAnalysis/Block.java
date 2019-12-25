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

	boolean containsID(String name) {
		return constTable.containsKey(name) || initVarTable.containsKey(name) || unInitVarTable.containsKey(name);
	}

	// kind
	// 0: const
	// 1: var
	// -1: uninit var
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

	boolean isUnInit(String name) {
		return unInitVarTable.containsKey(name);
	}

	boolean isConst(String name) {
		return constTable.containsKey(name);
	}

	// kind
	// 0: const
	// 1: var
	// -1: uninit var
	void put(int kind, String name, Integer offset) {
		if (kind == 0) {
			constTable.put(name, offset);
		} else if (kind == 1) {
			initVarTable.put(name, offset);
		} else if (kind == -1) {
			unInitVarTable.put(name, offset);
		}
	}

	// uninit var to var
	void change(String name) {
		Integer offset = unInitVarTable.get(name);
		unInitVarTable.remove(name);
		initVarTable.put(name, offset);
	}

	Integer getOffset(String name) {
		Integer offset = constTable.get(name);
		if (offset != null) {
			return offset;
		}
		offset = initVarTable.get(name);
		if (offset != null) {
			return offset;
		}
		offset = unInitVarTable.get(name);
		if (offset != null) {
			return offset;
		}
		return null;
	}
}
