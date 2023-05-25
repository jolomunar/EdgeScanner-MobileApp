package apc.mobprog.myqrcodeandbarcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class LeaderPrivacyPolicy extends AppCompatActivity {

    WebView privPol;
    Button accept;
    public String htmlFile = "privacy.html";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        privPol = findViewById(R.id.privacy);
        accept = findViewById(R.id.accept);

        privPol.getSettings().setJavaScriptEnabled(true);
        privPol.loadUrl("file:///android_asset/" + htmlFile);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaderPrivacyPolicy.this, Login.class);
                startActivity(intent);
            }
        });
    }
}