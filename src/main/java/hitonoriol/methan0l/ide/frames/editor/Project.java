package hitonoriol.methan0l.ide.frames.editor;

import java.util.ArrayList;
import java.util.List;

public class Project {
	private List<SourceFile> files = new ArrayList<>();

	public void addFile(SourceFile file) {
		files.add(file);
	}
	
	public void removeFile(SourceFile file) {
		files.remove(file);
	}
	
	public List<SourceFile> getFiles() {
		return files;
	}
}
