// Generated from ./CatrobatLanguageParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.project.antlr.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class CatrobatLanguageParser extends Parser {
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
		RULE_program = 0, RULE_programHeader = 1, RULE_programBody = 2, RULE_metadata = 3, 
		RULE_metadataContent = 4, RULE_description = 5, RULE_catrobatVersion = 6, 
		RULE_catrobatAppVersion = 7, RULE_stage = 8, RULE_stageContent = 9, RULE_landscapeMode = 10, 
		RULE_height = 11, RULE_width = 12, RULE_displayMode = 13, RULE_globals = 14, 
		RULE_multiplayerVariables = 15, RULE_variableOrListDeclaration = 16, RULE_variableDeclaration = 17, 
		RULE_scene = 18, RULE_background = 19, RULE_actor = 20, RULE_actorContent = 21, 
		RULE_localVariables = 22, RULE_looks = 23, RULE_sounds = 24, RULE_looksAndSoundsContent = 25, 
		RULE_scripts = 26, RULE_brick_defintion = 27, RULE_brick_with_body = 28, 
		RULE_brick_invocation = 29, RULE_brick_condition = 30, RULE_arg_list = 31, 
		RULE_argument = 32, RULE_formula = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "programHeader", "programBody", "metadata", "metadataContent", 
			"description", "catrobatVersion", "catrobatAppVersion", "stage", "stageContent", 
			"landscapeMode", "height", "width", "displayMode", "globals", "multiplayerVariables", 
			"variableOrListDeclaration", "variableDeclaration", "scene", "background", 
			"actor", "actorContent", "localVariables", "looks", "sounds", "looksAndSoundsContent", 
			"scripts", "brick_defintion", "brick_with_body", "brick_invocation", 
			"brick_condition", "arg_list", "argument", "formula"
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

	@Override
	public String getGrammarFileName() { return "CatrobatLanguageParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CatrobatLanguageParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public ProgramHeaderContext programHeader() {
			return getRuleContext(ProgramHeaderContext.class,0);
		}
		public ProgramBodyContext programBody() {
			return getRuleContext(ProgramBodyContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			programHeader();
			setState(69);
			programBody();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramHeaderContext extends ParserRuleContext {
		public TerminalNode PROGRAM_START() { return getToken(CatrobatLanguageParser.PROGRAM_START, 0); }
		public ProgramHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_programHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterProgramHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitProgramHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitProgramHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramHeaderContext programHeader() throws RecognitionException {
		ProgramHeaderContext _localctx = new ProgramHeaderContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_programHeader);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(PROGRAM_START);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramBodyContext extends ParserRuleContext {
		public TerminalNode PROGRAM() { return getToken(CatrobatLanguageParser.PROGRAM, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<MetadataContext> metadata() {
			return getRuleContexts(MetadataContext.class);
		}
		public MetadataContext metadata(int i) {
			return getRuleContext(MetadataContext.class,i);
		}
		public List<StageContext> stage() {
			return getRuleContexts(StageContext.class);
		}
		public StageContext stage(int i) {
			return getRuleContext(StageContext.class,i);
		}
		public List<GlobalsContext> globals() {
			return getRuleContexts(GlobalsContext.class);
		}
		public GlobalsContext globals(int i) {
			return getRuleContext(GlobalsContext.class,i);
		}
		public List<MultiplayerVariablesContext> multiplayerVariables() {
			return getRuleContexts(MultiplayerVariablesContext.class);
		}
		public MultiplayerVariablesContext multiplayerVariables(int i) {
			return getRuleContext(MultiplayerVariablesContext.class,i);
		}
		public List<SceneContext> scene() {
			return getRuleContexts(SceneContext.class);
		}
		public SceneContext scene(int i) {
			return getRuleContext(SceneContext.class,i);
		}
		public ProgramBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_programBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterProgramBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitProgramBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitProgramBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramBodyContext programBody() throws RecognitionException {
		ProgramBodyContext _localctx = new ProgramBodyContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_programBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(PROGRAM);
			setState(74);
			match(STRING);
			setState(75);
			match(CURLY_BRACKET_OPEN);
			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6361088L) != 0)) {
				{
				setState(80);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case METADATA:
					{
					setState(76);
					metadata();
					}
					break;
				case STAGE:
					{
					setState(77);
					stage();
					}
					break;
				case GLOBALS:
					{
					setState(78);
					globals();
					}
					break;
				case MULTIPLAYER_VARIABLES:
					{
					setState(79);
					multiplayerVariables();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(86); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(85);
				scene();
				}
				}
				setState(88); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCENE );
			setState(90);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MetadataContext extends ParserRuleContext {
		public TerminalNode METADATA() { return getToken(CatrobatLanguageParser.METADATA, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<MetadataContentContext> metadataContent() {
			return getRuleContexts(MetadataContentContext.class);
		}
		public MetadataContentContext metadataContent(int i) {
			return getRuleContext(MetadataContentContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CatrobatLanguageParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.SEPARATOR, i);
		}
		public MetadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitMetadata(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitMetadata(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MetadataContext metadata() throws RecognitionException {
		MetadataContext _localctx = new MetadataContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_metadata);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			match(METADATA);
			setState(93);
			match(CURLY_BRACKET_OPEN);
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 57344L) != 0)) {
				{
				setState(94);
				metadataContent();
				}
			}

			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(97);
				match(SEPARATOR);
				setState(98);
				metadataContent();
				}
				}
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(104);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MetadataContentContext extends ParserRuleContext {
		public DescriptionContext description() {
			return getRuleContext(DescriptionContext.class,0);
		}
		public CatrobatVersionContext catrobatVersion() {
			return getRuleContext(CatrobatVersionContext.class,0);
		}
		public CatrobatAppVersionContext catrobatAppVersion() {
			return getRuleContext(CatrobatAppVersionContext.class,0);
		}
		public MetadataContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadataContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterMetadataContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitMetadataContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitMetadataContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MetadataContentContext metadataContent() throws RecognitionException {
		MetadataContentContext _localctx = new MetadataContentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_metadataContent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DESCRIPTION:
				{
				setState(106);
				description();
				}
				break;
			case CATROBAT_VERSION:
				{
				setState(107);
				catrobatVersion();
				}
				break;
			case CATRPBAT_APP_VERSION:
				{
				setState(108);
				catrobatAppVersion();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DescriptionContext extends ParserRuleContext {
		public TerminalNode DESCRIPTION() { return getToken(CatrobatLanguageParser.DESCRIPTION, 0); }
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public DescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_description; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitDescription(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescriptionContext description() throws RecognitionException {
		DescriptionContext _localctx = new DescriptionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_description);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(DESCRIPTION);
			setState(112);
			match(COLON);
			setState(113);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CatrobatVersionContext extends ParserRuleContext {
		public TerminalNode CATROBAT_VERSION() { return getToken(CatrobatLanguageParser.CATROBAT_VERSION, 0); }
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public CatrobatVersionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catrobatVersion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterCatrobatVersion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitCatrobatVersion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitCatrobatVersion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CatrobatVersionContext catrobatVersion() throws RecognitionException {
		CatrobatVersionContext _localctx = new CatrobatVersionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_catrobatVersion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(CATROBAT_VERSION);
			setState(116);
			match(COLON);
			setState(117);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CatrobatAppVersionContext extends ParserRuleContext {
		public TerminalNode CATRPBAT_APP_VERSION() { return getToken(CatrobatLanguageParser.CATRPBAT_APP_VERSION, 0); }
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public CatrobatAppVersionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catrobatAppVersion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterCatrobatAppVersion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitCatrobatAppVersion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitCatrobatAppVersion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CatrobatAppVersionContext catrobatAppVersion() throws RecognitionException {
		CatrobatAppVersionContext _localctx = new CatrobatAppVersionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_catrobatAppVersion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(CATRPBAT_APP_VERSION);
			setState(120);
			match(COLON);
			setState(121);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StageContext extends ParserRuleContext {
		public TerminalNode STAGE() { return getToken(CatrobatLanguageParser.STAGE, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<StageContentContext> stageContent() {
			return getRuleContexts(StageContentContext.class);
		}
		public StageContentContext stageContent(int i) {
			return getRuleContext(StageContentContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CatrobatLanguageParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.SEPARATOR, i);
		}
		public StageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stage; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterStage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitStage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitStage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StageContext stage() throws RecognitionException {
		StageContext _localctx = new StageContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_stage);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(STAGE);
			setState(124);
			match(CURLY_BRACKET_OPEN);
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0)) {
				{
				setState(125);
				stageContent();
				}
			}

			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(128);
				match(SEPARATOR);
				setState(129);
				stageContent();
				}
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(135);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StageContentContext extends ParserRuleContext {
		public LandscapeModeContext landscapeMode() {
			return getRuleContext(LandscapeModeContext.class,0);
		}
		public HeightContext height() {
			return getRuleContext(HeightContext.class,0);
		}
		public WidthContext width() {
			return getRuleContext(WidthContext.class,0);
		}
		public DisplayModeContext displayMode() {
			return getRuleContext(DisplayModeContext.class,0);
		}
		public StageContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stageContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterStageContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitStageContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitStageContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StageContentContext stageContent() throws RecognitionException {
		StageContentContext _localctx = new StageContentContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_stageContent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANDSCAPE_MODE:
				{
				setState(137);
				landscapeMode();
				}
				break;
			case HEIGHT:
				{
				setState(138);
				height();
				}
				break;
			case WIDTH:
				{
				setState(139);
				width();
				}
				break;
			case DISPLAY_MODE:
				{
				setState(140);
				displayMode();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LandscapeModeContext extends ParserRuleContext {
		public TerminalNode LANDSCAPE_MODE() { return getToken(CatrobatLanguageParser.LANDSCAPE_MODE, 0); }
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public LandscapeModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_landscapeMode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterLandscapeMode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitLandscapeMode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitLandscapeMode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LandscapeModeContext landscapeMode() throws RecognitionException {
		LandscapeModeContext _localctx = new LandscapeModeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_landscapeMode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(LANDSCAPE_MODE);
			setState(144);
			match(COLON);
			setState(145);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HeightContext extends ParserRuleContext {
		public TerminalNode HEIGHT() { return getToken(CatrobatLanguageParser.HEIGHT, 0); }
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public HeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_height; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterHeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitHeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitHeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeightContext height() throws RecognitionException {
		HeightContext _localctx = new HeightContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_height);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(HEIGHT);
			setState(148);
			match(COLON);
			setState(149);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WidthContext extends ParserRuleContext {
		public TerminalNode WIDTH() { return getToken(CatrobatLanguageParser.WIDTH, 0); }
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public WidthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_width; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterWidth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitWidth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitWidth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WidthContext width() throws RecognitionException {
		WidthContext _localctx = new WidthContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_width);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(WIDTH);
			setState(152);
			match(COLON);
			setState(153);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DisplayModeContext extends ParserRuleContext {
		public TerminalNode DISPLAY_MODE() { return getToken(CatrobatLanguageParser.DISPLAY_MODE, 0); }
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public DisplayModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_displayMode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterDisplayMode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitDisplayMode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitDisplayMode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DisplayModeContext displayMode() throws RecognitionException {
		DisplayModeContext _localctx = new DisplayModeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_displayMode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(DISPLAY_MODE);
			setState(156);
			match(COLON);
			setState(157);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GlobalsContext extends ParserRuleContext {
		public TerminalNode GLOBALS() { return getToken(CatrobatLanguageParser.GLOBALS, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<VariableOrListDeclarationContext> variableOrListDeclaration() {
			return getRuleContexts(VariableOrListDeclarationContext.class);
		}
		public VariableOrListDeclarationContext variableOrListDeclaration(int i) {
			return getRuleContext(VariableOrListDeclarationContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CatrobatLanguageParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.SEPARATOR, i);
		}
		public GlobalsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_globals; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterGlobals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitGlobals(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitGlobals(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GlobalsContext globals() throws RecognitionException {
		GlobalsContext _localctx = new GlobalsContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_globals);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(GLOBALS);
			setState(160);
			match(CURLY_BRACKET_OPEN);
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(161);
				variableOrListDeclaration();
				}
			}

			setState(168);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(164);
				match(SEPARATOR);
				setState(165);
				variableOrListDeclaration();
				}
				}
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(171);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MultiplayerVariablesContext extends ParserRuleContext {
		public TerminalNode MULTIPLAYER_VARIABLES() { return getToken(CatrobatLanguageParser.MULTIPLAYER_VARIABLES, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<VariableDeclarationContext> variableDeclaration() {
			return getRuleContexts(VariableDeclarationContext.class);
		}
		public VariableDeclarationContext variableDeclaration(int i) {
			return getRuleContext(VariableDeclarationContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CatrobatLanguageParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.SEPARATOR, i);
		}
		public MultiplayerVariablesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplayerVariables; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterMultiplayerVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitMultiplayerVariables(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitMultiplayerVariables(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplayerVariablesContext multiplayerVariables() throws RecognitionException {
		MultiplayerVariablesContext _localctx = new MultiplayerVariablesContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_multiplayerVariables);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			match(MULTIPLAYER_VARIABLES);
			setState(174);
			match(CURLY_BRACKET_OPEN);
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF) {
				{
				setState(175);
				variableDeclaration();
				}
			}

			setState(182);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(178);
				match(SEPARATOR);
				setState(179);
				variableDeclaration();
				}
				}
				setState(184);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(185);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableOrListDeclarationContext extends ParserRuleContext {
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public TerminalNode LIST_REF() { return getToken(CatrobatLanguageParser.LIST_REF, 0); }
		public VariableOrListDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableOrListDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterVariableOrListDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitVariableOrListDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitVariableOrListDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableOrListDeclarationContext variableOrListDeclaration() throws RecognitionException {
		VariableOrListDeclarationContext _localctx = new VariableOrListDeclarationContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_variableOrListDeclaration);
		try {
			setState(189);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VARIABLE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(187);
				variableDeclaration();
				}
				break;
			case LIST_REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(188);
				match(LIST_REF);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableDeclarationContext extends ParserRuleContext {
		public TerminalNode VARIABLE_REF() { return getToken(CatrobatLanguageParser.VARIABLE_REF, 0); }
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_variableDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			match(VARIABLE_REF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SceneContext extends ParserRuleContext {
		public TerminalNode SCENE() { return getToken(CatrobatLanguageParser.SCENE, 0); }
		public TerminalNode STRING() { return getToken(CatrobatLanguageParser.STRING, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public BackgroundContext background() {
			return getRuleContext(BackgroundContext.class,0);
		}
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<ActorContext> actor() {
			return getRuleContexts(ActorContext.class);
		}
		public ActorContext actor(int i) {
			return getRuleContext(ActorContext.class,i);
		}
		public SceneContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scene; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterScene(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitScene(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitScene(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SceneContext scene() throws RecognitionException {
		SceneContext _localctx = new SceneContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_scene);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(SCENE);
			setState(194);
			match(STRING);
			setState(195);
			match(CURLY_BRACKET_OPEN);
			setState(196);
			background();
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ACTOR_OR_OBJECT) {
				{
				{
				setState(197);
				actor();
				}
				}
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(203);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BackgroundContext extends ParserRuleContext {
		public TerminalNode BACKGROUND() { return getToken(CatrobatLanguageParser.BACKGROUND, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public ActorContentContext actorContent() {
			return getRuleContext(ActorContentContext.class,0);
		}
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public BackgroundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_background; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBackground(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBackground(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBackground(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BackgroundContext background() throws RecognitionException {
		BackgroundContext _localctx = new BackgroundContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_background);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			match(BACKGROUND);
			setState(206);
			match(CURLY_BRACKET_OPEN);
			setState(207);
			actorContent();
			setState(208);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ActorContext extends ParserRuleContext {
		public TerminalNode ACTOR_OR_OBJECT() { return getToken(CatrobatLanguageParser.ACTOR_OR_OBJECT, 0); }
		public List<TerminalNode> STRING() { return getTokens(CatrobatLanguageParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(CatrobatLanguageParser.STRING, i);
		}
		public TerminalNode OF_TYPE() { return getToken(CatrobatLanguageParser.OF_TYPE, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public ActorContentContext actorContent() {
			return getRuleContext(ActorContentContext.class,0);
		}
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public ActorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterActor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitActor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitActor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActorContext actor() throws RecognitionException {
		ActorContext _localctx = new ActorContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_actor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			match(ACTOR_OR_OBJECT);
			setState(211);
			match(STRING);
			setState(212);
			match(OF_TYPE);
			setState(213);
			match(STRING);
			setState(214);
			match(CURLY_BRACKET_OPEN);
			setState(215);
			actorContent();
			setState(216);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ActorContentContext extends ParserRuleContext {
		public List<LocalVariablesContext> localVariables() {
			return getRuleContexts(LocalVariablesContext.class);
		}
		public LocalVariablesContext localVariables(int i) {
			return getRuleContext(LocalVariablesContext.class,i);
		}
		public List<LooksContext> looks() {
			return getRuleContexts(LooksContext.class);
		}
		public LooksContext looks(int i) {
			return getRuleContext(LooksContext.class,i);
		}
		public List<SoundsContext> sounds() {
			return getRuleContexts(SoundsContext.class);
		}
		public SoundsContext sounds(int i) {
			return getRuleContext(SoundsContext.class,i);
		}
		public List<ScriptsContext> scripts() {
			return getRuleContexts(ScriptsContext.class);
		}
		public ScriptsContext scripts(int i) {
			return getRuleContext(ScriptsContext.class,i);
		}
		public ActorContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actorContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterActorContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitActorContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitActorContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActorContentContext actorContent() throws RecognitionException {
		ActorContentContext _localctx = new ActorContentContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_actorContent);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(222);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LOCAL_VARIABLES:
					{
					setState(218);
					localVariables();
					}
					break;
				case LOOKS:
					{
					setState(219);
					looks();
					}
					break;
				case SOUNDS:
					{
					setState(220);
					sounds();
					}
					break;
				case SCRIPTS:
					{
					setState(221);
					scripts();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(224); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 1132462080L) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LocalVariablesContext extends ParserRuleContext {
		public TerminalNode LOCAL_VARIABLES() { return getToken(CatrobatLanguageParser.LOCAL_VARIABLES, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<VariableOrListDeclarationContext> variableOrListDeclaration() {
			return getRuleContexts(VariableOrListDeclarationContext.class);
		}
		public VariableOrListDeclarationContext variableOrListDeclaration(int i) {
			return getRuleContext(VariableOrListDeclarationContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CatrobatLanguageParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.SEPARATOR, i);
		}
		public LocalVariablesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localVariables; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterLocalVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitLocalVariables(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitLocalVariables(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LocalVariablesContext localVariables() throws RecognitionException {
		LocalVariablesContext _localctx = new LocalVariablesContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_localVariables);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			match(LOCAL_VARIABLES);
			setState(227);
			match(CURLY_BRACKET_OPEN);
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(228);
				variableOrListDeclaration();
				}
			}

			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(231);
				match(SEPARATOR);
				setState(232);
				variableOrListDeclaration();
				}
				}
				setState(237);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(238);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LooksContext extends ParserRuleContext {
		public TerminalNode LOOKS() { return getToken(CatrobatLanguageParser.LOOKS, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<LooksAndSoundsContentContext> looksAndSoundsContent() {
			return getRuleContexts(LooksAndSoundsContentContext.class);
		}
		public LooksAndSoundsContentContext looksAndSoundsContent(int i) {
			return getRuleContext(LooksAndSoundsContentContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CatrobatLanguageParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.SEPARATOR, i);
		}
		public LooksContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_looks; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterLooks(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitLooks(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitLooks(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LooksContext looks() throws RecognitionException {
		LooksContext _localctx = new LooksContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_looks);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			match(LOOKS);
			setState(241);
			match(CURLY_BRACKET_OPEN);
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(242);
				looksAndSoundsContent();
				}
			}

			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(245);
				match(SEPARATOR);
				setState(246);
				looksAndSoundsContent();
				}
				}
				setState(251);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(252);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SoundsContext extends ParserRuleContext {
		public TerminalNode SOUNDS() { return getToken(CatrobatLanguageParser.SOUNDS, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<LooksAndSoundsContentContext> looksAndSoundsContent() {
			return getRuleContexts(LooksAndSoundsContentContext.class);
		}
		public LooksAndSoundsContentContext looksAndSoundsContent(int i) {
			return getRuleContext(LooksAndSoundsContentContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CatrobatLanguageParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.SEPARATOR, i);
		}
		public SoundsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sounds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterSounds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitSounds(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitSounds(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SoundsContext sounds() throws RecognitionException {
		SoundsContext _localctx = new SoundsContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_sounds);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
			match(SOUNDS);
			setState(255);
			match(CURLY_BRACKET_OPEN);
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(256);
				looksAndSoundsContent();
				}
			}

			setState(263);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(259);
				match(SEPARATOR);
				setState(260);
				looksAndSoundsContent();
				}
				}
				setState(265);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(266);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LooksAndSoundsContentContext extends ParserRuleContext {
		public List<TerminalNode> STRING() { return getTokens(CatrobatLanguageParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(CatrobatLanguageParser.STRING, i);
		}
		public TerminalNode COLON() { return getToken(CatrobatLanguageParser.COLON, 0); }
		public LooksAndSoundsContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_looksAndSoundsContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterLooksAndSoundsContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitLooksAndSoundsContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitLooksAndSoundsContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LooksAndSoundsContentContext looksAndSoundsContent() throws RecognitionException {
		LooksAndSoundsContentContext _localctx = new LooksAndSoundsContentContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_looksAndSoundsContent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(268);
			match(STRING);
			setState(269);
			match(COLON);
			setState(270);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ScriptsContext extends ParserRuleContext {
		public TerminalNode SCRIPTS() { return getToken(CatrobatLanguageParser.SCRIPTS, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<Brick_with_bodyContext> brick_with_body() {
			return getRuleContexts(Brick_with_bodyContext.class);
		}
		public Brick_with_bodyContext brick_with_body(int i) {
			return getRuleContext(Brick_with_bodyContext.class,i);
		}
		public ScriptsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scripts; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterScripts(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitScripts(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitScripts(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptsContext scripts() throws RecognitionException {
		ScriptsContext _localctx = new ScriptsContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_scripts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(272);
			match(SCRIPTS);
			setState(273);
			match(CURLY_BRACKET_OPEN);
			setState(277);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DISABLED_BRICK_INDICATION || _la==BRICK_NAME) {
				{
				{
				setState(274);
				brick_with_body();
				}
				}
				setState(279);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(280);
			match(CURLY_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Brick_defintionContext extends ParserRuleContext {
		public TerminalNode NOTE_BRICK() { return getToken(CatrobatLanguageParser.NOTE_BRICK, 0); }
		public TerminalNode DISABLED_BRICK_INDICATION() { return getToken(CatrobatLanguageParser.DISABLED_BRICK_INDICATION, 0); }
		public Brick_invocationContext brick_invocation() {
			return getRuleContext(Brick_invocationContext.class,0);
		}
		public Brick_with_bodyContext brick_with_body() {
			return getRuleContext(Brick_with_bodyContext.class,0);
		}
		public Brick_defintionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brick_defintion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrick_defintion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrick_defintion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrick_defintion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Brick_defintionContext brick_defintion() throws RecognitionException {
		Brick_defintionContext _localctx = new Brick_defintionContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_brick_defintion);
		int _la;
		try {
			setState(288);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DISABLED_BRICK_INDICATION) {
					{
					setState(282);
					match(DISABLED_BRICK_INDICATION);
					}
				}

				setState(285);
				match(NOTE_BRICK);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(286);
				brick_invocation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(287);
				brick_with_body();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Brick_with_bodyContext extends ParserRuleContext {
		public TerminalNode BRICK_NAME() { return getToken(CatrobatLanguageParser.BRICK_NAME, 0); }
		public TerminalNode BRICK_BODY_OPEN() { return getToken(CatrobatLanguageParser.BRICK_BODY_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public Brick_conditionContext brick_condition() {
			return getRuleContext(Brick_conditionContext.class,0);
		}
		public List<Brick_defintionContext> brick_defintion() {
			return getRuleContexts(Brick_defintionContext.class);
		}
		public Brick_defintionContext brick_defintion(int i) {
			return getRuleContext(Brick_defintionContext.class,i);
		}
		public List<TerminalNode> DISABLED_BRICK_INDICATION() { return getTokens(CatrobatLanguageParser.DISABLED_BRICK_INDICATION); }
		public TerminalNode DISABLED_BRICK_INDICATION(int i) {
			return getToken(CatrobatLanguageParser.DISABLED_BRICK_INDICATION, i);
		}
		public Brick_with_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brick_with_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrick_with_body(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrick_with_body(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrick_with_body(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Brick_with_bodyContext brick_with_body() throws RecognitionException {
		Brick_with_bodyContext _localctx = new Brick_with_bodyContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_brick_with_body);
		int _la;
		try {
			int _alt;
			setState(316);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRICK_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(290);
				match(BRICK_NAME);
				setState(292);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN) {
					{
					setState(291);
					brick_condition();
					}
				}

				setState(294);
				match(BRICK_BODY_OPEN);
				setState(298);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 15032385536L) != 0)) {
					{
					{
					setState(295);
					brick_defintion();
					}
					}
					setState(300);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(301);
				match(CURLY_BRACKET_CLOSE);
				}
				break;
			case DISABLED_BRICK_INDICATION:
				enterOuterAlt(_localctx, 2);
				{
				setState(302);
				match(DISABLED_BRICK_INDICATION);
				setState(303);
				match(BRICK_NAME);
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN) {
					{
					setState(304);
					brick_condition();
					}
				}

				setState(307);
				match(BRICK_BODY_OPEN);
				setState(311);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(308);
						brick_defintion();
						}
						} 
					}
					setState(313);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
				}
				setState(314);
				match(DISABLED_BRICK_INDICATION);
				setState(315);
				match(CURLY_BRACKET_CLOSE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Brick_invocationContext extends ParserRuleContext {
		public TerminalNode BRICK_NAME() { return getToken(CatrobatLanguageParser.BRICK_NAME, 0); }
		public TerminalNode SEMICOLON() { return getToken(CatrobatLanguageParser.SEMICOLON, 0); }
		public TerminalNode DISABLED_BRICK_INDICATION() { return getToken(CatrobatLanguageParser.DISABLED_BRICK_INDICATION, 0); }
		public Brick_conditionContext brick_condition() {
			return getRuleContext(Brick_conditionContext.class,0);
		}
		public Brick_invocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brick_invocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrick_invocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrick_invocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrick_invocation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Brick_invocationContext brick_invocation() throws RecognitionException {
		Brick_invocationContext _localctx = new Brick_invocationContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_brick_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DISABLED_BRICK_INDICATION) {
				{
				setState(318);
				match(DISABLED_BRICK_INDICATION);
				}
			}

			setState(321);
			match(BRICK_NAME);
			setState(323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_MODE_BRACKET_OPEN) {
				{
				setState(322);
				brick_condition();
				}
			}

			setState(325);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Brick_conditionContext extends ParserRuleContext {
		public TerminalNode BRICK_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.BRICK_MODE_BRACKET_OPEN, 0); }
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public TerminalNode PARAM_MODE_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.PARAM_MODE_BRACKET_CLOSE, 0); }
		public Brick_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brick_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrick_condition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrick_condition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrick_condition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Brick_conditionContext brick_condition() throws RecognitionException {
		Brick_conditionContext _localctx = new Brick_conditionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_brick_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			match(BRICK_MODE_BRACKET_OPEN);
			setState(328);
			arg_list();
			setState(329);
			match(PARAM_MODE_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Arg_listContext extends ParserRuleContext {
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public List<TerminalNode> PARAM_SEPARATOR() { return getTokens(CatrobatLanguageParser.PARAM_SEPARATOR); }
		public TerminalNode PARAM_SEPARATOR(int i) {
			return getToken(CatrobatLanguageParser.PARAM_SEPARATOR, i);
		}
		public Arg_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterArg_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitArg_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitArg_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Arg_listContext arg_list() throws RecognitionException {
		Arg_listContext _localctx = new Arg_listContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			argument();
			setState(336);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAM_SEPARATOR) {
				{
				{
				setState(332);
				match(PARAM_SEPARATOR);
				setState(333);
				argument();
				}
				}
				setState(338);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentContext extends ParserRuleContext {
		public TerminalNode PARAM_MODE_NAME() { return getToken(CatrobatLanguageParser.PARAM_MODE_NAME, 0); }
		public TerminalNode PARAM_MODE_COLON() { return getToken(CatrobatLanguageParser.PARAM_MODE_COLON, 0); }
		public TerminalNode PARAM_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.PARAM_MODE_BRACKET_OPEN, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode FORMULA_MODE_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.FORMULA_MODE_BRACKET_CLOSE, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(339);
			match(PARAM_MODE_NAME);
			setState(340);
			match(PARAM_MODE_COLON);
			setState(341);
			match(PARAM_MODE_BRACKET_OPEN);
			setState(342);
			formula();
			setState(343);
			match(FORMULA_MODE_BRACKET_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormulaContext extends ParserRuleContext {
		public List<TerminalNode> FORMULA_MODE_BRACKET_OPEN() { return getTokens(CatrobatLanguageParser.FORMULA_MODE_BRACKET_OPEN); }
		public TerminalNode FORMULA_MODE_BRACKET_OPEN(int i) {
			return getToken(CatrobatLanguageParser.FORMULA_MODE_BRACKET_OPEN, i);
		}
		public List<TerminalNode> FORMULA_MODE_BRACKET_CLOSE() { return getTokens(CatrobatLanguageParser.FORMULA_MODE_BRACKET_CLOSE); }
		public TerminalNode FORMULA_MODE_BRACKET_CLOSE(int i) {
			return getToken(CatrobatLanguageParser.FORMULA_MODE_BRACKET_CLOSE, i);
		}
		public List<TerminalNode> FORMULA_MODE_ANYTHING() { return getTokens(CatrobatLanguageParser.FORMULA_MODE_ANYTHING); }
		public TerminalNode FORMULA_MODE_ANYTHING(int i) {
			return getToken(CatrobatLanguageParser.FORMULA_MODE_ANYTHING, i);
		}
		public List<TerminalNode> FORMULA_MODE_APOSTROPHE() { return getTokens(CatrobatLanguageParser.FORMULA_MODE_APOSTROPHE); }
		public TerminalNode FORMULA_MODE_APOSTROPHE(int i) {
			return getToken(CatrobatLanguageParser.FORMULA_MODE_APOSTROPHE, i);
		}
		public List<TerminalNode> ESCAPE_MODE_APOSTROPHE_CHAR() { return getTokens(CatrobatLanguageParser.ESCAPE_MODE_APOSTROPHE_CHAR); }
		public TerminalNode ESCAPE_MODE_APOSTROPHE_CHAR(int i) {
			return getToken(CatrobatLanguageParser.ESCAPE_MODE_APOSTROPHE_CHAR, i);
		}
		public List<TerminalNode> FORMULA_MODE_QUOTE() { return getTokens(CatrobatLanguageParser.FORMULA_MODE_QUOTE); }
		public TerminalNode FORMULA_MODE_QUOTE(int i) {
			return getToken(CatrobatLanguageParser.FORMULA_MODE_QUOTE, i);
		}
		public List<TerminalNode> ESCAPE_MODE_QUOTE_CHAR() { return getTokens(CatrobatLanguageParser.ESCAPE_MODE_QUOTE_CHAR); }
		public TerminalNode ESCAPE_MODE_QUOTE_CHAR(int i) {
			return getToken(CatrobatLanguageParser.ESCAPE_MODE_QUOTE_CHAR, i);
		}
		public List<TerminalNode> FORMULA_MODE_UDB_PARAM() { return getTokens(CatrobatLanguageParser.FORMULA_MODE_UDB_PARAM); }
		public TerminalNode FORMULA_MODE_UDB_PARAM(int i) {
			return getToken(CatrobatLanguageParser.FORMULA_MODE_UDB_PARAM, i);
		}
		public List<TerminalNode> ESCAPE_UDB_PARAM_MODE_CHAR() { return getTokens(CatrobatLanguageParser.ESCAPE_UDB_PARAM_MODE_CHAR); }
		public TerminalNode ESCAPE_UDB_PARAM_MODE_CHAR(int i) {
			return getToken(CatrobatLanguageParser.ESCAPE_UDB_PARAM_MODE_CHAR, i);
		}
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitFormula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitFormula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		FormulaContext _localctx = new FormulaContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_formula);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(345);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 96792207616376832L) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					} 
				}
				setState(350);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u00018\u0160\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002Q\b"+
		"\u0002\n\u0002\f\u0002T\t\u0002\u0001\u0002\u0004\u0002W\b\u0002\u000b"+
		"\u0002\f\u0002X\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0003\u0003`\b\u0003\u0001\u0003\u0001\u0003\u0005\u0003d\b\u0003"+
		"\n\u0003\f\u0003g\t\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0003\u0004n\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0003\b\u007f"+
		"\b\b\u0001\b\u0001\b\u0005\b\u0083\b\b\n\b\f\b\u0086\t\b\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u008e\b\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u00a3\b\u000e\u0001\u000e\u0001\u000e\u0005\u000e"+
		"\u00a7\b\u000e\n\u000e\f\u000e\u00aa\t\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00b1\b\u000f\u0001\u000f\u0001"+
		"\u000f\u0005\u000f\u00b5\b\u000f\n\u000f\f\u000f\u00b8\t\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u00be\b\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0005\u0012\u00c7\b\u0012\n\u0012\f\u0012\u00ca\t\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0004"+
		"\u0015\u00df\b\u0015\u000b\u0015\f\u0015\u00e0\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0003\u0016\u00e6\b\u0016\u0001\u0016\u0001\u0016\u0005\u0016"+
		"\u00ea\b\u0016\n\u0016\f\u0016\u00ed\t\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u00f4\b\u0017\u0001\u0017\u0001"+
		"\u0017\u0005\u0017\u00f8\b\u0017\n\u0017\f\u0017\u00fb\t\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u0102\b\u0018"+
		"\u0001\u0018\u0001\u0018\u0005\u0018\u0106\b\u0018\n\u0018\f\u0018\u0109"+
		"\t\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u0114\b\u001a\n"+
		"\u001a\f\u001a\u0117\t\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0003"+
		"\u001b\u011c\b\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u0121"+
		"\b\u001b\u0001\u001c\u0001\u001c\u0003\u001c\u0125\b\u001c\u0001\u001c"+
		"\u0001\u001c\u0005\u001c\u0129\b\u001c\n\u001c\f\u001c\u012c\t\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0132\b\u001c\u0001"+
		"\u001c\u0001\u001c\u0005\u001c\u0136\b\u001c\n\u001c\f\u001c\u0139\t\u001c"+
		"\u0001\u001c\u0001\u001c\u0003\u001c\u013d\b\u001c\u0001\u001d\u0003\u001d"+
		"\u0140\b\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0144\b\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0005\u001f\u014f\b\u001f\n\u001f\f\u001f"+
		"\u0152\t\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001!\u0005"+
		"!\u015b\b!\n!\f!\u015e\t!\u0001!\u0000\u0000\"\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,."+
		"02468:<>@B\u0000\u0001\u0004\u0000-2446688\u0168\u0000D\u0001\u0000\u0000"+
		"\u0000\u0002G\u0001\u0000\u0000\u0000\u0004I\u0001\u0000\u0000\u0000\u0006"+
		"\\\u0001\u0000\u0000\u0000\bm\u0001\u0000\u0000\u0000\no\u0001\u0000\u0000"+
		"\u0000\fs\u0001\u0000\u0000\u0000\u000ew\u0001\u0000\u0000\u0000\u0010"+
		"{\u0001\u0000\u0000\u0000\u0012\u008d\u0001\u0000\u0000\u0000\u0014\u008f"+
		"\u0001\u0000\u0000\u0000\u0016\u0093\u0001\u0000\u0000\u0000\u0018\u0097"+
		"\u0001\u0000\u0000\u0000\u001a\u009b\u0001\u0000\u0000\u0000\u001c\u009f"+
		"\u0001\u0000\u0000\u0000\u001e\u00ad\u0001\u0000\u0000\u0000 \u00bd\u0001"+
		"\u0000\u0000\u0000\"\u00bf\u0001\u0000\u0000\u0000$\u00c1\u0001\u0000"+
		"\u0000\u0000&\u00cd\u0001\u0000\u0000\u0000(\u00d2\u0001\u0000\u0000\u0000"+
		"*\u00de\u0001\u0000\u0000\u0000,\u00e2\u0001\u0000\u0000\u0000.\u00f0"+
		"\u0001\u0000\u0000\u00000\u00fe\u0001\u0000\u0000\u00002\u010c\u0001\u0000"+
		"\u0000\u00004\u0110\u0001\u0000\u0000\u00006\u0120\u0001\u0000\u0000\u0000"+
		"8\u013c\u0001\u0000\u0000\u0000:\u013f\u0001\u0000\u0000\u0000<\u0147"+
		"\u0001\u0000\u0000\u0000>\u014b\u0001\u0000\u0000\u0000@\u0153\u0001\u0000"+
		"\u0000\u0000B\u015c\u0001\u0000\u0000\u0000DE\u0003\u0002\u0001\u0000"+
		"EF\u0003\u0004\u0002\u0000F\u0001\u0001\u0000\u0000\u0000GH\u0005\n\u0000"+
		"\u0000H\u0003\u0001\u0000\u0000\u0000IJ\u0005\u000b\u0000\u0000JK\u0005"+
		"\u0005\u0000\u0000KR\u0005\u0003\u0000\u0000LQ\u0003\u0006\u0003\u0000"+
		"MQ\u0003\u0010\b\u0000NQ\u0003\u001c\u000e\u0000OQ\u0003\u001e\u000f\u0000"+
		"PL\u0001\u0000\u0000\u0000PM\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000"+
		"\u0000PO\u0001\u0000\u0000\u0000QT\u0001\u0000\u0000\u0000RP\u0001\u0000"+
		"\u0000\u0000RS\u0001\u0000\u0000\u0000SV\u0001\u0000\u0000\u0000TR\u0001"+
		"\u0000\u0000\u0000UW\u0003$\u0012\u0000VU\u0001\u0000\u0000\u0000WX\u0001"+
		"\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000"+
		"YZ\u0001\u0000\u0000\u0000Z[\u0005\u0004\u0000\u0000[\u0005\u0001\u0000"+
		"\u0000\u0000\\]\u0005\f\u0000\u0000]_\u0005\u0003\u0000\u0000^`\u0003"+
		"\b\u0004\u0000_^\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`e\u0001"+
		"\u0000\u0000\u0000ab\u0005\u0006\u0000\u0000bd\u0003\b\u0004\u0000ca\u0001"+
		"\u0000\u0000\u0000dg\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000"+
		"ef\u0001\u0000\u0000\u0000fh\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000"+
		"\u0000hi\u0005\u0004\u0000\u0000i\u0007\u0001\u0000\u0000\u0000jn\u0003"+
		"\n\u0005\u0000kn\u0003\f\u0006\u0000ln\u0003\u000e\u0007\u0000mj\u0001"+
		"\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000ml\u0001\u0000\u0000\u0000"+
		"n\t\u0001\u0000\u0000\u0000op\u0005\r\u0000\u0000pq\u0005\u0007\u0000"+
		"\u0000qr\u0005\u0005\u0000\u0000r\u000b\u0001\u0000\u0000\u0000st\u0005"+
		"\u000e\u0000\u0000tu\u0005\u0007\u0000\u0000uv\u0005\u0005\u0000\u0000"+
		"v\r\u0001\u0000\u0000\u0000wx\u0005\u000f\u0000\u0000xy\u0005\u0007\u0000"+
		"\u0000yz\u0005\u0005\u0000\u0000z\u000f\u0001\u0000\u0000\u0000{|\u0005"+
		"\u0010\u0000\u0000|~\u0005\u0003\u0000\u0000}\u007f\u0003\u0012\t\u0000"+
		"~}\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0084"+
		"\u0001\u0000\u0000\u0000\u0080\u0081\u0005\u0006\u0000\u0000\u0081\u0083"+
		"\u0003\u0012\t\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0083\u0086\u0001"+
		"\u0000\u0000\u0000\u0084\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001"+
		"\u0000\u0000\u0000\u0085\u0087\u0001\u0000\u0000\u0000\u0086\u0084\u0001"+
		"\u0000\u0000\u0000\u0087\u0088\u0005\u0004\u0000\u0000\u0088\u0011\u0001"+
		"\u0000\u0000\u0000\u0089\u008e\u0003\u0014\n\u0000\u008a\u008e\u0003\u0016"+
		"\u000b\u0000\u008b\u008e\u0003\u0018\f\u0000\u008c\u008e\u0003\u001a\r"+
		"\u0000\u008d\u0089\u0001\u0000\u0000\u0000\u008d\u008a\u0001\u0000\u0000"+
		"\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008d\u008c\u0001\u0000\u0000"+
		"\u0000\u008e\u0013\u0001\u0000\u0000\u0000\u008f\u0090\u0005\u0011\u0000"+
		"\u0000\u0090\u0091\u0005\u0007\u0000\u0000\u0091\u0092\u0005\u0005\u0000"+
		"\u0000\u0092\u0015\u0001\u0000\u0000\u0000\u0093\u0094\u0005\u0012\u0000"+
		"\u0000\u0094\u0095\u0005\u0007\u0000\u0000\u0095\u0096\u0005\u0005\u0000"+
		"\u0000\u0096\u0017\u0001\u0000\u0000\u0000\u0097\u0098\u0005\u0013\u0000"+
		"\u0000\u0098\u0099\u0005\u0007\u0000\u0000\u0099\u009a\u0005\u0005\u0000"+
		"\u0000\u009a\u0019\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u0014\u0000"+
		"\u0000\u009c\u009d\u0005\u0007\u0000\u0000\u009d\u009e\u0005\u0005\u0000"+
		"\u0000\u009e\u001b\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u0015\u0000"+
		"\u0000\u00a0\u00a2\u0005\u0003\u0000\u0000\u00a1\u00a3\u0003 \u0010\u0000"+
		"\u00a2\u00a1\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000"+
		"\u00a3\u00a8\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005\u0006\u0000\u0000"+
		"\u00a5\u00a7\u0003 \u0010\u0000\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a7"+
		"\u00aa\u0001\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000\u0000\u0000\u00a8"+
		"\u00a9\u0001\u0000\u0000\u0000\u00a9\u00ab\u0001\u0000\u0000\u0000\u00aa"+
		"\u00a8\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005\u0004\u0000\u0000\u00ac"+
		"\u001d\u0001\u0000\u0000\u0000\u00ad\u00ae\u0005\u0016\u0000\u0000\u00ae"+
		"\u00b0\u0005\u0003\u0000\u0000\u00af\u00b1\u0003\"\u0011\u0000\u00b0\u00af"+
		"\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b6"+
		"\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\u0006\u0000\u0000\u00b3\u00b5"+
		"\u0003\"\u0011\u0000\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b5\u00b8\u0001"+
		"\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001"+
		"\u0000\u0000\u0000\u00b7\u00b9\u0001\u0000\u0000\u0000\u00b8\u00b6\u0001"+
		"\u0000\u0000\u0000\u00b9\u00ba\u0005\u0004\u0000\u0000\u00ba\u001f\u0001"+
		"\u0000\u0000\u0000\u00bb\u00be\u0003\"\u0011\u0000\u00bc\u00be\u0005\t"+
		"\u0000\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000\u00bd\u00bc\u0001\u0000"+
		"\u0000\u0000\u00be!\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005\b\u0000"+
		"\u0000\u00c0#\u0001\u0000\u0000\u0000\u00c1\u00c2\u0005\u001c\u0000\u0000"+
		"\u00c2\u00c3\u0005\u0005\u0000\u0000\u00c3\u00c4\u0005\u0003\u0000\u0000"+
		"\u00c4\u00c8\u0003&\u0013\u0000\u00c5\u00c7\u0003(\u0014\u0000\u00c6\u00c5"+
		"\u0001\u0000\u0000\u0000\u00c7\u00ca\u0001\u0000\u0000\u0000\u00c8\u00c6"+
		"\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00cb"+
		"\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00cb\u00cc"+
		"\u0005\u0004\u0000\u0000\u00cc%\u0001\u0000\u0000\u0000\u00cd\u00ce\u0005"+
		"\u001d\u0000\u0000\u00ce\u00cf\u0005\u0003\u0000\u0000\u00cf\u00d0\u0003"+
		"*\u0015\u0000\u00d0\u00d1\u0005\u0004\u0000\u0000\u00d1\'\u0001\u0000"+
		"\u0000\u0000\u00d2\u00d3\u0005\u001a\u0000\u0000\u00d3\u00d4\u0005\u0005"+
		"\u0000\u0000\u00d4\u00d5\u0005\u001b\u0000\u0000\u00d5\u00d6\u0005\u0005"+
		"\u0000\u0000\u00d6\u00d7\u0005\u0003\u0000\u0000\u00d7\u00d8\u0003*\u0015"+
		"\u0000\u00d8\u00d9\u0005\u0004\u0000\u0000\u00d9)\u0001\u0000\u0000\u0000"+
		"\u00da\u00df\u0003,\u0016\u0000\u00db\u00df\u0003.\u0017\u0000\u00dc\u00df"+
		"\u00030\u0018\u0000\u00dd\u00df\u00034\u001a\u0000\u00de\u00da\u0001\u0000"+
		"\u0000\u0000\u00de\u00db\u0001\u0000\u0000\u0000\u00de\u00dc\u0001\u0000"+
		"\u0000\u0000\u00de\u00dd\u0001\u0000\u0000\u0000\u00df\u00e0\u0001\u0000"+
		"\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001\u0000"+
		"\u0000\u0000\u00e1+\u0001\u0000\u0000\u0000\u00e2\u00e3\u0005\u0017\u0000"+
		"\u0000\u00e3\u00e5\u0005\u0003\u0000\u0000\u00e4\u00e6\u0003 \u0010\u0000"+
		"\u00e5\u00e4\u0001\u0000\u0000\u0000\u00e5\u00e6\u0001\u0000\u0000\u0000"+
		"\u00e6\u00eb\u0001\u0000\u0000\u0000\u00e7\u00e8\u0005\u0006\u0000\u0000"+
		"\u00e8\u00ea\u0003 \u0010\u0000\u00e9\u00e7\u0001\u0000\u0000\u0000\u00ea"+
		"\u00ed\u0001\u0000\u0000\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000\u00eb"+
		"\u00ec\u0001\u0000\u0000\u0000\u00ec\u00ee\u0001\u0000\u0000\u0000\u00ed"+
		"\u00eb\u0001\u0000\u0000\u0000\u00ee\u00ef\u0005\u0004\u0000\u0000\u00ef"+
		"-\u0001\u0000\u0000\u0000\u00f0\u00f1\u0005\u0018\u0000\u0000\u00f1\u00f3"+
		"\u0005\u0003\u0000\u0000\u00f2\u00f4\u00032\u0019\u0000\u00f3\u00f2\u0001"+
		"\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4\u00f9\u0001"+
		"\u0000\u0000\u0000\u00f5\u00f6\u0005\u0006\u0000\u0000\u00f6\u00f8\u0003"+
		"2\u0019\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000\u00f8\u00fb\u0001\u0000"+
		"\u0000\u0000\u00f9\u00f7\u0001\u0000\u0000\u0000\u00f9\u00fa\u0001\u0000"+
		"\u0000\u0000\u00fa\u00fc\u0001\u0000\u0000\u0000\u00fb\u00f9\u0001\u0000"+
		"\u0000\u0000\u00fc\u00fd\u0005\u0004\u0000\u0000\u00fd/\u0001\u0000\u0000"+
		"\u0000\u00fe\u00ff\u0005\u0019\u0000\u0000\u00ff\u0101\u0005\u0003\u0000"+
		"\u0000\u0100\u0102\u00032\u0019\u0000\u0101\u0100\u0001\u0000\u0000\u0000"+
		"\u0101\u0102\u0001\u0000\u0000\u0000\u0102\u0107\u0001\u0000\u0000\u0000"+
		"\u0103\u0104\u0005\u0006\u0000\u0000\u0104\u0106\u00032\u0019\u0000\u0105"+
		"\u0103\u0001\u0000\u0000\u0000\u0106\u0109\u0001\u0000\u0000\u0000\u0107"+
		"\u0105\u0001\u0000\u0000\u0000\u0107\u0108\u0001\u0000\u0000\u0000\u0108"+
		"\u010a\u0001\u0000\u0000\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u010a"+
		"\u010b\u0005\u0004\u0000\u0000\u010b1\u0001\u0000\u0000\u0000\u010c\u010d"+
		"\u0005\u0005\u0000\u0000\u010d\u010e\u0005\u0007\u0000\u0000\u010e\u010f"+
		"\u0005\u0005\u0000\u0000\u010f3\u0001\u0000\u0000\u0000\u0110\u0111\u0005"+
		"\u001e\u0000\u0000\u0111\u0115\u0005\u0003\u0000\u0000\u0112\u0114\u0003"+
		"8\u001c\u0000\u0113\u0112\u0001\u0000\u0000\u0000\u0114\u0117\u0001\u0000"+
		"\u0000\u0000\u0115\u0113\u0001\u0000\u0000\u0000\u0115\u0116\u0001\u0000"+
		"\u0000\u0000\u0116\u0118\u0001\u0000\u0000\u0000\u0117\u0115\u0001\u0000"+
		"\u0000\u0000\u0118\u0119\u0005\u0004\u0000\u0000\u01195\u0001\u0000\u0000"+
		"\u0000\u011a\u011c\u0005 \u0000\u0000\u011b\u011a\u0001\u0000\u0000\u0000"+
		"\u011b\u011c\u0001\u0000\u0000\u0000\u011c\u011d\u0001\u0000\u0000\u0000"+
		"\u011d\u0121\u0005\u001f\u0000\u0000\u011e\u0121\u0003:\u001d\u0000\u011f"+
		"\u0121\u00038\u001c\u0000\u0120\u011b\u0001\u0000\u0000\u0000\u0120\u011e"+
		"\u0001\u0000\u0000\u0000\u0120\u011f\u0001\u0000\u0000\u0000\u01217\u0001"+
		"\u0000\u0000\u0000\u0122\u0124\u0005!\u0000\u0000\u0123\u0125\u0003<\u001e"+
		"\u0000\u0124\u0123\u0001\u0000\u0000\u0000\u0124\u0125\u0001\u0000\u0000"+
		"\u0000\u0125\u0126\u0001\u0000\u0000\u0000\u0126\u012a\u0005%\u0000\u0000"+
		"\u0127\u0129\u00036\u001b\u0000\u0128\u0127\u0001\u0000\u0000\u0000\u0129"+
		"\u012c\u0001\u0000\u0000\u0000\u012a\u0128\u0001\u0000\u0000\u0000\u012a"+
		"\u012b\u0001\u0000\u0000\u0000\u012b\u012d\u0001\u0000\u0000\u0000\u012c"+
		"\u012a\u0001\u0000\u0000\u0000\u012d\u013d\u0005\u0004\u0000\u0000\u012e"+
		"\u012f\u0005 \u0000\u0000\u012f\u0131\u0005!\u0000\u0000\u0130\u0132\u0003"+
		"<\u001e\u0000\u0131\u0130\u0001\u0000\u0000\u0000\u0131\u0132\u0001\u0000"+
		"\u0000\u0000\u0132\u0133\u0001\u0000\u0000\u0000\u0133\u0137\u0005%\u0000"+
		"\u0000\u0134\u0136\u00036\u001b\u0000\u0135\u0134\u0001\u0000\u0000\u0000"+
		"\u0136\u0139\u0001\u0000\u0000\u0000\u0137\u0135\u0001\u0000\u0000\u0000"+
		"\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u013a\u0001\u0000\u0000\u0000"+
		"\u0139\u0137\u0001\u0000\u0000\u0000\u013a\u013b\u0005 \u0000\u0000\u013b"+
		"\u013d\u0005\u0004\u0000\u0000\u013c\u0122\u0001\u0000\u0000\u0000\u013c"+
		"\u012e\u0001\u0000\u0000\u0000\u013d9\u0001\u0000\u0000\u0000\u013e\u0140"+
		"\u0005 \u0000\u0000\u013f\u013e\u0001\u0000\u0000\u0000\u013f\u0140\u0001"+
		"\u0000\u0000\u0000\u0140\u0141\u0001\u0000\u0000\u0000\u0141\u0143\u0005"+
		"!\u0000\u0000\u0142\u0144\u0003<\u001e\u0000\u0143\u0142\u0001\u0000\u0000"+
		"\u0000\u0143\u0144\u0001\u0000\u0000\u0000\u0144\u0145\u0001\u0000\u0000"+
		"\u0000\u0145\u0146\u0005$\u0000\u0000\u0146;\u0001\u0000\u0000\u0000\u0147"+
		"\u0148\u0005#\u0000\u0000\u0148\u0149\u0003>\u001f\u0000\u0149\u014a\u0005"+
		"(\u0000\u0000\u014a=\u0001\u0000\u0000\u0000\u014b\u0150\u0003@ \u0000"+
		"\u014c\u014d\u0005+\u0000\u0000\u014d\u014f\u0003@ \u0000\u014e\u014c"+
		"\u0001\u0000\u0000\u0000\u014f\u0152\u0001\u0000\u0000\u0000\u0150\u014e"+
		"\u0001\u0000\u0000\u0000\u0150\u0151\u0001\u0000\u0000\u0000\u0151?\u0001"+
		"\u0000\u0000\u0000\u0152\u0150\u0001\u0000\u0000\u0000\u0153\u0154\u0005"+
		")\u0000\u0000\u0154\u0155\u0005*\u0000\u0000\u0155\u0156\u0005\'\u0000"+
		"\u0000\u0156\u0157\u0003B!\u0000\u0157\u0158\u0005-\u0000\u0000\u0158"+
		"A\u0001\u0000\u0000\u0000\u0159\u015b\u0007\u0000\u0000\u0000\u015a\u0159"+
		"\u0001\u0000\u0000\u0000\u015b\u015e\u0001\u0000\u0000\u0000\u015c\u015a"+
		"\u0001\u0000\u0000\u0000\u015c\u015d\u0001\u0000\u0000\u0000\u015dC\u0001"+
		"\u0000\u0000\u0000\u015e\u015c\u0001\u0000\u0000\u0000#PRX_em~\u0084\u008d"+
		"\u00a2\u00a8\u00b0\u00b6\u00bd\u00c8\u00de\u00e0\u00e5\u00eb\u00f3\u00f9"+
		"\u0101\u0107\u0115\u011b\u0120\u0124\u012a\u0131\u0137\u013c\u013f\u0143"+
		"\u0150\u015c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}