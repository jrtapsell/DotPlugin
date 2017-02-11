package uk.ac.rhul.cs.csle.arteclipse.dot.editors;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;

/**
 * @author James Tapsell
 */
public class MyDDR extends DefaultDamagerRepairer {
  public MyDDR(RuleBasedScanner rbs) {
    super(rbs);
  }

  @Override
  public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e, boolean documentPartitioningChanged) {
   if (e.getDocument().getLength() == 0) {
     return super.getDamageRegion(partition, e, documentPartitioningChanged);
   }
    return new Region(0, e.getDocument().getLength());
  }
}
