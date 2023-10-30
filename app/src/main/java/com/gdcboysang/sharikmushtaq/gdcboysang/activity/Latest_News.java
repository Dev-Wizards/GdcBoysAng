package com.gdcboysang.sharikmushtaq.gdcboysang.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


public class Latest_News extends Fragment {

    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    JSONArray contacts;
    public String FILENAME="";

    private static String url = "http://www.gdcboysang.ac.in/about/Droid/Latest_News.php";
    private static String fileUrl = "http://www.gdcboysang.ac.in/about/Droid/uploads/";
    ArrayList<HashMap<String, String>> LatestNewsList;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LatestNewsList = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_latest__news, container, false);
        lv = rootView.findViewById(R.id.listLatestNews);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView

                    if(isConnectingToInternet()) {
                        HashMap<String, String> contact = LatestNewsList.get(position);
                        FILENAME = contact.get("filename");
                        String Message=contact.get("message");
                        String headline=contact.get("headline");
                        String URL = fileUrl+FILENAME;

                        ShowLatestNews sln = new ShowLatestNews ();
                        Bundle args = new Bundle();
                        args.putString("url", URL);
                        args.putString("headline", headline);
                        args.putString("message", Message);
                        sln.setArguments(args);

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, sln);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }

                    else
                        Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
                }
        });

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Latest News");

        if (isConnectingToInternet())
            new GetLatestNews().execute();
        else
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
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

    private class GetLatestNews extends AsyncTask<Void, Void, Void> {

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
                        String headline = c.getString("headline");
                        String filename = c.getString("filename");
                        String date = c.getString("date");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("title", title);
                        contact.put("headline", headline);
                        contact.put("message", message);
                        contact.put("filename", filename);
                        contact.put("date", date);

                        // adding contact to contact list
                        LatestNewsList.add(contact);
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
            if (contacts.length() > 0) {
                ListAdapter adapter = new SimpleAdapter(
                        getActivity(), LatestNewsList,
                        R.layout.list_latestnews, new String[]{"date", "headline",
                        "title"}, new int[]{R.id.dateLatestNews,
                        R.id.latestnews1});
                lv.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "No News Yet", Toast.LENGTH_SHORT).show();
            }
        }
    }
}