package hitonoriol.methan0l.ide.frames.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import hitonoriol.methan0l.ide.lang.Methan0lTokenMaker;

public class FileEditPanel extends JPanel {
	private SourceFile file;

	private RTextScrollPane scrollPane;
	private RSyntaxTextArea textArea;

	public FileEditPanel(SourceFile file) {
		this.file = file;
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);

		if (file.isValid())
			readFile();
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

	private static final long serialVersionUID = -2397959436359193919L;
}
