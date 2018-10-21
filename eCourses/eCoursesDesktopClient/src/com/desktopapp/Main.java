package com.desktopapp;

import com.classes.Credentials;
import com.classes.Professor;
import com.classes.Token;
import com.classes.XMLHandler;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class Main extends JFrame implements ActionListener {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton signupButton;
    public static Token t;

    public Main() {
        setTitle("Log In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createTitledBorder(""));

        JPanel row = new JPanel(new GridLayout(1, 2));
        row.add(new JLabel("Username:"));
        usernameField = new JTextField(12);
        row.add(usernameField);
        panel.add(row);

        row = new JPanel(new GridLayout(1, 2));
        row.add(new JLabel("Password:"));
        passwordField = new JPasswordField(12);
        row.add(passwordField);
        panel.add(row);

        row = new JPanel(new GridLayout(1, 2));
        signupButton = new JButton("Sign Up");
        signupButton.addActionListener(this);
        row.add(signupButton);

        loginButton = new JButton("Log In");
        loginButton.addActionListener(this);
        row.add(loginButton);
        panel.add(row);

        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void logOut() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Token.class);
        String path = "users/logout";
        XMLHandler.marshallSend(path, t, jaxbContext);
    }

    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main da = new Main();
                da.setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(loginButton)) {
            Credentials c = new Credentials();
            c.setUsername(usernameField.getText());
            c.setPassword(passwordField.getText());
            try {
                t = XMLHandler.authenticate(c);
                if (t.getUser().equals("students")) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Token.class);
                    String path = "users/logout";
                    XMLHandler.marshallSend(path, t, jaxbContext);
                    throw new FailedLoginException();

                } else if (t.getUser().equals("professors")) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Professor.class);
                    String path = "users/professor/" + t.getId();
                    Professor prof = (Professor) XMLHandler.getUnmarshall(path, jaxbContext);
                    if (prof.isActivated()) {
                        ProfessorFrame pf = ProfessorFrame.getInstance();
                    } else {
                        throw new LoginException();
                    }
                } else if (t.getUser().equals("administrator")) {
                    AdminFrame af = AdminFrame.getInstance();
                }
                setVisible(false);
            } catch (FailedLoginException ex) {
                JOptionPane.showMessageDialog(this,
                        "Authentication failed, wrong credentials.");
            } catch (LoginException ex) {
                JOptionPane.showMessageDialog(this,
                        "Authentication failed, not an active account.");
            } catch (JAXBException ex) {
                JOptionPane.showMessageDialog(this,
                        "A server error occurred, try again later.");
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (e.getSource().equals(signupButton)) {
            SignupFrame sf = SignupFrame.newInstance();
        }
    }

}
