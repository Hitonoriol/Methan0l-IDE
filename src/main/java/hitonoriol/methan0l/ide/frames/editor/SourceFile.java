package hitonoriol.methan0l.ide.frames.editor;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import hitonoriol.methan0l.ide.Dialogs;
import hitonoriol.methan0l.ide.Prefs;
import hitonoriol.methan0l.ide.run.Methan0lProgram;

public class SourceFile {
	private static final String NEW_FILE = "New file";
	public static final String EXT = "mt0";
	private File file;

	private static JFileChooser fileChooser = new JFileChooser();
	static {
		fileChooser.setCurrentDirectory(Paths.get(".").toFile());
		fileChooser.setFileFilter(new FileNameExtensionFilter("Methan0l source file", EXT));
	}

	public SourceFile() {
		this(createTempName());
	}

	public SourceFile(File file) {
		this.file = file;
	}

	public SourceFile(String path) {
		file = new File(path);
	}

	public String read() {
		try {
			return FileUtils.readFileToString(file, Charset.defaultCharset());
		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.error("Failed to read " + file.getName());
			return null;
		}
	}

	public boolean isValid() {
		return file.exists() && !file.isDirectory() && file.canRead();
	}

	public String getName() {
		return file.getName();
	}

	public String getPath() {
		return file.getAbsolutePath();
	}

	public void run() {
		if (!Prefs.values().validatePaths() || !isValid())
			return;

		new Methan0lProgram(this).run();
	}

	public static String createTempName() {
		File file = new File(NEW_FILE);
		int i = 0;
		while (file.exists())
			file = new File(NEW_FILE + " " + (++i));

		return file.getName();
	}

	public boolean save(String contents, boolean saveAs) {
		saveAs |= !isValid();
		if (saveAs)
			fileChooser.showSaveDialog(null);

		File file = saveAs ? fileChooser.getSelectedFile() : this.file;
		if (file == null)
			return false;

		try {
			String fname = file.getAbsolutePath();
			if (!fname.endsWith(EXT))
				file = new File(fname + "." + EXT);

			FileUtils.write(file, contents, Charset.defaultCharset());

			if (saveAs)
				this.file = file;
		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.error("Failed to save " + file.getName());
			return false;
		}
		return true;
	}

	public static SourceFile choose() {
		fileChooser.showOpenDialog(null);
		File file = fileChooser.getSelectedFile();
		if (file == null)
			return null;

		return new SourceFile(file);
	}
}
