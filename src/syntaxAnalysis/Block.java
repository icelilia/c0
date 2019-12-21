package syntaxAnalysis;

import java.util.HashMap;

class Block {
	int no;
	int fatherNo;
	HashMap<String, Integer> constTable = new HashMap<String, Integer>();
	HashMap<String, Integer> varTable = new HashMap<String, Integer>();

	Block(int no, int fatherNo) {
		this.no = no;
		this.fatherNo = fatherNo;
	}

	boolean containsKey(String name) {
		return constTable.containsKey(name) || varTable.containsKey(name);
	}

	void put(int kind, String name, int offset) {
		if (kind == 0) {
			constTable.put(name, offset);
		} else if (kind == 1) {
			varTable.put(name, offset);
		}
	}

	Integer getOffset(String name) {
		Integer offset = constTable.get(name);
		if (offset != null) {
			return offset;
		}
		offset = varTable.get(name);
		if (offset != null) {
			return offset;
		}
		return null;
	}
}
