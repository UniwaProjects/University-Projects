<%@page import="com.classes.SimpleCourse"%>
<%@page import="com.classes.SimpleCourses"%>
<%@page import="com.classes.LearningOutcome"%>
<%@page import="com.classes.XMLHandler"%>
<%@page import="com.classes.LearningOutcomes"%>
<%@page import="javax.xml.bind.JAXBContext"%>
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
        <title>New Course</title>
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
                if (session.getAttribute("user") != null
                        && session.getAttribute("user").equals("professors")) {
            %>
            
            <br><p>${error}</p>
            <form action="NewCourse" method="post">
                <table style="width:100%">
                    <thead>
                        <tr>
                            <th colspan="3">Create a new course.</th>
                        </tr>
                    </thead>
                    <tr>
                        <th>English Title:</th>
                        <td><input type="text" name="englishtitle"> </td> 
                        <td>Between 2-45 characters long.</td>
                    </tr>
                    <tr>
                        <th>Greek Title:</th>
                        <td><input type="text" name="greektitle"> </td> 
                        <td>Between 2-45 characters long.</td>
                    </tr>
                    <tr>
                        <th>Educational Level:</th>
                        <td><select name = "edulevel">
                                <option value="Undergraduate">Undergraduate</option>
                                <option value="Postgraduate">Postgraduate</option>
                            </select></td>
                    </tr>
                    <tr>
                        <th>Semester:</th>
                        <td><select name = "semester">
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                                <option value="6">6</option>
                                <option value="7">7</option>
                                <option value="8">8</option>
                            </select></td>
                    </tr>
                    <tr>
                        <th>Learning Outcomes</th>
                            <%
                                JAXBContext jaxbContext = JAXBContext.newInstance(LearningOutcomes.class);
                                String path = "outcomes/all";
                                LearningOutcomes los = new LearningOutcomes();
                                los = (LearningOutcomes) XMLHandler.getUnmarshall(path, jaxbContext);
                            %>
                        <td><form>
                                <%
                                    for (LearningOutcome lo : los.getOutcomes()) {
                                %>
                                <input type="checkbox" name="outcome" value=<%=lo.getId()%>><%=lo.getId()%><br>                        
                                <%
                                    }
                                %>
                            </form></td>
                    </tr>
                    <tr>
                        <th>Required Courses</th>
                            <%
                                jaxbContext = JAXBContext.newInstance(SimpleCourses.class);
                                path = "courses/all";
                                SimpleCourses scs = new SimpleCourses();
                                scs = (SimpleCourses) XMLHandler.getUnmarshall(path, jaxbContext);
                            %>
                        <td>
                            <%
                                for (SimpleCourse sc : scs.getCourses()) {
                            %>
                            <input type="checkbox" name="reqcourse" value=<%=sc.getCourseId()%>><%=sc.getEnglishTitle()%><br>                        
                            <%
                                }
                            %>
                        </td>
                    </tr>
                    <tr>
                        <th>Required Learning Outcomes</th>
                        <td>
                            <%
                                for (LearningOutcome lo : los.getOutcomes()) {
                            %>
                            <input type="checkbox" name="reqoutcome" value=<%=lo.getId()%>><%=lo.getId()%><br>                        
                            <%
                                }
                            %>
                            </form></td>
                    </tr>
                    <tr>
                        <th>Create course:</th>
                        <td><input type="submit" value="Submit"></td>
                    </tr>
                </table>
            </form>
            <%
                } else {
                    response.sendRedirect("index.jsp");
                }
            %>
        </div>
    </body>
</html>
