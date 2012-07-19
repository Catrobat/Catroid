// $ANTLR 3.4 src/CalcGrammar.g 2012-07-19 15:02:09

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
			"CONSTANT", "DECINT", "DIGIT", "GT", "ID", "LAND", "LETTER", "LT", "MINUS", "MULOP", "NOT", "NUMBER", "OR",
			"PLUS", "RELOP", "UPPERCASE", "WS", "'('", "')'", "','" };

	public static final int EOF = -1;
	public static final int T__22 = 22;
	public static final int T__23 = 23;
	public static final int T__24 = 24;
	public static final int COMMENT = 4;
	public static final int CONSTANT = 5;
	public static final int DECINT = 6;
	public static final int DIGIT = 7;
	public static final int GT = 8;
	public static final int ID = 9;
	public static final int LAND = 10;
	public static final int LETTER = 11;
	public static final int LT = 12;
	public static final int MINUS = 13;
	public static final int MULOP = 14;
	public static final int NOT = 15;
	public static final int NUMBER = 16;
	public static final int OR = 17;
	public static final int PLUS = 18;
	public static final int RELOP = 19;
	public static final int UPPERCASE = 20;
	public static final int WS = 21;

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

	public String internalCommaSeperatedDouble(String value) {
		return value.replace(',', '.');
	}

	// $ANTLR start "formula"
	// src/CalcGrammar.g:152:1: formula returns [FormulaElement formulaTree] : term_list ;
	public final FormulaElement formula() throws RecognitionException {
		FormulaElement formulaTree = null;

		FormulaElement term_list1 = null;

		try {
			// src/CalcGrammar.g:152:45: ( term_list )
			// src/CalcGrammar.g:153:5: term_list
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
	// src/CalcGrammar.g:158:1: term_list returns [FormulaElement termListTree] : (firstTermTree= term ) ( operator loopTermTree= term )* ;
	public final FormulaElement term_list() throws RecognitionException {
		FormulaElement termListTree = null;

		FormulaElement firstTermTree = null;

		FormulaElement loopTermTree = null;

		String operator2 = null;

		try {
			// src/CalcGrammar.g:158:49: ( (firstTermTree= term ) ( operator loopTermTree= term )* )
			// src/CalcGrammar.g:159:5: (firstTermTree= term ) ( operator loopTermTree= term )*
			{

				FormulaElement curElem;

				// src/CalcGrammar.g:162:5: (firstTermTree= term )
				// src/CalcGrammar.g:162:6: firstTermTree= term
				{
					pushFollow(FOLLOW_term_in_term_list90);
					firstTermTree = term();

					state._fsp--;

					termListTree = firstTermTree;
					curElem = termListTree;

				}

				// src/CalcGrammar.g:168:5: ( operator loopTermTree= term )*
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if (((LA1_0 >= MINUS && LA1_0 <= MULOP) || LA1_0 == PLUS)) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// src/CalcGrammar.g:168:6: operator loopTermTree= term
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
	// src/CalcGrammar.g:175:1: term returns [FormulaElement termTree] : ( MINUS )? ( NUMBER | '(' term_list ')' | variableOrFunction ) ;
	public final FormulaElement term() throws RecognitionException {
		FormulaElement termTree = null;

		Token MINUS3 = null;
		Token NUMBER4 = null;
		FormulaElement term_list5 = null;

		FormulaElement variableOrFunction6 = null;

		try {
			// src/CalcGrammar.g:175:40: ( ( MINUS )? ( NUMBER | '(' term_list ')' | variableOrFunction ) )
			// src/CalcGrammar.g:176:5: ( MINUS )? ( NUMBER | '(' term_list ')' | variableOrFunction )
			{

				termTree = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, null, null, null);
				FormulaElement curElem = termTree;
				System.out.println("term enter");

				// src/CalcGrammar.g:181:5: ( MINUS )?
				int alt2 = 2;
				int LA2_0 = input.LA(1);

				if ((LA2_0 == MINUS)) {
					alt2 = 1;
				}
				switch (alt2) {
					case 1:
					// src/CalcGrammar.g:181:6: MINUS
					{
						MINUS3 = (Token) match(input, MINUS, FOLLOW_MINUS_in_term157);

						System.out.println("minus enter");
						curElem = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, termTree, null, null);
						termTree.replaceElement(FormulaElement.ELEMENT_OPERATOR, MINUS3.getText(), null, curElem);

					}
						break;

				}

				// src/CalcGrammar.g:188:5: ( NUMBER | '(' term_list ')' | variableOrFunction )
				int alt3 = 3;
				switch (input.LA(1)) {
					case NUMBER: {
						alt3 = 1;
					}
						break;
					case 22: {
						alt3 = 2;
					}
						break;
					case CONSTANT:
					case ID: {
						alt3 = 3;
					}
						break;
					default:
						NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

						throw nvae;

				}

				switch (alt3) {
					case 1:
					// src/CalcGrammar.g:188:7: NUMBER
					{
						NUMBER4 = (Token) match(input, NUMBER, FOLLOW_NUMBER_in_term176);

						System.out.println("number enter");
						String number = internalCommaSeperatedDouble(NUMBER4.getText());
						curElem.replaceElement(FormulaElement.ELEMENT_VALUE, number);

					}
						break;
					case 2:
					// src/CalcGrammar.g:194:7: '(' term_list ')'
					{
						match(input, 22, FOLLOW_22_in_term194);

						pushFollow(FOLLOW_term_list_in_term196);
						term_list5 = term_list();

						state._fsp--;

						match(input, 23, FOLLOW_23_in_term198);

						System.out.println("brace enter");
						System.out.println("term_list: " + term_list5.getTreeString());
						curElem.replaceElement(term_list5);

					}
						break;
					case 3:
					// src/CalcGrammar.g:200:7: variableOrFunction
					{
						pushFollow(FOLLOW_variableOrFunction_in_term216);
						variableOrFunction6 = variableOrFunction();

						state._fsp--;

						curElem.replaceElement(variableOrFunction6);

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

	// $ANTLR start "variableOrFunction"
	// src/CalcGrammar.g:206:1: variableOrFunction returns [FormulaElement variableOrFunctionTree] : ( CONSTANT | ID ( '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' |) );
	public final FormulaElement variableOrFunction() throws RecognitionException {
		FormulaElement variableOrFunctionTree = null;

		Token CONSTANT7 = null;
		Token ID8 = null;
		FormulaElement leftChildTree = null;

		FormulaElement rightChildTree = null;

		try {
			// src/CalcGrammar.g:206:67: ( CONSTANT | ID ( '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' |) )
			int alt7 = 2;
			int LA7_0 = input.LA(1);

			if ((LA7_0 == CONSTANT)) {
				alt7 = 1;
			} else if ((LA7_0 == ID)) {
				alt7 = 2;
			} else {
				NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

				throw nvae;

			}
			switch (alt7) {
				case 1:
				// src/CalcGrammar.g:207:7: CONSTANT
				{

					System.out.println("variableOrFunction enter");

					CONSTANT7 = (Token) match(input, CONSTANT, FOLLOW_CONSTANT_in_variableOrFunction259);

					variableOrFunctionTree = new FormulaElement(FormulaElement.ELEMENT_CONSTANT, CONSTANT7.getText(),
							null, null, null);

				}
					break;
				case 2:
				// src/CalcGrammar.g:216:9: ID ( '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' |)
				{
					ID8 = (Token) match(input, ID, FOLLOW_ID_in_variableOrFunction290);

					// src/CalcGrammar.g:217:13: ( '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' |)
					int alt6 = 2;
					int LA6_0 = input.LA(1);

					if ((LA6_0 == 22)) {
						alt6 = 1;
					} else if ((LA6_0 == EOF || (LA6_0 >= MINUS && LA6_0 <= MULOP) || LA6_0 == PLUS || (LA6_0 >= 23 && LA6_0 <= 24))) {
						alt6 = 2;
					} else {
						NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

						throw nvae;

					}
					switch (alt6) {
						case 1:
						// src/CalcGrammar.g:218:17: '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')'
						{

							FormulaElement leftChild = null;
							FormulaElement rightChild = null;

							match(input, 22, FOLLOW_22_in_variableOrFunction343);

							// src/CalcGrammar.g:222:21: (leftChildTree= term_list ( ',' rightChildTree= term_list )? )?
							int alt5 = 2;
							int LA5_0 = input.LA(1);

							if ((LA5_0 == CONSTANT || LA5_0 == ID || LA5_0 == MINUS || LA5_0 == NUMBER || LA5_0 == 22)) {
								alt5 = 1;
							}
							switch (alt5) {
								case 1:
								// src/CalcGrammar.g:222:22: leftChildTree= term_list ( ',' rightChildTree= term_list )?
								{
									pushFollow(FOLLOW_term_list_in_variableOrFunction348);
									leftChildTree = term_list();

									state._fsp--;

									leftChild = leftChildTree;

									// src/CalcGrammar.g:226:23: ( ',' rightChildTree= term_list )?
									int alt4 = 2;
									int LA4_0 = input.LA(1);

									if ((LA4_0 == 24)) {
										alt4 = 1;
									}
									switch (alt4) {
										case 1:
										// src/CalcGrammar.g:226:24: ',' rightChildTree= term_list
										{
											match(input, 24, FOLLOW_24_in_variableOrFunction398);

											pushFollow(FOLLOW_term_list_in_variableOrFunction402);
											rightChildTree = term_list();

											state._fsp--;

											rightChild = rightChildTree;

										}
											break;

									}

								}
									break;

							}

							match(input, 23, FOLLOW_23_in_variableOrFunction481);

							variableOrFunctionTree = new FormulaElement(FormulaElement.ELEMENT_FUNCTION, ID8.getText(),
									null, leftChild, rightChild);

						}
							break;
						case 2:
						// src/CalcGrammar.g:236:17: 
						{

							variableOrFunctionTree = new FormulaElement(FormulaElement.ELEMENT_VARIABLE, ID8.getText(),
									null, null, null);

						}
							break;

					}

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
		return variableOrFunctionTree;
	}

	// $ANTLR end "variableOrFunction"

	// $ANTLR start "operator"
	// src/CalcGrammar.g:240:1: operator returns [String operatorString] : ( MULOP | PLUS | MINUS );
	public final String operator() throws RecognitionException {
		String operatorString = null;

		Token MULOP9 = null;
		Token PLUS10 = null;
		Token MINUS11 = null;

		try {
			// src/CalcGrammar.g:240:41: ( MULOP | PLUS | MINUS )
			int alt8 = 3;
			switch (input.LA(1)) {
				case MULOP: {
					alt8 = 1;
				}
					break;
				case PLUS: {
					alt8 = 2;
				}
					break;
				case MINUS: {
					alt8 = 3;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 8, 0, input);

					throw nvae;

			}

			switch (alt8) {
				case 1:
				// src/CalcGrammar.g:241:5: MULOP
				{
					MULOP9 = (Token) match(input, MULOP, FOLLOW_MULOP_in_operator549);

					operatorString = MULOP9.getText();

				}
					break;
				case 2:
				// src/CalcGrammar.g:245:7: PLUS
				{
					PLUS10 = (Token) match(input, PLUS, FOLLOW_PLUS_in_operator568);

					operatorString = PLUS10.getText();

				}
					break;
				case 3:
				// src/CalcGrammar.g:249:7: MINUS
				{
					MINUS11 = (Token) match(input, MINUS, FOLLOW_MINUS_in_operator590);

					operatorString = MINUS11.getText();

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
	public static final BitSet FOLLOW_term_in_term_list90 = new BitSet(new long[] { 0x0000000000046002L });
	public static final BitSet FOLLOW_operator_in_term_list115 = new BitSet(new long[] { 0x0000000000412220L });
	public static final BitSet FOLLOW_term_in_term_list119 = new BitSet(new long[] { 0x0000000000046002L });
	public static final BitSet FOLLOW_MINUS_in_term157 = new BitSet(new long[] { 0x0000000000410220L });
	public static final BitSet FOLLOW_NUMBER_in_term176 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_22_in_term194 = new BitSet(new long[] { 0x0000000000412220L });
	public static final BitSet FOLLOW_term_list_in_term196 = new BitSet(new long[] { 0x0000000000800000L });
	public static final BitSet FOLLOW_23_in_term198 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_variableOrFunction_in_term216 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_CONSTANT_in_variableOrFunction259 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_ID_in_variableOrFunction290 = new BitSet(new long[] { 0x0000000000400002L });
	public static final BitSet FOLLOW_22_in_variableOrFunction343 = new BitSet(new long[] { 0x0000000000C12220L });
	public static final BitSet FOLLOW_term_list_in_variableOrFunction348 = new BitSet(
			new long[] { 0x0000000001800000L });
	public static final BitSet FOLLOW_24_in_variableOrFunction398 = new BitSet(new long[] { 0x0000000000412220L });
	public static final BitSet FOLLOW_term_list_in_variableOrFunction402 = new BitSet(
			new long[] { 0x0000000000800000L });
	public static final BitSet FOLLOW_23_in_variableOrFunction481 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_MULOP_in_operator549 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_PLUS_in_operator568 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_MINUS_in_operator590 = new BitSet(new long[] { 0x0000000000000002L });

}