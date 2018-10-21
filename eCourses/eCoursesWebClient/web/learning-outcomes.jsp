<%@page import="com.classes.LearningOutcome"%>
<%@page import="com.classes.XMLHandler"%>
<%@page import="javax.xml.bind.JAXBContext"%>
<%@page import="com.classes.LearningOutcomes"%>
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
        <title>Learning Outcomes</title>
    </head>
    <body>
        <div id="centerContainer">

            <ul class="nav nav-pills">
                <li><a href="index.jsp">Courses</a></li>
                <li  class="active"><a href="learning-outcomes.jsp">Learning Outcomes</a></li>
                    <%
                        String serverPath = "http://localhost:8080/eCourses/webresources";
                        String clientPath = "http://localhost:8080/eCoursesWebClient/";
                        String outcomesPath = clientPath + "learning-outcomes.jsp";

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
                LearningOutcomes favOutcomes = new LearningOutcomes();

                if (session.getAttribute("user") != null
                        && session.getAttribute("user").equals("students")) {

                    JAXBContext jaxbContext = JAXBContext.newInstance(LearningOutcomes.class);
                    String path = "favorites/outcomes/" + session.getAttribute("userid");
                    favOutcomes = (LearningOutcomes) XMLHandler.getUnmarshall(path, jaxbContext);

                    if (favOutcomes.getOutcomes().isEmpty() == false) {
            %>
            <br><table style="width:100%">
                <thead>
                    <tr>
                        <th colspan="7">Favorite Learning Outcomes</th>
                    </tr>
                </thead>
                <tr>
                    <th>Code</th>                     
                    <th>Field</th>
                    <th>Category</th>
                    <th>Number</th>
                    <th>Description</th> 
                    <th>Mastery Level</th> 
                    <th></th>
                </tr>
                <%
                    for (LearningOutcome lo : favOutcomes.getOutcomes()) {
                %>
                <tr>
                    <td><%=lo.getId()%></td>
                    <td><%=lo.getField()%></td>
                    <td><%=lo.getCategory()%></td>
                    <td><%=lo.getNumber()%></td>
                    <td><%=lo.getDescription()%></td>
                    <td><%=lo.getMasteryLevel()%></td>
                    <td><a  href="search-results.jsp?find=<%=lo.getId()%>&criterion=outcome">See Courses</a></td>
                    <%
                        if (session.getAttribute("user") != null
                                && session.getAttribute("user").equals("students")) {

                            String removePath = serverPath
                                    + "/favorites/outcomes/remove";
                    %>
                    <td><form action=<%=removePath%> method="post">
                            <input type="hidden" value=<%=session.getAttribute("userid")%> name="userid">
                            <input type="hidden" value=<%=lo.getId()%> name="outcomeid">
                            <input type="hidden" value=<%=outcomesPath%> name="page">
                            <input type="submit" value="Remove"><br>
                        </form></td>
                        <%
                            }
                        %>
                </tr>
                <%
                    }
                %>
            </table><br>
            <%
                    }
                }

                JAXBContext jaxbContext = JAXBContext.newInstance(LearningOutcomes.class);
                String path = "outcomes/all";
                LearningOutcomes allOutcomes = new LearningOutcomes();
                allOutcomes = (LearningOutcomes) XMLHandler.getUnmarshall(path, jaxbContext);
            %>
            <br><table style="width:100%">
                <thead>
                    <tr>
                        <th colspan="7">All Available Learning Outcomes</th>
                    </tr>
                </thead>
                <tr>
                    <th>Code</th>                     
                    <th>Field</th>
                    <th>Category</th>
                    <th>Number</th>
                    <th>Description</th> 
                    <th>Mastery Level</th> 
                    <th></th>
                </tr>
                <%
                    for (LearningOutcome lo : allOutcomes.getOutcomes()) {
                        if (favOutcomes.getOutcomes().contains(lo) == false) {
                %>
                <tr>
                    <td><%=lo.getId()%></td>                    
                    <td><%=lo.getField()%></td>
                    <td><%=lo.getCategory()%></td>
                    <td><%=lo.getNumber()%></td>
                    <td><%=lo.getDescription()%></td>
                    <td><%=lo.getMasteryLevel()%></td>

                    <td><a  href="search-results.jsp?find=<%=lo.getId()%>&criterion=outcome">See Courses</a></td>
                    <%
                        if (session.getAttribute("user") != null
                                && session.getAttribute("user").equals("students")) {

                            String addPath = serverPath
                                    + "/favorites/outcomes/add";
                    %>
                    <td><form action=<%=addPath%> method="post">
                            <input type="hidden" value=<%=session.getAttribute("userid")%> name="userid">
                            <input type="hidden" value=<%=lo.getId()%> name="outcomeid">
                            <input type="hidden" value=<%=outcomesPath%> name="page">
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
        </div>
    </body>
</html>