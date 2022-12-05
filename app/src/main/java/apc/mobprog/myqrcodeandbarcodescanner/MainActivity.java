package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.net.HttpURLConnection;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

        private TextView textView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            textView = findViewById(R.id.textview);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PackageManager.PERMISSION_GRANTED);

        }
        public void ScanButton(View view){
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
            intentIntegrator.setCaptureActivity(CapturePortrait.class);
            intentIntegrator.initiateScan();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            Intent intent = new Intent(this, DisplayActivity.class);
            intent.putExtra("barcode_nr", intentResult.getContents());
            startActivity(intent);
        }
    }