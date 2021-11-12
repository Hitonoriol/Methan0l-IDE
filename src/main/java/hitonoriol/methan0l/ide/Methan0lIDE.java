package hitonoriol.methan0l.ide;

import java.awt.EventQueue;

import hitonoriol.methan0l.ide.frames.editor.EditorWindow;

public class Methan0lIDE {
	public static void main(String args[]) {
		EventQueue.invokeLater(() -> {
			try {
				Util.setSystemUIStyle();
				new EditorWindow();
				Prefs.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
