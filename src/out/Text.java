package out;

import java.util.ArrayList;

public class Text {
	private int index = 0;
	private ArrayList<Code> codeList = new ArrayList<Code>();

	public void addCode(String opcode, String operands1, String operands2) {
		Code code = new Code(index++, opcode, operands1, operands2);
		codeList.add(code);
	}

	public void addFunc(String name) {
		Code func = new Code(-1, name, name, name);
		codeList.add(func);
	}

	public ArrayList<Code> getTextList() {
		return codeList;
	}
}
