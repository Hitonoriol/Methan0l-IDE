package quickterminal;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import hitonoriol.methan0l.ide.Resources;

public class ProcessRunner extends Thread {
	private List<String> cmds;
	private CommandListener listener;

	private Process process;

	public ProcessRunner(CommandListener listener, List<String> cmds) {
		this.cmds = cmds;
		this.listener = listener;
		start();
	}

	@Override
	public void run() {
		try {
			ProcessBuilder pb = new ProcessBuilder(cmds);
			pb.redirectErrorStream(true);
			process = pb.start();
			StreamReader reader = new StreamReader(listener, process.getInputStream());

			int result = process.waitFor();

			reader.join();

			StringJoiner sj = new StringJoiner(" ");
			cmds.stream().forEach((cmd) -> {
				sj.add(cmd);
			});

			listener.commandCompleted(sj.toString(), result);
		} catch (Exception exp) {
			exp.printStackTrace();
			listener.commandFailed(exp);
		}
	}

	public void write(String text) throws IOException {
		if (process != null && process.isAlive()) {
			process.getOutputStream().write(text.getBytes(Resources.UTF8));
			process.getOutputStream().flush();
		}
	}
	
	public void kill() {
		process.destroy();
	}
}