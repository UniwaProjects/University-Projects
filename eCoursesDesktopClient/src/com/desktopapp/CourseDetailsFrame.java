package com.desktopapp;

import com.classes.DetailedCourse;
import com.classes.XMLHandler;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class CourseDetailsFrame extends JFrame implements ActionListener {

    private final JButton closeButton;

    CourseDetailsFrame(String title) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(DetailedCourse.class);
        
        String t = (String) title.replace(" ", "%20");
        String path = "courses/" + t;
        DetailedCourse dc = (DetailedCourse) XMLHandler.getUnmarshall(path, jaxbContext);

        setTitle("Course Details");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(""));

        JPanel centerPanel = new JPanel(new GridLayout(5, 1));

        JPanel row = new JPanel(new GridLayout(1, 2));
        JLabel etitleLabel = new JLabel("English Title:");
        row.add(etitleLabel);
        JTextField etitleText = new JTextField(dc.getEnglishTitle());
        etitleText.setEditable(false);
        row.add(etitleText);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 2));
        JLabel gtitleLabel = new JLabel("Greek Title:");
        row.add(gtitleLabel);
        JTextField gtitleText = new JTextField(dc.getGreekTitle());
        gtitleText.setEditable(false);
        row.add(gtitleText);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 2));
        JLabel eduLevelLabel = new JLabel("Educational Level:");
        row.add(eduLevelLabel);
        JTextField eduLevelText = new JTextField(dc.getEduLevel());
        eduLevelText.setEditable(false);
        row.add(eduLevelText);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 2));
        JLabel semesterLabel = new JLabel("Semester:");
        row.add(semesterLabel);
        JTextField semesterText = new JTextField(String.valueOf(dc.getSemester()));
        semesterText.setEditable(false);
        row.add(semesterText);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 2));
        JLabel profLabel = new JLabel("Professor:");
        row.add(profLabel);
        JTextField profText = new JTextField(dc.getProfessor());
        profText.setEditable(false);
        row.add(profText);
        centerPanel.add(row);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new GridLayout(3, 1));

        String outcomes = "";
        for (String s : dc.getOutcomes()) {
            outcomes += s + "\n";
        }

        row = new JPanel(new GridLayout(1, 2));
        JLabel loLabel = new JLabel("Learning Outcomes:");
        row.add(loLabel);
        JTextArea loText = new JTextArea(outcomes);
        loText.setEditable(false);
        row.add(loText);
        southPanel.add(row);

        String reqCourses = "";
        for (String s : dc.getReqCourses()) {
            reqCourses += s + "\n";
        }

        row = new JPanel(new GridLayout(1, 2));
        JLabel rcLabel = new JLabel("Required Courses:");
        row.add(rcLabel);
        JTextArea rcText = new JTextArea(reqCourses);
        rcText.setEditable(false);
        row.add(rcText);
        southPanel.add(row);

        String reqOutcomes = "";
        for (String s : dc.getReqOutcomes()) {
            reqOutcomes += s + "\n";
        }

        row = new JPanel(new GridLayout(1, 2));
        JLabel roLabel = new JLabel("Required Outcomes:");
        row.add(roLabel);
        JTextArea roText = new JTextArea(reqOutcomes);
        roText.setEditable(false);
        row.add(roText);
        southPanel.add(row);

        panel.add(southPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        row.add(closeButton);

        add(closeButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(closeButton)) {
            this.dispose();
        }
    }
}
