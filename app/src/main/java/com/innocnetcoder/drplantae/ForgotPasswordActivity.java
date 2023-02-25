package com.innocnetcoder.drplantae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageView forgotPasswordScreenBack;
    MaterialButton requestMailBtn;
    TextInputLayout forgotPasswordEmail;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        forgotPasswordScreenBack = findViewById(R.id.forgotPasswordScreenBack);
        forgotPasswordScreenBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        forgotPasswordEmail = findViewById(R.id.forgotPasswordEmail);

        requestMailBtn = findViewById(R.id.requestMailBtn);
        requestMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotPasswordEmail.getEditText().getText().toString();

                if (email.isEmpty()){
                    showToast("Enter email");
                } else {
                    loadingBar(true);
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loadingBar(false);
                                showToast("Password Reset link sent to your associated mail.");
                                finish();
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
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadingBar(Boolean isLoading){
        if (isLoading){
            requestMailBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            forgotPasswordEmail.setAlpha(.5f);
            forgotPasswordEmail.setFocusable(false);
        } else {
            requestMailBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            forgotPasswordEmail.setAlpha(1);
            forgotPasswordEmail.setFocusableInTouchMode(true);
        }
    }
}