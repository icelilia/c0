package syntaxAnalysis;

public class Code {
	public Integer index;
	public String opcode;
	public String operands1;
	public String operands2;

	public Code(Integer index, String opcode, String operands1, String operands2) {
		this.index = index;
		this.opcode = opcode;
		this.operands1 = operands1;
		this.operands2 = operands2;
	}

	@Override
	public String toString() {
		if (operands1.contentEquals("")) {
			return " " + index.toString() + " " + opcode + "\n";
		}
		if (operands2.contentEquals("")) {
			return " " + index.toString() + " " + opcode + " " + operands1 + "\n";
		}
		return " " + index.toString() + " " + opcode + " " + operands1 + " , " + operands2 + "\n";
	}
}
