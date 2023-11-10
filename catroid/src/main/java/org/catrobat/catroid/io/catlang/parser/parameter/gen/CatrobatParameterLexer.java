// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/CatrobatParameterLexer.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CatrobatParameterLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, NUMBER=2, BRACE_OPEN=3, BRACE_CLOSE=4, COMMA=5, VARIABLE=6, UDB_PARAMETER=7, 
		LIST=8, STRING=9, OPERATOR_ADD=10, OPERATOR_NUMERIC_DIVIDE=11, OPERATOR_NUMERIC_MULTIPLY=12, 
		OPERATOR_LOGIC_AND=13, OPERATOR_LOGIC_OR=14, OPERATOR_LOGIC_NOT=15, OPERATOR_LOGIC_EQUAL=16, 
		OPERATOR_LOGIC_NOT_EQUAL=17, OPERATOR_LOGIC_LOWER=18, OPERATOR_LOGIC_GREATER=19, 
		OPERATOR_LOGIC_LOWER_EQUAL=20, OPERATOR_LOGIC_GREATER_EQUAL=21, FUNCTION_OR_SENSOR=22;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LETTER", "UPPERCASE", "LOWERCASE", "DIGIT", "WS", "NUMBER", "BRACE_OPEN", 
			"BRACE_CLOSE", "COMMA", "VARIABLE", "UDB_PARAMETER", "LIST", "STRING", 
			"OPERATOR_ADD", "OPERATOR_NUMERIC_DIVIDE", "OPERATOR_NUMERIC_MULTIPLY", 
			"OPERATOR_LOGIC_AND", "OPERATOR_LOGIC_OR", "OPERATOR_LOGIC_NOT", "OPERATOR_LOGIC_EQUAL", 
			"OPERATOR_LOGIC_NOT_EQUAL", "OPERATOR_LOGIC_LOWER", "OPERATOR_LOGIC_GREATER", 
			"OPERATOR_LOGIC_LOWER_EQUAL", "OPERATOR_LOGIC_GREATER_EQUAL", "FUNCTION_OR_SENSOR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'('", "')'", "','", null, null, null, null, null, 
			null, "'\\u00D7'", "'&&'", "'||'", "'!'", "'='", null, "'<'", "'>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "NUMBER", "BRACE_OPEN", "BRACE_CLOSE", "COMMA", "VARIABLE", 
			"UDB_PARAMETER", "LIST", "STRING", "OPERATOR_ADD", "OPERATOR_NUMERIC_DIVIDE", 
			"OPERATOR_NUMERIC_MULTIPLY", "OPERATOR_LOGIC_AND", "OPERATOR_LOGIC_OR", 
			"OPERATOR_LOGIC_NOT", "OPERATOR_LOGIC_EQUAL", "OPERATOR_LOGIC_NOT_EQUAL", 
			"OPERATOR_LOGIC_LOWER", "OPERATOR_LOGIC_GREATER", "OPERATOR_LOGIC_LOWER_EQUAL", 
			"OPERATOR_LOGIC_GREATER_EQUAL", "FUNCTION_OR_SENSOR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CatrobatParameterLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CatrobatParameterLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0016\u00b3\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017"+
		"\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0001\u0000\u0001\u0000"+
		"\u0003\u00008\b\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0004\u0004A\b\u0004\u000b\u0004"+
		"\f\u0004B\u0001\u0004\u0001\u0004\u0001\u0005\u0004\u0005H\b\u0005\u000b"+
		"\u0005\f\u0005I\u0001\u0005\u0001\u0005\u0004\u0005N\b\u0005\u000b\u0005"+
		"\f\u0005O\u0003\u0005R\b\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0005\t^\b\t\n"+
		"\t\f\ta\t\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0005\nl\b\n\n\n\f\no\t\n\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0005\u000bw\b\u000b\n\u000b\f\u000bz\t"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f"+
		"\u0082\b\f\n\f\f\f\u0085\t\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u009c\b\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0003\u0017\u00a5\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0003\u0018\u00aa\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0004\u0019\u00b0\b\u0019\u000b\u0019\f\u0019\u00b1\u0000\u0000\u001a"+
		"\u0001\u0000\u0003\u0000\u0005\u0000\u0007\u0000\t\u0001\u000b\u0002\r"+
		"\u0003\u000f\u0004\u0011\u0005\u0013\u0006\u0015\u0007\u0017\b\u0019\t"+
		"\u001b\n\u001d\u000b\u001f\f!\r#\u000e%\u000f\'\u0010)\u0011+\u0012-\u0013"+
		"/\u00141\u00153\u0016\u0001\u0000\t\u0003\u0000\t\n\r\r  \u0001\u0000"+
		"\"\"\u0001\u0000[[\u0001\u0000]]\u0001\u0000**\u0001\u0000\'\'\u0002\u0000"+
		"++--\u0002\u0000//\u00f7\u00f7\u0002\u0000  %%\u00c3\u0000\t\u0001\u0000"+
		"\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000"+
		"\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000"+
		"\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000"+
		"\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000"+
		"\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'"+
		"\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000"+
		"\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000"+
		"\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00017"+
		"\u0001\u0000\u0000\u0000\u00039\u0001\u0000\u0000\u0000\u0005;\u0001\u0000"+
		"\u0000\u0000\u0007=\u0001\u0000\u0000\u0000\t@\u0001\u0000\u0000\u0000"+
		"\u000bG\u0001\u0000\u0000\u0000\rS\u0001\u0000\u0000\u0000\u000fU\u0001"+
		"\u0000\u0000\u0000\u0011W\u0001\u0000\u0000\u0000\u0013Y\u0001\u0000\u0000"+
		"\u0000\u0015d\u0001\u0000\u0000\u0000\u0017r\u0001\u0000\u0000\u0000\u0019"+
		"}\u0001\u0000\u0000\u0000\u001b\u0088\u0001\u0000\u0000\u0000\u001d\u008a"+
		"\u0001\u0000\u0000\u0000\u001f\u008c\u0001\u0000\u0000\u0000!\u008e\u0001"+
		"\u0000\u0000\u0000#\u0091\u0001\u0000\u0000\u0000%\u0094\u0001\u0000\u0000"+
		"\u0000\'\u0096\u0001\u0000\u0000\u0000)\u009b\u0001\u0000\u0000\u0000"+
		"+\u009d\u0001\u0000\u0000\u0000-\u009f\u0001\u0000\u0000\u0000/\u00a4"+
		"\u0001\u0000\u0000\u00001\u00a9\u0001\u0000\u0000\u00003\u00ab\u0001\u0000"+
		"\u0000\u000058\u0003\u0005\u0002\u000068\u0003\u0003\u0001\u000075\u0001"+
		"\u0000\u0000\u000076\u0001\u0000\u0000\u00008\u0002\u0001\u0000\u0000"+
		"\u00009:\u0002AZ\u0000:\u0004\u0001\u0000\u0000\u0000;<\u0002az\u0000"+
		"<\u0006\u0001\u0000\u0000\u0000=>\u000209\u0000>\b\u0001\u0000\u0000\u0000"+
		"?A\u0007\u0000\u0000\u0000@?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000"+
		"\u0000B@\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CD\u0001\u0000"+
		"\u0000\u0000DE\u0006\u0004\u0000\u0000E\n\u0001\u0000\u0000\u0000FH\u0003"+
		"\u0007\u0003\u0000GF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000"+
		"IG\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JQ\u0001\u0000\u0000"+
		"\u0000KM\u0005.\u0000\u0000LN\u0003\u0007\u0003\u0000ML\u0001\u0000\u0000"+
		"\u0000NO\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000"+
		"\u0000\u0000PR\u0001\u0000\u0000\u0000QK\u0001\u0000\u0000\u0000QR\u0001"+
		"\u0000\u0000\u0000R\f\u0001\u0000\u0000\u0000ST\u0005(\u0000\u0000T\u000e"+
		"\u0001\u0000\u0000\u0000UV\u0005)\u0000\u0000V\u0010\u0001\u0000\u0000"+
		"\u0000WX\u0005,\u0000\u0000X\u0012\u0001\u0000\u0000\u0000Y_\u0005\"\u0000"+
		"\u0000Z^\b\u0001\u0000\u0000[\\\u0005\\\u0000\u0000\\^\u0005\"\u0000\u0000"+
		"]Z\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000"+
		"\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`b\u0001\u0000"+
		"\u0000\u0000a_\u0001\u0000\u0000\u0000bc\u0005\"\u0000\u0000c\u0014\u0001"+
		"\u0000\u0000\u0000dm\u0005[\u0000\u0000el\b\u0002\u0000\u0000fl\b\u0003"+
		"\u0000\u0000gh\u0005\\\u0000\u0000hl\u0005[\u0000\u0000ij\u0005\\\u0000"+
		"\u0000jl\u0005]\u0000\u0000ke\u0001\u0000\u0000\u0000kf\u0001\u0000\u0000"+
		"\u0000kg\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000lo\u0001\u0000"+
		"\u0000\u0000mk\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000np\u0001"+
		"\u0000\u0000\u0000om\u0001\u0000\u0000\u0000pq\u0005]\u0000\u0000q\u0016"+
		"\u0001\u0000\u0000\u0000rx\u0005*\u0000\u0000sw\b\u0004\u0000\u0000tu"+
		"\u0005\\\u0000\u0000uw\u0005*\u0000\u0000vs\u0001\u0000\u0000\u0000vt"+
		"\u0001\u0000\u0000\u0000wz\u0001\u0000\u0000\u0000xv\u0001\u0000\u0000"+
		"\u0000xy\u0001\u0000\u0000\u0000y{\u0001\u0000\u0000\u0000zx\u0001\u0000"+
		"\u0000\u0000{|\u0005*\u0000\u0000|\u0018\u0001\u0000\u0000\u0000}\u0083"+
		"\u0005\'\u0000\u0000~\u0082\b\u0005\u0000\u0000\u007f\u0080\u0005\\\u0000"+
		"\u0000\u0080\u0082\u0005\'\u0000\u0000\u0081~\u0001\u0000\u0000\u0000"+
		"\u0081\u007f\u0001\u0000\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000"+
		"\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000"+
		"\u0084\u0086\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000"+
		"\u0086\u0087\u0005\'\u0000\u0000\u0087\u001a\u0001\u0000\u0000\u0000\u0088"+
		"\u0089\u0007\u0006\u0000\u0000\u0089\u001c\u0001\u0000\u0000\u0000\u008a"+
		"\u008b\u0007\u0007\u0000\u0000\u008b\u001e\u0001\u0000\u0000\u0000\u008c"+
		"\u008d\u0005\u00d7\u0000\u0000\u008d \u0001\u0000\u0000\u0000\u008e\u008f"+
		"\u0005&\u0000\u0000\u008f\u0090\u0005&\u0000\u0000\u0090\"\u0001\u0000"+
		"\u0000\u0000\u0091\u0092\u0005|\u0000\u0000\u0092\u0093\u0005|\u0000\u0000"+
		"\u0093$\u0001\u0000\u0000\u0000\u0094\u0095\u0005!\u0000\u0000\u0095&"+
		"\u0001\u0000\u0000\u0000\u0096\u0097\u0005=\u0000\u0000\u0097(\u0001\u0000"+
		"\u0000\u0000\u0098\u0099\u0005!\u0000\u0000\u0099\u009c\u0005=\u0000\u0000"+
		"\u009a\u009c\u0005\u2260\u0000\u0000\u009b\u0098\u0001\u0000\u0000\u0000"+
		"\u009b\u009a\u0001\u0000\u0000\u0000\u009c*\u0001\u0000\u0000\u0000\u009d"+
		"\u009e\u0005<\u0000\u0000\u009e,\u0001\u0000\u0000\u0000\u009f\u00a0\u0005"+
		">\u0000\u0000\u00a0.\u0001\u0000\u0000\u0000\u00a1\u00a2\u0005<\u0000"+
		"\u0000\u00a2\u00a5\u0005=\u0000\u0000\u00a3\u00a5\u0005\u2264\u0000\u0000"+
		"\u00a4\u00a1\u0001\u0000\u0000\u0000\u00a4\u00a3\u0001\u0000\u0000\u0000"+
		"\u00a50\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005>\u0000\u0000\u00a7\u00aa"+
		"\u0005=\u0000\u0000\u00a8\u00aa\u0005\u2265\u0000\u0000\u00a9\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a9\u00a8\u0001\u0000\u0000\u0000\u00aa2\u0001\u0000"+
		"\u0000\u0000\u00ab\u00af\u0003\u0001\u0000\u0000\u00ac\u00b0\u0003\u0001"+
		"\u0000\u0000\u00ad\u00b0\u0003\u0007\u0003\u0000\u00ae\u00b0\u0007\b\u0000"+
		"\u0000\u00af\u00ac\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000"+
		"\u0000\u00af\u00ae\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000"+
		"\u0000\u00b1\u00af\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000"+
		"\u0000\u00b24\u0001\u0000\u0000\u0000\u0013\u00007BIOQ]_kmvx\u0081\u0083"+
		"\u009b\u00a4\u00a9\u00af\u00b1\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}