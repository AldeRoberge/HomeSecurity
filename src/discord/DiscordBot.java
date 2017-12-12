package discord;

import java.io.File;
import java.util.HashMap;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import logger.RunUI;

public class DiscordBot {

	public static final String BOT_TOKEN = "MzMyNjc0OTcyOTc3OTg3NTg0.DEB1mQ.Rs7Tz1D_zCv6-SNKgfkZp6jLaxo";
	public static final String GENERAL_CHANNEL_ID = "332674456830869507";

	public static String TAG = "DiscordBot";

	// ids of channels

	static HashMap<String, Channel> idAndChannel = new HashMap<String, Channel>();
	static DiscordAPI api = Javacord.getApi(BOT_TOKEN, true);

	static RunUI runUI;

	public DiscordBot(RunUI runUI) {
		this.runUI = runUI;
		runBot();
	}

	public void runBot() {

		DiscordMessageListener messageListener = new DiscordMessageListener(runUI.statusHandler);
		api.connect(new FutureCallback<DiscordAPI>() {

			@Override
			public void onSuccess(DiscordAPI api) {
				System.out.println("Discord bot connected!");
				api.registerListener(messageListener);

				// init channels

				idAndChannel.put(GENERAL_CHANNEL_ID, api.getChannelById(GENERAL_CHANNEL_ID));

				System.out.println("Discord bot ready.");

				setStatus(false);

			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
				print(TAG, "Discord bot failed to connect.", true);
			}
		});

	}

	public static void setStatus(boolean isOn) {
		api.setGame("OFF");
	}

	public void sendFile(File file, String information) {
		if (file == null) {
			System.err.println("sendFile file is null!");
		} else {
			Channel c = getChannelForId(GENERAL_CHANNEL_ID);

			if (c == null) {
				System.err.println("channel is null!");
			} else {
				getChannelForId(GENERAL_CHANNEL_ID).sendFile(file, information);
			}

		}

	}

	public void print(String tag, String toPrint, boolean isError) {
		runUI.print(tag, toPrint, isError);
	}

	public Channel getChannelForId(String channelId) {
		if (!idAndChannel.containsKey(channelId)) {
			idAndChannel.put(channelId, api.getChannelById(channelId));
		}

		return idAndChannel.get(channelId);
	}
}