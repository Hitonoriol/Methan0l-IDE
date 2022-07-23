package hitonoriol.methan0l.ide.frames.editor;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import hitonoriol.methan0l.ide.Dialogs;
import hitonoriol.methan0l.ide.Prefs;
import hitonoriol.methan0l.ide.lang.Methan0lTokenMaker;
import hitonoriol.methan0l.ide.run.Methan0lProgram;
import quickterminal.Command;
import quickterminal.CommandReader;

public class EditorWindow {
	private Editor editor = new Editor(this);
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newFileMn;
	private JMenuItem loadFileMn;
	private JMenu projMenu;
	private JMenuItem runProjMI;
	private JTabbedPane projectTabs;
	private JMenuItem saveFileMn;
	private JMenu systemMenu;
	private JMenuItem locateBinMn;
	private JMenuItem interModeMn;
	private JMenuItem setWDMn;
	private JMenuItem closeMn;

	private final static String TITLE = "Methan0l IDE";
	
	static {
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping(Methan0lTokenMaker.STYLE_NAME, Methan0lTokenMaker.class.getCanonicalName());
	}

	private Action saveFileAction = new AbstractAction() {
		private static final long serialVersionUID = -888203250312187531L;

		@Override
		public void actionPerformed(ActionEvent e) {
			FileEditPanel tab = getCurrentTab();
			if (tab == null)
				return;

			if (tab.modified())
				editor.saveFile(tab);
		}
	};

