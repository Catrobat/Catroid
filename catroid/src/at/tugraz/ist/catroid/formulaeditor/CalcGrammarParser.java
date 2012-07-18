// $ANTLR 3.4 src/CalcGrammar.g 2012-07-18 16:28:49

package at.tugraz.ist.catroid.formulaeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

@SuppressWarnings({ "all", "warnings", "unchecked" })
public class CalcGrammarParser extends Parser {
	public static final String[] tokenNames = new String[] { "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMENT",
			"DECINT", "DIGIT", "FUNCTION", "GT", "LAND", "LETTER", "LT", "MINUS", "MULOP", "NOT", "NUMBER", "OR",
			"PLUS", "RELOP", "WS", "'('", "')'", "','" };

	public static final int EOF = -1;
	public static final int T__20 = 20;
	public static final int T__21 = 21;
	public static final int T__22 = 22;
	public static final int COMMENT = 4;
	public static final int DECINT = 5;
	public static final int DIGIT = 6;
	public static final int FUNCTION = 7;
	public static final int GT = 8;
	public static final int LAND = 9;
	public static final int LETTER = 10;
	public static final int LT = 11;
	public static final int MINUS = 12;
	public static final int MULOP = 13;
	public static final int NOT = 14;
	public static final int NUMBER = 15;
	public static final int OR = 16;
	public static final int PLUS = 17;
	public static final int RELOP = 18;
	public static final int WS = 19;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators

