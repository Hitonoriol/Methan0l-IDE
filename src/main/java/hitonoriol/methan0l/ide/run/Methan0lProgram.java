package hitonoriol.methan0l.ide.run;

import java.io.OutputStream;

import hitonoriol.methan0l.ide.Dialogs;
import hitonoriol.methan0l.ide.Prefs;
import hitonoriol.methan0l.ide.frames.console.ConsoleWindow;
import hitonoriol.methan0l.ide.frames.console.ProgramWorker;
import hitonoriol.methan0l.ide.frames.editor.SourceFile;

public class Methan0lProgram {
	private SourceFile srcFile;
	private Process program;

	private OutputStream stdin;

	public Methan0lProgram() {
		this(null);
	}

	public Methan0lProgram(SourceFile file) {
		srcFile = file;
	}

	public void run() {
		try {
			program = Runtime.getRuntime().exec(getLaunchCmd());
			stdin = program.getOutputStream();

			ConsoleWindow window = new ConsoleWindow(this);
			new ProgramWorker(this, window).execute();
		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.error("Failed to launch program.");
		}
	}

	private String getLaunchCmd() {
		String cmd = Prefs.values().getBinaryPath();
		if (srcFile == null)
			return cmd;
		else
			return cmd + " "
					+ "\"" + srcFile.getPath() + "\"";
	}

	public Process getProcess() {
		return program;
	}

	public void sendInput(String str) {
		if (!program.isAlive())
			return;

		try {
			stdin.write(str.getBytes());
			stdin.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		program.destroy();
	}

	public String getName() {
		return srcFile == null ? "Interactive mode" : srcFile.getName();
	}
}
