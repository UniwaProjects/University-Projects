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
        <title>Login</title>
    </head>
    <body>
        <div id="centerContainer">
            <div id ="centerAlign">

                <ul class="nav nav-pills" >
                    <li><a href="index.jsp">Courses</a></li>
                    <li><a href="learning-outcomes.jsp">Learning Outcomes</a></li>
                        <%
                            if (session.getAttribute("token") == null
                                    || session.getAttribute("token") == "") {
                        %>
                    <li class="active"><a href="login.jsp">Log In</a></li>
                        <%
                        } else {
                        %>
                    <li><a href="Logout" method="post">Log Out</a></li>
                        <%
                            }
                        %>
                </ul>
                <br><p>${error}</p>
                <div id="Sign In" class="tabcontent">
                    <form action="Authentication" method="post">                
                        <table align="center">
                            <thead>
                                <tr>
                                    <th colspan="3">Connect to eCourses</th>
                                </tr>
                            </thead>
                            <tr>
                                <th>Username:</th>
                                <td><input type="text" name="username"> </td> 
                            </tr>
                            <tr>
                                <th>Password: </th>
                                <td><input type="password" name="password"></td> 
                            </tr>
                            <tr>
                                <th></th>
                                <td><input type="submit" value="Sign In"></td>
                            </tr>
                        </table>
                    </form>

                    <br><form action="signup.jsp" method="get">
                        Create a new 
                        <select name = "form">
                            <option value="student">Student</option>
                            <option value="professor">Professor</option>
                        </select> 
                        account.
                        <input style="width:80px" type="submit" value="SignUp">
                    </form>
                </div>
            </div>
    </body>
</html>

