package com.gdcboysang.sharikmushtaq.gdcboysang.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gdcboysang.sharikmushtaq.gdcboysang.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class News extends Fragment {
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    JSONArray contacts;

    private static String url = "http://www.gdcboysang.ac.in/about/Droid/News.php";

    ArrayList<HashMap<String, String>> NewsList;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NewsList = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        lv = (ListView)rootView. findViewById(R.id.listNews);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Announcements");

        if(isConnectingToInternet())
            new GetNews().execute();
        else
            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class GetNews extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("Tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                     contacts = jsonObj.getJSONArray("data");

                            // looping through All Contacts
                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject c = contacts.getJSONObject(i);

                                String title = c.getString("title");
                                String message = c.getString("message");
                                String date = c.getString("date");

                                // tmp hash map for single contact
                                HashMap<String, String> contact = new HashMap<>();

                                // adding each child node to HashMap key => value
                                contact.put("title", title);
                                contact.put("message", message);
                                contact.put("date", date);

                                // adding contact to contact list
                                NewsList.add(contact);
                            }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());


                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(contacts.length()>0) {
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), NewsList,
                    R.layout.list_news, new String[]{"date", "message",
                    "title"}, new int[]{R.id.dateNews,
                    R.id.news});

            lv.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(getActivity(), "No News Yet", Toast.LENGTH_SHORT).show();
            }
        }
    }
}