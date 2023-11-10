// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/CatrobatParameterParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterParserListener;
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterParserVisitor;

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
		LIST=8, STRING=9, OPERATOR_ADD=10, OPERATOR_NUMERIC_DIVIDE=11, OPERATOR_NUMERIC_MULTIPLY=12, 
		OPERATOR_LOGIC_AND=13, OPERATOR_LOGIC_OR=14, OPERATOR_LOGIC_NOT=15, OPERATOR_LOGIC_EQUAL=16, 
		OPERATOR_LOGIC_NOT_EQUAL=17, OPERATOR_LOGIC_LOWER=18, OPERATOR_LOGIC_GREATER=19, 
		OPERATOR_LOGIC_LOWER_EQUAL=20, OPERATOR_LOGIC_GREATER_EQUAL=21, FUNCTION_OR_SENSOR=22;
	public static final int
		RULE_argument = 0, RULE_expression = 1, RULE_simple_expression = 2, RULE_sensor_reference = 3, 
		RULE_method_invoaction = 4, RULE_parameters = 5, RULE_param_list = 6, 
		RULE_unary_expression = 7, RULE_literal = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"argument", "expression", "simple_expression", "sensor_reference", "method_invoaction", 
			"parameters", "param_list", "unary_expression", "literal"
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
			setState(18);
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
	public static class ExpressionContext extends ParserRuleContext {
		public Simple_expressionContext simple_expression() {
			return getRuleContext(Simple_expressionContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OPERATOR_ADD() { return getToken(CatrobatParameterParser.OPERATOR_ADD, 0); }
		public TerminalNode OPERATOR_NUMERIC_DIVIDE() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_DIVIDE, 0); }
		public TerminalNode OPERATOR_NUMERIC_MULTIPLY() { return getToken(CatrobatParameterParser.OPERATOR_NUMERIC_MULTIPLY, 0); }
		public TerminalNode OPERATOR_LOGIC_AND() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_AND, 0); }
		public TerminalNode OPERATOR_LOGIC_OR() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_OR, 0); }
		public TerminalNode OPERATOR_LOGIC_NOT() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_NOT, 0); }
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
			setState(21);
			simple_expression();
			}
			_ctx.stop = _input.LT(-1);
			setState(61);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(59);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(23);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(24);
						match(OPERATOR_ADD);
						setState(25);
						expression(13);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(26);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(27);
						match(OPERATOR_NUMERIC_DIVIDE);
						setState(28);
						expression(12);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(29);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(30);
						match(OPERATOR_NUMERIC_MULTIPLY);
						setState(31);
						expression(11);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(32);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(33);
						match(OPERATOR_LOGIC_AND);
						setState(34);
						expression(10);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(35);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(36);
						match(OPERATOR_LOGIC_OR);
						setState(37);
						expression(9);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(38);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(39);
						match(OPERATOR_LOGIC_NOT);
						setState(40);
						expression(8);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(41);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(42);
						match(OPERATOR_LOGIC_EQUAL);
						setState(43);
						expression(7);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(44);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(45);
						match(OPERATOR_LOGIC_NOT_EQUAL);
						setState(46);
						expression(6);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(47);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(48);
						match(OPERATOR_LOGIC_LOWER);
						setState(49);
						expression(5);
						}
						break;
					case 10:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(50);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(51);
						match(OPERATOR_LOGIC_GREATER);
						setState(52);
						expression(4);
						}
						break;
					case 11:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(53);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(54);
						match(OPERATOR_LOGIC_LOWER_EQUAL);
						setState(55);
						expression(3);
						}
						break;
					case 12:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(56);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(57);
						match(OPERATOR_LOGIC_GREATER_EQUAL);
						setState(58);
						expression(2);
						}
						break;
					}
					} 
				}
				setState(63);
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
	public static class Simple_expressionContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public Sensor_referenceContext sensor_reference() {
			return getRuleContext(Sensor_referenceContext.class,0);
		}
		public Method_invoactionContext method_invoaction() {
			return getRuleContext(Method_invoactionContext.class,0);
		}
		public Unary_expressionContext unary_expression() {
			return getRuleContext(Unary_expressionContext.class,0);
		}
		public TerminalNode BRACE_OPEN() { return getToken(CatrobatParameterParser.BRACE_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BRACE_CLOSE() { return getToken(CatrobatParameterParser.BRACE_CLOSE, 0); }
		public Simple_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterSimple_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitSimple_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitSimple_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Simple_expressionContext simple_expression() throws RecognitionException {
		Simple_expressionContext _localctx = new Simple_expressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_simple_expression);
		try {
			setState(72);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(64);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(65);
				sensor_reference();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(66);
				method_invoaction();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(67);
				unary_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(68);
				match(BRACE_OPEN);
				setState(69);
				expression(0);
				setState(70);
				match(BRACE_CLOSE);
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
	public static class Sensor_referenceContext extends ParserRuleContext {
		public TerminalNode FUNCTION_OR_SENSOR() { return getToken(CatrobatParameterParser.FUNCTION_OR_SENSOR, 0); }
		public Sensor_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sensor_reference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterSensor_reference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitSensor_reference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitSensor_reference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sensor_referenceContext sensor_reference() throws RecognitionException {
		Sensor_referenceContext _localctx = new Sensor_referenceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_sensor_reference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(FUNCTION_OR_SENSOR);
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
	public static class Method_invoactionContext extends ParserRuleContext {
		public TerminalNode FUNCTION_OR_SENSOR() { return getToken(CatrobatParameterParser.FUNCTION_OR_SENSOR, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public Method_invoactionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_invoaction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterMethod_invoaction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitMethod_invoaction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitMethod_invoaction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Method_invoactionContext method_invoaction() throws RecognitionException {
		Method_invoactionContext _localctx = new Method_invoactionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_method_invoaction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(FUNCTION_OR_SENSOR);
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(77);
				parameters();
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
	public static class ParametersContext extends ParserRuleContext {
		public TerminalNode BRACE_OPEN() { return getToken(CatrobatParameterParser.BRACE_OPEN, 0); }
		public Param_listContext param_list() {
			return getRuleContext(Param_listContext.class,0);
		}
		public TerminalNode BRACE_CLOSE() { return getToken(CatrobatParameterParser.BRACE_CLOSE, 0); }
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_parameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(BRACE_OPEN);
			setState(81);
			param_list();
			setState(82);
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
	public static class Param_listContext extends ParserRuleContext {
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
		public Param_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterParam_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitParam_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitParam_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Param_listContext param_list() throws RecognitionException {
		Param_listContext _localctx = new Param_listContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_param_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			expression(0);
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(85);
				match(COMMA);
				setState(86);
				expression(0);
				}
				}
				setState(91);
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
	public static class Unary_expressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode OPERATOR_ADD() { return getToken(CatrobatParameterParser.OPERATOR_ADD, 0); }
		public TerminalNode OPERATOR_LOGIC_NOT() { return getToken(CatrobatParameterParser.OPERATOR_LOGIC_NOT, 0); }
		public Unary_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).enterUnary_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CatrobatParameterParserListener ) ((CatrobatParameterParserListener)listener).exitUnary_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CatrobatParameterParserVisitor ) return ((CatrobatParameterParserVisitor<? extends T>)visitor).visitUnary_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unary_expressionContext unary_expression() throws RecognitionException {
		Unary_expressionContext _localctx = new Unary_expressionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_unary_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			_la = _input.LA(1);
			if ( !(_la==OPERATOR_ADD || _la==OPERATOR_LOGIC_NOT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(93);
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
		enterRule(_localctx, 16, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
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
			return precpred(_ctx, 12);
		case 1:
			return precpred(_ctx, 11);
		case 2:
			return precpred(_ctx, 10);
		case 3:
			return precpred(_ctx, 9);
		case 4:
			return precpred(_ctx, 8);
		case 5:
			return precpred(_ctx, 7);
		case 6:
			return precpred(_ctx, 6);
		case 7:
			return precpred(_ctx, 5);
		case 8:
			return precpred(_ctx, 4);
		case 9:
			return precpred(_ctx, 3);
		case 10:
			return precpred(_ctx, 2);
		case 11:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0016b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0005\u0001<\b\u0001\n\u0001\f\u0001?\t\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002I\b\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0003\u0004O\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006X\b\u0006\n\u0006\f\u0006"+
		"[\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b"+
		"\u0000\u0001\u0002\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0000\u0002"+
		"\u0002\u0000\n\n\u000f\u000f\u0002\u0000\u0002\u0002\u0006\tj\u0000\u0012"+
		"\u0001\u0000\u0000\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004H\u0001"+
		"\u0000\u0000\u0000\u0006J\u0001\u0000\u0000\u0000\bL\u0001\u0000\u0000"+
		"\u0000\nP\u0001\u0000\u0000\u0000\fT\u0001\u0000\u0000\u0000\u000e\\\u0001"+
		"\u0000\u0000\u0000\u0010_\u0001\u0000\u0000\u0000\u0012\u0013\u0003\u0002"+
		"\u0001\u0000\u0013\u0001\u0001\u0000\u0000\u0000\u0014\u0015\u0006\u0001"+
		"\uffff\uffff\u0000\u0015\u0016\u0003\u0004\u0002\u0000\u0016=\u0001\u0000"+
		"\u0000\u0000\u0017\u0018\n\f\u0000\u0000\u0018\u0019\u0005\n\u0000\u0000"+
		"\u0019<\u0003\u0002\u0001\r\u001a\u001b\n\u000b\u0000\u0000\u001b\u001c"+
		"\u0005\u000b\u0000\u0000\u001c<\u0003\u0002\u0001\f\u001d\u001e\n\n\u0000"+
		"\u0000\u001e\u001f\u0005\f\u0000\u0000\u001f<\u0003\u0002\u0001\u000b"+
		" !\n\t\u0000\u0000!\"\u0005\r\u0000\u0000\"<\u0003\u0002\u0001\n#$\n\b"+
		"\u0000\u0000$%\u0005\u000e\u0000\u0000%<\u0003\u0002\u0001\t&\'\n\u0007"+
		"\u0000\u0000\'(\u0005\u000f\u0000\u0000(<\u0003\u0002\u0001\b)*\n\u0006"+
		"\u0000\u0000*+\u0005\u0010\u0000\u0000+<\u0003\u0002\u0001\u0007,-\n\u0005"+
		"\u0000\u0000-.\u0005\u0011\u0000\u0000.<\u0003\u0002\u0001\u0006/0\n\u0004"+
		"\u0000\u000001\u0005\u0012\u0000\u00001<\u0003\u0002\u0001\u000523\n\u0003"+
		"\u0000\u000034\u0005\u0013\u0000\u00004<\u0003\u0002\u0001\u000456\n\u0002"+
		"\u0000\u000067\u0005\u0014\u0000\u00007<\u0003\u0002\u0001\u000389\n\u0001"+
		"\u0000\u00009:\u0005\u0015\u0000\u0000:<\u0003\u0002\u0001\u0002;\u0017"+
		"\u0001\u0000\u0000\u0000;\u001a\u0001\u0000\u0000\u0000;\u001d\u0001\u0000"+
		"\u0000\u0000; \u0001\u0000\u0000\u0000;#\u0001\u0000\u0000\u0000;&\u0001"+
		"\u0000\u0000\u0000;)\u0001\u0000\u0000\u0000;,\u0001\u0000\u0000\u0000"+
		";/\u0001\u0000\u0000\u0000;2\u0001\u0000\u0000\u0000;5\u0001\u0000\u0000"+
		"\u0000;8\u0001\u0000\u0000\u0000<?\u0001\u0000\u0000\u0000=;\u0001\u0000"+
		"\u0000\u0000=>\u0001\u0000\u0000\u0000>\u0003\u0001\u0000\u0000\u0000"+
		"?=\u0001\u0000\u0000\u0000@I\u0003\u0010\b\u0000AI\u0003\u0006\u0003\u0000"+
		"BI\u0003\b\u0004\u0000CI\u0003\u000e\u0007\u0000DE\u0005\u0003\u0000\u0000"+
		"EF\u0003\u0002\u0001\u0000FG\u0005\u0004\u0000\u0000GI\u0001\u0000\u0000"+
		"\u0000H@\u0001\u0000\u0000\u0000HA\u0001\u0000\u0000\u0000HB\u0001\u0000"+
		"\u0000\u0000HC\u0001\u0000\u0000\u0000HD\u0001\u0000\u0000\u0000I\u0005"+
		"\u0001\u0000\u0000\u0000JK\u0005\u0016\u0000\u0000K\u0007\u0001\u0000"+
		"\u0000\u0000LN\u0005\u0016\u0000\u0000MO\u0003\n\u0005\u0000NM\u0001\u0000"+
		"\u0000\u0000NO\u0001\u0000\u0000\u0000O\t\u0001\u0000\u0000\u0000PQ\u0005"+
		"\u0003\u0000\u0000QR\u0003\f\u0006\u0000RS\u0005\u0004\u0000\u0000S\u000b"+
		"\u0001\u0000\u0000\u0000TY\u0003\u0002\u0001\u0000UV\u0005\u0005\u0000"+
		"\u0000VX\u0003\u0002\u0001\u0000WU\u0001\u0000\u0000\u0000X[\u0001\u0000"+
		"\u0000\u0000YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z\r\u0001"+
		"\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000\\]\u0007\u0000\u0000\u0000"+
		"]^\u0003\u0002\u0001\u0000^\u000f\u0001\u0000\u0000\u0000_`\u0007\u0001"+
		"\u0000\u0000`\u0011\u0001\u0000\u0000\u0000\u0005;=HNY";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}