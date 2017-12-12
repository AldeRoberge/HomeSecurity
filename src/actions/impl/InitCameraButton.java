package actions.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamPanel;

import actions.Setting;

public class InitCameraButton extends Setting {

	@Override
	public String getTAG() {
		return "Init Camera";
	}

	boolean cameraInit;
	JButton initButton;
	JLabel status;

	private boolean pause;

	public InitCameraButton() {
		initButton = new JButton("Init camera");
		initButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initCamera();
				initButton.setText("Pause");
			}
		});

		status = new JLabel("Waiting...");

	}

	private void switchPause() {

		pause = !pause;

		print("Paused set to " + pause, false);

		if (pause) {
			setStatus("Paused");
			initButton.setText("Resume");
		} else {
			setStatus("Resumed");
			initButton.setText("Pause");
		}

	}

	public void initCamera() {
		if (!cameraInit) {

			try {

				initButton.setText("Ready");

				cameraInit = true;

				// Init webcam

				Webcam webcam = Webcam.getDefault();

				webcam.setViewSize(new Dimension(640, 480));

				final WebcamMotionDetector detector = new WebcamMotionDetector(webcam);
				detector.setInterval(1000);
				detector.start();

				// frame

				WebcamPanel cameraPanel = new WebcamPanel(webcam);

				// frame

				Thread t = new Thread("motion-printer") {

					@Override
					public void run() {
						do {

							if (detector.isMotion()) {

								if (!pause) {

									BufferedImage image = webcam.getImage();

									String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())
											+ ".jpg";

									// save image to file
									try {

										File file = new File(
												new File(".").getAbsolutePath() + "\\archive\\" + fileName);

										ImageIO.write(image, "JPG", file);

										print("Movement detected", false);

										Calendar now = Calendar.getInstance();
										int hour = now.get(Calendar.HOUR_OF_DAY);
										int minute = now.get(Calendar.MINUTE);
										int second = now.get(Calendar.SECOND);

										String information = "Movement detected " + " " + hour + "h " + minute + "m "
												+ second + "s";

										runUI.discordBot.sendFile(file, information);
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								} else {
									print("Movement detected but software is paused... Not doing anything. ", true);
								}

							}

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								break;
							}
						} while (true);
					}
				};

				t.setDaemon(true);
				t.start();

				runUI.tabbedPane.addTab("Camera", null, cameraPanel, null);

				print("Successfully inited camera", false);
				status.setText("Working");

			} catch (Exception e) {
				e.printStackTrace();
				print("Exception in init camera : " + e.getMessage(), true);
				setStatus("Error : " + e.getMessage());
			}

		} else {
			switchPause();
		}

	}

	private void setStatus(String string) {
		status.setText("Status : " + string);
	}

	@Override
	public ArrayList<Component> getComponents() {
		ArrayList<Component> components = new ArrayList<Component>();
		components.add(initButton);
		components.add(status);
		return components;
	}

	@Override
	public void handleCommand(String command) {
		if (command.contains("init")) {
			initCamera();
		} else if (command.contains("pause")) {
			switchPause();
		} else if (command.contains("resume")) {
			switchPause();
		}
	}

	@Override
	public String getTriggerCommand() {
		return "camera";
	}

}
