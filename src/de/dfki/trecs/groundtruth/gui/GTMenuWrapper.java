/**
 * 
 */
package de.dfki.trecs.groundtruth.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class includes the menu shown on top of the application.
 * 
 * @author Shahab
 *
 */
public class GTMenuWrapper implements MenuIndexConstants {

	private static Logger log() {
		return Logger.getLogger(GTMenuWrapper.class.getName());
	}

	private ActionListener listener;
	private MenuItem[] items;
	private MenuBar menuBar;
	private String[] menuLabels;

	public GTMenuWrapper(ActionListener listener) {
		menuBar = new MenuBar();
		setListener(listener);
		items = new MenuItem[MenuIndexConstants.NUM_CONSTANTS];
		menuLabels = new String[MenuIndexConstants.NUM_CONSTANTS];
		loadMenuLabels();
		initItems();
		createMenu();
	}

	public void updateEnabled(OperationProcessor processor) {

	}

	/**
	 * This method tries to find the index of the given menu item for the processing
	 * of the message by the operation processor class later on.
	 * 
	 * @param o
	 * @return
	 */
	public int findIndex(Object o) {
		if (o != null && items != null) {
			for (int i = 0; i < items.length; i++) {
				if (o == items[i])
					return i;
			}
		}
		return -1;
	}

	/**
	 * Create all the menu items.
	 */
	private void initItems() {
		for (int i = 0; i < items.length; i++) {
			String label = menuLabels[i];
			MenuItem item = new MenuItem(label);
			items[i] = item;
		}
	}

	/**
	 * load menu labels from resources/menu.txt
	 */
	private void loadMenuLabels() {
		try {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream("menu.txt");
			// BufferedReader reader = new BufferedReader(new FileReader(new
			// File(menuResource)));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			int index = 0;
			while ((line = reader.readLine()) != null) {
				menuLabels[index] = line.trim();
				index++;
				if (index > menuLabels.length)
					break;
			}
			reader.close();
		} catch (IOException ex) {
			log().log(Level.SEVERE, "Error loading menu entries from resources/menu.txt", ex.getCause());
			System.exit(-1);
		}
	}

	/**
	 * creates all the menu items and it to the MenuBar.
	 */
	private void createMenu() {
		Menu fileMenu = new Menu(menuLabels[MenuIndexConstants.FILE]);
		items[MenuIndexConstants.FILE] = fileMenu;

		fileMenu.add(items[MenuIndexConstants.FILE_OPEN]);
		fileMenu.add(items[MenuIndexConstants.OPEN_GT_FILE]);
		// fileMenu.add(items[MenuIndexConstants.FILE_SAVE]);
		// fileMenu.add(items[MenuIndexConstants.FILE_SAVE_AS]);
		fileMenu.add(items[MenuIndexConstants.SAVE_GT_FILE]);
		fileMenu.add(items[MenuIndexConstants.FILE_CLOSE]);
		fileMenu.add(items[MenuIndexConstants.EXIT]);

		menuBar.add(fileMenu);

		Menu editMenu = new Menu(menuLabels[MenuIndexConstants.EDIT]);
		items[MenuIndexConstants.EDIT] = editMenu;

		editMenu.add(items[MenuIndexConstants.MARK_TABLE]);
		editMenu.add(items[MenuIndexConstants.MARK_ROW_COL]);
		editMenu.add(items[MenuIndexConstants.MARK_ROW_COL_SPAN]);
		editMenu.add(items[MenuIndexConstants.ASSIGN_COLORS]);
		editMenu.add(items[MenuIndexConstants.UNDO]);
		editMenu.add(items[MenuIndexConstants.REDO]);

		Menu viewMenu = new Menu(menuLabels[MenuIndexConstants.VIEW]);
		viewMenu.add(items[MenuIndexConstants.ZOOM_IN]);
		viewMenu.add(items[MenuIndexConstants.ZOOM_OUT]);
		viewMenu.add(items[MenuIndexConstants.ZOOM_TO_FIT]);

		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		addShortcutAndListener();

	}

	private MenuShortcut createMenuShortcut(int menuIndex) {
		switch (menuIndex) {
		case (MenuIndexConstants.FILE_OPEN):
			return new MenuShortcut(KeyEvent.VK_O);
//			case(MenuIndexConstants.FILE_EXIT): return new MenuShortcut(KeyEvent.VK_Q);
		case (MenuIndexConstants.UNDO):
			return new MenuShortcut(KeyEvent.VK_Z);
		case (MenuIndexConstants.REDO):
			return new MenuShortcut(KeyEvent.VK_Y);
		case (MenuIndexConstants.ZOOM_IN):
			return new MenuShortcut(KeyEvent.VK_1);
		case (MenuIndexConstants.ZOOM_OUT):
			return new MenuShortcut(KeyEvent.VK_2);
		case (MenuIndexConstants.ZOOM_TO_FIT):
			return new MenuShortcut(KeyEvent.VK_0);
		case (MenuIndexConstants.MARK_TABLE):
			return new MenuShortcut(KeyEvent.VK_T);
		case (MenuIndexConstants.MARK_ROW_COL):
			return new MenuShortcut(KeyEvent.VK_R);
		case (MenuIndexConstants.MARK_ROW_COL_SPAN):
			return new MenuShortcut(KeyEvent.VK_M);
		case (MenuIndexConstants.SAVE_GT_FILE):
			return new MenuShortcut(KeyEvent.VK_S);
		case (MenuIndexConstants.OPEN_GT_FILE):
			return new MenuShortcut(KeyEvent.VK_L);
		default:
			return null;
		}
	}

	/**
	 * Add menu shortcut and listener to all the menu items.
	 * 
	 */
	private void addShortcutAndListener() {
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				MenuShortcut shortcut = createMenuShortcut(i);
				if (shortcut != null)
					items[i].setShortcut(shortcut);
				items[i].addActionListener(listener);
			}

		}
	}

	/**
	 * @return the listener
	 */
	public ActionListener getListener() {
		return listener;
	}

	/**
	 * @return the items
	 */
	public MenuItem[] getItems() {
		return items;
	}

	/**
	 * @return the menuBar
	 */
	public MenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(ActionListener listener) {
		this.listener = listener;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(MenuItem[] items) {
		this.items = items;
	}

	/**
	 * @param menuBar the menuBar to set
	 */
	public void setMenuBar(MenuBar menuBar) {
		this.menuBar = menuBar;
	}
}
