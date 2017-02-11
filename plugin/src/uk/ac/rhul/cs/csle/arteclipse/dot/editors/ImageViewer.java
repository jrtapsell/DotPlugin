package uk.ac.rhul.cs.csle.arteclipse.dot.editors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.xtext.util.StringInputStream;
import uk.ac.rhul.cs.csle.arteclipse.dot.Activator;

/**
 * @author James Tapsell
 */
public class ImageViewer {

  public static final String DOT_EXECUTABLE = "dot";
  public static final Display DEFAULT = Display.getDefault();
  private final Browser browser;
  private final DotViewer editor;

  public ImageViewer(Composite parent, DotViewer editor) {
    browser = new Browser(parent, SWT.NORMAL);
    System.out.println("USING BROWSER: "  + browser.getBrowserType());
    IDocument document = editor.getDocument();
    this.editor = editor;
    document.addDocumentListener(new IDocumentListener() {
      @Override
      public void documentAboutToBeChanged(DocumentEvent documentEvent) {}

      @Override
      public void documentChanged(DocumentEvent documentEvent) {
        attemptRender(document);
      }
    });
    attemptRender(document);
  }

  private IFile file() {
    final IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
    return editorInput.getFile();
  }

  private void attemptRender(IDocument document) {
    browser.setText("<h1>Rendering...</h1>");
    editor.doSave(null);
    new UpdateJob().schedule();
  }

  private IStatus job() throws IOException, InterruptedException, CoreException {
    IFile dotFile = file();
    ProcessBuilder pb = new ProcessBuilder(DOT_EXECUTABLE, "-Tsvg", dotFile.getRawLocation().toOSString());
    return runAndDisplay(pb);
  }

  public class InputBuffer {
    StringBuilder sb = new StringBuilder();
    private final Thread t;

    public InputBuffer(InputStream is) {
      Scanner s = new Scanner(is);
      t = new Thread(() -> {
        while (s.hasNextLine()) {
          sb.append(s.nextLine());
        }
      });
      t.start();
    }

    public void waitFor() throws InterruptedException {
      t.join();
    }
    public String getString() {
      return sb.toString();
    }

  }

  private IStatus runAndDisplay(final ProcessBuilder pb) throws IOException, InterruptedException, CoreException {
    final Process p = pb.start();
    final InputBuffer out = new InputBuffer(p.getInputStream());
    final InputBuffer err = new InputBuffer(p.getErrorStream());
    final int returnCode = p.waitFor();
    final String outString = out.getString();
    final String errString = err.getString();
    if(returnCode == 0) {
      final String text = getHTML(outString);
      //logHTML(text);
      setText(text);
      return Status.OK_STATUS;
    } else {
      final String message = String.format(
          "Dot returned error code %d<br>" +
          "=-=-=-=-=--=-<br>" +
          "%s",
          returnCode,
          errString);
      return new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
    }
  }

  private void logHTML(String text) throws CoreException{
    IFile file = file();
    IPath path = file.getProjectRelativePath().addFileExtension("html");
    IFile htmlFile = file.getProject().getFile(path);
    if (htmlFile.exists()) {
      htmlFile.delete(true, false, null);
    }
    final StringInputStream sis = new StringInputStream(text);
    htmlFile.create(sis, true, null);
  }

  private String getHTML(String svg) {
    return String.format("<div style='width:98vw;height:98vh;overflow:scroll'><svg style='width:100%%;height:100%%'>%s</svg></div>", svg);
  }

  private void setText(final String text) {
    DEFAULT.syncExec(() -> browser.setText(text, true));
  }

  public Control getControl() {
    return browser;
  }

  private class UpdateJob extends Job {
    private UpdateJob() {
      super("Update Dot View");
    }

    @Override
    protected IStatus run(final IProgressMonitor iProgressMonitor) {
      IStatus ret;
      try {
        ret = job();
       } catch (final Throwable ex) {
        ret = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not draw dot", ex);
      }
      if (ret.getSeverity() != IStatus.OK) {
        handleError(ret);
      }
      if (ret.getSeverity() == IStatus.ERROR) {
        return ret;
      } else {
        return new Status(IStatus.OK, Activator.PLUGIN_ID, ret.getMessage(), ret.getException());
      }
    }

    private void handleError(final IStatus ret) {
      final String message = ret.getMessage();
      setText(String.format("<span style='color:red'>%s</h2>", message));
    }
  }
}
