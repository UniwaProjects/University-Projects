<%@page import="com.classes.SimpleCourse"%>
<%@page import="com.classes.XMLHandler"%>
<%@page import="javax.xml.bind.JAXBContext"%>
<%@page import="com.classes.SimpleCourses"%>
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
        <title>Search Results</title>
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
                if (request.getParameter("find").equals("")) {
            %>
            <br><b>No matching courses found.</b>
            <%
            } else {
                String s = (String) request.getParameter("find").replace(" ", "%20");
                String path = "";

                if (request.getParameter("criterion").equals("professor")) {
                    path = "courses/find/professor/" + s;
            %>
            <br><b>Courses taught by Professor </b>
            <%
            } else if (request.getParameter("criterion").equals("title")) {
                path = "courses/find/" + s;
            %>
            <br><b>Courses matching </b>
            <%
            } else if (request.getParameter("criterion").equals("outcome")) {
                path = "courses/outcome/" + s;
            %>
            <br><b>Courses fitting learning outcome </b>
            <%
                } else {
                    response.sendRedirect("index.jsp");
                }

                JAXBContext jaxbContext = JAXBContext.newInstance(SimpleCourses.class);
                SimpleCourses foundCourses = new SimpleCourses();
                foundCourses = (SimpleCourses) XMLHandler.getUnmarshall(path, jaxbContext);

            %>
            <b>"<%=request.getParameter("find")%>"</b>
            <%
                if (foundCourses.getCourses().isEmpty()) {
            %>
            <b> not found.</b>
            <%
            } else {
            %>
            <br><br><table style="width:100%">
                <tr>
                    <th>English Title</th>
                    <th>Greek Title</th> 
                    <th></th>
                </tr>
                <%
                    for (SimpleCourse sc : foundCourses.getCourses()) {
                %>
                <tr>
                    <td><%=sc.getEnglishTitle()%></td>
                    <td><%=sc.getGreekTitle()%></td>
                    <td><a href="course-details.jsp?title=<%=sc.getEnglishTitle()%>">
                            See Details</a></td>
                </tr>
                <%
                    }
                %>
            </table>
            <%
                    }
                }
            %>
        </div>
    </body>
</html>
