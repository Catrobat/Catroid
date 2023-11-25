// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/CatrobatParameterParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class CatrobatParameterParser extends Parser {
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
		RULE_argument = 0, RULE_expression = 1, RULE_additiveExpression = 2, RULE_multiplicativeExpression = 3, 
		RULE_additiveOperator = 4, RULE_multiplicativeOperator = 5, RULE_simpleExpression = 6, 
		RULE_sensorPropertyOrMethodInvocation = 7, RULE_methodParameters = 8, 
		RULE_parameterList = 9, RULE_unaryExpression = 10, RULE_literal = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"argument", "expression", "additiveExpression", "multiplicativeExpression", 
			"additiveOperator", "multiplicativeOperator", "simpleExpression", "sensorPropertyOrMethodInvocation", 
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
	public String getGrammarFileName() { return "CatrobatParameterParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CatrobatParameterParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(CatrobatParameterParser.EOF, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener) ((CatrobatParameterParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			expression(0);
			setState(25);
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
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OPERATOR_LOGIC_EQUAL() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_EQUAL, 0); }
		public TerminalNode OPERATOR_LOGIC_NOT_EQUAL() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_NOT_EQUAL, 0); }
		public TerminalNode OPERATOR_LOGIC_LOWER() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_LOWER, 0); }
		public TerminalNode OPERATOR_LOGIC_GREATER() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_GREATER, 0); }
		public TerminalNode OPERATOR_LOGIC_LOWER_EQUAL() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_LOWER_EQUAL, 0); }
		public TerminalNode OPERATOR_LOGIC_GREATER_EQUAL() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_GREATER_EQUAL, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(28);
			additiveExpression();
			}
			_ctx.stop = _input.LT(-1);
			setState(50);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(48);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(30);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(31);
						match(OPERATOR_LOGIC_EQUAL);
						setState(32);
						expression(7);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(33);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(34);
						match(OPERATOR_LOGIC_NOT_EQUAL);
						setState(35);
						expression(6);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(36);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(37);
						match(OPERATOR_LOGIC_LOWER);
						setState(38);
						expression(5);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(39);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(40);
						match(OPERATOR_LOGIC_GREATER);
						setState(41);
						expression(4);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(42);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(43);
						match(OPERATOR_LOGIC_LOWER_EQUAL);
						setState(44);
						expression(3);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(45);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(46);
						match(OPERATOR_LOGIC_GREATER_EQUAL);
						setState(47);
						expression(2);
						}
						break;
					}
					} 
				}
				setState(52);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
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
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitAdditiveExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitAdditiveExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
		AdditiveExpressionContext _localctx = new AdditiveExpressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_additiveExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			multiplicativeExpression();
			setState(59);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(54);
					additiveOperator();
					setState(55);
					multiplicativeExpression();
					}
					} 
				}
				setState(61);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
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
		public List<SimpleExpressionContext> simpleExpression() {
			return getRuleContexts(SimpleExpressionContext.class);
		}
		public SimpleExpressionContext simpleExpression(int i) {
			return getRuleContext(SimpleExpressionContext.class,i);
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
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitMultiplicativeExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitMultiplicativeExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
		MultiplicativeExpressionContext _localctx = new MultiplicativeExpressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_multiplicativeExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			simpleExpression();
			setState(68);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(63);
					multiplicativeOperator();
					setState(64);
					simpleExpression();
					}
					} 
				}
				setState(70);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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
		public TerminalNode OPERATOR_NUMERIC_ADD() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_ADD, 0); }
		public TerminalNode OPERATOR_NUMERIC_MINUS() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_MINUS, 0); }
		public TerminalNode OPERATOR_LOGIC_OR() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_OR, 0); }
		public AdditiveOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterAdditiveOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitAdditiveOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitAdditiveOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveOperatorContext additiveOperator() throws RecognitionException {
		AdditiveOperatorContext _localctx = new AdditiveOperatorContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_additiveOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
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
		public TerminalNode OPERATOR_NUMERIC_MULTIPLY() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_MULTIPLY, 0); }
		public TerminalNode OPERATOR_NUMERIC_DIVIDE() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_DIVIDE, 0); }
		public TerminalNode OPERATOR_LOGIC_AND() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_AND, 0); }
		public MultiplicativeOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterMultiplicativeOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitMultiplicativeOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitMultiplicativeOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplicativeOperatorContext multiplicativeOperator() throws RecognitionException {
		MultiplicativeOperatorContext _localctx = new MultiplicativeOperatorContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_multiplicativeOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
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
		public TerminalNode BRACE_OPEN() { return getToken(CatrobatParameterParser.BRACE_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BRACE_CLOSE() { return getToken(CatrobatParameterParser.BRACE_CLOSE, 0); }
		public SimpleExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterSimpleExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitSimpleExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitSimpleExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleExpressionContext simpleExpression() throws RecognitionException {
		SimpleExpressionContext _localctx = new SimpleExpressionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_simpleExpression);
		try {
			setState(82);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
			case VARIABLE:
			case UDB_PARAMETER:
			case LIST:
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(75);
				literal();
				}
				break;
			case SENSOR_OR_PROPERTY_OR_METHOD:
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				sensorPropertyOrMethodInvocation();
				}
				break;
			case OPERATOR_NUMERIC_ADD:
			case OPERATOR_NUMERIC_MINUS:
			case OPERATOR_LOGIC_NOT:
				enterOuterAlt(_localctx, 3);
				{
				setState(77);
				unaryExpression();
				}
				break;
			case BRACE_OPEN:
				enterOuterAlt(_localctx, 4);
				{
				setState(78);
				match(BRACE_OPEN);
				setState(79);
				expression(0);
				setState(80);
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
		public TerminalNode SENSOR_OR_PROPERTY_OR_METHOD() { return getToken(CatrobatParameterParser.SENSOR_OR_PROPERTY_OR_METHOD, 0); }
		public MethodParametersContext methodParameters() {
			return getRuleContext(MethodParametersContext.class,0);
		}
		public SensorPropertyOrMethodInvocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sensorPropertyOrMethodInvocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterSensorPropertyOrMethodInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitSensorPropertyOrMethodInvocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitSensorPropertyOrMethodInvocation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SensorPropertyOrMethodInvocationContext sensorPropertyOrMethodInvocation() throws RecognitionException {
		SensorPropertyOrMethodInvocationContext _localctx = new SensorPropertyOrMethodInvocationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_sensorPropertyOrMethodInvocation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(SENSOR_OR_PROPERTY_OR_METHOD);
			setState(86);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(85);
				methodParameters();
				}
				break;
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
		public TerminalNode BRACE_OPEN() { return getToken(CatrobatParameterParser.BRACE_OPEN, 0); }
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode BRACE_CLOSE() { return getToken(CatrobatParameterParser.BRACE_CLOSE, 0); }
		public MethodParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterMethodParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitMethodParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitMethodParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodParametersContext methodParameters() throws RecognitionException {
		MethodParametersContext _localctx = new MethodParametersContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_methodParameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(BRACE_OPEN);
			setState(89);
			parameterList();
			setState(90);
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
		public List<TerminalNode> COMMA() { return getTokens(CatrobatParameterParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CatrobatParameterParser.COMMA, i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitParameterList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_parameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			expression(0);
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(93);
				match(COMMA);
				setState(94);
				expression(0);
				}
				}
				setState(99);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode OPERATOR_NUMERIC_ADD() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_ADD, 0); }
		public TerminalNode OPERATOR_NUMERIC_MINUS() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_MINUS, 0); }
		public TerminalNode OPERATOR_LOGIC_NOT() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_NOT, 0); }
		public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitUnaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitUnaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_unaryExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 68608L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(101);
			expression(0);
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
		public TerminalNode NUMBER() { return getToken(CatrobatParameterParser.NUMBER, 0); }
		public TerminalNode VARIABLE() { return getToken(CatrobatParameterParser.VARIABLE, 0); }
		public TerminalNode LIST() { return getToken(CatrobatParameterParser.LIST, 0); }
		public TerminalNode UDB_PARAMETER() { return getToken(CatrobatParameterParser.UDB_PARAMETER, 0); }
		public TerminalNode STRING() { return getToken(CatrobatParameterParser.STRING, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		case 4:
			return precpred(_ctx, 2);
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0017j\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005"+
		"\u00011\b\u0001\n\u0001\f\u00014\t\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002:\b\u0002\n\u0002\f\u0002=\t\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003C\b\u0003\n\u0003"+
		"\f\u0003F\t\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0003\u0006S\b\u0006\u0001\u0007\u0001\u0007\u0003\u0007W\b\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t`\b\t"+
		"\n\t\f\tc\t\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0000\u0001\u0002\f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0016\u0000\u0004\u0002\u0000\n\u000b\u000f\u000f\u0001\u0000\f\u000e"+
		"\u0002\u0000\n\u000b\u0010\u0010\u0002\u0000\u0002\u0002\u0006\tj\u0000"+
		"\u0018\u0001\u0000\u0000\u0000\u0002\u001b\u0001\u0000\u0000\u0000\u0004"+
		"5\u0001\u0000\u0000\u0000\u0006>\u0001\u0000\u0000\u0000\bG\u0001\u0000"+
		"\u0000\u0000\nI\u0001\u0000\u0000\u0000\fR\u0001\u0000\u0000\u0000\u000e"+
		"T\u0001\u0000\u0000\u0000\u0010X\u0001\u0000\u0000\u0000\u0012\\\u0001"+
		"\u0000\u0000\u0000\u0014d\u0001\u0000\u0000\u0000\u0016g\u0001\u0000\u0000"+
		"\u0000\u0018\u0019\u0003\u0002\u0001\u0000\u0019\u001a\u0005\u0000\u0000"+
		"\u0001\u001a\u0001\u0001\u0000\u0000\u0000\u001b\u001c\u0006\u0001\uffff"+
		"\uffff\u0000\u001c\u001d\u0003\u0004\u0002\u0000\u001d2\u0001\u0000\u0000"+
		"\u0000\u001e\u001f\n\u0006\u0000\u0000\u001f \u0005\u0011\u0000\u0000"+
		" 1\u0003\u0002\u0001\u0007!\"\n\u0005\u0000\u0000\"#\u0005\u0012\u0000"+
		"\u0000#1\u0003\u0002\u0001\u0006$%\n\u0004\u0000\u0000%&\u0005\u0013\u0000"+
		"\u0000&1\u0003\u0002\u0001\u0005\'(\n\u0003\u0000\u0000()\u0005\u0014"+
		"\u0000\u0000)1\u0003\u0002\u0001\u0004*+\n\u0002\u0000\u0000+,\u0005\u0015"+
		"\u0000\u0000,1\u0003\u0002\u0001\u0003-.\n\u0001\u0000\u0000./\u0005\u0016"+
		"\u0000\u0000/1\u0003\u0002\u0001\u00020\u001e\u0001\u0000\u0000\u0000"+
		"0!\u0001\u0000\u0000\u00000$\u0001\u0000\u0000\u00000\'\u0001\u0000\u0000"+
		"\u00000*\u0001\u0000\u0000\u00000-\u0001\u0000\u0000\u000014\u0001\u0000"+
		"\u0000\u000020\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u00003\u0003"+
		"\u0001\u0000\u0000\u000042\u0001\u0000\u0000\u00005;\u0003\u0006\u0003"+
		"\u000067\u0003\b\u0004\u000078\u0003\u0006\u0003\u00008:\u0001\u0000\u0000"+
		"\u000096\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000\u0000;9\u0001\u0000"+
		"\u0000\u0000;<\u0001\u0000\u0000\u0000<\u0005\u0001\u0000\u0000\u0000"+
		"=;\u0001\u0000\u0000\u0000>D\u0003\f\u0006\u0000?@\u0003\n\u0005\u0000"+
		"@A\u0003\f\u0006\u0000AC\u0001\u0000\u0000\u0000B?\u0001\u0000\u0000\u0000"+
		"CF\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000"+
		"\u0000E\u0007\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000GH\u0007"+
		"\u0000\u0000\u0000H\t\u0001\u0000\u0000\u0000IJ\u0007\u0001\u0000\u0000"+
		"J\u000b\u0001\u0000\u0000\u0000KS\u0003\u0016\u000b\u0000LS\u0003\u000e"+
		"\u0007\u0000MS\u0003\u0014\n\u0000NO\u0005\u0003\u0000\u0000OP\u0003\u0002"+
		"\u0001\u0000PQ\u0005\u0004\u0000\u0000QS\u0001\u0000\u0000\u0000RK\u0001"+
		"\u0000\u0000\u0000RL\u0001\u0000\u0000\u0000RM\u0001\u0000\u0000\u0000"+
		"RN\u0001\u0000\u0000\u0000S\r\u0001\u0000\u0000\u0000TV\u0005\u0017\u0000"+
		"\u0000UW\u0003\u0010\b\u0000VU\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000"+
		"\u0000W\u000f\u0001\u0000\u0000\u0000XY\u0005\u0003\u0000\u0000YZ\u0003"+
		"\u0012\t\u0000Z[\u0005\u0004\u0000\u0000[\u0011\u0001\u0000\u0000\u0000"+
		"\\a\u0003\u0002\u0001\u0000]^\u0005\u0005\u0000\u0000^`\u0003\u0002\u0001"+
		"\u0000_]\u0001\u0000\u0000\u0000`c\u0001\u0000\u0000\u0000a_\u0001\u0000"+
		"\u0000\u0000ab\u0001\u0000\u0000\u0000b\u0013\u0001\u0000\u0000\u0000"+
		"ca\u0001\u0000\u0000\u0000de\u0007\u0002\u0000\u0000ef\u0003\u0002\u0001"+
		"\u0000f\u0015\u0001\u0000\u0000\u0000gh\u0007\u0003\u0000\u0000h\u0017"+
		"\u0001\u0000\u0000\u0000\u000702;DRVa";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}