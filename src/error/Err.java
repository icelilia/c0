package error;

public class Err {

	public static void error(ErrEnum err) {
		switch (err) {
		case PARA_ERR:
			System.out.println("参数错误");
			System.exit(-1);
			break;
		case INPUT_FILE_ERR:
			System.out.println("输入文件不存在");
			System.exit(-1);
			break;
		case OUTPUT_FILE_ERR:
			System.out.println("无法创建输出文件");
			System.exit(-1);
			break;
		case INPUT_ERR:
			System.out.println("输入含有非法字符或者十六进制整型字面量格式错误");
			System.exit(-1);
			break;
		case OUTPUT_ERR:
			System.out.println("输出错误，文件写入出错");
			System.exit(-1);
			break;
		case INT_OF_ERR:
			System.out.println("32位整型字面量溢出");
			System.exit(-1);
			break;
		case SP_ERR:
			System.out.println("特殊错误，仅调试用");
			System.exit(-1);
			break;
		case EOF_ERR:
			System.out.println("分析中途遇到EOF");
			System.exit(-1);
			break;
		case ID_ERR:
			System.out.println("ID错误，缺少标识符");
			System.exit(-1);
			break;
		case UK_TYPE_ERR:
			System.out.println("不支持的数据类型");
			System.exit(-1);
			break;
		case CONST_DECL_ERR:
			System.out.println("常量声明时语法错误");
			System.exit(-1);
			break;
		case CONST_INIT_ERR:
			System.out.println("常量未被显式地初始化");
			System.exit(-1);
			break;
		case VAR_DECL_ERR:
			System.out.println("变量声明时语法错误");
			System.exit(-1);
			break;
		case ID_REDECL_ERR:
			System.out.println("标识符重定义");
			System.exit(-1);
			break;
		case SEM_ERR:
			System.out.println("缺少分号");
			System.exit(-1);
			break;
		case EXP_ERR:
			System.out.println("表达式中存在错误");
			System.exit(-1);
			break;
		case ID_UNDECL_ERR:
			System.out.println("标识符未定义");
			System.exit(-1);
			break;
		case VAR_UNINIT_ERR:
			System.out.println("使用了未初始化的变量");
			System.exit(-1);
			break;
		case FUNC_REDECL_ERR:
			System.out.println("函数重定义");
			System.exit(-1);
			break;
		case LSB_ERR:
			System.out.println("缺少左-小括号");
			System.exit(-1);
			break;
		case RSB_ERR:
			System.out.println("缺少右-小括号");
			System.exit(-1);
			break;
		case FUNC_PARA_DECL_ERR:
			System.out.println("函数参数声明时语法错误");
			System.exit(-1);
			break;
		case LLB_ERR:
			System.out.println("缺少左-大括号");
			System.exit(-1);
			break;
		case RLB_ERR:
			System.out.println("缺少右-大括号");
			System.exit(-1);
			break;
		case FUNC_STATMENT_ERR:
			System.out.println("函数语句语法错误");
			System.exit(-1);
			break;
		default:
			System.out.println("未定义的错误");
			System.exit(-1);
			break;
		}
	}
}
