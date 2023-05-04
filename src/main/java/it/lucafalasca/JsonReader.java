package it.lucafalasca;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class JsonReader {
    public static JSONObject readJsonFromUrl(String url, boolean auth) throws IOException, JSONException {
        URL urlOb = new URL(url);
        URLConnection uc = urlOb.openConnection();
        if(auth) {
            String[] cred = getAuthFromJsonFile();
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode((cred[0] + ":" + cred[1]).getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
        }
        InputStream is = uc.getInputStream();

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }catch (Exception e){
            System.out.println("Error in json reader");
            e.printStackTrace();
            return null;
        } finally {
            is.close();
        }
    }

    public static JSONArray readJsonArrayFromUrl(String url, boolean auth) throws IOException, JSONException {
        URL urlOb = new URL(url);

        URLConnection uc = urlOb.openConnection();
        if(auth) {
            String[] cred = getAuthFromJsonFile();
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode((cred[0] + ":" + cred[1]).getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
        }
        InputStream is = uc.getInputStream();

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static JSONArray readJsonArrayFromFile(String path) throws IOException, JSONException {
        String jsonString = "";
        File file = new File(path);
        Scanner scanner = new Scanner(file);


        while (scanner.hasNextLine()) {
            String riga = scanner.nextLine();
            jsonString += riga + "\n";
        }

        scanner.close();
        JSONArray json = new JSONArray(jsonString);
        return json;
    }

    public static JSONObject readJsonObjectFromFile(String path) throws IOException, JSONException {
        String jsonString = "";
        File file = new File(path);
        Scanner scanner = new Scanner(file);


        while (scanner.hasNextLine()) {
            String riga = scanner.nextLine();
            jsonString += riga + "\n";
        }

        scanner.close();
        JSONObject json = new JSONObject(jsonString);
        return json;
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
        String path = "src\\main\\resources\\token_auth_github.json";
        return getAuthFromJsonFile(path);
    }
}
