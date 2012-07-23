// $ANTLR 3.4 src/CalcGrammar.g 2012-07-23 17:50:40

package at.tugraz.ist.catroid.formulaeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

@SuppressWarnings({ "all", "warnings", "unchecked" })
public class CalcGrammarParser extends Parser {
	public static final String[] tokenNames = new String[] { "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CONSTANT",
			"DECINT", "DIGIT", "GT", "ID", "LAND", "LETTER", "LT", "MINUS", "MULOP", "NOT", "NUMBER", "OR", "PLUS",
			"RELOP", "SENSOR", "UPID", "UPPERCASE", "WS", "'('", "')'", "','" };

	public static final int EOF = -1;
	public static final int T__23 = 23;
	public static final int T__24 = 24;
	public static final int T__25 = 25;
	public static final int CONSTANT = 4;
	public static final int DECINT = 5;
	public static final int DIGIT = 6;
	public static final int GT = 7;
	public static final int ID = 8;
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
	public static final int SENSOR = 19;
	public static final int UPID = 20;
	public static final int UPPERCASE = 21;
	public static final int WS = 22;

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

	private int errorCharacterPosition = -1;
	private CalcGrammarLexer lexer;
	private String formulaString = "";

	@Override
	public void reportError(RecognitionException e) {

		if (errorCharacterPosition != -1) {
			return;
		}

		errorCharacterPosition = e.charPositionInLine;

		if (errorCharacterPosition == -1) {
			errorCharacterPosition = formulaString.length() - 1;
		}

		throw new RuntimeException(e);
	}

	public int getErrorCharacterPosition() {
		return errorCharacterPosition;
	}

	public static CalcGrammarParser getFormulaParser(String formulaString) {
		CharStream cs = new ANTLRStringStream(formulaString);
		CalcGrammarLexer lexer = new CalcGrammarLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcGrammarParser parser = new CalcGrammarParser(tokens);
		parser.setLexer(lexer);
		parser.formulaString = formulaString;
		return parser;
	}

	public void setLexer(CalcGrammarLexer lexer) {
		this.lexer = lexer;
	}

	public FormulaElement parseFormula() {
		FormulaElement parsedFormula = null;
		try {
			parsedFormula = formula();
		} catch (RuntimeException re) {
			if (lexer.getLexerError() != -1) {
				errorCharacterPosition = lexer.getLexerError();
				return null;
			}
			if (errorCharacterPosition != -1) {
				return null;
			}
		} catch (RecognitionException re) {
			return null;
		}

		return parsedFormula;
	}

