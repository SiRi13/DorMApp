package de.hochschuletrier.dbconnectionlib.helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import de.hochschuletrier.dbconnectionlib.constants.Constants;

/**
 * Created by simon on 11/16/14.
 */
public class JSONParser
{
    private final String TAG = Constants.TAG_PREFIX + getClass().getName();

    static InputStream is = null;

    static JSONObject jObj = null;

    static String json = "";

    // constructor
    public JSONParser()
    {
    }

    public JSONObject getJSONFromUrl(String url, List<BasicNameValuePair> params)
    {
        // Making HTTP request
        try
        {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.i("JSON", "url: " + url + "; json: \n" + json);
        }
        catch (Exception e)
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try
        {
            if (json.contains("Wrong")) {
                Log.i("JSON Parser", json);
            }
            else {
                jObj = new JSONObject(json);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

/*        try {
            Log.v(TAG, "Sleep(1000);");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        // return JSON String
        return jObj;
    }
}