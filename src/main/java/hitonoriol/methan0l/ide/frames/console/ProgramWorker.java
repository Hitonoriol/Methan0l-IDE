package hitonoriol.methan0l.ide.frames.console;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import hitonoriol.methan0l.ide.run.Methan0lProgram;

public class ProgramWorker extends SwingWorker<Object, Object> {
	private Methan0lProgram program;
	private ConsoleWindow window;

	public ProgramWorker(Methan0lProgram program, ConsoleWindow window) {
		this.program = program;
		this.window = window;
	}

	@Override
	protected Object doInBackground() throws Exception {
		Process program = this.program.getProcess();
		InputStream stderr = program.getErrorStream();
		InputStream stdout = program.getInputStream();

		BufferedReader out = new BufferedReader(new InputStreamReader(stdout));
		BufferedReader err = new BufferedReader(new InputStreamReader(stderr));

		do {
			while (window.appendLine(out) || window.appendLine(err));
		} while (!program.waitFor(150, TimeUnit.MILLISECONDS));

		window.appendLine("[Program exited with code " + program.exitValue() + "]");
		window.executionFinished();
		return null;
	}

}
