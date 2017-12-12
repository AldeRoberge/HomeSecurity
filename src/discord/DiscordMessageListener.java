package discord;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

public class DiscordMessageListener implements MessageCreateListener {

	SettingsHandler s;

	public DiscordMessageListener(SettingsHandler s) {
		super();

		this.s = s;
	}

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) { // new
																	// message

		System.out.println(message.getAuthor().getName() + ": " + message.getContent());

		if (!message.getAuthor().isYourself()) {
			s.handleCommand(message.getContent());
		}

	}

}