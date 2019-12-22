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
		return "\t" + index.toString() + "\t" + opcode + "\t" + operands1 + "\t" + operands2 + "\n";
	}
}
