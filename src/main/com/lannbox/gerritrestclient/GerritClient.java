package com.lannbox.gerritrestclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lannbox.gerritrestclient.models.AccountCapabilities;
import com.lannbox.gerritrestclient.models.Change;
import com.lannbox.gerritrestclient.models.Project;
import com.lannbox.gerritrestclient.models.Revision;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class GerritClient {
    public static double[] SUPPORTED_VERSIONS = {2.5};
    public static double LATEST_SUPPORTED_VERSION = SUPPORTED_VERSIONS[SUPPORTED_VERSIONS.length - 1];

    private URL base_url;
    private Gson gson;

    public GerritClient(URL base_url) {
        this(base_url, LATEST_SUPPORTED_VERSION);
    }

    public GerritClient(URL base_url, double version) {
        String proto = base_url.getProtocol();
        if (!proto.equals("http") && !proto.equals("https")) {
            throw new IllegalArgumentException("base_url must be http or https");
        }
        this.base_url = base_url;
        this.gson = buildGson(version);
    }

    public AccountCapabilities getAccountCapabilities(String params) throws IOException, ServerError {
        return get("accounts/self/capabilities", params, AccountCapabilities.class);
    }

    public Map<String, Project> getProjects(String prefix, String params) throws IOException, ServerError {
        String path = "projects/";
        if (prefix != null) { path += prefix; }
        return get(path, params, new TypeToken<Map<String, Project>>(){}.getType());
    }

    public Change.ResultList queryChanges(String params) throws IOException, ServerError {
        return get("changes/", params, Change.ResultList.class);
    }

    private<T> T get(String path, String params, Type type) throws IOException, ServerError {
        if (params != null) { path += "?" + params; }

        HttpURLConnection conn = openConnection(path);
        conn.setRequestProperty("Accept", "application/json");

        if(conn.getResponseCode() == 200) {
            Reader json = new InputStreamReader(conn.getInputStream());
            try {
                return gson.fromJson(json, type);
            } finally {
                json.close();
            }
        } else {
            throw new ServerError(conn.getResponseMessage());
        }
    }

    private HttpURLConnection openConnection(String spec) throws IOException {
        try {
            return (HttpURLConnection) new URL(base_url, spec).openConnection();
        } catch (MalformedURLException e) {
            return null; // This shouldn't happen
        }
    }

    public static class ServerError extends Exception {
        public ServerError(String message) {
            super(message);
        }
    }

    public static Gson buildGson(double version) {
        GsonBuilder builder = new GsonBuilder()
                .setVersion(version)
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        AccountCapabilities.registerGsonAdapter(builder, version);
        Revision.registerGsonAdapter(builder, version);
        Project.registerGsonAdapter(builder, version);
        return builder.create();
    }

    public static void main(String[] args) {
        try {
            Change.ResultList changes = new GerritClient(new URL("https://www.studentrobotics.org/gerrit/")).queryChanges("n=1&o=LABELS&o=CURRENT_REVISION&o=ALL_FILES&o=CURRENT_COMMIT");
            System.out.println("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
