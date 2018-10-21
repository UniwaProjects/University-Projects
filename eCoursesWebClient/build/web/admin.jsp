<%@page import="com.classes.Professor"%>
<%@page import="com.classes.XMLHandler"%>
<%@page import="javax.xml.bind.JAXBContext"%>
<%@page import="com.classes.Professors"%>
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
        <title>Management</title>
    </head>
    <body>
        <div id="centerContainer">

            <ul class="nav nav-pills">
                <li class="active"><a href="admin.jsp">Management</a></li>
                <li><a href="Logout">Log Out</a></li>
            </ul>

            <%
                String serverPath = "http://localhost:8080/eCourses/webresources";
                String clientPath = "http://localhost:8080/eCoursesWebClient/";
                String adminPath = clientPath + "admin.jsp";

                if (session.getAttribute("user") != null
                        && session.getAttribute("user").equals("administrator")) {

                    JAXBContext jaxbContext = JAXBContext.newInstance(Professors.class);
                    String path = "users/professors";
                    Professors profs = new Professors();
                    profs = (Professors) XMLHandler.getUnmarshall(path, jaxbContext);

                    if (profs.getProfs().isEmpty() == false) {
            %>
            <br><table style="width:100%">
                <thead>
                    <tr>
                        <th colspan="3">Professors</th>
                    </tr>
                </thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th> 
                    <th>Education Level</th>
                    <th>Activated</th>
                    <th></th>
                </tr>
                <%
                    for (Professor p : profs.getProfs()) {
                %>
                <tr>
                    <td><%=p.getFirstname()%></td>
                    <td><%=p.getLastname()%></td>
                    <td><%=p.getEduLevel()%></td>
                    <td><%=p.isActivated()%></td>
                    <%
                        if (p.isActivated()) {
                            String deactivatePath = serverPath
                                    + "/users/professor/deactivate";
                    %>
                    <td><form action=<%=deactivatePath%> method="post">
                            <input type="hidden" value=<%=p.getProfId()%> name="id">
                            <input type="hidden" value=<%=adminPath%> name="page">
                            <input type="submit" value="Deactivate"><br>
                        </form></td>
                        <%
                        } else {
                            String activatePath = serverPath
                                    + "/users/professor/activate";
                        %>
                    <td><form action=<%=activatePath%> method="post">
                            <input type="hidden" value=<%=p.getProfId()%> name="id">
                            <input type="hidden" value=<%=adminPath%> name="page">
                            <input type="submit" value="Activate"><br>
                        </form></td>
                        <%
                            }
                        %>
                </tr>
                <%
                    }
                %>
            </table>
            <%
                    }
                } else {
                    response.sendRedirect("index.jsp");
                }
            %>
        </div>
    </body>
</html>
