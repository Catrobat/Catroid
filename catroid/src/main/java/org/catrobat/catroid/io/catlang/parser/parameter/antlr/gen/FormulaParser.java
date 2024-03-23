// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/FormulaParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.FormulaParserListener;
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.FormulaParserVisitor;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class FormulaParser extends Parser {
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
		OPERATOR_LOGIC_LOWER_EQUAL=21, OPERATOR_LOGIC_GREATER_EQUAL=22, SENSOR_OR_PROPERTY_OR_METHOD=23;
	public static final int
		RULE_formula = 0, RULE_expression = 1, RULE_additiveExpression = 2, RULE_multiplicativeExpression = 3, 
		RULE_comparisonExpression = 4, RULE_additiveOperator = 5, RULE_multiplicativeOperator = 6, 
		RULE_comparisonOperator = 7, RULE_simpleExpression = 8, RULE_sensorPropertyOrMethodInvocation = 9, 
		RULE_methodParameters = 10, RULE_parameterList = 11, RULE_unaryExpression = 12, 
		RULE_literal = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"formula", "expression", "additiveExpression", "multiplicativeExpression", 
			"comparisonExpression", "additiveOperator", "multiplicativeOperator", 
			"comparisonOperator", "simpleExpression", "sensorPropertyOrMethodInvocation", 
			"methodParameters", "parameterList", "unaryExpression", "literal"
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
			"OPERATOR_LOGIC_GREATER_EQUAL", "SENSOR_OR_PROPERTY_OR_METHOD"
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
	public String getGrammarFileName() { return "FormulaParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public FormulaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormulaContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(FormulaParser.EOF, 0); }
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener) ((FormulaParserListener)listener).enterFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitFormula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor) return ((FormulaParserVisitor<? extends T>)visitor).visitFormula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		FormulaContext _localctx = new FormulaContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			expression();
			setState(29);
			match(EOF);
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
	public static class ExpressionContext extends ParserRuleContext {
		public AdditiveExpressionContext additiveExpression() {
			return getRuleContext(AdditiveExpressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			additiveExpression();
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
	public static class AdditiveExpressionContext extends ParserRuleContext {
		public List<MultiplicativeExpressionContext> multiplicativeExpression() {
			return getRuleContexts(MultiplicativeExpressionContext.class);
		}
		public MultiplicativeExpressionContext multiplicativeExpression(int i) {
			return getRuleContext(MultiplicativeExpressionContext.class,i);
		}
		public List<AdditiveOperatorContext> additiveOperator() {
			return getRuleContexts(AdditiveOperatorContext.class);
		}
		public AdditiveOperatorContext additiveOperator(int i) {
			return getRuleContext(AdditiveOperatorContext.class,i);
		}
		public AdditiveExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitAdditiveExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitAdditiveExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
		AdditiveExpressionContext _localctx = new AdditiveExpressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_additiveExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			multiplicativeExpression();
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 35840L) != 0)) {
				{
				{
				setState(34);
				additiveOperator();
				setState(35);
				multiplicativeExpression();
				}
				}
				setState(41);
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
	public static class MultiplicativeExpressionContext extends ParserRuleContext {
		public List<ComparisonExpressionContext> comparisonExpression() {
			return getRuleContexts(ComparisonExpressionContext.class);
		}
		public ComparisonExpressionContext comparisonExpression(int i) {
			return getRuleContext(ComparisonExpressionContext.class,i);
		}
		public List<MultiplicativeOperatorContext> multiplicativeOperator() {
			return getRuleContexts(MultiplicativeOperatorContext.class);
		}
		public MultiplicativeOperatorContext multiplicativeOperator(int i) {
			return getRuleContext(MultiplicativeOperatorContext.class,i);
		}
		public MultiplicativeExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitMultiplicativeExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitMultiplicativeExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
		MultiplicativeExpressionContext _localctx = new MultiplicativeExpressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_multiplicativeExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			comparisonExpression();
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 28672L) != 0)) {
				{
				{
				setState(43);
				multiplicativeOperator();
				setState(44);
				comparisonExpression();
				}
				}
				setState(50);
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
	public static class ComparisonExpressionContext extends ParserRuleContext {
		public List<SimpleExpressionContext> simpleExpression() {
			return getRuleContexts(SimpleExpressionContext.class);
		}
		public SimpleExpressionContext simpleExpression(int i) {
			return getRuleContext(SimpleExpressionContext.class,i);
		}
		public List<ComparisonOperatorContext> comparisonOperator() {
			return getRuleContexts(ComparisonOperatorContext.class);
		}
		public ComparisonOperatorContext comparisonOperator(int i) {
			return getRuleContext(ComparisonOperatorContext.class,i);
		}
		public ComparisonExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterComparisonExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitComparisonExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitComparisonExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonExpressionContext comparisonExpression() throws RecognitionException {
		ComparisonExpressionContext _localctx = new ComparisonExpressionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_comparisonExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			simpleExpression();
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8257536L) != 0)) {
				{
				{
				setState(52);
				comparisonOperator();
				setState(53);
				simpleExpression();
				}
				}
				setState(59);
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
	public static class AdditiveOperatorContext extends ParserRuleContext {
		public TerminalNode OPERATOR_NUMERIC_ADD() { return getToken(FormulaParser.OPERATOR_NUMERIC_ADD, 0); }
		public TerminalNode OPERATOR_NUMERIC_MINUS() { return getToken(FormulaParser.OPERATOR_NUMERIC_MINUS, 0); }
		public TerminalNode OPERATOR_LOGIC_OR() { return getToken(FormulaParser.OPERATOR_LOGIC_OR, 0); }
		public AdditiveOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterAdditiveOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitAdditiveOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitAdditiveOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveOperatorContext additiveOperator() throws RecognitionException {
		AdditiveOperatorContext _localctx = new AdditiveOperatorContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_additiveOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 35840L) != 0)) ) {
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
	public static class MultiplicativeOperatorContext extends ParserRuleContext {
		public TerminalNode OPERATOR_NUMERIC_MULTIPLY() { return getToken(FormulaParser.OPERATOR_NUMERIC_MULTIPLY, 0); }
		public TerminalNode OPERATOR_NUMERIC_DIVIDE() { return getToken(FormulaParser.OPERATOR_NUMERIC_DIVIDE, 0); }
		public TerminalNode OPERATOR_LOGIC_AND() { return getToken(FormulaParser.OPERATOR_LOGIC_AND, 0); }
		public MultiplicativeOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterMultiplicativeOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitMultiplicativeOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitMultiplicativeOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplicativeOperatorContext multiplicativeOperator() throws RecognitionException {
		MultiplicativeOperatorContext _localctx = new MultiplicativeOperatorContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_multiplicativeOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 28672L) != 0)) ) {
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
	public static class ComparisonOperatorContext extends ParserRuleContext {
		public TerminalNode OPERATOR_LOGIC_EQUAL() { return getToken(FormulaParser.OPERATOR_LOGIC_EQUAL, 0); }
		public TerminalNode OPERATOR_LOGIC_NOT_EQUAL() { return getToken(FormulaParser.OPERATOR_LOGIC_NOT_EQUAL, 0); }
		public TerminalNode OPERATOR_LOGIC_LOWER() { return getToken(FormulaParser.OPERATOR_LOGIC_LOWER, 0); }
		public TerminalNode OPERATOR_LOGIC_GREATER() { return getToken(FormulaParser.OPERATOR_LOGIC_GREATER, 0); }
		public TerminalNode OPERATOR_LOGIC_LOWER_EQUAL() { return getToken(FormulaParser.OPERATOR_LOGIC_LOWER_EQUAL, 0); }
		public TerminalNode OPERATOR_LOGIC_GREATER_EQUAL() { return getToken(FormulaParser.OPERATOR_LOGIC_GREATER_EQUAL, 0); }
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitComparisonOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8257536L) != 0)) ) {
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
	public static class SimpleExpressionContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public SensorPropertyOrMethodInvocationContext sensorPropertyOrMethodInvocation() {
			return getRuleContext(SensorPropertyOrMethodInvocationContext.class,0);
		}
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public TerminalNode BRACE_OPEN() { return getToken(FormulaParser.BRACE_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BRACE_CLOSE() { return getToken(FormulaParser.BRACE_CLOSE, 0); }
		public SimpleExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterSimpleExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitSimpleExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitSimpleExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleExpressionContext simpleExpression() throws RecognitionException {
		SimpleExpressionContext _localctx = new SimpleExpressionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_simpleExpression);
		try {
			setState(73);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
			case VARIABLE:
			case UDB_PARAMETER:
			case LIST:
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(66);
				literal();
				}
				break;
			case SENSOR_OR_PROPERTY_OR_METHOD:
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
				sensorPropertyOrMethodInvocation();
				}
				break;
			case OPERATOR_NUMERIC_ADD:
			case OPERATOR_NUMERIC_MINUS:
			case OPERATOR_LOGIC_NOT:
				enterOuterAlt(_localctx, 3);
				{
				setState(68);
				unaryExpression();
				}
				break;
			case BRACE_OPEN:
				enterOuterAlt(_localctx, 4);
				{
				setState(69);
				match(BRACE_OPEN);
				setState(70);
				expression();
				setState(71);
				match(BRACE_CLOSE);
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
	public static class SensorPropertyOrMethodInvocationContext extends ParserRuleContext {
		public TerminalNode SENSOR_OR_PROPERTY_OR_METHOD() { return getToken(FormulaParser.SENSOR_OR_PROPERTY_OR_METHOD, 0); }
		public MethodParametersContext methodParameters() {
			return getRuleContext(MethodParametersContext.class,0);
		}
		public SensorPropertyOrMethodInvocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sensorPropertyOrMethodInvocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterSensorPropertyOrMethodInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitSensorPropertyOrMethodInvocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitSensorPropertyOrMethodInvocation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SensorPropertyOrMethodInvocationContext sensorPropertyOrMethodInvocation() throws RecognitionException {
		SensorPropertyOrMethodInvocationContext _localctx = new SensorPropertyOrMethodInvocationContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_sensorPropertyOrMethodInvocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(SENSOR_OR_PROPERTY_OR_METHOD);
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BRACE_OPEN) {
				{
				setState(76);
				methodParameters();
				}
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
	public static class MethodParametersContext extends ParserRuleContext {
		public TerminalNode BRACE_OPEN() { return getToken(FormulaParser.BRACE_OPEN, 0); }
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode BRACE_CLOSE() { return getToken(FormulaParser.BRACE_CLOSE, 0); }
		public MethodParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterMethodParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitMethodParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitMethodParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodParametersContext methodParameters() throws RecognitionException {
		MethodParametersContext _localctx = new MethodParametersContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_methodParameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(BRACE_OPEN);
			setState(80);
			parameterList();
			setState(81);
			match(BRACE_CLOSE);
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
	public static class ParameterListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(FormulaParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(FormulaParser.COMMA, i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitParameterList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_parameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			expression();
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(84);
				match(COMMA);
				setState(85);
				expression();
				}
				}
				setState(90);
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
	public static class UnaryExpressionContext extends ParserRuleContext {
		public SimpleExpressionContext simpleExpression() {
			return getRuleContext(SimpleExpressionContext.class,0);
		}
		public TerminalNode OPERATOR_NUMERIC_ADD() { return getToken(FormulaParser.OPERATOR_NUMERIC_ADD, 0); }
		public TerminalNode OPERATOR_NUMERIC_MINUS() { return getToken(FormulaParser.OPERATOR_NUMERIC_MINUS, 0); }
		public TerminalNode OPERATOR_LOGIC_NOT() { return getToken(FormulaParser.OPERATOR_LOGIC_NOT, 0); }
		public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitUnaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitUnaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_unaryExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 68608L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(92);
			simpleExpression();
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
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(FormulaParser.NUMBER, 0); }
		public TerminalNode VARIABLE() { return getToken(FormulaParser.VARIABLE, 0); }
		public TerminalNode LIST() { return getToken(FormulaParser.LIST, 0); }
		public TerminalNode UDB_PARAMETER() { return getToken(FormulaParser.UDB_PARAMETER, 0); }
		public TerminalNode STRING() { return getToken(FormulaParser.STRING, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FormulaParserListener ) ((FormulaParserListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FormulaParserVisitor ) return ((FormulaParserVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 964L) != 0)) ) {
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
		"\u0004\u0001\u0017a\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002"+
		"&\b\u0002\n\u0002\f\u0002)\t\u0002\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0005\u0003/\b\u0003\n\u0003\f\u00032\t\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u00048\b\u0004\n\u0004\f\u0004"+
		";\t\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003"+
		"\bJ\b\b\u0001\t\u0001\t\u0003\tN\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0005\u000bW\b\u000b\n\u000b\f\u000bZ\t"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0000\u0000\u000e"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u0000\u0005\u0002\u0000\n\u000b\u000f\u000f\u0001\u0000\f\u000e\u0001"+
		"\u0000\u0011\u0016\u0002\u0000\n\u000b\u0010\u0010\u0002\u0000\u0002\u0002"+
		"\u0006\tZ\u0000\u001c\u0001\u0000\u0000\u0000\u0002\u001f\u0001\u0000"+
		"\u0000\u0000\u0004!\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000\u0000"+
		"\b3\u0001\u0000\u0000\u0000\n<\u0001\u0000\u0000\u0000\f>\u0001\u0000"+
		"\u0000\u0000\u000e@\u0001\u0000\u0000\u0000\u0010I\u0001\u0000\u0000\u0000"+
		"\u0012K\u0001\u0000\u0000\u0000\u0014O\u0001\u0000\u0000\u0000\u0016S"+
		"\u0001\u0000\u0000\u0000\u0018[\u0001\u0000\u0000\u0000\u001a^\u0001\u0000"+
		"\u0000\u0000\u001c\u001d\u0003\u0002\u0001\u0000\u001d\u001e\u0005\u0000"+
		"\u0000\u0001\u001e\u0001\u0001\u0000\u0000\u0000\u001f \u0003\u0004\u0002"+
		"\u0000 \u0003\u0001\u0000\u0000\u0000!\'\u0003\u0006\u0003\u0000\"#\u0003"+
		"\n\u0005\u0000#$\u0003\u0006\u0003\u0000$&\u0001\u0000\u0000\u0000%\""+
		"\u0001\u0000\u0000\u0000&)\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000"+
		"\u0000\'(\u0001\u0000\u0000\u0000(\u0005\u0001\u0000\u0000\u0000)\'\u0001"+
		"\u0000\u0000\u0000*0\u0003\b\u0004\u0000+,\u0003\f\u0006\u0000,-\u0003"+
		"\b\u0004\u0000-/\u0001\u0000\u0000\u0000.+\u0001\u0000\u0000\u0000/2\u0001"+
		"\u0000\u0000\u00000.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u0000"+
		"1\u0007\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u000039\u0003\u0010"+
		"\b\u000045\u0003\u000e\u0007\u000056\u0003\u0010\b\u000068\u0001\u0000"+
		"\u0000\u000074\u0001\u0000\u0000\u00008;\u0001\u0000\u0000\u000097\u0001"+
		"\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:\t\u0001\u0000\u0000\u0000"+
		";9\u0001\u0000\u0000\u0000<=\u0007\u0000\u0000\u0000=\u000b\u0001\u0000"+
		"\u0000\u0000>?\u0007\u0001\u0000\u0000?\r\u0001\u0000\u0000\u0000@A\u0007"+
		"\u0002\u0000\u0000A\u000f\u0001\u0000\u0000\u0000BJ\u0003\u001a\r\u0000"+
		"CJ\u0003\u0012\t\u0000DJ\u0003\u0018\f\u0000EF\u0005\u0003\u0000\u0000"+
		"FG\u0003\u0002\u0001\u0000GH\u0005\u0004\u0000\u0000HJ\u0001\u0000\u0000"+
		"\u0000IB\u0001\u0000\u0000\u0000IC\u0001\u0000\u0000\u0000ID\u0001\u0000"+
		"\u0000\u0000IE\u0001\u0000\u0000\u0000J\u0011\u0001\u0000\u0000\u0000"+
		"KM\u0005\u0017\u0000\u0000LN\u0003\u0014\n\u0000ML\u0001\u0000\u0000\u0000"+
		"MN\u0001\u0000\u0000\u0000N\u0013\u0001\u0000\u0000\u0000OP\u0005\u0003"+
		"\u0000\u0000PQ\u0003\u0016\u000b\u0000QR\u0005\u0004\u0000\u0000R\u0015"+
		"\u0001\u0000\u0000\u0000SX\u0003\u0002\u0001\u0000TU\u0005\u0005\u0000"+
		"\u0000UW\u0003\u0002\u0001\u0000VT\u0001\u0000\u0000\u0000WZ\u0001\u0000"+
		"\u0000\u0000XV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000Y\u0017"+
		"\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000[\\\u0007\u0003\u0000"+
		"\u0000\\]\u0003\u0010\b\u0000]\u0019\u0001\u0000\u0000\u0000^_\u0007\u0004"+
		"\u0000\u0000_\u001b\u0001\u0000\u0000\u0000\u0006\'09IMX";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}