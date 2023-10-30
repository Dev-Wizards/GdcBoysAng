package com.gdcboysang.sharikmushtaq.gdcboysang.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gdcboysang.sharikmushtaq.gdcboysang.BuildConfig;
import com.gdcboysang.sharikmushtaq.gdcboysang.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;


public class Downloads extends Fragment {

    private ProgressDialog pDialog,pDialog2;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    public String FILENAME="";
    JSONArray contacts;

    // URL to get contacts JSON
    private static String url = "http://www.gdcboysang.ac.in/about/Droid/Downloads.php";
    private static String fileUrl = "http://www.gdcboysang.ac.in/about/Droid/uploads/";

    ArrayList<HashMap<String, String>> DownloadList;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DownloadList = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_downloads, container, false);
        lv = rootView. findViewById(R.id.listDownloads);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView

                boolean b=isStoragePermissionGranted();

                if(b) {
                    if(isConnectingToInternet()) {
                        HashMap<String, String> contact = DownloadList.get(position);
                        FILENAME = contact.get("filename");
                        String URL = fileUrl+FILENAME;
                        new Downloader().execute(URL);
                    }
                    else
                        Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getActivity(),"No Storage Permission\nEnable Storage Permission in Settings",Toast.LENGTH_LONG).show();

            }
        });


        return rootView;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
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


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Downloads");

        if(isConnectingToInternet())
            new GetDownloads().execute();
        else
            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
    }

    private class GetDownloads extends AsyncTask<Void, Void, Void> {

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
                        String filename = c.getString("filename");
                        String date = c.getString("date");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("title", title);
                        contact.put("message", message);
                        contact.put("filename", filename);
                        contact.put("date", date);

                        // adding contact to contact list
                        DownloadList.add(contact);
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
                    getActivity(), DownloadList,
                    R.layout.list_downloads, new String[]{"date", "message",
                    "filename", "title"}, new int[]{R.id.dateDownloads,
                    R.id.downloads});

            lv.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(getActivity(), "No Downloads Yet", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class Downloader extends AsyncTask<String, String, String> {
        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getActivity().getApplicationContext(),"Downloading File",Toast.LENGTH_SHORT).show();

            pDialog2 = new ProgressDialog(getActivity());
            pDialog2.setMessage("Downloading file. Please wait...");
            pDialog2.setIndeterminate(false);
            pDialog2.setMax(100);
            pDialog2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog2.setCanceledOnTouchOutside(false);
            pDialog2.setCancelable(true);
            pDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // cancel AsyncTask
                    cancel(true);
                }
            });
            pDialog2.show();
        }

        protected void onCancelled()
        {
            //called on ui thread
            if (pDialog2 != null) {
                pDialog2.dismiss();
                String root = Environment.getExternalStorageDirectory().toString();
                File file = new File(root +  "/"
                        + Utils.downloadDirectory +"/"+ FILENAME);

                try {
                    file.delete();
                    if (file.exists()) {
                        file.getCanonicalFile().delete();
                        if (file.exists()) {
                            getActivity().getApplicationContext().deleteFile(file.getName());
                        }

                    }
                    Toast.makeText(getActivity(),"Downloading Cancelled", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e){}
                super.onCancelled();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Update progress
            pDialog2.setProgress(Integer.parseInt(values[0]));

        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            Log.e("TAG1", "Inside DoInBackGround");
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                long lenghtOfFile = conection.getContentLength();

                //Get File if SD card is present

                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + Utils.downloadDirectory);
                }

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e("TAG1", "Directory Created.");
                }

                outputFile = new File(apkStorage, FILENAME);


                //Create New File if not present
                if (!outputFile.exists()) {
                    boolean b = outputFile.createNewFile();
                    if (b)
                        Log.e("TAG1", "File Created");
                }

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                Log.e("TAG1", "File Transferring");
                // Output stream to write file
                OutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }
                Log.e("TAG1", "File Transferred");
                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog2.isShowing())
                pDialog2.dismiss();

            Log.e("TAG1", "File onPostExecute");
            //Toast.makeText(getActivity().getApplicationContext(),"File Downloaded",Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            // Setting Dialog Title
            alertDialog.setTitle("File Downloaded");
            // Setting Dialog Message
            alertDialog.setMessage("Open File "+FILENAME);
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {

                    // Write your code here to invoke YES event
                    // Toast.makeText(getActivity().getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();

                    try {
                        File url = new File(
                                Environment.getExternalStorageDirectory() + "/"
                                        + Utils.downloadDirectory, FILENAME);

                        Intent intent = new Intent();

                        intent.setAction(android.content.Intent.ACTION_VIEW);

                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Uri uri = FileProvider.getUriForFile(getActivity(),
                                BuildConfig.APPLICATION_ID + ".provider",url );

                        if (url.toString().toLowerCase().contains(".doc") || url.toString().toLowerCase().contains(".docx")) {
                            // Word document
                            intent.setDataAndType(uri, "application/msword");
                        } else if (url.toString().toLowerCase().contains(".pdf")) {
                            // PDF file
                            intent.setDataAndType(uri, "application/pdf");
                        } else if (url.toString().toLowerCase().contains(".ppt") || url.toString().toLowerCase().contains(".pptx")) {
                            // Powerpoint file
                            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                        } else if (url.toString().toLowerCase().contains(".xls") || url.toString().toLowerCase().contains(".xlsx")) {
                            // Excel file
                            intent.setDataAndType(uri, "application/vnd.ms-excel");

                        } else if (url.toString().toLowerCase().contains(".gif")) {
                            // GIF file
                            intent.setDataAndType(uri, "image/gif");
                        } else if (url.toString().toLowerCase().contains(".jpg") || url.toString().toLowerCase().contains(".jpeg") || url.toString().toLowerCase().contains(".png")) {
                            // JPG file
                            intent.setDataAndType(uri, "image/jpeg");
                        } else if (url.toString().toLowerCase().contains(".txt")) {
                            // Text file
                            intent.setDataAndType(uri, "text/plain");
                        }  else {
                            intent.setDataAndType(uri, "*/*");
                        }

                        try {

                            startActivity(intent);

                        } catch (Exception e) {
                            // Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Error: ", e.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                   // Toast.makeText(getActivity().getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
            super.onPostExecute(result);


        }
    }
}