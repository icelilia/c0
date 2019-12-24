package syntaxAnalysis;

import java.util.ArrayList;

public class Text {
	private String name;
	private int index = 0;
	private ArrayList<Code> codeList = new ArrayList<Code>();

	// name即为函数名
	Text(String name) {
		this.name = name;
	}

	// 添加指令
	void addCode(String opcode, String operands1, String operands2) {
		Code code = new Code(index++, opcode, operands1, operands2);
		codeList.add(code);
	}

	// 关于回填
	// 得到顶层指令的index
	int getIndex() {
		//
		if (codeList.size() == 0) {
			return -1;
		}
		return codeList.get(codeList.size() - 1).index;
	}

	// 根据index来更改指令
	void reWrite(int index, String opcode, String operands1, String operands2) {
		Code code = codeList.get(index);
		if (!opcode.contentEquals("")) {
			code.opcode = opcode;
		}
		if (!operands1.contentEquals("")) {
			code.operands1 = operands1;
		}
		if (!operands2.contentEquals("")) {
			code.operands2 = operands2;
		}
	}

	// 得到名字
	public String getName() {
		return this.name;
	}

	// 返回指令
	public ArrayList<Code> getCodesList() {
		return codeList;
	}
}
