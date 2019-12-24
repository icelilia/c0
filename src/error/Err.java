package error;

public class Err {

	public static void error(ErrEnum err) {
		switch (err) {
		case CLI_PARA_ERR:
			System.out.println("错误：命令行参数错误");
			System.exit(-2);
			break;
		case INPUT_FILE_ERR:
			System.out.println("错误：输入文件不存在");
			System.exit(-2);
			break;
		case OUTPUT_FILE_ERR:
			System.out.println("错误：创建输出文件失败");
			System.exit(-2);
			break;
		case INPUT_ERR:
			System.out.println("错误：输入含有非法字符或者十六进制整型字面量格式错误");
			System.exit(-2);
			break;
		case OUTPUT_ERR:
			System.out.println("错误：文件写入出错");
			System.exit(-2);
			break;
		case INT_OF_ERR:
			System.out.println("错误：32位整型字面量溢出");
			System.exit(-2);
			break;
		case SP_ERR:
			System.out.println("错误：特殊错误，仅调试用");
			System.exit(-2);
			break;
		case EOF_ERR:
			System.out.println("错误：分析中途遇到EOF");
			System.exit(-2);
			break;
		case NEED_ID_ERR:
			System.out.println("错误：缺少标识符");
			System.exit(-2);
			break;
		case US_TYPE_ERR:
			System.out.println("错误：不支持的数据类型");
			System.exit(-2);
			break;
		case CONST_DECL_ERR:
			System.out.println("错误：常量声明时语法错误");
			System.exit(-2);
			break;
		case CONST_INIT_ERR:
			System.out.println("错误：常量未被显式地初始化");
			System.exit(-2);
			break;
		case CONST_AS_ERR:
			System.out.println("错误：常量无法被再次赋值");
			System.exit(-2);
			break;
		case VAR_DECL_ERR:
			System.out.println("错误：变量声明时语法错误");
			System.exit(-2);
			break;
		case ID_REDECL_ERR:
			System.out.println("错误：变量名重定义");
			System.exit(-2);
			break;
		case SEM_ERR:
			System.out.println("错误：缺少分号");
			System.exit(-2);
			break;
		case EXP_ERR:
			System.out.println("错误：表达式中存在错误");
			System.exit(-2);
			break;
		case ID_UNDECL_ERR:
			System.out.println("错误：标识符未定义");
			System.exit(-2);
			break;
		case VAR_UNINIT_ERR:
			System.out.println("错误：使用了未初始化的变量");
			System.exit(-2);
			break;
		case FUNC_REDECL_ERR:
			System.out.println("错误：函数重定义");
			System.exit(-2);
			break;
		case LSB_ERR:
			System.out.println("错误：缺少左-小括号");
			System.exit(-2);
			break;
		case RSB_ERR:
			System.out.println("错误：缺少右-小括号");
			System.exit(-2);
			break;
		case FUNC_PARA_DECL_ERR:
			System.out.println("错误：函数参数声明时语法错误");
			System.exit(-2);
			break;
		case LLB_ERR:
			System.out.println("错误：缺少左-大括号");
			System.exit(-2);
			break;
		case RLB_ERR:
			System.out.println("错误：缺少右-大括号");
			System.exit(-2);
			break;
		case FUNC_STATMENT_ERR:
			System.out.println("错误：函数语句语法错误");
			System.exit(-2);
			break;
		case FUNC_PARA_ERR:
			System.out.println("错误：调用函数时参数个数错误");
			System.exit(-2);
			break;
		case VOID_FUNC_CALL_ERR:
			System.out.println("错误：表达式中调用返回值为空的函数");
			System.exit(-2);
			break;
		case NO_MAIN_ERR:
			System.out.println("错误：没有main函数");
			System.exit(-2);
			break;
		default:
			System.out.println("错误：未定义的错误");
			System.exit(-2);
			break;
		}
	}
}
