package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import java.util.HashMap;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.text.TextUtils;
import android.content.Intent;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText firstname, lastname, email, password, mobNum;
    Spinner locationCode, roleSpinner;
    String first, sur, emailAdd, passwd, locCode, uRole;
    Button register;

    FirebaseDatabase node = FirebaseDatabase.getInstance();
    DatabaseReference ref;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        locationCode = findViewById(R.id.locationCode);
        roleSpinner = findViewById(R.id.role);

        //Location Code Dropdown
        locationCode = findViewById(R.id.locationCode);
        ArrayAdapter<String> outlet = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.locationCode));
        outlet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationCode.setAdapter(outlet);

        //User Role Code Dropdown
        roleSpinner = findViewById(R.id.role);
        ArrayAdapter<String> userRole = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.role));
        userRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(userRole);

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
                locCode = locationCode.getSelectedItem().toString();
                uRole = roleSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(first)) {
                    firstname.setError("Please enter First Name");
                    return;
                } else if (TextUtils.isEmpty(sur)) {
                    lastname.setError("Please enter Last Name");
                    return;
                } else if (uRole.equals("Select Role")) {
                    Toast.makeText(Register.this, "Please select a role", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(emailAdd)) {
                    email.setError("Please enter Email");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches()) {
                    email.setError("Email is invalid");
                    return;
                } else if (TextUtils.isEmpty(passwd) || passwd.length() < 8) {
                    password.setError("Password must contain at least 8 characters");
                    return;
                } else if (locCode.equals("Select Location Code")) {
                    Toast.makeText(Register.this, "Please select a location code", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerAccount(first, sur, emailAdd, passwd, locCode, uRole);
            }
        });
    }

    private void registerAccount(String first, String sur, String emailAdd, String passwd, String locCode, String role) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(emailAdd, passwd).addOnCompleteListener(Register.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Account registered successfully
                            Toast.makeText(Register.this, "Account Registered", Toast.LENGTH_LONG).show();
                            FirebaseUser user = auth.getCurrentUser();
                            String userId = user.getUid();
                            ref = node.getReference("registration-data").child("user");

                            Intent intent;
                            if (role.equals("Team Leader")) {
                                // Redirect to Team Leader activities
                                intent = new Intent(Register.this, LeaderPrivacyPolicy.class);
                            } else {
                                // Redirect to regular user activities
                                intent = new Intent(Register.this, PrivacyPolicy.class);
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();

                            HashMap<String, String> promoMap = new HashMap<>();
                            promoMap.put("firstname", first);
                            promoMap.put("lastname", sur);
                            promoMap.put("email", emailAdd);
                            promoMap.put("password", passwd);
                            promoMap.put("outlet", locCode);
                            promoMap.put("userRole", role);

                            ref.child(userId).setValue(promoMap);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                email.setError("Email already exists");
                                email.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}