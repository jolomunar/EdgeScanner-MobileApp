package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;


public class PrivacyPolicy extends AppCompatActivity {

    WebView privPol;
    Button accept;
    public String htmlFile = "privacy.html";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        PromoData.userRole = preferences.getString("UserRole", "");

        privPol = findViewById(R.id.privacy);
        accept = findViewById(R.id.accept);

        privPol.getSettings().setJavaScriptEnabled(true);
        privPol.loadUrl("file:///android_asset/" + htmlFile);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrivacyPolicy.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}