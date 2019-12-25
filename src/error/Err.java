package error;

public class Err {

	public static void error(ErrEnum err) {
		switch (err) {
		case CLI_PARA_ERR:
			System.out.println("error: cli para err.");
			System.exit(-2);
			break;
		case INPUT_FILE_ERR:
			System.out.println("error: input file err, maybe input file don't exist.");
			System.exit(-2);
			break;
		case OUTPUT_FILE_ERR:
			System.out.println("error: output file err, can't create output file.");
			System.exit(-2);
			break;
		case INPUT_ERR:
			System.out.println("error: input err, include invalid character.");
			System.exit(-2);
			break;
		case OUTPUT_ERR:
			System.out.println("error: output err, err writing file.");
			System.exit(-2);
			break;
		case HEX_INT_ERR:
			System.out.println("error: hex int err, need digit after \"0x\".");
			System.exit(-2);
			break;
		case INT_OF_ERR:
			System.out.println("error: int32_t overflow.");
			System.exit(-2);
			break;
		case COMMENT_ERR:
			System.out.println("error: incomplete comment.");
			System.exit(-2);
			break;
		case SP_ERR:
			System.out.println("error: special err, just be used in debug.");
			System.exit(-2);
			break;
		case EOF_ERR:
			System.out.println("error: EOF err, EOF in the wrong place.");
			System.exit(-2);
			break;
		case NEED_ID_ERR:
			System.out.println("error: need ID.");
			System.exit(-2);
			break;
		case US_TYPE_ERR:
			System.out.println("error: unsupported type.");
			System.exit(-2);
			break;
		case CONST_DECL_ERR:
			System.out.println("error: error when defining const.");
			System.exit(-2);
			break;
		case CONST_INIT_ERR:
			System.out.println("error: const must be explicitly initialized.");
			System.exit(-2);
			break;
		case CONST_AS_ERR:
			System.out.println("error: const can't assignment again.");
			System.exit(-2);
			break;
		case VAR_DECL_ERR:
			System.out.println("error: err when defining var.");
			System.exit(-2);
			break;
		case ID_REDECL_ERR:
			System.out.println("error: ID redefine err.");
			System.exit(-2);
			break;
		case SEM_ERR:
			System.out.println("error: need sem.");
			System.exit(-2);
			break;
		case EXP_ERR:
			System.out.println("error: expression err.");
			System.exit(-2);
			break;
		case ID_UNDECL_ERR:
			System.out.println("error: ID undefine.");
			System.exit(-2);
			break;
		case VAR_UNINIT_ERR:
			System.out.println("error: use uninit var.");
			System.exit(-2);
			break;
		case FUNC_REDECL_ERR:
			System.out.println("error: function redefine.");
			System.exit(-2);
			break;
		case LSB_ERR:
			System.out.println("error: need LSB.");
			System.exit(-2);
			break;
		case RSB_ERR:
			System.out.println("error: need RSB.");
			System.exit(-2);
			break;
		case FUNC_PARA_DECL_ERR:
			System.out.println("error: err when defining function para.");
			System.exit(-2);
			break;
		case LLB_ERR:
			System.out.println("error: need LLB.");
			System.exit(-2);
			break;
		case RLB_ERR:
			System.out.println("error: need RLB.");
			System.exit(-2);
			break;
		case FUNC_STATEMENT_ERR:
			System.out.println("error: function statement err.");
			System.exit(-2);
			break;
		case FUNC_PARA_ERR:
			System.out.println("error: para err when calling function.");
			System.exit(-2);
			break;
		case VOID_FUNC_CALL_ERR:
			System.out.println("error: use function call with void return value in expression");
			System.exit(-2);
			break;
		case NO_MAIN_ERR:
			System.out.println("error: need main function");
			System.exit(-2);
			break;
		default:
			System.out.println("error: other err");
			System.exit(-2);
			break;
		}
	}
}
