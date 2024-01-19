// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/CatrobatParameterLexer.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen;
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
		LIST=8, STRING=9, OPERATOR_NUMERIC_ADD=10, OPERATOR_NUMERIC_MINUS=11, 
		OPERATOR_NUMERIC_DIVIDE=12, OPERATOR_NUMERIC_MULTIPLY=13, OPERATOR_LOGIC_AND=14, 
		OPERATOR_LOGIC_OR=15, OPERATOR_LOGIC_NOT=16, OPERATOR_LOGIC_EQUAL=17, 
		OPERATOR_LOGIC_NOT_EQUAL=18, OPERATOR_LOGIC_LOWER=19, OPERATOR_LOGIC_GREATER=20, 
		OPERATOR_LOGIC_LOWER_EQUAL=21, OPERATOR_LOGIC_GREATER_EQUAL=22, SENSOR_OR_PROPERTY_OR_METHOD=23, 
		HEX_NUMBER=24;
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
			"OPERATOR_NUMERIC_ADD", "OPERATOR_NUMERIC_MINUS", "OPERATOR_NUMERIC_DIVIDE", 
			"OPERATOR_NUMERIC_MULTIPLY", "OPERATOR_LOGIC_AND", "OPERATOR_LOGIC_OR", 
			"OPERATOR_LOGIC_NOT", "OPERATOR_LOGIC_EQUAL", "OPERATOR_LOGIC_NOT_EQUAL", 
			"OPERATOR_LOGIC_LOWER", "OPERATOR_LOGIC_GREATER", "OPERATOR_LOGIC_LOWER_EQUAL", 
			"OPERATOR_LOGIC_GREATER_EQUAL", "SENSOR_OR_PROPERTY_OR_METHOD", "HEX_NUMBER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'('", "')'", "','", null, null, null, null, "'+'", 
			"'-'", null, "'\\u00D7'", "'&&'", "'||'", "'!'", "'='", null, "'<'", 
			"'>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "NUMBER", "BRACE_OPEN", "BRACE_CLOSE", "COMMA", "VARIABLE", 
			"UDB_PARAMETER", "LIST", "STRING", "OPERATOR_NUMERIC_ADD", "OPERATOR_NUMERIC_MINUS", 
			"OPERATOR_NUMERIC_DIVIDE", "OPERATOR_NUMERIC_MULTIPLY", "OPERATOR_LOGIC_AND", 
			"OPERATOR_LOGIC_OR", "OPERATOR_LOGIC_NOT", "OPERATOR_LOGIC_EQUAL", "OPERATOR_LOGIC_NOT_EQUAL", 
			"OPERATOR_LOGIC_LOWER", "OPERATOR_LOGIC_GREATER", "OPERATOR_LOGIC_LOWER_EQUAL", 
			"OPERATOR_LOGIC_GREATER_EQUAL", "SENSOR_OR_PROPERTY_OR_METHOD", "HEX_NUMBER"
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
		"\u0004\u0000\u0018\u00bf\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017"+
		"\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a"+
		"\u0002\u001b\u0007\u001b\u0001\u0000\u0001\u0000\u0003\u0000<\b\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0004\u0004E\b\u0004\u000b\u0004\f\u0004F\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0004\u0005L\b\u0005\u000b\u0005\f\u0005M\u0001\u0005"+
		"\u0001\u0005\u0004\u0005R\b\u0005\u000b\u0005\f\u0005S\u0003\u0005V\b"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\t\u0001\t\u0001\t\u0001\t\u0005\tb\b\t\n\t\f\te\t\t\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\np\b"+
		"\n\n\n\f\ns\t\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0005\u000b{\b\u000b\n\u000b\f\u000b~\t\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u0086\b\f\n\f\f\f\u0089"+
		"\t\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00a2\b\u0015"+
		"\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0003\u0018\u00ab\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0003\u0019\u00b0\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0004\u001a\u00b6\b\u001a\u000b\u001a\f\u001a\u00b7\u0001\u001b\u0001"+
		"\u001b\u0004\u001b\u00bc\b\u001b\u000b\u001b\f\u001b\u00bd\u0000\u0000"+
		"\u001c\u0001\u0000\u0003\u0000\u0005\u0000\u0007\u0000\t\u0001\u000b\u0002"+
		"\r\u0003\u000f\u0004\u0011\u0005\u0013\u0006\u0015\u0007\u0017\b\u0019"+
		"\t\u001b\n\u001d\u000b\u001f\f!\r#\u000e%\u000f\'\u0010)\u0011+\u0012"+
		"-\u0013/\u00141\u00153\u00165\u00177\u0018\u0001\u0000\t\u0003\u0000\t"+
		"\n\r\r  \u0001\u0000\"\"\u0001\u0000[[\u0001\u0000]]\u0001\u0000**\u0001"+
		"\u0000\'\'\u0002\u0000//\u00f7\u00f7\u0003\u0000  %%\u00b0\u00b0\u0003"+
		"\u000009AFaf\u00d0\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000"+
		"\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000"+
		"\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000"+
		"\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000"+
		"\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000"+
		"\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000"+
		"\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000"+
		"%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001"+
		"\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000"+
		"\u0000\u0000/\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u0000"+
		"3\u0001\u0000\u0000\u0000\u00005\u0001\u0000\u0000\u0000\u00007\u0001"+
		"\u0000\u0000\u0000\u0001;\u0001\u0000\u0000\u0000\u0003=\u0001\u0000\u0000"+
		"\u0000\u0005?\u0001\u0000\u0000\u0000\u0007A\u0001\u0000\u0000\u0000\t"+
		"D\u0001\u0000\u0000\u0000\u000bK\u0001\u0000\u0000\u0000\rW\u0001\u0000"+
		"\u0000\u0000\u000fY\u0001\u0000\u0000\u0000\u0011[\u0001\u0000\u0000\u0000"+
		"\u0013]\u0001\u0000\u0000\u0000\u0015h\u0001\u0000\u0000\u0000\u0017v"+
		"\u0001\u0000\u0000\u0000\u0019\u0081\u0001\u0000\u0000\u0000\u001b\u008c"+
		"\u0001\u0000\u0000\u0000\u001d\u008e\u0001\u0000\u0000\u0000\u001f\u0090"+
		"\u0001\u0000\u0000\u0000!\u0092\u0001\u0000\u0000\u0000#\u0094\u0001\u0000"+
		"\u0000\u0000%\u0097\u0001\u0000\u0000\u0000\'\u009a\u0001\u0000\u0000"+
		"\u0000)\u009c\u0001\u0000\u0000\u0000+\u00a1\u0001\u0000\u0000\u0000-"+
		"\u00a3\u0001\u0000\u0000\u0000/\u00a5\u0001\u0000\u0000\u00001\u00aa\u0001"+
		"\u0000\u0000\u00003\u00af\u0001\u0000\u0000\u00005\u00b1\u0001\u0000\u0000"+
		"\u00007\u00b9\u0001\u0000\u0000\u00009<\u0003\u0005\u0002\u0000:<\u0003"+
		"\u0003\u0001\u0000;9\u0001\u0000\u0000\u0000;:\u0001\u0000\u0000\u0000"+
		"<\u0002\u0001\u0000\u0000\u0000=>\u0002AZ\u0000>\u0004\u0001\u0000\u0000"+
		"\u0000?@\u0002az\u0000@\u0006\u0001\u0000\u0000\u0000AB\u000209\u0000"+
		"B\b\u0001\u0000\u0000\u0000CE\u0007\u0000\u0000\u0000DC\u0001\u0000\u0000"+
		"\u0000EF\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000"+
		"\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0006\u0004\u0000\u0000I\n\u0001"+
		"\u0000\u0000\u0000JL\u0003\u0007\u0003\u0000KJ\u0001\u0000\u0000\u0000"+
		"LM\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000"+
		"\u0000NU\u0001\u0000\u0000\u0000OQ\u0005.\u0000\u0000PR\u0003\u0007\u0003"+
		"\u0000QP\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SQ\u0001\u0000"+
		"\u0000\u0000ST\u0001\u0000\u0000\u0000TV\u0001\u0000\u0000\u0000UO\u0001"+
		"\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000V\f\u0001\u0000\u0000\u0000"+
		"WX\u0005(\u0000\u0000X\u000e\u0001\u0000\u0000\u0000YZ\u0005)\u0000\u0000"+
		"Z\u0010\u0001\u0000\u0000\u0000[\\\u0005,\u0000\u0000\\\u0012\u0001\u0000"+
		"\u0000\u0000]c\u0005\"\u0000\u0000^b\b\u0001\u0000\u0000_`\u0005\\\u0000"+
		"\u0000`b\u0005\"\u0000\u0000a^\u0001\u0000\u0000\u0000a_\u0001\u0000\u0000"+
		"\u0000be\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cd\u0001\u0000"+
		"\u0000\u0000df\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000fg\u0005"+
		"\"\u0000\u0000g\u0014\u0001\u0000\u0000\u0000hq\u0005[\u0000\u0000ip\b"+
		"\u0002\u0000\u0000jp\b\u0003\u0000\u0000kl\u0005\\\u0000\u0000lp\u0005"+
		"[\u0000\u0000mn\u0005\\\u0000\u0000np\u0005]\u0000\u0000oi\u0001\u0000"+
		"\u0000\u0000oj\u0001\u0000\u0000\u0000ok\u0001\u0000\u0000\u0000om\u0001"+
		"\u0000\u0000\u0000ps\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000"+
		"qr\u0001\u0000\u0000\u0000rt\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000"+
		"\u0000tu\u0005]\u0000\u0000u\u0016\u0001\u0000\u0000\u0000v|\u0005*\u0000"+
		"\u0000w{\b\u0004\u0000\u0000xy\u0005\\\u0000\u0000y{\u0005*\u0000\u0000"+
		"zw\u0001\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000{~\u0001\u0000\u0000"+
		"\u0000|z\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}\u007f\u0001"+
		"\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000\u007f\u0080\u0005*\u0000"+
		"\u0000\u0080\u0018\u0001\u0000\u0000\u0000\u0081\u0087\u0005\'\u0000\u0000"+
		"\u0082\u0086\b\u0005\u0000\u0000\u0083\u0084\u0005\\\u0000\u0000\u0084"+
		"\u0086\u0005\'\u0000\u0000\u0085\u0082\u0001\u0000\u0000\u0000\u0085\u0083"+
		"\u0001\u0000\u0000\u0000\u0086\u0089\u0001\u0000\u0000\u0000\u0087\u0085"+
		"\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u008a"+
		"\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u008a\u008b"+
		"\u0005\'\u0000\u0000\u008b\u001a\u0001\u0000\u0000\u0000\u008c\u008d\u0005"+
		"+\u0000\u0000\u008d\u001c\u0001\u0000\u0000\u0000\u008e\u008f\u0005-\u0000"+
		"\u0000\u008f\u001e\u0001\u0000\u0000\u0000\u0090\u0091\u0007\u0006\u0000"+
		"\u0000\u0091 \u0001\u0000\u0000\u0000\u0092\u0093\u0005\u00d7\u0000\u0000"+
		"\u0093\"\u0001\u0000\u0000\u0000\u0094\u0095\u0005&\u0000\u0000\u0095"+
		"\u0096\u0005&\u0000\u0000\u0096$\u0001\u0000\u0000\u0000\u0097\u0098\u0005"+
		"|\u0000\u0000\u0098\u0099\u0005|\u0000\u0000\u0099&\u0001\u0000\u0000"+
		"\u0000\u009a\u009b\u0005!\u0000\u0000\u009b(\u0001\u0000\u0000\u0000\u009c"+
		"\u009d\u0005=\u0000\u0000\u009d*\u0001\u0000\u0000\u0000\u009e\u009f\u0005"+
		"!\u0000\u0000\u009f\u00a2\u0005=\u0000\u0000\u00a0\u00a2\u0005\u2260\u0000"+
		"\u0000\u00a1\u009e\u0001\u0000\u0000\u0000\u00a1\u00a0\u0001\u0000\u0000"+
		"\u0000\u00a2,\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005<\u0000\u0000\u00a4"+
		".\u0001\u0000\u0000\u0000\u00a5\u00a6\u0005>\u0000\u0000\u00a60\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a8\u0005<\u0000\u0000\u00a8\u00ab\u0005=\u0000"+
		"\u0000\u00a9\u00ab\u0005\u2264\u0000\u0000\u00aa\u00a7\u0001\u0000\u0000"+
		"\u0000\u00aa\u00a9\u0001\u0000\u0000\u0000\u00ab2\u0001\u0000\u0000\u0000"+
		"\u00ac\u00ad\u0005>\u0000\u0000\u00ad\u00b0\u0005=\u0000\u0000\u00ae\u00b0"+
		"\u0005\u2265\u0000\u0000\u00af\u00ac\u0001\u0000\u0000\u0000\u00af\u00ae"+
		"\u0001\u0000\u0000\u0000\u00b04\u0001\u0000\u0000\u0000\u00b1\u00b5\u0003"+
		"\u0001\u0000\u0000\u00b2\u00b6\u0003\u0001\u0000\u0000\u00b3\u00b6\u0003"+
		"\u0007\u0003\u0000\u00b4\u00b6\u0007\u0007\u0000\u0000\u00b5\u00b2\u0001"+
		"\u0000\u0000\u0000\u00b5\u00b3\u0001\u0000\u0000\u0000\u00b5\u00b4\u0001"+
		"\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00b5\u0001"+
		"\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b86\u0001\u0000"+
		"\u0000\u0000\u00b9\u00bb\u0005#\u0000\u0000\u00ba\u00bc\u0007\b\u0000"+
		"\u0000\u00bb\u00ba\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000"+
		"\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000\u00bd\u00be\u0001\u0000\u0000"+
		"\u0000\u00be8\u0001\u0000\u0000\u0000\u0014\u0000;FMSUacoqz|\u0085\u0087"+
		"\u00a1\u00aa\u00af\u00b5\u00b7\u00bd\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}