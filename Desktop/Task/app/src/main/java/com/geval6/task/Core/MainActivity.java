package com.geval6.task.Core;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geval6.task.Adapter.GridViewAdapter;
import com.geval6.task.Model.ModelClass;
import com.geval6.task.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<ModelClass> mGridData;
    private LinearLayout progressCircle;
    private String url="https://api.flickr.com/services/rest/?method=flickr.people.getPhotos&api_key=c7c7e36e3b626c1ff8c29eb9dfadc60b&user_id=144366085%40N08&format=json&nojsoncallback=1&api_sig=90f7e736b9f41cc20d95e63ebdf39d18";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);
        progressCircle = (LinearLayout) findViewById(R.id.progressCircle);

        mGridData = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        gridView.setAdapter(gridViewAdapter);
        new AsyncHttpTask().execute(url);

    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressCircle.setVisibility(View.VISIBLE);

        }

        protected Integer doInBackground(String... params) {
            Integer result = 0;
            InputStream inputStream;
            Exception e;
            try {

                HttpURLConnection urlConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream2 = new BufferedInputStream(urlConnection.getInputStream());
                    result = 1;
                    try {
                        parseResult(convertInputStreamToString(inputStream2));

                    } catch (Exception e2) {
                        e = e2;
                        inputStream = inputStream2;
                        Log.d("Exception", e.getLocalizedMessage());
                        return null;
                    }
                }
                else{
                    result = 0;
                }
            } catch (Exception e3) {
                e = e3;
                Log.d("Exception", e.getLocalizedMessage());
                return null;
            }
            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result ="";
            while ((line = bufferedReader.readLine()) != null) {
                result = result + line;
            }

            if (inputStream != null) {
                inputStream.close();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                gridViewAdapter.setGridData(mGridData);
            }
            else {
                Toast.makeText(MainActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            progressCircle.setVisibility(View.INVISIBLE);
            }
        }

    private void parseResult(String result) {

        try {
            JSONObject response = new JSONObject(result);
            JSONObject response1 =response.getJSONObject("photos");
            JSONArray posts = response1.optJSONArray("photo");

            ModelClass item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                String id= post.optString("id");
                String farm= post.optString("farm");
                String secret= post.optString("secret");
                String server= post.optString("server");

                item = new ModelClass();
                item.setTitle(title);

              String image_url="http://farm" +farm+".staticflickr.com//"+server+ "//" +id+  "_" +secret+".jpg";
              item.setImage(image_url);

                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }
}
