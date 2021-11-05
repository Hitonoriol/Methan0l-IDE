package hitonoriol.methan0l.ide.run;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import hitonoriol.methan0l.ide.Dialogs;
import hitonoriol.methan0l.ide.Prefs;
import hitonoriol.methan0l.ide.frames.console.ConsoleWindow;
import hitonoriol.methan0l.ide.frames.editor.SourceFile;

public class Methan0lProgram {
	private SourceFile srcFile;
	private Process program;
	private OutputStream stdin;

	public Methan0lProgram(SourceFile file) {
		srcFile = file;
	}

	public void run() {
		try {
			String launchCmd = Prefs.values().getBinaryPath()
					+ " "
					+ "\"" + srcFile.getPath() + "\"";
			program = Runtime.getRuntime().exec(launchCmd);
			stdin = program.getOutputStream();
			InputStream stderr = program.getErrorStream();
			InputStream stdout = program.getInputStream();

			BufferedReader out = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader err = new BufferedReader(new InputStreamReader(stderr));

			ConsoleWindow window = new ConsoleWindow(this);

			do {
				while (window.appendLine(out) || window.appendLine(err));
			} while (!program.waitFor(150, TimeUnit.MILLISECONDS));

			window.appendLine("[Program exited with code " + program.exitValue() + "]");

		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.error("Failed to launch program.");
		}
	}

	public void sendInput(String str) {
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
		return srcFile.getName();
	}
}
