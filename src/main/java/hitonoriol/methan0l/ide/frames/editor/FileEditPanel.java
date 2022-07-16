package hitonoriol.methan0l.ide.frames.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import hitonoriol.methan0l.ide.lang.Methan0lTokenMaker;

public class FileEditPanel extends JPanel {
	private EditorWindow window;
	private SourceFile file;

	private RTextScrollPane scrollPane;
	private RSyntaxTextArea textArea;

	private boolean modified = false;

	public FileEditPanel(EditorWindow window, SourceFile file) {
		this.window = window;
		this.file = file;
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);

		if (file.isValid())
			readFile();

		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				modify();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				modify();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}

	private RTextScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new RTextScrollPane(getTextArea());
		}
		return scrollPane;
	}

	private RSyntaxTextArea getTextArea() {
		if (textArea == null) {
			textArea = new RSyntaxTextArea();
			textArea.setSyntaxEditingStyle(Methan0lTokenMaker.STYLE_NAME);
		}
		return textArea;
	}

	private void modify() {
		if (window != null)
			window.textModified(true);
	}

	public SourceFile getFile() {
		return file;
	}

	public String getText() {
		return textArea.getText();
	}

	void readFile() {
		String text = file.read();
		if (text != null)
			textArea.setText(text);
	}

	public boolean modified() {
		return modified;
	}

	public void setModified(boolean value) {
		modified = value;
	}

	private static final long serialVersionUID = -2397959436359193919L;
}
