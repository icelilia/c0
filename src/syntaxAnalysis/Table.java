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

	Offset getOffset(String name, int no) {
		// 已经到全局数据了
		if (no == 0) {
			Block block = blockList.get(no);
			return new Offset(block.getOffset(name), no);
		} else {
			// 先在本Block中查找
			Block block = blockList.get(no);
			Integer offset = block.getOffset(name);
			if (offset != null) {
				return new Offset(offset, no);
			}
			// 找不到，在父Block中查找
			else {
				getOffset(name, block.fatherNo);
			}
		}
		return null;
	}

}
