package error;

public class Err {

	public static void error(ErrEnum err) {
		switch (err) {
		case CLI_PARA_ERR:
			System.out.println("error: cli para err");
			System.exit(-2);
			break;
		case INPUT_FILE_ERR:
			System.out.println("error: input file err");
			System.exit(-2);
			break;
		case OUTPUT_FILE_ERR:
			System.out.println("error: output file err");
			System.exit(-2);
			break;
		case INPUT_ERR:
			System.out.println("error: input err");
			System.exit(-2);
			break;
		case OUTPUT_ERR:
			System.out.println("error: output err");
			System.exit(-2);
			break;
		case INT_OF_ERR:
			System.out.println("error: int overflow");
			System.exit(-2);
			break;
		case SP_ERR:
			System.out.println("error: special err");
			System.exit(-2);
			break;
		case EOF_ERR:
			System.out.println("error: EOF err");
			System.exit(-2);
			break;
		case NEED_ID_ERR:
			System.out.println("error: need ID");
			System.exit(-2);
			break;
		case US_TYPE_ERR:
			System.out.println("error: unsupport type");
			System.exit(-2);
			break;
		case CONST_DECL_ERR:
			System.out.println("error: const declaration err");
			System.exit(-2);
			break;
		case CONST_INIT_ERR:
			System.out.println("error: const init err");
			System.exit(-2);
			break;
		case CONST_AS_ERR:
			System.out.println("error: const can't assignment again");
			System.exit(-2);
			break;
		case VAR_DECL_ERR:
			System.out.println("error: var declaration err");
			System.exit(-2);
			break;
		case ID_REDECL_ERR:
			System.out.println("error: ID redeclaration err");
			System.exit(-2);
			break;
		case SEM_ERR:
			System.out.println("error: need sem");
			System.exit(-2);
			break;
		case EXP_ERR:
			System.out.println("error: expression err");
			System.exit(-2);
			break;
		case ID_UNDECL_ERR:
			System.out.println("error: ID undeclaration err");
			System.exit(-2);
			break;
		case VAR_UNINIT_ERR:
			System.out.println("error: var uninit err");
			System.exit(-2);
			break;
		case FUNC_REDECL_ERR:
			System.out.println("error: function redeclaration err");
			System.exit(-2);
			break;
		case LSB_ERR:
			System.out.println("error: need LSB");
			System.exit(-2);
			break;
		case RSB_ERR:
			System.out.println("error: need RSB");
			System.exit(-2);
			break;
		case FUNC_PARA_DECL_ERR:
			System.out.println("error: function para decl err");
			System.exit(-2);
			break;
		case LLB_ERR:
			System.out.println("error: need LLB");
			System.exit(-2);
			break;
		case RLB_ERR:
			System.out.println("error: need RLB");
			System.exit(-2);
			break;
		case FUNC_STATMENT_ERR:
			System.out.println("error: function statment err");
			System.exit(-2);
			break;
		case FUNC_PARA_ERR:
			System.out.println("error: function call para err");
			System.exit(-2);
			break;
		case VOID_FUNC_CALL_ERR:
			System.out.println("error: void value in experssion");
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
