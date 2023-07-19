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
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;


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

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        PromoData.userRole = preferences.getString("UserRole", "");

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
        String url = "https://edgescanner.herokuapp.com/api/login"; // Replace with your API endpoint

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("email", emailAdd);
            requestData.put("password", passwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the successful response from the API

                        try {
                            String token = response.getString("token");

                            if(!token.isEmpty()) {

                                JSONObject userData = response.getJSONObject("user");
                                String firstName = userData.getString("firstname");
                                String lastName = userData.getString("lastname");
                                String locationCode = userData.getString("location_code");

                                // Store the retrieved user information in shared preferences or any other storage mechanism as needed
                                SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("FirstName", firstName);
                                editor.putString("LastName", lastName);
                                editor.putString("LocationCode", locationCode);
                                editor.apply();

                                if (PromoData.userRole.equals("Team Leader")) {
                                    Intent intent = new Intent(Login.this, TeamLeaderActivity.class);

                                    intent.putExtra("firstname", firstName);
                                    intent.putExtra("lastname", lastName);
                                    intent.putExtra("locationcode", locationCode);

                                    startActivity(intent);
                                    finish();
                                } else if (PromoData.userRole.equals("Promo Merchandiser")) {
                                    Intent intent = new Intent(Login.this, MainActivity.class);

                                    intent.putExtra("firstname", firstName);
                                    intent.putExtra("lastname", lastName);
                                    intent.putExtra("locationcode", locationCode);

                                    startActivity(intent);
                                    finish();
                                }

                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                Log.e(TAG, "5555 - Login Error" + response);

                                Log.i(TAG, "User Data: " + userData);
                                Log.i(TAG, "First Name: " + firstName);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof AuthFailureError) {
                            // Incorrect password
                            password.setError("Incorrect Password");
                        } else {
                            // Other error occurred
                            Toast.makeText(Login.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
            @Override
            public String getBodyContentType() {
                // Specify that the request body is in JSON format
                return "application/json";
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

}