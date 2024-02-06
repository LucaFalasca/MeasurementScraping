package it.lucafalasca.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class JsonReader {

    private static final Logger logger = LogManager.getLogger(JsonReader.class);

    private JsonReader() {
    }
    public static JSONObject readJsonFromUrl(String url, boolean auth) throws IOException, JSONException {
        URI uri = Paths.get( url).toUri() ;
        URLConnection uc = uri.toURL().openConnection();
        if(auth) {
            String[] cred = getAuthFromJsonFile();
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode((cred[0] + ":" + cred[1]).getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
        }

        try (InputStream is = uc.getInputStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (Exception e) {
            logger.info(String.format("Errore nella lettura del file json da url: %s %s", url, e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray readJsonArrayFromUrl(String url, boolean auth) throws IOException, JSONException {
        URI uri = Paths.get( url).toUri() ;
        URLConnection uc = uri.toURL().openConnection();
        if(auth) {
            String[] cred = getAuthFromJsonFile();
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode((cred[0] + ":" + cred[1]).getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
        }

        try (InputStream is = uc.getInputStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        }
    }

    public static JSONArray readJsonArrayFromFile(String path) throws IOException, JSONException {
        return new JSONArray(readStringFromFile(path));
    }

    public static JSONObject readJsonObjectFromFile(String path) throws IOException, JSONException {
        return new JSONObject(readStringFromFile(path));
    }

    private static String readStringFromFile(String path) throws IOException, JSONException {
        StringBuilder string = new StringBuilder();
        File file = new File(path);
        Scanner scanner = new Scanner(file);


        while (scanner.hasNextLine()) {
            String riga = scanner.nextLine();
            string.append(riga);
        }

        scanner.close();
        return string.toString();
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /*
     * Get username and token from json file
     * @return an array of string with username and token
     */
    private static String[] getAuthFromJsonFile(String path) throws IOException {
        JSONObject jsonObject = readJsonObjectFromFile(path);
        String username = jsonObject.getString("username");
        String token = jsonObject.getString("token");
        return new String[]{username, token};
    }


    private static String[] getAuthFromJsonFile() throws IOException {
        String path = "src/main/resources/token_auth_github.json";
        return getAuthFromJsonFile(path);
    }
}
