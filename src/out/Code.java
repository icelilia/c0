package out;

public class Code {
	public int index;
	public String opcode;
	public String operands1;
	public String operands2;

	public Code(int index, String opcode, String operands1, String operands2) {
		this.index = index;
		this.opcode = opcode;
		this.operands1 = operands1;
		this.operands2 = operands2;
	}
}
