package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The view component for the Taiga project import screen, used by the Moderator.
 * This class creates and displays the user interface for entering Taiga credentials
 * (username, password) and the project slug. User actions are forwarded to the
 * TaigaImportController to handle the API login and story fetching process.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

public class TaigaStoryFetcher {
    public static String loginAndGetToken(String username, String password) throws Exception {
        URL url = new URL("https://api.taiga.io/api/v1/auth");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String jsonInput = String.format("{\"type\": \"normal\", \"username\": \"%s\", \"password\": \"%s\"}", username, password);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
            os.flush();
        }
        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()
        ));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        if (responseCode != 200) {
            String errorMessage = json.optString("_error_message", "Unknown error");
            throw new RuntimeException("Login failed: " + errorMessage);
        }
        String authToken = json.getString("auth_token");
        return authToken;
    }

    public static int getProjectId(String token, String projectSlug) throws Exception {
        URL url = new URL("https://api.taiga.io/api/v1/projects/by_slug?slug=" + projectSlug);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        JSONObject json = new JSONObject(response.toString());
        return json.getInt("id");
    }

    public static JSONArray fetchUserStories(String token, int projectId) throws Exception {
        URL url = new URL("https://api.taiga.io/api/v1/userstories?project=" + projectId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        JSONArray allStories = new JSONArray(response.toString());
        JSONArray backlogStories = new JSONArray();

        for (int i = 0; i < allStories.length(); i++) {
            JSONObject story = allStories.getJSONObject(i);
            if (story.isNull("milestone")) {
                backlogStories.put(story);
            }
        }
        return backlogStories;
    }
}