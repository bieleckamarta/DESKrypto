package crypt;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {

	private JFrame frame;
	private JPanel contentPane;
	private JButton encryptButton;
	private JRadioButton encryptFromFileCheckbox;
	private JButton decryptButton;
	private JRadioButton decryptFromFileCheckbox;
	private JTextField textArea;
	private boolean encryptFromFileOption = false; 
	private boolean decryptFromFileOption = false; 
	private Controller controller;
	private JLabel statusInfoLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Create the application.
	 */
	public App() {
		initialize();
		controller = new Controller();
		addButtonListeners();
		
	}
	
	public void addButtonListeners() {

		encryptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					if(textArea.getText().isEmpty()) {
						showErrorDialog("Password must not be empty");
					} else { 
						if(controller.encryptFromFile(textArea.getText())) { 
							statusInfoLabel.setText("File encrypted");
						} else {
							statusInfoLabel.setText("Encryption failed. Make sure you chose the right file.");
						}
					}
			}
		});
		
		decryptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					
					if(textArea.getText().isEmpty()) {
						showErrorDialog("Password must not be empty");
					} else { 
						if(controller.decryptFromFile(textArea.getText())) { 
							statusInfoLabel.setText("File encrypted");
						} else {
							statusInfoLabel.setText("Decryption failed. Make sure you chose the right file.");
						}
					}
				}		
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{30, 77, 35, 269, 0};
		gridBagLayout.rowHeights = new int[]{36, 25, 25, 35, 25, 26, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		encryptButton = new JButton("Encrypt");
		GridBagConstraints gbc_encryptButton = new GridBagConstraints();
		gbc_encryptButton.insets = new Insets(0, 0, 5, 5);
		gbc_encryptButton.gridx = 1;
		gbc_encryptButton.gridy = 1;
		frame.getContentPane().add(encryptButton, gbc_encryptButton);
		
		JLabel passwdLabel = new JLabel("Password: ");
		GridBagConstraints gbc_passwdLabel = new GridBagConstraints();
		gbc_passwdLabel.fill = GridBagConstraints.BOTH;
		gbc_passwdLabel.gridx = 3;
		gbc_passwdLabel.gridy = 1;
		frame.getContentPane().add(passwdLabel, gbc_passwdLabel);
		
		textArea = new JTextField();
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 3;
		gbc_textArea.gridy = 2;
		frame.getContentPane().add(textArea, gbc_textArea);
		
		statusInfoLabel = new JLabel("");
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.insets = new Insets(0, 0, 5, 5);
		gbc_statusLabel.gridx = 3;
		gbc_statusLabel.gridy = 4;
		frame.getContentPane().add(statusInfoLabel, gbc_statusLabel);
		
		decryptButton = new JButton("Decrypt");
		GridBagConstraints gbc_decryptButton = new GridBagConstraints();
		gbc_decryptButton.insets = new Insets(0, 0, 5, 5);
		gbc_decryptButton.gridx = 1;
		gbc_decryptButton.gridy = 4;
		frame.getContentPane().add(decryptButton, gbc_decryptButton);
		
	}
	
	public void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(null,
				msg, "Error",
				JOptionPane.WARNING_MESSAGE);
	}

}
