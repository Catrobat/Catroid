// $ANTLR 3.4 src/CalcGrammar.g 2012-07-18 19:01:53
package at.tugraz.ist.catroid.formulaeditor;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;

@SuppressWarnings({ "all", "warnings", "unchecked" })
public class CalcGrammarLexer extends Lexer {
	public static final int EOF = -1;
	public static final int T__21 = 21;
	public static final int T__22 = 22;
	public static final int T__23 = 23;
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
	public static final int WS = 20;

	private int lexerErrorCount = 0;
	private List<String> lexerErrorMessages = null;

	@Override
	public void reportError(RecognitionException e) {
		lexerErrorCount++;
		if (lexerErrorMessages == null) {
			lexerErrorMessages = new ArrayList<String>();
		}

		NoViableAltException ex = (NoViableAltException) e;

		lexerErrorMessages.add("\t#" + lexerErrorCount + ": line " + this.state.tokenStartLine + ":"
				+ this.state.tokenStartCharPositionInLine + " no viable " + "alternative at character '"
				+ this.input.substring(ex.index, ex.index) + "'");
	}

	public int getLexerErrorCount() {
		return this.lexerErrorCount;
	}

	public List<String> getLexerErrorMessages() {
		return this.lexerErrorMessages;
	}

	@Override
	public Token nextToken() {
		Token token = super.nextToken();
		if (token.getText().equals("<EOF>")) {
			return Token.EOF_TOKEN;
		}
		return token;
	}

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public CalcGrammarLexer() {
	}

	public CalcGrammarLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public CalcGrammarLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public String getGrammarFileName() {
		return "src/CalcGrammar.g";
	}

