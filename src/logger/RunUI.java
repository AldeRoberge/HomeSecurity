package logger;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import discord.DiscordBot;
import discord.SettingsHandler;
import util.Autocomplete;
import util.Autocomplete.CommitAction;

public class RunUI {

	// JFrame components
	private JFrame frame;

	// Console
	public JTextPane consoleTextArea;
	public static HTMLEditorKit htmlEditorKit;
	public static StyleSheet css;

	int currentConsoleLine = 0;

	// Tabbed pane

	public JTabbedPane tabbedPane;

	// UILogger
	public static final String TAG = "HomeSecurity";
	private static final String ICONURL = "https://i.imgur.com/06iYSHg.png";
	public SettingsHandler statusHandler = new SettingsHandler();

	// Database

	public DiscordBot discordBot;
	private JTextField imputPanelTextField;
	private static final String COMMIT_ACTION = "commit";

	// Hide to tray
	TrayIcon trayIcon;
	SystemTray tray;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		// Launch JFrame
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RunUI window = new RunUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Image used in system tray and software
	public Image softwareIcon;

	/**
	 * Create the application.
	 */
	public RunUI() {

		// then init jframe
		initialize();

		discordBot = new DiscordBot(this);

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setTitle(TAG);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {

			print(TAG, "Error 34, 360tLookAndFeel", true);
			e1.printStackTrace();
		}

		try {
			softwareIcon = new ImageIcon(ImageIO.read(new URL(ICONURL))).getImage();
		} catch (IOException e2) {
			print(TAG, "Could not get icon image from URL", true);
			e2.printStackTrace();
		}

		frame.setIconImage(softwareIcon);

		frame.setBounds(100, 100, 462, 404);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// Settings

		statusHandler.init(this);

		tabbedPane.addTab("Settings", null, statusHandler.getSettingsPanel(), null);

		// Console

		JPanel consolePanel = new JPanel();
		tabbedPane.addTab("Console", null, consolePanel, null);
		consolePanel.setLayout(new BorderLayout(0, 0));

		JScrollPane consoleScrollPane = new JScrollPane();
		consolePanel.add(consoleScrollPane);

		// ConsoleTextArea

		consoleTextArea = new JTextPane();
		consoleTextArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		consoleTextArea.setEnabled(true);
		consoleTextArea.setEditable(false);
		consoleTextArea.setContentType("text/html");

		consoleTextArea.setBackground(hex2Rgb("#000000"));

		consoleScrollPane.setViewportView(consoleTextArea);

		consoleTextArea.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							print(TAG, "IOException in getDesktop.browse(URI)", false);
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							print(TAG, "URISyntaxException in getDesktop.browse(URI)", false);
							e1.printStackTrace();
						}
					}
				}
			}
		});

		// HTML AND CSS (FORMATING OF CONSOLE)

		htmlEditorKit = new HTMLEditorKit(); // html
		consoleTextArea.setEditorKit(htmlEditorKit);

		JPanel imputPanel = new JPanel();
		consolePanel.add(imputPanel, BorderLayout.SOUTH);
		imputPanel.setLayout(new BorderLayout(0, 0));

		imputPanelTextField = new JTextField();
		imputPanel.add(imputPanelTextField);
		imputPanelTextField.setColumns(10);

		imputPanelTextField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				statusHandler.handleCommand(imputPanelTextField.getText());
				imputPanelTextField.setText("");
			}
		});

		// Without this, cursor always leaves text field
		imputPanelTextField.setFocusTraversalKeysEnabled(false);

		// Our words to complete
		Autocomplete autoComplete = new Autocomplete(imputPanelTextField, statusHandler.getCommands());
		imputPanelTextField.getDocument().addDocumentListener(autoComplete);

		// Maps the tab key to the commit action, which finishes the
		// autocomplete when given a suggestion
		imputPanelTextField.getInputMap().put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
		imputPanelTextField.getActionMap().put(COMMIT_ACTION, autoComplete.new CommitAction());

		// End

		print(TAG, "Welcome to " + TAG + "!", false);

		// Hide in system tray

		if (SystemTray.isSupported()) {
			System.out.println("System tray is supported");
			tray = SystemTray.getSystemTray();

			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Restore");
			defaultItem.addActionListener(new ActionListener() { // restore
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(true);
					frame.setExtendedState(JFrame.NORMAL);
				}
			});
			popup.add(defaultItem);
			trayIcon = new TrayIcon(softwareIcon, "HomeSecurity", popup);
			trayIcon.setImageAutoSize(true);
		} else {
			System.out.println("system tray not supported");
		}

		frame.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == frame.ICONIFIED) {
					try {
						tray.add(trayIcon);
						frame.setVisible(false);
						System.out.println("added to SystemTray");
					} catch (AWTException ex) {
						System.out.println("unable to add to tray");
					}
				}
				if (e.getNewState() == 7) {
					try {
						tray.add(trayIcon);
						frame.setVisible(false);
						System.out.println("added to SystemTray");
					} catch (AWTException ex) {
						System.out.println("unable to add to system tray");
					}
				}
				if (e.getNewState() == frame.MAXIMIZED_BOTH) {
					tray.remove(trayIcon);
					frame.setVisible(true);
					System.out.println("Tray icon removed");
				}
				if (e.getNewState() == frame.NORMAL) {
					tray.remove(trayIcon);
					frame.setVisible(true);
					System.out.println("Tray icon removed");
				}
			}
		});

	}

	/**
	 * @param text
	 *            Text to color
	 * @param color
	 *            Hex or name of color
	 * @return Html string. Ex : <font color="FF0000">Hey!</font>
	 */
	public String makeTextColor(String text, String color) {
		return "<font color=\"" + color + "\">" + text + "</font>";
	}

	public String makeBold(String text) {
		return "<b>" + text + "</b>";
	}

	/**
	 * @param printToConsole
	 *            String printed on screen, using markdown converted to HTML. C:
	 * 
	 * @param isError
	 *            used to show red text
	 */
	public void print(String tag, String printToConsole, boolean isError) {

		printToConsole = printToConsole.replaceAll("\n", "<br>");

		String prefix = makeBold(makeTextColor(currentConsoleLine + ": ", "#FF00FF"));

		prefix = prefix + makeBold(makeTextColor(tag + ": ", "#FFAA00"));

		String color = "#00FFFF";

		if (isError) {
			// F99565
			color = "#FF0000";
		}

		String processedString = prefix + makeTextColor(printToConsole, color);

		String htmlText = "<font face=\"Bitstream Vera Sans Mono\">" + processedString + "</font>";

		Document doc = consoleTextArea.getDocument();
		try {
			htmlEditorKit.insertHTML((HTMLDocument) doc, doc.getLength(), htmlText, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			System.err.println("Error with insertHTML in print with string " + printToConsole + "...");
			print(TAG, "Exception in RunUI.print : " + e.getMessage(), true);
			e.printStackTrace();
		}

		System.out.println(processedString);

		currentConsoleLine++;

	}

	public static String stackTraceToString(Throwable e) {
		if (e != null) {

			StringBuilder sb = new StringBuilder(e.toString());
			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append("\n\tat ");
				sb.append(ste);
			}

			return sb.toString();
		} else {
			return "Exception is null";
		}

	}

	/**
	 * 
	 * @param colorStr
	 *            e.g. "#FFFFFF"
	 * @return Color object
	 */
	public static Color hex2Rgb(String colorStr) {
		// @formatter:off
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
		// @formatter:on
	}

}