	public void handleOperator(String operator, FormulaElement curElem, FormulaElement newElem) {
		//        System.out.println("handleOperator: operator="+operator + " curElem="+curElem.getValue() + " newElem="+newElem.getValue());
		if (curElem.getParent() == null) {
			new FormulaElement(FormulaElement.ELEMENT_OPERATOR, operator, null, curElem, newElem);
			System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
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

		System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
	}

	public String internalCommaSeperatedDouble(String value) {
		return value.replace(',', '.');
	}

	// $ANTLR start "formula"
	// src/CalcGrammar.g:149:1: formula returns [FormulaElement formulaTree] : term_list ;
	public final FormulaElement formula() throws RecognitionException {
		FormulaElement formulaTree = null;

		FormulaElement term_list1 = null;

		try {
			// src/CalcGrammar.g:149:45: ( term_list )
			// src/CalcGrammar.g:150:5: term_list
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
	// src/CalcGrammar.g:155:1: term_list returns [FormulaElement termListTree] : (firstTermTree= term ) ( operator loopTermTree= term )* ;
	public final FormulaElement term_list() throws RecognitionException {
		FormulaElement termListTree = null;

		FormulaElement firstTermTree = null;

		FormulaElement loopTermTree = null;

		String operator2 = null;

		try {
			// src/CalcGrammar.g:155:49: ( (firstTermTree= term ) ( operator loopTermTree= term )* )
			// src/CalcGrammar.g:156:5: (firstTermTree= term ) ( operator loopTermTree= term )*
			{

				FormulaElement curElem;

				// src/CalcGrammar.g:159:5: (firstTermTree= term )
				// src/CalcGrammar.g:159:6: firstTermTree= term
				{
					pushFollow(FOLLOW_term_in_term_list90);
					firstTermTree = term();

					state._fsp--;

					termListTree = firstTermTree;
					curElem = termListTree;

				}

				// src/CalcGrammar.g:165:5: ( operator loopTermTree= term )*
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if (((LA1_0 >= MINUS && LA1_0 <= MULOP) || LA1_0 == PLUS)) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// src/CalcGrammar.g:165:6: operator loopTermTree= term
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
	// src/CalcGrammar.g:172:1: term returns [FormulaElement termTree] : ( MINUS )? ( NUMBER | '(' term_list ')' | variableOrFunction ) ;
	public final FormulaElement term() throws RecognitionException {
		FormulaElement termTree = null;

		Token MINUS3 = null;
		Token NUMBER4 = null;
		FormulaElement term_list5 = null;

		FormulaElement variableOrFunction6 = null;

		try {
			// src/CalcGrammar.g:172:40: ( ( MINUS )? ( NUMBER | '(' term_list ')' | variableOrFunction ) )
			// src/CalcGrammar.g:173:5: ( MINUS )? ( NUMBER | '(' term_list ')' | variableOrFunction )
			{

				termTree = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, null, null, null);
				FormulaElement curElem = termTree;
				System.out.println("term enter");

				// src/CalcGrammar.g:178:5: ( MINUS )?
				int alt2 = 2;
				int LA2_0 = input.LA(1);

				if ((LA2_0 == MINUS)) {
					alt2 = 1;
				}
				switch (alt2) {
					case 1:
					// src/CalcGrammar.g:178:6: MINUS
					{
						MINUS3 = (Token) match(input, MINUS, FOLLOW_MINUS_in_term157);

						System.out.println("minus enter");
						curElem = new FormulaElement(FormulaElement.ELEMENT_VALUE, null, termTree, null, null);
						termTree.replaceElement(FormulaElement.ELEMENT_OPERATOR, MINUS3.getText(), null, curElem);

					}
						break;

				}

				// src/CalcGrammar.g:185:5: ( NUMBER | '(' term_list ')' | variableOrFunction )
				int alt3 = 3;
				switch (input.LA(1)) {
					case NUMBER: {
						alt3 = 1;
					}
						break;
					case 23: {
						alt3 = 2;
					}
						break;
					case CONSTANT:
					case ID:
					case SENSOR:
					case UPID: {
						alt3 = 3;
					}
						break;
					default:
						NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

						throw nvae;

				}

				switch (alt3) {
					case 1:
					// src/CalcGrammar.g:185:7: NUMBER
					{
						NUMBER4 = (Token) match(input, NUMBER, FOLLOW_NUMBER_in_term176);

						System.out.println("number enter");
						String number = internalCommaSeperatedDouble(NUMBER4.getText());
						curElem.replaceElement(FormulaElement.ELEMENT_VALUE, number);

					}
						break;
					case 2:
					// src/CalcGrammar.g:191:7: '(' term_list ')'
					{
						match(input, 23, FOLLOW_23_in_term194);

						pushFollow(FOLLOW_term_list_in_term196);
						term_list5 = term_list();

						state._fsp--;

						match(input, 24, FOLLOW_24_in_term198);

						System.out.println("brace enter");
						System.out.println("term_list: " + term_list5.getTreeString());
						curElem.replaceElement(term_list5);

					}
						break;
					case 3:
					// src/CalcGrammar.g:197:7: variableOrFunction
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
	// src/CalcGrammar.g:203:1: variableOrFunction returns [FormulaElement variableOrFunctionTree] : ( CONSTANT | ID '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' | SENSOR | UPID );
	public final FormulaElement variableOrFunction() throws RecognitionException {
		FormulaElement variableOrFunctionTree = null;

		Token CONSTANT7 = null;
		Token ID8 = null;
		Token SENSOR9 = null;
		Token UPID10 = null;
		FormulaElement leftChildTree = null;

		FormulaElement rightChildTree = null;

		try {
			// src/CalcGrammar.g:203:67: ( CONSTANT | ID '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')' | SENSOR | UPID )
			int alt6 = 4;
			switch (input.LA(1)) {
				case CONSTANT: {
					alt6 = 1;
				}
					break;
				case ID: {
					alt6 = 2;
				}
					break;
				case SENSOR: {
					alt6 = 3;
				}
					break;
				case UPID: {
					alt6 = 4;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

					throw nvae;

			}

			switch (alt6) {
				case 1:
				// src/CalcGrammar.g:204:7: CONSTANT
				{

					System.out.println("variableOrFunction enter");

					CONSTANT7 = (Token) match(input, CONSTANT, FOLLOW_CONSTANT_in_variableOrFunction259);

					variableOrFunctionTree = new FormulaElement(FormulaElement.ELEMENT_CONSTANT, CONSTANT7.getText(),
							null, null, null);

				}
					break;
				case 2:
				// src/CalcGrammar.g:213:9: ID '(' (leftChildTree= term_list ( ',' rightChildTree= term_list )? )? ')'
				{
					ID8 = (Token) match(input, ID, FOLLOW_ID_in_variableOrFunction290);

					FormulaElement leftChild = null;
					FormulaElement rightChild = null;

					match(input, 23, FOLLOW_23_in_variableOrFunction320);

					// src/CalcGrammar.g:218:17: (leftChildTree= term_list ( ',' rightChildTree= term_list )? )?
					int alt5 = 2;
					int LA5_0 = input.LA(1);

					if ((LA5_0 == CONSTANT || LA5_0 == ID || LA5_0 == MINUS || LA5_0 == NUMBER
							|| (LA5_0 >= SENSOR && LA5_0 <= UPID) || LA5_0 == 23)) {
						alt5 = 1;
					}
					switch (alt5) {
						case 1:
						// src/CalcGrammar.g:218:18: leftChildTree= term_list ( ',' rightChildTree= term_list )?
						{
							pushFollow(FOLLOW_term_list_in_variableOrFunction325);
							leftChildTree = term_list();

							state._fsp--;

							leftChild = leftChildTree;

							// src/CalcGrammar.g:222:21: ( ',' rightChildTree= term_list )?
							int alt4 = 2;
							int LA4_0 = input.LA(1);

							if ((LA4_0 == 25)) {
								alt4 = 1;
							}
							switch (alt4) {
								case 1:
								// src/CalcGrammar.g:222:22: ',' rightChildTree= term_list
								{
									match(input, 25, FOLLOW_25_in_variableOrFunction371);

									pushFollow(FOLLOW_term_list_in_variableOrFunction375);
									rightChildTree = term_list();

									state._fsp--;

									rightChild = rightChildTree;

								}
									break;

							}

						}
							break;

					}

					match(input, 24, FOLLOW_24_in_variableOrFunction445);

					variableOrFunctionTree = new FormulaElement(FormulaElement.ELEMENT_FUNCTION, ID8.getText(), null,
							leftChild, rightChild);

				}
					break;
				case 3:
				// src/CalcGrammar.g:232:11: SENSOR
				{
					SENSOR9 = (Token) match(input, SENSOR, FOLLOW_SENSOR_in_variableOrFunction472);

					variableOrFunctionTree = new FormulaElement(FormulaElement.ELEMENT_SENSOR, SENSOR9.getText(), null,
							null, null);

				}
					break;
				case 4:
				// src/CalcGrammar.g:236:11: UPID
				{
					UPID10 = (Token) match(input, UPID, FOLLOW_UPID_in_variableOrFunction498);

					variableOrFunctionTree = new FormulaElement(FormulaElement.ELEMENT_VARIABLE, UPID10.getText(),
							null, null, null);

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
	// src/CalcGrammar.g:242:1: operator returns [String operatorString] : ( MULOP | PLUS | MINUS );
	public final String operator() throws RecognitionException {
		String operatorString = null;

		Token MULOP11 = null;
		Token PLUS12 = null;
		Token MINUS13 = null;

		try {
			// src/CalcGrammar.g:242:41: ( MULOP | PLUS | MINUS )
			int alt7 = 3;
			switch (input.LA(1)) {
				case MULOP: {
					alt7 = 1;
				}
					break;
				case PLUS: {
					alt7 = 2;
				}
					break;
				case MINUS: {
					alt7 = 3;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

					throw nvae;

			}

			switch (alt7) {
				case 1:
				// src/CalcGrammar.g:243:5: MULOP
				{
					MULOP11 = (Token) match(input, MULOP, FOLLOW_MULOP_in_operator529);

					operatorString = MULOP11.getText();

				}
					break;
				case 2:
				// src/CalcGrammar.g:247:7: PLUS
				{
					PLUS12 = (Token) match(input, PLUS, FOLLOW_PLUS_in_operator548);

					operatorString = PLUS12.getText();

				}
					break;
				case 3:
				// src/CalcGrammar.g:251:7: MINUS
				{
					MINUS13 = (Token) match(input, MINUS, FOLLOW_MINUS_in_operator570);

					operatorString = MINUS13.getText();

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
	public static final BitSet FOLLOW_operator_in_term_list115 = new BitSet(new long[] { 0x0000000000989110L });
	public static final BitSet FOLLOW_term_in_term_list119 = new BitSet(new long[] { 0x0000000000023002L });
	public static final BitSet FOLLOW_MINUS_in_term157 = new BitSet(new long[] { 0x0000000000988110L });
	public static final BitSet FOLLOW_NUMBER_in_term176 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_23_in_term194 = new BitSet(new long[] { 0x0000000000989110L });
	public static final BitSet FOLLOW_term_list_in_term196 = new BitSet(new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_24_in_term198 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_variableOrFunction_in_term216 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_CONSTANT_in_variableOrFunction259 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_ID_in_variableOrFunction290 = new BitSet(new long[] { 0x0000000000800000L });
	public static final BitSet FOLLOW_23_in_variableOrFunction320 = new BitSet(new long[] { 0x0000000001989110L });
	public static final BitSet FOLLOW_term_list_in_variableOrFunction325 = new BitSet(
			new long[] { 0x0000000003000000L });
	public static final BitSet FOLLOW_25_in_variableOrFunction371 = new BitSet(new long[] { 0x0000000000989110L });
	public static final BitSet FOLLOW_term_list_in_variableOrFunction375 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_24_in_variableOrFunction445 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_SENSOR_in_variableOrFunction472 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_UPID_in_variableOrFunction498 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_MULOP_in_operator529 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_PLUS_in_operator548 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_MINUS_in_operator570 = new BitSet(new long[] { 0x0000000000000002L });

}