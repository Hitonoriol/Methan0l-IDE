package hitonoriol.methan0l.ide.frames.editor;

import hitonoriol.methan0l.ide.Dialogs;

public class Editor {
	private Project currentProject = new Project();
	private EditorWindow window;

	public Editor(EditorWindow window) {
		this.window = window;
	}

	void createFile() {
		SourceFile file = new SourceFile();
		currentProject.addFile(file);
		window.loadFile(file);
	}

	void loadFile(SourceFile file) {
		if (file == null)
			return;

		if (!file.isValid()) {
			Dialogs.error(file.getName() + " is not a valid Methan0l source file.");
			return;
		}

		currentProject.addFile(file);
		window.loadFile(file);
	}

	void saveFile(FileEditPanel tab) {
		tab.getFile().save(tab.getText(), false);
		window.saveFile();
	}
	
	void removeFile(FileEditPanel tab) {
		currentProject.removeFile(tab.getFile());
	}
}
