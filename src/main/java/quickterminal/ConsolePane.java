package quickterminal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

import hitonoriol.methan0l.ide.Bindings;

public class ConsolePane extends JPanel implements CommandListener, Terminal {
	private static final long serialVersionUID = 8965534740814016152L;
	private JTextArea textArea;
	private int userInputStart = 0;
	private Command cmd;

	public ConsolePane(String cmdStr) {
		cmd = new Command(this);
		cmd.execute(cmdStr);

		setLayout(new BorderLayout());
		textArea = new JTextArea(25, 100);
		Bindings.setupFontScaling(textArea);
		((AbstractDocument) textArea.getDocument()).setDocumentFilter(new ProtectedDocumentFilter(this));
		add(new JScrollPane(textArea));

		ActionMap am = textArea.getActionMap();
		Action oldAction = am.get("insert-break");
		am.put("insert-break", new AbstractAction() {
			private static final long serialVersionUID = 1322810617331608502L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int caretPos = textArea.getCaretPosition();
					int lineEnd = textArea.getLineEndOffset(textArea.getLineOfOffset(caretPos));
					int range = lineEnd - userInputStart;
					String text = textArea.getText(userInputStart, range);
					System.out.printf("User input: `%s`\n", text);

					userInputStart = lineEnd;
					if (!cmd.isRunning()) {
						return;
					} else {
						try {
							cmd.send(text + System.lineSeparator());
						} catch (IOException ex) {
							printError("Failed to send command to process: " + ex.getMessage());
							ex.printStackTrace();
						}
					}
				} catch (BadLocationException ex) {
					printError("Bad console caret location");
					ex.printStackTrace();
				}
				resetCaret();
				oldAction.actionPerformed(e);
			}
		});

	}

	@Override
	public void commandOutput(String text) {
		SwingUtilities.invokeLater(() -> appendText(text));
	}

	@Override
	public void commandFailed(Exception exp) {
		SwingUtilities.invokeLater(
				() -> printError("Something broke when passing your input to the process: " + exp.getMessage()));
	}

	@Override
	public void commandCompleted(String cmd, int result) {
		if (result == 0)
			return;

		appendText("\n[Program exited with code " + result + "]\n\n");
	}

	private void resetCaret() {
		textArea.setCaretPosition(textArea.getText().length());
	}

	protected void updateUserInputPos() {
		int pos = textArea.getCaretPosition();
		resetCaret();
		userInputStart = pos;
	}

	@Override
	public int getUserInputStart() {
		return userInputStart;
	}

	private void printError(String text) {
		appendText("[IDE error] " + text + System.lineSeparator());
	}

	@Override
	public void appendText(String text) {
		textArea.append(text);
		updateUserInputPos();
	}

	Command getCommand() {
		return cmd;
	}
}