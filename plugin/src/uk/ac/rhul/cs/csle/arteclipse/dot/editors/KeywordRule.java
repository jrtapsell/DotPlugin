package uk.ac.rhul.cs.csle.arteclipse.dot.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

/**
 * @author James Tapsell
 */
public class KeywordRule implements IRule {
  private String keyword;
  private IToken matches;
  private static final IToken NO_MATCH = new IToken() {
    @Override
    public boolean isUndefined() {
      return true;
    }

    @Override
    public boolean isWhitespace() {
      return false;
    }

    @Override
    public boolean isEOF() {
      return false;
    }

    @Override
    public boolean isOther() {
      return false;
    }

    @Override
    public Object getData() {
      return null;
    }
  };

  public KeywordRule(String aTrue, IToken aBoolean) {
    keyword = aTrue;
    matches = aBoolean;
  }

  @Override
  public IToken evaluate(ICharacterScanner iCharacterScanner) {
    if (matches(iCharacterScanner, 0)) {
      return matches;
    }
    return NO_MATCH;
  }

  private boolean matches(ICharacterScanner iCharacterScanner, int i) {
    if (i == keyword.length()) {
      return true;
    }
    if (iCharacterScanner.read() == keyword.charAt(i)) {
      if (matches(iCharacterScanner, i + 1)) {
        return true;
      }
    }
    iCharacterScanner.unread();
    return false;
  }
}

