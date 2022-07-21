package hitonoriol.methan0l.ide;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.swing.JFileChooser;

import org.apache.commons.io.FilenameUtils;

import hitonoriol.methan0l.ide.frames.editor.SourceFile;

public class Prefs implements Serializable {
	private static final long serialVersionUID = -3239797926127336797L;
	
	public final static Charset UTF8 = Charset.forName("utf8");
	
	private static final File prefFile = new File("prefs.bin");

	private String workDir;
	private String interpBin;

	private static Prefs instance;

	static {
		if (prefFile.exists()) {
			try (FileInputStream fStr = new FileInputStream(prefFile);
					ObjectInputStream oStr = new ObjectInputStream(fStr)) {
				instance = (Prefs) oStr.readObject();
				EventQueue.invokeLater(
						() -> SourceFile.getFileChooser().setCurrentDirectory(new File(instance.getWorkDir())));
			} catch (Exception e) {
				e.printStackTrace();
				Dialogs.error("Couldn't load preference file");
				createNew();
			}
		} else
			createNew();
	}

	private Prefs() {
	}

	public String getWorkDir() {
		return workDir;
	}

	public String getBinaryPath() {
		return interpBin;
	}

	private boolean validate() {
		return workDir == null || interpBin == null || !new File(workDir).exists() || !new File(interpBin).exists();
	}

	public boolean validatePaths() {
		if (validate()) {
			locateBinary();
			return validate();
		}
		return true;
	}

	public void locateBinary() {
		Dialogs.info("Locate the interpreter executable in order to run Methan0l programs.");
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(null);
		File binFile = chooser.getSelectedFile();
		if (binFile == null)
			Dialogs.info("You can locate the binary later via System -> Locate Methan0l binary");
		else {
			setInterpBin(binFile);
		}
	}

	private void setInterpBin(File path) {
		interpBin = path.getAbsolutePath();
		workDir = FilenameUtils.getFullPath(interpBin);
		save();
	}

	public void setWorkDir(String path) {
		workDir = path;
		save();
	}

	public void save() {
		SourceFile.getFileChooser().setCurrentDirectory(new File(workDir));
		try (FileOutputStream ofStr = new FileOutputStream(prefFile);
				ObjectOutputStream ooStr = new ObjectOutputStream(ofStr)) {
			ooStr.writeObject(instance);
		} catch (Exception e) {
			e.printStackTrace();
			Dialogs.error("Couldn't save preference file");
		}
	}

	private static void createNew() {
		instance = new Prefs();
		instance.locateBinary();
	}

	public static void init() {
	}

	public static Prefs values() {
		return instance;
	}

}
