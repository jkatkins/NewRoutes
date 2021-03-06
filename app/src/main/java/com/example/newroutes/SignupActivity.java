package com.example.newroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.databinding.ActivitySignupBinding;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import es.dmoral.toasty.Toasty;

public class SignupActivity extends AppCompatActivity {

    EditText etUsername;
    EditText etPassword;
    EditText etConfirmPassword;
    Button btnSignup;
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        etConfirmPassword = binding.etConfirmPassword;
        btnSignup = binding.btnSignup;

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                createAccount(username,password,confirmPassword);
            }
        });
    }

    private void createAccount(String username, String password, String confirmPassword) {
        //TODO add case for duplicate username
        //TODO add password requirements
        if (username.isEmpty()) {
            Toasty.error(this,R.string.empty_username,Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toasty.error(this, R.string.empty_password, Toast.LENGTH_SHORT).show();
        } else if (!(password.equals(confirmPassword))) {
            Toasty.error(this, R.string.passowrd_mismatch, Toast.LENGTH_SHORT).show();
        } else {
            final ParseUser user = new ParseUser();
            // Set core properties
            user.setUsername(username);
            user.setPassword(password);
            final FriendsManager friendsManager = new FriendsManager();
            friendsManager.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        user.put("FriendsManager",friendsManager);
                        user.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toasty.success(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignupActivity.this,MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toasty.error(SignupActivity.this, "Error signing up, " + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toasty.error(SignupActivity.this, "Error signing up, " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}