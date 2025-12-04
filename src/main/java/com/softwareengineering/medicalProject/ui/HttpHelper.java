package com.softwareengineering.medicalProject.ui;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.JOptionPane;

public class HttpHelper {

    private final Component parentComponent;

    public HttpHelper(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public String httpGET(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                String errorResponse = readStream(con.getErrorStream());
                JOptionPane.showMessageDialog(parentComponent, "GET error (Status " + status + "): " + errorResponse);
                return null;
            }

            String response = readStream(con.getInputStream());
            con.disconnect();
            return response;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, "Network/GET error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String httpPOST(String urlStr) {
        String result = sendRequest("POST", urlStr);
        return result;
    }

    public String httpPUT(String urlStr) {
        String result = sendRequest("PUT", urlStr);
        return result;
    }

    public String httpDELETE(String urlStr) {
        String result = sendRequest("DELETE", urlStr);
        return result;
    }

    // URL-encodes a string param to safely transmit it in an HTTP query
    public String encodeParam(String param) {
        if (param == null)
            return "";
        try {
            String encoded = URLEncoder.encode(param, "UTF-8");
            return encoded;
        } catch (UnsupportedEncodingException e) {
            return param.replace(" ", "%20");
        }
    }

    // Generic method to send non-GET requests (POST, PUT, DELETE)
    private String sendRequest(String method, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.connect();

            int status = con.getResponseCode();
            if (status >= 200 && status < 300) {
                try {
                    readStream(con.getInputStream());
                    return "OK";
                } catch (Exception e) {
                    return "OK";
                }
            } else {
                String errorResponse = readStream(con.getErrorStream());
                JOptionPane.showMessageDialog(parentComponent, method + " error (Status " + status + "): " + errorResponse);
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, "Network/" + method + " error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String readStream(java.io.InputStream inputStream) throws java.io.IOException {
        if (inputStream == null) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}