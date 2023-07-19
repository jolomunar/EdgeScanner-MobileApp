package apc.mobprog.myqrcodeandbarcodescanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

        private TextView textView;
        TextView verifyMsg;
        FirebaseAuth auth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            PromoData.firstname = getIntent().getStringExtra("firstname");
            PromoData.lastname = getIntent().getStringExtra("lastname");
            PromoData.locationCode = getIntent().getStringExtra("locationcode");


            auth = FirebaseAuth.getInstance();
            verifyMsg = findViewById(R.id.verifyEmailMsg);

            if(auth.getCurrentUser().isEmailVerified()){
                verifyMsg.setVisibility(View.VISIBLE);
            }

            verifyMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //send verification link
                    auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Verification Link Email Sent", Toast.LENGTH_SHORT).show();
                            verifyMsg.setVisibility(View.GONE);

                        }

                    });
                }
            });

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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("barcode_nr", intentResult.getContents());
            intent.putExtra("firstname", PromoData.firstname);
            intent.putExtra("lastname", PromoData.lastname);
            intent.putExtra("locationcode", PromoData.locationCode);
            startActivity(intent);
        }
    }