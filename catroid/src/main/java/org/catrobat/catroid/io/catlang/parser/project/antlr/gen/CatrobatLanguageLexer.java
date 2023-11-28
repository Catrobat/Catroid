// Generated from ./CatrobatLanguageLexer.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.project.antlr.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CatrobatLanguageLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NUMBER=1, WS=2, CURLY_BRACKET_OPEN=3, CURLY_BRACKET_CLOSE=4, STRING=5, 
		SEPARATOR=6, COLON=7, VARIABLE_REF=8, LIST_REF=9, PROGRAM_START=10, PROGRAM=11, 
		METADATA=12, DESCRIPTION=13, CATROBAT_VERSION=14, CATRPBAT_APP_VERSION=15, 
		STAGE=16, LANDSCAPE_MODE=17, HEIGHT=18, WIDTH=19, DISPLAY_MODE=20, GLOBALS=21, 
		MULTIPLAYER_VARIABLES=22, LOCAL_VARIABLES=23, LOOKS=24, SOUNDS=25, ACTOR_OR_OBJECT=26, 
		OF_TYPE=27, SCENE=28, BACKGROUND=29, SCRIPTS=30, NOTE_BRICK=31, DISABLED_BRICK_INDICATION=32, 
		BRICK_NAME=33, BRICK_MODE_WS=34, BRICK_MODE_BRACKET_OPEN=35, SEMICOLON=36, 
		BRICK_BODY_OPEN=37, PARAM_MODE_WS=38, PARAM_MODE_BRACKET_OPEN=39, PARAM_MODE_BRACKET_CLOSE=40, 
		PARAM_MODE_NAME=41, PARAM_MODE_COLON=42, PARAM_SEPARATOR=43, FORMULA_MODE_WS=44, 
		FORMULA_MODE_BRACKET_CLOSE=45, FORMULA_MODE_BRACKET_OPEN=46, FORMULA_MODE_ANYTHING=47, 
		FORMULA_MODE_APOSTROPHE=48, FORMULA_MODE_QUOTE=49, FORMULA_MODE_UDB_PARAM=50, 
		ESCAPE_MODE_APOSTROPHE_ANYTHING=51, ESCAPE_MODE_APOSTROPHE_CHAR=52, ESCAPE_MODE_QUOTE_ANYTHING=53, 
		ESCAPE_MODE_QUOTE_CHAR=54, ESCAPE_UDB_PARAM_MODE_ANYTHING=55, ESCAPE_UDB_PARAM_MODE_CHAR=56;
	public static final int
		BRICK_MODE=1, PARAM_MODE=2, FORMULA_MODE=3, ESCAPE_MODE_APOSTROPHE=4, 
		ESCAPE_MODE_QUOTE=5, ESCAPE_UDB_PARAM_MODE=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "BRICK_MODE", "PARAM_MODE", "FORMULA_MODE", "ESCAPE_MODE_APOSTROPHE", 
		"ESCAPE_MODE_QUOTE", "ESCAPE_UDB_PARAM_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LETTER", "UPPERCASE", "LOWERCASE", "DIGIT", "NUMBER", "WS", "CURLY_BRACKET_OPEN", 
			"CURLY_BRACKET_CLOSE", "STRING", "SEPARATOR", "COLON", "VARIABLE_REF", 
			"VAR_ESCAPE", "LIST_REF", "LIST_ESCAPE", "PROGRAM_START", "PROGRAM", 
			"METADATA", "DESCRIPTION", "CATROBAT_VERSION", "CATRPBAT_APP_VERSION", 
			"STAGE", "LANDSCAPE_MODE", "HEIGHT", "WIDTH", "DISPLAY_MODE", "GLOBALS", 
			"MULTIPLAYER_VARIABLES", "LOCAL_VARIABLES", "LOOKS", "SOUNDS", "ACTOR_OR_OBJECT", 
			"OF_TYPE", "SCENE", "BACKGROUND", "SCRIPTS", "NOTE_BRICK", "NOTE_BRICK_ESCAPE", 
			"DISABLED_BRICK_INDICATION", "BRICK_NAME", "BRICK_MODE_WS", "BRICK_MODE_BRACKET_OPEN", 
			"SEMICOLON", "BRICK_BODY_OPEN", "PARAM_MODE_WS", "PARAM_MODE_BRACKET_OPEN", 
			"PARAM_MODE_BRACKET_CLOSE", "PARAM_MODE_NAME", "PARAM_MODE_COLON", "PARAM_SEPARATOR", 
			"FORMULA_MODE_WS", "FORMULA_MODE_BRACKET_CLOSE", "FORMULA_MODE_BRACKET_OPEN", 
			"FORMULA_MODE_ANYTHING", "FORMULA_MODE_APOSTROPHE", "FORMULA_MODE_QUOTE", 
			"FORMULA_MODE_UDB_PARAM", "ESCAPE_MODE_APOSTROPHE_ANYTHING", "ESCAPE_MODE_APOSTROPHE_CHAR", 
			"ESCAPE_MODE_QUOTE_ANYTHING", "ESCAPE_MODE_QUOTE_CHAR", "ESCAPE_UDB_PARAM_MODE_ANYTHING", 
			"ESCAPE_UDB_PARAM_MODE_CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "'}'", null, null, null, null, null, null, "'Program'", 
			"'Metadata'", "'Description'", "'Catrobat version'", "'Catrobat app version'", 
			"'Stage'", "'Landscape mode'", "'Height'", "'Width'", "'Display mode'", 
			"'Globals'", "'Multiplayer variables'", "'Locals'", "'Looks'", "'Sounds'", 
			"'Actor or object'", "'of type'", "'Scene'", "'Background'", "'Scripts'", 
			null, "'//'", null, null, null, "';'", null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "'['", null, null, null, 
			null, null, "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NUMBER", "WS", "CURLY_BRACKET_OPEN", "CURLY_BRACKET_CLOSE", "STRING", 
			"SEPARATOR", "COLON", "VARIABLE_REF", "LIST_REF", "PROGRAM_START", "PROGRAM", 
			"METADATA", "DESCRIPTION", "CATROBAT_VERSION", "CATRPBAT_APP_VERSION", 
			"STAGE", "LANDSCAPE_MODE", "HEIGHT", "WIDTH", "DISPLAY_MODE", "GLOBALS", 
			"MULTIPLAYER_VARIABLES", "LOCAL_VARIABLES", "LOOKS", "SOUNDS", "ACTOR_OR_OBJECT", 
			"OF_TYPE", "SCENE", "BACKGROUND", "SCRIPTS", "NOTE_BRICK", "DISABLED_BRICK_INDICATION", 
			"BRICK_NAME", "BRICK_MODE_WS", "BRICK_MODE_BRACKET_OPEN", "SEMICOLON", 
			"BRICK_BODY_OPEN", "PARAM_MODE_WS", "PARAM_MODE_BRACKET_OPEN", "PARAM_MODE_BRACKET_CLOSE", 
			"PARAM_MODE_NAME", "PARAM_MODE_COLON", "PARAM_SEPARATOR", "FORMULA_MODE_WS", 
			"FORMULA_MODE_BRACKET_CLOSE", "FORMULA_MODE_BRACKET_OPEN", "FORMULA_MODE_ANYTHING", 
			"FORMULA_MODE_APOSTROPHE", "FORMULA_MODE_QUOTE", "FORMULA_MODE_UDB_PARAM", 
			"ESCAPE_MODE_APOSTROPHE_ANYTHING", "ESCAPE_MODE_APOSTROPHE_CHAR", "ESCAPE_MODE_QUOTE_ANYTHING", 
			"ESCAPE_MODE_QUOTE_CHAR", "ESCAPE_UDB_PARAM_MODE_ANYTHING", "ESCAPE_UDB_PARAM_MODE_CHAR"
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


	public CatrobatLanguageLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CatrobatLanguageLexer.g4"; }

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
		"\u0004\u00008\u0267\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff"+
		"\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff"+
		"\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002"+
		"\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005"+
		"\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002"+
		"\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002"+
		"\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010"+
		"\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013"+
		"\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016"+
		"\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019"+
		"\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c"+
		"\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f"+
		"\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007"+
		"#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002(\u0007"+
		"(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002-\u0007"+
		"-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u00022\u0007"+
		"2\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u00027\u0007"+
		"7\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002<\u0007"+
		"<\u0002=\u0007=\u0002>\u0007>\u0001\u0000\u0001\u0000\u0003\u0000\u0088"+
		"\b\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0004\u0004\u0091\b\u0004\u000b\u0004\f\u0004\u0092"+
		"\u0001\u0004\u0001\u0004\u0004\u0004\u0097\b\u0004\u000b\u0004\f\u0004"+
		"\u0098\u0003\u0004\u009b\b\u0004\u0001\u0005\u0004\u0005\u009e\b\u0005"+
		"\u000b\u0005\f\u0005\u009f\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b\u00ac"+
		"\b\b\n\b\f\b\u00af\t\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u00ba\b\u000b\n\u000b"+
		"\f\u000b\u00bd\t\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f"+
		"\u0001\r\u0001\r\u0001\r\u0005\r\u00c7\b\r\n\r\f\r\u00ca\t\r\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0004\u000f\u00d5\b\u000f\u000b\u000f\f\u000f\u00d6\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0004\u000f\u00f4\b\u000f\u000b\u000f\f"+
		"\u000f\u00f5\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001"+
		"\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001"+
		"$\u0001$\u0001$\u0005$\u01d2\b$\n$\f$\u01d5\t$\u0001%\u0001%\u0001%\u0001"+
		"&\u0001&\u0001&\u0001\'\u0004\'\u01de\b\'\u000b\'\f\'\u01df\u0001\'\u0001"+
		"\'\u0004\'\u01e4\b\'\u000b\'\f\'\u01e5\u0005\'\u01e8\b\'\n\'\f\'\u01eb"+
		"\t\'\u0001\'\u0001\'\u0001(\u0004(\u01f0\b(\u000b(\f(\u01f1\u0001(\u0001"+
		"(\u0001)\u0001)\u0001)\u0001)\u0001*\u0001*\u0001*\u0001*\u0001+\u0001"+
		"+\u0001+\u0001+\u0001,\u0004,\u0203\b,\u000b,\f,\u0204\u0001,\u0001,\u0001"+
		"-\u0001-\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001/\u0004/\u0212"+
		"\b/\u000b/\f/\u0213\u0001/\u0001/\u0004/\u0218\b/\u000b/\f/\u0219\u0005"+
		"/\u021c\b/\n/\f/\u021f\t/\u00010\u00010\u00011\u00011\u00012\u00042\u0226"+
		"\b2\u000b2\f2\u0227\u00012\u00012\u00013\u00013\u00013\u00013\u00014\u0001"+
		"4\u00014\u00014\u00015\u00045\u0235\b5\u000b5\f5\u0236\u00015\u00015\u0001"+
		"6\u00016\u00016\u00016\u00017\u00017\u00017\u00017\u00018\u00018\u0001"+
		"8\u00018\u00019\u00049\u0248\b9\u000b9\f9\u0249\u00019\u00019\u0001:\u0001"+
		":\u0001:\u0001:\u0001;\u0004;\u0253\b;\u000b;\f;\u0254\u0001;\u0001;\u0001"+
		"<\u0001<\u0001<\u0001<\u0001=\u0004=\u025e\b=\u000b=\f=\u025f\u0001=\u0001"+
		"=\u0001>\u0001>\u0001>\u0001>\u0000\u0000?\u0007\u0000\t\u0000\u000b\u0000"+
		"\r\u0000\u000f\u0001\u0011\u0002\u0013\u0003\u0015\u0004\u0017\u0005\u0019"+
		"\u0006\u001b\u0007\u001d\b\u001f\u0000!\t#\u0000%\n\'\u000b)\f+\r-\u000e"+
		"/\u000f1\u00103\u00115\u00127\u00139\u0014;\u0015=\u0016?\u0017A\u0018"+
		"C\u0019E\u001aG\u001bI\u001cK\u001dM\u001eO\u001fQ\u0000S U!W\"Y#[$]%"+
		"_&a\'c(e)g*i+k,m-o.q/s0u1w2y3{4}5\u007f6\u00817\u00838\u0007\u0000\u0001"+
		"\u0002\u0003\u0004\u0005\u0006\u000b\u0003\u0000\t\n\r\r  \u0001\u0000"+
		"\'\'\u0003\u0000\n\n\r\r\"\"\u0007\u0000\"\"\\\\bbffnnrrtt\u0003\u0000"+
		"\n\n\r\r**\u0007\u0000**\\\\bbffnnrrtt\u0002\u0000\n\n\r\r\u0006\u0000"+
		"\\\\bbffnnrrtt\u0004\u0000\"\"\')[[]]\u0001\u0000\"\"\u0001\u0000]]\u0275"+
		"\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000"+
		"\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000"+
		"\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000"+
		"\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000"+
		"\u0000!\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'"+
		"\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000"+
		"\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000"+
		"\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00005"+
		"\u0001\u0000\u0000\u0000\u00007\u0001\u0000\u0000\u0000\u00009\u0001\u0000"+
		"\u0000\u0000\u0000;\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000\u0000"+
		"\u0000?\u0001\u0000\u0000\u0000\u0000A\u0001\u0000\u0000\u0000\u0000C"+
		"\u0001\u0000\u0000\u0000\u0000E\u0001\u0000\u0000\u0000\u0000G\u0001\u0000"+
		"\u0000\u0000\u0000I\u0001\u0000\u0000\u0000\u0000K\u0001\u0000\u0000\u0000"+
		"\u0000M\u0001\u0000\u0000\u0000\u0000O\u0001\u0000\u0000\u0000\u0000S"+
		"\u0001\u0000\u0000\u0000\u0000U\u0001\u0000\u0000\u0000\u0001W\u0001\u0000"+
		"\u0000\u0000\u0001Y\u0001\u0000\u0000\u0000\u0001[\u0001\u0000\u0000\u0000"+
		"\u0001]\u0001\u0000\u0000\u0000\u0002_\u0001\u0000\u0000\u0000\u0002a"+
		"\u0001\u0000\u0000\u0000\u0002c\u0001\u0000\u0000\u0000\u0002e\u0001\u0000"+
		"\u0000\u0000\u0002g\u0001\u0000\u0000\u0000\u0002i\u0001\u0000\u0000\u0000"+
		"\u0003k\u0001\u0000\u0000\u0000\u0003m\u0001\u0000\u0000\u0000\u0003o"+
		"\u0001\u0000\u0000\u0000\u0003q\u0001\u0000\u0000\u0000\u0003s\u0001\u0000"+
		"\u0000\u0000\u0003u\u0001\u0000\u0000\u0000\u0003w\u0001\u0000\u0000\u0000"+
		"\u0004y\u0001\u0000\u0000\u0000\u0004{\u0001\u0000\u0000\u0000\u0005}"+
		"\u0001\u0000\u0000\u0000\u0005\u007f\u0001\u0000\u0000\u0000\u0006\u0081"+
		"\u0001\u0000\u0000\u0000\u0006\u0083\u0001\u0000\u0000\u0000\u0007\u0087"+
		"\u0001\u0000\u0000\u0000\t\u0089\u0001\u0000\u0000\u0000\u000b\u008b\u0001"+
		"\u0000\u0000\u0000\r\u008d\u0001\u0000\u0000\u0000\u000f\u0090\u0001\u0000"+
		"\u0000\u0000\u0011\u009d\u0001\u0000\u0000\u0000\u0013\u00a3\u0001\u0000"+
		"\u0000\u0000\u0015\u00a5\u0001\u0000\u0000\u0000\u0017\u00a7\u0001\u0000"+
		"\u0000\u0000\u0019\u00b2\u0001\u0000\u0000\u0000\u001b\u00b4\u0001\u0000"+
		"\u0000\u0000\u001d\u00b6\u0001\u0000\u0000\u0000\u001f\u00c0\u0001\u0000"+
		"\u0000\u0000!\u00c3\u0001\u0000\u0000\u0000#\u00cd\u0001\u0000\u0000\u0000"+
		"%\u00d0\u0001\u0000\u0000\u0000\'\u00f9\u0001\u0000\u0000\u0000)\u0101"+
		"\u0001\u0000\u0000\u0000+\u010a\u0001\u0000\u0000\u0000-\u0116\u0001\u0000"+
		"\u0000\u0000/\u0127\u0001\u0000\u0000\u00001\u013c\u0001\u0000\u0000\u0000"+
		"3\u0142\u0001\u0000\u0000\u00005\u0151\u0001\u0000\u0000\u00007\u0158"+
		"\u0001\u0000\u0000\u00009\u015e\u0001\u0000\u0000\u0000;\u016b\u0001\u0000"+
		"\u0000\u0000=\u0173\u0001\u0000\u0000\u0000?\u0189\u0001\u0000\u0000\u0000"+
		"A\u0190\u0001\u0000\u0000\u0000C\u0196\u0001\u0000\u0000\u0000E\u019d"+
		"\u0001\u0000\u0000\u0000G\u01ad\u0001\u0000\u0000\u0000I\u01b5\u0001\u0000"+
		"\u0000\u0000K\u01bb\u0001\u0000\u0000\u0000M\u01c6\u0001\u0000\u0000\u0000"+
		"O\u01ce\u0001\u0000\u0000\u0000Q\u01d6\u0001\u0000\u0000\u0000S\u01d9"+
		"\u0001\u0000\u0000\u0000U\u01dd\u0001\u0000\u0000\u0000W\u01ef\u0001\u0000"+
		"\u0000\u0000Y\u01f5\u0001\u0000\u0000\u0000[\u01f9\u0001\u0000\u0000\u0000"+
		"]\u01fd\u0001\u0000\u0000\u0000_\u0202\u0001\u0000\u0000\u0000a\u0208"+
		"\u0001\u0000\u0000\u0000c\u020c\u0001\u0000\u0000\u0000e\u0211\u0001\u0000"+
		"\u0000\u0000g\u0220\u0001\u0000\u0000\u0000i\u0222\u0001\u0000\u0000\u0000"+
		"k\u0225\u0001\u0000\u0000\u0000m\u022b\u0001\u0000\u0000\u0000o\u022f"+
		"\u0001\u0000\u0000\u0000q\u0234\u0001\u0000\u0000\u0000s\u023a\u0001\u0000"+
		"\u0000\u0000u\u023e\u0001\u0000\u0000\u0000w\u0242\u0001\u0000\u0000\u0000"+
		"y\u0247\u0001\u0000\u0000\u0000{\u024d\u0001\u0000\u0000\u0000}\u0252"+
		"\u0001\u0000\u0000\u0000\u007f\u0258\u0001\u0000\u0000\u0000\u0081\u025d"+
		"\u0001\u0000\u0000\u0000\u0083\u0263\u0001\u0000\u0000\u0000\u0085\u0088"+
		"\u0003\u000b\u0002\u0000\u0086\u0088\u0003\t\u0001\u0000\u0087\u0085\u0001"+
		"\u0000\u0000\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0088\b\u0001\u0000"+
		"\u0000\u0000\u0089\u008a\u0002AZ\u0000\u008a\n\u0001\u0000\u0000\u0000"+
		"\u008b\u008c\u0002az\u0000\u008c\f\u0001\u0000\u0000\u0000\u008d\u008e"+
		"\u000209\u0000\u008e\u000e\u0001\u0000\u0000\u0000\u008f\u0091\u0003\r"+
		"\u0003\u0000\u0090\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000"+
		"\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000"+
		"\u0000\u0000\u0093\u009a\u0001\u0000\u0000\u0000\u0094\u0096\u0005.\u0000"+
		"\u0000\u0095\u0097\u0003\r\u0003\u0000\u0096\u0095\u0001\u0000\u0000\u0000"+
		"\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000"+
		"\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u009b\u0001\u0000\u0000\u0000"+
		"\u009a\u0094\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000"+
		"\u009b\u0010\u0001\u0000\u0000\u0000\u009c\u009e\u0007\u0000\u0000\u0000"+
		"\u009d\u009c\u0001\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000"+
		"\u009f\u009d\u0001\u0000\u0000\u0000\u009f\u00a0\u0001\u0000\u0000\u0000"+
		"\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a2\u0006\u0005\u0000\u0000"+
		"\u00a2\u0012\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005{\u0000\u0000\u00a4"+
		"\u0014\u0001\u0000\u0000\u0000\u00a5\u00a6\u0005}\u0000\u0000\u00a6\u0016"+
		"\u0001\u0000\u0000\u0000\u00a7\u00ad\u0005\'\u0000\u0000\u00a8\u00ac\b"+
		"\u0001\u0000\u0000\u00a9\u00aa\u0005\\\u0000\u0000\u00aa\u00ac\u0005\'"+
		"\u0000\u0000\u00ab\u00a8\u0001\u0000\u0000\u0000\u00ab\u00a9\u0001\u0000"+
		"\u0000\u0000\u00ac\u00af\u0001\u0000\u0000\u0000\u00ad\u00ab\u0001\u0000"+
		"\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00b0\u0001\u0000"+
		"\u0000\u0000\u00af\u00ad\u0001\u0000\u0000\u0000\u00b0\u00b1\u0005\'\u0000"+
		"\u0000\u00b1\u0018\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005,\u0000\u0000"+
		"\u00b3\u001a\u0001\u0000\u0000\u0000\u00b4\u00b5\u0005:\u0000\u0000\u00b5"+
		"\u001c\u0001\u0000\u0000\u0000\u00b6\u00bb\u0005\"\u0000\u0000\u00b7\u00ba"+
		"\b\u0002\u0000\u0000\u00b8\u00ba\u0003\u001f\f\u0000\u00b9\u00b7\u0001"+
		"\u0000\u0000\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00ba\u00bd\u0001"+
		"\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001"+
		"\u0000\u0000\u0000\u00bc\u00be\u0001\u0000\u0000\u0000\u00bd\u00bb\u0001"+
		"\u0000\u0000\u0000\u00be\u00bf\u0005\"\u0000\u0000\u00bf\u001e\u0001\u0000"+
		"\u0000\u0000\u00c0\u00c1\u0005\\\u0000\u0000\u00c1\u00c2\u0007\u0003\u0000"+
		"\u0000\u00c2 \u0001\u0000\u0000\u0000\u00c3\u00c8\u0005*\u0000\u0000\u00c4"+
		"\u00c7\b\u0004\u0000\u0000\u00c5\u00c7\u0003#\u000e\u0000\u00c6\u00c4"+
		"\u0001\u0000\u0000\u0000\u00c6\u00c5\u0001\u0000\u0000\u0000\u00c7\u00ca"+
		"\u0001\u0000\u0000\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c8\u00c9"+
		"\u0001\u0000\u0000\u0000\u00c9\u00cb\u0001\u0000\u0000\u0000\u00ca\u00c8"+
		"\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005*\u0000\u0000\u00cc\"\u0001"+
		"\u0000\u0000\u0000\u00cd\u00ce\u0005\\\u0000\u0000\u00ce\u00cf\u0007\u0005"+
		"\u0000\u0000\u00cf$\u0001\u0000\u0000\u0000\u00d0\u00d1\u0005#\u0000\u0000"+
		"\u00d1\u00d2\u0005!\u0000\u0000\u00d2\u00d4\u0001\u0000\u0000\u0000\u00d3"+
		"\u00d5\u0005 \u0000\u0000\u00d4\u00d3\u0001\u0000\u0000\u0000\u00d5\u00d6"+
		"\u0001\u0000\u0000\u0000\u00d6\u00d4\u0001\u0000\u0000\u0000\u00d6\u00d7"+
		"\u0001\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00d9"+
		"\u0005C\u0000\u0000\u00d9\u00da\u0005a\u0000\u0000\u00da\u00db\u0005t"+
		"\u0000\u0000\u00db\u00dc\u0005r\u0000\u0000\u00dc\u00dd\u0005o\u0000\u0000"+
		"\u00dd\u00de\u0005b\u0000\u0000\u00de\u00df\u0005a\u0000\u0000\u00df\u00e0"+
		"\u0005t\u0000\u0000\u00e0\u00e1\u0005 \u0000\u0000\u00e1\u00e2\u0005L"+
		"\u0000\u0000\u00e2\u00e3\u0005a\u0000\u0000\u00e3\u00e4\u0005n\u0000\u0000"+
		"\u00e4\u00e5\u0005g\u0000\u0000\u00e5\u00e6\u0005u\u0000\u0000\u00e6\u00e7"+
		"\u0005a\u0000\u0000\u00e7\u00e8\u0005g\u0000\u0000\u00e8\u00e9\u0005e"+
		"\u0000\u0000\u00e9\u00ea\u0005 \u0000\u0000\u00ea\u00eb\u0005V\u0000\u0000"+
		"\u00eb\u00ec\u0005e\u0000\u0000\u00ec\u00ed\u0005r\u0000\u0000\u00ed\u00ee"+
		"\u0005s\u0000\u0000\u00ee\u00ef\u0005i\u0000\u0000\u00ef\u00f0\u0005o"+
		"\u0000\u0000\u00f0\u00f1\u0005n\u0000\u0000\u00f1\u00f3\u0001\u0000\u0000"+
		"\u0000\u00f2\u00f4\u0005 \u0000\u0000\u00f3\u00f2\u0001\u0000\u0000\u0000"+
		"\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f3\u0001\u0000\u0000\u0000"+
		"\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000\u0000\u0000"+
		"\u00f7\u00f8\u0003\u000f\u0004\u0000\u00f8&\u0001\u0000\u0000\u0000\u00f9"+
		"\u00fa\u0005P\u0000\u0000\u00fa\u00fb\u0005r\u0000\u0000\u00fb\u00fc\u0005"+
		"o\u0000\u0000\u00fc\u00fd\u0005g\u0000\u0000\u00fd\u00fe\u0005r\u0000"+
		"\u0000\u00fe\u00ff\u0005a\u0000\u0000\u00ff\u0100\u0005m\u0000\u0000\u0100"+
		"(\u0001\u0000\u0000\u0000\u0101\u0102\u0005M\u0000\u0000\u0102\u0103\u0005"+
		"e\u0000\u0000\u0103\u0104\u0005t\u0000\u0000\u0104\u0105\u0005a\u0000"+
		"\u0000\u0105\u0106\u0005d\u0000\u0000\u0106\u0107\u0005a\u0000\u0000\u0107"+
		"\u0108\u0005t\u0000\u0000\u0108\u0109\u0005a\u0000\u0000\u0109*\u0001"+
		"\u0000\u0000\u0000\u010a\u010b\u0005D\u0000\u0000\u010b\u010c\u0005e\u0000"+
		"\u0000\u010c\u010d\u0005s\u0000\u0000\u010d\u010e\u0005c\u0000\u0000\u010e"+
		"\u010f\u0005r\u0000\u0000\u010f\u0110\u0005i\u0000\u0000\u0110\u0111\u0005"+
		"p\u0000\u0000\u0111\u0112\u0005t\u0000\u0000\u0112\u0113\u0005i\u0000"+
		"\u0000\u0113\u0114\u0005o\u0000\u0000\u0114\u0115\u0005n\u0000\u0000\u0115"+
		",\u0001\u0000\u0000\u0000\u0116\u0117\u0005C\u0000\u0000\u0117\u0118\u0005"+
		"a\u0000\u0000\u0118\u0119\u0005t\u0000\u0000\u0119\u011a\u0005r\u0000"+
		"\u0000\u011a\u011b\u0005o\u0000\u0000\u011b\u011c\u0005b\u0000\u0000\u011c"+
		"\u011d\u0005a\u0000\u0000\u011d\u011e\u0005t\u0000\u0000\u011e\u011f\u0005"+
		" \u0000\u0000\u011f\u0120\u0005v\u0000\u0000\u0120\u0121\u0005e\u0000"+
		"\u0000\u0121\u0122\u0005r\u0000\u0000\u0122\u0123\u0005s\u0000\u0000\u0123"+
		"\u0124\u0005i\u0000\u0000\u0124\u0125\u0005o\u0000\u0000\u0125\u0126\u0005"+
		"n\u0000\u0000\u0126.\u0001\u0000\u0000\u0000\u0127\u0128\u0005C\u0000"+
		"\u0000\u0128\u0129\u0005a\u0000\u0000\u0129\u012a\u0005t\u0000\u0000\u012a"+
		"\u012b\u0005r\u0000\u0000\u012b\u012c\u0005o\u0000\u0000\u012c\u012d\u0005"+
		"b\u0000\u0000\u012d\u012e\u0005a\u0000\u0000\u012e\u012f\u0005t\u0000"+
		"\u0000\u012f\u0130\u0005 \u0000\u0000\u0130\u0131\u0005a\u0000\u0000\u0131"+
		"\u0132\u0005p\u0000\u0000\u0132\u0133\u0005p\u0000\u0000\u0133\u0134\u0005"+
		" \u0000\u0000\u0134\u0135\u0005v\u0000\u0000\u0135\u0136\u0005e\u0000"+
		"\u0000\u0136\u0137\u0005r\u0000\u0000\u0137\u0138\u0005s\u0000\u0000\u0138"+
		"\u0139\u0005i\u0000\u0000\u0139\u013a\u0005o\u0000\u0000\u013a\u013b\u0005"+
		"n\u0000\u0000\u013b0\u0001\u0000\u0000\u0000\u013c\u013d\u0005S\u0000"+
		"\u0000\u013d\u013e\u0005t\u0000\u0000\u013e\u013f\u0005a\u0000\u0000\u013f"+
		"\u0140\u0005g\u0000\u0000\u0140\u0141\u0005e\u0000\u0000\u01412\u0001"+
		"\u0000\u0000\u0000\u0142\u0143\u0005L\u0000\u0000\u0143\u0144\u0005a\u0000"+
		"\u0000\u0144\u0145\u0005n\u0000\u0000\u0145\u0146\u0005d\u0000\u0000\u0146"+
		"\u0147\u0005s\u0000\u0000\u0147\u0148\u0005c\u0000\u0000\u0148\u0149\u0005"+
		"a\u0000\u0000\u0149\u014a\u0005p\u0000\u0000\u014a\u014b\u0005e\u0000"+
		"\u0000\u014b\u014c\u0005 \u0000\u0000\u014c\u014d\u0005m\u0000\u0000\u014d"+
		"\u014e\u0005o\u0000\u0000\u014e\u014f\u0005d\u0000\u0000\u014f\u0150\u0005"+
		"e\u0000\u0000\u01504\u0001\u0000\u0000\u0000\u0151\u0152\u0005H\u0000"+
		"\u0000\u0152\u0153\u0005e\u0000\u0000\u0153\u0154\u0005i\u0000\u0000\u0154"+
		"\u0155\u0005g\u0000\u0000\u0155\u0156\u0005h\u0000\u0000\u0156\u0157\u0005"+
		"t\u0000\u0000\u01576\u0001\u0000\u0000\u0000\u0158\u0159\u0005W\u0000"+
		"\u0000\u0159\u015a\u0005i\u0000\u0000\u015a\u015b\u0005d\u0000\u0000\u015b"+
		"\u015c\u0005t\u0000\u0000\u015c\u015d\u0005h\u0000\u0000\u015d8\u0001"+
		"\u0000\u0000\u0000\u015e\u015f\u0005D\u0000\u0000\u015f\u0160\u0005i\u0000"+
		"\u0000\u0160\u0161\u0005s\u0000\u0000\u0161\u0162\u0005p\u0000\u0000\u0162"+
		"\u0163\u0005l\u0000\u0000\u0163\u0164\u0005a\u0000\u0000\u0164\u0165\u0005"+
		"y\u0000\u0000\u0165\u0166\u0005 \u0000\u0000\u0166\u0167\u0005m\u0000"+
		"\u0000\u0167\u0168\u0005o\u0000\u0000\u0168\u0169\u0005d\u0000\u0000\u0169"+
		"\u016a\u0005e\u0000\u0000\u016a:\u0001\u0000\u0000\u0000\u016b\u016c\u0005"+
		"G\u0000\u0000\u016c\u016d\u0005l\u0000\u0000\u016d\u016e\u0005o\u0000"+
		"\u0000\u016e\u016f\u0005b\u0000\u0000\u016f\u0170\u0005a\u0000\u0000\u0170"+
		"\u0171\u0005l\u0000\u0000\u0171\u0172\u0005s\u0000\u0000\u0172<\u0001"+
		"\u0000\u0000\u0000\u0173\u0174\u0005M\u0000\u0000\u0174\u0175\u0005u\u0000"+
		"\u0000\u0175\u0176\u0005l\u0000\u0000\u0176\u0177\u0005t\u0000\u0000\u0177"+
		"\u0178\u0005i\u0000\u0000\u0178\u0179\u0005p\u0000\u0000\u0179\u017a\u0005"+
		"l\u0000\u0000\u017a\u017b\u0005a\u0000\u0000\u017b\u017c\u0005y\u0000"+
		"\u0000\u017c\u017d\u0005e\u0000\u0000\u017d\u017e\u0005r\u0000\u0000\u017e"+
		"\u017f\u0005 \u0000\u0000\u017f\u0180\u0005v\u0000\u0000\u0180\u0181\u0005"+
		"a\u0000\u0000\u0181\u0182\u0005r\u0000\u0000\u0182\u0183\u0005i\u0000"+
		"\u0000\u0183\u0184\u0005a\u0000\u0000\u0184\u0185\u0005b\u0000\u0000\u0185"+
		"\u0186\u0005l\u0000\u0000\u0186\u0187\u0005e\u0000\u0000\u0187\u0188\u0005"+
		"s\u0000\u0000\u0188>\u0001\u0000\u0000\u0000\u0189\u018a\u0005L\u0000"+
		"\u0000\u018a\u018b\u0005o\u0000\u0000\u018b\u018c\u0005c\u0000\u0000\u018c"+
		"\u018d\u0005a\u0000\u0000\u018d\u018e\u0005l\u0000\u0000\u018e\u018f\u0005"+
		"s\u0000\u0000\u018f@\u0001\u0000\u0000\u0000\u0190\u0191\u0005L\u0000"+
		"\u0000\u0191\u0192\u0005o\u0000\u0000\u0192\u0193\u0005o\u0000\u0000\u0193"+
		"\u0194\u0005k\u0000\u0000\u0194\u0195\u0005s\u0000\u0000\u0195B\u0001"+
		"\u0000\u0000\u0000\u0196\u0197\u0005S\u0000\u0000\u0197\u0198\u0005o\u0000"+
		"\u0000\u0198\u0199\u0005u\u0000\u0000\u0199\u019a\u0005n\u0000\u0000\u019a"+
		"\u019b\u0005d\u0000\u0000\u019b\u019c\u0005s\u0000\u0000\u019cD\u0001"+
		"\u0000\u0000\u0000\u019d\u019e\u0005A\u0000\u0000\u019e\u019f\u0005c\u0000"+
		"\u0000\u019f\u01a0\u0005t\u0000\u0000\u01a0\u01a1\u0005o\u0000\u0000\u01a1"+
		"\u01a2\u0005r\u0000\u0000\u01a2\u01a3\u0005 \u0000\u0000\u01a3\u01a4\u0005"+
		"o\u0000\u0000\u01a4\u01a5\u0005r\u0000\u0000\u01a5\u01a6\u0005 \u0000"+
		"\u0000\u01a6\u01a7\u0005o\u0000\u0000\u01a7\u01a8\u0005b\u0000\u0000\u01a8"+
		"\u01a9\u0005j\u0000\u0000\u01a9\u01aa\u0005e\u0000\u0000\u01aa\u01ab\u0005"+
		"c\u0000\u0000\u01ab\u01ac\u0005t\u0000\u0000\u01acF\u0001\u0000\u0000"+
		"\u0000\u01ad\u01ae\u0005o\u0000\u0000\u01ae\u01af\u0005f\u0000\u0000\u01af"+
		"\u01b0\u0005 \u0000\u0000\u01b0\u01b1\u0005t\u0000\u0000\u01b1\u01b2\u0005"+
		"y\u0000\u0000\u01b2\u01b3\u0005p\u0000\u0000\u01b3\u01b4\u0005e\u0000"+
		"\u0000\u01b4H\u0001\u0000\u0000\u0000\u01b5\u01b6\u0005S\u0000\u0000\u01b6"+
		"\u01b7\u0005c\u0000\u0000\u01b7\u01b8\u0005e\u0000\u0000\u01b8\u01b9\u0005"+
		"n\u0000\u0000\u01b9\u01ba\u0005e\u0000\u0000\u01baJ\u0001\u0000\u0000"+
		"\u0000\u01bb\u01bc\u0005B\u0000\u0000\u01bc\u01bd\u0005a\u0000\u0000\u01bd"+
		"\u01be\u0005c\u0000\u0000\u01be\u01bf\u0005k\u0000\u0000\u01bf\u01c0\u0005"+
		"g\u0000\u0000\u01c0\u01c1\u0005r\u0000\u0000\u01c1\u01c2\u0005o\u0000"+
		"\u0000\u01c2\u01c3\u0005u\u0000\u0000\u01c3\u01c4\u0005n\u0000\u0000\u01c4"+
		"\u01c5\u0005d\u0000\u0000\u01c5L\u0001\u0000\u0000\u0000\u01c6\u01c7\u0005"+
		"S\u0000\u0000\u01c7\u01c8\u0005c\u0000\u0000\u01c8\u01c9\u0005r\u0000"+
		"\u0000\u01c9\u01ca\u0005i\u0000\u0000\u01ca\u01cb\u0005p\u0000\u0000\u01cb"+
		"\u01cc\u0005t\u0000\u0000\u01cc\u01cd\u0005s\u0000\u0000\u01cdN\u0001"+
		"\u0000\u0000\u0000\u01ce\u01d3\u0005#\u0000\u0000\u01cf\u01d2\b\u0006"+
		"\u0000\u0000\u01d0\u01d2\u0003Q%\u0000\u01d1\u01cf\u0001\u0000\u0000\u0000"+
		"\u01d1\u01d0\u0001\u0000\u0000\u0000\u01d2\u01d5\u0001\u0000\u0000\u0000"+
		"\u01d3\u01d1\u0001\u0000\u0000\u0000\u01d3\u01d4\u0001\u0000\u0000\u0000"+
		"\u01d4P\u0001\u0000\u0000\u0000\u01d5\u01d3\u0001\u0000\u0000\u0000\u01d6"+
		"\u01d7\u0005\\\u0000\u0000\u01d7\u01d8\u0007\u0007\u0000\u0000\u01d8R"+
		"\u0001\u0000\u0000\u0000\u01d9\u01da\u0005/\u0000\u0000\u01da\u01db\u0005"+
		"/\u0000\u0000\u01dbT\u0001\u0000\u0000\u0000\u01dc\u01de\u0003\u0007\u0000"+
		"\u0000\u01dd\u01dc\u0001\u0000\u0000\u0000\u01de\u01df\u0001\u0000\u0000"+
		"\u0000\u01df\u01dd\u0001\u0000\u0000\u0000\u01df\u01e0\u0001\u0000\u0000"+
		"\u0000\u01e0\u01e9\u0001\u0000\u0000\u0000\u01e1\u01e3\u0005 \u0000\u0000"+
		"\u01e2\u01e4\u0003\u0007\u0000\u0000\u01e3\u01e2\u0001\u0000\u0000\u0000"+
		"\u01e4\u01e5\u0001\u0000\u0000\u0000\u01e5\u01e3\u0001\u0000\u0000\u0000"+
		"\u01e5\u01e6\u0001\u0000\u0000\u0000\u01e6\u01e8\u0001\u0000\u0000\u0000"+
		"\u01e7\u01e1\u0001\u0000\u0000\u0000\u01e8\u01eb\u0001\u0000\u0000\u0000"+
		"\u01e9\u01e7\u0001\u0000\u0000\u0000\u01e9\u01ea\u0001\u0000\u0000\u0000"+
		"\u01ea\u01ec\u0001\u0000\u0000\u0000\u01eb\u01e9\u0001\u0000\u0000\u0000"+
		"\u01ec\u01ed\u0006\'\u0001\u0000\u01edV\u0001\u0000\u0000\u0000\u01ee"+
		"\u01f0\u0007\u0000\u0000\u0000\u01ef\u01ee\u0001\u0000\u0000\u0000\u01f0"+
		"\u01f1\u0001\u0000\u0000\u0000\u01f1\u01ef\u0001\u0000\u0000\u0000\u01f1"+
		"\u01f2\u0001\u0000\u0000\u0000\u01f2\u01f3\u0001\u0000\u0000\u0000\u01f3"+
		"\u01f4\u0006(\u0000\u0000\u01f4X\u0001\u0000\u0000\u0000\u01f5\u01f6\u0005"+
		"(\u0000\u0000\u01f6\u01f7\u0001\u0000\u0000\u0000\u01f7\u01f8\u0006)\u0002"+
		"\u0000\u01f8Z\u0001\u0000\u0000\u0000\u01f9\u01fa\u0005;\u0000\u0000\u01fa"+
		"\u01fb\u0001\u0000\u0000\u0000\u01fb\u01fc\u0006*\u0003\u0000\u01fc\\"+
		"\u0001\u0000\u0000\u0000\u01fd\u01fe\u0005{\u0000\u0000\u01fe\u01ff\u0001"+
		"\u0000\u0000\u0000\u01ff\u0200\u0006+\u0003\u0000\u0200^\u0001\u0000\u0000"+
		"\u0000\u0201\u0203\u0007\u0000\u0000\u0000\u0202\u0201\u0001\u0000\u0000"+
		"\u0000\u0203\u0204\u0001\u0000\u0000\u0000\u0204\u0202\u0001\u0000\u0000"+
		"\u0000\u0204\u0205\u0001\u0000\u0000\u0000\u0205\u0206\u0001\u0000\u0000"+
		"\u0000\u0206\u0207\u0006,\u0000\u0000\u0207`\u0001\u0000\u0000\u0000\u0208"+
		"\u0209\u0005(\u0000\u0000\u0209\u020a\u0001\u0000\u0000\u0000\u020a\u020b"+
		"\u0006-\u0004\u0000\u020bb\u0001\u0000\u0000\u0000\u020c\u020d\u0005)"+
		"\u0000\u0000\u020d\u020e\u0001\u0000\u0000\u0000\u020e\u020f\u0006.\u0001"+
		"\u0000\u020fd\u0001\u0000\u0000\u0000\u0210\u0212\u0003\u0007\u0000\u0000"+
		"\u0211\u0210\u0001\u0000\u0000\u0000\u0212\u0213\u0001\u0000\u0000\u0000"+
		"\u0213\u0211\u0001\u0000\u0000\u0000\u0213\u0214\u0001\u0000\u0000\u0000"+
		"\u0214\u021d\u0001\u0000\u0000\u0000\u0215\u0217\u0005 \u0000\u0000\u0216"+
		"\u0218\u0003\u0007\u0000\u0000\u0217\u0216\u0001\u0000\u0000\u0000\u0218"+
		"\u0219\u0001\u0000\u0000\u0000\u0219\u0217\u0001\u0000\u0000\u0000\u0219"+
		"\u021a\u0001\u0000\u0000\u0000\u021a\u021c\u0001\u0000\u0000\u0000\u021b"+
		"\u0215\u0001\u0000\u0000\u0000\u021c\u021f\u0001\u0000\u0000\u0000\u021d"+
		"\u021b\u0001\u0000\u0000\u0000\u021d\u021e\u0001\u0000\u0000\u0000\u021e"+
		"f\u0001\u0000\u0000\u0000\u021f\u021d\u0001\u0000\u0000\u0000\u0220\u0221"+
		"\u0005:\u0000\u0000\u0221h\u0001\u0000\u0000\u0000\u0222\u0223\u0005,"+
		"\u0000\u0000\u0223j\u0001\u0000\u0000\u0000\u0224\u0226\u0007\u0000\u0000"+
		"\u0000\u0225\u0224\u0001\u0000\u0000\u0000\u0226\u0227\u0001\u0000\u0000"+
		"\u0000\u0227\u0225\u0001\u0000\u0000\u0000\u0227\u0228\u0001\u0000\u0000"+
		"\u0000\u0228\u0229\u0001\u0000\u0000\u0000\u0229\u022a\u00062\u0000\u0000"+
		"\u022al\u0001\u0000\u0000\u0000\u022b\u022c\u0005)\u0000\u0000\u022c\u022d"+
		"\u0001\u0000\u0000\u0000\u022d\u022e\u00063\u0005\u0000\u022en\u0001\u0000"+
		"\u0000\u0000\u022f\u0230\u0005(\u0000\u0000\u0230\u0231\u0001\u0000\u0000"+
		"\u0000\u0231\u0232\u00064\u0004\u0000\u0232p\u0001\u0000\u0000\u0000\u0233"+
		"\u0235\b\b\u0000\u0000\u0234\u0233\u0001\u0000\u0000\u0000\u0235\u0236"+
		"\u0001\u0000\u0000\u0000\u0236\u0234\u0001\u0000\u0000\u0000\u0236\u0237"+
		"\u0001\u0000\u0000\u0000\u0237\u0238\u0001\u0000\u0000\u0000\u0238\u0239"+
		"\u00065\u0000\u0000\u0239r\u0001\u0000\u0000\u0000\u023a\u023b\u0005\'"+
		"\u0000\u0000\u023b\u023c\u0001\u0000\u0000\u0000\u023c\u023d\u00066\u0006"+
		"\u0000\u023dt\u0001\u0000\u0000\u0000\u023e\u023f\u0005\"\u0000\u0000"+
		"\u023f\u0240\u0001\u0000\u0000\u0000\u0240\u0241\u00067\u0007\u0000\u0241"+
		"v\u0001\u0000\u0000\u0000\u0242\u0243\u0005[\u0000\u0000\u0243\u0244\u0001"+
		"\u0000\u0000\u0000\u0244\u0245\u00068\b\u0000\u0245x\u0001\u0000\u0000"+
		"\u0000\u0246\u0248\b\u0001\u0000\u0000\u0247\u0246\u0001\u0000\u0000\u0000"+
		"\u0248\u0249\u0001\u0000\u0000\u0000\u0249\u0247\u0001\u0000\u0000\u0000"+
		"\u0249\u024a\u0001\u0000\u0000\u0000\u024a\u024b\u0001\u0000\u0000\u0000"+
		"\u024b\u024c\u00069\u0000\u0000\u024cz\u0001\u0000\u0000\u0000\u024d\u024e"+
		"\u0005\'\u0000\u0000\u024e\u024f\u0001\u0000\u0000\u0000\u024f\u0250\u0006"+
		":\u0005\u0000\u0250|\u0001\u0000\u0000\u0000\u0251\u0253\b\t\u0000\u0000"+
		"\u0252\u0251\u0001\u0000\u0000\u0000\u0253\u0254\u0001\u0000\u0000\u0000"+
		"\u0254\u0252\u0001\u0000\u0000\u0000\u0254\u0255\u0001\u0000\u0000\u0000"+
		"\u0255\u0256\u0001\u0000\u0000\u0000\u0256\u0257\u0006;\u0000\u0000\u0257"+
		"~\u0001\u0000\u0000\u0000\u0258\u0259\u0005\"\u0000\u0000\u0259\u025a"+
		"\u0001\u0000\u0000\u0000\u025a\u025b\u0006<\u0005\u0000\u025b\u0080\u0001"+
		"\u0000\u0000\u0000\u025c\u025e\b\n\u0000\u0000\u025d\u025c\u0001\u0000"+
		"\u0000\u0000\u025e\u025f\u0001\u0000\u0000\u0000\u025f\u025d\u0001\u0000"+
		"\u0000\u0000\u025f\u0260\u0001\u0000\u0000\u0000\u0260\u0261\u0001\u0000"+
		"\u0000\u0000\u0261\u0262\u0006=\u0000\u0000\u0262\u0082\u0001\u0000\u0000"+
		"\u0000\u0263\u0264\u0005]\u0000\u0000\u0264\u0265\u0001\u0000\u0000\u0000"+
		"\u0265\u0266\u0006>\u0005\u0000\u0266\u0084\u0001\u0000\u0000\u0000#\u0000"+
		"\u0001\u0002\u0003\u0004\u0005\u0006\u0087\u0092\u0098\u009a\u009f\u00ab"+
		"\u00ad\u00b9\u00bb\u00c6\u00c8\u00d6\u00f5\u01d1\u01d3\u01df\u01e5\u01e9"+
		"\u01f1\u0204\u0213\u0219\u021d\u0227\u0236\u0249\u0254\u025f\t\u0006\u0000"+
		"\u0000\u0002\u0001\u0000\u0002\u0002\u0000\u0002\u0000\u0000\u0005\u0003"+
		"\u0000\u0004\u0000\u0000\u0005\u0004\u0000\u0005\u0005\u0000\u0005\u0006"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}