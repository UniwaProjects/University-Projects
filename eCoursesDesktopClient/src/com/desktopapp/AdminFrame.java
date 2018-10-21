package com.desktopapp;

import com.classes.CommunicationHandler;
import com.classes.Professor;
import com.classes.Professors;
import com.classes.XMLHandler;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class AdminFrame extends JFrame implements ActionListener {

    private static AdminFrame admnInstance;
    private final JButton activateButton;
    private final JButton deactivateButton;
    private final JTable profTable;
    private JMenuItem logoutMenuITem;
    private DefaultTableModel model;
    private final CommunicationHandler com = new CommunicationHandler();

    public static AdminFrame getInstance() {
        if (admnInstance == null) {
            try {
                admnInstance = new AdminFrame();
            } catch (JAXBException ex) {
                Logger.getLogger(AdminFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return admnInstance;
    }

    public static AdminFrame newInstance() {
        try {
            admnInstance.dispose();
            admnInstance = new AdminFrame();
        } catch (JAXBException ex) {
            Logger.getLogger(AdminFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return admnInstance;
    }

    AdminFrame() throws JAXBException {

        setTitle("Professor Manager");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        menuBar();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(""));

        profTable = getProfTable();
        profTable.setDefaultEditor(Object.class, null);
        panel.add(new JScrollPane(profTable), BorderLayout.CENTER);

        JPanel row = new JPanel(new GridLayout(1, 2));
        activateButton = new JButton("Activate");
        activateButton.addActionListener(this);
        row.add(activateButton);

        deactivateButton = new JButton("Deactivate");
        deactivateButton.addActionListener(this);
        row.add(deactivateButton);

        panel.add(row, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void menuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
        logoutMenuITem = new JMenuItem("Exit");
        logoutMenuITem.addActionListener(this);
        menu.add(logoutMenuITem);
        setJMenuBar(menuBar);
    }

    public JTable getProfTable() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Professors.class);
        String path = "users/professors";
        Professors profs = new Professors();
        profs = (Professors) XMLHandler.getUnmarshall(path, jaxbContext);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Education Level");
        model.addColumn("Activated");

        for (Professor p : profs.getProfs()) {
            String[] row = {String.valueOf(p.getProfId()), p.getFirstname(),
                p.getLastname(), p.getEduLevel(), String.valueOf(p.isActivated())};
            model.addRow(row);
        }

        JTable professors = new JTable(model);
        return professors;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //LOGOUT BUTTON
        if (e.getSource().equals(logoutMenuITem)) {
            try {
                Main.logOut();
            } catch (JAXBException ex) {
                Logger.getLogger(Main.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }//ACTIVATE BUTTON
        else if (e.getSource().equals(activateButton)) {
            int row = profTable.getSelectedRow();
            int column = 0;
            String profId = profTable.getModel().getValueAt(row, column).toString();
            try {
                com.sendPost("users/professor/activate", "id=" + profId);
            } catch (Exception ex) {
                Logger.getLogger(AdminFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            //x.put("professor/activate", profId);
            AdminFrame af = AdminFrame.newInstance();
        }//DEACTIVATE BUTTON
        else if (e.getSource().equals(deactivateButton)) {
            int row = profTable.getSelectedRow();
            int column = 0;
            String profId = profTable.getModel().getValueAt(row, column).toString();

            try {
                com.sendPost("users/professor/deactivate", "id=" + profId);
            } catch (Exception ex) {
                Logger.getLogger(AdminFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            AdminFrame af = AdminFrame.newInstance();
        }
    }

}
