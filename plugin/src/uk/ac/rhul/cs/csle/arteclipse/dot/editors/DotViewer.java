package uk.ac.rhul.cs.csle.arteclipse.dot.editors;

import static uk.ac.rhul.cs.csle.arteclipse.dot.editors.DotParticipant.ContentTypes.BRACED;
import static uk.ac.rhul.cs.csle.arteclipse.dot.editors.DotParticipant.ContentTypes.OUTSIDE;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author James Tapsell
 */
public class DotViewer extends TextEditor {
  public DotViewer() {
    setSourceViewerConfiguration(new Svc());
  }

  private class Svc extends SourceViewerConfiguration {
    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
      final PresentationReconciler presentationReconciler = new PresentationReconciler();
      presentationReconciler.setDocumentPartitioning("PARTITIONING");
      makeBraced(presentationReconciler);
      makeUnbraced(presentationReconciler);
      return presentationReconciler;
    }

    private void makeUnbraced(PresentationReconciler presentationReconciler) {
      setRules(presentationReconciler, new IRule[] {
          new PatternRule("\"", "\"", makeToken(200, 0, 200), (char) -1, true, true),
          new KeywordRule("->", makeToken(0, 200, 0)),
          new WordRule(new MyIWordDetector(), makeToken(0, 100, 100))
      }, OUTSIDE.name());
    }

    private void setRules(PresentationReconciler presentationReconciler, IRule[] rules, String name) {
      final RuleBasedScanner rbs = new RuleBasedScanner();
      rbs.setRules(rules);
      attach(presentationReconciler, rbs, name);
    }

    private void attach(PresentationReconciler presentationReconciler, RuleBasedScanner rbs, String name) {
      DefaultDamagerRepairer ddr = new MyDDR(rbs);
      presentationReconciler.setDamager(ddr, name);
      presentationReconciler.setRepairer(ddr, name);
    }

    private void makeBraced(PresentationReconciler presentationReconciler) {
      setRules(presentationReconciler, new IRule[] {
          new PatternRule("\"", "\"", makeToken(200, 0, 0), (char) -1, true, true),
          new NumberRule(makeToken(200, 200, 0)),
          new KeywordRule("=", makeToken(100, 0, 100)),
          new WordRule(new MyIWordDetector(), makeToken(100, 100, 0))
      }, BRACED.name());
    }

    private class MyIWordDetector implements IWordDetector {
      @Override
      public boolean isWordStart(char c) {
        return Character.isJavaIdentifierStart(c);
      }

      @Override
      public boolean isWordPart(char c) {
        return Character.isJavaIdentifierPart(c);
      }
    }
  }

  public IDocument getDocument() {
    return getSourceViewer().getDocument();
  }


  private static IToken makeToken(int i, int i1, int i2) {
    return new Token(new TextAttribute(new Color(Display.getDefault(), i, i1, i2)));
  }
}
