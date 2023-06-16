package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.util.Patterns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.net.HttpURLConnection;
import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {

    TextView register, forgot;
    EditText email, password;
    String emailAdd, passwd;
    Button login;
    private FirebaseAuth mAuth;
    private static final String TAG = "Login";

    FirebaseDatabase node = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        beginOnClick();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Login.this, Register.class);
                startActivity(intent);
            }
        });
        forgot =  findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void beginOnClick() {
        login = findViewById(R.id.button3);

        //Log In Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailAdd = email.getText().toString();
                passwd = password.getText().toString();

                if (TextUtils.isEmpty(emailAdd)) {
                    email.setError("Please Enter Email");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches()) {
                    email.setError("Email is Invalid");
                    return;
                } else if (TextUtils.isEmpty(passwd)) {
                    password.setError("Please Enter Password");
                    return;
                } else {
                    signInUser(emailAdd, passwd);
                }

            }
        });
    }

    private void signInUser(String emailAdd, String passwd) {
        mAuth.signInWithEmailAndPassword(emailAdd, passwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser newUser = mAuth.getCurrentUser();
                    String userUid = newUser.getUid();

                    PromoData.email = email.getText().toString().trim();
                    PromoData.password = password.getText().toString().trim();

                    DatabaseReference ref = node.getReference("registration-data").child("user").child(userUid);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                email.setError(null);
                                String regPass = dataSnapshot.child("password").getValue(String.class);

                                if (regPass != null && regPass.equals(PromoData.password)) {
                                    email.setError(null);

                                    PromoData.firstname = dataSnapshot.child("firstname").getValue(String.class);
                                    PromoData.lastname = dataSnapshot.child("lastname").getValue(String.class);
                                    PromoData.locationCode = dataSnapshot.child("outlet").getValue(String.class);
                                    PromoData.userRole = dataSnapshot.child("userRole").getValue(String.class);
                                }
                            }

                            // Check user role and perform redirection
                            if (PromoData.userRole != null) {
                                if (PromoData.userRole.equals("Team Leader")) {
                                    Intent intent = new Intent(Login.this, TeamLeaderActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (PromoData.userRole.equals("Promo Merchandiser")) {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Login.this, "User role not defined", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        email.setError("User Invalid or Non-existent");
                        email.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        password.setError("Incorrect Password");
                        password.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



}