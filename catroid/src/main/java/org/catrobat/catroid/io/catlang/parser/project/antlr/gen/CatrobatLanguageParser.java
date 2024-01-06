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
		UDB_DEFINE=32, ELSE_BRICK=33, NOTE_BRICK=34, DISABLED_BRICK_INDICATION=35, 
		BRICK_NAME=36, UDB_START=37, BRICK_MODE_WS=38, BRICK_MODE_BRACKET_OPEN=39, 
		SEMICOLON=40, BRICK_BODY_OPEN=41, UDB_MODE_WS=42, UDB_PARAM_START=43, 
		UDB_LABEL=44, UDB_END=45, UDB_PARAM_MODE_WS=46, UDB_PARAM_TEXT=47, UDB_PARAM_END=48, 
		UDB_AFTER_MODE_WS=49, UDB_MODE_BRACKET_OPEN=50, UDB_SEMICOLON=51, UDB_DEFINITION_SCREEN_REFRESH=52, 
		UDB_DEFINITION_NO_SCREEN_REFRESH=53, UDB_BODY_OPEN=54, PARAM_MODE_WS=55, 
		PARAM_MODE_BRACKET_OPEN=56, PARAM_MODE_BRACKET_CLOSE=57, PARAM_MODE_NAME=58, 
		PARAM_MODE_COLON=59, PARAM_SEPARATOR=60, FORMULA_MODE_WS=61, FORMULA_MODE_BRACKET_CLOSE=62, 
		FORMULA_MODE_BRACKET_OPEN=63, FORMULA_MODE_ANYTHING=64, FORMULA_MODE_STRING_BEGIN=65, 
		FORMULA_MODE_VARIABLE_BEGIN=66, FORMULA_MODE_UDB_PARAM_BEGIN=67, FORMULA_LIST_MODE_BEGIN=68, 
		FORMULA_STRING_MODE_ANYTHING=69, FORMULA_STRING_MODE_END=70, FORMULA_VARIABLE_MODE_ANYTHING=71, 
		FORMULA_VARIABLE_MODE_END=72, FORMULA_UDB_PARAM_MODE_ANYTHING=73, FORMULA_UDB_PARAM_MODE_END=74, 
		FORMULA_LIST_MODE_ANYTHING=75, FORMULA_LIST_MODE_END=76;
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
		RULE_brick_with_body = 32, RULE_elseBranch = 33, RULE_brick_invocation = 34, 
		RULE_brick_condition = 35, RULE_arg_list = 36, RULE_argument = 37, RULE_formula = 38, 
		RULE_formulaElement = 39;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "programHeader", "programBody", "metadata", "metadataContent", 
			"description", "catrobatVersion", "catrobatAppVersion", "stage", "stageContent", 
			"landscapeMode", "height", "width", "displayMode", "globals", "multiplayerVariables", 
			"variableOrListDeclaration", "variableDeclaration", "scene", "background", 
			"actor", "actorContent", "localVariables", "looks", "sounds", "looksAndSoundsContent", 
			"scripts", "userDefinedScripts", "userDefinedScript", "brick_defintion", 
			"userDefinedBrick", "userDefinedBrickPart", "brick_with_body", "elseBranch", 
			"brick_invocation", "brick_condition", "arg_list", "argument", "formula", 
			"formulaElement"
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
			"'User Defined Bricks'", "'Define'", null, null, "'//'", null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "'with screen refresh as'", "'without screen refresh as'"
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
			"UDB_DEFINE", "ELSE_BRICK", "NOTE_BRICK", "DISABLED_BRICK_INDICATION", 
			"BRICK_NAME", "UDB_START", "BRICK_MODE_WS", "BRICK_MODE_BRACKET_OPEN", 
			"SEMICOLON", "BRICK_BODY_OPEN", "UDB_MODE_WS", "UDB_PARAM_START", "UDB_LABEL", 
			"UDB_END", "UDB_PARAM_MODE_WS", "UDB_PARAM_TEXT", "UDB_PARAM_END", "UDB_AFTER_MODE_WS", 
			"UDB_MODE_BRACKET_OPEN", "UDB_SEMICOLON", "UDB_DEFINITION_SCREEN_REFRESH", 
			"UDB_DEFINITION_NO_SCREEN_REFRESH", "UDB_BODY_OPEN", "PARAM_MODE_WS", 
			"PARAM_MODE_BRACKET_OPEN", "PARAM_MODE_BRACKET_CLOSE", "PARAM_MODE_NAME", 
			"PARAM_MODE_COLON", "PARAM_SEPARATOR", "FORMULA_MODE_WS", "FORMULA_MODE_BRACKET_CLOSE", 
			"FORMULA_MODE_BRACKET_OPEN", "FORMULA_MODE_ANYTHING", "FORMULA_MODE_STRING_BEGIN", 
			"FORMULA_MODE_VARIABLE_BEGIN", "FORMULA_MODE_UDB_PARAM_BEGIN", "FORMULA_LIST_MODE_BEGIN", 
			"FORMULA_STRING_MODE_ANYTHING", "FORMULA_STRING_MODE_END", "FORMULA_VARIABLE_MODE_ANYTHING", 
			"FORMULA_VARIABLE_MODE_END", "FORMULA_UDB_PARAM_MODE_ANYTHING", "FORMULA_UDB_PARAM_MODE_END", 
			"FORMULA_LIST_MODE_ANYTHING", "FORMULA_LIST_MODE_END"
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
			setState(80);
			programHeader();
			setState(81);
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
			setState(83);
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
			setState(85);
			match(PROGRAM);
			setState(86);
			match(STRING);
			setState(87);
			match(CURLY_BRACKET_OPEN);
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6361088L) != 0)) {
				{
				setState(92);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case METADATA:
					{
					setState(88);
					metadata();
					}
					break;
				case STAGE:
					{
					setState(89);
					stage();
					}
					break;
				case GLOBALS:
					{
					setState(90);
					globals();
					}
					break;
				case MULTIPLAYER_VARIABLES:
					{
					setState(91);
					multiplayerVariables();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(98); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(97);
				scene();
				}
				}
				setState(100); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCENE );
			setState(102);
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
			setState(104);
			match(METADATA);
			setState(105);
			match(CURLY_BRACKET_OPEN);
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 57344L) != 0)) {
				{
				setState(106);
				metadataContent();
				}
			}

			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(109);
				match(SEPARATOR);
				setState(110);
				metadataContent();
				}
				}
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(116);
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
			setState(121);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DESCRIPTION:
				{
				setState(118);
				description();
				}
				break;
			case CATROBAT_VERSION:
				{
				setState(119);
				catrobatVersion();
				}
				break;
			case CATRPBAT_APP_VERSION:
				{
				setState(120);
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
			setState(123);
			match(DESCRIPTION);
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
			setState(127);
			match(CATROBAT_VERSION);
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
			setState(131);
			match(CATRPBAT_APP_VERSION);
			setState(132);
			match(COLON);
			setState(133);
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
			setState(135);
			match(STAGE);
			setState(136);
			match(CURLY_BRACKET_OPEN);
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0)) {
				{
				setState(137);
				stageContent();
				}
			}

			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(140);
				match(SEPARATOR);
				setState(141);
				stageContent();
				}
				}
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(147);
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
			setState(153);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANDSCAPE_MODE:
				{
				setState(149);
				landscapeMode();
				}
				break;
			case HEIGHT:
				{
				setState(150);
				height();
				}
				break;
			case WIDTH:
				{
				setState(151);
				width();
				}
				break;
			case DISPLAY_MODE:
				{
				setState(152);
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
			setState(155);
			match(LANDSCAPE_MODE);
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
			setState(159);
			match(HEIGHT);
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
			setState(163);
			match(WIDTH);
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
			setState(167);
			match(DISPLAY_MODE);
			setState(168);
			match(COLON);
			setState(169);
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
			setState(171);
			match(GLOBALS);
			setState(172);
			match(CURLY_BRACKET_OPEN);
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(173);
				variableOrListDeclaration();
				}
			}

			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(176);
				match(SEPARATOR);
				setState(177);
				variableOrListDeclaration();
				}
				}
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(183);
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
			setState(185);
			match(MULTIPLAYER_VARIABLES);
			setState(186);
			match(CURLY_BRACKET_OPEN);
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF) {
				{
				setState(187);
				variableDeclaration();
				}
			}

			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(190);
				match(SEPARATOR);
				setState(191);
				variableDeclaration();
				}
				}
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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
			setState(201);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VARIABLE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(199);
				variableDeclaration();
				}
				break;
			case LIST_REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(200);
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
			setState(203);
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
			setState(205);
			match(SCENE);
			setState(206);
			match(STRING);
			setState(207);
			match(CURLY_BRACKET_OPEN);
			setState(208);
			background();
			setState(212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ACTOR_OR_OBJECT) {
				{
				{
				setState(209);
				actor();
				}
				}
				setState(214);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(215);
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
			setState(217);
			match(BACKGROUND);
			setState(218);
			match(CURLY_BRACKET_OPEN);
			setState(219);
			actorContent();
			setState(220);
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
			setState(222);
			match(ACTOR_OR_OBJECT);
			setState(223);
			match(STRING);
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OF_TYPE) {
				{
				setState(224);
				match(OF_TYPE);
				setState(225);
				match(STRING);
				}
			}

			setState(228);
			match(CURLY_BRACKET_OPEN);
			setState(229);
			actorContent();
			setState(230);
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
			setState(237); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(237);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LOCAL_VARIABLES:
					{
					setState(232);
					localVariables();
					}
					break;
				case LOOKS:
					{
					setState(233);
					looks();
					}
					break;
				case SOUNDS:
					{
					setState(234);
					sounds();
					}
					break;
				case SCRIPTS:
					{
					setState(235);
					scripts();
					}
					break;
				case USER_DEFINED_SCRIPTS:
					{
					setState(236);
					userDefinedScripts();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(239); 
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
			setState(241);
			match(LOCAL_VARIABLES);
			setState(242);
			match(CURLY_BRACKET_OPEN);
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE_REF || _la==LIST_REF) {
				{
				setState(243);
				variableOrListDeclaration();
				}
			}

			setState(250);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(246);
				match(SEPARATOR);
				setState(247);
				variableOrListDeclaration();
				}
				}
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(253);
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
			setState(255);
			match(LOOKS);
			setState(256);
			match(CURLY_BRACKET_OPEN);
			setState(258);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(257);
				looksAndSoundsContent();
				}
			}

			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(260);
				match(SEPARATOR);
				setState(261);
				looksAndSoundsContent();
				}
				}
				setState(266);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(267);
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
			setState(269);
			match(SOUNDS);
			setState(270);
			match(CURLY_BRACKET_OPEN);
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(271);
				looksAndSoundsContent();
				}
			}

			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(274);
				match(SEPARATOR);
				setState(275);
				looksAndSoundsContent();
				}
				}
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(281);
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
			setState(283);
			match(STRING);
			setState(284);
			match(COLON);
			setState(285);
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
			setState(287);
			match(SCRIPTS);
			setState(288);
			match(CURLY_BRACKET_OPEN);
			setState(292);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DISABLED_BRICK_INDICATION || _la==BRICK_NAME) {
				{
				{
				setState(289);
				brick_with_body();
				}
				}
				setState(294);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(295);
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
			setState(297);
			match(USER_DEFINED_SCRIPTS);
			setState(298);
			match(CURLY_BRACKET_OPEN);
			setState(302);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==UDB_DEFINE || _la==DISABLED_BRICK_INDICATION) {
				{
				{
				setState(299);
				userDefinedScript();
				}
				}
				setState(304);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(305);
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
			setState(333);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case UDB_DEFINE:
				enterOuterAlt(_localctx, 1);
				{
				setState(307);
				match(UDB_DEFINE);
				setState(308);
				userDefinedBrick();
				setState(309);
				_la = _input.LA(1);
				if ( !(_la==UDB_DEFINITION_SCREEN_REFRESH || _la==UDB_DEFINITION_NO_SCREEN_REFRESH) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(310);
				match(UDB_BODY_OPEN);
				setState(314);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 257698037760L) != 0)) {
					{
					{
					setState(311);
					brick_defintion();
					}
					}
					setState(316);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(317);
				match(CURLY_BRACKET_CLOSE);
				}
				break;
			case DISABLED_BRICK_INDICATION:
				enterOuterAlt(_localctx, 2);
				{
				setState(319);
				match(DISABLED_BRICK_INDICATION);
				setState(320);
				match(UDB_DEFINE);
				setState(321);
				userDefinedBrick();
				setState(322);
				_la = _input.LA(1);
				if ( !(_la==UDB_DEFINITION_SCREEN_REFRESH || _la==UDB_DEFINITION_NO_SCREEN_REFRESH) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(323);
				match(UDB_BODY_OPEN);
				setState(327);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(324);
						brick_defintion();
						}
						} 
					}
					setState(329);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
				}
				setState(330);
				match(DISABLED_BRICK_INDICATION);
				setState(331);
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
			setState(341);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(336);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DISABLED_BRICK_INDICATION) {
					{
					setState(335);
					match(DISABLED_BRICK_INDICATION);
					}
				}

				setState(338);
				match(NOTE_BRICK);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(339);
				brick_invocation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(340);
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
			setState(343);
			match(UDB_START);
			setState(345); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(344);
				userDefinedBrickPart();
				}
				}
				setState(347); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==UDB_PARAM_START || _la==UDB_LABEL );
			setState(349);
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
			setState(355);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case UDB_PARAM_START:
				enterOuterAlt(_localctx, 1);
				{
				setState(351);
				match(UDB_PARAM_START);
				setState(352);
				match(UDB_PARAM_TEXT);
				setState(353);
				match(UDB_PARAM_END);
				}
				break;
			case UDB_LABEL:
				enterOuterAlt(_localctx, 2);
				{
				setState(354);
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
		public ElseBranchContext elseBranch() {
			return getRuleContext(ElseBranchContext.class,0);
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
			setState(389);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRICK_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(357);
				match(BRICK_NAME);
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(358);
					brick_condition();
					}
				}

				setState(361);
				match(BRICK_BODY_OPEN);
				setState(365);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 257698037760L) != 0)) {
					{
					{
					setState(362);
					brick_defintion();
					}
					}
					setState(367);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(368);
				match(CURLY_BRACKET_CLOSE);
				setState(370);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE_BRICK) {
					{
					setState(369);
					elseBranch();
					}
				}

				}
				break;
			case DISABLED_BRICK_INDICATION:
				enterOuterAlt(_localctx, 2);
				{
				setState(372);
				match(DISABLED_BRICK_INDICATION);
				setState(373);
				match(BRICK_NAME);
				setState(375);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
					{
					setState(374);
					brick_condition();
					}
				}

				setState(377);
				match(BRICK_BODY_OPEN);
				setState(381);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(378);
						brick_defintion();
						}
						} 
					}
					setState(383);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
				}
				setState(384);
				match(DISABLED_BRICK_INDICATION);
				setState(385);
				match(CURLY_BRACKET_CLOSE);
				setState(387);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE_BRICK) {
					{
					setState(386);
					elseBranch();
					}
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
		public TerminalNode ELSE_BRICK() { return getToken(CatrobatLanguageParser.ELSE_BRICK, 0); }
		public TerminalNode CURLY_BRACKET_OPEN() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_OPEN, 0); }
		public TerminalNode CURLY_BRACKET_CLOSE() { return getToken(CatrobatLanguageParser.CURLY_BRACKET_CLOSE, 0); }
		public List<Brick_defintionContext> brick_defintion() {
			return getRuleContexts(Brick_defintionContext.class);
		}
		public Brick_defintionContext brick_defintion(int i) {
			return getRuleContext(Brick_defintionContext.class,i);
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
		enterRule(_localctx, 66, RULE_elseBranch);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(391);
			match(ELSE_BRICK);
			setState(392);
			match(CURLY_BRACKET_OPEN);
			setState(396);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 257698037760L) != 0)) {
				{
				{
				setState(393);
				brick_defintion();
				}
				}
				setState(398);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(399);
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
		enterRule(_localctx, 68, RULE_brick_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(402);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DISABLED_BRICK_INDICATION) {
				{
				setState(401);
				match(DISABLED_BRICK_INDICATION);
				}
			}

			setState(406);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRICK_NAME:
				{
				setState(404);
				match(BRICK_NAME);
				}
				break;
			case UDB_START:
				{
				setState(405);
				userDefinedBrick();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(409);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) {
				{
				setState(408);
				brick_condition();
				}
			}

			setState(411);
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
		enterRule(_localctx, 70, RULE_brick_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(413);
			_la = _input.LA(1);
			if ( !(_la==BRICK_MODE_BRACKET_OPEN || _la==UDB_MODE_BRACKET_OPEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(414);
			arg_list();
			setState(415);
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
		enterRule(_localctx, 72, RULE_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(417);
			argument();
			setState(422);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAM_SEPARATOR) {
				{
				{
				setState(418);
				match(PARAM_SEPARATOR);
				setState(419);
				argument();
				}
				}
				setState(424);
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
		enterRule(_localctx, 74, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(425);
			match(PARAM_MODE_NAME);
			setState(426);
			match(PARAM_MODE_COLON);
			setState(427);
			match(PARAM_MODE_BRACKET_OPEN);
			setState(428);
			formula();
			setState(429);
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
		enterRule(_localctx, 76, RULE_formula);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(434);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(431);
					formulaElement();
					}
					} 
				}
				setState(436);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
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
		enterRule(_localctx, 78, RULE_formulaElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(437);
			_la = _input.LA(1);
			if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 32767L) != 0)) ) {
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
		"\u0004\u0001L\u01b8\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005"+
		"\u0002]\b\u0002\n\u0002\f\u0002`\t\u0002\u0001\u0002\u0004\u0002c\b\u0002"+
		"\u000b\u0002\f\u0002d\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0003\u0003l\b\u0003\u0001\u0003\u0001\u0003\u0005\u0003"+
		"p\b\u0003\n\u0003\f\u0003s\t\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u0004z\b\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0003\b\u008b\b\b\u0001\b\u0001\b\u0005\b\u008f\b\b\n\b\f\b\u0092\t"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u009a\b\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00af\b\u000e\u0001\u000e\u0001"+
		"\u000e\u0005\u000e\u00b3\b\u000e\n\u000e\f\u000e\u00b6\t\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00bd\b\u000f"+
		"\u0001\u000f\u0001\u000f\u0005\u000f\u00c1\b\u000f\n\u000f\f\u000f\u00c4"+
		"\t\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u00ca"+
		"\b\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0005\u0012\u00d3\b\u0012\n\u0012\f\u0012\u00d6\t\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014"+
		"\u00e3\b\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0004\u0015\u00ee\b\u0015"+
		"\u000b\u0015\f\u0015\u00ef\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u00f5\b\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u00f9\b\u0016\n\u0016"+
		"\f\u0016\u00fc\t\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0003\u0017\u0103\b\u0017\u0001\u0017\u0001\u0017\u0005\u0017"+
		"\u0107\b\u0017\n\u0017\f\u0017\u010a\t\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u0111\b\u0018\u0001\u0018\u0001"+
		"\u0018\u0005\u0018\u0115\b\u0018\n\u0018\f\u0018\u0118\t\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0005\u001a\u0123\b\u001a\n\u001a\f\u001a\u0126"+
		"\t\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0005"+
		"\u001b\u012d\b\u001b\n\u001b\f\u001b\u0130\t\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c"+
		"\u0139\b\u001c\n\u001c\f\u001c\u013c\t\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005"+
		"\u001c\u0146\b\u001c\n\u001c\f\u001c\u0149\t\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0003\u001c\u014e\b\u001c\u0001\u001d\u0003\u001d\u0151\b"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0156\b\u001d\u0001"+
		"\u001e\u0001\u001e\u0004\u001e\u015a\b\u001e\u000b\u001e\f\u001e\u015b"+
		"\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0003\u001f\u0164\b\u001f\u0001 \u0001 \u0003 \u0168\b \u0001 \u0001"+
		" \u0005 \u016c\b \n \f \u016f\t \u0001 \u0001 \u0003 \u0173\b \u0001 "+
		"\u0001 \u0001 \u0003 \u0178\b \u0001 \u0001 \u0005 \u017c\b \n \f \u017f"+
		"\t \u0001 \u0001 \u0001 \u0003 \u0184\b \u0003 \u0186\b \u0001!\u0001"+
		"!\u0001!\u0005!\u018b\b!\n!\f!\u018e\t!\u0001!\u0001!\u0001\"\u0003\""+
		"\u0193\b\"\u0001\"\u0001\"\u0003\"\u0197\b\"\u0001\"\u0003\"\u019a\b\""+
		"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001$\u0005"+
		"$\u01a5\b$\n$\f$\u01a8\t$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001"+
		"&\u0005&\u01b1\b&\n&\f&\u01b4\t&\u0001\'\u0001\'\u0001\'\u0000\u0000("+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.02468:<>@BDFHJLN\u0000\u0004\u0001\u000045\u0002"+
		"\u0000((33\u0002\u0000\'\'22\u0001\u0000>L\u01c6\u0000P\u0001\u0000\u0000"+
		"\u0000\u0002S\u0001\u0000\u0000\u0000\u0004U\u0001\u0000\u0000\u0000\u0006"+
		"h\u0001\u0000\u0000\u0000\by\u0001\u0000\u0000\u0000\n{\u0001\u0000\u0000"+
		"\u0000\f\u007f\u0001\u0000\u0000\u0000\u000e\u0083\u0001\u0000\u0000\u0000"+
		"\u0010\u0087\u0001\u0000\u0000\u0000\u0012\u0099\u0001\u0000\u0000\u0000"+
		"\u0014\u009b\u0001\u0000\u0000\u0000\u0016\u009f\u0001\u0000\u0000\u0000"+
		"\u0018\u00a3\u0001\u0000\u0000\u0000\u001a\u00a7\u0001\u0000\u0000\u0000"+
		"\u001c\u00ab\u0001\u0000\u0000\u0000\u001e\u00b9\u0001\u0000\u0000\u0000"+
		" \u00c9\u0001\u0000\u0000\u0000\"\u00cb\u0001\u0000\u0000\u0000$\u00cd"+
		"\u0001\u0000\u0000\u0000&\u00d9\u0001\u0000\u0000\u0000(\u00de\u0001\u0000"+
		"\u0000\u0000*\u00ed\u0001\u0000\u0000\u0000,\u00f1\u0001\u0000\u0000\u0000"+
		".\u00ff\u0001\u0000\u0000\u00000\u010d\u0001\u0000\u0000\u00002\u011b"+
		"\u0001\u0000\u0000\u00004\u011f\u0001\u0000\u0000\u00006\u0129\u0001\u0000"+
		"\u0000\u00008\u014d\u0001\u0000\u0000\u0000:\u0155\u0001\u0000\u0000\u0000"+
		"<\u0157\u0001\u0000\u0000\u0000>\u0163\u0001\u0000\u0000\u0000@\u0185"+
		"\u0001\u0000\u0000\u0000B\u0187\u0001\u0000\u0000\u0000D\u0192\u0001\u0000"+
		"\u0000\u0000F\u019d\u0001\u0000\u0000\u0000H\u01a1\u0001\u0000\u0000\u0000"+
		"J\u01a9\u0001\u0000\u0000\u0000L\u01b2\u0001\u0000\u0000\u0000N\u01b5"+
		"\u0001\u0000\u0000\u0000PQ\u0003\u0002\u0001\u0000QR\u0003\u0004\u0002"+
		"\u0000R\u0001\u0001\u0000\u0000\u0000ST\u0005\n\u0000\u0000T\u0003\u0001"+
		"\u0000\u0000\u0000UV\u0005\u000b\u0000\u0000VW\u0005\u0005\u0000\u0000"+
		"W^\u0005\u0003\u0000\u0000X]\u0003\u0006\u0003\u0000Y]\u0003\u0010\b\u0000"+
		"Z]\u0003\u001c\u000e\u0000[]\u0003\u001e\u000f\u0000\\X\u0001\u0000\u0000"+
		"\u0000\\Y\u0001\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000\\[\u0001"+
		"\u0000\u0000\u0000]`\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000\u0000"+
		"^_\u0001\u0000\u0000\u0000_b\u0001\u0000\u0000\u0000`^\u0001\u0000\u0000"+
		"\u0000ac\u0003$\u0012\u0000ba\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000"+
		"\u0000db\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ef\u0001\u0000"+
		"\u0000\u0000fg\u0005\u0004\u0000\u0000g\u0005\u0001\u0000\u0000\u0000"+
		"hi\u0005\f\u0000\u0000ik\u0005\u0003\u0000\u0000jl\u0003\b\u0004\u0000"+
		"kj\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000lq\u0001\u0000\u0000"+
		"\u0000mn\u0005\u0006\u0000\u0000np\u0003\b\u0004\u0000om\u0001\u0000\u0000"+
		"\u0000ps\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000qr\u0001\u0000"+
		"\u0000\u0000rt\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000tu\u0005"+
		"\u0004\u0000\u0000u\u0007\u0001\u0000\u0000\u0000vz\u0003\n\u0005\u0000"+
		"wz\u0003\f\u0006\u0000xz\u0003\u000e\u0007\u0000yv\u0001\u0000\u0000\u0000"+
		"yw\u0001\u0000\u0000\u0000yx\u0001\u0000\u0000\u0000z\t\u0001\u0000\u0000"+
		"\u0000{|\u0005\r\u0000\u0000|}\u0005\u0007\u0000\u0000}~\u0005\u0005\u0000"+
		"\u0000~\u000b\u0001\u0000\u0000\u0000\u007f\u0080\u0005\u000e\u0000\u0000"+
		"\u0080\u0081\u0005\u0007\u0000\u0000\u0081\u0082\u0005\u0005\u0000\u0000"+
		"\u0082\r\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u000f\u0000\u0000\u0084"+
		"\u0085\u0005\u0007\u0000\u0000\u0085\u0086\u0005\u0005\u0000\u0000\u0086"+
		"\u000f\u0001\u0000\u0000\u0000\u0087\u0088\u0005\u0010\u0000\u0000\u0088"+
		"\u008a\u0005\u0003\u0000\u0000\u0089\u008b\u0003\u0012\t\u0000\u008a\u0089"+
		"\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u0090"+
		"\u0001\u0000\u0000\u0000\u008c\u008d\u0005\u0006\u0000\u0000\u008d\u008f"+
		"\u0003\u0012\t\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008f\u0092\u0001"+
		"\u0000\u0000\u0000\u0090\u008e\u0001\u0000\u0000\u0000\u0090\u0091\u0001"+
		"\u0000\u0000\u0000\u0091\u0093\u0001\u0000\u0000\u0000\u0092\u0090\u0001"+
		"\u0000\u0000\u0000\u0093\u0094\u0005\u0004\u0000\u0000\u0094\u0011\u0001"+
		"\u0000\u0000\u0000\u0095\u009a\u0003\u0014\n\u0000\u0096\u009a\u0003\u0016"+
		"\u000b\u0000\u0097\u009a\u0003\u0018\f\u0000\u0098\u009a\u0003\u001a\r"+
		"\u0000\u0099\u0095\u0001\u0000\u0000\u0000\u0099\u0096\u0001\u0000\u0000"+
		"\u0000\u0099\u0097\u0001\u0000\u0000\u0000\u0099\u0098\u0001\u0000\u0000"+
		"\u0000\u009a\u0013\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u0011\u0000"+
		"\u0000\u009c\u009d\u0005\u0007\u0000\u0000\u009d\u009e\u0005\u0005\u0000"+
		"\u0000\u009e\u0015\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u0012\u0000"+
		"\u0000\u00a0\u00a1\u0005\u0007\u0000\u0000\u00a1\u00a2\u0005\u0005\u0000"+
		"\u0000\u00a2\u0017\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005\u0013\u0000"+
		"\u0000\u00a4\u00a5\u0005\u0007\u0000\u0000\u00a5\u00a6\u0005\u0005\u0000"+
		"\u0000\u00a6\u0019\u0001\u0000\u0000\u0000\u00a7\u00a8\u0005\u0014\u0000"+
		"\u0000\u00a8\u00a9\u0005\u0007\u0000\u0000\u00a9\u00aa\u0005\u0005\u0000"+
		"\u0000\u00aa\u001b\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005\u0015\u0000"+
		"\u0000\u00ac\u00ae\u0005\u0003\u0000\u0000\u00ad\u00af\u0003 \u0010\u0000"+
		"\u00ae\u00ad\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000"+
		"\u00af\u00b4\u0001\u0000\u0000\u0000\u00b0\u00b1\u0005\u0006\u0000\u0000"+
		"\u00b1\u00b3\u0003 \u0010\u0000\u00b2\u00b0\u0001\u0000\u0000\u0000\u00b3"+
		"\u00b6\u0001\u0000\u0000\u0000\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b4"+
		"\u00b5\u0001\u0000\u0000\u0000\u00b5\u00b7\u0001\u0000\u0000\u0000\u00b6"+
		"\u00b4\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005\u0004\u0000\u0000\u00b8"+
		"\u001d\u0001\u0000\u0000\u0000\u00b9\u00ba\u0005\u0016\u0000\u0000\u00ba"+
		"\u00bc\u0005\u0003\u0000\u0000\u00bb\u00bd\u0003\"\u0011\u0000\u00bc\u00bb"+
		"\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd\u00c2"+
		"\u0001\u0000\u0000\u0000\u00be\u00bf\u0005\u0006\u0000\u0000\u00bf\u00c1"+
		"\u0003\"\u0011\u0000\u00c0\u00be\u0001\u0000\u0000\u0000\u00c1\u00c4\u0001"+
		"\u0000\u0000\u0000\u00c2\u00c0\u0001\u0000\u0000\u0000\u00c2\u00c3\u0001"+
		"\u0000\u0000\u0000\u00c3\u00c5\u0001\u0000\u0000\u0000\u00c4\u00c2\u0001"+
		"\u0000\u0000\u0000\u00c5\u00c6\u0005\u0004\u0000\u0000\u00c6\u001f\u0001"+
		"\u0000\u0000\u0000\u00c7\u00ca\u0003\"\u0011\u0000\u00c8\u00ca\u0005\t"+
		"\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00c9\u00c8\u0001\u0000"+
		"\u0000\u0000\u00ca!\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005\b\u0000"+
		"\u0000\u00cc#\u0001\u0000\u0000\u0000\u00cd\u00ce\u0005\u001c\u0000\u0000"+
		"\u00ce\u00cf\u0005\u0005\u0000\u0000\u00cf\u00d0\u0005\u0003\u0000\u0000"+
		"\u00d0\u00d4\u0003&\u0013\u0000\u00d1\u00d3\u0003(\u0014\u0000\u00d2\u00d1"+
		"\u0001\u0000\u0000\u0000\u00d3\u00d6\u0001\u0000\u0000\u0000\u00d4\u00d2"+
		"\u0001\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000\u0000\u00d5\u00d7"+
		"\u0001\u0000\u0000\u0000\u00d6\u00d4\u0001\u0000\u0000\u0000\u00d7\u00d8"+
		"\u0005\u0004\u0000\u0000\u00d8%\u0001\u0000\u0000\u0000\u00d9\u00da\u0005"+
		"\u001d\u0000\u0000\u00da\u00db\u0005\u0003\u0000\u0000\u00db\u00dc\u0003"+
		"*\u0015\u0000\u00dc\u00dd\u0005\u0004\u0000\u0000\u00dd\'\u0001\u0000"+
		"\u0000\u0000\u00de\u00df\u0005\u001a\u0000\u0000\u00df\u00e2\u0005\u0005"+
		"\u0000\u0000\u00e0\u00e1\u0005\u001b\u0000\u0000\u00e1\u00e3\u0005\u0005"+
		"\u0000\u0000\u00e2\u00e0\u0001\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000"+
		"\u0000\u0000\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e5\u0005\u0003"+
		"\u0000\u0000\u00e5\u00e6\u0003*\u0015\u0000\u00e6\u00e7\u0005\u0004\u0000"+
		"\u0000\u00e7)\u0001\u0000\u0000\u0000\u00e8\u00ee\u0003,\u0016\u0000\u00e9"+
		"\u00ee\u0003.\u0017\u0000\u00ea\u00ee\u00030\u0018\u0000\u00eb\u00ee\u0003"+
		"4\u001a\u0000\u00ec\u00ee\u00036\u001b\u0000\u00ed\u00e8\u0001\u0000\u0000"+
		"\u0000\u00ed\u00e9\u0001\u0000\u0000\u0000\u00ed\u00ea\u0001\u0000\u0000"+
		"\u0000\u00ed\u00eb\u0001\u0000\u0000\u0000\u00ed\u00ec\u0001\u0000\u0000"+
		"\u0000\u00ee\u00ef\u0001\u0000\u0000\u0000\u00ef\u00ed\u0001\u0000\u0000"+
		"\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0+\u0001\u0000\u0000\u0000"+
		"\u00f1\u00f2\u0005\u0017\u0000\u0000\u00f2\u00f4\u0005\u0003\u0000\u0000"+
		"\u00f3\u00f5\u0003 \u0010\u0000\u00f4\u00f3\u0001\u0000\u0000\u0000\u00f4"+
		"\u00f5\u0001\u0000\u0000\u0000\u00f5\u00fa\u0001\u0000\u0000\u0000\u00f6"+
		"\u00f7\u0005\u0006\u0000\u0000\u00f7\u00f9\u0003 \u0010\u0000\u00f8\u00f6"+
		"\u0001\u0000\u0000\u0000\u00f9\u00fc\u0001\u0000\u0000\u0000\u00fa\u00f8"+
		"\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000\u0000\u00fb\u00fd"+
		"\u0001\u0000\u0000\u0000\u00fc\u00fa\u0001\u0000\u0000\u0000\u00fd\u00fe"+
		"\u0005\u0004\u0000\u0000\u00fe-\u0001\u0000\u0000\u0000\u00ff\u0100\u0005"+
		"\u0018\u0000\u0000\u0100\u0102\u0005\u0003\u0000\u0000\u0101\u0103\u0003"+
		"2\u0019\u0000\u0102\u0101\u0001\u0000\u0000\u0000\u0102\u0103\u0001\u0000"+
		"\u0000\u0000\u0103\u0108\u0001\u0000\u0000\u0000\u0104\u0105\u0005\u0006"+
		"\u0000\u0000\u0105\u0107\u00032\u0019\u0000\u0106\u0104\u0001\u0000\u0000"+
		"\u0000\u0107\u010a\u0001\u0000\u0000\u0000\u0108\u0106\u0001\u0000\u0000"+
		"\u0000\u0108\u0109\u0001\u0000\u0000\u0000\u0109\u010b\u0001\u0000\u0000"+
		"\u0000\u010a\u0108\u0001\u0000\u0000\u0000\u010b\u010c\u0005\u0004\u0000"+
		"\u0000\u010c/\u0001\u0000\u0000\u0000\u010d\u010e\u0005\u0019\u0000\u0000"+
		"\u010e\u0110\u0005\u0003\u0000\u0000\u010f\u0111\u00032\u0019\u0000\u0110"+
		"\u010f\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111"+
		"\u0116\u0001\u0000\u0000\u0000\u0112\u0113\u0005\u0006\u0000\u0000\u0113"+
		"\u0115\u00032\u0019\u0000\u0114\u0112\u0001\u0000\u0000\u0000\u0115\u0118"+
		"\u0001\u0000\u0000\u0000\u0116\u0114\u0001\u0000\u0000\u0000\u0116\u0117"+
		"\u0001\u0000\u0000\u0000\u0117\u0119\u0001\u0000\u0000\u0000\u0118\u0116"+
		"\u0001\u0000\u0000\u0000\u0119\u011a\u0005\u0004\u0000\u0000\u011a1\u0001"+
		"\u0000\u0000\u0000\u011b\u011c\u0005\u0005\u0000\u0000\u011c\u011d\u0005"+
		"\u0007\u0000\u0000\u011d\u011e\u0005\u0005\u0000\u0000\u011e3\u0001\u0000"+
		"\u0000\u0000\u011f\u0120\u0005\u001e\u0000\u0000\u0120\u0124\u0005\u0003"+
		"\u0000\u0000\u0121\u0123\u0003@ \u0000\u0122\u0121\u0001\u0000\u0000\u0000"+
		"\u0123\u0126\u0001\u0000\u0000\u0000\u0124\u0122\u0001\u0000\u0000\u0000"+
		"\u0124\u0125\u0001\u0000\u0000\u0000\u0125\u0127\u0001\u0000\u0000\u0000"+
		"\u0126\u0124\u0001\u0000\u0000\u0000\u0127\u0128\u0005\u0004\u0000\u0000"+
		"\u01285\u0001\u0000\u0000\u0000\u0129\u012a\u0005\u001f\u0000\u0000\u012a"+
		"\u012e\u0005\u0003\u0000\u0000\u012b\u012d\u00038\u001c\u0000\u012c\u012b"+
		"\u0001\u0000\u0000\u0000\u012d\u0130\u0001\u0000\u0000\u0000\u012e\u012c"+
		"\u0001\u0000\u0000\u0000\u012e\u012f\u0001\u0000\u0000\u0000\u012f\u0131"+
		"\u0001\u0000\u0000\u0000\u0130\u012e\u0001\u0000\u0000\u0000\u0131\u0132"+
		"\u0005\u0004\u0000\u0000\u01327\u0001\u0000\u0000\u0000\u0133\u0134\u0005"+
		" \u0000\u0000\u0134\u0135\u0003<\u001e\u0000\u0135\u0136\u0007\u0000\u0000"+
		"\u0000\u0136\u013a\u00056\u0000\u0000\u0137\u0139\u0003:\u001d\u0000\u0138"+
		"\u0137\u0001\u0000\u0000\u0000\u0139\u013c\u0001\u0000\u0000\u0000\u013a"+
		"\u0138\u0001\u0000\u0000\u0000\u013a\u013b\u0001\u0000\u0000\u0000\u013b"+
		"\u013d\u0001\u0000\u0000\u0000\u013c\u013a\u0001\u0000\u0000\u0000\u013d"+
		"\u013e\u0005\u0004\u0000\u0000\u013e\u014e\u0001\u0000\u0000\u0000\u013f"+
		"\u0140\u0005#\u0000\u0000\u0140\u0141\u0005 \u0000\u0000\u0141\u0142\u0003"+
		"<\u001e\u0000\u0142\u0143\u0007\u0000\u0000\u0000\u0143\u0147\u00056\u0000"+
		"\u0000\u0144\u0146\u0003:\u001d\u0000\u0145\u0144\u0001\u0000\u0000\u0000"+
		"\u0146\u0149\u0001\u0000\u0000\u0000\u0147\u0145\u0001\u0000\u0000\u0000"+
		"\u0147\u0148\u0001\u0000\u0000\u0000\u0148\u014a\u0001\u0000\u0000\u0000"+
		"\u0149\u0147\u0001\u0000\u0000\u0000\u014a\u014b\u0005#\u0000\u0000\u014b"+
		"\u014c\u0005\u0004\u0000\u0000\u014c\u014e\u0001\u0000\u0000\u0000\u014d"+
		"\u0133\u0001\u0000\u0000\u0000\u014d\u013f\u0001\u0000\u0000\u0000\u014e"+
		"9\u0001\u0000\u0000\u0000\u014f\u0151\u0005#\u0000\u0000\u0150\u014f\u0001"+
		"\u0000\u0000\u0000\u0150\u0151\u0001\u0000\u0000\u0000\u0151\u0152\u0001"+
		"\u0000\u0000\u0000\u0152\u0156\u0005\"\u0000\u0000\u0153\u0156\u0003D"+
		"\"\u0000\u0154\u0156\u0003@ \u0000\u0155\u0150\u0001\u0000\u0000\u0000"+
		"\u0155\u0153\u0001\u0000\u0000\u0000\u0155\u0154\u0001\u0000\u0000\u0000"+
		"\u0156;\u0001\u0000\u0000\u0000\u0157\u0159\u0005%\u0000\u0000\u0158\u015a"+
		"\u0003>\u001f\u0000\u0159\u0158\u0001\u0000\u0000\u0000\u015a\u015b\u0001"+
		"\u0000\u0000\u0000\u015b\u0159\u0001\u0000\u0000\u0000\u015b\u015c\u0001"+
		"\u0000\u0000\u0000\u015c\u015d\u0001\u0000\u0000\u0000\u015d\u015e\u0005"+
		"-\u0000\u0000\u015e=\u0001\u0000\u0000\u0000\u015f\u0160\u0005+\u0000"+
		"\u0000\u0160\u0161\u0005/\u0000\u0000\u0161\u0164\u00050\u0000\u0000\u0162"+
		"\u0164\u0005,\u0000\u0000\u0163\u015f\u0001\u0000\u0000\u0000\u0163\u0162"+
		"\u0001\u0000\u0000\u0000\u0164?\u0001\u0000\u0000\u0000\u0165\u0167\u0005"+
		"$\u0000\u0000\u0166\u0168\u0003F#\u0000\u0167\u0166\u0001\u0000\u0000"+
		"\u0000\u0167\u0168\u0001\u0000\u0000\u0000\u0168\u0169\u0001\u0000\u0000"+
		"\u0000\u0169\u016d\u0005)\u0000\u0000\u016a\u016c\u0003:\u001d\u0000\u016b"+
		"\u016a\u0001\u0000\u0000\u0000\u016c\u016f\u0001\u0000\u0000\u0000\u016d"+
		"\u016b\u0001\u0000\u0000\u0000\u016d\u016e\u0001\u0000\u0000\u0000\u016e"+
		"\u0170\u0001\u0000\u0000\u0000\u016f\u016d\u0001\u0000\u0000\u0000\u0170"+
		"\u0172\u0005\u0004\u0000\u0000\u0171\u0173\u0003B!\u0000\u0172\u0171\u0001"+
		"\u0000\u0000\u0000\u0172\u0173\u0001\u0000\u0000\u0000\u0173\u0186\u0001"+
		"\u0000\u0000\u0000\u0174\u0175\u0005#\u0000\u0000\u0175\u0177\u0005$\u0000"+
		"\u0000\u0176\u0178\u0003F#\u0000\u0177\u0176\u0001\u0000\u0000\u0000\u0177"+
		"\u0178\u0001\u0000\u0000\u0000\u0178\u0179\u0001\u0000\u0000\u0000\u0179"+
		"\u017d\u0005)\u0000\u0000\u017a\u017c\u0003:\u001d\u0000\u017b\u017a\u0001"+
		"\u0000\u0000\u0000\u017c\u017f\u0001\u0000\u0000\u0000\u017d\u017b\u0001"+
		"\u0000\u0000\u0000\u017d\u017e\u0001\u0000\u0000\u0000\u017e\u0180\u0001"+
		"\u0000\u0000\u0000\u017f\u017d\u0001\u0000\u0000\u0000\u0180\u0181\u0005"+
		"#\u0000\u0000\u0181\u0183\u0005\u0004\u0000\u0000\u0182\u0184\u0003B!"+
		"\u0000\u0183\u0182\u0001\u0000\u0000\u0000\u0183\u0184\u0001\u0000\u0000"+
		"\u0000\u0184\u0186\u0001\u0000\u0000\u0000\u0185\u0165\u0001\u0000\u0000"+
		"\u0000\u0185\u0174\u0001\u0000\u0000\u0000\u0186A\u0001\u0000\u0000\u0000"+
		"\u0187\u0188\u0005!\u0000\u0000\u0188\u018c\u0005\u0003\u0000\u0000\u0189"+
		"\u018b\u0003:\u001d\u0000\u018a\u0189\u0001\u0000\u0000\u0000\u018b\u018e"+
		"\u0001\u0000\u0000\u0000\u018c\u018a\u0001\u0000\u0000\u0000\u018c\u018d"+
		"\u0001\u0000\u0000\u0000\u018d\u018f\u0001\u0000\u0000\u0000\u018e\u018c"+
		"\u0001\u0000\u0000\u0000\u018f\u0190\u0005\u0004\u0000\u0000\u0190C\u0001"+
		"\u0000\u0000\u0000\u0191\u0193\u0005#\u0000\u0000\u0192\u0191\u0001\u0000"+
		"\u0000\u0000\u0192\u0193\u0001\u0000\u0000\u0000\u0193\u0196\u0001\u0000"+
		"\u0000\u0000\u0194\u0197\u0005$\u0000\u0000\u0195\u0197\u0003<\u001e\u0000"+
		"\u0196\u0194\u0001\u0000\u0000\u0000\u0196\u0195\u0001\u0000\u0000\u0000"+
		"\u0197\u0199\u0001\u0000\u0000\u0000\u0198\u019a\u0003F#\u0000\u0199\u0198"+
		"\u0001\u0000\u0000\u0000\u0199\u019a\u0001\u0000\u0000\u0000\u019a\u019b"+
		"\u0001\u0000\u0000\u0000\u019b\u019c\u0007\u0001\u0000\u0000\u019cE\u0001"+
		"\u0000\u0000\u0000\u019d\u019e\u0007\u0002\u0000\u0000\u019e\u019f\u0003"+
		"H$\u0000\u019f\u01a0\u00059\u0000\u0000\u01a0G\u0001\u0000\u0000\u0000"+
		"\u01a1\u01a6\u0003J%\u0000\u01a2\u01a3\u0005<\u0000\u0000\u01a3\u01a5"+
		"\u0003J%\u0000\u01a4\u01a2\u0001\u0000\u0000\u0000\u01a5\u01a8\u0001\u0000"+
		"\u0000\u0000\u01a6\u01a4\u0001\u0000\u0000\u0000\u01a6\u01a7\u0001\u0000"+
		"\u0000\u0000\u01a7I\u0001\u0000\u0000\u0000\u01a8\u01a6\u0001\u0000\u0000"+
		"\u0000\u01a9\u01aa\u0005:\u0000\u0000\u01aa\u01ab\u0005;\u0000\u0000\u01ab"+
		"\u01ac\u00058\u0000\u0000\u01ac\u01ad\u0003L&\u0000\u01ad\u01ae\u0005"+
		">\u0000\u0000\u01aeK\u0001\u0000\u0000\u0000\u01af\u01b1\u0003N\'\u0000"+
		"\u01b0\u01af\u0001\u0000\u0000\u0000\u01b1\u01b4\u0001\u0000\u0000\u0000"+
		"\u01b2\u01b0\u0001\u0000\u0000\u0000\u01b2\u01b3\u0001\u0000\u0000\u0000"+
		"\u01b3M\u0001\u0000\u0000\u0000\u01b4\u01b2\u0001\u0000\u0000\u0000\u01b5"+
		"\u01b6\u0007\u0003\u0000\u0000\u01b6O\u0001\u0000\u0000\u0000.\\^dkqy"+
		"\u008a\u0090\u0099\u00ae\u00b4\u00bc\u00c2\u00c9\u00d4\u00e2\u00ed\u00ef"+
		"\u00f4\u00fa\u0102\u0108\u0110\u0116\u0124\u012e\u013a\u0147\u014d\u0150"+
		"\u0155\u015b\u0163\u0167\u016d\u0172\u0177\u017d\u0183\u0185\u018c\u0192"+
		"\u0196\u0199\u01a6\u01b2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}