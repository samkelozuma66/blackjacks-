package com.example.blackjacks.data;

import android.os.StrictMode;

import com.example.blackjacks.data.model.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser = null;
            System.out.println("Before getUserdata");
            String userdat = getUserdata(username,password);
            System.out.println("after getUserdata");
            if(userdat.compareTo("[]") >= 0)
            {
                JSONArray jobj = new JSONArray(userdat);
                JSONObject userd = new JSONObject(jobj.getString(0));
                System.out.println(userd.getString("id"));
                fakeUser = new LoggedInUser(userd.getString("id"),
                        userd.getString("name"),
                        userd.getString("email"),
                        userd.getString("gender"),
                        userd.getString("mobile"),
                        userd.getString("designation"),
                        userd.getString("image"),
                        userd.getString("status"),
                        userd.getString("user_type"));


                return new Result.Success<>(fakeUser);

            }
            else
                return new Result.Error(new IOException("Login  failed "));

            //String userdat = getUserdata(username,password);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public String getUserdata(String email,String password)
    {
        String ad ="http://192.168.1.41/userm/app_login.php?"
                +"username="+email
                +"&password="+password;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream)url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }

            adJson  = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException  e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return  adJson;
    }
}