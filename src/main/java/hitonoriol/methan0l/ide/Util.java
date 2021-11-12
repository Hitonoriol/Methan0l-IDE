package hitonoriol.methan0l.ide;

import javax.swing.UIManager;

public class Util {
	public static void setSystemUIStyle() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
