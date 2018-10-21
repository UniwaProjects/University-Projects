<%@page import="com.classes.DetailedCourse"%>
<%@page import="javax.xml.bind.JAXBContext"%>
<%@page import="com.classes.DetailedCourses"%>
<%@page import="com.classes.XMLHandler"%>
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
        <title>Detailed Courses</title>
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
                JAXBContext jaxbContext = JAXBContext.newInstance(DetailedCourses.class);
                String path = "courses/all/details";
                DetailedCourses dcs = new DetailedCourses();
                dcs = (DetailedCourses) XMLHandler.getUnmarshall(path, jaxbContext);
            %>
            <br><table style="width:100%">
                <thead>
                    <tr>
                        <th colspan="5">All Available Online Courses</th>
                    </tr>
                </thead>
                <tr>
                    <th>Professor</th>
                    <th>English Title</th>
                    <th>Greek Title</th> 
                    <th>Semester</th>
                    <th></th>
                </tr>
                <%
                    for (DetailedCourse dc : dcs.getCourses()) {
                %>
                <tr>
                    <td><%=dc.getProfessor()%></td>
                    <td><%=dc.getEnglishTitle()%></td>
                    <td><%=dc.getGreekTitle()%></td>
                    <td><%=dc.getSemester()%></td>
                    <td><a href="course-details.jsp?title=<%=dc.getEnglishTitle()%>">
                            More Details</a>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
        </div>
    </body>
</html>
