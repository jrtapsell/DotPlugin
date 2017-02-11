package uk.ac.rhul.cs.csle.arteclipse.dot.editors;

import org.eclipse.jface.text.rules.IToken;

/**
 * @author James Tapsell
 */
class SpecialToken implements IToken {
  public enum Type {
    UNDEFINED, WHITESPACE, EOF, OTHER;
  }

  public final Type specialToken;

  public SpecialToken(Type type) {
    specialToken = type;
  }

  @Override
  public boolean isUndefined() {
    return specialToken == Type.UNDEFINED;
  }

  @Override
  public boolean isWhitespace() {
    return specialToken == Type.WHITESPACE;
  }

  @Override
  public boolean isEOF() {
    return specialToken == Type.EOF;
  }

  @Override
  public boolean isOther() {
    return specialToken == Type.OTHER;
  }

  @Override
  public Object getData() {
    return null;
  }
}
