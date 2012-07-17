// $ANTLR 3.4 src/CalcGrammar.g 2012-07-17 21:44:15

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
			"DECINT", "DIGIT", "FUNCTION", "GT", "LAND", "LETTER", "LT", "MINUS", "MULOP", "MULOP_", "NOT", "NUMBER",
			"OPERATOR", "OR", "PLUS", "RELOP", "ROOT", "SIGN", "SIGN_", "TERM_", "WS", "'('", "')'", "','" };

	public static final int EOF = -1;
	public static final int T__26 = 26;
	public static final int T__27 = 27;
	public static final int T__28 = 28;
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
	public static final int MULOP_ = 14;
	public static final int NOT = 15;
	public static final int NUMBER = 16;
	public static final int OPERATOR = 17;
	public static final int OR = 18;
	public static final int PLUS = 19;
	public static final int RELOP = 20;
	public static final int ROOT = 21;
	public static final int SIGN = 22;
	public static final int SIGN_ = 23;
	public static final int TERM_ = 24;
	public static final int WS = 25;

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

		//TODO handle other exceptions
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
		System.out.println("handleOperator: operator=" + operator + " curElem=" + curElem.getValue() + " newElem="
				+ newElem.getValue());
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
	// src/CalcGrammar.g:161:1: formula returns [FormulaElement formulaTree] : term_list ;
	public final FormulaElement formula() throws RecognitionException {
		FormulaElement formulaTree = null;

		FormulaElement term_list1 = null;

		try {
			// src/CalcGrammar.g:161:45: ( term_list )
			// src/CalcGrammar.g:161:47: term_list
			{
				pushFollow(FOLLOW_term_list_in_formula67);
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
	// src/CalcGrammar.g:163:1: term_list returns [FormulaElement termListTree] : (firstTermTree= term ) (operatorLexVal= OPERATOR loopTermTree= term )* ;
	public final FormulaElement term_list() throws RecognitionException {
		FormulaElement termListTree = null;

		Token operatorLexVal = null;
		FormulaElement firstTermTree = null;

		FormulaElement loopTermTree = null;

		try {
			// src/CalcGrammar.g:163:49: ( (firstTermTree= term ) (operatorLexVal= OPERATOR loopTermTree= term )* )
			// src/CalcGrammar.g:164:5: (firstTermTree= term ) (operatorLexVal= OPERATOR loopTermTree= term )*
			{

				FormulaElement curElem;

				// src/CalcGrammar.g:167:5: (firstTermTree= term )
				// src/CalcGrammar.g:167:6: firstTermTree= term
				{
					pushFollow(FOLLOW_term_in_term_list95);
					firstTermTree = term();

					state._fsp--;

					termListTree = firstTermTree;
					curElem = termListTree;

				}

				// src/CalcGrammar.g:173:5: (operatorLexVal= OPERATOR loopTermTree= term )*
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if ((LA1_0 == OPERATOR)) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// src/CalcGrammar.g:173:6: operatorLexVal= OPERATOR loopTermTree= term
						{
							operatorLexVal = (Token) match(input, OPERATOR, FOLLOW_OPERATOR_in_term_list122);

							pushFollow(FOLLOW_term_in_term_list126);
							loopTermTree = term();

							state._fsp--;

							handleOperator(operatorLexVal.getText(), curElem, loopTermTree);
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
	// src/CalcGrammar.g:180:1: term returns [FormulaElement termTree] : ( MINUS )? (number= NUMBER | '(' braceTree= term_list ')' |functionTree= function ) ;
	public final FormulaElement term() throws RecognitionException {
		FormulaElement termTree = null;

		Token number = null;
		FormulaElement braceTree = null;

		FormulaElement functionTree = null;

		try {
			// src/CalcGrammar.g:180:40: ( ( MINUS )? (number= NUMBER | '(' braceTree= term_list ')' |functionTree= function ) )
			// src/CalcGrammar.g:181:5: ( MINUS )? (number= NUMBER | '(' braceTree= term_list ')' |functionTree= function )
			{

				termTree = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, null, null, null);
				FormulaElement curElem = termTree;

				// src/CalcGrammar.g:185:5: ( MINUS )?
				int alt2 = 2;
				int LA2_0 = input.LA(1);

				if ((LA2_0 == MINUS)) {
					alt2 = 1;
				}
				switch (alt2) {
					case 1:
					// src/CalcGrammar.g:185:6: MINUS
					{
						match(input, MINUS, FOLLOW_MINUS_in_term164);

						curElem = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, termTree, null, null);
						termTree.replaceElement(FormulaElement.ELEMENT_OPERATOR, "-", null, curElem);

					}
						break;

				}

				// src/CalcGrammar.g:191:5: (number= NUMBER | '(' braceTree= term_list ')' |functionTree= function )
				int alt3 = 3;
				switch (input.LA(1)) {
					case NUMBER: {
						alt3 = 1;
					}
						break;
					case 26: {
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
					// src/CalcGrammar.g:191:7: number= NUMBER
					{
						number = (Token) match(input, NUMBER, FOLLOW_NUMBER_in_term185);

						curElem.replaceElement(FormulaElement.ELEMENT_VALUE, number.getText());

					}
						break;
					case 2:
					// src/CalcGrammar.g:195:11: '(' braceTree= term_list ')'
					{
						match(input, 26, FOLLOW_26_in_term207);

						pushFollow(FOLLOW_term_list_in_term211);
						braceTree = term_list();

						state._fsp--;

						match(input, 27, FOLLOW_27_in_term213);

						System.out.println("braceTree: " + braceTree.getTreeString());
						curElem.replaceElement(braceTree);

					}
						break;
					case 3:
					// src/CalcGrammar.g:200:11: functionTree= function
					{
						pushFollow(FOLLOW_function_in_term241);
						functionTree = function();

						state._fsp--;

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
	// src/CalcGrammar.g:205:1: function returns [FormulaElement functionTree] : functionValue= FUNCTION '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' ;
	public final FormulaElement function() throws RecognitionException {
		FormulaElement functionTree = null;

		Token functionValue = null;
		FormulaElement leftChildTree = null;

		FormulaElement rightChildTree = null;

		try {
			// src/CalcGrammar.g:205:47: (functionValue= FUNCTION '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' )
			// src/CalcGrammar.g:206:7: functionValue= FUNCTION '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')'
			{
				functionValue = (Token) match(input, FUNCTION, FOLLOW_FUNCTION_in_function277);

				FormulaElement leftChild = null;
				FormulaElement rightChild = null;

				match(input, 26, FOLLOW_26_in_function306);

				// src/CalcGrammar.g:211:17: (leftChildTree= term_list ( ',' rightChildTree= term_list )? )?
				int alt5 = 2;
				int LA5_0 = input.LA(1);

				if ((LA5_0 == FUNCTION || LA5_0 == MINUS || LA5_0 == NUMBER || LA5_0 == 26)) {
					alt5 = 1;
				}
				switch (alt5) {
					case 1:
					// src/CalcGrammar.g:211:18: leftChildTree= term_list ( ',' rightChildTree= term_list )?
					{
						pushFollow(FOLLOW_term_list_in_function311);
						leftChildTree = term_list();

						state._fsp--;

						leftChild = leftChildTree;

						// src/CalcGrammar.g:215:21: ( ',' rightChildTree= term_list )?
						int alt4 = 2;
						int LA4_0 = input.LA(1);

						if ((LA4_0 == 28)) {
							alt4 = 1;
						}
						switch (alt4) {
							case 1:
							// src/CalcGrammar.g:215:22: ',' rightChildTree= term_list
							{
								match(input, 28, FOLLOW_28_in_function357);

								pushFollow(FOLLOW_term_list_in_function361);
								rightChildTree = term_list();

								state._fsp--;

								rightChild = rightChildTree;

							}
								break;

						}

					}
						break;

				}

				match(input, 27, FOLLOW_27_in_function431);

				functionTree = new FormulaElement(FormulaElement.ELEMENT_FUNCTION, functionValue.getText(), null,
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

	// Delegated rules

	public static final BitSet FOLLOW_term_list_in_formula67 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_term_in_term_list95 = new BitSet(new long[] { 0x0000000000020002L });
	public static final BitSet FOLLOW_OPERATOR_in_term_list122 = new BitSet(new long[] { 0x0000000004011080L });
	public static final BitSet FOLLOW_term_in_term_list126 = new BitSet(new long[] { 0x0000000000020002L });
	public static final BitSet FOLLOW_MINUS_in_term164 = new BitSet(new long[] { 0x0000000004010080L });
	public static final BitSet FOLLOW_NUMBER_in_term185 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_26_in_term207 = new BitSet(new long[] { 0x0000000004011080L });
	public static final BitSet FOLLOW_term_list_in_term211 = new BitSet(new long[] { 0x0000000008000000L });
	public static final BitSet FOLLOW_27_in_term213 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_function_in_term241 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_FUNCTION_in_function277 = new BitSet(new long[] { 0x0000000004000000L });
	public static final BitSet FOLLOW_26_in_function306 = new BitSet(new long[] { 0x000000000C011080L });
	public static final BitSet FOLLOW_term_list_in_function311 = new BitSet(new long[] { 0x0000000018000000L });
	public static final BitSet FOLLOW_28_in_function357 = new BitSet(new long[] { 0x0000000004011080L });
	public static final BitSet FOLLOW_term_list_in_function361 = new BitSet(new long[] { 0x0000000008000000L });
	public static final BitSet FOLLOW_27_in_function431 = new BitSet(new long[] { 0x0000000000000002L });

}