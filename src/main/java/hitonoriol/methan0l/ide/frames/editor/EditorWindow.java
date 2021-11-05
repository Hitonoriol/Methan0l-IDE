package hitonoriol.methan0l.ide.frames.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import hitonoriol.methan0l.ide.Prefs;
import hitonoriol.methan0l.ide.lang.Methan0lTokenMaker;
import hitonoriol.methan0l.ide.run.Methan0lProgram;

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

	static {
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping(Methan0lTokenMaker.STYLE_NAME, Methan0lTokenMaker.class.getCanonicalName());
	}

	public EditorWindow() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame("Methan0l IDE");
		frame.setMinimumSize(new Dimension(450, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(getMenuBar(), BorderLayout.NORTH);
		frame.getContentPane().add(getProjectTabs(), BorderLayout.CENTER);
		frame.setVisible(true);
	}

	void loadFile(SourceFile file) {
		Component newTab = new FileEditPanel(file);
		projectTabs.addTab(file.getName(), null, newTab, null);
		projectTabs.setSelectedComponent(newTab);
	}

	void saveFile() {
		JTabbedPane tabs = getProjectTabs();
		tabs.setTitleAt(tabs.getSelectedIndex(), getCurrentTab().getFile().getName());
	}

	FileEditPanel getCurrentTab() {
		Component comp = getProjectTabs().getSelectedComponent();
		if (comp == null)
			return null;

		return (FileEditPanel) comp;
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
			newFileMn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editor.createFile();
				}
			});
		}
		return newFileMn;
	}

	private JMenuItem getLoadFileMn() {
		if (loadFileMn == null) {
			loadFileMn = new JMenuItem("Load file");
			loadFileMn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editor.loadFile(SourceFile.choose());
				}
			});
		}
		return loadFileMn;
	}

	private JMenu getProjMenu() {
		if (projMenu == null) {
			projMenu = new JMenu("Project");
			projMenu.add(getRunProjMI());
			projMenu.add(getInterModeMn());
		}
		return projMenu;
	}

	private JMenuItem getRunProjMI() {
		if (runProjMI == null) {
			runProjMI = new JMenuItem("Run current file");
			runProjMI.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileEditPanel tab = getCurrentTab();
					if (tab != null)
						tab.getFile().run();
				}
			});
		}
		return runProjMI;
	}

	JTabbedPane getProjectTabs() {
		if (projectTabs == null) {
			projectTabs = new JTabbedPane(JTabbedPane.TOP);
			projectTabs.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					final int idx = projectTabs.getUI().tabForCoordinate(projectTabs, e.getX(), e.getY());
					if (SwingUtilities.isMiddleMouseButton(e)) {
						FileEditPanel tab = (FileEditPanel) projectTabs.getComponentAt(idx);
						editor.removeFile(tab);
						projectTabs.removeTabAt(idx);
					}
				}
			});
		}
		return projectTabs;
	}

	private JMenuItem getSaveFileMn() {
		if (saveFileMn == null) {
			saveFileMn = new JMenuItem("Save file");
			saveFileMn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileEditPanel tab = getCurrentTab();
					if (tab == null)
						return;

					editor.saveFile(tab);
				}
			});
		}
		return saveFileMn;
	}

	private JMenu getSystemMenu() {
		if (systemMenu == null) {
			systemMenu = new JMenu("System");
			systemMenu.add(getLocateBinMn());
		}
		return systemMenu;
	}

	private JMenuItem getLocateBinMn() {
		if (locateBinMn == null) {
			locateBinMn = new JMenuItem("Locate Methan0l binary");
			locateBinMn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Prefs.values().locateBinary();
				}
			});
		}
		return locateBinMn;
	}

	private JMenuItem getInterModeMn() {
		if (interModeMn == null) {
			interModeMn = new JMenuItem("Interactive mode");
			interModeMn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Methan0lProgram().run();
				}
			});
		}
		return interModeMn;
	}
}
