package com.innocnetcoder.drplantae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.innocnetcoder.drplantae.admin.AdminLoginActivity;

public class LoginActivity extends AppCompatActivity {

    TextView createNewAccountLink, forgotPasswordTxt, adminLogin;
    MaterialButton loginBtn;
    ProgressBar progressBar;
    TextInputLayout loginEmail, loginPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);

        progressBar = findViewById(R.id.progressBar);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getEditText().getText().toString();
                String password = loginPassword.getEditText().getText().toString();

                if (email.isEmpty()){
                    showToast("Enter email");
                } else if (password.isEmpty()){
                    showToast("Enter password");
                } else {
                    loadingBar(true);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                loadingBar(false);
                            } else {
                                String msg = task.getException().getMessage();
                                showToast(msg);
                                loadingBar(false);
                            }
                        }
                    });
                }
            }
        });

        forgotPasswordTxt = findViewById(R.id.forgotPasswordTxt);
        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        createNewAccountLink = findViewById(R.id.createNewAccountLink);
        createNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        adminLogin = findViewById(R.id.adminLogin);
        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadingBar(Boolean isLoading){
        if (isLoading){
            loginBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            loginEmail.setAlpha(.5f);
            loginEmail.setFocusable(false);
            loginPassword.setAlpha(.5f);
            loginPassword.setFocusable(false);
        } else {
            loginBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            loginEmail.setAlpha(1);
            loginEmail.setFocusableInTouchMode(true);
            loginPassword.setAlpha(1);
            loginPassword.setFocusableInTouchMode(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null ){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}