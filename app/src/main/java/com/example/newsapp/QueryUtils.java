package com.example.newsapp;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class QueryUtils {
    static final String LOG_TAG = QueryUtils.class.getSimpleName();
    public static List<NewsStory> fetchNewsStoriesData(String apiUrl)
    {
        URL url = null;
        try {
            url = new URL(apiUrl);
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG,"Invalid URL", e);
        }

        String jsonResponse = null;
        try
        {
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG,"Error Closing Input Stream", e);
        }
        return extractNewsStories(jsonResponse);
    }

    private static List<NewsStory> extractNewsStories(String jsonResponse) {
        List<NewsStory> newsStories = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 0 ; i < results.length() ; i++)
            {
                JSONObject current = results.optJSONObject(i);
                String sectionId = current.optString("sectionId");
                String sectionName = current.optString("sectionName");
                String title = current.optString("webTitle");
                String url = current.optString("webUrl");
                String dateAndTime = current.optString("webPublicationDate");
                String[] s = dateAndTime.split("[TZ]");
                String date = s[0] + "\nAt " + s[1];
                JSONArray tags = current.optJSONArray("tags");
                String authorName = "";
                if(tags.length()!=0)
                {
                    JSONObject tag = tags.optJSONObject(0);
                    authorName = tag.optString("webTitle");
                }
                newsStories.add(new NewsStory(sectionId,sectionName,title,url,date,authorName));
            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Error Parsing JSON",e);
        }
        return newsStories;
    }

    public static List<String> fetchSections(String apiUrl)
    {
        URL url = null;
        try {
            url = new URL(apiUrl);
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG,"Invalid URL", e);
        }

        String jsonResponse = null;
        try
        {
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG,"Error Closing Input Stream", e);
        }
        return extractSections(jsonResponse);
    }
    private static List<String> extractSections(String jsonResponse)
    {
        List<String> sections = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.optJSONObject("response");
            JSONArray results = response.optJSONArray("results");
            for(int i = 0 ; i < results.length() ; i++)
            {
                JSONObject res = results.getJSONObject(i);
                sections.add(res.getString("id"));
            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Error Parsing JSON",e);
        }
        return  sections;
    }
    private static String makeHttpRequest(URL url)throws IOException {
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
                Log.e(LOG_TAG,"Error Response Code:" + urlConnection.getResponseCode());
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG,"Error Retrieving JSON" , e);
        }
        finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
            if(inputStream!=null)
                inputStream.close();
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while(line!=null)
            {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }
}
