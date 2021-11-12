package hitonoriol.methan0l.ide.run;

import hitonoriol.methan0l.ide.Prefs;
import hitonoriol.methan0l.ide.frames.editor.SourceFile;
import quickterminal.QuickTerminal;

public class Methan0lProgram {
	private SourceFile srcFile;

	public Methan0lProgram() {
		this(null);
	}

	public Methan0lProgram(SourceFile file) {
		srcFile = file;
	}

	public void run() {
		new QuickTerminal(getName(), getLaunchCmd());
	}

	private String getLaunchCmd() {
		String cmd = Prefs.values().getBinaryPath();
		if (srcFile == null)
			return cmd;
		else
			return cmd + " "
					+ "\"" + srcFile.getPath() + "\"";
	}

	public String getName() {
		return srcFile == null ? "Interactive mode" : srcFile.getName();
	}
}
