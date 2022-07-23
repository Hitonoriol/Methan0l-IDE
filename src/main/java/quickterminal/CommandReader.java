package quickterminal;

import java.util.Optional;
import java.util.function.Consumer;

public class CommandReader implements CommandListener {
	private boolean failed = false;
	private StringBuilder out = new StringBuilder();
	private Consumer<Optional<String>> outConsumer;

	public CommandReader(Consumer<Optional<String>> outputConsumer) {
		outConsumer = outputConsumer;
	}

	@Override
	public void commandOutput(String text) {
		out.append(text);
	}

	@Override
	public void commandFailed(Exception exp) {
		failed = true;
		exp.printStackTrace();
	}

	@Override
	public void commandCompleted(String cmd, int ret) {
		failed = ret != 0;
		Optional<String> result = Optional.ofNullable(failed ? null : out.toString());
		outConsumer.accept(result);
	}

}
