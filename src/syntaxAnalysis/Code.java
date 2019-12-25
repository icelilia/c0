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

	public byte[] toHexSeq() {
		int op;
		int on1;
		int on2;
		byte on1res[];
		byte on2res[];
		byte res[] = null;
		if (opcode.contentEquals("bipush")) {
			op = 0x01;
			on1 = Integer.parseInt(operands1);

			res = new byte[2];
			res[0] = byteToBytes(op)[0];
			res[1] = (byte) on1;
		} else if (opcode.contentEquals("ipush")) {
			op = 0x02;
			on1 = Integer.parseInt(operands1);
			on1res = intToBytes(on1);

			res = new byte[5];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
			res[3] = on1res[2];
			res[4] = on1res[3];
		} else if (opcode.contentEquals("loadc")) {
			op = 0x09;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);

			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("loada")) {
			op = 0x0a;
			on1 = Integer.parseInt(operands1);
			on2 = Integer.parseInt(operands2);
			on1res = shortToBytes(on1);
			on2res = intToBytes(on2);

			res = new byte[7];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
			res[3] = on2res[0];
			res[4] = on2res[1];
			res[5] = on2res[2];
			res[6] = on2res[3];
		} else if (opcode.contentEquals("iload")) {
			op = 0x10;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("istore")) {
			op = 0x20;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("iadd")) {
			op = 0x30;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("isub")) {
			op = 0x34;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("imul")) {
			op = 0x38;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("idiv")) {
			op = 0x3c;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("jmp")) {
			op = 0x70;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("je")) {
			op = 0x71;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("jne")) {
			op = 0x72;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("jl")) {
			op = 0x73;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("jge")) {
			op = 0x74;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("jg")) {
			op = 0x75;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("jle")) {
			op = 0x76;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("call")) {
			op = 0x80;
			on1 = Integer.parseInt(operands1);
			on1res = shortToBytes(on1);
			res = new byte[3];
			res[0] = byteToBytes(op)[0];
			res[1] = on1res[0];
			res[2] = on1res[1];
		} else if (opcode.contentEquals("ret")) {
			op = 0x88;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("iret")) {
			op = 0x89;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("iprint")) {
			op = 0xa0;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("cprint")) {
			op = 0xa2;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("printl")) {
			op = 0xaf;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		} else if (opcode.contentEquals("iscan")) {
			op = 0xb0;
			res = new byte[1];
			res[0] = byteToBytes(op)[0];
		}
		return res;
	}

	private byte[] intToBytes(int on) {
		byte[] res = new byte[4];
		for (int i = res.length - 1; i >= 0; i--) {
			res[i] = (byte) (on % 0xFF);
			on = on / 0xFF;
		}
		return res;
	}

	private byte[] shortToBytes(int on) {
		byte[] res = new byte[2];
		for (int i = res.length - 1; i >= 0; i--) {
			res[i] = (byte) (on % 0xFF);
			on = on / 0xFF;
		}
		return res;
	}

	private byte[] byteToBytes(int on) {
		byte[] res = new byte[1];
		for (int i = res.length - 1; i >= 0; i--) {
			res[i] = (byte) (on % 0xFF);
			on = on / 0xFF;
		}
		return res;
	}
}
