package quickterminal;

import java.io.IOException;
import java.io.InputStream;

import hitonoriol.methan0l.ide.Resources;

public class StreamReader extends Thread {

	private InputStream is;
	private CommandListener listener;

	private final static int BUFFER_CAP = 128;
	
	public StreamReader(CommandListener listener, InputStream is) {
		this.is = is;
		this.listener = listener;
		start();
	}

	@Override
	public void run() {
		try {
			int len = 0;
			byte[] buffer = new byte[BUFFER_CAP];
			while ((len = is.read(buffer)) > 0)
				listener.commandOutput(new String(buffer, 0, len, Resources.UTF8));
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}
}