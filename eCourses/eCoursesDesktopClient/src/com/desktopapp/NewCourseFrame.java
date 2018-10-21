package com.desktopapp;

import com.classes.DetailedCourse;
import com.classes.LearningOutcome;
import com.classes.LearningOutcomes;
import com.classes.SimpleCourse;
import com.classes.SimpleCourses;
import com.classes.XMLHandler;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class NewCourseFrame extends JFrame implements ActionListener {

    private static NewCourseFrame ncInstance;
    private final JButton submitButton;
    private final JButton closeButton;
    private final JTextField etitleText;
    private final JTextField gtitleText;
    private final JComboBox eduLevelBox;
    private final JComboBox semesterBox;
    private final List<JCheckBox> loCbarr;
    private final List<JCheckBox> rcCbarr;
    private final List<JCheckBox> rloCbarr;
    private SimpleCourses scs;

    public static NewCourseFrame newInstance() {
        if (ncInstance != null) {
            ncInstance.dispose();
        }
        try {
            ncInstance = new NewCourseFrame();
        } catch (JAXBException ex) {
            Logger.getLogger(NewCourseFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ncInstance;
    }

    NewCourseFrame() throws JAXBException {

        setTitle("New Course");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(""));

        JPanel centerPanel = new JPanel(new GridLayout(5, 1));

        JPanel row = new JPanel(new GridLayout(1, 3));
        JLabel etitleLabel = new JLabel("English Title:");
        row.add(etitleLabel);
        etitleText = new JTextField();
        row.add(etitleText);
        JLabel noticeLabel = new JLabel("  Between 2-45 characters long.");
        row.add(noticeLabel);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel gtitleLabel = new JLabel("Greek Title:");
        row.add(gtitleLabel);
        gtitleText = new JTextField();
        row.add(gtitleText);
        noticeLabel = new JLabel("  Between 2-45 characters long.");
        row.add(noticeLabel);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel eduLevelLabel = new JLabel("Educational Level:");
        row.add(eduLevelLabel);
        String[] eduLevels = {"Undergraduate", "Postgraduate"};
        eduLevelBox = new JComboBox(eduLevels);
        row.add(eduLevelBox);
        noticeLabel = new JLabel("");
        row.add(noticeLabel);
        centerPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel semesterLabel = new JLabel("Semester:");
        row.add(semesterLabel);
        String[] semesters = {"1", "2", "3", "4", "5", "6", "7", "8"};
        semesterBox = new JComboBox(semesters);
        row.add(semesterBox);
        noticeLabel = new JLabel("");
        row.add(noticeLabel);
        centerPanel.add(row);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new GridLayout(3, 1));

        JAXBContext jaxbContext = JAXBContext.newInstance(LearningOutcomes.class);
        String path = "outcomes/all";
        LearningOutcomes los = new LearningOutcomes();
        los = (LearningOutcomes) XMLHandler.getUnmarshall(path, jaxbContext);

        jaxbContext = JAXBContext.newInstance(SimpleCourses.class);
        path = "courses/all";
        scs = new SimpleCourses();
        scs = (SimpleCourses) XMLHandler.getUnmarshall(path, jaxbContext);

        row = new JPanel(new GridLayout(1, 3));
        JLabel loLabel = new JLabel("Learning Outcomes:");
        row.add(loLabel);

        JPanel cbPanel = new JPanel(new GridLayout(0, 2));
        loCbarr = new ArrayList<>();
        for (LearningOutcome lo : los.getOutcomes()) {
            JCheckBox cb = new JCheckBox(lo.getId());
            loCbarr.add(cb);
            cbPanel.add(cb);
        }
        row.add(cbPanel);
        noticeLabel = new JLabel("");
        row.add(noticeLabel);
        southPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel rcLabel = new JLabel("Required Courses:");
        row.add(rcLabel);

        cbPanel = new JPanel(new GridLayout(0, 2));
        rcCbarr = new ArrayList<>();
        for (SimpleCourse sc : scs.getCourses()) {
            JCheckBox cb = new JCheckBox(sc.getEnglishTitle());
            rcCbarr.add(cb);
            cbPanel.add(cb);
        }
        row.add(cbPanel);
        noticeLabel = new JLabel("");
        row.add(noticeLabel);
        southPanel.add(row);

        row = new JPanel(new GridLayout(1, 3));
        JLabel rloLabel = new JLabel("Required Learning Outcomes:");
        row.add(rloLabel);

        cbPanel = new JPanel(new GridLayout(0, 2));
        rloCbarr = new ArrayList<>();
        for (LearningOutcome lo : los.getOutcomes()) {
            JCheckBox cb = new JCheckBox(lo.getId());
            rloCbarr.add(cb);
            cbPanel.add(cb);
        }
        row.add(cbPanel);
        noticeLabel = new JLabel("");
        row.add(noticeLabel);
        southPanel.add(row);

        panel.add(southPanel, BorderLayout.SOUTH);
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
            if (etitleText.getText().length() >= 2
                    && etitleText.getText().length() <= 45
                    && gtitleText.getText().length() >= 2
                    && gtitleText.getText().length() <= 45) {

                DetailedCourse course = new DetailedCourse();
                course.setProfId(Main.t.getId());
                course.setGreekTitle(gtitleText.getText());
                course.setEnglishTitle(etitleText.getText());
                course.setEduLevel(eduLevelBox.getSelectedItem().toString());
                course.setSemester(Integer.parseInt(semesterBox.getSelectedItem().toString()));

                List<String> lo = new ArrayList<>();
                for (JCheckBox cb : loCbarr) {
                    if (cb.isSelected()) {
                        lo.add(cb.getText());
                    }
                }
                course.setOutcomes(lo);

                List<String> rc = new ArrayList<>();
                for (JCheckBox cb : rcCbarr) {
                    if (cb.isSelected()) {
                        for (SimpleCourse sc : scs.getCourses()) {
                            if (sc.getEnglishTitle().equals(cb.getText())) {
                                rc.add(String.valueOf(sc.getCourseId()));
                            }
                        }
                    }
                }
                course.setReqCourses(rc);

                List<String> rlo = new ArrayList<>();
                for (JCheckBox cb : rloCbarr) {
                    if (cb.isSelected()) {
                        rlo.add(cb.getText());
                    }
                }
                course.setReqOutcomes(rlo);

                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(DetailedCourse.class);
                    String path = "courses/insert";
                    XMLHandler.marshallSend(path, course, jaxbContext);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.dispose();
                ProfessorFrame pf = ProfessorFrame.newInstance();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please fill out all the fields according to limitations.");
            }

        } else if (e.getSource().equals(closeButton)) {
            this.dispose();
        }
    }

}
