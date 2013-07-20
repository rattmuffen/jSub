package st.rattmuffen.jsub.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import st.rattmuffen.jsub.exceptions.NoCredentialFileFoundException;
import st.rattmuffen.jsub.util.UserCredentials;
import st.rattmuffen.jsub.util.Utils;

/**
 * Panel containing username and password fields among other things.
 * Made with WindowBuilder.
 * @version 0.3
 * @author rattmuffen
 */
public class LoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField txtUsernametextfield;
	
	JSubFrame controller;
	private JPasswordField passwordField;

	/**
	 * Create the panel.
	 */
	public LoginPanel(JSubFrame parent) {
		this.controller = parent;
		
		setLayout(new BorderLayout(0, 0));
		
		JPanel credentialsPanel = new JPanel();
		add(credentialsPanel, BorderLayout.CENTER);
		credentialsPanel.setLayout(new GridLayout(2, 2, 0, 0));
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		credentialsPanel.add(lblUsername);
		
		txtUsernametextfield = new JTextField();
		credentialsPanel.add(txtUsernametextfield);
		txtUsernametextfield.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		credentialsPanel.add(lblPassword);
		
		passwordField = new JPasswordField();
		credentialsPanel.add(passwordField);
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnLogin = new JButton("Login");
		
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (txtUsernametextfield.getText().equals("") || passwordField.getPassword().equals("")) {
					controller.setViewByLoginSuccess(false);
				} else {
					UserCredentials uc = new UserCredentials(txtUsernametextfield.getText(), passwordField.getText());
					
					try {
						uc.save("credentials");
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (NoCredentialFileFoundException ex2) {
						uc.createFileAndSave("credentials");
					}
					controller.relogin(uc);
				}
			}
		});
		buttonPanel.add(btnLogin);
		
		JButton btnRegisterUser = new JButton("Register user");
		btnRegisterUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Utils.openURL(new URL("http://www.opensubtitles.org/en/newuser"));
				} catch (MalformedURLException e1) {}
			}
		});
		buttonPanel.add(btnRegisterUser);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		buttonPanel.add(btnExit);
	}
}
