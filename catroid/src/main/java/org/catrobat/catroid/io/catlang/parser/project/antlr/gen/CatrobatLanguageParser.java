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
		OF_TYPE=27, SCENE=28, BACKGROUND=29, SCRIPTS=30, BRICK_NAME=31, BRICK_MODE_WS=32, 
		BRICK_MODE_BRACKET_OPEN=33, SEMICOLON=34, BRICK_BODY_OPEN=35, PARAM_MODE_WS=36, 
		PARAM_MODE_BRACKET_OPEN=37, PARAM_MODE_BRACKET_CLOSE=38, PARAM_MODE_NAME=39, 
		PARAM_MODE_COLON=40, PARAM_SEPARATOR=41, FORMULA_MODE_WS=42, FORMULA_MODE_BRACKET_CLOSE=43, 
		FORMULA_MODE_BRACKET_OPEN=44, FORMULA_MODE_ANYTHING=45, FORMULA_MODE_APOSTROPHE=46, 
		FORMULA_MODE_QUOTE=47, FORMULA_MODE_UDB_PARAM=48, ESCAPE_MODE_APOSTROPHE_ANYTHING=49, 
		ESCAPE_MODE_APOSTROPHE_CHAR=50, ESCAPE_MODE_QUOTE_ANYTHING=51, ESCAPE_MODE_QUOTE_CHAR=52, 
		ESCAPE_UDB_PARAM_MODE_ANYTHING=53, ESCAPE_UDB_PARAM_MODE_CHAR=54;
	public static final int
		RULE_program = 0, RULE_programHeader = 1, RULE_programBody = 2, RULE_metadata = 3, 
		RULE_metadataContent = 4, RULE_description = 5, RULE_catrobatVersion = 6, 
		RULE_catrobatAppVersion = 7, RULE_stage = 8, RULE_stageContent = 9, RULE_landscapeMode = 10, 
		RULE_height = 11, RULE_width = 12, RULE_displayMode = 13, RULE_globals = 14, 
		RULE_multiplayerVariables = 15, RULE_variableDeclaration = 16, RULE_scene = 17, 
		RULE_background = 18, RULE_actor = 19, RULE_actorContent = 20, RULE_localVariables = 21, 
		RULE_looks = 22, RULE_sounds = 23, RULE_looksAndSoundsContent = 24, RULE_scripts = 25, 
		RULE_brick_with_body = 26, RULE_brick_invocation = 27, RULE_brick_condition = 28, 
		RULE_arg_list = 29, RULE_argument = 30, RULE_formula = 31;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "programHeader", "programBody", "metadata", "metadataContent", 
			"description", "catrobatVersion", "catrobatAppVersion", "stage", "stageContent", 
			"landscapeMode", "height", "width", "displayMode", "globals", "multiplayerVariables", 
			"variableDeclaration", "scene", "background", "actor", "actorContent", 
			"localVariables", "looks", "sounds", "looksAndSoundsContent", "scripts", 
			"brick_with_body", "brick_invocation", "brick_condition", "arg_list", 
			"argument", "formula"
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
			null, null, null, "';'", null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "'['", null, null, null, null, null, "']'"
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
			"OF_TYPE", "SCENE", "BACKGROUND", "SCRIPTS", "BRICK_NAME", "BRICK_MODE_WS", 
			"BRICK_MODE_BRACKET_OPEN", "SEMICOLON", "BRICK_BODY_OPEN", "PARAM_MODE_WS", 
			"PARAM_MODE_BRACKET_OPEN", "PARAM_MODE_BRACKET_CLOSE", "PARAM_MODE_NAME", 
			"PARAM_MODE_COLON", "PARAM_SEPARATOR", "FORMULA_MODE_WS", "FORMULA_MODE_BRACKET_CLOSE", 
			"FORMULA_MODE_BRACKET_OPEN", "FORMULA_MODE_ANYTHING", "FORMULA_MODE_APOSTROPHE", 
			"FORMULA_MODE_QUOTE", "FORMULA_MODE_UDB_PARAM", "ESCAPE_MODE_APOSTROPHE_ANYTHING", 
			"ESCAPE_MODE_APOSTROPHE_CHAR", "ESCAPE_MODE_QUOTE_ANYTHING", "ESCAPE_MODE_QUOTE_CHAR", 
			"ESCAPE_UDB_PARAM_MODE_ANYTHING", "ESCAPE_UDB_PARAM_MODE_CHAR"
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
			setState(64);
			programHeader();
			setState(65);
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
			setState(67);
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
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public StageContext stage() {
			return getRuleContext(StageContext.class,0);
		}
		public GlobalsContext globals() {
			return getRuleContext(GlobalsContext.class,0);
		}
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public MultiplayerVariablesContext multiplayerVariables() {
			return getRuleContext(MultiplayerVariablesContext.class,0);
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
			setState(69);
			match(PROGRAM);
			setState(70);
			match(STRING);
			setState(71);
			match(CURLY_BRACKET_OPEN);
			setState(72);
			metadata();
			setState(73);
			stage();
			setState(74);
			globals();
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MULTIPLAYER_VARIABLES) {
				{
				setState(75);
				multiplayerVariables();
				}
			}

			setState(79); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(78);
				scene();
				}
				}
				setState(81); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCENE );
			setState(83);
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
			setState(85);
			match(METADATA);
			setState(86);
			match(CURLY_BRACKET_OPEN);
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 57344L) != 0)) {
				{
				setState(87);
				metadataContent();
				}
			}

			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(90);
				match(SEPARATOR);
				setState(91);
				metadataContent();
				}
				}
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(97);
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
			setState(102);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DESCRIPTION:
				{
				setState(99);
				description();
				}
				break;
			case CATROBAT_VERSION:
				{
				setState(100);
				catrobatVersion();
				}
				break;
			case CATRPBAT_APP_VERSION:
				{
				setState(101);
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
			setState(104);
			match(DESCRIPTION);
			setState(105);
			match(COLON);
			setState(106);
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
			setState(108);
			match(CATROBAT_VERSION);
			setState(109);
			match(COLON);
			setState(110);
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
			setState(112);
			match(CATRPBAT_APP_VERSION);
			setState(113);
			match(COLON);
			setState(114);
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
			setState(116);
			match(STAGE);
			setState(117);
			match(CURLY_BRACKET_OPEN);
			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0)) {
				{
				setState(118);
				stageContent();
				}
			}

			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(121);
				match(SEPARATOR);
				setState(122);
				stageContent();
				}
				}
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(128);
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
			setState(134);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANDSCAPE_MODE:
				{
				setState(130);
				landscapeMode();
				}
				break;
			case HEIGHT:
				{
				setState(131);
				height();
				}
				break;
			case WIDTH:
				{
				setState(132);
				width();
				}
				break;
			case DISPLAY_MODE:
				{
				setState(133);
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
			setState(136);
			match(LANDSCAPE_MODE);
			setState(137);
			match(COLON);
			setState(138);
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
			setState(140);
			match(HEIGHT);
			setState(141);
			match(COLON);
			setState(142);
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
			setState(144);
			match(WIDTH);
			setState(145);
			match(COLON);
			setState(146);
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
			setState(148);
			match(DISPLAY_MODE);
			setState(149);
			match(COLON);
			setState(150);
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
			setState(152);
			match(GLOBALS);
			setState(153);
			match(CURLY_BRACKET_OPEN);
			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(154);
				variableDeclaration();
				}
			}

			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(157);
				match(SEPARATOR);
				setState(158);
				variableDeclaration();
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(164);
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
			setState(166);
			match(MULTIPLAYER_VARIABLES);
			setState(167);
			match(CURLY_BRACKET_OPEN);
			setState(169);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(168);
				variableDeclaration();
				}
			}

			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(171);
				match(SEPARATOR);
				setState(172);
				variableDeclaration();
				}
				}
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(178);
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
	public static class VariableDeclarationContext extends ParserRuleContext {
		public TerminalNode VARIABLE_REF() { return getToken(CatrobatLanguageParser.VARIABLE_REF, 0); }
		public TerminalNode LIST_REF() { return getToken(CatrobatLanguageParser.LIST_REF, 0); }
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
		enterRule(_localctx, 32, RULE_variableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			_la = _input.LA(1);
			if ( !(_la==VARIABLE_REF || _la==LIST_REF) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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
		enterRule(_localctx, 34, RULE_scene);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(SCENE);
			setState(183);
			match(STRING);
			setState(184);
			match(CURLY_BRACKET_OPEN);
			setState(185);
			background();
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ACTOR_OR_OBJECT) {
				{
				{
				setState(186);
				actor();
				}
				}
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(192);
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
		enterRule(_localctx, 36, RULE_background);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(BACKGROUND);
			setState(195);
			match(CURLY_BRACKET_OPEN);
			setState(196);
			actorContent();
			setState(197);
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
		enterRule(_localctx, 38, RULE_actor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(ACTOR_OR_OBJECT);
			setState(200);
			match(STRING);
			setState(201);
			match(OF_TYPE);
			setState(202);
			match(STRING);
			setState(203);
			match(CURLY_BRACKET_OPEN);
			setState(204);
			actorContent();
			setState(205);
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
		enterRule(_localctx, 40, RULE_actorContent);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(211);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LOCAL_VARIABLES:
					{
					setState(207);
					localVariables();
					}
					break;
				case LOOKS:
					{
					setState(208);
					looks();
					}
					break;
				case SOUNDS:
					{
					setState(209);
					sounds();
					}
					break;
				case SCRIPTS:
					{
					setState(210);
					scripts();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(213); 
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
		enterRule(_localctx, 42, RULE_localVariables);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			match(LOCAL_VARIABLES);
			setState(216);
			match(CURLY_BRACKET_OPEN);
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(217);
				variableDeclaration();
				}
			}

			setState(224);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(220);
				match(SEPARATOR);
				setState(221);
				variableDeclaration();
				}
				}
				setState(226);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(227);
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
		enterRule(_localctx, 44, RULE_looks);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			match(LOOKS);
			setState(230);
			match(CURLY_BRACKET_OPEN);
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(231);
				looksAndSoundsContent();
				}
			}

			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(234);
				match(SEPARATOR);
				setState(235);
				looksAndSoundsContent();
				}
				}
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(241);
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
		enterRule(_localctx, 46, RULE_sounds);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(SOUNDS);
			setState(244);
			match(CURLY_BRACKET_OPEN);
			setState(246);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(245);
				looksAndSoundsContent();
				}
			}

			setState(252);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(248);
				match(SEPARATOR);
				setState(249);
				looksAndSoundsContent();
				}
				}
				setState(254);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(255);
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
		enterRule(_localctx, 48, RULE_looksAndSoundsContent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			match(STRING);
			setState(258);
			match(COLON);
			setState(259);
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
		enterRule(_localctx, 50, RULE_scripts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(SCRIPTS);
			setState(262);
			match(CURLY_BRACKET_OPEN);
			setState(266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BRICK_NAME) {
				{
				{
				setState(263);
				brick_with_body();
				}
				}
				setState(268);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(269);
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
	public static class Brick_with_bodyContext extends ParserRuleContext {
		public TerminalNode BRICK_NAME() { return getToken(CatrobatLanguageParser.BRICK_NAME, 0); }
		public TerminalNode BRICK_BODY_OPEN() { return getToken(CatrobatLanguageParser.BRICK_BODY_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public Brick_conditionContext brick_condition() {
			return getRuleContext(Brick_conditionContext.class,0);
		}
		public List<Brick_invocationContext> brick_invocation() {
			return getRuleContexts(Brick_invocationContext.class);
		}
		public Brick_invocationContext brick_invocation(int i) {
			return getRuleContext(Brick_invocationContext.class,i);
		}
		public List<Brick_with_bodyContext> brick_with_body() {
			return getRuleContexts(Brick_with_bodyContext.class);
		}
		public Brick_with_bodyContext brick_with_body(int i) {
			return getRuleContext(Brick_with_bodyContext.class,i);
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
		enterRule(_localctx, 52, RULE_brick_with_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			match(BRICK_NAME);
			setState(273);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_MODE_BRACKET_OPEN) {
				{
				setState(272);
				brick_condition();
				}
			}

			setState(275);
			match(BRICK_BODY_OPEN);
			setState(280);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BRICK_NAME) {
				{
				setState(278);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					setState(276);
					brick_invocation();
					}
					break;
				case 2:
					{
					setState(277);
					brick_with_body();
					}
					break;
				}
				}
				setState(282);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(283);
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
	public static class Brick_invocationContext extends ParserRuleContext {
		public TerminalNode BRICK_NAME() { return getToken(CatrobatLanguageParser.BRICK_NAME, 0); }
		public TerminalNode SEMICOLON() { return getToken(CatrobatLanguageParser.SEMICOLON, 0); }
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
		enterRule(_localctx, 54, RULE_brick_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(BRICK_NAME);
			setState(287);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_MODE_BRACKET_OPEN) {
				{
				setState(286);
				brick_condition();
				}
			}

			setState(289);
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
		enterRule(_localctx, 56, RULE_brick_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(BRICK_MODE_BRACKET_OPEN);
			setState(292);
			arg_list();
			setState(293);
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
		enterRule(_localctx, 58, RULE_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			argument();
			setState(300);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAM_SEPARATOR) {
				{
				{
				setState(296);
				match(PARAM_SEPARATOR);
				setState(297);
				argument();
				}
				}
				setState(302);
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
		enterRule(_localctx, 60, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			match(PARAM_MODE_NAME);
			setState(304);
			match(PARAM_MODE_COLON);
			setState(305);
			match(PARAM_MODE_BRACKET_OPEN);
			setState(306);
			formula();
			setState(307);
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
		enterRule(_localctx, 62, RULE_formula);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(309);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 24198051904094208L) != 0)) ) {
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
				setState(314);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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
		"\u0004\u00016\u013c\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002M\b\u0002\u0001\u0002\u0004\u0002"+
		"P\b\u0002\u000b\u0002\f\u0002Q\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003Y\b\u0003\u0001\u0003\u0001\u0003\u0005"+
		"\u0003]\b\u0003\n\u0003\f\u0003`\t\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0003\u0004g\b\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\b\u0003\bx\b\b\u0001\b\u0001\b\u0005\b|\b\b\n\b\f\b\u007f\t\b\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u0087\b\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0003\u000e\u009c\b\u000e\u0001\u000e\u0001\u000e"+
		"\u0005\u000e\u00a0\b\u000e\n\u000e\f\u000e\u00a3\t\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00aa\b\u000f\u0001"+
		"\u000f\u0001\u000f\u0005\u000f\u00ae\b\u000f\n\u000f\f\u000f\u00b1\t\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u00bc\b\u0011\n\u0011"+
		"\f\u0011\u00bf\t\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0004\u0014\u00d4\b\u0014\u000b\u0014"+
		"\f\u0014\u00d5\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00db\b"+
		"\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u00df\b\u0015\n\u0015\f\u0015"+
		"\u00e2\t\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0003\u0016\u00e9\b\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u00ed\b"+
		"\u0016\n\u0016\f\u0016\u00f0\t\u0016\u0001\u0016\u0001\u0016\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0003\u0017\u00f7\b\u0017\u0001\u0017\u0001\u0017"+
		"\u0005\u0017\u00fb\b\u0017\n\u0017\f\u0017\u00fe\t\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0005\u0019\u0109\b\u0019\n\u0019\f\u0019\u010c\t\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0003\u001a\u0112\b\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u0117\b\u001a\n\u001a"+
		"\f\u001a\u011a\t\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b"+
		"\u0003\u001b\u0120\b\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0005\u001d"+
		"\u012b\b\u001d\n\u001d\f\u001d\u012e\t\u001d\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0005\u001f\u0137"+
		"\b\u001f\n\u001f\f\u001f\u013a\t\u001f\u0001\u001f\u0000\u0000 \u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"$&(*,.02468:<>\u0000\u0002\u0001\u0000\b\t\u0004\u0000+02244"+
		"66\u013c\u0000@\u0001\u0000\u0000\u0000\u0002C\u0001\u0000\u0000\u0000"+
		"\u0004E\u0001\u0000\u0000\u0000\u0006U\u0001\u0000\u0000\u0000\bf\u0001"+
		"\u0000\u0000\u0000\nh\u0001\u0000\u0000\u0000\fl\u0001\u0000\u0000\u0000"+
		"\u000ep\u0001\u0000\u0000\u0000\u0010t\u0001\u0000\u0000\u0000\u0012\u0086"+
		"\u0001\u0000\u0000\u0000\u0014\u0088\u0001\u0000\u0000\u0000\u0016\u008c"+
		"\u0001\u0000\u0000\u0000\u0018\u0090\u0001\u0000\u0000\u0000\u001a\u0094"+
		"\u0001\u0000\u0000\u0000\u001c\u0098\u0001\u0000\u0000\u0000\u001e\u00a6"+
		"\u0001\u0000\u0000\u0000 \u00b4\u0001\u0000\u0000\u0000\"\u00b6\u0001"+
		"\u0000\u0000\u0000$\u00c2\u0001\u0000\u0000\u0000&\u00c7\u0001\u0000\u0000"+
		"\u0000(\u00d3\u0001\u0000\u0000\u0000*\u00d7\u0001\u0000\u0000\u0000,"+
		"\u00e5\u0001\u0000\u0000\u0000.\u00f3\u0001\u0000\u0000\u00000\u0101\u0001"+
		"\u0000\u0000\u00002\u0105\u0001\u0000\u0000\u00004\u010f\u0001\u0000\u0000"+
		"\u00006\u011d\u0001\u0000\u0000\u00008\u0123\u0001\u0000\u0000\u0000:"+
		"\u0127\u0001\u0000\u0000\u0000<\u012f\u0001\u0000\u0000\u0000>\u0138\u0001"+
		"\u0000\u0000\u0000@A\u0003\u0002\u0001\u0000AB\u0003\u0004\u0002\u0000"+
		"B\u0001\u0001\u0000\u0000\u0000CD\u0005\n\u0000\u0000D\u0003\u0001\u0000"+
		"\u0000\u0000EF\u0005\u000b\u0000\u0000FG\u0005\u0005\u0000\u0000GH\u0005"+
		"\u0003\u0000\u0000HI\u0003\u0006\u0003\u0000IJ\u0003\u0010\b\u0000JL\u0003"+
		"\u001c\u000e\u0000KM\u0003\u001e\u000f\u0000LK\u0001\u0000\u0000\u0000"+
		"LM\u0001\u0000\u0000\u0000MO\u0001\u0000\u0000\u0000NP\u0003\"\u0011\u0000"+
		"ON\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QO\u0001\u0000\u0000"+
		"\u0000QR\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000ST\u0005\u0004"+
		"\u0000\u0000T\u0005\u0001\u0000\u0000\u0000UV\u0005\f\u0000\u0000VX\u0005"+
		"\u0003\u0000\u0000WY\u0003\b\u0004\u0000XW\u0001\u0000\u0000\u0000XY\u0001"+
		"\u0000\u0000\u0000Y^\u0001\u0000\u0000\u0000Z[\u0005\u0006\u0000\u0000"+
		"[]\u0003\b\u0004\u0000\\Z\u0001\u0000\u0000\u0000]`\u0001\u0000\u0000"+
		"\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_a\u0001\u0000"+
		"\u0000\u0000`^\u0001\u0000\u0000\u0000ab\u0005\u0004\u0000\u0000b\u0007"+
		"\u0001\u0000\u0000\u0000cg\u0003\n\u0005\u0000dg\u0003\f\u0006\u0000e"+
		"g\u0003\u000e\u0007\u0000fc\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000"+
		"\u0000fe\u0001\u0000\u0000\u0000g\t\u0001\u0000\u0000\u0000hi\u0005\r"+
		"\u0000\u0000ij\u0005\u0007\u0000\u0000jk\u0005\u0005\u0000\u0000k\u000b"+
		"\u0001\u0000\u0000\u0000lm\u0005\u000e\u0000\u0000mn\u0005\u0007\u0000"+
		"\u0000no\u0005\u0005\u0000\u0000o\r\u0001\u0000\u0000\u0000pq\u0005\u000f"+
		"\u0000\u0000qr\u0005\u0007\u0000\u0000rs\u0005\u0005\u0000\u0000s\u000f"+
		"\u0001\u0000\u0000\u0000tu\u0005\u0010\u0000\u0000uw\u0005\u0003\u0000"+
		"\u0000vx\u0003\u0012\t\u0000wv\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000"+
		"\u0000x}\u0001\u0000\u0000\u0000yz\u0005\u0006\u0000\u0000z|\u0003\u0012"+
		"\t\u0000{y\u0001\u0000\u0000\u0000|\u007f\u0001\u0000\u0000\u0000}{\u0001"+
		"\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u0080\u0001\u0000\u0000"+
		"\u0000\u007f}\u0001\u0000\u0000\u0000\u0080\u0081\u0005\u0004\u0000\u0000"+
		"\u0081\u0011\u0001\u0000\u0000\u0000\u0082\u0087\u0003\u0014\n\u0000\u0083"+
		"\u0087\u0003\u0016\u000b\u0000\u0084\u0087\u0003\u0018\f\u0000\u0085\u0087"+
		"\u0003\u001a\r\u0000\u0086\u0082\u0001\u0000\u0000\u0000\u0086\u0083\u0001"+
		"\u0000\u0000\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0085\u0001"+
		"\u0000\u0000\u0000\u0087\u0013\u0001\u0000\u0000\u0000\u0088\u0089\u0005"+
		"\u0011\u0000\u0000\u0089\u008a\u0005\u0007\u0000\u0000\u008a\u008b\u0005"+
		"\u0005\u0000\u0000\u008b\u0015\u0001\u0000\u0000\u0000\u008c\u008d\u0005"+
		"\u0012\u0000\u0000\u008d\u008e\u0005\u0007\u0000\u0000\u008e\u008f\u0005"+
		"\u0005\u0000\u0000\u008f\u0017\u0001\u0000\u0000\u0000\u0090\u0091\u0005"+
		"\u0013\u0000\u0000\u0091\u0092\u0005\u0007\u0000\u0000\u0092\u0093\u0005"+
		"\u0005\u0000\u0000\u0093\u0019\u0001\u0000\u0000\u0000\u0094\u0095\u0005"+
		"\u0014\u0000\u0000\u0095\u0096\u0005\u0007\u0000\u0000\u0096\u0097\u0005"+
		"\u0005\u0000\u0000\u0097\u001b\u0001\u0000\u0000\u0000\u0098\u0099\u0005"+
		"\u0015\u0000\u0000\u0099\u009b\u0005\u0003\u0000\u0000\u009a\u009c\u0003"+
		" \u0010\u0000\u009b\u009a\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000"+
		"\u0000\u0000\u009c\u00a1\u0001\u0000\u0000\u0000\u009d\u009e\u0005\u0006"+
		"\u0000\u0000\u009e\u00a0\u0003 \u0010\u0000\u009f\u009d\u0001\u0000\u0000"+
		"\u0000\u00a0\u00a3\u0001\u0000\u0000\u0000\u00a1\u009f\u0001\u0000\u0000"+
		"\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a4\u0001\u0000\u0000"+
		"\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005\u0004\u0000"+
		"\u0000\u00a5\u001d\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005\u0016\u0000"+
		"\u0000\u00a7\u00a9\u0005\u0003\u0000\u0000\u00a8\u00aa\u0003 \u0010\u0000"+
		"\u00a9\u00a8\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000"+
		"\u00aa\u00af\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005\u0006\u0000\u0000"+
		"\u00ac\u00ae\u0003 \u0010\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000\u00ae"+
		"\u00b1\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000\u0000\u00af"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b0\u00b2\u0001\u0000\u0000\u0000\u00b1"+
		"\u00af\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\u0004\u0000\u0000\u00b3"+
		"\u001f\u0001\u0000\u0000\u0000\u00b4\u00b5\u0007\u0000\u0000\u0000\u00b5"+
		"!\u0001\u0000\u0000\u0000\u00b6\u00b7\u0005\u001c\u0000\u0000\u00b7\u00b8"+
		"\u0005\u0005\u0000\u0000\u00b8\u00b9\u0005\u0003\u0000\u0000\u00b9\u00bd"+
		"\u0003$\u0012\u0000\u00ba\u00bc\u0003&\u0013\u0000\u00bb\u00ba\u0001\u0000"+
		"\u0000\u0000\u00bc\u00bf\u0001\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000"+
		"\u0000\u0000\u00bd\u00be\u0001\u0000\u0000\u0000\u00be\u00c0\u0001\u0000"+
		"\u0000\u0000\u00bf\u00bd\u0001\u0000\u0000\u0000\u00c0\u00c1\u0005\u0004"+
		"\u0000\u0000\u00c1#\u0001\u0000\u0000\u0000\u00c2\u00c3\u0005\u001d\u0000"+
		"\u0000\u00c3\u00c4\u0005\u0003\u0000\u0000\u00c4\u00c5\u0003(\u0014\u0000"+
		"\u00c5\u00c6\u0005\u0004\u0000\u0000\u00c6%\u0001\u0000\u0000\u0000\u00c7"+
		"\u00c8\u0005\u001a\u0000\u0000\u00c8\u00c9\u0005\u0005\u0000\u0000\u00c9"+
		"\u00ca\u0005\u001b\u0000\u0000\u00ca\u00cb\u0005\u0005\u0000\u0000\u00cb"+
		"\u00cc\u0005\u0003\u0000\u0000\u00cc\u00cd\u0003(\u0014\u0000\u00cd\u00ce"+
		"\u0005\u0004\u0000\u0000\u00ce\'\u0001\u0000\u0000\u0000\u00cf\u00d4\u0003"+
		"*\u0015\u0000\u00d0\u00d4\u0003,\u0016\u0000\u00d1\u00d4\u0003.\u0017"+
		"\u0000\u00d2\u00d4\u00032\u0019\u0000\u00d3\u00cf\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d0\u0001\u0000\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d2\u0001\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000\u0000"+
		"\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000"+
		"\u00d6)\u0001\u0000\u0000\u0000\u00d7\u00d8\u0005\u0017\u0000\u0000\u00d8"+
		"\u00da\u0005\u0003\u0000\u0000\u00d9\u00db\u0003 \u0010\u0000\u00da\u00d9"+
		"\u0001\u0000\u0000\u0000\u00da\u00db\u0001\u0000\u0000\u0000\u00db\u00e0"+
		"\u0001\u0000\u0000\u0000\u00dc\u00dd\u0005\u0006\u0000\u0000\u00dd\u00df"+
		"\u0003 \u0010\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00df\u00e2\u0001"+
		"\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e3\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001"+
		"\u0000\u0000\u0000\u00e3\u00e4\u0005\u0004\u0000\u0000\u00e4+\u0001\u0000"+
		"\u0000\u0000\u00e5\u00e6\u0005\u0018\u0000\u0000\u00e6\u00e8\u0005\u0003"+
		"\u0000\u0000\u00e7\u00e9\u00030\u0018\u0000\u00e8\u00e7\u0001\u0000\u0000"+
		"\u0000\u00e8\u00e9\u0001\u0000\u0000\u0000\u00e9\u00ee\u0001\u0000\u0000"+
		"\u0000\u00ea\u00eb\u0005\u0006\u0000\u0000\u00eb\u00ed\u00030\u0018\u0000"+
		"\u00ec\u00ea\u0001\u0000\u0000\u0000\u00ed\u00f0\u0001\u0000\u0000\u0000"+
		"\u00ee\u00ec\u0001\u0000\u0000\u0000\u00ee\u00ef\u0001\u0000\u0000\u0000"+
		"\u00ef\u00f1\u0001\u0000\u0000\u0000\u00f0\u00ee\u0001\u0000\u0000\u0000"+
		"\u00f1\u00f2\u0005\u0004\u0000\u0000\u00f2-\u0001\u0000\u0000\u0000\u00f3"+
		"\u00f4\u0005\u0019\u0000\u0000\u00f4\u00f6\u0005\u0003\u0000\u0000\u00f5"+
		"\u00f7\u00030\u0018\u0000\u00f6\u00f5\u0001\u0000\u0000\u0000\u00f6\u00f7"+
		"\u0001\u0000\u0000\u0000\u00f7\u00fc\u0001\u0000\u0000\u0000\u00f8\u00f9"+
		"\u0005\u0006\u0000\u0000\u00f9\u00fb\u00030\u0018\u0000\u00fa\u00f8\u0001"+
		"\u0000\u0000\u0000\u00fb\u00fe\u0001\u0000\u0000\u0000\u00fc\u00fa\u0001"+
		"\u0000\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000\u0000\u00fd\u00ff\u0001"+
		"\u0000\u0000\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000\u00ff\u0100\u0005"+
		"\u0004\u0000\u0000\u0100/\u0001\u0000\u0000\u0000\u0101\u0102\u0005\u0005"+
		"\u0000\u0000\u0102\u0103\u0005\u0007\u0000\u0000\u0103\u0104\u0005\u0005"+
		"\u0000\u0000\u01041\u0001\u0000\u0000\u0000\u0105\u0106\u0005\u001e\u0000"+
		"\u0000\u0106\u010a\u0005\u0003\u0000\u0000\u0107\u0109\u00034\u001a\u0000"+
		"\u0108\u0107\u0001\u0000\u0000\u0000\u0109\u010c\u0001\u0000\u0000\u0000"+
		"\u010a\u0108\u0001\u0000\u0000\u0000\u010a\u010b\u0001\u0000\u0000\u0000"+
		"\u010b\u010d\u0001\u0000\u0000\u0000\u010c\u010a\u0001\u0000\u0000\u0000"+
		"\u010d\u010e\u0005\u0004\u0000\u0000\u010e3\u0001\u0000\u0000\u0000\u010f"+
		"\u0111\u0005\u001f\u0000\u0000\u0110\u0112\u00038\u001c\u0000\u0111\u0110"+
		"\u0001\u0000\u0000\u0000\u0111\u0112\u0001\u0000\u0000\u0000\u0112\u0113"+
		"\u0001\u0000\u0000\u0000\u0113\u0118\u0005#\u0000\u0000\u0114\u0117\u0003"+
		"6\u001b\u0000\u0115\u0117\u00034\u001a\u0000\u0116\u0114\u0001\u0000\u0000"+
		"\u0000\u0116\u0115\u0001\u0000\u0000\u0000\u0117\u011a\u0001\u0000\u0000"+
		"\u0000\u0118\u0116\u0001\u0000\u0000\u0000\u0118\u0119\u0001\u0000\u0000"+
		"\u0000\u0119\u011b\u0001\u0000\u0000\u0000\u011a\u0118\u0001\u0000\u0000"+
		"\u0000\u011b\u011c\u0005\u0004\u0000\u0000\u011c5\u0001\u0000\u0000\u0000"+
		"\u011d\u011f\u0005\u001f\u0000\u0000\u011e\u0120\u00038\u001c\u0000\u011f"+
		"\u011e\u0001\u0000\u0000\u0000\u011f\u0120\u0001\u0000\u0000\u0000\u0120"+
		"\u0121\u0001\u0000\u0000\u0000\u0121\u0122\u0005\"\u0000\u0000\u01227"+
		"\u0001\u0000\u0000\u0000\u0123\u0124\u0005!\u0000\u0000\u0124\u0125\u0003"+
		":\u001d\u0000\u0125\u0126\u0005&\u0000\u0000\u01269\u0001\u0000\u0000"+
		"\u0000\u0127\u012c\u0003<\u001e\u0000\u0128\u0129\u0005)\u0000\u0000\u0129"+
		"\u012b\u0003<\u001e\u0000\u012a\u0128\u0001\u0000\u0000\u0000\u012b\u012e"+
		"\u0001\u0000\u0000\u0000\u012c\u012a\u0001\u0000\u0000\u0000\u012c\u012d"+
		"\u0001\u0000\u0000\u0000\u012d;\u0001\u0000\u0000\u0000\u012e\u012c\u0001"+
		"\u0000\u0000\u0000\u012f\u0130\u0005\'\u0000\u0000\u0130\u0131\u0005("+
		"\u0000\u0000\u0131\u0132\u0005%\u0000\u0000\u0132\u0133\u0003>\u001f\u0000"+
		"\u0133\u0134\u0005+\u0000\u0000\u0134=\u0001\u0000\u0000\u0000\u0135\u0137"+
		"\u0007\u0001\u0000\u0000\u0136\u0135\u0001\u0000\u0000\u0000\u0137\u013a"+
		"\u0001\u0000\u0000\u0000\u0138\u0136\u0001\u0000\u0000\u0000\u0138\u0139"+
		"\u0001\u0000\u0000\u0000\u0139?\u0001\u0000\u0000\u0000\u013a\u0138\u0001"+
		"\u0000\u0000\u0000\u001cLQX^fw}\u0086\u009b\u00a1\u00a9\u00af\u00bd\u00d3"+
		"\u00d5\u00da\u00e0\u00e8\u00ee\u00f6\u00fc\u010a\u0111\u0116\u0118\u011f"+
		"\u012c\u0138";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}