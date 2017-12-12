package discord;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import actions.Setting;
import actions.impl.InitCameraButton;
import logger.RunUI;

public class SettingsHandler {

	public String TAG = "StatusHandler";

	public static ArrayList<Setting> settings = new ArrayList<Setting>();

	static {
		// Static add all actions

		settings.add(new InitCameraButton());
	}

	public boolean running = false;

	public RunUI runUI;

	public void init(RunUI runUI) {
		Setting.runUI = runUI;
		this.runUI = runUI;
	}

	public void handleCommand(String command) {

		runUI.print(TAG, "Imput : " + command, false);

		for (Setting action : settings) {

			if (action.getTriggerCommand().contains(command)) {
				action.handleCommand(command);
			}

		}
	}

	//Used by UI for autocomplete console jtextfield
	public List<String> getCommands() {

		List<String> keywords = new ArrayList<String>();

		for (Setting action : settings) {
			keywords.add(action.getTriggerCommand());
		}

		return keywords;
	}

	public JPanel getSettingsPanel() {

		JPanel settingsPanel = new JPanel();

		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

		for (Setting action : settings) {
			settingsPanel.add(action.getJPanel());

		}

		return settingsPanel;

	}

}
