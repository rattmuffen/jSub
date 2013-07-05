package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import exceptions.NoCredentialFileFoundException;

import util.UserCredentials;

public class LoginView extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	public JButton exitButton,loginButton;
	
	JSubFrame controller;
	
	JTextField usernameField;
	JPasswordField passwordField;
	
	
	
	public LoginView(JSubFrame parent) {
		super(new BorderLayout());
		
		controller = parent;
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);
		
		
		loginButton = new JButton("Login");
		loginButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(loginButton);
		buttonPanel.add(exitButton);
		
		
		usernameField = new JTextField("");
		passwordField = new JPasswordField("");

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		inputPanel.add(usernameField);
		inputPanel.add(passwordField);
		
		this.add(inputPanel,BorderLayout.CENTER);
		this.add(buttonPanel,BorderLayout.EAST);
	}
	
	
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		
		if (source == exitButton) {
			System.exit(0);
		}
		
		if (source == loginButton) {
			if (usernameField.getText().equals("") || passwordField.getPassword().equals("")) {
				controller.setViewByLoginSuccess(false);
			} else {
				UserCredentials uc = new UserCredentials(usernameField.getText(), passwordField.getText());
				try {
					uc.save("credentials");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NoCredentialFileFoundException e) {
					uc.createFileAndSave("credentials");
				}
				controller.relogin(uc);
			}
		}
		
		
	}
}
