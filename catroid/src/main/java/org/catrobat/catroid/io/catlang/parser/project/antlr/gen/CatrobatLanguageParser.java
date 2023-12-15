// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/project/antlr/CatrobatLanguageParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.project.antlr.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParserListener;
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParserVisitor;

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
		OF_TYPE=27, SCENE=28, BACKGROUND=29, SCRIPTS=30, USER_DEFINED_SCRIPTS=31, 
		UDB_DEFINE=32, NOTE_BRICK=33, DISABLED_BRICK_INDICATION=34, BRICK_NAME=35, 
		UDB_START=36, BRICK_MODE_WS=37, BRICK_MODE_BRACKET_OPEN=38, SEMICOLON=39, 
		BRICK_BODY_OPEN=40, UDB_MODE_WS=41, UDB_PARAM_START=42, UDB_LABEL=43, 
		UDB_END=44, UDB_PARAM_MODE_WS=45, UDB_PARAM_END=46, UDB_PARAM_TEXT=47, 
		UDB_AFTER_MODE_WS=48, UDB_MODE_BRACKET_OPEN=49, UDB_SEMICOLON=50, UDB_DEFINITION_SCREEN_REFRESH=51, 
		UDB_DEFINITION_NO_SCREEN_REFRESH=52, UDB_BODY_OPEN=53, PARAM_MODE_WS=54, 
		PARAM_MODE_BRACKET_OPEN=55, PARAM_MODE_BRACKET_CLOSE=56, PARAM_MODE_NAME=57, 
		PARAM_MODE_COLON=58, PARAM_SEPARATOR=59, FORMULA_MODE_WS=60, FORMULA_MODE_BRACKET_CLOSE=61, 
		FORMULA_MODE_BRACKET_OPEN=62, FORMULA_MODE_ANYTHING=63, FORMULA_MODE_APOSTROPHE=64, 
		FORMULA_MODE_QUOTE=65, FORMULA_MODE_UDB_PARAM=66, ESCAPE_MODE_APOSTROPHE_ANYTHING=67, 
		ESCAPE_MODE_APOSTROPHE_CHAR=68, ESCAPE_MODE_QUOTE_ANYTHING=69, ESCAPE_MODE_QUOTE_CHAR=70, 
		ESCAPE_UDB_PARAM_MODE_ANYTHING=71, ESCAPE_UDB_PARAM_MODE_CHAR=72;
	public static final int
		RULE_program = 0, RULE_programHeader = 1, RULE_programBody = 2, RULE_metadata = 3, 
		RULE_metadataContent = 4, RULE_description = 5, RULE_catrobatVersion = 6, 
		RULE_catrobatAppVersion = 7, RULE_stage = 8, RULE_stageContent = 9, RULE_landscapeMode = 10, 
		RULE_height = 11, RULE_width = 12, RULE_displayMode = 13, RULE_globals = 14, 
		RULE_multiplayerVariables = 15, RULE_variableOrListDeclaration = 16, RULE_variableDeclaration = 17, 
		RULE_scene = 18, RULE_background = 19, RULE_actor = 20, RULE_actorContent = 21, 
		RULE_localVariables = 22, RULE_looks = 23, RULE_sounds = 24, RULE_looksAndSoundsContent = 25, 
		RULE_scripts = 26, RULE_userDefinedScripts = 27, RULE_userDefinedScript = 28, 
		RULE_brick_defintion = 29, RULE_userDefinedBrick = 30, RULE_userDefinedBrickPart = 31, 
		RULE_brick_with_body = 32, RULE_brick_invocation = 33, RULE_brick_condition = 34, 
		RULE_arg_list = 35, RULE_argument = 36, RULE_formula = 37;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "programHeader", "programBody", "metadata", "metadataContent", 
			"description", "catrobatVersion", "catrobatAppVersion", "stage", "stageContent", 
			"landscapeMode", "height", "width", "displayMode", "globals", "multiplayerVariables", 
			"variableOrListDeclaration", "variableDeclaration", "scene", "background", 
			"actor", "actorContent", "localVariables", "looks", "sounds", "looksAndSoundsContent", 
			"scripts", "userDefinedScripts", "userDefinedScript", "brick_defintion", 
			"userDefinedBrick", "userDefinedBrickPart", "brick_with_body", "brick_invocation", 
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
			"'User Defined Bricks'", "'Define'", null, "'//'", null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "'with screen refresh as'", "'without screen refresh as'"
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
			"OF_TYPE", "SCENE", "BACKGROUND", "SCRIPTS", "USER_DEFINED_SCRIPTS", 
			"UDB_DEFINE", "NOTE_BRICK", "DISABLED_BRICK_INDICATION", "BRICK_NAME", 
			"UDB_START", "BRICK_MODE_WS", "BRICK_MODE_BRACKET_OPEN", "SEMICOLON", 
			"BRICK_BODY_OPEN", "UDB_MODE_WS", "UDB_PARAM_START", "UDB_LABEL", "UDB_END", 
			"UDB_PARAM_MODE_WS", "UDB_PARAM_END", "UDB_PARAM_TEXT", "UDB_AFTER_MODE_WS", 
			"UDB_MODE_BRACKET_OPEN", "UDB_SEMICOLON", "UDB_DEFINITION_SCREEN_REFRESH", 
			"UDB_DEFINITION_NO_SCREEN_REFRESH", "UDB_BODY_OPEN", "PARAM_MODE_WS", 
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
			if ( listener instanceof CatrobatLanguageParserListener) ((CatrobatLanguageParserListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			programHeader();
			setState(77);
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
			setState(79);
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
			setState(81);
			match(PROGRAM);
			setState(82);
			match(STRING);
			setState(83);
			match(CURLY_BRACKET_OPEN);
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6361088L) != 0)) {
				{
				setState(88);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case METADATA:
					{
					setState(84);
					metadata();
					}
					break;
				case STAGE:
					{
					setState(85);
					stage();
					}
					break;
				case GLOBALS:
					{
					setState(86);
					globals();
					}
					break;
				case MULTIPLAYER_VARIABLES:
					{
					setState(87);
					multiplayerVariables();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(92);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(94); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(93);
				scene();
				}
				}
				setState(96); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCENE );
			setState(98);
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
			setState(100);
			match(METADATA);
			setState(101);
			match(CURLY_BRACKET_OPEN);
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 57344L) != 0)) {
				{
				setState(102);
				metadataContent();
				}
			}

			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(105);
				match(SEPARATOR);
				setState(106);
				metadataContent();
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(112);
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
			setState(117);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DESCRIPTION:
				{
				setState(114);
				description();
				}
				break;
			case CATROBAT_VERSION:
				{
				setState(115);
				catrobatVersion();
				}
				break;
			case CATRPBAT_APP_VERSION:
				{
				setState(116);
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
			setState(119);
			match(DESCRIPTION);
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
			setState(123);
			match(CATROBAT_VERSION);
			setState(124);
			match(COLON);
			setState(125);
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
			setState(127);
			match(CATRPBAT_APP_VERSION);
			setState(128);
			match(COLON);
			setState(129);
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
			setState(131);
			match(STAGE);
			setState(132);
			match(CURLY_BRACKET_OPEN);
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0)) {
				{
				setState(133);
				stageContent();
				}
			}

			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(136);
				match(SEPARATOR);
				setState(137);
				stageContent();
				}
				}
				setState(142);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(143);
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
			setState(149);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANDSCAPE_MODE:
				{
				setState(145);
				landscapeMode();
				}
				break;
			case HEIGHT:
				{
				setState(146);
				height();
				}
				break;
			case WIDTH:
				{
				setState(147);
				width();
				}
				break;
			case DISPLAY_MODE:
				{
				setState(148);
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
			setState(151);
			match(LANDSCAPE_MODE);
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
			setState(155);
			match(HEIGHT);
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
			setState(159);
			match(WIDTH);
			setState(160);
			match(COLON);
			setState(161);
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
			setState(163);
			match(DISPLAY_MODE);
			setState(164);
			match(COLON);
			setState(165);
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
			setState(167);
			match(GLOBALS);
			setState(168);
			match(CURLY_BRACKET_OPEN);
			setState(170);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(169);
				variableOrListDeclaration();
				}
			}

			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(172);
				match(SEPARATOR);
				setState(173);
				variableOrListDeclaration();
				}
				}
				setState(178);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(179);
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
			setState(181);
			match(MULTIPLAYER_VARIABLES);
			setState(182);
			match(CURLY_BRACKET_OPEN);
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF) {
				{
				setState(183);
				variableDeclaration();
				}
			}

			setState(190);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(186);
				match(SEPARATOR);
				setState(187);
				variableDeclaration();
				}
				}
				setState(192);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(193);
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
			setState(197);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VARIABLE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(195);
				variableDeclaration();
				}
				break;
			case LIST_REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(196);
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
			setState(199);
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
			setState(201);
			match(SCENE);
			setState(202);
			match(STRING);
			setState(203);
			match(CURLY_BRACKET_OPEN);
			setState(204);
			background();
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ACTOR_OR_OBJECT) {
				{
				{
				setState(205);
				actor();
				}
				}
				setState(210);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(211);
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
			setState(213);
			match(BACKGROUND);
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
			setState(218);
			match(ACTOR_OR_OBJECT);
			setState(219);
			match(STRING);
			setState(220);
			match(OF_TYPE);
			setState(221);
			match(STRING);
			setState(222);
			match(CURLY_BRACKET_OPEN);
			setState(223);
			actorContent();
			setState(224);
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
		public List<UserDefinedScriptsContext> userDefinedScripts() {
			return getRuleContexts(UserDefinedScriptsContext.class);
		}
		public UserDefinedScriptsContext userDefinedScripts(int i) {
			return getRuleContext(UserDefinedScriptsContext.class,i);
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
			setState(231); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(231);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LOCAL_VARIABLES:
					{
					setState(226);
					localVariables();
					}
					break;
				case LOOKS:
					{
					setState(227);
					looks();
					}
					break;
				case SOUNDS:
					{
					setState(228);
					sounds();
					}
					break;
				case SCRIPTS:
					{
					setState(229);
					scripts();
					}
					break;
				case USER_DEFINED_SCRIPTS:
					{
					setState(230);
					userDefinedScripts();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(233); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 3279945728L) != 0) );
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
			setState(235);
			match(LOCAL_VARIABLES);
			setState(236);
			match(CURLY_BRACKET_OPEN);
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(237);
				variableOrListDeclaration();
				}
			}

			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(240);
				match(SEPARATOR);
				setState(241);
				variableOrListDeclaration();
				}
				}
				setState(246);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(247);
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
			setState(249);
			match(LOOKS);
			setState(250);
			match(CURLY_BRACKET_OPEN);
			setState(252);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(251);
				looksAndSoundsContent();
				}
			}

			setState(258);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(254);
				match(SEPARATOR);
				setState(255);
				looksAndSoundsContent();
				}
				}
				setState(260);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(261);
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
			setState(263);
			match(SOUNDS);
			setState(264);
			match(CURLY_BRACKET_OPEN);
			setState(266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(265);
				looksAndSoundsContent();
				}
			}

			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(268);
				match(SEPARATOR);
				setState(269);
				looksAndSoundsContent();
				}
				}
				setState(274);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(275);
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
			setState(277);
			match(STRING);
			setState(278);
			match(COLON);
			setState(279);
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
			setState(281);
			match(SCRIPTS);
			setState(282);
			match(CURLY_BRACKET_OPEN);
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DISABLED_BRICK_INDICATION || _la==BRICK_NAME) {
				{
				{
				setState(283);
				brick_with_body();
				}
				}
				setState(288);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(289);
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
	public static class UserDefinedScriptsContext extends ParserRuleContext {
		public TerminalNode USER_DEFINED_SCRIPTS() { return getToken(CatrobatLanguageParser.USER_DEFINED_SCRIPTS, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<UserDefinedScriptContext> userDefinedScript() {
			return getRuleContexts(UserDefinedScriptContext.class);
		}
		public UserDefinedScriptContext userDefinedScript(int i) {
			return getRuleContext(UserDefinedScriptContext.class,i);
		}
		public UserDefinedScriptsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userDefinedScripts; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterUserDefinedScripts(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitUserDefinedScripts(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitUserDefinedScripts(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UserDefinedScriptsContext userDefinedScripts() throws RecognitionException {
		UserDefinedScriptsContext _localctx = new UserDefinedScriptsContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_userDefinedScripts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(USER_DEFINED_SCRIPTS);
			setState(292);
			match(CURLY_BRACKET_OPEN);
			setState(296);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==UDB_DEFINE || _la==DISABLED_BRICK_INDICATION) {
				{
				{
				setState(293);
				userDefinedScript();
				}
				}
				setState(298);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(299);
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
	public static class UserDefinedScriptContext extends ParserRuleContext {
		public TerminalNode UDB_DEFINE() { return getToken(CatrobatLanguageParser.UDB_DEFINE, 0); }
		public UserDefinedBrickContext userDefinedBrick() {
			return getRuleContext(UserDefinedBrickContext.class,0);
		}
		public TerminalNode UDB_BODY_OPEN() { return getToken(CatrobatLanguageParser.UDB_BODY_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public TerminalNode UDB_DEFINITION_SCREEN_REFRESH() { return getToken(CatrobatLanguageParser.UDB_DEFINITION_SCREEN_REFRESH, 0); }
		public TerminalNode UDB_DEFINITION_NO_SCREEN_REFRESH() { return getToken(CatrobatLanguageParser.UDB_DEFINITION_NO_SCREEN_REFRESH, 0); }
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
		public UserDefinedScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userDefinedScript; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterUserDefinedScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitUserDefinedScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitUserDefinedScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UserDefinedScriptContext userDefinedScript() throws RecognitionException {
		UserDefinedScriptContext _localctx = new UserDefinedScriptContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_userDefinedScript);
		int _la;
		try {
			int _alt;
			setState(327);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case UDB_DEFINE:
				enterOuterAlt(_localctx, 1);
				{
				setState(301);
				match(UDB_DEFINE);
				setState(302);
				userDefinedBrick();
				setState(303);
				_la = _input.LA(1);
				if ( !(_la==UDB_DEFINITION_SCREEN_REFRESH || _la==UDB_DEFINITION_NO_SCREEN_REFRESH) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(304);
				match(UDB_BODY_OPEN);
				setState(308);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 128849018880L) != 0)) {
					{
					{
					setState(305);
					brick_defintion();
					}
					}
					setState(310);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(311);
				match(CURLY_BRACKET_CLOSE);
				}
				break;
			case DISABLED_BRICK_INDICATION:
				enterOuterAlt(_localctx, 2);
				{
				setState(313);
				match(DISABLED_BRICK_INDICATION);
				setState(314);
				match(UDB_DEFINE);
				setState(315);
				userDefinedBrick();
				setState(316);
				_la = _input.LA(1);
				if ( !(_la==UDB_DEFINITION_SCREEN_REFRESH || _la==UDB_DEFINITION_NO_SCREEN_REFRESH) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(317);
				match(UDB_BODY_OPEN);
				setState(321);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(318);
						brick_defintion();
						}
						} 
					}
					setState(323);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
				}
				setState(324);
				match(DISABLED_BRICK_INDICATION);
				setState(325);
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
		enterRule(_localctx, 58, RULE_brick_defintion);
		int _la;
		try {
			setState(335);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(330);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DISABLED_BRICK_INDICATION) {
					{
					setState(329);
					match(DISABLED_BRICK_INDICATION);
					}
				}

				setState(332);
				match(NOTE_BRICK);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(333);
				brick_invocation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(334);
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
	public static class UserDefinedBrickContext extends ParserRuleContext {
		public TerminalNode UDB_START() { return getToken(CatrobatLanguageParser.UDB_START, 0); }
		public TerminalNode UDB_END() { return getToken(CatrobatLanguageParser.UDB_END, 0); }
		public List<UserDefinedBrickPartContext> userDefinedBrickPart() {
			return getRuleContexts(UserDefinedBrickPartContext.class);
		}
		public UserDefinedBrickPartContext userDefinedBrickPart(int i) {
			return getRuleContext(UserDefinedBrickPartContext.class,i);
		}
		public UserDefinedBrickContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userDefinedBrick; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterUserDefinedBrick(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitUserDefinedBrick(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitUserDefinedBrick(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UserDefinedBrickContext userDefinedBrick() throws RecognitionException {
		UserDefinedBrickContext _localctx = new UserDefinedBrickContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_userDefinedBrick);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(UDB_START);
			setState(339); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(338);
				userDefinedBrickPart();
				}
				}
				setState(341); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==UDB_PARAM_START || _la==UDB_LABEL );
			setState(343);
			match(UDB_END);
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
	public static class UserDefinedBrickPartContext extends ParserRuleContext {
		public TerminalNode UDB_PARAM_START() { return getToken(CatrobatLanguageParser.UDB_PARAM_START, 0); }
		public TerminalNode UDB_PARAM_TEXT() { return getToken(CatrobatLanguageParser.UDB_PARAM_TEXT, 0); }
		public TerminalNode UDB_PARAM_END() { return getToken(CatrobatLanguageParser.UDB_PARAM_END, 0); }
		public TerminalNode UDB_LABEL() { return getToken(CatrobatLanguageParser.UDB_LABEL, 0); }
		public UserDefinedBrickPartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userDefinedBrickPart; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterUserDefinedBrickPart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitUserDefinedBrickPart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitUserDefinedBrickPart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UserDefinedBrickPartContext userDefinedBrickPart() throws RecognitionException {
		UserDefinedBrickPartContext _localctx = new UserDefinedBrickPartContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_userDefinedBrickPart);
		try {
			setState(349);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case UDB_PARAM_START:
				enterOuterAlt(_localctx, 1);
				{
				setState(345);
				match(UDB_PARAM_START);
				setState(346);
				match(UDB_PARAM_TEXT);
				setState(347);
				match(UDB_PARAM_END);
				}
				break;
			case UDB_LABEL:
				enterOuterAlt(_localctx, 2);
				{
				setState(348);
				match(UDB_LABEL);
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
		enterRule(_localctx, 64, RULE_brick_with_body);
		int _la;
		try {
			int _alt;
			setState(377);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRICK_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(351);
				match(BRICK_NAME);
				setState(353);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(352);
					brick_condition();
					}
				}

				setState(355);
				match(BRICK_BODY_OPEN);
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 128849018880L) != 0)) {
					{
					{
					setState(356);
					brick_defintion();
					}
					}
					setState(361);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(362);
				match(CURLY_BRACKET_CLOSE);
				}
				break;
			case DISABLED_BRICK_INDICATION:
				enterOuterAlt(_localctx, 2);
				{
				setState(363);
				match(DISABLED_BRICK_INDICATION);
				setState(364);
				match(BRICK_NAME);
				setState(366);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(365);
					brick_condition();
					}
				}

				setState(368);
				match(BRICK_BODY_OPEN);
				setState(372);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(369);
						brick_defintion();
						}
						} 
					}
					setState(374);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				}
				setState(375);
				match(DISABLED_BRICK_INDICATION);
				setState(376);
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
		public TerminalNode SEMICOLON() { return getToken(CatrobatLanguageParser.SEMICOLON, 0); }
		public TerminalNode UDB_SEMICOLON() { return getToken(CatrobatLanguageParser.UDB_SEMICOLON, 0); }
		public TerminalNode BRICK_NAME() { return getToken(CatrobatLanguageParser.BRICK_NAME, 0); }
		public UserDefinedBrickContext userDefinedBrick() {
			return getRuleContext(UserDefinedBrickContext.class,0);
		}
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
		enterRule(_localctx, 66, RULE_brick_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(380);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DISABLED_BRICK_INDICATION) {
				{
				setState(379);
				match(DISABLED_BRICK_INDICATION);
				}
			}

			setState(384);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRICK_NAME:
				{
				setState(382);
				match(BRICK_NAME);
				}
				break;
			case UDB_START:
				{
				setState(383);
				userDefinedBrick();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(387);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
				{
				setState(386);
				brick_condition();
				}
			}

			setState(389);
			_la = _input.LA(1);
			if ( !(_la==SEMICOLON || _la==UDB_SEMICOLON) ) {
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
	public static class Brick_conditionContext extends ParserRuleContext {
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public TerminalNode PARAM_MODE_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.PARAM_MODE_BRACKET_CLOSE, 0); }
		public TerminalNode BRICK_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.BRICK_MODE_BRACKET_OPEN, 0); }
		public TerminalNode UDB_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.UDB_MODE_BRACKET_OPEN, 0); }
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
		enterRule(_localctx, 68, RULE_brick_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(391);
			_la = _input.LA(1);
			if ( !(_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(392);
			arg_list();
			setState(393);
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
		enterRule(_localctx, 70, RULE_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			argument();
			setState(400);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAM_SEPARATOR) {
				{
				{
				setState(396);
				match(PARAM_SEPARATOR);
				setState(397);
				argument();
				}
				}
				setState(402);
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
		enterRule(_localctx, 72, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
			match(PARAM_MODE_NAME);
			setState(404);
			match(PARAM_MODE_COLON);
			setState(405);
			match(PARAM_MODE_BRACKET_OPEN);
			setState(406);
			formula();
			setState(407);
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
		enterRule(_localctx, 74, RULE_formula);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(409);
					_la = _input.LA(1);
					if ( !(((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & 2751L) != 0)) ) {
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
				setState(414);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
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
		"\u0004\u0001H\u01a0\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002Y\b\u0002\n\u0002\f\u0002"+
		"\\\t\u0002\u0001\u0002\u0004\u0002_\b\u0002\u000b\u0002\f\u0002`\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003h\b"+
		"\u0003\u0001\u0003\u0001\u0003\u0005\u0003l\b\u0003\n\u0003\f\u0003o\t"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003"+
		"\u0004v\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0003\b\u0087\b\b\u0001\b\u0001"+
		"\b\u0005\b\u008b\b\b\n\b\f\b\u008e\t\b\u0001\b\u0001\b\u0001\t\u0001\t"+
		"\u0001\t\u0001\t\u0003\t\u0096\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0003\u000e\u00ab\b\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u00af\b"+
		"\u000e\n\u000e\f\u000e\u00b2\t\u000e\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0003\u000f\u00b9\b\u000f\u0001\u000f\u0001\u000f"+
		"\u0005\u000f\u00bd\b\u000f\n\u000f\f\u000f\u00c0\t\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u00c6\b\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005"+
		"\u0012\u00cf\b\u0012\n\u0012\f\u0012\u00d2\t\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0004\u0015\u00e8\b\u0015\u000b\u0015\f\u0015\u00e9\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0003\u0016\u00ef\b\u0016\u0001\u0016\u0001\u0016\u0005"+
		"\u0016\u00f3\b\u0016\n\u0016\f\u0016\u00f6\t\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u00fd\b\u0017\u0001\u0017"+
		"\u0001\u0017\u0005\u0017\u0101\b\u0017\n\u0017\f\u0017\u0104\t\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u010b"+
		"\b\u0018\u0001\u0018\u0001\u0018\u0005\u0018\u010f\b\u0018\n\u0018\f\u0018"+
		"\u0112\t\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u011d\b\u001a"+
		"\n\u001a\f\u001a\u0120\t\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0005\u001b\u0127\b\u001b\n\u001b\f\u001b\u012a\t\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0005\u001c\u0133\b\u001c\n\u001c\f\u001c\u0136\t\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0005\u001c\u0140\b\u001c\n\u001c\f\u001c\u0143\t\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0148\b\u001c\u0001\u001d"+
		"\u0003\u001d\u014b\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d"+
		"\u0150\b\u001d\u0001\u001e\u0001\u001e\u0004\u001e\u0154\b\u001e\u000b"+
		"\u001e\f\u001e\u0155\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0003\u001f\u015e\b\u001f\u0001 \u0001 \u0003 \u0162"+
		"\b \u0001 \u0001 \u0005 \u0166\b \n \f \u0169\t \u0001 \u0001 \u0001 "+
		"\u0001 \u0003 \u016f\b \u0001 \u0001 \u0005 \u0173\b \n \f \u0176\t \u0001"+
		" \u0001 \u0003 \u017a\b \u0001!\u0003!\u017d\b!\u0001!\u0001!\u0003!\u0181"+
		"\b!\u0001!\u0003!\u0184\b!\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001"+
		"\"\u0001#\u0001#\u0001#\u0005#\u018f\b#\n#\f#\u0192\t#\u0001$\u0001$\u0001"+
		"$\u0001$\u0001$\u0001$\u0001%\u0005%\u019b\b%\n%\f%\u019e\t%\u0001%\u0000"+
		"\u0000&\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJ\u0000\u0004\u0001\u000034\u0002"+
		"\u0000\'\'22\u0002\u0000&&11\u0004\u0000=BDDFFHH\u01ac\u0000L\u0001\u0000"+
		"\u0000\u0000\u0002O\u0001\u0000\u0000\u0000\u0004Q\u0001\u0000\u0000\u0000"+
		"\u0006d\u0001\u0000\u0000\u0000\bu\u0001\u0000\u0000\u0000\nw\u0001\u0000"+
		"\u0000\u0000\f{\u0001\u0000\u0000\u0000\u000e\u007f\u0001\u0000\u0000"+
		"\u0000\u0010\u0083\u0001\u0000\u0000\u0000\u0012\u0095\u0001\u0000\u0000"+
		"\u0000\u0014\u0097\u0001\u0000\u0000\u0000\u0016\u009b\u0001\u0000\u0000"+
		"\u0000\u0018\u009f\u0001\u0000\u0000\u0000\u001a\u00a3\u0001\u0000\u0000"+
		"\u0000\u001c\u00a7\u0001\u0000\u0000\u0000\u001e\u00b5\u0001\u0000\u0000"+
		"\u0000 \u00c5\u0001\u0000\u0000\u0000\"\u00c7\u0001\u0000\u0000\u0000"+
		"$\u00c9\u0001\u0000\u0000\u0000&\u00d5\u0001\u0000\u0000\u0000(\u00da"+
		"\u0001\u0000\u0000\u0000*\u00e7\u0001\u0000\u0000\u0000,\u00eb\u0001\u0000"+
		"\u0000\u0000.\u00f9\u0001\u0000\u0000\u00000\u0107\u0001\u0000\u0000\u0000"+
		"2\u0115\u0001\u0000\u0000\u00004\u0119\u0001\u0000\u0000\u00006\u0123"+
		"\u0001\u0000\u0000\u00008\u0147\u0001\u0000\u0000\u0000:\u014f\u0001\u0000"+
		"\u0000\u0000<\u0151\u0001\u0000\u0000\u0000>\u015d\u0001\u0000\u0000\u0000"+
		"@\u0179\u0001\u0000\u0000\u0000B\u017c\u0001\u0000\u0000\u0000D\u0187"+
		"\u0001\u0000\u0000\u0000F\u018b\u0001\u0000\u0000\u0000H\u0193\u0001\u0000"+
		"\u0000\u0000J\u019c\u0001\u0000\u0000\u0000LM\u0003\u0002\u0001\u0000"+
		"MN\u0003\u0004\u0002\u0000N\u0001\u0001\u0000\u0000\u0000OP\u0005\n\u0000"+
		"\u0000P\u0003\u0001\u0000\u0000\u0000QR\u0005\u000b\u0000\u0000RS\u0005"+
		"\u0005\u0000\u0000SZ\u0005\u0003\u0000\u0000TY\u0003\u0006\u0003\u0000"+
		"UY\u0003\u0010\b\u0000VY\u0003\u001c\u000e\u0000WY\u0003\u001e\u000f\u0000"+
		"XT\u0001\u0000\u0000\u0000XU\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000"+
		"\u0000XW\u0001\u0000\u0000\u0000Y\\\u0001\u0000\u0000\u0000ZX\u0001\u0000"+
		"\u0000\u0000Z[\u0001\u0000\u0000\u0000[^\u0001\u0000\u0000\u0000\\Z\u0001"+
		"\u0000\u0000\u0000]_\u0003$\u0012\u0000^]\u0001\u0000\u0000\u0000_`\u0001"+
		"\u0000\u0000\u0000`^\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000"+
		"ab\u0001\u0000\u0000\u0000bc\u0005\u0004\u0000\u0000c\u0005\u0001\u0000"+
		"\u0000\u0000de\u0005\f\u0000\u0000eg\u0005\u0003\u0000\u0000fh\u0003\b"+
		"\u0004\u0000gf\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hm\u0001"+
		"\u0000\u0000\u0000ij\u0005\u0006\u0000\u0000jl\u0003\b\u0004\u0000ki\u0001"+
		"\u0000\u0000\u0000lo\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000"+
		"mn\u0001\u0000\u0000\u0000np\u0001\u0000\u0000\u0000om\u0001\u0000\u0000"+
		"\u0000pq\u0005\u0004\u0000\u0000q\u0007\u0001\u0000\u0000\u0000rv\u0003"+
		"\n\u0005\u0000sv\u0003\f\u0006\u0000tv\u0003\u000e\u0007\u0000ur\u0001"+
		"\u0000\u0000\u0000us\u0001\u0000\u0000\u0000ut\u0001\u0000\u0000\u0000"+
		"v\t\u0001\u0000\u0000\u0000wx\u0005\r\u0000\u0000xy\u0005\u0007\u0000"+
		"\u0000yz\u0005\u0005\u0000\u0000z\u000b\u0001\u0000\u0000\u0000{|\u0005"+
		"\u000e\u0000\u0000|}\u0005\u0007\u0000\u0000}~\u0005\u0005\u0000\u0000"+
		"~\r\u0001\u0000\u0000\u0000\u007f\u0080\u0005\u000f\u0000\u0000\u0080"+
		"\u0081\u0005\u0007\u0000\u0000\u0081\u0082\u0005\u0005\u0000\u0000\u0082"+
		"\u000f\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u0010\u0000\u0000\u0084"+
		"\u0086\u0005\u0003\u0000\u0000\u0085\u0087\u0003\u0012\t\u0000\u0086\u0085"+
		"\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u008c"+
		"\u0001\u0000\u0000\u0000\u0088\u0089\u0005\u0006\u0000\u0000\u0089\u008b"+
		"\u0003\u0012\t\u0000\u008a\u0088\u0001\u0000\u0000\u0000\u008b\u008e\u0001"+
		"\u0000\u0000\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001"+
		"\u0000\u0000\u0000\u008d\u008f\u0001\u0000\u0000\u0000\u008e\u008c\u0001"+
		"\u0000\u0000\u0000\u008f\u0090\u0005\u0004\u0000\u0000\u0090\u0011\u0001"+
		"\u0000\u0000\u0000\u0091\u0096\u0003\u0014\n\u0000\u0092\u0096\u0003\u0016"+
		"\u000b\u0000\u0093\u0096\u0003\u0018\f\u0000\u0094\u0096\u0003\u001a\r"+
		"\u0000\u0095\u0091\u0001\u0000\u0000\u0000\u0095\u0092\u0001\u0000\u0000"+
		"\u0000\u0095\u0093\u0001\u0000\u0000\u0000\u0095\u0094\u0001\u0000\u0000"+
		"\u0000\u0096\u0013\u0001\u0000\u0000\u0000\u0097\u0098\u0005\u0011\u0000"+
		"\u0000\u0098\u0099\u0005\u0007\u0000\u0000\u0099\u009a\u0005\u0005\u0000"+
		"\u0000\u009a\u0015\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u0012\u0000"+
		"\u0000\u009c\u009d\u0005\u0007\u0000\u0000\u009d\u009e\u0005\u0005\u0000"+
		"\u0000\u009e\u0017\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u0013\u0000"+
		"\u0000\u00a0\u00a1\u0005\u0007\u0000\u0000\u00a1\u00a2\u0005\u0005\u0000"+
		"\u0000\u00a2\u0019\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005\u0014\u0000"+
		"\u0000\u00a4\u00a5\u0005\u0007\u0000\u0000\u00a5\u00a6\u0005\u0005\u0000"+
		"\u0000\u00a6\u001b\u0001\u0000\u0000\u0000\u00a7\u00a8\u0005\u0015\u0000"+
		"\u0000\u00a8\u00aa\u0005\u0003\u0000\u0000\u00a9\u00ab\u0003 \u0010\u0000"+
		"\u00aa\u00a9\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000"+
		"\u00ab\u00b0\u0001\u0000\u0000\u0000\u00ac\u00ad\u0005\u0006\u0000\u0000"+
		"\u00ad\u00af\u0003 \u0010\u0000\u00ae\u00ac\u0001\u0000\u0000\u0000\u00af"+
		"\u00b2\u0001\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b3\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b3\u00b4\u0005\u0004\u0000\u0000\u00b4"+
		"\u001d\u0001\u0000\u0000\u0000\u00b5\u00b6\u0005\u0016\u0000\u0000\u00b6"+
		"\u00b8\u0005\u0003\u0000\u0000\u00b7\u00b9\u0003\"\u0011\u0000\u00b8\u00b7"+
		"\u0001\u0000\u0000\u0000\u00b8\u00b9\u0001\u0000\u0000\u0000\u00b9\u00be"+
		"\u0001\u0000\u0000\u0000\u00ba\u00bb\u0005\u0006\u0000\u0000\u00bb\u00bd"+
		"\u0003\"\u0011\u0000\u00bc\u00ba\u0001\u0000\u0000\u0000\u00bd\u00c0\u0001"+
		"\u0000\u0000\u0000\u00be\u00bc\u0001\u0000\u0000\u0000\u00be\u00bf\u0001"+
		"\u0000\u0000\u0000\u00bf\u00c1\u0001\u0000\u0000\u0000\u00c0\u00be\u0001"+
		"\u0000\u0000\u0000\u00c1\u00c2\u0005\u0004\u0000\u0000\u00c2\u001f\u0001"+
		"\u0000\u0000\u0000\u00c3\u00c6\u0003\"\u0011\u0000\u00c4\u00c6\u0005\t"+
		"\u0000\u0000\u00c5\u00c3\u0001\u0000\u0000\u0000\u00c5\u00c4\u0001\u0000"+
		"\u0000\u0000\u00c6!\u0001\u0000\u0000\u0000\u00c7\u00c8\u0005\b\u0000"+
		"\u0000\u00c8#\u0001\u0000\u0000\u0000\u00c9\u00ca\u0005\u001c\u0000\u0000"+
		"\u00ca\u00cb\u0005\u0005\u0000\u0000\u00cb\u00cc\u0005\u0003\u0000\u0000"+
		"\u00cc\u00d0\u0003&\u0013\u0000\u00cd\u00cf\u0003(\u0014\u0000\u00ce\u00cd"+
		"\u0001\u0000\u0000\u0000\u00cf\u00d2\u0001\u0000\u0000\u0000\u00d0\u00ce"+
		"\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d3"+
		"\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d3\u00d4"+
		"\u0005\u0004\u0000\u0000\u00d4%\u0001\u0000\u0000\u0000\u00d5\u00d6\u0005"+
		"\u001d\u0000\u0000\u00d6\u00d7\u0005\u0003\u0000\u0000\u00d7\u00d8\u0003"+
		"*\u0015\u0000\u00d8\u00d9\u0005\u0004\u0000\u0000\u00d9\'\u0001\u0000"+
		"\u0000\u0000\u00da\u00db\u0005\u001a\u0000\u0000\u00db\u00dc\u0005\u0005"+
		"\u0000\u0000\u00dc\u00dd\u0005\u001b\u0000\u0000\u00dd\u00de\u0005\u0005"+
		"\u0000\u0000\u00de\u00df\u0005\u0003\u0000\u0000\u00df\u00e0\u0003*\u0015"+
		"\u0000\u00e0\u00e1\u0005\u0004\u0000\u0000\u00e1)\u0001\u0000\u0000\u0000"+
		"\u00e2\u00e8\u0003,\u0016\u0000\u00e3\u00e8\u0003.\u0017\u0000\u00e4\u00e8"+
		"\u00030\u0018\u0000\u00e5\u00e8\u00034\u001a\u0000\u00e6\u00e8\u00036"+
		"\u001b\u0000\u00e7\u00e2\u0001\u0000\u0000\u0000\u00e7\u00e3\u0001\u0000"+
		"\u0000\u0000\u00e7\u00e4\u0001\u0000\u0000\u0000\u00e7\u00e5\u0001\u0000"+
		"\u0000\u0000\u00e7\u00e6\u0001\u0000\u0000\u0000\u00e8\u00e9\u0001\u0000"+
		"\u0000\u0000\u00e9\u00e7\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000"+
		"\u0000\u0000\u00ea+\u0001\u0000\u0000\u0000\u00eb\u00ec\u0005\u0017\u0000"+
		"\u0000\u00ec\u00ee\u0005\u0003\u0000\u0000\u00ed\u00ef\u0003 \u0010\u0000"+
		"\u00ee\u00ed\u0001\u0000\u0000\u0000\u00ee\u00ef\u0001\u0000\u0000\u0000"+
		"\u00ef\u00f4\u0001\u0000\u0000\u0000\u00f0\u00f1\u0005\u0006\u0000\u0000"+
		"\u00f1\u00f3\u0003 \u0010\u0000\u00f2\u00f0\u0001\u0000\u0000\u0000\u00f3"+
		"\u00f6\u0001\u0000\u0000\u0000\u00f4\u00f2\u0001\u0000\u0000\u0000\u00f4"+
		"\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f7\u0001\u0000\u0000\u0000\u00f6"+
		"\u00f4\u0001\u0000\u0000\u0000\u00f7\u00f8\u0005\u0004\u0000\u0000\u00f8"+
		"-\u0001\u0000\u0000\u0000\u00f9\u00fa\u0005\u0018\u0000\u0000\u00fa\u00fc"+
		"\u0005\u0003\u0000\u0000\u00fb\u00fd\u00032\u0019\u0000\u00fc\u00fb\u0001"+
		"\u0000\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000\u0000\u00fd\u0102\u0001"+
		"\u0000\u0000\u0000\u00fe\u00ff\u0005\u0006\u0000\u0000\u00ff\u0101\u0003"+
		"2\u0019\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0101\u0104\u0001\u0000"+
		"\u0000\u0000\u0102\u0100\u0001\u0000\u0000\u0000\u0102\u0103\u0001\u0000"+
		"\u0000\u0000\u0103\u0105\u0001\u0000\u0000\u0000\u0104\u0102\u0001\u0000"+
		"\u0000\u0000\u0105\u0106\u0005\u0004\u0000\u0000\u0106/\u0001\u0000\u0000"+
		"\u0000\u0107\u0108\u0005\u0019\u0000\u0000\u0108\u010a\u0005\u0003\u0000"+
		"\u0000\u0109\u010b\u00032\u0019\u0000\u010a\u0109\u0001\u0000\u0000\u0000"+
		"\u010a\u010b\u0001\u0000\u0000\u0000\u010b\u0110\u0001\u0000\u0000\u0000"+
		"\u010c\u010d\u0005\u0006\u0000\u0000\u010d\u010f\u00032\u0019\u0000\u010e"+
		"\u010c\u0001\u0000\u0000\u0000\u010f\u0112\u0001\u0000\u0000\u0000\u0110"+
		"\u010e\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111"+
		"\u0113\u0001\u0000\u0000\u0000\u0112\u0110\u0001\u0000\u0000\u0000\u0113"+
		"\u0114\u0005\u0004\u0000\u0000\u01141\u0001\u0000\u0000\u0000\u0115\u0116"+
		"\u0005\u0005\u0000\u0000\u0116\u0117\u0005\u0007\u0000\u0000\u0117\u0118"+
		"\u0005\u0005\u0000\u0000\u01183\u0001\u0000\u0000\u0000\u0119\u011a\u0005"+
		"\u001e\u0000\u0000\u011a\u011e\u0005\u0003\u0000\u0000\u011b\u011d\u0003"+
		"@ \u0000\u011c\u011b\u0001\u0000\u0000\u0000\u011d\u0120\u0001\u0000\u0000"+
		"\u0000\u011e\u011c\u0001\u0000\u0000\u0000\u011e\u011f\u0001\u0000\u0000"+
		"\u0000\u011f\u0121\u0001\u0000\u0000\u0000\u0120\u011e\u0001\u0000\u0000"+
		"\u0000\u0121\u0122\u0005\u0004\u0000\u0000\u01225\u0001\u0000\u0000\u0000"+
		"\u0123\u0124\u0005\u001f\u0000\u0000\u0124\u0128\u0005\u0003\u0000\u0000"+
		"\u0125\u0127\u00038\u001c\u0000\u0126\u0125\u0001\u0000\u0000\u0000\u0127"+
		"\u012a\u0001\u0000\u0000\u0000\u0128\u0126\u0001\u0000\u0000\u0000\u0128"+
		"\u0129\u0001\u0000\u0000\u0000\u0129\u012b\u0001\u0000\u0000\u0000\u012a"+
		"\u0128\u0001\u0000\u0000\u0000\u012b\u012c\u0005\u0004\u0000\u0000\u012c"+
		"7\u0001\u0000\u0000\u0000\u012d\u012e\u0005 \u0000\u0000\u012e\u012f\u0003"+
		"<\u001e\u0000\u012f\u0130\u0007\u0000\u0000\u0000\u0130\u0134\u00055\u0000"+
		"\u0000\u0131\u0133\u0003:\u001d\u0000\u0132\u0131\u0001\u0000\u0000\u0000"+
		"\u0133\u0136\u0001\u0000\u0000\u0000\u0134\u0132\u0001\u0000\u0000\u0000"+
		"\u0134\u0135\u0001\u0000\u0000\u0000\u0135\u0137\u0001\u0000\u0000\u0000"+
		"\u0136\u0134\u0001\u0000\u0000\u0000\u0137\u0138\u0005\u0004\u0000\u0000"+
		"\u0138\u0148\u0001\u0000\u0000\u0000\u0139\u013a\u0005\"\u0000\u0000\u013a"+
		"\u013b\u0005 \u0000\u0000\u013b\u013c\u0003<\u001e\u0000\u013c\u013d\u0007"+
		"\u0000\u0000\u0000\u013d\u0141\u00055\u0000\u0000\u013e\u0140\u0003:\u001d"+
		"\u0000\u013f\u013e\u0001\u0000\u0000\u0000\u0140\u0143\u0001\u0000\u0000"+
		"\u0000\u0141\u013f\u0001\u0000\u0000\u0000\u0141\u0142\u0001\u0000\u0000"+
		"\u0000\u0142\u0144\u0001\u0000\u0000\u0000\u0143\u0141\u0001\u0000\u0000"+
		"\u0000\u0144\u0145\u0005\"\u0000\u0000\u0145\u0146\u0005\u0004\u0000\u0000"+
		"\u0146\u0148\u0001\u0000\u0000\u0000\u0147\u012d\u0001\u0000\u0000\u0000"+
		"\u0147\u0139\u0001\u0000\u0000\u0000\u01489\u0001\u0000\u0000\u0000\u0149"+
		"\u014b\u0005\"\u0000\u0000\u014a\u0149\u0001\u0000\u0000\u0000\u014a\u014b"+
		"\u0001\u0000\u0000\u0000\u014b\u014c\u0001\u0000\u0000\u0000\u014c\u0150"+
		"\u0005!\u0000\u0000\u014d\u0150\u0003B!\u0000\u014e\u0150\u0003@ \u0000"+
		"\u014f\u014a\u0001\u0000\u0000\u0000\u014f\u014d\u0001\u0000\u0000\u0000"+
		"\u014f\u014e\u0001\u0000\u0000\u0000\u0150;\u0001\u0000\u0000\u0000\u0151"+
		"\u0153\u0005$\u0000\u0000\u0152\u0154\u0003>\u001f\u0000\u0153\u0152\u0001"+
		"\u0000\u0000\u0000\u0154\u0155\u0001\u0000\u0000\u0000\u0155\u0153\u0001"+
		"\u0000\u0000\u0000\u0155\u0156\u0001\u0000\u0000\u0000\u0156\u0157\u0001"+
		"\u0000\u0000\u0000\u0157\u0158\u0005,\u0000\u0000\u0158=\u0001\u0000\u0000"+
		"\u0000\u0159\u015a\u0005*\u0000\u0000\u015a\u015b\u0005/\u0000\u0000\u015b"+
		"\u015e\u0005.\u0000\u0000\u015c\u015e\u0005+\u0000\u0000\u015d\u0159\u0001"+
		"\u0000\u0000\u0000\u015d\u015c\u0001\u0000\u0000\u0000\u015e?\u0001\u0000"+
		"\u0000\u0000\u015f\u0161\u0005#\u0000\u0000\u0160\u0162\u0003D\"\u0000"+
		"\u0161\u0160\u0001\u0000\u0000\u0000\u0161\u0162\u0001\u0000\u0000\u0000"+
		"\u0162\u0163\u0001\u0000\u0000\u0000\u0163\u0167\u0005(\u0000\u0000\u0164"+
		"\u0166\u0003:\u001d\u0000\u0165\u0164\u0001\u0000\u0000\u0000\u0166\u0169"+
		"\u0001\u0000\u0000\u0000\u0167\u0165\u0001\u0000\u0000\u0000\u0167\u0168"+
		"\u0001\u0000\u0000\u0000\u0168\u016a\u0001\u0000\u0000\u0000\u0169\u0167"+
		"\u0001\u0000\u0000\u0000\u016a\u017a\u0005\u0004\u0000\u0000\u016b\u016c"+
		"\u0005\"\u0000\u0000\u016c\u016e\u0005#\u0000\u0000\u016d\u016f\u0003"+
		"D\"\u0000\u016e\u016d\u0001\u0000\u0000\u0000\u016e\u016f\u0001\u0000"+
		"\u0000\u0000\u016f\u0170\u0001\u0000\u0000\u0000\u0170\u0174\u0005(\u0000"+
		"\u0000\u0171\u0173\u0003:\u001d\u0000\u0172\u0171\u0001\u0000\u0000\u0000"+
		"\u0173\u0176\u0001\u0000\u0000\u0000\u0174\u0172\u0001\u0000\u0000\u0000"+
		"\u0174\u0175\u0001\u0000\u0000\u0000\u0175\u0177\u0001\u0000\u0000\u0000"+
		"\u0176\u0174\u0001\u0000\u0000\u0000\u0177\u0178\u0005\"\u0000\u0000\u0178"+
		"\u017a\u0005\u0004\u0000\u0000\u0179\u015f\u0001\u0000\u0000\u0000\u0179"+
		"\u016b\u0001\u0000\u0000\u0000\u017aA\u0001\u0000\u0000\u0000\u017b\u017d"+
		"\u0005\"\u0000\u0000\u017c\u017b\u0001\u0000\u0000\u0000\u017c\u017d\u0001"+
		"\u0000\u0000\u0000\u017d\u0180\u0001\u0000\u0000\u0000\u017e\u0181\u0005"+
		"#\u0000\u0000\u017f\u0181\u0003<\u001e\u0000\u0180\u017e\u0001\u0000\u0000"+
		"\u0000\u0180\u017f\u0001\u0000\u0000\u0000\u0181\u0183\u0001\u0000\u0000"+
		"\u0000\u0182\u0184\u0003D\"\u0000\u0183\u0182\u0001\u0000\u0000\u0000"+
		"\u0183\u0184\u0001\u0000\u0000\u0000\u0184\u0185\u0001\u0000\u0000\u0000"+
		"\u0185\u0186\u0007\u0001\u0000\u0000\u0186C\u0001\u0000\u0000\u0000\u0187"+
		"\u0188\u0007\u0002\u0000\u0000\u0188\u0189\u0003F#\u0000\u0189\u018a\u0005"+
		"8\u0000\u0000\u018aE\u0001\u0000\u0000\u0000\u018b\u0190\u0003H$\u0000"+
		"\u018c\u018d\u0005;\u0000\u0000\u018d\u018f\u0003H$\u0000\u018e\u018c"+
		"\u0001\u0000\u0000\u0000\u018f\u0192\u0001\u0000\u0000\u0000\u0190\u018e"+
		"\u0001\u0000\u0000\u0000\u0190\u0191\u0001\u0000\u0000\u0000\u0191G\u0001"+
		"\u0000\u0000\u0000\u0192\u0190\u0001\u0000\u0000\u0000\u0193\u0194\u0005"+
		"9\u0000\u0000\u0194\u0195\u0005:\u0000\u0000\u0195\u0196\u00057\u0000"+
		"\u0000\u0196\u0197\u0003J%\u0000\u0197\u0198\u0005=\u0000\u0000\u0198"+
		"I\u0001\u0000\u0000\u0000\u0199\u019b\u0007\u0003\u0000\u0000\u019a\u0199"+
		"\u0001\u0000\u0000\u0000\u019b\u019e\u0001\u0000\u0000\u0000\u019c\u019a"+
		"\u0001\u0000\u0000\u0000\u019c\u019d\u0001\u0000\u0000\u0000\u019dK\u0001"+
		"\u0000\u0000\u0000\u019e\u019c\u0001\u0000\u0000\u0000*XZ`gmu\u0086\u008c"+
		"\u0095\u00aa\u00b0\u00b8\u00be\u00c5\u00d0\u00e7\u00e9\u00ee\u00f4\u00fc"+
		"\u0102\u010a\u0110\u011e\u0128\u0134\u0141\u0147\u014a\u014f\u0155\u015d"+
		"\u0161\u0167\u016e\u0174\u0179\u017c\u0180\u0183\u0190\u019c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}