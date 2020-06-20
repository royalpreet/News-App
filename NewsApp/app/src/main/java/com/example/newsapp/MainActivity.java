package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static Map<Integer, String> urlMap = new HashMap<Integer, String>();
    ListView listView;
    ArrayList<String> articles = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    URL url;
    HttpURLConnection connection = null;
    InputStream in;
    InputStreamReader reader;

    public class DownloadArticle extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                String articleJson = "";
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                in = connection.getInputStream();
                reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    articleJson += (char) data;
                    data = reader.read();
                }

                JSONObject articleInfo = new JSONObject(articleJson);
                String title;
                String articleUrl;
                try {
                    title = articleInfo.getString("title");
                    articleUrl = articleInfo.getString("url");
                    articles.add(title);
                    urlMap.put(articles.size() - 1, articleUrl);
                    //Log.i("kl", String.valueOf(articles.size() - 1));

                    Log.i("article", title);
                    Log.i("article", articleUrl);
                    //Log.i("article",id);
                }catch(Exception e){
                    Log.i("Failed2","Failed2");
                    e.printStackTrace();
                }
            }catch(Exception e){
                Log.i("Failed3","Failed3");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            update();
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... urls) {

            String idJson = "";
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                in = connection.getInputStream();
                reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    idJson += (char) data;
                    data = reader.read();
                }

                JSONArray idArray = new JSONArray(idJson);
                articles.clear();

                for (int i = 0; i < 20; i++) {
                    String id = idArray.getString(i);
                    DownloadArticle article = new DownloadArticle();
                    article.execute("https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty");
                    //Log.i("url","https://hacker-news.firebaseio.com/v0/item/"+id+".json?print=pretty");
                }
                //update();

            } catch (Exception e) {
                Log.i("Failed", "Failed");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            update();
        }
    }

    public void update(){
        arrayAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, articles);
        listView.setAdapter(arrayAdapter);

        DownloadTask task = new DownloadTask();
        task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), Display.class);
                i.putExtra("id",position);
                startActivity(i);
            }
        });

    }
}
