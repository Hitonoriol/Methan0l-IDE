package hitonoriol.methan0l.ide.frames.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import hitonoriol.methan0l.ide.run.Methan0lProgram;

public class ConsoleWindow extends JFrame {
	private static final long serialVersionUID = -117260421198692692L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTextArea programOutput;
	private JTextField inputField;
	private Methan0lProgram program;

	public ConsoleWindow(Methan0lProgram program) {
		super(program.getName());
		this.program = program;
		createContents();
	}

	private void createContents() {
		setMinimumSize(new Dimension(350, 300));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(getScrollPane(), BorderLayout.CENTER);
		contentPane.add(getInputField(), BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	public boolean appendLine(BufferedReader reader) {
		try {
			String line;
			if ((line = reader.readLine()) != null) {
				appendLine(line);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void appendLine(String line) {
		programOutput.append(line + System.lineSeparator());
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getProgramOutput());
		}
		return scrollPane;
	}

	private JTextArea getProgramOutput() {
		if (programOutput == null) {
			programOutput = new JTextArea();
			programOutput.setEditable(false);
		}
		return programOutput;
	}

	private JTextField getInputField() {
		if (inputField == null) {
			inputField = new JTextField();
			inputField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() != KeyEvent.VK_ENTER)
						return;
					
					program.sendInput(inputField.getText());
					inputField.setText("");
				}
			});
			inputField.setColumns(10);
		}
		return inputField;
	}
}
