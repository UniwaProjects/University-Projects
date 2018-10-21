package com.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.security.auth.login.FailedLoginException;

public class CommunicationHandler {

    public CommunicationHandler() {
    }

    public String getXml(String path) {

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("http://localhost:8080/eCourses/webresources/"
                    + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("FAILED : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            System.out.println("*****SERVER     OUTPUT*****");
            System.out.println(sb.toString());

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void sendXml(String path, String xml) {

        try {
            URL url = new URL("http://localhost:8080/eCourses/webresources/"
                    + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/xml");

            OutputStream os = conn.getOutputStream();
            os.write(xml.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED
                    && conn.getResponseCode() != 200) {
                throw new RuntimeException("FAILED : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("*****SERVER     OUTPUT*****");
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String authenticate(String xml) throws FailedLoginException {

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("http://localhost:8080/eCourses/webresources/"
                    + "users/authentication");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/xml");

            OutputStream os = conn.getOutputStream();
            os.write(xml.getBytes());
            os.flush();

            if (conn.getResponseCode() == 401) {
                throw new FailedLoginException("FAILED: Incorrect credentials.");
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED
                    && conn.getResponseCode() != 200) {
                throw new RuntimeException("FAILED : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            System.out.println("*****SERVER     OUTPUT*****");
            System.out.println(sb.toString());

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void sendPost(String path, String urlParameters) throws Exception {

        byte[] postData = urlParameters.getBytes();
        int postDataLength = postData.length;

        try {
            String request = "http://localhost:8080/eCourses/webresources/" + path;
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(postData);
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("*****SERVER     OUTPUT*****");
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
