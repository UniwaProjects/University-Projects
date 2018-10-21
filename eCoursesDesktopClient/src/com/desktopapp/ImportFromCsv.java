package com.desktopapp;

import com.classes.DetailedCourse;
import com.classes.DetailedCourses;
import com.classes.XMLHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFrame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class ImportFromCsv extends JFrame {

    private static ImportFromCsv ifcInstance;
    private File csvFile;

    public static ImportFromCsv newInstance() {
        if (ifcInstance != null) {
            ifcInstance.dispose();
        }
        try {
            ifcInstance = new ImportFromCsv();
        } catch (JAXBException ex) {
            Logger.getLogger(ImportFromCsv.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ifcInstance;
    }

    ImportFromCsv() throws JAXBException {

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setFileFilter(new FileNameExtensionFilter(".csv", "csv"));

        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            csvFile = fc.getSelectedFile();
            uploadCourses(getCoursesFromCsv(csvFile));
            this.dispose();
            ProfessorFrame pf = ProfessorFrame.newInstance();
        }
        if (returnVal == JFileChooser.CANCEL_OPTION) {
            dispose();
        }
    }

    private DetailedCourses getCoursesFromCsv(File file) {

        boolean error = false;
        DetailedCourses dcs = new DetailedCourses();
        List<DetailedCourse> list = new ArrayList<>();

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 7) {
                    DetailedCourse dc = new DetailedCourse();

                    dc.setProfId(Main.t.getId());
                    dc.setEnglishTitle(fields[0]);
                    dc.setGreekTitle(fields[1]);
                    dc.setEduLevel(fields[2]);
                    dc.setSemester(Integer.parseInt(fields[3]));

                    List<String> outcomesList = new ArrayList<>();
                    try {
                        String[] outcomes = fields[4].split("#");
                        for (int i = 0; i < outcomes.length; i++) {
                            outcomesList.add(outcomes[i]);
                        }
                    } catch (PatternSyntaxException e) {
                        outcomesList.add(fields[4]);
                    }
                    dc.setOutcomes(outcomesList);

                    List<String> coursesList = new ArrayList<>();
                    try {
                        String[] courses = fields[5].split("#");
                        for (int i = 0; i < courses.length; i++) {
                            coursesList.add(courses[i]);
                        }
                    } catch (PatternSyntaxException e) {
                        coursesList.add(fields[5]);
                    }
                    dc.setReqCourses(coursesList);

                    List<String> reqOutcomesList = new ArrayList<>();
                    try {
                        String[] reqOutcomes = fields[6].split("#");
                        for (int i = 0; i < reqOutcomes.length; i++) {
                            reqOutcomesList.add(reqOutcomes[i]);
                        }
                    } catch (PatternSyntaxException e) {
                        reqOutcomesList.add(fields[6]);
                    }
                    dc.setReqOutcomes(reqOutcomesList);

                    list.add(dc);
                } else {
                    error = true;
                }
            }

            if (error == true) {
                JOptionPane.showMessageDialog(this,
                        "One or more entries are invalid and were ignored.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        dcs.setCourses(list);
        return dcs;
    }

    private void uploadCourses(DetailedCourses dcs) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DetailedCourses.class);
            String path = "courses/fileupload";
            XMLHandler.marshallSend(path, dcs, jaxbContext);
        } catch (JAXBException ex) {
            Logger.getLogger(ImportFromCsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
