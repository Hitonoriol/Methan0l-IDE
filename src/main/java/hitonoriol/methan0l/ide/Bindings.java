package hitonoriol.methan0l.ide;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class Bindings {

	private final static Consumer<JComponent> scaleFontDown = cmp -> scaleFont(cmp, -1f),
			scaleFontUp = cmp -> scaleFont(cmp, 1f);

	/* Add font scaling keybinds to any Swing component.
	 * 
	 * Events used for these bindings:
	 * 		CTRL + `+` / `-`
	 * 		CTRL + `NUM +` / `NUM -`
	 * 		CTRL + `Scroll up` / `Scroll down`
	 */
	public static void setupFontScaling(JComponent component) {
		/* Upper row `+` key is an `=` key with `Shift` pressed down */
		setBinding(component, KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK, scaleFontUp);
		setBinding(component, KeyEvent.VK_MINUS, scaleFontDown);
		setBinding(component, KeyEvent.VK_ADD, scaleFontUp);
		setBinding(component, KeyEvent.VK_SUBTRACT, scaleFontDown);
		component.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (!e.isControlDown()) {
					e.getComponent().getParent().dispatchEvent(e);
					return;
				}
				
				if (e.getWheelRotation() < 1)
					scaleFontUp.accept(component);
				else
					scaleFontDown.accept(component);
			}
		});
	}

	private static void scaleFont(JComponent component, float by) {
		Font font = component.getFont();
		component.setFont(font.deriveFont(font.getSize() + by));
	}

	public static <T extends JComponent> void setBinding(T component, int key, int mask, Consumer<T> action) {
		InputMap input = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actions = component.getActionMap();

		input.put(KeyStroke.getKeyStroke(key, mask), action);
		actions.put(action, new AbstractAction() {
			private static final long serialVersionUID = 3135636986547825873L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				action.accept(component);
			}
		});
	}

	public static <T extends JComponent> void setBinding(T component, int key, Consumer<T> action) {
		setBinding(component, key, InputEvent.CTRL_DOWN_MASK, action);
	}

}
