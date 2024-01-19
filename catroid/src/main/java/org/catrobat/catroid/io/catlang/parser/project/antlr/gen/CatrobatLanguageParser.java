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
		SCRIPTS_WS=32, SCRIPT_DISABLED_INDICATOR=33, SCRIPTS_START=34, SCRIPT_NAME=35, 
		SCRIPTS_END=36, USER_DEFINED_SCRIPTS_WS=37, USER_DEFINED_SCRIPTS_START=38, 
		UDB_DEFINE=39, USER_DEFINED_SCRIPT_UDB_START=40, USER_DEFINED_SCRIPTS_END=41, 
		BRICK_LIST_WS=42, BRICKLIST_NEWLINE=43, BRICK_NAME=44, UDB_START=45, BRICK_LIST_DISABLED_INDICATOR=46, 
		NOTE_BRICK=47, BRICK_LIST_END_ELSE=48, BRICK_LIST_END=49, BRICK_MODE_WS=50, 
		BRICK_MODE_BRACKET_OPEN=51, SEMICOLON=52, BRICK_BODY_OPEN=53, NODE_BRICK_TEXT=54, 
		UDB_MODE_WS=55, UDB_PARAM_START=56, UDB_LABEL=57, UDB_END=58, UDB_PARAM_MODE_WS=59, 
		UDB_PARAM_TEXT=60, UDB_PARAM_END=61, UDB_AFTER_MODE_WS=62, UDB_MODE_BRACKET_OPEN=63, 
		UDB_SEMICOLON=64, UDB_DEFINITION_SCREEN_REFRESH=65, UDB_DEFINITION_NO_SCREEN_REFRESH=66, 
		UDB_BODY_OPEN=67, PARAM_MODE_WS=68, PARAM_MODE_BRACKET_OPEN=69, PARAM_MODE_BRACKET_CLOSE=70, 
		PARAM_MODE_NAME=71, PARAM_MODE_UDB_NAME=72, PARAM_MODE_COLON=73, PARAM_SEPARATOR=74, 
		FORMULA_MODE_WS=75, FORMULA_MODE_BRACKET_CLOSE=76, FORMULA_MODE_BRACKET_OPEN=77, 
		FORMULA_MODE_ANYTHING=78, FORMULA_MODE_STRING_BEGIN=79, FORMULA_MODE_VARIABLE_BEGIN=80, 
		FORMULA_MODE_UDB_PARAM_BEGIN=81, FORMULA_LIST_MODE_BEGIN=82, FORMULA_STRING_MODE_ANYTHING=83, 
		FORMULA_STRING_MODE_END=84, FORMULA_VARIABLE_MODE_ANYTHING=85, FORMULA_VARIABLE_MODE_END=86, 
		FORMULA_UDB_PARAM_MODE_ANYTHING=87, FORMULA_UDB_PARAM_MODE_END=88, FORMULA_LIST_MODE_ANYTHING=89, 
		FORMULA_LIST_MODE_END=90;
	public static final int
		RULE_program = 0, RULE_programHeader = 1, RULE_programBody = 2, RULE_metadata = 3, 
		RULE_metadataContent = 4, RULE_description = 5, RULE_catrobatVersion = 6, 
		RULE_catrobatAppVersion = 7, RULE_stage = 8, RULE_stageContent = 9, RULE_landscapeMode = 10, 
		RULE_height = 11, RULE_width = 12, RULE_displayMode = 13, RULE_globals = 14, 
		RULE_multiplayerVariables = 15, RULE_variableOrListDeclaration = 16, RULE_variableDeclaration = 17, 
		RULE_scene = 18, RULE_background = 19, RULE_actor = 20, RULE_actorContent = 21, 
		RULE_localVariables = 22, RULE_looks = 23, RULE_sounds = 24, RULE_looksAndSoundsContent = 25, 
		RULE_scripts = 26, RULE_script = 27, RULE_brickDefintion = 28, RULE_noteBrick = 29, 
		RULE_brickWithBody = 30, RULE_elseBranch = 31, RULE_elseBranchDisabled = 32, 
		RULE_brickInvocation = 33, RULE_userDefinedScripts = 34, RULE_userDefinedScript = 35, 
		RULE_userDefinedBrick = 36, RULE_userDefinedBrickPart = 37, RULE_brickCondition = 38, 
		RULE_argumentList = 39, RULE_argument = 40, RULE_formula = 41, RULE_formulaElement = 42;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "programHeader", "programBody", "metadata", "metadataContent", 
			"description", "catrobatVersion", "catrobatAppVersion", "stage", "stageContent", 
			"landscapeMode", "height", "width", "displayMode", "globals", "multiplayerVariables", 
			"variableOrListDeclaration", "variableDeclaration", "scene", "background", 
			"actor", "actorContent", "localVariables", "looks", "sounds", "looksAndSoundsContent", 
			"scripts", "script", "brickDefintion", "noteBrick", "brickWithBody", 
			"elseBranch", "elseBranchDisabled", "brickInvocation", "userDefinedScripts", 
			"userDefinedScript", "userDefinedBrick", "userDefinedBrickPart", "brickCondition", 
			"argumentList", "argument", "formula", "formulaElement"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, "'Program'", 
			"'Metadata'", "'Description'", "'Catrobat version'", "'Catrobat app version'", 
			"'Stage'", "'Landscape mode'", "'Height'", "'Width'", "'Display mode'", 
			"'Globals'", "'Multiplayer variables'", "'Locals'", "'Looks'", "'Sounds'", 
			"'Actor or object'", "'of type'", "'Scene'", "'Background'", "'Scripts'", 
			"'User Defined Bricks'", null, null, null, null, null, null, null, "'Define'", 
			null, null, null, null, null, null, null, null, null, null, null, null, 
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
			"SCRIPTS_WS", "SCRIPT_DISABLED_INDICATOR", "SCRIPTS_START", "SCRIPT_NAME", 
			"SCRIPTS_END", "USER_DEFINED_SCRIPTS_WS", "USER_DEFINED_SCRIPTS_START", 
			"UDB_DEFINE", "USER_DEFINED_SCRIPT_UDB_START", "USER_DEFINED_SCRIPTS_END", 
			"BRICK_LIST_WS", "BRICKLIST_NEWLINE", "BRICK_NAME", "UDB_START", "BRICK_LIST_DISABLED_INDICATOR", 
			"NOTE_BRICK", "BRICK_LIST_END_ELSE", "BRICK_LIST_END", "BRICK_MODE_WS", 
			"BRICK_MODE_BRACKET_OPEN", "SEMICOLON", "BRICK_BODY_OPEN", "NODE_BRICK_TEXT", 
			"UDB_MODE_WS", "UDB_PARAM_START", "UDB_LABEL", "UDB_END", "UDB_PARAM_MODE_WS", 
			"UDB_PARAM_TEXT", "UDB_PARAM_END", "UDB_AFTER_MODE_WS", "UDB_MODE_BRACKET_OPEN", 
			"UDB_SEMICOLON", "UDB_DEFINITION_SCREEN_REFRESH", "UDB_DEFINITION_NO_SCREEN_REFRESH", 
			"UDB_BODY_OPEN", "PARAM_MODE_WS", "PARAM_MODE_BRACKET_OPEN", "PARAM_MODE_BRACKET_CLOSE", 
			"PARAM_MODE_NAME", "PARAM_MODE_UDB_NAME", "PARAM_MODE_COLON", "PARAM_SEPARATOR", 
			"FORMULA_MODE_WS", "FORMULA_MODE_BRACKET_CLOSE", "FORMULA_MODE_BRACKET_OPEN", 
			"FORMULA_MODE_ANYTHING", "FORMULA_MODE_STRING_BEGIN", "FORMULA_MODE_VARIABLE_BEGIN", 
			"FORMULA_MODE_UDB_PARAM_BEGIN", "FORMULA_LIST_MODE_BEGIN", "FORMULA_STRING_MODE_ANYTHING", 
			"FORMULA_STRING_MODE_END", "FORMULA_VARIABLE_MODE_ANYTHING", "FORMULA_VARIABLE_MODE_END", 
			"FORMULA_UDB_PARAM_MODE_ANYTHING", "FORMULA_UDB_PARAM_MODE_END", "FORMULA_LIST_MODE_ANYTHING", 
			"FORMULA_LIST_MODE_END"
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
			setState(86);
			programHeader();
			setState(87);
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
			setState(89);
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
			setState(91);
			match(PROGRAM);
			setState(92);
			match(STRING);
			setState(93);
			match(CURLY_BRACKET_OPEN);
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6361088L) != 0)) {
				{
				setState(98);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case METADATA:
					{
					setState(94);
					metadata();
					}
					break;
				case STAGE:
					{
					setState(95);
					stage();
					}
					break;
				case GLOBALS:
					{
					setState(96);
					globals();
					}
					break;
				case MULTIPLAYER_VARIABLES:
					{
					setState(97);
					multiplayerVariables();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(104); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(103);
				scene();
				}
				}
				setState(106); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCENE );
			setState(108);
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
			setState(110);
			match(METADATA);
			setState(111);
			match(CURLY_BRACKET_OPEN);
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 57344L) != 0)) {
				{
				setState(112);
				metadataContent();
				}
			}

			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(115);
				match(SEPARATOR);
				setState(116);
				metadataContent();
				}
				}
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(122);
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
			setState(127);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DESCRIPTION:
				{
				setState(124);
				description();
				}
				break;
			case CATROBAT_VERSION:
				{
				setState(125);
				catrobatVersion();
				}
				break;
			case CATRPBAT_APP_VERSION:
				{
				setState(126);
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
			setState(129);
			match(DESCRIPTION);
			setState(130);
			match(COLON);
			setState(131);
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
			setState(133);
			match(CATROBAT_VERSION);
			setState(134);
			match(COLON);
			setState(135);
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
			setState(137);
			match(CATRPBAT_APP_VERSION);
			setState(138);
			match(COLON);
			setState(139);
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
			setState(141);
			match(STAGE);
			setState(142);
			match(CURLY_BRACKET_OPEN);
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0)) {
				{
				setState(143);
				stageContent();
				}
			}

			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(146);
				match(SEPARATOR);
				setState(147);
				stageContent();
				}
				}
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(153);
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
			setState(159);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANDSCAPE_MODE:
				{
				setState(155);
				landscapeMode();
				}
				break;
			case HEIGHT:
				{
				setState(156);
				height();
				}
				break;
			case WIDTH:
				{
				setState(157);
				width();
				}
				break;
			case DISPLAY_MODE:
				{
				setState(158);
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
			setState(161);
			match(LANDSCAPE_MODE);
			setState(162);
			match(COLON);
			setState(163);
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
			setState(165);
			match(HEIGHT);
			setState(166);
			match(COLON);
			setState(167);
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
			setState(169);
			match(WIDTH);
			setState(170);
			match(COLON);
			setState(171);
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
			setState(173);
			match(DISPLAY_MODE);
			setState(174);
			match(COLON);
			setState(175);
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
			setState(177);
			match(GLOBALS);
			setState(178);
			match(CURLY_BRACKET_OPEN);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(179);
				variableOrListDeclaration();
				}
			}

			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(182);
				match(SEPARATOR);
				setState(183);
				variableOrListDeclaration();
				}
				}
				setState(188);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(189);
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
			setState(191);
			match(MULTIPLAYER_VARIABLES);
			setState(192);
			match(CURLY_BRACKET_OPEN);
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF) {
				{
				setState(193);
				variableDeclaration();
				}
			}

			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(196);
				match(SEPARATOR);
				setState(197);
				variableDeclaration();
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
			setState(207);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VARIABLE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(205);
				variableDeclaration();
				}
				break;
			case LIST_REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(206);
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
			setState(209);
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
			setState(211);
			match(SCENE);
			setState(212);
			match(STRING);
			setState(213);
			match(CURLY_BRACKET_OPEN);
			setState(214);
			background();
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ACTOR_OR_OBJECT) {
				{
				{
				setState(215);
				actor();
				}
				}
				setState(220);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(221);
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
			setState(223);
			match(BACKGROUND);
			setState(224);
			match(CURLY_BRACKET_OPEN);
			setState(225);
			actorContent();
			setState(226);
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
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public ActorContentContext actorContent() {
			return getRuleContext(ActorContentContext.class,0);
		}
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public TerminalNode OF_TYPE() { return getToken(CatrobatLanguageParser.OF_TYPE, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(ACTOR_OR_OBJECT);
			setState(229);
			match(STRING);
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OF_TYPE) {
				{
				setState(230);
				match(OF_TYPE);
				setState(231);
				match(STRING);
				}
			}

			setState(234);
			match(CURLY_BRACKET_OPEN);
			setState(235);
			actorContent();
			setState(236);
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
			setState(245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3279945728L) != 0)) {
				{
				setState(243);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LOCAL_VARIABLES:
					{
					setState(238);
					localVariables();
					}
					break;
				case LOOKS:
					{
					setState(239);
					looks();
					}
					break;
				case SOUNDS:
					{
					setState(240);
					sounds();
					}
					break;
				case SCRIPTS:
					{
					setState(241);
					scripts();
					}
					break;
				case USER_DEFINED_SCRIPTS:
					{
					setState(242);
					userDefinedScripts();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(247);
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
			setState(248);
			match(LOCAL_VARIABLES);
			setState(249);
			match(CURLY_BRACKET_OPEN);
			setState(251);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(250);
				variableOrListDeclaration();
				}
			}

			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(253);
				match(SEPARATOR);
				setState(254);
				variableOrListDeclaration();
				}
				}
				setState(259);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(260);
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
			setState(262);
			match(LOOKS);
			setState(263);
			match(CURLY_BRACKET_OPEN);
			setState(265);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(264);
				looksAndSoundsContent();
				}
			}

			setState(271);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(267);
				match(SEPARATOR);
				setState(268);
				looksAndSoundsContent();
				}
				}
				setState(273);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(274);
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
			setState(276);
			match(SOUNDS);
			setState(277);
			match(CURLY_BRACKET_OPEN);
			setState(279);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(278);
				looksAndSoundsContent();
				}
			}

			setState(285);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(281);
				match(SEPARATOR);
				setState(282);
				looksAndSoundsContent();
				}
				}
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(288);
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
			setState(290);
			match(STRING);
			setState(291);
			match(COLON);
			setState(292);
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
		public TerminalNode SCRIPTS_START() { return getToken(CatrobatLanguageParser.SCRIPTS_START, 0); }
		public TerminalNode SCRIPTS_END() { return getToken(CatrobatLanguageParser.SCRIPTS_END, 0); }
		public List<ScriptContext> script() {
			return getRuleContexts(ScriptContext.class);
		}
		public ScriptContext script(int i) {
			return getRuleContext(ScriptContext.class,i);
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
			setState(294);
			match(SCRIPTS);
			setState(295);
			match(SCRIPTS_START);
			setState(299);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SCRIPT_DISABLED_INDICATOR || _la==SCRIPT_NAME) {
				{
				{
				setState(296);
				script();
				}
				}
				setState(301);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(302);
			match(SCRIPTS_END);
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
	public static class ScriptContext extends ParserRuleContext {
		public TerminalNode SCRIPT_NAME() { return getToken(CatrobatLanguageParser.SCRIPT_NAME, 0); }
		public TerminalNode BRICK_BODY_OPEN() { return getToken(CatrobatLanguageParser.BRICK_BODY_OPEN, 0); }
		public TerminalNode BRICK_LIST_END() { return getToken(CatrobatLanguageParser.BRICK_LIST_END, 0); }
		public BrickConditionContext brickCondition() {
			return getRuleContext(BrickConditionContext.class,0);
		}
		public List<TerminalNode> BRICKLIST_NEWLINE() { return getTokens(CatrobatLanguageParser.BRICKLIST_NEWLINE); }
		public TerminalNode BRICKLIST_NEWLINE(int i) {
			return getToken(CatrobatLanguageParser.BRICKLIST_NEWLINE, i);
		}
		public List<BrickDefintionContext> brickDefintion() {
			return getRuleContexts(BrickDefintionContext.class);
		}
		public BrickDefintionContext brickDefintion(int i) {
			return getRuleContext(BrickDefintionContext.class,i);
		}
		public TerminalNode SCRIPT_DISABLED_INDICATOR() { return getToken(CatrobatLanguageParser.SCRIPT_DISABLED_INDICATOR, 0); }
		public TerminalNode BRICK_LIST_DISABLED_INDICATOR() { return getToken(CatrobatLanguageParser.BRICK_LIST_DISABLED_INDICATOR, 0); }
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_script);
		int _la;
		try {
			setState(334);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SCRIPT_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(304);
				match(SCRIPT_NAME);
				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(305);
					brickCondition();
					}
				}

				setState(308);
				match(BRICK_BODY_OPEN);
				setState(313); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(309);
					match(BRICKLIST_NEWLINE);
					setState(311);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 264982302294016L) != 0)) {
						{
						setState(310);
						brickDefintion();
						}
					}

					}
					}
					setState(315); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==BRICKLIST_NEWLINE );
				setState(317);
				match(BRICK_LIST_END);
				}
				break;
			case SCRIPT_DISABLED_INDICATOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(318);
				match(SCRIPT_DISABLED_INDICATOR);
				setState(319);
				match(SCRIPT_NAME);
				setState(321);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(320);
					brickCondition();
					}
				}

				setState(323);
				match(BRICK_BODY_OPEN);
				setState(328); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(324);
					match(BRICKLIST_NEWLINE);
					setState(326);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
					case 1:
						{
						setState(325);
						brickDefintion();
						}
						break;
					}
					}
					}
					setState(330); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==BRICKLIST_NEWLINE );
				setState(332);
				match(BRICK_LIST_DISABLED_INDICATOR);
				setState(333);
				match(BRICK_LIST_END);
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
	public static class BrickDefintionContext extends ParserRuleContext {
		public NoteBrickContext noteBrick() {
			return getRuleContext(NoteBrickContext.class,0);
		}
		public BrickInvocationContext brickInvocation() {
			return getRuleContext(BrickInvocationContext.class,0);
		}
		public BrickWithBodyContext brickWithBody() {
			return getRuleContext(BrickWithBodyContext.class,0);
		}
		public BrickDefintionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brickDefintion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrickDefintion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrickDefintion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrickDefintion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrickDefintionContext brickDefintion() throws RecognitionException {
		BrickDefintionContext _localctx = new BrickDefintionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_brickDefintion);
		try {
			setState(339);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(336);
				noteBrick();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(337);
				brickInvocation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(338);
				brickWithBody();
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
	public static class NoteBrickContext extends ParserRuleContext {
		public TerminalNode NOTE_BRICK() { return getToken(CatrobatLanguageParser.NOTE_BRICK, 0); }
		public TerminalNode NODE_BRICK_TEXT() { return getToken(CatrobatLanguageParser.NODE_BRICK_TEXT, 0); }
		public TerminalNode BRICK_LIST_DISABLED_INDICATOR() { return getToken(CatrobatLanguageParser.BRICK_LIST_DISABLED_INDICATOR, 0); }
		public NoteBrickContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_noteBrick; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterNoteBrick(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitNoteBrick(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitNoteBrick(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NoteBrickContext noteBrick() throws RecognitionException {
		NoteBrickContext _localctx = new NoteBrickContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_noteBrick);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_LIST_DISABLED_INDICATOR) {
				{
				setState(341);
				match(BRICK_LIST_DISABLED_INDICATOR);
				}
			}

			setState(344);
			match(NOTE_BRICK);
			setState(345);
			match(NODE_BRICK_TEXT);
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
	public static class BrickWithBodyContext extends ParserRuleContext {
		public TerminalNode BRICK_NAME() { return getToken(CatrobatLanguageParser.BRICK_NAME, 0); }
		public TerminalNode BRICK_BODY_OPEN() { return getToken(CatrobatLanguageParser.BRICK_BODY_OPEN, 0); }
		public TerminalNode BRICK_LIST_END() { return getToken(CatrobatLanguageParser.BRICK_LIST_END, 0); }
		public ElseBranchContext elseBranch() {
			return getRuleContext(ElseBranchContext.class,0);
		}
		public BrickConditionContext brickCondition() {
			return getRuleContext(BrickConditionContext.class,0);
		}
		public List<TerminalNode> BRICKLIST_NEWLINE() { return getTokens(CatrobatLanguageParser.BRICKLIST_NEWLINE); }
		public TerminalNode BRICKLIST_NEWLINE(int i) {
			return getToken(CatrobatLanguageParser.BRICKLIST_NEWLINE, i);
		}
		public List<BrickDefintionContext> brickDefintion() {
			return getRuleContexts(BrickDefintionContext.class);
		}
		public BrickDefintionContext brickDefintion(int i) {
			return getRuleContext(BrickDefintionContext.class,i);
		}
		public List<TerminalNode> BRICK_LIST_DISABLED_INDICATOR() { return getTokens(CatrobatLanguageParser.BRICK_LIST_DISABLED_INDICATOR); }
		public TerminalNode BRICK_LIST_DISABLED_INDICATOR(int i) {
			return getToken(CatrobatLanguageParser.BRICK_LIST_DISABLED_INDICATOR, i);
		}
		public ElseBranchDisabledContext elseBranchDisabled() {
			return getRuleContext(ElseBranchDisabledContext.class,0);
		}
		public BrickWithBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brickWithBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrickWithBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrickWithBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrickWithBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrickWithBodyContext brickWithBody() throws RecognitionException {
		BrickWithBodyContext _localctx = new BrickWithBodyContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_brickWithBody);
		int _la;
		try {
			setState(383);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRICK_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(347);
				match(BRICK_NAME);
				setState(349);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(348);
					brickCondition();
					}
				}

				setState(351);
				match(BRICK_BODY_OPEN);
				setState(356); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(352);
					match(BRICKLIST_NEWLINE);
					setState(354);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 264982302294016L) != 0)) {
						{
						setState(353);
						brickDefintion();
						}
					}

					}
					}
					setState(358); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==BRICKLIST_NEWLINE );
				setState(362);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case BRICK_LIST_END:
					{
					setState(360);
					match(BRICK_LIST_END);
					}
					break;
				case BRICK_LIST_END_ELSE:
					{
					setState(361);
					elseBranch();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case BRICK_LIST_DISABLED_INDICATOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(364);
				match(BRICK_LIST_DISABLED_INDICATOR);
				setState(365);
				match(BRICK_NAME);
				setState(367);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(366);
					brickCondition();
					}
				}

				setState(369);
				match(BRICK_BODY_OPEN);
				setState(374); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(370);
					match(BRICKLIST_NEWLINE);
					setState(372);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
					case 1:
						{
						setState(371);
						brickDefintion();
						}
						break;
					}
					}
					}
					setState(376); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==BRICKLIST_NEWLINE );
				setState(381);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
				case 1:
					{
					{
					setState(378);
					match(BRICK_LIST_DISABLED_INDICATOR);
					setState(379);
					match(BRICK_LIST_END);
					}
					}
					break;
				case 2:
					{
					setState(380);
					elseBranchDisabled();
					}
					break;
				}
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
	public static class ElseBranchContext extends ParserRuleContext {
		public TerminalNode BRICK_LIST_END_ELSE() { return getToken(CatrobatLanguageParser.BRICK_LIST_END_ELSE, 0); }
		public TerminalNode BRICK_BODY_OPEN() { return getToken(CatrobatLanguageParser.BRICK_BODY_OPEN, 0); }
		public TerminalNode BRICK_LIST_END() { return getToken(CatrobatLanguageParser.BRICK_LIST_END, 0); }
		public List<TerminalNode> BRICKLIST_NEWLINE() { return getTokens(CatrobatLanguageParser.BRICKLIST_NEWLINE); }
		public TerminalNode BRICKLIST_NEWLINE(int i) {
			return getToken(CatrobatLanguageParser.BRICKLIST_NEWLINE, i);
		}
		public List<BrickDefintionContext> brickDefintion() {
			return getRuleContexts(BrickDefintionContext.class);
		}
		public BrickDefintionContext brickDefintion(int i) {
			return getRuleContext(BrickDefintionContext.class,i);
		}
		public ElseBranchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseBranch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterElseBranch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitElseBranch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitElseBranch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseBranchContext elseBranch() throws RecognitionException {
		ElseBranchContext _localctx = new ElseBranchContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_elseBranch);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			match(BRICK_LIST_END_ELSE);
			setState(386);
			match(BRICK_BODY_OPEN);
			setState(391); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(387);
				match(BRICKLIST_NEWLINE);
				setState(389);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 264982302294016L) != 0)) {
					{
					setState(388);
					brickDefintion();
					}
				}

				}
				}
				setState(393); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==BRICKLIST_NEWLINE );
			setState(395);
			match(BRICK_LIST_END);
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
	public static class ElseBranchDisabledContext extends ParserRuleContext {
		public List<TerminalNode> BRICK_LIST_DISABLED_INDICATOR() { return getTokens(CatrobatLanguageParser.BRICK_LIST_DISABLED_INDICATOR); }
		public TerminalNode BRICK_LIST_DISABLED_INDICATOR(int i) {
			return getToken(CatrobatLanguageParser.BRICK_LIST_DISABLED_INDICATOR, i);
		}
		public TerminalNode BRICK_LIST_END_ELSE() { return getToken(CatrobatLanguageParser.BRICK_LIST_END_ELSE, 0); }
		public TerminalNode BRICK_BODY_OPEN() { return getToken(CatrobatLanguageParser.BRICK_BODY_OPEN, 0); }
		public TerminalNode BRICK_LIST_END() { return getToken(CatrobatLanguageParser.BRICK_LIST_END, 0); }
		public List<TerminalNode> BRICKLIST_NEWLINE() { return getTokens(CatrobatLanguageParser.BRICKLIST_NEWLINE); }
		public TerminalNode BRICKLIST_NEWLINE(int i) {
			return getToken(CatrobatLanguageParser.BRICKLIST_NEWLINE, i);
		}
		public List<BrickDefintionContext> brickDefintion() {
			return getRuleContexts(BrickDefintionContext.class);
		}
		public BrickDefintionContext brickDefintion(int i) {
			return getRuleContext(BrickDefintionContext.class,i);
		}
		public ElseBranchDisabledContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseBranchDisabled; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterElseBranchDisabled(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitElseBranchDisabled(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitElseBranchDisabled(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseBranchDisabledContext elseBranchDisabled() throws RecognitionException {
		ElseBranchDisabledContext _localctx = new ElseBranchDisabledContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_elseBranchDisabled);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			match(BRICK_LIST_DISABLED_INDICATOR);
			setState(398);
			match(BRICK_LIST_END_ELSE);
			setState(399);
			match(BRICK_BODY_OPEN);
			setState(404); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(400);
				match(BRICKLIST_NEWLINE);
				setState(402);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(401);
					brickDefintion();
					}
					break;
				}
				}
				}
				setState(406); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==BRICKLIST_NEWLINE );
			setState(408);
			match(BRICK_LIST_DISABLED_INDICATOR);
			setState(409);
			match(BRICK_LIST_END);
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
	public static class BrickInvocationContext extends ParserRuleContext {
		public TerminalNode SEMICOLON() { return getToken(CatrobatLanguageParser.SEMICOLON, 0); }
		public TerminalNode UDB_SEMICOLON() { return getToken(CatrobatLanguageParser.UDB_SEMICOLON, 0); }
		public TerminalNode BRICK_NAME() { return getToken(CatrobatLanguageParser.BRICK_NAME, 0); }
		public UserDefinedBrickContext userDefinedBrick() {
			return getRuleContext(UserDefinedBrickContext.class,0);
		}
		public TerminalNode BRICK_LIST_DISABLED_INDICATOR() { return getToken(CatrobatLanguageParser.BRICK_LIST_DISABLED_INDICATOR, 0); }
		public BrickConditionContext brickCondition() {
			return getRuleContext(BrickConditionContext.class,0);
		}
		public BrickInvocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brickInvocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrickInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrickInvocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrickInvocation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrickInvocationContext brickInvocation() throws RecognitionException {
		BrickInvocationContext _localctx = new BrickInvocationContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_brickInvocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_LIST_DISABLED_INDICATOR) {
				{
				setState(411);
				match(BRICK_LIST_DISABLED_INDICATOR);
				}
			}

			setState(416);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRICK_NAME:
				{
				setState(414);
				match(BRICK_NAME);
				}
				break;
			case USER_DEFINED_SCRIPT_UDB_START:
			case UDB_START:
				{
				setState(415);
				userDefinedBrick();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(419);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
				{
				setState(418);
				brickCondition();
				}
			}

			setState(421);
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
	public static class UserDefinedScriptsContext extends ParserRuleContext {
		public TerminalNode USER_DEFINED_SCRIPTS() { return getToken(CatrobatLanguageParser.USER_DEFINED_SCRIPTS, 0); }
		public TerminalNode USER_DEFINED_SCRIPTS_START() { return getToken(CatrobatLanguageParser.USER_DEFINED_SCRIPTS_START, 0); }
		public TerminalNode USER_DEFINED_SCRIPTS_END() { return getToken(CatrobatLanguageParser.USER_DEFINED_SCRIPTS_END, 0); }
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
		enterRule(_localctx, 68, RULE_userDefinedScripts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			match(USER_DEFINED_SCRIPTS);
			setState(424);
			match(USER_DEFINED_SCRIPTS_START);
			setState(428);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==UDB_DEFINE) {
				{
				{
				setState(425);
				userDefinedScript();
				}
				}
				setState(430);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(431);
			match(USER_DEFINED_SCRIPTS_END);
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
		public TerminalNode BRICK_LIST_END() { return getToken(CatrobatLanguageParser.BRICK_LIST_END, 0); }
		public TerminalNode UDB_DEFINITION_SCREEN_REFRESH() { return getToken(CatrobatLanguageParser.UDB_DEFINITION_SCREEN_REFRESH, 0); }
		public TerminalNode UDB_DEFINITION_NO_SCREEN_REFRESH() { return getToken(CatrobatLanguageParser.UDB_DEFINITION_NO_SCREEN_REFRESH, 0); }
		public List<TerminalNode> BRICKLIST_NEWLINE() { return getTokens(CatrobatLanguageParser.BRICKLIST_NEWLINE); }
		public TerminalNode BRICKLIST_NEWLINE(int i) {
			return getToken(CatrobatLanguageParser.BRICKLIST_NEWLINE, i);
		}
		public List<BrickDefintionContext> brickDefintion() {
			return getRuleContexts(BrickDefintionContext.class);
		}
		public BrickDefintionContext brickDefintion(int i) {
			return getRuleContext(BrickDefintionContext.class,i);
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
		enterRule(_localctx, 70, RULE_userDefinedScript);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
			match(UDB_DEFINE);
			setState(434);
			userDefinedBrick();
			setState(435);
			_la = _input.LA(1);
			if ( !(_la==UDB_DEFINITION_SCREEN_REFRESH || _la==UDB_DEFINITION_NO_SCREEN_REFRESH) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(436);
			match(UDB_BODY_OPEN);
			setState(441); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(437);
				match(BRICKLIST_NEWLINE);
				setState(439);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 264982302294016L) != 0)) {
					{
					setState(438);
					brickDefintion();
					}
				}

				}
				}
				setState(443); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==BRICKLIST_NEWLINE );
			setState(445);
			match(BRICK_LIST_END);
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
		public TerminalNode UDB_END() { return getToken(CatrobatLanguageParser.UDB_END, 0); }
		public TerminalNode USER_DEFINED_SCRIPT_UDB_START() { return getToken(CatrobatLanguageParser.USER_DEFINED_SCRIPT_UDB_START, 0); }
		public TerminalNode UDB_START() { return getToken(CatrobatLanguageParser.UDB_START, 0); }
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
		enterRule(_localctx, 72, RULE_userDefinedBrick);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(447);
			_la = _input.LA(1);
			if ( !(_la==USER_DEFINED_SCRIPT_UDB_START || _la==UDB_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(449); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(448);
				userDefinedBrickPart();
				}
				}
				setState(451); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==UDB_PARAM_START || _la==UDB_LABEL );
			setState(453);
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
		enterRule(_localctx, 74, RULE_userDefinedBrickPart);
		try {
			setState(459);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case UDB_PARAM_START:
				enterOuterAlt(_localctx, 1);
				{
				setState(455);
				match(UDB_PARAM_START);
				setState(456);
				match(UDB_PARAM_TEXT);
				setState(457);
				match(UDB_PARAM_END);
				}
				break;
			case UDB_LABEL:
				enterOuterAlt(_localctx, 2);
				{
				setState(458);
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
	public static class BrickConditionContext extends ParserRuleContext {
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TerminalNode PARAM_MODE_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.PARAM_MODE_BRACKET_CLOSE, 0); }
		public TerminalNode BRICK_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.BRICK_MODE_BRACKET_OPEN, 0); }
		public TerminalNode UDB_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.UDB_MODE_BRACKET_OPEN, 0); }
		public BrickConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brickCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterBrickCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitBrickCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitBrickCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrickConditionContext brickCondition() throws RecognitionException {
		BrickConditionContext _localctx = new BrickConditionContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_brickCondition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(461);
			_la = _input.LA(1);
			if ( !(_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(462);
			argumentList();
			setState(463);
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
	public static class ArgumentListContext extends ParserRuleContext {
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
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitArgumentList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitArgumentList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(465);
			argument();
			setState(470);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAM_SEPARATOR) {
				{
				{
				setState(466);
				match(PARAM_SEPARATOR);
				setState(467);
				argument();
				}
				}
				setState(472);
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
		public TerminalNode PARAM_MODE_COLON() { return getToken(CatrobatLanguageParser.PARAM_MODE_COLON, 0); }
		public TerminalNode PARAM_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.PARAM_MODE_BRACKET_OPEN, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode FORMULA_MODE_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.FORMULA_MODE_BRACKET_CLOSE, 0); }
		public TerminalNode PARAM_MODE_NAME() { return getToken(CatrobatLanguageParser.PARAM_MODE_NAME, 0); }
		public TerminalNode PARAM_MODE_UDB_NAME() { return getToken(CatrobatLanguageParser.PARAM_MODE_UDB_NAME, 0); }
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
		enterRule(_localctx, 80, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(473);
			_la = _input.LA(1);
			if ( !(_la==PARAM_MODE_NAME || _la==PARAM_MODE_UDB_NAME) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(474);
			match(PARAM_MODE_COLON);
			setState(475);
			match(PARAM_MODE_BRACKET_OPEN);
			setState(476);
			formula();
			setState(477);
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
		public List<FormulaElementContext> formulaElement() {
			return getRuleContexts(FormulaElementContext.class);
		}
		public FormulaElementContext formulaElement(int i) {
			return getRuleContext(FormulaElementContext.class,i);
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
		enterRule(_localctx, 82, RULE_formula);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(482);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(479);
					formulaElement();
					}
					} 
				}
				setState(484);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
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
	public static class FormulaElementContext extends ParserRuleContext {
		public TerminalNode FORMULA_MODE_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.FORMULA_MODE_BRACKET_OPEN, 0); }
		public TerminalNode FORMULA_MODE_ANYTHING() { return getToken(CatrobatLanguageParser.FORMULA_MODE_ANYTHING, 0); }
		public TerminalNode FORMULA_MODE_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.FORMULA_MODE_BRACKET_CLOSE, 0); }
		public TerminalNode FORMULA_MODE_STRING_BEGIN() { return getToken(CatrobatLanguageParser.FORMULA_MODE_STRING_BEGIN, 0); }
		public TerminalNode FORMULA_STRING_MODE_ANYTHING() { return getToken(CatrobatLanguageParser.FORMULA_STRING_MODE_ANYTHING, 0); }
		public TerminalNode FORMULA_STRING_MODE_END() { return getToken(CatrobatLanguageParser.FORMULA_STRING_MODE_END, 0); }
		public TerminalNode FORMULA_MODE_VARIABLE_BEGIN() { return getToken(CatrobatLanguageParser.FORMULA_MODE_VARIABLE_BEGIN, 0); }
		public TerminalNode FORMULA_VARIABLE_MODE_ANYTHING() { return getToken(CatrobatLanguageParser.FORMULA_VARIABLE_MODE_ANYTHING, 0); }
		public TerminalNode FORMULA_VARIABLE_MODE_END() { return getToken(CatrobatLanguageParser.FORMULA_VARIABLE_MODE_END, 0); }
		public TerminalNode FORMULA_MODE_UDB_PARAM_BEGIN() { return getToken(CatrobatLanguageParser.FORMULA_MODE_UDB_PARAM_BEGIN, 0); }
		public TerminalNode FORMULA_UDB_PARAM_MODE_ANYTHING() { return getToken(CatrobatLanguageParser.FORMULA_UDB_PARAM_MODE_ANYTHING, 0); }
		public TerminalNode FORMULA_UDB_PARAM_MODE_END() { return getToken(CatrobatLanguageParser.FORMULA_UDB_PARAM_MODE_END, 0); }
		public TerminalNode FORMULA_LIST_MODE_BEGIN() { return getToken(CatrobatLanguageParser.FORMULA_LIST_MODE_BEGIN, 0); }
		public TerminalNode FORMULA_LIST_MODE_ANYTHING() { return getToken(CatrobatLanguageParser.FORMULA_LIST_MODE_ANYTHING, 0); }
		public TerminalNode FORMULA_LIST_MODE_END() { return getToken(CatrobatLanguageParser.FORMULA_LIST_MODE_END, 0); }
		public FormulaElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formulaElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).enterFormulaElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatLanguageParserListener ) ((CatrobatLanguageParserListener)listener).exitFormulaElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatLanguageParserVisitor ) return ((CatrobatLanguageParserVisitor<? extends T>)visitor).visitFormulaElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaElementContext formulaElement() throws RecognitionException {
		FormulaElementContext _localctx = new FormulaElementContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_formulaElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(485);
			_la = _input.LA(1);
			if ( !(((((_la - 76)) & ~0x3f) == 0 && ((1L << (_la - 76)) & 32767L) != 0)) ) {
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

	public static final String _serializedATN =
		"\u0004\u0001Z\u01e8\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002c\b\u0002\n\u0002\f\u0002"+
		"f\t\u0002\u0001\u0002\u0004\u0002i\b\u0002\u000b\u0002\f\u0002j\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003r\b"+
		"\u0003\u0001\u0003\u0001\u0003\u0005\u0003v\b\u0003\n\u0003\f\u0003y\t"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003"+
		"\u0004\u0080\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0003\b\u0091\b\b\u0001\b\u0001"+
		"\b\u0005\b\u0095\b\b\n\b\f\b\u0098\t\b\u0001\b\u0001\b\u0001\t\u0001\t"+
		"\u0001\t\u0001\t\u0003\t\u00a0\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0003\u000e\u00b5\b\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u00b9\b"+
		"\u000e\n\u000e\f\u000e\u00bc\t\u000e\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0003\u000f\u00c3\b\u000f\u0001\u000f\u0001\u000f"+
		"\u0005\u000f\u00c7\b\u000f\n\u000f\f\u000f\u00ca\t\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u00d0\b\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005"+
		"\u0012\u00d9\b\u0012\n\u0012\f\u0012\u00dc\t\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u00e9\b\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0005\u0015\u00f4\b\u0015\n\u0015\f\u0015\u00f7"+
		"\t\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00fc\b\u0016"+
		"\u0001\u0016\u0001\u0016\u0005\u0016\u0100\b\u0016\n\u0016\f\u0016\u0103"+
		"\t\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0003"+
		"\u0017\u010a\b\u0017\u0001\u0017\u0001\u0017\u0005\u0017\u010e\b\u0017"+
		"\n\u0017\f\u0017\u0111\t\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0003\u0018\u0118\b\u0018\u0001\u0018\u0001\u0018\u0005"+
		"\u0018\u011c\b\u0018\n\u0018\f\u0018\u011f\t\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0005\u001a\u012a\b\u001a\n\u001a\f\u001a\u012d\t\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0003\u001b\u0133\b\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u0138\b\u001b\u0004\u001b\u013a"+
		"\b\u001b\u000b\u001b\f\u001b\u013b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0003\u001b\u0142\b\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0003\u001b\u0147\b\u001b\u0004\u001b\u0149\b\u001b\u000b\u001b\f\u001b"+
		"\u014a\u0001\u001b\u0001\u001b\u0003\u001b\u014f\b\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0003\u001c\u0154\b\u001c\u0001\u001d\u0003\u001d\u0157"+
		"\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0003"+
		"\u001e\u015e\b\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u0163"+
		"\b\u001e\u0004\u001e\u0165\b\u001e\u000b\u001e\f\u001e\u0166\u0001\u001e"+
		"\u0001\u001e\u0003\u001e\u016b\b\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0003\u001e\u0170\b\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e"+
		"\u0175\b\u001e\u0004\u001e\u0177\b\u001e\u000b\u001e\f\u001e\u0178\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u017e\b\u001e\u0003\u001e\u0180"+
		"\b\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0186"+
		"\b\u001f\u0004\u001f\u0188\b\u001f\u000b\u001f\f\u001f\u0189\u0001\u001f"+
		"\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0003 \u0193\b \u0004"+
		" \u0195\b \u000b \f \u0196\u0001 \u0001 \u0001 \u0001!\u0003!\u019d\b"+
		"!\u0001!\u0001!\u0003!\u01a1\b!\u0001!\u0003!\u01a4\b!\u0001!\u0001!\u0001"+
		"\"\u0001\"\u0001\"\u0005\"\u01ab\b\"\n\"\f\"\u01ae\t\"\u0001\"\u0001\""+
		"\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0003#\u01b8\b#\u0004#\u01ba"+
		"\b#\u000b#\f#\u01bb\u0001#\u0001#\u0001$\u0001$\u0004$\u01c2\b$\u000b"+
		"$\f$\u01c3\u0001$\u0001$\u0001%\u0001%\u0001%\u0001%\u0003%\u01cc\b%\u0001"+
		"&\u0001&\u0001&\u0001&\u0001\'\u0001\'\u0001\'\u0005\'\u01d5\b\'\n\'\f"+
		"\'\u01d8\t\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001)\u0005)\u01e1"+
		"\b)\n)\f)\u01e4\t)\u0001*\u0001*\u0001*\u0000\u0000+\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.02468:<>@BDFHJLNPRT\u0000\u0006\u0002\u000044@@\u0001\u0000AB\u0002"+
		"\u0000((--\u0002\u000033??\u0001\u0000GH\u0001\u0000LZ\u01fe\u0000V\u0001"+
		"\u0000\u0000\u0000\u0002Y\u0001\u0000\u0000\u0000\u0004[\u0001\u0000\u0000"+
		"\u0000\u0006n\u0001\u0000\u0000\u0000\b\u007f\u0001\u0000\u0000\u0000"+
		"\n\u0081\u0001\u0000\u0000\u0000\f\u0085\u0001\u0000\u0000\u0000\u000e"+
		"\u0089\u0001\u0000\u0000\u0000\u0010\u008d\u0001\u0000\u0000\u0000\u0012"+
		"\u009f\u0001\u0000\u0000\u0000\u0014\u00a1\u0001\u0000\u0000\u0000\u0016"+
		"\u00a5\u0001\u0000\u0000\u0000\u0018\u00a9\u0001\u0000\u0000\u0000\u001a"+
		"\u00ad\u0001\u0000\u0000\u0000\u001c\u00b1\u0001\u0000\u0000\u0000\u001e"+
		"\u00bf\u0001\u0000\u0000\u0000 \u00cf\u0001\u0000\u0000\u0000\"\u00d1"+
		"\u0001\u0000\u0000\u0000$\u00d3\u0001\u0000\u0000\u0000&\u00df\u0001\u0000"+
		"\u0000\u0000(\u00e4\u0001\u0000\u0000\u0000*\u00f5\u0001\u0000\u0000\u0000"+
		",\u00f8\u0001\u0000\u0000\u0000.\u0106\u0001\u0000\u0000\u00000\u0114"+
		"\u0001\u0000\u0000\u00002\u0122\u0001\u0000\u0000\u00004\u0126\u0001\u0000"+
		"\u0000\u00006\u014e\u0001\u0000\u0000\u00008\u0153\u0001\u0000\u0000\u0000"+
		":\u0156\u0001\u0000\u0000\u0000<\u017f\u0001\u0000\u0000\u0000>\u0181"+
		"\u0001\u0000\u0000\u0000@\u018d\u0001\u0000\u0000\u0000B\u019c\u0001\u0000"+
		"\u0000\u0000D\u01a7\u0001\u0000\u0000\u0000F\u01b1\u0001\u0000\u0000\u0000"+
		"H\u01bf\u0001\u0000\u0000\u0000J\u01cb\u0001\u0000\u0000\u0000L\u01cd"+
		"\u0001\u0000\u0000\u0000N\u01d1\u0001\u0000\u0000\u0000P\u01d9\u0001\u0000"+
		"\u0000\u0000R\u01e2\u0001\u0000\u0000\u0000T\u01e5\u0001\u0000\u0000\u0000"+
		"VW\u0003\u0002\u0001\u0000WX\u0003\u0004\u0002\u0000X\u0001\u0001\u0000"+
		"\u0000\u0000YZ\u0005\n\u0000\u0000Z\u0003\u0001\u0000\u0000\u0000[\\\u0005"+
		"\u000b\u0000\u0000\\]\u0005\u0005\u0000\u0000]d\u0005\u0003\u0000\u0000"+
		"^c\u0003\u0006\u0003\u0000_c\u0003\u0010\b\u0000`c\u0003\u001c\u000e\u0000"+
		"ac\u0003\u001e\u000f\u0000b^\u0001\u0000\u0000\u0000b_\u0001\u0000\u0000"+
		"\u0000b`\u0001\u0000\u0000\u0000ba\u0001\u0000\u0000\u0000cf\u0001\u0000"+
		"\u0000\u0000db\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000eh\u0001"+
		"\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000gi\u0003$\u0012\u0000hg\u0001"+
		"\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000"+
		"jk\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000lm\u0005\u0004\u0000"+
		"\u0000m\u0005\u0001\u0000\u0000\u0000no\u0005\f\u0000\u0000oq\u0005\u0003"+
		"\u0000\u0000pr\u0003\b\u0004\u0000qp\u0001\u0000\u0000\u0000qr\u0001\u0000"+
		"\u0000\u0000rw\u0001\u0000\u0000\u0000st\u0005\u0006\u0000\u0000tv\u0003"+
		"\b\u0004\u0000us\u0001\u0000\u0000\u0000vy\u0001\u0000\u0000\u0000wu\u0001"+
		"\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xz\u0001\u0000\u0000\u0000"+
		"yw\u0001\u0000\u0000\u0000z{\u0005\u0004\u0000\u0000{\u0007\u0001\u0000"+
		"\u0000\u0000|\u0080\u0003\n\u0005\u0000}\u0080\u0003\f\u0006\u0000~\u0080"+
		"\u0003\u000e\u0007\u0000\u007f|\u0001\u0000\u0000\u0000\u007f}\u0001\u0000"+
		"\u0000\u0000\u007f~\u0001\u0000\u0000\u0000\u0080\t\u0001\u0000\u0000"+
		"\u0000\u0081\u0082\u0005\r\u0000\u0000\u0082\u0083\u0005\u0007\u0000\u0000"+
		"\u0083\u0084\u0005\u0005\u0000\u0000\u0084\u000b\u0001\u0000\u0000\u0000"+
		"\u0085\u0086\u0005\u000e\u0000\u0000\u0086\u0087\u0005\u0007\u0000\u0000"+
		"\u0087\u0088\u0005\u0005\u0000\u0000\u0088\r\u0001\u0000\u0000\u0000\u0089"+
		"\u008a\u0005\u000f\u0000\u0000\u008a\u008b\u0005\u0007\u0000\u0000\u008b"+
		"\u008c\u0005\u0005\u0000\u0000\u008c\u000f\u0001\u0000\u0000\u0000\u008d"+
		"\u008e\u0005\u0010\u0000\u0000\u008e\u0090\u0005\u0003\u0000\u0000\u008f"+
		"\u0091\u0003\u0012\t\u0000\u0090\u008f\u0001\u0000\u0000\u0000\u0090\u0091"+
		"\u0001\u0000\u0000\u0000\u0091\u0096\u0001\u0000\u0000\u0000\u0092\u0093"+
		"\u0005\u0006\u0000\u0000\u0093\u0095\u0003\u0012\t\u0000\u0094\u0092\u0001"+
		"\u0000\u0000\u0000\u0095\u0098\u0001\u0000\u0000\u0000\u0096\u0094\u0001"+
		"\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0099\u0001"+
		"\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0099\u009a\u0005"+
		"\u0004\u0000\u0000\u009a\u0011\u0001\u0000\u0000\u0000\u009b\u00a0\u0003"+
		"\u0014\n\u0000\u009c\u00a0\u0003\u0016\u000b\u0000\u009d\u00a0\u0003\u0018"+
		"\f\u0000\u009e\u00a0\u0003\u001a\r\u0000\u009f\u009b\u0001\u0000\u0000"+
		"\u0000\u009f\u009c\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000"+
		"\u0000\u009f\u009e\u0001\u0000\u0000\u0000\u00a0\u0013\u0001\u0000\u0000"+
		"\u0000\u00a1\u00a2\u0005\u0011\u0000\u0000\u00a2\u00a3\u0005\u0007\u0000"+
		"\u0000\u00a3\u00a4\u0005\u0005\u0000\u0000\u00a4\u0015\u0001\u0000\u0000"+
		"\u0000\u00a5\u00a6\u0005\u0012\u0000\u0000\u00a6\u00a7\u0005\u0007\u0000"+
		"\u0000\u00a7\u00a8\u0005\u0005\u0000\u0000\u00a8\u0017\u0001\u0000\u0000"+
		"\u0000\u00a9\u00aa\u0005\u0013\u0000\u0000\u00aa\u00ab\u0005\u0007\u0000"+
		"\u0000\u00ab\u00ac\u0005\u0005\u0000\u0000\u00ac\u0019\u0001\u0000\u0000"+
		"\u0000\u00ad\u00ae\u0005\u0014\u0000\u0000\u00ae\u00af\u0005\u0007\u0000"+
		"\u0000\u00af\u00b0\u0005\u0005\u0000\u0000\u00b0\u001b\u0001\u0000\u0000"+
		"\u0000\u00b1\u00b2\u0005\u0015\u0000\u0000\u00b2\u00b4\u0005\u0003\u0000"+
		"\u0000\u00b3\u00b5\u0003 \u0010\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000"+
		"\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u00ba\u0001\u0000\u0000\u0000"+
		"\u00b6\u00b7\u0005\u0006\u0000\u0000\u00b7\u00b9\u0003 \u0010\u0000\u00b8"+
		"\u00b6\u0001\u0000\u0000\u0000\u00b9\u00bc\u0001\u0000\u0000\u0000\u00ba"+
		"\u00b8\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb"+
		"\u00bd\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000\u0000\u0000\u00bd"+
		"\u00be\u0005\u0004\u0000\u0000\u00be\u001d\u0001\u0000\u0000\u0000\u00bf"+
		"\u00c0\u0005\u0016\u0000\u0000\u00c0\u00c2\u0005\u0003\u0000\u0000\u00c1"+
		"\u00c3\u0003\"\u0011\u0000\u00c2\u00c1\u0001\u0000\u0000\u0000\u00c2\u00c3"+
		"\u0001\u0000\u0000\u0000\u00c3\u00c8\u0001\u0000\u0000\u0000\u00c4\u00c5"+
		"\u0005\u0006\u0000\u0000\u00c5\u00c7\u0003\"\u0011\u0000\u00c6\u00c4\u0001"+
		"\u0000\u0000\u0000\u00c7\u00ca\u0001\u0000\u0000\u0000\u00c8\u00c6\u0001"+
		"\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00cb\u0001"+
		"\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005"+
		"\u0004\u0000\u0000\u00cc\u001f\u0001\u0000\u0000\u0000\u00cd\u00d0\u0003"+
		"\"\u0011\u0000\u00ce\u00d0\u0005\t\u0000\u0000\u00cf\u00cd\u0001\u0000"+
		"\u0000\u0000\u00cf\u00ce\u0001\u0000\u0000\u0000\u00d0!\u0001\u0000\u0000"+
		"\u0000\u00d1\u00d2\u0005\b\u0000\u0000\u00d2#\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d4\u0005\u001c\u0000\u0000\u00d4\u00d5\u0005\u0005\u0000\u0000"+
		"\u00d5\u00d6\u0005\u0003\u0000\u0000\u00d6\u00da\u0003&\u0013\u0000\u00d7"+
		"\u00d9\u0003(\u0014\u0000\u00d8\u00d7\u0001\u0000\u0000\u0000\u00d9\u00dc"+
		"\u0001\u0000\u0000\u0000\u00da\u00d8\u0001\u0000\u0000\u0000\u00da\u00db"+
		"\u0001\u0000\u0000\u0000\u00db\u00dd\u0001\u0000\u0000\u0000\u00dc\u00da"+
		"\u0001\u0000\u0000\u0000\u00dd\u00de\u0005\u0004\u0000\u0000\u00de%\u0001"+
		"\u0000\u0000\u0000\u00df\u00e0\u0005\u001d\u0000\u0000\u00e0\u00e1\u0005"+
		"\u0003\u0000\u0000\u00e1\u00e2\u0003*\u0015\u0000\u00e2\u00e3\u0005\u0004"+
		"\u0000\u0000\u00e3\'\u0001\u0000\u0000\u0000\u00e4\u00e5\u0005\u001a\u0000"+
		"\u0000\u00e5\u00e8\u0005\u0005\u0000\u0000\u00e6\u00e7\u0005\u001b\u0000"+
		"\u0000\u00e7\u00e9\u0005\u0005\u0000\u0000\u00e8\u00e6\u0001\u0000\u0000"+
		"\u0000\u00e8\u00e9\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000"+
		"\u0000\u00ea\u00eb\u0005\u0003\u0000\u0000\u00eb\u00ec\u0003*\u0015\u0000"+
		"\u00ec\u00ed\u0005\u0004\u0000\u0000\u00ed)\u0001\u0000\u0000\u0000\u00ee"+
		"\u00f4\u0003,\u0016\u0000\u00ef\u00f4\u0003.\u0017\u0000\u00f0\u00f4\u0003"+
		"0\u0018\u0000\u00f1\u00f4\u00034\u001a\u0000\u00f2\u00f4\u0003D\"\u0000"+
		"\u00f3\u00ee\u0001\u0000\u0000\u0000\u00f3\u00ef\u0001\u0000\u0000\u0000"+
		"\u00f3\u00f0\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000"+
		"\u00f3\u00f2\u0001\u0000\u0000\u0000\u00f4\u00f7\u0001\u0000\u0000\u0000"+
		"\u00f5\u00f3\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000"+
		"\u00f6+\u0001\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000\u00f8"+
		"\u00f9\u0005\u0017\u0000\u0000\u00f9\u00fb\u0005\u0003\u0000\u0000\u00fa"+
		"\u00fc\u0003 \u0010\u0000\u00fb\u00fa\u0001\u0000\u0000\u0000\u00fb\u00fc"+
		"\u0001\u0000\u0000\u0000\u00fc\u0101\u0001\u0000\u0000\u0000\u00fd\u00fe"+
		"\u0005\u0006\u0000\u0000\u00fe\u0100\u0003 \u0010\u0000\u00ff\u00fd\u0001"+
		"\u0000\u0000\u0000\u0100\u0103\u0001\u0000\u0000\u0000\u0101\u00ff\u0001"+
		"\u0000\u0000\u0000\u0101\u0102\u0001\u0000\u0000\u0000\u0102\u0104\u0001"+
		"\u0000\u0000\u0000\u0103\u0101\u0001\u0000\u0000\u0000\u0104\u0105\u0005"+
		"\u0004\u0000\u0000\u0105-\u0001\u0000\u0000\u0000\u0106\u0107\u0005\u0018"+
		"\u0000\u0000\u0107\u0109\u0005\u0003\u0000\u0000\u0108\u010a\u00032\u0019"+
		"\u0000\u0109\u0108\u0001\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000"+
		"\u0000\u010a\u010f\u0001\u0000\u0000\u0000\u010b\u010c\u0005\u0006\u0000"+
		"\u0000\u010c\u010e\u00032\u0019\u0000\u010d\u010b\u0001\u0000\u0000\u0000"+
		"\u010e\u0111\u0001\u0000\u0000\u0000\u010f\u010d\u0001\u0000\u0000\u0000"+
		"\u010f\u0110\u0001\u0000\u0000\u0000\u0110\u0112\u0001\u0000\u0000\u0000"+
		"\u0111\u010f\u0001\u0000\u0000\u0000\u0112\u0113\u0005\u0004\u0000\u0000"+
		"\u0113/\u0001\u0000\u0000\u0000\u0114\u0115\u0005\u0019\u0000\u0000\u0115"+
		"\u0117\u0005\u0003\u0000\u0000\u0116\u0118\u00032\u0019\u0000\u0117\u0116"+
		"\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000\u0000\u0118\u011d"+
		"\u0001\u0000\u0000\u0000\u0119\u011a\u0005\u0006\u0000\u0000\u011a\u011c"+
		"\u00032\u0019\u0000\u011b\u0119\u0001\u0000\u0000\u0000\u011c\u011f\u0001"+
		"\u0000\u0000\u0000\u011d\u011b\u0001\u0000\u0000\u0000\u011d\u011e\u0001"+
		"\u0000\u0000\u0000\u011e\u0120\u0001\u0000\u0000\u0000\u011f\u011d\u0001"+
		"\u0000\u0000\u0000\u0120\u0121\u0005\u0004\u0000\u0000\u01211\u0001\u0000"+
		"\u0000\u0000\u0122\u0123\u0005\u0005\u0000\u0000\u0123\u0124\u0005\u0007"+
		"\u0000\u0000\u0124\u0125\u0005\u0005\u0000\u0000\u01253\u0001\u0000\u0000"+
		"\u0000\u0126\u0127\u0005\u001e\u0000\u0000\u0127\u012b\u0005\"\u0000\u0000"+
		"\u0128\u012a\u00036\u001b\u0000\u0129\u0128\u0001\u0000\u0000\u0000\u012a"+
		"\u012d\u0001\u0000\u0000\u0000\u012b\u0129\u0001\u0000\u0000\u0000\u012b"+
		"\u012c\u0001\u0000\u0000\u0000\u012c\u012e\u0001\u0000\u0000\u0000\u012d"+
		"\u012b\u0001\u0000\u0000\u0000\u012e\u012f\u0005$\u0000\u0000\u012f5\u0001"+
		"\u0000\u0000\u0000\u0130\u0132\u0005#\u0000\u0000\u0131\u0133\u0003L&"+
		"\u0000\u0132\u0131\u0001\u0000\u0000\u0000\u0132\u0133\u0001\u0000\u0000"+
		"\u0000\u0133\u0134\u0001\u0000\u0000\u0000\u0134\u0139\u00055\u0000\u0000"+
		"\u0135\u0137\u0005+\u0000\u0000\u0136\u0138\u00038\u001c\u0000\u0137\u0136"+
		"\u0001\u0000\u0000\u0000\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u013a"+
		"\u0001\u0000\u0000\u0000\u0139\u0135\u0001\u0000\u0000\u0000\u013a\u013b"+
		"\u0001\u0000\u0000\u0000\u013b\u0139\u0001\u0000\u0000\u0000\u013b\u013c"+
		"\u0001\u0000\u0000\u0000\u013c\u013d\u0001\u0000\u0000\u0000\u013d\u014f"+
		"\u00051\u0000\u0000\u013e\u013f\u0005!\u0000\u0000\u013f\u0141\u0005#"+
		"\u0000\u0000\u0140\u0142\u0003L&\u0000\u0141\u0140\u0001\u0000\u0000\u0000"+
		"\u0141\u0142\u0001\u0000\u0000\u0000\u0142\u0143\u0001\u0000\u0000\u0000"+
		"\u0143\u0148\u00055\u0000\u0000\u0144\u0146\u0005+\u0000\u0000\u0145\u0147"+
		"\u00038\u001c\u0000\u0146\u0145\u0001\u0000\u0000\u0000\u0146\u0147\u0001"+
		"\u0000\u0000\u0000\u0147\u0149\u0001\u0000\u0000\u0000\u0148\u0144\u0001"+
		"\u0000\u0000\u0000\u0149\u014a\u0001\u0000\u0000\u0000\u014a\u0148\u0001"+
		"\u0000\u0000\u0000\u014a\u014b\u0001\u0000\u0000\u0000\u014b\u014c\u0001"+
		"\u0000\u0000\u0000\u014c\u014d\u0005.\u0000\u0000\u014d\u014f\u00051\u0000"+
		"\u0000\u014e\u0130\u0001\u0000\u0000\u0000\u014e\u013e\u0001\u0000\u0000"+
		"\u0000\u014f7\u0001\u0000\u0000\u0000\u0150\u0154\u0003:\u001d\u0000\u0151"+
		"\u0154\u0003B!\u0000\u0152\u0154\u0003<\u001e\u0000\u0153\u0150\u0001"+
		"\u0000\u0000\u0000\u0153\u0151\u0001\u0000\u0000\u0000\u0153\u0152\u0001"+
		"\u0000\u0000\u0000\u01549\u0001\u0000\u0000\u0000\u0155\u0157\u0005.\u0000"+
		"\u0000\u0156\u0155\u0001\u0000\u0000\u0000\u0156\u0157\u0001\u0000\u0000"+
		"\u0000\u0157\u0158\u0001\u0000\u0000\u0000\u0158\u0159\u0005/\u0000\u0000"+
		"\u0159\u015a\u00056\u0000\u0000\u015a;\u0001\u0000\u0000\u0000\u015b\u015d"+
		"\u0005,\u0000\u0000\u015c\u015e\u0003L&\u0000\u015d\u015c\u0001\u0000"+
		"\u0000\u0000\u015d\u015e\u0001\u0000\u0000\u0000\u015e\u015f\u0001\u0000"+
		"\u0000\u0000\u015f\u0164\u00055\u0000\u0000\u0160\u0162\u0005+\u0000\u0000"+
		"\u0161\u0163\u00038\u001c\u0000\u0162\u0161\u0001\u0000\u0000\u0000\u0162"+
		"\u0163\u0001\u0000\u0000\u0000\u0163\u0165\u0001\u0000\u0000\u0000\u0164"+
		"\u0160\u0001\u0000\u0000\u0000\u0165\u0166\u0001\u0000\u0000\u0000\u0166"+
		"\u0164\u0001\u0000\u0000\u0000\u0166\u0167\u0001\u0000\u0000\u0000\u0167"+
		"\u016a\u0001\u0000\u0000\u0000\u0168\u016b\u00051\u0000\u0000\u0169\u016b"+
		"\u0003>\u001f\u0000\u016a\u0168\u0001\u0000\u0000\u0000\u016a\u0169\u0001"+
		"\u0000\u0000\u0000\u016b\u0180\u0001\u0000\u0000\u0000\u016c\u016d\u0005"+
		".\u0000\u0000\u016d\u016f\u0005,\u0000\u0000\u016e\u0170\u0003L&\u0000"+
		"\u016f\u016e\u0001\u0000\u0000\u0000\u016f\u0170\u0001\u0000\u0000\u0000"+
		"\u0170\u0171\u0001\u0000\u0000\u0000\u0171\u0176\u00055\u0000\u0000\u0172"+
		"\u0174\u0005+\u0000\u0000\u0173\u0175\u00038\u001c\u0000\u0174\u0173\u0001"+
		"\u0000\u0000\u0000\u0174\u0175\u0001\u0000\u0000\u0000\u0175\u0177\u0001"+
		"\u0000\u0000\u0000\u0176\u0172\u0001\u0000\u0000\u0000\u0177\u0178\u0001"+
		"\u0000\u0000\u0000\u0178\u0176\u0001\u0000\u0000\u0000\u0178\u0179\u0001"+
		"\u0000\u0000\u0000\u0179\u017d\u0001\u0000\u0000\u0000\u017a\u017b\u0005"+
		".\u0000\u0000\u017b\u017e\u00051\u0000\u0000\u017c\u017e\u0003@ \u0000"+
		"\u017d\u017a\u0001\u0000\u0000\u0000\u017d\u017c\u0001\u0000\u0000\u0000"+
		"\u017e\u0180\u0001\u0000\u0000\u0000\u017f\u015b\u0001\u0000\u0000\u0000"+
		"\u017f\u016c\u0001\u0000\u0000\u0000\u0180=\u0001\u0000\u0000\u0000\u0181"+
		"\u0182\u00050\u0000\u0000\u0182\u0187\u00055\u0000\u0000\u0183\u0185\u0005"+
		"+\u0000\u0000\u0184\u0186\u00038\u001c\u0000\u0185\u0184\u0001\u0000\u0000"+
		"\u0000\u0185\u0186\u0001\u0000\u0000\u0000\u0186\u0188\u0001\u0000\u0000"+
		"\u0000\u0187\u0183\u0001\u0000\u0000\u0000\u0188\u0189\u0001\u0000\u0000"+
		"\u0000\u0189\u0187\u0001\u0000\u0000\u0000\u0189\u018a\u0001\u0000\u0000"+
		"\u0000\u018a\u018b\u0001\u0000\u0000\u0000\u018b\u018c\u00051\u0000\u0000"+
		"\u018c?\u0001\u0000\u0000\u0000\u018d\u018e\u0005.\u0000\u0000\u018e\u018f"+
		"\u00050\u0000\u0000\u018f\u0194\u00055\u0000\u0000\u0190\u0192\u0005+"+
		"\u0000\u0000\u0191\u0193\u00038\u001c\u0000\u0192\u0191\u0001\u0000\u0000"+
		"\u0000\u0192\u0193\u0001\u0000\u0000\u0000\u0193\u0195\u0001\u0000\u0000"+
		"\u0000\u0194\u0190\u0001\u0000\u0000\u0000\u0195\u0196\u0001\u0000\u0000"+
		"\u0000\u0196\u0194\u0001\u0000\u0000\u0000\u0196\u0197\u0001\u0000\u0000"+
		"\u0000\u0197\u0198\u0001\u0000\u0000\u0000\u0198\u0199\u0005.\u0000\u0000"+
		"\u0199\u019a\u00051\u0000\u0000\u019aA\u0001\u0000\u0000\u0000\u019b\u019d"+
		"\u0005.\u0000\u0000\u019c\u019b\u0001\u0000\u0000\u0000\u019c\u019d\u0001"+
		"\u0000\u0000\u0000\u019d\u01a0\u0001\u0000\u0000\u0000\u019e\u01a1\u0005"+
		",\u0000\u0000\u019f\u01a1\u0003H$\u0000\u01a0\u019e\u0001\u0000\u0000"+
		"\u0000\u01a0\u019f\u0001\u0000\u0000\u0000\u01a1\u01a3\u0001\u0000\u0000"+
		"\u0000\u01a2\u01a4\u0003L&\u0000\u01a3\u01a2\u0001\u0000\u0000\u0000\u01a3"+
		"\u01a4\u0001\u0000\u0000\u0000\u01a4\u01a5\u0001\u0000\u0000\u0000\u01a5"+
		"\u01a6\u0007\u0000\u0000\u0000\u01a6C\u0001\u0000\u0000\u0000\u01a7\u01a8"+
		"\u0005\u001f\u0000\u0000\u01a8\u01ac\u0005&\u0000\u0000\u01a9\u01ab\u0003"+
		"F#\u0000\u01aa\u01a9\u0001\u0000\u0000\u0000\u01ab\u01ae\u0001\u0000\u0000"+
		"\u0000\u01ac\u01aa\u0001\u0000\u0000\u0000\u01ac\u01ad\u0001\u0000\u0000"+
		"\u0000\u01ad\u01af\u0001\u0000\u0000\u0000\u01ae\u01ac\u0001\u0000\u0000"+
		"\u0000\u01af\u01b0\u0005)\u0000\u0000\u01b0E\u0001\u0000\u0000\u0000\u01b1"+
		"\u01b2\u0005\'\u0000\u0000\u01b2\u01b3\u0003H$\u0000\u01b3\u01b4\u0007"+
		"\u0001\u0000\u0000\u01b4\u01b9\u0005C\u0000\u0000\u01b5\u01b7\u0005+\u0000"+
		"\u0000\u01b6\u01b8\u00038\u001c\u0000\u01b7\u01b6\u0001\u0000\u0000\u0000"+
		"\u01b7\u01b8\u0001\u0000\u0000\u0000\u01b8\u01ba\u0001\u0000\u0000\u0000"+
		"\u01b9\u01b5\u0001\u0000\u0000\u0000\u01ba\u01bb\u0001\u0000\u0000\u0000"+
		"\u01bb\u01b9\u0001\u0000\u0000\u0000\u01bb\u01bc\u0001\u0000\u0000\u0000"+
		"\u01bc\u01bd\u0001\u0000\u0000\u0000\u01bd\u01be\u00051\u0000\u0000\u01be"+
		"G\u0001\u0000\u0000\u0000\u01bf\u01c1\u0007\u0002\u0000\u0000\u01c0\u01c2"+
		"\u0003J%\u0000\u01c1\u01c0\u0001\u0000\u0000\u0000\u01c2\u01c3\u0001\u0000"+
		"\u0000\u0000\u01c3\u01c1\u0001\u0000\u0000\u0000\u01c3\u01c4\u0001\u0000"+
		"\u0000\u0000\u01c4\u01c5\u0001\u0000\u0000\u0000\u01c5\u01c6\u0005:\u0000"+
		"\u0000\u01c6I\u0001\u0000\u0000\u0000\u01c7\u01c8\u00058\u0000\u0000\u01c8"+
		"\u01c9\u0005<\u0000\u0000\u01c9\u01cc\u0005=\u0000\u0000\u01ca\u01cc\u0005"+
		"9\u0000\u0000\u01cb\u01c7\u0001\u0000\u0000\u0000\u01cb\u01ca\u0001\u0000"+
		"\u0000\u0000\u01ccK\u0001\u0000\u0000\u0000\u01cd\u01ce\u0007\u0003\u0000"+
		"\u0000\u01ce\u01cf\u0003N\'\u0000\u01cf\u01d0\u0005F\u0000\u0000\u01d0"+
		"M\u0001\u0000\u0000\u0000\u01d1\u01d6\u0003P(\u0000\u01d2\u01d3\u0005"+
		"J\u0000\u0000\u01d3\u01d5\u0003P(\u0000\u01d4\u01d2\u0001\u0000\u0000"+
		"\u0000\u01d5\u01d8\u0001\u0000\u0000\u0000\u01d6\u01d4\u0001\u0000\u0000"+
		"\u0000\u01d6\u01d7\u0001\u0000\u0000\u0000\u01d7O\u0001\u0000\u0000\u0000"+
		"\u01d8\u01d6\u0001\u0000\u0000\u0000\u01d9\u01da\u0007\u0004\u0000\u0000"+
		"\u01da\u01db\u0005I\u0000\u0000\u01db\u01dc\u0005E\u0000\u0000\u01dc\u01dd"+
		"\u0003R)\u0000\u01dd\u01de\u0005L\u0000\u0000\u01deQ\u0001\u0000\u0000"+
		"\u0000\u01df\u01e1\u0003T*\u0000\u01e0\u01df\u0001\u0000\u0000\u0000\u01e1"+
		"\u01e4\u0001\u0000\u0000\u0000\u01e2\u01e0\u0001\u0000\u0000\u0000\u01e2"+
		"\u01e3\u0001\u0000\u0000\u0000\u01e3S\u0001\u0000\u0000\u0000\u01e4\u01e2"+
		"\u0001\u0000\u0000\u0000\u01e5\u01e6\u0007\u0005\u0000\u0000\u01e6U\u0001"+
		"\u0000\u0000\u00009bdjqw\u007f\u0090\u0096\u009f\u00b4\u00ba\u00c2\u00c8"+
		"\u00cf\u00da\u00e8\u00f3\u00f5\u00fb\u0101\u0109\u010f\u0117\u011d\u012b"+
		"\u0132\u0137\u013b\u0141\u0146\u014a\u014e\u0153\u0156\u015d\u0162\u0166"+
		"\u016a\u016f\u0174\u0178\u017d\u017f\u0185\u0189\u0192\u0196\u019c\u01a0"+
		"\u01a3\u01ac\u01b7\u01bb\u01c3\u01cb\u01d6\u01e2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}