	// $ANTLR start "T__21"
	public final void mT__21() throws RecognitionException {
		try {
			int _type = T__21;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:40:7: ( '(' )
			// src/CalcGrammar.g:40:9: '('
			{
				match('(');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__21"

	// $ANTLR start "T__22"
	public final void mT__22() throws RecognitionException {
		try {
			int _type = T__22;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:41:7: ( ')' )
			// src/CalcGrammar.g:41:9: ')'
			{
				match(')');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__22"

	// $ANTLR start "T__23"
	public final void mT__23() throws RecognitionException {
		try {
			int _type = T__23;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:42:7: ( ',' )
			// src/CalcGrammar.g:42:9: ','
			{
				match(',');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__23"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:260:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
			// src/CalcGrammar.g:260:7: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
			{
				if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || (input.LA(1) >= '\f' && input.LA(1) <= '\r')
						|| input.LA(1) == ' ') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

				_channel = HIDDEN;

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "WS"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:263:5: ( '/*' ( . )* '*/' )
			// src/CalcGrammar.g:263:7: '/*' ( . )* '*/'
			{
				match("/*");

				// src/CalcGrammar.g:263:12: ( . )*
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if ((LA1_0 == '*')) {
						int LA1_1 = input.LA(2);

						if ((LA1_1 == '/')) {
							alt1 = 2;
						} else if (((LA1_1 >= '\u0000' && LA1_1 <= '.') || (LA1_1 >= '0' && LA1_1 <= '\uFFFF'))) {
							alt1 = 1;
						}

					} else if (((LA1_0 >= '\u0000' && LA1_0 <= ')') || (LA1_0 >= '+' && LA1_0 <= '\uFFFF'))) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// src/CalcGrammar.g:263:12: .
						{
							matchAny();

						}
							break;

						default:
							break loop1;
					}
				} while (true);

				match("*/");

				_channel = HIDDEN;

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "COMMENT"

	// $ANTLR start "CONSTANT"
	public final void mCONSTANT() throws RecognitionException {
		try {
			int _type = CONSTANT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:267:10: ( 'pi' | 'e' )
			int alt2 = 2;
			int LA2_0 = input.LA(1);

			if ((LA2_0 == 'p')) {
				alt2 = 1;
			} else if ((LA2_0 == 'e')) {
				alt2 = 2;
			} else {
				NoViableAltException nvae = new NoViableAltException("", 2, 0, input);

				throw nvae;

			}
			switch (alt2) {
				case 1:
				// src/CalcGrammar.g:267:12: 'pi'
				{
					match("pi");

				}
					break;
				case 2:
				// src/CalcGrammar.g:267:17: 'e'
				{
					match('e');

				}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "CONSTANT"

	// $ANTLR start "LT"
	public final void mLT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:270:13: ( '<' )
			// src/CalcGrammar.g:270:15: '<'
			{
				match('<');

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "LT"

	// $ANTLR start "GT"
	public final void mGT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:271:13: ( '>' )
			// src/CalcGrammar.g:271:15: '>'
			{
				match('>');

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "GT"

	// $ANTLR start "RELOP"
	public final void mRELOP() throws RecognitionException {
		try {
			int _type = RELOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:272:10: ( ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) ) )
			// src/CalcGrammar.g:272:12: ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) )
			{
				// src/CalcGrammar.g:272:12: ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) )
				int alt3 = 5;
				switch (input.LA(1)) {
					case '<': {
						int LA3_1 = input.LA(2);

						if ((LA3_1 == '=')) {
							alt3 = 2;
						} else {
							alt3 = 1;
						}
					}
						break;
					case '>': {
						int LA3_2 = input.LA(2);

						if ((LA3_2 == '=')) {
							alt3 = 4;
						} else {
							alt3 = 3;
						}
					}
						break;
					case '=': {
						alt3 = 5;
					}
						break;
					default:
						NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

						throw nvae;

				}

				switch (alt3) {
					case 1:
					// src/CalcGrammar.g:272:13: LT
					{
						mLT();

					}
						break;
					case 2:
					// src/CalcGrammar.g:272:16: ( LT '=' )
					{
						// src/CalcGrammar.g:272:16: ( LT '=' )
						// src/CalcGrammar.g:272:17: LT '='
						{
							mLT();

							match('=');

						}

					}
						break;
					case 3:
					// src/CalcGrammar.g:272:24: GT
					{
						mGT();

					}
						break;
					case 4:
					// src/CalcGrammar.g:272:27: ( GT '=' )
					{
						// src/CalcGrammar.g:272:27: ( GT '=' )
						// src/CalcGrammar.g:272:28: GT '='
						{
							mGT();

							match('=');

						}

					}
						break;
					case 5:
					// src/CalcGrammar.g:272:35: ( '=' )
					{
						// src/CalcGrammar.g:272:35: ( '=' )
						// src/CalcGrammar.g:272:36: '='
						{
							match('=');

						}

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "RELOP"

	// $ANTLR start "MULOP"
	public final void mMULOP() throws RecognitionException {
		try {
			int _type = MULOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:273:10: ( '*' | '/' | '%' )
			// src/CalcGrammar.g:
			{
				if (input.LA(1) == '%' || input.LA(1) == '*' || input.LA(1) == '/') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "MULOP"

	// $ANTLR start "PLUS"
	public final void mPLUS() throws RecognitionException {
		try {
			int _type = PLUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:274:10: ( '+' )
			// src/CalcGrammar.g:274:12: '+'
			{
				match('+');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "PLUS"

	// $ANTLR start "MINUS"
	public final void mMINUS() throws RecognitionException {
		try {
			int _type = MINUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:275:10: ( '-' )
			// src/CalcGrammar.g:275:12: '-'
			{
				match('-');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "MINUS"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:276:10: ( '|' )
			// src/CalcGrammar.g:276:12: '|'
			{
				match('|');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "OR"

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:277:10: ( '!' )
			// src/CalcGrammar.g:277:12: '!'
			{
				match('!');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "NOT"

	// $ANTLR start "LAND"
	public final void mLAND() throws RecognitionException {
		try {
			int _type = LAND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:278:10: ( '&' )
			// src/CalcGrammar.g:278:12: '&'
			{
				match('&');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "LAND"

	// $ANTLR start "NUMBER"
	public final void mNUMBER() throws RecognitionException {
		try {
			int _type = NUMBER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:281:11: ( DECINT )
			// src/CalcGrammar.g:281:13: DECINT
			{
				mDECINT();

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "NUMBER"

	// $ANTLR start "DECINT"
	public final void mDECINT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:282:17: ( ( DIGIT )+ ( ( '.' | ',' ) ( DIGIT )+ )? )
			// src/CalcGrammar.g:282:19: ( DIGIT )+ ( ( '.' | ',' ) ( DIGIT )+ )?
			{
				// src/CalcGrammar.g:282:19: ( DIGIT )+
				int cnt4 = 0;
				loop4: do {
					int alt4 = 2;
					int LA4_0 = input.LA(1);

					if (((LA4_0 >= '0' && LA4_0 <= '9'))) {
						alt4 = 1;
					}

					switch (alt4) {
						case 1:
						// src/CalcGrammar.g:
						{
							if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
								input.consume();
							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							if (cnt4 >= 1) {
								break loop4;
							}
							EarlyExitException eee = new EarlyExitException(4, input);
							throw eee;
					}
					cnt4++;
				} while (true);

				// src/CalcGrammar.g:282:28: ( ( '.' | ',' ) ( DIGIT )+ )?
				int alt6 = 2;
				int LA6_0 = input.LA(1);

				if ((LA6_0 == ',' || LA6_0 == '.')) {
					alt6 = 1;
				}
				switch (alt6) {
					case 1:
					// src/CalcGrammar.g:282:29: ( '.' | ',' ) ( DIGIT )+
					{
						if (input.LA(1) == ',' || input.LA(1) == '.') {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}

						// src/CalcGrammar.g:282:39: ( DIGIT )+
						int cnt5 = 0;
						loop5: do {
							int alt5 = 2;
							int LA5_0 = input.LA(1);

							if (((LA5_0 >= '0' && LA5_0 <= '9'))) {
								alt5 = 1;
							}

							switch (alt5) {
								case 1:
								// src/CalcGrammar.g:
								{
									if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
										input.consume();
									} else {
										MismatchedSetException mse = new MismatchedSetException(null, input);
										recover(mse);
										throw mse;
									}

								}
									break;

								default:
									if (cnt5 >= 1) {
										break loop5;
									}
									EarlyExitException eee = new EarlyExitException(5, input);
									throw eee;
							}
							cnt5++;
						} while (true);

					}
						break;

				}

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "DECINT"

	// $ANTLR start "DIGIT"
	public final void mDIGIT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:283:19: ( '0' .. '9' )
			// src/CalcGrammar.g:
			{
				if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "DIGIT"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:286:11: ( LETTER ( LETTER | DIGIT | '_' )* )
			// src/CalcGrammar.g:286:15: LETTER ( LETTER | DIGIT | '_' )*
			{
				mLETTER();

				// src/CalcGrammar.g:286:21: ( LETTER | DIGIT | '_' )*
				loop7: do {
					int alt7 = 2;
					int LA7_0 = input.LA(1);

					if (((LA7_0 >= '0' && LA7_0 <= '9') || LA7_0 == '_' || (LA7_0 >= 'a' && LA7_0 <= 'z'))) {
						alt7 = 1;
					}

					switch (alt7) {
						case 1:
						// src/CalcGrammar.g:
						{
							if ((input.LA(1) >= '0' && input.LA(1) <= '9') || input.LA(1) == '_'
									|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
								input.consume();
							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop7;
					}
				} while (true);

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "ID"

	// $ANTLR start "LETTER"
	public final void mLETTER() throws RecognitionException {
		try {
			// src/CalcGrammar.g:287:20: ( ( 'a' .. 'z' ) )
			// src/CalcGrammar.g:
			{
				if ((input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "LETTER"

	public void mTokens() throws RecognitionException {
		// src/CalcGrammar.g:1:8: ( T__21 | T__22 | T__23 | WS | COMMENT | CONSTANT | RELOP | MULOP | PLUS | MINUS | OR | NOT | LAND | NUMBER | ID )
		int alt8 = 15;
		switch (input.LA(1)) {
			case '(': {
				alt8 = 1;
			}
				break;
			case ')': {
				alt8 = 2;
			}
				break;
			case ',': {
				alt8 = 3;
			}
				break;
			case '\t':
			case '\n':
			case '\f':
			case '\r':
			case ' ': {
				alt8 = 4;
			}
				break;
			case '/': {
				int LA8_5 = input.LA(2);

				if ((LA8_5 == '*')) {
					alt8 = 5;
				} else {
					alt8 = 8;
				}
			}
				break;
			case 'p': {
				int LA8_6 = input.LA(2);

				if ((LA8_6 == 'i')) {
					int LA8_18 = input.LA(3);

					if (((LA8_18 >= '0' && LA8_18 <= '9') || LA8_18 == '_' || (LA8_18 >= 'a' && LA8_18 <= 'z'))) {
						alt8 = 15;
					} else {
						alt8 = 6;
					}
				} else {
					alt8 = 15;
				}
			}
				break;
			case 'e': {
				int LA8_7 = input.LA(2);

				if (((LA8_7 >= '0' && LA8_7 <= '9') || LA8_7 == '_' || (LA8_7 >= 'a' && LA8_7 <= 'z'))) {
					alt8 = 15;
				} else {
					alt8 = 6;
				}
			}
				break;
			case '<':
			case '=':
			case '>': {
				alt8 = 7;
			}
				break;
			case '%':
			case '*': {
				alt8 = 8;
			}
				break;
			case '+': {
				alt8 = 9;
			}
				break;
			case '-': {
				alt8 = 10;
			}
				break;
			case '|': {
				alt8 = 11;
			}
				break;
			case '!': {
				alt8 = 12;
			}
				break;
			case '&': {
				alt8 = 13;
			}
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9': {
				alt8 = 14;
			}
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z': {
				alt8 = 15;
			}
				break;
			default:
				NoViableAltException nvae = new NoViableAltException("", 8, 0, input);

				throw nvae;

		}

		switch (alt8) {
			case 1:
			// src/CalcGrammar.g:1:10: T__21
			{
				mT__21();

			}
				break;
			case 2:
			// src/CalcGrammar.g:1:16: T__22
			{
				mT__22();

			}
				break;
			case 3:
			// src/CalcGrammar.g:1:22: T__23
			{
				mT__23();

			}
				break;
			case 4:
			// src/CalcGrammar.g:1:28: WS
			{
				mWS();

			}
				break;
			case 5:
			// src/CalcGrammar.g:1:31: COMMENT
			{
				mCOMMENT();

			}
				break;
			case 6:
			// src/CalcGrammar.g:1:39: CONSTANT
			{
				mCONSTANT();

			}
				break;
			case 7:
			// src/CalcGrammar.g:1:48: RELOP
			{
				mRELOP();

			}
				break;
			case 8:
			// src/CalcGrammar.g:1:54: MULOP
			{
				mMULOP();

			}
				break;
			case 9:
			// src/CalcGrammar.g:1:60: PLUS
			{
				mPLUS();

			}
				break;
			case 10:
			// src/CalcGrammar.g:1:65: MINUS
			{
				mMINUS();

			}
				break;
			case 11:
			// src/CalcGrammar.g:1:71: OR
			{
				mOR();

			}
				break;
			case 12:
			// src/CalcGrammar.g:1:74: NOT
			{
				mNOT();

			}
				break;
			case 13:
			// src/CalcGrammar.g:1:78: LAND
			{
				mLAND();

			}
				break;
			case 14:
			// src/CalcGrammar.g:1:83: NUMBER
			{
				mNUMBER();

			}
				break;
			case 15:
			// src/CalcGrammar.g:1:90: ID
			{
				mID();

			}
				break;

		}

	}

}