package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.net.HttpURLConnection;
import java.util.HashMap;

import android.text.TextUtils;
import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText firstname, lastname, email, password;
    String first, sur, emailAdd, passwd;
    Button register;

    FirebaseDatabase node = FirebaseDatabase.getInstance();
    DatabaseReference ref = node.getReference().child("registration-data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        beginOnClick();
    }
    public void beginOnClick() {

        register = findViewById(R.id.button6);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                first = firstname.getText().toString();
                sur = lastname.getText().toString();
                emailAdd = email.getText().toString();
                passwd = password.getText().toString();

                if (TextUtils.isEmpty(first)){
                    firstname.setError("Please Enter Email");
                    return;
                } else if (TextUtils.isEmpty(sur)) {
                    lastname.setError("Please Enter Password");
                    return;
                } else if (TextUtils.isEmpty(emailAdd)) {
                    email.setError("Please Enter Email");
                    return;
                } else if (TextUtils.isEmpty(passwd)) {
                    password.setError("Please Enter Password");
                    return;
                } else {
                    registerAccount(first, sur, emailAdd, passwd);
                }
            }
        });
    }
    public void registerAccount (String first, String sur, String emailAdd, String passwd){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(emailAdd, passwd).addOnCompleteListener(Register.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Register.this, "Account Registered", Toast.LENGTH_LONG).show();
                            FirebaseUser user= auth.getCurrentUser();

                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            HashMap<String , String> promoMap = new HashMap<>();

                            promoMap.put("firstname", first);
                            promoMap.put("lastname", sur);
                            promoMap.put("email", emailAdd);
                            promoMap.put("password", passwd);

                            ref.push().setValue(promoMap);
                        }
                        else {

                        }
                    }
                });
    }
}