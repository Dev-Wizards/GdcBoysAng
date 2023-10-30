package com.gdcboysang.sharikmushtaq.gdcboysang.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gdcboysang.sharikmushtaq.gdcboysang.R;


public class AboutCollege extends Fragment {
    public WebView webView;
    String htmlText = " %s ";
    String myData = "<html><body style='text-align:justify;'><b><center><font color=green><u>About College</u></font></center></b><p>Government Degree College Anantnag, is a premier educational institute of South Kashmir. It was established in 1950 as an intermediate co-education college in \n" +
            "\t\tAnantnag town. Later it was shifted to its present location which was a rest house of the Maharaja. In the early eighties a women's college was established in the town \n" +
            "\t\tthereby, making it a seat of learning for boys only.</p><p> Under the able guidance of various educationalists it has flourished and distinguished itself as one of the leading higher educational institutions of the state. During \n" +
            "\t\tits existence of 68 years it has produced a large human resource that has been serving in and out side the state in various capacities. The availability of quality learning\n" +
            "\t\tatmosphere with allied facilities was recognized by a national autonomous accreditation body NAAC Bangalore, in the year 2012 when it was reaccredited with grade \"A\"\n" +
            "\t\twith CGPA 3.03.</p><p>The college is affiliated to the University of Kashmir and is recognized under section 2(f) and 12(B) of the UGC Act of 1956. The college has also been given <strong>\"College with Potential for Excellence Status\"</strong> by UGC \n" +
            "\t\t  in 2016. </p></body></html>";
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View v=inflater.inflate(R.layout.fragment_about_college, container, false);
         webView =  v.findViewById(R.id.webView1);
        webView.loadData(String.format(htmlText, myData), "text/html", "utf-8");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);

        // Force links and redirects to open in the WebView instead of in a browser
        webView.setWebViewClient(new WebViewClient());
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("About College");

    }
}