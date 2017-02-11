package uk.ac.rhul.cs.csle.arteclipse.dot.editors;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import uk.ac.rhul.cs.csle.arteclipse.dot.editors.SpecialToken.Type;

public class DotParticipant implements IDocumentSetupParticipant {

	public enum ContentTypes {
		BRACED,
		OUTSIDE;

		private final Token token;

		ContentTypes() {
			token = new Token(name());
		}

		public static String[] getTypes() {
			final ContentTypes[] data = values();
			String[] ret = new String[data.length];
			for (int i = 0; i < data.length; i++) {
				ret[i] = data[i].name();
			}
			return ret;
		}

		public Token getToken() {
			return token;
		}
	}

	@Override
	public void setup(IDocument document) {
		final RuleBasedPartitionScanner scanner = new RuleBasedPartitionScanner();
		scanner.setPredicateRules(new IPredicateRule[]{
				new CountingRule('[', ']', ContentTypes.BRACED.getToken()),
				new IPredicateRule() {
					@Override
					public IToken getSuccessToken() {
						return ContentTypes.OUTSIDE.getToken();
					}

					@Override
					public IToken evaluate(ICharacterScanner iCharacterScanner, boolean b) {
						return evaluate(iCharacterScanner);
					}

					@Override
					public IToken evaluate(ICharacterScanner iCharacterScanner) {
						if (iCharacterScanner.read() == '[') {
							iCharacterScanner.unread();
							return new SpecialToken(Type.UNDEFINED);
						}
						int current;
						int read = 0;
						while ((current = iCharacterScanner.read()) != -1) {
							read++;
							if (current == '[') {
								iCharacterScanner.unread();
								return ContentTypes.OUTSIDE.getToken();
							}
						}
						if (read == 0) {
							return new SpecialToken(Type.EOF);
						}
						return ContentTypes.OUTSIDE.getToken();
					}
				}
		});
		scanner.setDefaultReturnToken(new Token(ContentTypes.OUTSIDE.name()));
		final FastPartitioner partitioner = new FastPartitioner(scanner, ContentTypes.getTypes());
		partitioner.connect(document);
		IDocumentExtension3 idex3 = (IDocumentExtension3) document;
		idex3.setDocumentPartitioner("PARTITIONING", partitioner);
	}

}
