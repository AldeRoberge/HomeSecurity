package actions;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import logger.RunUI;

/**
 * Settings are both inside the Settings tab and can be performed inside the console too
 * @author VaypeNaysh
 *
 */
public abstract class Setting {

	public static RunUI runUI;

	public abstract String getTAG();

	public abstract ArrayList<Component> getComponents();

	public JPanel getJPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(Box.createHorizontalStrut(20));

		for (Component c : getComponents()) {
			panel.add(c);
			panel.add(Box.createHorizontalStrut(10));
		}

		return panel;

	}

	public void print(String toPrint, boolean error) {
		runUI.print(getTAG(), toPrint, error);
	}

	public abstract void handleCommand(String command);
	public abstract String getTriggerCommand();

}