	public CalcGrammarParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}

	public CalcGrammarParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public String[] getTokenNames() {
		return CalcGrammarParser.tokenNames;
	}

	public String getGrammarFileName() {
		return "src/CalcGrammar.g";
	}

	private int parserErrorCount = 0;
	private List<String> parserErrorMessages = null;
	public List<String> testString = new ArrayList<String>();

	private Queue<FormulaElement> formulaStack = new LinkedBlockingQueue<FormulaElement>();
	private FormulaElement currentFormulaElement;

	@Override
	public void reportError(RecognitionException e) {
		parserErrorCount++;
		if (parserErrorMessages == null) {
			parserErrorMessages = new ArrayList<String>();
		}

		boolean handled = false;

		try {
			NoViableAltException ex = (NoViableAltException) e;

			parserErrorMessages.add("\t#" + parserErrorCount + ": line " + ex.token.getLine() + ":"
					+ ex.token.getCharPositionInLine() + " no viable alternative at character '" + ex.token.getText()
					+ "'");

			handled = true;
		} catch (Exception etemp) {
		}

		try {
			if (!handled) {
				MismatchedTokenException mt = (MismatchedTokenException) e;

				parserErrorMessages.add("\t#" + parserErrorCount + ": line " + mt.token.getLine() + ":"
						+ mt.token.getCharPositionInLine() + " missmatched token at character '" + mt.token.getText()
						+ "'");

				handled = true;
			}
		} catch (Exception etemp) {
		}

		try {
			if (!handled) {
				parserErrorMessages.add("\t#" + parserErrorCount + ": line " + e.token.getLine() + ":"
						+ e.token.getCharPositionInLine() + " exception at character '" + e.token.getText() + "'");
				handled = true;
			}
		} catch (Exception etemp) {
		}

	}

	public int getParserErrorCount() {
		return this.parserErrorCount;
	}

	public List<String> getParserErrorMessages() {
		return this.parserErrorMessages;
	}

	public void handleOperator(String operator, FormulaElement curElem, FormulaElement newElem) {
		//        System.out.println("handleOperator: operator="+operator + " curElem="+curElem.getValue() + " newElem="+newElem.getValue());
		if (curElem.getParent() == null) {
			new FormulaElement(FormulaElement.ELEMENT_OPERATOR, operator, null, curElem, newElem);
			return;
		}

		Operators parentOp = Operators.getOperatorByValue(curElem.getParent().getValue());
		Operators currentOp = Operators.getOperatorByValue(operator);

		int compareOp = parentOp.compareOperatorTo(currentOp);

		if (compareOp >= 0) {
			new FormulaElement(FormulaElement.ELEMENT_OPERATOR, operator, null, curElem.getParent(), newElem);
		} else {
			curElem.replaceWithSubElement(operator, newElem);
		}
	}

	// $ANTLR start "formula"
	// src/CalcGrammar.g:147:1: formula returns [FormulaElement formulaTree] : term_list ;
	public final FormulaElement formula() throws RecognitionException {
		FormulaElement formulaTree = null;

		FormulaElement term_list1 = null;

		try {
			// src/CalcGrammar.g:147:45: ( term_list )
			// src/CalcGrammar.g:148:5: term_list
			{
				pushFollow(FOLLOW_term_list_in_formula53);
				term_list1 = term_list();

				state._fsp--;

				formulaTree = term_list1;

			}

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		}

		finally {
			// do for sure before leaving
		}
		return formulaTree;
	}

	// $ANTLR end "formula"

	// $ANTLR start "term_list"
	// src/CalcGrammar.g:153:1: term_list returns [FormulaElement termListTree] : (firstTermTree= term ) ( operator loopTermTree= term )* ;
	public final FormulaElement term_list() throws RecognitionException {
		FormulaElement termListTree = null;

		FormulaElement firstTermTree = null;

		FormulaElement loopTermTree = null;

		String operator2 = null;

		try {
			// src/CalcGrammar.g:153:49: ( (firstTermTree= term ) ( operator loopTermTree= term )* )
			// src/CalcGrammar.g:154:5: (firstTermTree= term ) ( operator loopTermTree= term )*
			{

				FormulaElement curElem;

				// src/CalcGrammar.g:157:5: (firstTermTree= term )
				// src/CalcGrammar.g:157:6: firstTermTree= term
				{
					pushFollow(FOLLOW_term_in_term_list90);
					firstTermTree = term();

					state._fsp--;

					termListTree = firstTermTree;
					curElem = termListTree;

				}

				// src/CalcGrammar.g:163:5: ( operator loopTermTree= term )*
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if (((LA1_0 >= MINUS && LA1_0 <= MULOP) || LA1_0 == PLUS)) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// src/CalcGrammar.g:163:6: operator loopTermTree= term
						{
							pushFollow(FOLLOW_operator_in_term_list115);
							operator2 = operator();

							state._fsp--;

							pushFollow(FOLLOW_term_in_term_list119);
							loopTermTree = term();

							state._fsp--;

							handleOperator(operator2, curElem, loopTermTree);
							curElem = loopTermTree;
							termListTree = curElem.getRoot();

						}
							break;

						default:
							break loop1;
					}
				} while (true);

			}

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		}

		finally {
			// do for sure before leaving
		}
		return termListTree;
	}

	// $ANTLR end "term_list"

	// $ANTLR start "term"
	// src/CalcGrammar.g:170:1: term returns [FormulaElement termTree] : ( MINUS )? ( NUMBER | '(' term_list ')' |functionTree= function ) ;
	public final FormulaElement term() throws RecognitionException {
		FormulaElement termTree = null;

		Token MINUS3 = null;
		Token NUMBER4 = null;
		FormulaElement functionTree = null;

		FormulaElement term_list5 = null;

		try {
			// src/CalcGrammar.g:170:40: ( ( MINUS )? ( NUMBER | '(' term_list ')' |functionTree= function ) )
			// src/CalcGrammar.g:171:5: ( MINUS )? ( NUMBER | '(' term_list ')' |functionTree= function )
			{

				termTree = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, null, null, null);
				FormulaElement curElem = termTree;
				System.out.println("term enter");

				// src/CalcGrammar.g:176:5: ( MINUS )?
				int alt2 = 2;
				int LA2_0 = input.LA(1);

				if ((LA2_0 == MINUS)) {
					alt2 = 1;
				}
				switch (alt2) {
					case 1:
					// src/CalcGrammar.g:176:6: MINUS
					{
						MINUS3 = (Token) match(input, MINUS, FOLLOW_MINUS_in_term157);

						System.out.println("minus enter");
						curElem = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, termTree, null, null);
						termTree.replaceElement(FormulaElement.ELEMENT_OPERATOR, MINUS3.getText(), null, curElem);

					}
						break;

				}

				// src/CalcGrammar.g:183:5: ( NUMBER | '(' term_list ')' |functionTree= function )
				int alt3 = 3;
				switch (input.LA(1)) {
					case NUMBER: {
						alt3 = 1;
					}
						break;
					case 20: {
						alt3 = 2;
					}
						break;
					case FUNCTION: {
						alt3 = 3;
					}
						break;
					default:
						NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

						throw nvae;

				}

				switch (alt3) {
					case 1:
					// src/CalcGrammar.g:183:7: NUMBER
					{
						NUMBER4 = (Token) match(input, NUMBER, FOLLOW_NUMBER_in_term176);

						System.out.println("number enter");
						curElem.replaceElement(FormulaElement.ELEMENT_VALUE, NUMBER4.getText());

					}
						break;
					case 2:
					// src/CalcGrammar.g:188:11: '(' term_list ')'
					{
						match(input, 20, FOLLOW_20_in_term198);

						pushFollow(FOLLOW_term_list_in_term200);
						term_list5 = term_list();

						state._fsp--;

						match(input, 21, FOLLOW_21_in_term202);

						System.out.println("brace enter");
						System.out.println("term_list: " + term_list5.getTreeString());
						curElem.replaceElement(term_list5);

					}
						break;
					case 3:
					// src/CalcGrammar.g:194:11: functionTree= function
					{
						pushFollow(FOLLOW_function_in_term230);
						functionTree = function();

						state._fsp--;

						System.out.println("function enter");
						curElem.replaceElement(functionTree);

					}
						break;

				}

			}

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		}

		finally {
			// do for sure before leaving
		}
		return termTree;
	}

	// $ANTLR end "term"

	// $ANTLR start "function"
	// src/CalcGrammar.g:200:1: function returns [FormulaElement functionTree] : FUNCTION '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' ;
	public final FormulaElement function() throws RecognitionException {
		FormulaElement functionTree = null;

		Token FUNCTION6 = null;
		FormulaElement leftChildTree = null;

		FormulaElement rightChildTree = null;

		try {
			// src/CalcGrammar.g:200:47: ( FUNCTION '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' )
			// src/CalcGrammar.g:201:7: FUNCTION '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')'
			{
				FUNCTION6 = (Token) match(input, FUNCTION, FOLLOW_FUNCTION_in_function264);

				FormulaElement leftChild = null;
				FormulaElement rightChild = null;

				match(input, 20, FOLLOW_20_in_function293);

				// src/CalcGrammar.g:206:17: (leftChildTree= term_list ( ',' rightChildTree= term_list )? )?
				int alt5 = 2;
				int LA5_0 = input.LA(1);

				if ((LA5_0 == FUNCTION || LA5_0 == MINUS || LA5_0 == NUMBER || LA5_0 == 20)) {
					alt5 = 1;
				}
				switch (alt5) {
					case 1:
					// src/CalcGrammar.g:206:18: leftChildTree= term_list ( ',' rightChildTree= term_list )?
					{
						pushFollow(FOLLOW_term_list_in_function298);
						leftChildTree = term_list();

						state._fsp--;

						leftChild = leftChildTree;

						// src/CalcGrammar.g:210:21: ( ',' rightChildTree= term_list )?
						int alt4 = 2;
						int LA4_0 = input.LA(1);

						if ((LA4_0 == 22)) {
							alt4 = 1;
						}
						switch (alt4) {
							case 1:
							// src/CalcGrammar.g:210:22: ',' rightChildTree= term_list
							{
								match(input, 22, FOLLOW_22_in_function344);

								pushFollow(FOLLOW_term_list_in_function348);
								rightChildTree = term_list();

								state._fsp--;

								rightChild = rightChildTree;

							}
								break;

						}

					}
						break;

				}

				match(input, 21, FOLLOW_21_in_function418);

				functionTree = new FormulaElement(FormulaElement.ELEMENT_FUNCTION, FUNCTION6.getText(), null,
						leftChild, rightChild);

			}

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		}

		finally {
			// do for sure before leaving
		}
		return functionTree;
	}

	// $ANTLR end "function"

	// $ANTLR start "operator"
	// src/CalcGrammar.g:220:1: operator returns [String operatorString] : ( MULOP | PLUS | MINUS );
	public final String operator() throws RecognitionException {
		String operatorString = null;

		Token MULOP7 = null;
		Token PLUS8 = null;
		Token MINUS9 = null;

		try {
			// src/CalcGrammar.g:220:41: ( MULOP | PLUS | MINUS )
			int alt6 = 3;
			switch (input.LA(1)) {
				case MULOP: {
					alt6 = 1;
				}
					break;
				case PLUS: {
					alt6 = 2;
				}
					break;
				case MINUS: {
					alt6 = 3;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

					throw nvae;

			}

			switch (alt6) {
				case 1:
				// src/CalcGrammar.g:221:5: MULOP
				{
					MULOP7 = (Token) match(input, MULOP, FOLLOW_MULOP_in_operator448);

					operatorString = MULOP7.getText();

				}
					break;
				case 2:
				// src/CalcGrammar.g:225:7: PLUS
				{
					PLUS8 = (Token) match(input, PLUS, FOLLOW_PLUS_in_operator467);

					operatorString = PLUS8.getText();

				}
					break;
				case 3:
				// src/CalcGrammar.g:229:7: MINUS
				{
					MINUS9 = (Token) match(input, MINUS, FOLLOW_MINUS_in_operator489);

					operatorString = MINUS9.getText();

				}
					break;

			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		}

		finally {
			// do for sure before leaving
		}
		return operatorString;
	}

	// $ANTLR end "operator"

	// Delegated rules

	public static final BitSet FOLLOW_term_list_in_formula53 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_term_in_term_list90 = new BitSet(new long[] { 0x0000000000023002L });
	public static final BitSet FOLLOW_operator_in_term_list115 = new BitSet(new long[] { 0x0000000000109080L });
	public static final BitSet FOLLOW_term_in_term_list119 = new BitSet(new long[] { 0x0000000000023002L });
	public static final BitSet FOLLOW_MINUS_in_term157 = new BitSet(new long[] { 0x0000000000108080L });
	public static final BitSet FOLLOW_NUMBER_in_term176 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_20_in_term198 = new BitSet(new long[] { 0x0000000000109080L });
	public static final BitSet FOLLOW_term_list_in_term200 = new BitSet(new long[] { 0x0000000000200000L });
	public static final BitSet FOLLOW_21_in_term202 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_function_in_term230 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_FUNCTION_in_function264 = new BitSet(new long[] { 0x0000000000100000L });
	public static final BitSet FOLLOW_20_in_function293 = new BitSet(new long[] { 0x0000000000309080L });
	public static final BitSet FOLLOW_term_list_in_function298 = new BitSet(new long[] { 0x0000000000600000L });
	public static final BitSet FOLLOW_22_in_function344 = new BitSet(new long[] { 0x0000000000109080L });
	public static final BitSet FOLLOW_term_list_in_function348 = new BitSet(new long[] { 0x0000000000200000L });
	public static final BitSet FOLLOW_21_in_function418 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_MULOP_in_operator448 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_PLUS_in_operator467 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_MINUS_in_operator489 = new BitSet(new long[] { 0x0000000000000002L });

}