package quickterminal;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class QuickTerminal extends JFrame {
	private static final long serialVersionUID = -6162945073450437269L;
	
	private ConsolePane console;
	private String cmd;

	public QuickTerminal(String title, String cmd) {
		super(title == null ? cmd : title);
		this.cmd = cmd;
		EventQueue.invokeLater(() -> {
			try {
				init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public QuickTerminal(String cmd) {
		this(null, cmd);
	}

	private void init() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(getConsole());
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public ConsolePane getConsole() {
		if (console == null)
			console = new ConsolePane(cmd);
		return console;
	}
	
	@Override
	public void dispose() {
		System.out.println("Killing methan0l process...");
		console.getCommand().stop();
		super.dispose();
	}
}