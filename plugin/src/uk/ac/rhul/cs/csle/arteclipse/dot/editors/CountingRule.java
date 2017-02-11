package uk.ac.rhul.cs.csle.arteclipse.dot.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import uk.ac.rhul.cs.csle.arteclipse.dot.editors.SpecialToken.Type;

/**
 * @author James Tapsell
 */
public class CountingRule implements IPredicateRule {

  private static final IToken INVALID_TOKEN = new SpecialToken(Type.UNDEFINED);
  private final IToken success;
  private final char increment;
  private final char decrement;

  public CountingRule(char increment, char decrement, Token token) {
    success = token;
    this.increment = increment;
    this.decrement = decrement;
  }

  @Override
  public IToken getSuccessToken() {
    return success;
  }

  @Override
  public IToken evaluate(ICharacterScanner iCharacterScanner, boolean resume) {
    if (resume) {
      throw new AssertionError("EXXX");
    }
    return evaluate(iCharacterScanner);
  }

  @Override
  public IToken evaluate(ICharacterScanner iCharacterScanner) {
    if (iCharacterScanner.read() != increment) {
      iCharacterScanner.unread();
      return INVALID_TOKEN;
    }
    int depth = 1;
    int current;
    while ((current = iCharacterScanner.read()) != -1) {
      char c = (char) current;
      if (c == increment) {
        depth++;
      } else if (c == decrement) {
        depth--;
        if (depth == 0) {
          return success;
        }
      }
    }
    return INVALID_TOKEN;
  }

}
