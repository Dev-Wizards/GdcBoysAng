package com.gdcboysang.sharikmushtaq.gdcboysang.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gdcboysang.sharikmushtaq.gdcboysang.R;


public class ShowLatestNews extends Fragment {
    public WebView web1;
    public ImageView imgView;
    String htmlText = " %s ";


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String url = getArguments().getString("url");
        String headline = getArguments().getString("headline");
        String message = getArguments().getString("message");

        //Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();

        View v=inflater.inflate(R.layout.fragment_show_latest_news, container, false);

        imgView=v.findViewById(R.id.imgView);
        final ProgressBar progressBar = (ProgressBar)v. findViewById(R.id.progress);
        web1=v.findViewById(R.id.web1);
        String myData = "<html><body style='text-align:justify;'><b>"+headline+
                "</b><p>"+message+"</p></body></html>";
        web1.loadData(String.format(htmlText, myData), "text/html", "utf-8");

        WebSettings webSettings = web1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web1.setBackgroundColor(Color.TRANSPARENT);

        // Force links and redirects to open in the WebView instead of in a browser
        web1.setWebViewClient(new WebViewClient());
        //GlideDrawableImageViewTarget imagePreview = new GlideDrawableImageViewTarget(imgView);
        Glide.with(this).load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imgView);
        Log.e("Tagg", Integer.toString(getFragmentManager().getBackStackEntryCount()));
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Latest News ");
    }
}