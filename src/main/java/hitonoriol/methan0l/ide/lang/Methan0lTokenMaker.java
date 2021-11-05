package hitonoriol.methan0l.ide.lang;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

public class Methan0lTokenMaker extends AbstractTokenMaker {
	public static final String STYLE_NAME = "text/Methan0l";
	private int currentTokenStart;
	private int currentTokenType;

	@Override
	public TokenMap getWordsToHighlight() {
		TokenMap tokenMap = new TokenMap();
		tokenMap.put("if", Token.RESERVED_WORD);
		tokenMap.put("do", Token.RESERVED_WORD);
		tokenMap.put("class", Token.RESERVED_WORD);
		tokenMap.put("box", Token.RESERVED_WORD);
		tokenMap.put("typeid", Token.RESERVED_WORD);
		tokenMap.put("%%", Token.RESERVED_WORD);

		return tokenMap;
	}

	@Override
	public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
		// This assumes all keywords, etc. were parsed as "identifiers."
		if (tokenType == Token.IDENTIFIER) {
			int value = wordsToHighlight.get(segment, start, end);
			if (value != -1)
				tokenType = value;
		}
		super.addToken(segment, start, end, tokenType, startOffset);
	}

	@Override
	public Token getTokenList(Segment text, int startTokenType, int startOffset) {
		resetTokenList();

		char[] array = text.array;
		int offset = text.offset;
		int count = text.count;
		int end = offset + count;

		// Token starting offsets are always of the form:
		// 'startOffset + (currentTokenStart-offset)', but since startOffset and
		// offset are constant, tokens' starting positions become:
		// 'newStartOffset+currentTokenStart'.
		int newStartOffset = startOffset - offset;

		currentTokenStart = offset;
		currentTokenType = startTokenType;

		for (int i = offset; i < end; i++) {
			char c = array[i];

			switch (currentTokenType) {

			case Token.NULL:
				currentTokenStart = i;

				switch (c) {

				case ' ':
				case '\t':
					currentTokenType = Token.WHITESPACE;
					break;

				case '"':
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

				default:
					if (RSyntaxUtilities.isDigit(c)) {
						currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
						break;
					} else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
						currentTokenType = Token.IDENTIFIER;
						break;
					}

					currentTokenType = Token.IDENTIFIER;
					break;

				}
				break;

			case Token.WHITESPACE:
				switch (c) {
				case ' ':
				case '\t':
					break;

				case '"':
					addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

				case '#':
					addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.COMMENT_EOL;
					break;

				default:
					addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
					currentTokenStart = i;

					if (RSyntaxUtilities.isDigit(c)) {
						currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
						break;
					} else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
						currentTokenType = Token.IDENTIFIER;
						break;
					}

					currentTokenType = Token.IDENTIFIER;

				}
				break;

			default:
			case Token.IDENTIFIER:
				switch (c) {
				case ' ':
				case '\t':
					addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.WHITESPACE;
					break;

				case '"':
					addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

				default:
					if (RSyntaxUtilities.isLetterOrDigit(c) || c == '/' || c == '_') {
						break;
					}
				}
				break;

			case Token.LITERAL_NUMBER_DECIMAL_INT:
				switch (c) {
				case ' ':
				case '\t':
					addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
							newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.WHITESPACE;
					break;

				case '"':
					addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
							newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

				default:
					/* Still a literal number */
					if (RSyntaxUtilities.isDigit(c)) 
						break;
					

					/* Otherwise, remember this was a number and start over */
					addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
							newStartOffset + currentTokenStart);
					i--;
					currentTokenType = Token.NULL;

				}
				break;

			case Token.COMMENT_EOL:
				i = end - 1;
				addToken(text, currentTokenStart, i, currentTokenType, newStartOffset + currentTokenStart);
				// We need to set token type to null so at the bottom we don't add one more
				// token.
				currentTokenType = Token.NULL;
				break;

			case Token.LITERAL_STRING_DOUBLE_QUOTE:
				if (c == '"') {
					addToken(text, currentTokenStart, i, Token.LITERAL_STRING_DOUBLE_QUOTE,
							newStartOffset + currentTokenStart);
					currentTokenType = Token.NULL;
				}
				break;
			}
		}

		switch (currentTokenType) {
		// Remember what token type to begin the next line with.
		case Token.LITERAL_STRING_DOUBLE_QUOTE:
			addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
			break;

		// Do nothing if everything was okay.
		case Token.NULL:
			addNullToken();
			break;

		// All other token types don't continue to the next line...
		default:
			addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
			addNullToken();

		}

		// Return the first token in our linked list.
		return firstToken;

	}
}
