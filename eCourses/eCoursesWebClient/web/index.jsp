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
        <title>eCourses</title>
    </head>
    <body>
        <div id="centerContainer">

            <ul class="nav nav-pills">
                <li class="active"><a href="index.jsp">Courses</a></li>
                <li><a href="learning-outcomes.jsp">Learning Outcomes</a></li>
                    <%
                        String serverPath = "http://localhost:8080/eCourses/webresources";
                        String clientPath = "http://localhost:8080/eCoursesWebClient/";
                        String indexPath = clientPath + "index.jsp";

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
                if (session.getAttribute("user") != null
                        && session.getAttribute("user").equals("administrator")) {
                    response.sendRedirect("admin.jsp");
                } else if (session.getAttribute("user") != null
                        && session.getAttribute("user").equals("professors")) {
            %>
            <br><p>${error}</p>
            <form action="UploadCsv" method="post" enctype="multipart/form-data">
                <table style="width:100%">
                    <tr>
                        <th>Insert courses from .csv file</th>
                        <td><input type="file" name="file"></td>
                        <td><input type="submit" value="Upload"></td>
                    </tr>
                </table>
            </form>                  
            <%
                JAXBContext jaxbContext = JAXBContext.newInstance(SimpleCourses.class);
                String path = "courses/professor/" + session.getAttribute("userid");
                SimpleCourses myCourses = new SimpleCourses();
                myCourses = (SimpleCourses) XMLHandler.getUnmarshall(path, jaxbContext);
            %>
            <br><table style="width:100%">
                <thead>
                    <tr>
                        <th colspan="3">My Courses</th>
                        <th><form action="new-course.jsp">
                                <input type="submit" value="New Course"><br>
                            </form></th>
                    </tr>
                </thead>
                <tr>
                    <th>English Title </th>
                    <th>Greek Title</th> 
                    <th></th>
                    <th></th>
                </tr>
                <%
                    String deletePath = serverPath + "/courses/delete";
                    for (SimpleCourse sc : myCourses.getCourses()) {
                %>
                <tr>
                    <td><%=sc.getEnglishTitle()%></td>
                    <td><%=sc.getGreekTitle()%></td>
                    <td><a href="course-details.jsp?title=<%=sc.getEnglishTitle()%>">
                            See Details</a></td>
                    <td><form action=<%=deletePath%> method="post">
                            <input type="hidden" value=<%=sc.getCourseId()%> name="id">
                            <input type="hidden" value=<%=indexPath%> name="page">
                            <input type="submit" value="Delete"><br>
                        </form></td>
                </tr>
                <%
                    }
                %>
            </table>
            <%
            } else {
            %>
            <br><form action="search-results.jsp" method="get">
                <table style="width:100%">
                    <tr>
                        <th>Find: </th>
                        <td><input type="radio" name="criterion" value="title" 
                                   checked> By Course Title</td>                  
                        <td><input type="radio" name="criterion" value="professor" 
                                   > By Professor</td>
                        <td><input style="width:100%"  type="text" name="find"></td>
                        <td><input type="submit" value="Search"></td>
                    <tr>
                </table>
            </form><br>
            <%
                SimpleCourses favCourses = new SimpleCourses();

                if (session.getAttribute("user") != null
                        && session.getAttribute("user").equals("students")) {

                    JAXBContext jaxbContext = JAXBContext.newInstance(SimpleCourses.class);
                    String path = "favorites/courses/" + session.getAttribute("userid");
                    favCourses = (SimpleCourses) XMLHandler.getUnmarshall(path, jaxbContext);

                    if (favCourses.getCourses().isEmpty() == false) {
            %>
            <table style="width:100%">
                <thead>
                    <tr>
                        <th colspan="3">Favorite Courses</th>
                    </tr>
                </thead>
                <tr>
                    <th>English Title</th>
                    <th>Greek Title</th> 
                    <th></th>
                </tr>
                <%
                    String removePath = serverPath + "/favorites/courses/remove";
                    for (SimpleCourse sc : favCourses.getCourses()) {
                %>
                <tr>
                    <td><%=sc.getEnglishTitle()%></td>
                    <td><%=sc.getGreekTitle()%></td>
                    <td><a href="course-details.jsp?title=<%=sc.getEnglishTitle()%>">
                            See Details</a></td>
                    <td><form action=<%=removePath%> method="post">
                            <input type="hidden" value=<%=session.getAttribute("userid")%> name="userid">
                            <input type="hidden" value=<%=sc.getCourseId()%> name="courseid">
                            <input type="hidden" value=<%=indexPath%> name="page">  
                            <input type="submit" value="Remove"><br>
                        </form></td>
                </tr>
                <%
                    }
                %>
            </table><br><br>
            <%
                    }
                }
                JAXBContext jaxbContext = JAXBContext.newInstance(SimpleCourses.class);
                String path = "courses/all";
                SimpleCourses allCourses = new SimpleCourses();
                allCourses = (SimpleCourses) XMLHandler.getUnmarshall(path, jaxbContext);
            %>
            <table style="width:100%">
                <thead>
                    <tr>
                        <th colspan="2">All Available Online Courses</th>
                        <th><form action="detailed-courses.jsp">
                                <input type="submit" value="Detailed Courses"><br>
                            </form></th>
                    </tr>
                </thead>
                <tr>
                    <th>English Title</th>
                    <th>Greek Title</th> 
                    <th></th>
                </tr>
                <%
                    for (SimpleCourse sc : allCourses.getCourses()) {
                        if (favCourses.getCourses().contains(sc) == false) {
                %>
                <tr>
                    <td><%=sc.getEnglishTitle()%></td>
                    <td><%=sc.getGreekTitle()%></td>
                    <td><a href="course-details.jsp?title=<%=sc.getEnglishTitle()%>">
                            See Details</a></td>
                            <%
                                if (session.getAttribute("user") != null
                                        && session.getAttribute("user").equals("students")) {
                                    String addPath = serverPath
                                            + "/favorites/courses/add";
                            %>
                    <td><form action=<%=addPath%> method="post">
                            <input type="hidden" value=<%=session.getAttribute("userid")%> name="userid">
                            <input type="hidden" value=<%=sc.getCourseId()%> name="courseid">
                            <input type="hidden" value=<%=indexPath%> name="page">                            
                            <input type="submit" value="Favorite"><br>
                        </form></td>
                        <%
                            }
                        %>
                </tr>
                <%
                        }
                    }
                %>
            </table>
            <%
                }
            %>
        </div>
    </body>
</html>
