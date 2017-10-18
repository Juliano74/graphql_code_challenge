package com.graphql.code_challenge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class JSONHandler {

    public static JSONObject loadJSON(String fileName) {
        String json = null;
        try {
            InputStream is = new FileInputStream(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            try {
                is.close();
            }
            catch(IOException e) {
                System.out.println("Unable to close stream.");
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }
        catch (IOException e) {
            System.out.println("IO Exception");
            return null;
        }

        try {
            return new JSONObject(json);
        }
        catch(JSONException e) {
            System.out.println("Unable to return json object");
            return null;
        }
    }
}
