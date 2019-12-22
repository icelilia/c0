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

	// 这里由于涉及到层级问题
	// 函数调函数，函数调全局，全局调全局
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
				return getOffset(name, block.fatherNo);
			}
		}
	}

	// 返回该标识符类型
	// 0：常量
	// 1：已初始化变量
	// -1：未初始化变量
	Integer getKind(String name, int no) {
		if (no == 0) {
			Block block = blockList.get(no);
			return block.getKind(name);
		} else {
			// 先在本Block中查找
			Block block = blockList.get(no);
			Integer kind = block.getKind(name);
			if (kind != null) {
				return kind;
			}
			// 找不到，在父Block中查找
			else {
				return getKind(name, block.fatherNo);
			}
		}
	}
}
