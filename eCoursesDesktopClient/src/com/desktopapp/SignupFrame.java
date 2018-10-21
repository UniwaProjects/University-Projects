package com.desktopapp;

import com.classes.Professor;
import com.classes.XMLHandler;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class SignupFrame extends JFrame implements ActionListener {

    private static SignupFrame sfInstance;
    private final JButton submitButton;
    private final JButton closeButton;
    private final JTextField fnameText;
    private final JTextField lnameText;
    private final JComboBox eduLevelBox;
    private final JTextField usernameText;
    private final JTextField passwordText;

    public static SignupFrame newInstance() {
        if (sfInstance != null) {
            sfInstance.dispose();
        }
        try {
            sfInstance = new SignupFrame();
        } catch (JAXBException ex) {
            Logger.getLogger(SignupFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sfInstance;
    }

    SignupFrame() throws JAXBException {

        setTitle("Sign Up");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(""));

        JPanel centerPanel = new JPanel(new GridLayout(7, 1));

        JPanel row = new JPanel(new GridLayout(1, 3));
        JLabel fnameLabel = new JLabel("First Name:");
        row.add(fnameLabel);
        fnameText = new JTextField();
        row.add(fnameText);
        JLabel noticeLabel = new JLabel("  Between 2-45 characters long.");
        row.add(noticeLabel);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel lnameLabel = new JLabel("Last Name:");
        row.add(lnameLabel);
        lnameText = new JTextField();
        row.add(lnameText);
        noticeLabel = new JLabel("  Between 2-45 characters long.");
        row.add(noticeLabel);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel eduLevelLabel = new JLabel("Educational Level:");
        row.add(eduLevelLabel);
        String[] eduLevels = {"Professor", "Associate Professor", "Assistant Professor", "Lecturer"};
        eduLevelBox = new JComboBox(eduLevels);
        row.add(eduLevelBox);
        noticeLabel = new JLabel("");
        row.add(noticeLabel);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel usernameLabel = new JLabel("Username:");
        row.add(usernameLabel);
        usernameText = new JTextField();
        row.add(usernameText);
        noticeLabel = new JLabel("  Between 4-12 characters long.");
        row.add(noticeLabel);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel passwordLabel = new JLabel("Password:");
        row.add(passwordLabel);
        passwordText = new JTextField();
        row.add(passwordText);
        noticeLabel = new JLabel("  Between 6-12 characters long.");
        row.add(noticeLabel);
        centerPanel.add(row);

        panel.add(centerPanel, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);

        row = new JPanel(new GridLayout(1, 2));

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        row.add(submitButton);

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        row.add(closeButton);

        add(row, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(submitButton)) {

            if (fnameText.getText().length() >= 2
                    && fnameText.getText().length() <= 45
                    && lnameText.getText().length() >= 2
                    && lnameText.getText().length() <= 45
                    && usernameText.getText().length() >= 4
                    && usernameText.getText().length() <= 12
                    && passwordText.getText().length() >= 6
                    && passwordText.getText().length() <= 12) {

                Professor p = new Professor();
                p.setFirstname(fnameText.getText());
                p.setLastname(lnameText.getText());
                p.setEduLevel(eduLevelBox.getSelectedItem().toString());
                p.setUsername(usernameText.getText());
                p.setPassword(passwordText.getText());

                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Professor.class);
                    String path = "users/professor";
                    XMLHandler.marshallSend(path, p, jaxbContext);
                } catch (JAXBException ex) {
                    Logger.getLogger(SignupFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please fill out all the fields according to limitations.");
            }
        } else if (e.getSource().equals(closeButton)) {
            this.dispose();
        }
    }

}
