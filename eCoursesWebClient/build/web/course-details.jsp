<%@page import="java.util.List"%>
<%@page import="com.classes.XMLHandler"%>
<%@page import="javax.xml.bind.JAXBContext"%>
<%@page import="com.classes.DetailedCourse"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link href="css_style.css" rel="stylesheet">
        <title>Course Details</title>
    </head>
    <body>
        <div id="centerContainer">

            <ul class="nav nav-pills">
                <li><a href="index.jsp">Courses</a></li>
                <li><a href="learning-outcomes.jsp">Learning Outcomes</a></li>
                    <%
                        if (session.getAttribute("token") == null
                                || session.getAttribute("token") == "") {
                    %>
                <li><a href="login.jsp">Log In</a></li>
                    <%
                    } else {
                    %>
                <li><a href="Logout">Log Out</a></li>
                    <%
                        }
                    %>
            </ul>

            <%
                String title = (String) request.getParameter("title").replace(" ", "%20");
                
                JAXBContext jaxbContext = JAXBContext.newInstance(DetailedCourse.class);
                String path = "courses/" + title;
                DetailedCourse dc = new DetailedCourse();
                dc = (DetailedCourse) XMLHandler.getUnmarshall(path, jaxbContext);
            %>
            <br><table style="width:100%">
                <tr>
                    <th colspan="2">Course Details</th>
                </tr>
                <tr>
                    <th>English Title</th>
                    <td><%=dc.getEnglishTitle()%></td>
                </tr>
                <tr>
                    <th>Greek Title</th>
                    <td><%=dc.getGreekTitle()%></td> 
                </tr>
                <tr>
                    <th>Educational Level</th>
                    <td><%=dc.getEduLevel()%></td>
                </tr>
                <tr>
                    <th>Semester</th>
                    <td><%=dc.getSemester()%></td>
                </tr>
                <tr>
                    <th>Professor</th>
                    <td><%=dc.getProfessor()%></td>
                </tr>
                <tr>
                    <th>Learning Outcomes</th>
                    <td><ol style="list-style-type:disc">
                            <%
                                List<String> outcomes = dc.getOutcomes();
                                for (String s : outcomes) {
                            %>
                            <li><%=s%></li> 
                                <%
                                    }
                                %>
                        </ol>
                    </td>
                </tr>
                <tr>
                    <th>Required Courses</th>
                    <td><ol style="list-style-type:disc">
                            <%
                                List<String> reqCourses = dc.getReqCourses();
                                for (String s : reqCourses) {
                            %>
                            <li><%=s%></li>
                                <%
                                    }
                                %>
                        </ol>
                    </td>          
                </tr>
                <tr>
                    <th>Required Learning Outcomes</th>
                    <td><ol style="list-style-type:disc">
                            <%
                                List<String> reqOutcomes = dc.getReqOutcomes();
                                for (String s : reqOutcomes) {
                            %>
                            <li><%=s%></li> 
                                <%
                                    }
                                %>
                        </ol>
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>
