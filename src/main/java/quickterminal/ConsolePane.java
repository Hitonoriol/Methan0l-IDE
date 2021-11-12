package quickterminal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

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
		((AbstractDocument) textArea.getDocument()).setDocumentFilter(new ProtectedDocumentFilter(this));
		add(new JScrollPane(textArea));

		// InputMap im = textArea.getInputMap(WHEN_FOCUSED);
		ActionMap am = textArea.getActionMap();

		Action oldAction = am.get("insert-break");
		am.put("insert-break", new AbstractAction() {
			private static final long serialVersionUID = 1322810617331608502L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int range = textArea.getCaretPosition() - userInputStart;
				try {
					String text = textArea.getText(userInputStart, range).trim();
					System.out.println("[" + text + "]");
					userInputStart += range;
					if (!cmd.isRunning()) {
						return;
					} else {
						try {
							cmd.send(text + "\n");
						} catch (IOException ex) {
							appendText("!! Failed to send command to process: " + ex.getMessage() + "\n");
						}
					}
				} catch (BadLocationException ex) {
					Logger.getLogger(QuickTerminal.class.getName()).log(Level.SEVERE, null, ex);
				}
				oldAction.actionPerformed(e);
			}
		});

	}

	@Override
	public void commandOutput(String text) {
		SwingUtilities.invokeLater(new AppendTask(this, text));
	}

	@Override
	public void commandFailed(Exception exp) {
		SwingUtilities.invokeLater(new AppendTask(this, "Command failed - " + exp.getMessage()));
	}

	@Override
	public void commandCompleted(String cmd, int result) {
		if (result == 0)
			return;

		appendText("\n[Program exited with code " + result + "]\n");
		appendText("\n");
	}

	protected void updateUserInputPos() {
		int pos = textArea.getCaretPosition();
		textArea.setCaretPosition(textArea.getText().length());
		userInputStart = pos;

	}

	@Override
	public int getUserInputStart() {
		return userInputStart;
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