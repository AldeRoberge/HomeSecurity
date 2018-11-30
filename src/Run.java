import java.awt.EventQueue;

import javax.swing.JFrame;

import alde.commons.util.UtilityJFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class Run {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Run window = new Run();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Run() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new UtilityJFrame();
		frame.setTitle("HS");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel webcamPanel = new JPanel();
		tabbedPane.addTab("Webcam", null, webcamPanel, null);
		
		JPanel loggerPanel = new JPanel();
		tabbedPane.addTab("Logger", null, loggerPanel, null);
	}

}