	private Action newFileAction = new AbstractAction() {
		private static final long serialVersionUID = 6119695424646226826L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editor.createFile();
		}
	};

	private Action loadFileAction = new AbstractAction() {
		private static final long serialVersionUID = -1692879718457529619L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editor.loadFile(SourceFile.choose());
		}
	};

	private Action runAction = new AbstractAction() {
		private static final long serialVersionUID = -1225335966782914674L;

		@Override
		public void actionPerformed(ActionEvent e) {
			FileEditPanel tab = getCurrentTab();
			if (tab != null) {
				editor.saveFile(tab);
				tab.getFile().run();
			} else
				Dialogs.info("Nothing to run");
		}
	};

	private Action interactiveAction = new AbstractAction() {
		private static final long serialVersionUID = -8688277570699253272L;

		@Override
		public void actionPerformed(ActionEvent e) {
			new Methan0lProgram().run();
		}
	};

	private Action setWDAction = new AbstractAction() {
		private static final long serialVersionUID = -4639406000977977114L;

		@Override
		public void actionPerformed(ActionEvent e) {
			File dir = SourceFile.chooseDirectory();
			if (dir != null)
				Prefs.values().setWorkDir(dir.getPath());
		}
	};

	private Action closeTabAction = new AbstractAction() {
		private static final long serialVersionUID = -8688277570699253272L;

		@Override
		public void actionPerformed(ActionEvent e) {
			closeTab(projectTabs.getSelectedIndex());
		}
	};

	public EditorWindow() {
		initialize();
		setHotkey(KeyStroke.getKeyStroke("control N"), newFileMn);
		setHotkey(KeyStroke.getKeyStroke("control O"), loadFileMn);
		setHotkey(KeyStroke.getKeyStroke("control S"), saveFileMn);
		setHotkey(KeyStroke.getKeyStroke("control R"), runProjMI);
		setHotkey(KeyStroke.getKeyStroke("control I"), interModeMn);
		setHotkey(KeyStroke.getKeyStroke("control W"), closeMn);
	}

	private void initialize() {
		frame = new JFrame(TITLE);
		frame.setMinimumSize(new Dimension(450, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(getMenuBar(), BorderLayout.NORTH);
		frame.getContentPane().add(getProjectTabs(), BorderLayout.CENTER);
		parseVersionString();
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				for (Window window : Window.getWindows())
					window.dispose();
			}
		});
		
		frame.setDropTarget(createDndTarget());
	}

	DropTarget createDndTarget() {
		return new DropTarget() {
			private static final long serialVersionUID = -6103476608564601103L;

			@SuppressWarnings("unchecked")
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt
							.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles)
						editor.loadFile(new SourceFile(file));
				} catch (Exception ex) {
					ex.printStackTrace();
					Dialogs.error("Unsupported drag-and-drop object");
				}
			}
		};
	}
	
	private static final String VERSION_REGEX = ".*v\\d+\\..*[\\r\\n]*";

	private void parseVersionString() {
		CommandReader reader = new CommandReader(out -> {
			out.ifPresent(outStr -> frame
					.setTitle(TITLE
							+ " ["
							+ (outStr.matches(VERSION_REGEX) ? out.get() : "Unknown methan0l version")
							+ "]"));
		});
		Command executor = new Command(reader);
		executor.execute(Prefs.values().getBinaryPath() + " --version");
	}
	
	void textModified(boolean modified) {
		FileEditPanel tab = getCurrentTab();
		if (tab.modified() == modified)
			return;

		int current = projectTabs.getSelectedIndex();
		String name = tab.getFile().getName();
		projectTabs.setTitleAt(current, name + (modified ? "*" : ""));
		tab.setModified(modified);
	}

	void loadFile(SourceFile file) {
		Component newTab = new FileEditPanel(this, file);
		projectTabs.addTab(file.getName(), null, newTab, null);
		projectTabs.setSelectedComponent(newTab);
	}

	void saveFile() {
		JTabbedPane tabs = getProjectTabs();
		tabs.setTitleAt(tabs.getSelectedIndex(), getCurrentTab().getFile().getName());
		textModified(false);
	}

	FileEditPanel getCurrentTab() {
		Component comp = getProjectTabs().getSelectedComponent();
		if (comp == null)
			return null;

		return (FileEditPanel) comp;
	}

	private void setHotkey(KeyStroke hotKey, JMenuItem item) {
		JComponent root = frame.getRootPane();
		Action action = item.getAction();
		root.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(hotKey, action);
		root.getActionMap().put(action, action);
		item.setText(item.getText() + " (" + getKeystrokeString(hotKey) + ")");
	}

	private static void setAction(AbstractButton btn, Action action) {
		String str = btn.getText();
		btn.setAction(action);
		btn.setText(str);
	}

	private static String getKeystrokeString(KeyStroke stroke) {
		String str = stroke.toString().replace("pressed", "+").replace(" ", "");
		str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
		return str;
	}

	private void closeTab(int idx) {
		if (idx == -1)
			return;

		FileEditPanel tab = (FileEditPanel) projectTabs.getComponentAt(idx);
		editor.removeFile(tab);
		projectTabs.removeTabAt(idx);
	}

	/******************* Auto-generated Swing boilerplate *******************/

	private JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			menuBar.add(getFileMenu());
			menuBar.add(getProjMenu());
			menuBar.add(getSystemMenu());
		}
		return menuBar;
	}

	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu("File");
			fileMenu.add(getNewFileMn());
			fileMenu.add(getLoadFileMn());
			fileMenu.add(getSaveFileMn());
		}
		return fileMenu;
	}

	private JMenuItem getNewFileMn() {
		if (newFileMn == null) {
			newFileMn = new JMenuItem("New file");
			setAction(newFileMn, newFileAction);
		}
		return newFileMn;
	}

	private JMenuItem getLoadFileMn() {
		if (loadFileMn == null) {
			loadFileMn = new JMenuItem("Open file");
			setAction(loadFileMn, loadFileAction);
		}
		return loadFileMn;
	}

	private JMenu getProjMenu() {
		if (projMenu == null) {
			projMenu = new JMenu("Project");
			projMenu.add(getRunProjMI());
			projMenu.add(getCloseMn());
			projMenu.add(getInterModeMn());
		}
		return projMenu;
	}

	private JMenuItem getRunProjMI() {
		if (runProjMI == null) {
			runProjMI = new JMenuItem("Run current file");
			setAction(runProjMI, runAction);
		}
		return runProjMI;
	}

	JTabbedPane getProjectTabs() {
		if (projectTabs == null) {
			projectTabs = new JTabbedPane(JTabbedPane.TOP);
			projectTabs.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isMiddleMouseButton(e))
						closeTab(projectTabs.getUI().tabForCoordinate(projectTabs, e.getX(), e.getY()));
				}
			});
		}
		return projectTabs;
	}

	private JMenuItem getSaveFileMn() {
		if (saveFileMn == null) {
			saveFileMn = new JMenuItem("Save file");
			setAction(saveFileMn, saveFileAction);
		}
		return saveFileMn;
	}

	private JMenu getSystemMenu() {
		if (systemMenu == null) {
			systemMenu = new JMenu("System");
			systemMenu.add(getLocateBinMn());
			systemMenu.add(getSetWDMn());
		}
		return systemMenu;
	}

	private JMenuItem getLocateBinMn() {
		if (locateBinMn == null) {
			locateBinMn = new JMenuItem("Locate Methan0l binary");
			locateBinMn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Prefs.values().locateBinary();
					parseVersionString();
				}
			});
		}
		return locateBinMn;
	}

	private JMenuItem getInterModeMn() {
		if (interModeMn == null) {
			interModeMn = new JMenuItem("Open interactive mode console");
			setAction(interModeMn, interactiveAction);
		}
		return interModeMn;
	}

	private JMenuItem getSetWDMn() {
		if (setWDMn == null) {
			setWDMn = new JMenuItem("Set working directory");
			setAction(setWDMn, setWDAction);
		}
		return setWDMn;
	}

	private JMenuItem getCloseMn() {
		if (closeMn == null) {
			closeMn = new JMenuItem("Close current tab");
			setAction(closeMn, closeTabAction);
		}
		return closeMn;
	}
	
	JFrame getFrame() {
		return frame;
	}
}
