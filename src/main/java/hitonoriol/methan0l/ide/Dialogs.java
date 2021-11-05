package hitonoriol.methan0l.ide;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Dialogs {
	
	private static void show(int type, String title, String text) {
		JDialog dialog = new JOptionPane(text, type).createDialog(title);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}
	
	public static void error(String text) {
		show(JOptionPane.ERROR_MESSAGE, "Error", text);		
	}
	
	public static void info(String text) {
		show(JOptionPane.INFORMATION_MESSAGE, "Info", text);
	}
}
