package com.servlets;

import com.classes.DetailedCourse;
import com.classes.DetailedCourses;
import com.classes.XMLHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class UploadCsv extends HttpServlet {

    private int id;
    private boolean error = false;
    private String errorMsg = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        id = (int) session.getAttribute("userid");
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                if (item.isFormField()) {
                } else {
                    //String fieldName = item.getFieldName();
                    String fileName = FilenameUtils.getName(item.getName());
                    String ext = FilenameUtils.getExtension(fileName);
                    if (ext.equals("csv")) {

                        InputStream fileContent = item.getInputStream();
                        DetailedCourses dcs = getCoursesFromCsv(fileContent);
                        uploadCourses(dcs);

                    } else {
                        error = true;
                        errorMsg = "Wrong type of file, please select a .csv file.";
                    }
                }

            }
            if (error == false) {
                response.sendRedirect("index.jsp");
            } else {
                request.setAttribute("error", errorMsg);
                request.getRequestDispatcher("index.jsp").forward(request, response);
                errorMsg = "";
            }
        } catch (FileUploadException ex) {
            Logger.getLogger(UploadCsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DetailedCourses getCoursesFromCsv(InputStream is) {

        DetailedCourses dcs = new DetailedCourses();
        List<DetailedCourse> list = new ArrayList<>();

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 7) {
                    DetailedCourse dc = new DetailedCourse();

                    dc.setProfId(id);
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
                    errorMsg = "One or more entries are invalid and were ignored.";
                }
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
            Logger.getLogger(UploadCsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
