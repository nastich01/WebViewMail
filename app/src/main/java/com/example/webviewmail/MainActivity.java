package com.example.webviewmail;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
//implements GestureDetector.OnGestureListener

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE=150;
    private GestureDetector gestureDetector;
    WebView webView;
    TextView textUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        textUrl = findViewById(R.id.textViewUrl);
        Button backButton =(Button) findViewById(R.id.buttonback);
        Button forwardButton =(Button) findViewById(R.id.buttonnext);
        Button casheButton =(Button) findViewById(R.id.casheBtn);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);

        new Thread(new Runnable() {
            public void run() {
                try{
                    String content = getContent("https://mail.ru/");
                    webView.post(new Runnable() {
                        public void run() {
                            webView.loadDataWithBaseURL("https://mail.ru/",content, "text/html", "UTF-8", "https://mail.ru/");
                            Toast.makeText(getApplicationContext(), "Данные загружены", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (IOException ex){
                }
            }
        }).start();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Going back if canGoBack true
                if(webView.canGoBack()){
                    webView.goBack();
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go Forward if canGoForward is frue

                if(webView.canGoForward()){
                    webView.goForward();
                }
            }
        });

        casheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearCache(true);
                Toast.makeText(getApplicationContext(), "Кэш очищен", Toast.LENGTH_SHORT).show();
            }
        });

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                Log.d("WebView", "your current url when webpage loading.." + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
                textUrl.setText(url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        this.gestureDetector = new GestureDetector(MainActivity.this, this);
    }

//свайп_перезагрузка
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                //x2 = event.getX();
                y2 = event.getY();

                //float valueX = x2-x1;
                float valueY = y2-y1;

                if (Math.abs(valueY)>MIN_DISTANCE) {
                    if (y2>y1){
                        Toast.makeText(this, "Загрузка", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            public void run() {
                                try{
                                    String url = "https://mail.ru/";
                                    String content = getContent(url);
                                    webView.post(new Runnable() {
                                        public void run() {
                                            webView.loadDataWithBaseURL(url,content, "text/html", "UTF-8", "https://mail.ru/");
                                            Toast.makeText(getApplicationContext(), "Данные загружены", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                catch (IOException ex){
                                }
                            }
                        }).start();
                    }
                }


        }

        return super.onTouchEvent(event);
    }

    private String getContent(String path) throws IOException {
        BufferedReader reader=null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            URL url=new URL(path);
            connection =(HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            stream = connection.getInputStream();
            reader= new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf=new StringBuilder();
            String line;
            while ((line=reader.readLine()) != null) {
                buf.append(line).append("\n");
            }
            return(buf.toString());
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}