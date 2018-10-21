<%@ page import="java.net.URL, java.util.*" %>
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
        <title>Sign Up</title>
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

            <br><p>${error}</p>
            <%
                if (request.getParameter("form").equals("professor")) {
            %>
            <form action="Registration" method="post">
                <input type="hidden" value="professor" name="account" />
                <table style="width:100%">
                    <thead>
                        <tr>
                            <th colspan="3">Create a new professor's account</th>
                        </tr>
                    </thead>
                    <tr>
                        <th>First Name:</th>
                        <td><input type="text" name="firstname"> </td> 
                        <td>Between 2-45 characters long.</td>
                    </tr>
                    <tr>
                        <th>Last Name:</th>
                        <td><input type="text" name="lastname"> </td> 
                        <td>Between 2-45 characters long.</td>
                    </tr>
                    <tr>
                        <th>Educational Level:</th>
                        <td><select name = "edulevel">
                                <option value="Professor">Professor</option>
                                <option value="Associate Professor">Associate Professor</option>  
                                <option value="Assistant Professor">Assistant Professor</option> 
                                <option value="Lecturer">Lecturer</option> 
                            </select></td> 
                    </tr>
                    <tr>
                        <th>Username:</th>
                        <td><input type="text" name="username"> </td> 
                        <td>Between 4-12 characters long.</td>
                    </tr>
                    <tr>
                        <th>Password: </th>
                        <td><input type="password" name="password"></td> 
                        <td>Between 6-12 characters long.</td>
                    </tr>
                    <tr>
                        <th>Create account:</th>
                        <td><input type="submit" value="Submit"></td>
                    </tr>
                </table>
            </form>
            <%
            } else {
            %>           
            <form action="Registration" method="post">
                <input type="hidden" value="student" name="account" />
                <table style="width:100%">
                    <thead>
                        <tr>
                            <th colspan="3">Create a new student's account</th>
                        </tr>
                    </thead>
                    <tr>
                        <th>Username:</th>
                        <td><input type="text" name="username"> </td> 
                        <td>Between 4-12 characters long.</td>
                    </tr>
                    <tr>
                        <th>Password: </th>
                        <td><input type="password" name="password"></td> 
                        <td>Between 6-12 characters long.</td>
                    </tr>
                    <tr>
                        <th>Create account:</th>
                        <td><input type="submit" value="Submit"></td>
                    </tr>
                </table>
            </form>
            <%
                }
            %>
        </div>
    </body>
</html>
