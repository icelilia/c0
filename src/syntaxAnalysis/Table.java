package syntaxAnalysis;

import java.util.ArrayList;

class Table {
	private ArrayList<Block> blockList = new ArrayList<Block>();

	Table() {
	}

	void addBlock(Block block) {
		this.blockList.add(block);
	}

	Block getBlock(int no) {
		return blockList.get(no);
	}

	// no: level
	Offset getOffset(String name, int no) {
		// levell = 0
		if (no == 0) {
			Block block = blockList.get(no);
			return new Offset(block.getOffset(name), no);
		} else {
			// self block
			Block block = blockList.get(no);
			Integer offset = block.getOffset(name);
			if (offset != null) {
				return new Offset(offset, no);
			}
			// father block
			else {
				return getOffset(name, block.fatherNo);
			}
		}
	}

	// kind
	// 0: const
	// 1: var
	// -1: uninit var
	Integer getKind(String name, int no) {
		if (no == 0) {
			Block block = blockList.get(no);
			return block.getKind(name);
		} else {
			// self block
			Block block = blockList.get(no);
			Integer kind = block.getKind(name);
			if (kind != null) {
				return kind;
			}
			// father block
			else {
				return getKind(name, block.fatherNo);
			}
		}
	}
}
