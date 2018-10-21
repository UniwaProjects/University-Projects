package com.desktopapp;

import com.classes.CommunicationHandler;
import com.classes.LearningOutcome;
import com.classes.LearningOutcomes;
import com.classes.SimpleCourse;
import com.classes.SimpleCourses;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class ProfessorFrame extends JFrame implements ActionListener {

    private static ProfessorFrame pfInstance;
    private final JButton detailsButton;
    private final JButton deleteButton;
    private final JButton createButton;
    private final JButton importButton;
    private final JTable courseTable;
    private final JTable outcomeTable;
    private JMenuItem logoutMenuITem;
    private DefaultTableModel model;

    private final CommunicationHandler com = new CommunicationHandler();

    public static ProfessorFrame getInstance() {
        if (pfInstance == null) {
            try {
                pfInstance = new ProfessorFrame();
            } catch (JAXBException ex) {
                Logger.getLogger(ProfessorFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pfInstance;
    }

    public static ProfessorFrame newInstance() {
        try {
            pfInstance.dispose();
            pfInstance = new ProfessorFrame();
        } catch (JAXBException ex) {
            Logger.getLogger(ProfessorFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pfInstance;
    }

    ProfessorFrame() throws JAXBException {

        setTitle("eCourse Manager");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        menuBar();

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.setBorder(BorderFactory.createTitledBorder(""));

        courseTable = getCourseTable();
        courseTable.setDefaultEditor(Object.class, null);
        coursesPanel.add(new JScrollPane(courseTable), BorderLayout.NORTH);

        JPanel row = new JPanel(new GridLayout(1, 3));
        detailsButton = new JButton("Details");
        detailsButton.addActionListener(this);
        row.add(detailsButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        row.add(deleteButton);

        createButton = new JButton("Create new");
        createButton.addActionListener(this);
        row.add(createButton);

        coursesPanel.add(row, BorderLayout.CENTER);

        row = new JPanel(new GridLayout(1, 2));
        JLabel fileLabel = new JLabel("Import courses from a .csv file ");
        row.add(fileLabel);

        importButton = new JButton("Import");
        importButton.addActionListener(this);
        row.add(importButton);
        coursesPanel.add(row, BorderLayout.SOUTH);
        
        tabbedPane.addTab("My Courses", coursesPanel);

        JPanel outcomesPanel = new JPanel(new BorderLayout());
        outcomesPanel.setBorder(BorderFactory.createTitledBorder(""));
        outcomeTable = getOutcomesTable();
        outcomeTable.setDefaultEditor(Object.class, null);
        outcomesPanel.add(new JScrollPane(outcomeTable), BorderLayout.CENTER);

        tabbedPane.addTab("Learning Outcomes", outcomesPanel);

        add(tabbedPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void menuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
        logoutMenuITem = new JMenuItem("Exit");
        logoutMenuITem.addActionListener(this);
        menu.add(logoutMenuITem);
        setJMenuBar(menuBar);
    }

    private JTable getCourseTable() throws JAXBException {
        SimpleCourses myCourses;
        JAXBContext jaxbContext = JAXBContext.newInstance(SimpleCourses.class);
        String path = "courses/professor/" + Main.t.getId();
        myCourses = (SimpleCourses) XMLHandler.getUnmarshall(path, jaxbContext);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("English Title");
        model.addColumn("Greek Title");

        for (SimpleCourse sc : myCourses.getCourses()) {
            String[] row = {String.valueOf(sc.getCourseId()), sc.getEnglishTitle(),
                sc.getGreekTitle()};
            model.addRow(row);
        }

        JTable courses = new JTable(model);
        return courses;
    }

    private JTable getOutcomesTable() throws JAXBException {
        LearningOutcomes los;
        JAXBContext jaxbContext = JAXBContext.newInstance(LearningOutcomes.class);
        String path = "outcomes/all";
        los = (LearningOutcomes) XMLHandler.getUnmarshall(path, jaxbContext);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Code");
        model.addColumn("Field");
        model.addColumn("Category");
        model.addColumn("Number");
        model.addColumn("Description");
        model.addColumn("Mastery Level");

        for (LearningOutcome lo : los.getOutcomes()) {
            String[] row = {lo.getId(),
                lo.getField(),
                lo.getCategory(),
                String.valueOf(lo.getNumber()),
                lo.getDescription(),
                lo.getMasteryLevel()};
            model.addRow(row);
        }

        JTable outcomes = new JTable(model);
        return outcomes;
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
        }//DELETE BUTTON
        else if (e.getSource().equals(deleteButton)) {
            int row = courseTable.getSelectedRow();
            int column = 0;
            String courseId = courseTable.getModel().getValueAt(row, column).toString();
            int del = JOptionPane.showConfirmDialog(null,
                    "Are you sure you wish to delete course with ID: " + courseId,
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (del == 0) {
                try {
                    com.sendPost("courses/delete", "id=" + courseId);
                } catch (Exception ex) {
                    Logger.getLogger(ProfessorFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                model.removeRow(row);
            }
        }//COURSE DETAILS
        else if (e.getSource().equals(detailsButton)) {
            int row = courseTable.getSelectedRow();
            int column = 1;
            String title = courseTable.getModel().getValueAt(row, column).toString();
            try {
                CourseDetailsFrame cd = new CourseDetailsFrame(title);
            } catch (JAXBException ex) {
                Logger.getLogger(Main.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }//NEW COURSE
        else if (e.getSource().equals(createButton)) {
            NewCourseFrame nc = NewCourseFrame.newInstance();
        }//IMPORT
        else if (e.getSource().equals(importButton)) {
            try {
                ImportFromCsv ifc = new ImportFromCsv();
            } catch (JAXBException ex) {
                Logger.getLogger(ProfessorFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
