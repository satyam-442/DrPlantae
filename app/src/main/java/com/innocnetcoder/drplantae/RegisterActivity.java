package com.innocnetcoder.drplantae;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton proceedWithDetailsButton;
    String gender;
    TextInputLayout userNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, genderEditText, phoneEditText;
    RadioGroup genderGroup;
    RadioButton male, female, other;
    //DatePicker datePickerBirthday;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        userNameEditText = findViewById(R.id.userNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        genderEditText = findViewById(R.id.genderEditText);

        genderGroup = findViewById(R.id.genderGroup);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        other = findViewById(R.id.other);
        //datePickerBirthday = findViewById(R.id.datePickerBirthday);
        progressBar = findViewById(R.id.progressBar);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonCLicked(view);
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonCLicked(view);
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonCLicked(view);
            }
        });

        proceedWithDetailsButton = findViewById(R.id.proceedWithDetailsButton);
        proceedWithDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = userNameEditText.getEditText().getText().toString();
                String lname = lastNameEditText.getEditText().getText().toString();
                String email = emailEditText.getEditText().getText().toString();
                String phone = phoneEditText.getEditText().getText().toString();
                String password = passwordEditText.getEditText().getText().toString();
                String confirmPassword = confirmPasswordEditText.getEditText().getText().toString();
                String gender = genderEditText.getEditText().getText().toString();

                if (fname.isEmpty() && lname.isEmpty()) {
                    showToast("Enter name");
                } else if (email.isEmpty()) {
                    showToast("Enter email");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showToast("Badly formatted email");
                } else if (password.isEmpty()) {
                    showToast("Enter password");
                } else if (password.length() < 8) {
                    showToast("Password must be at least 8 characters");
                } else if (confirmPassword.isEmpty()) {
                    showToast("Retype password");
                } else if (!password.equals(confirmPassword)) {
                    showToast("Password mismatched");
                } else if (gender.isEmpty()) {
                    showToast("Please select gender");
                } else {
                    loadingBar(true);

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String userID = mAuth.getCurrentUser().getUid();

                            HashMap userMap = new HashMap();
                            userMap.put("FirstName", fname);
                            userMap.put("LastName", lname);
                            userMap.put("Email", email);
                            userMap.put("Password", password);
                            userMap.put("Gender", gender);
                            userMap.put("Image", "default");
                            userMap.put("Address", "NA");
                            userMap.put("About", "I am plant lover and I'm here to take care of know more about plants");
                            userMap.put("Phone", phone);
                            userMap.put("UID", userID);
                            db.collection("Users").document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                    });
                }
                //Intent intent = new Intent(getApplicationContext(), UploadProfilePic.class);
                //startActivity(intent);
                //finish();
            }
        });
    }

    private void onRadioButtonCLicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.male:
                if (checked)
                    genderEditText.getEditText().setText("Male");
                break;
            case R.id.female:
                if (checked)
                    genderEditText.getEditText().setText("Female");
                break;
            case R.id.other:
                if (checked)
                    genderEditText.getEditText().setText("other");
                break;
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void loadingBar(Boolean isLoading) {
        if (isLoading) {
            proceedWithDetailsButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            userNameEditText.setAlpha(.5f);
            userNameEditText.setFocusable(false);
            userNameEditText.setEnabled(false);
            emailEditText.setAlpha(.5f);
            emailEditText.setFocusable(false);
            emailEditText.setEnabled(false);
            passwordEditText.setAlpha(.5f);
            passwordEditText.setFocusable(false);
            passwordEditText.setEnabled(false);
            confirmPasswordEditText.setAlpha(.5f);
            confirmPasswordEditText.setFocusable(false);
            confirmPasswordEditText.setEnabled(false);

            male.setEnabled(false);
            female.setEnabled(false);
            other.setEnabled(false);
        } else {
            proceedWithDetailsButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            userNameEditText.setAlpha(1);
            userNameEditText.setFocusableInTouchMode(true);
            userNameEditText.setEnabled(true);
            emailEditText.setAlpha(1);
            emailEditText.setFocusableInTouchMode(true);
            emailEditText.setEnabled(true);
            passwordEditText.setAlpha(1);
            passwordEditText.setFocusableInTouchMode(true);
            passwordEditText.setEnabled(true);
            confirmPasswordEditText.setAlpha(1);
            confirmPasswordEditText.setFocusableInTouchMode(true);
            confirmPasswordEditText.setEnabled(true);

            male.setEnabled(true);
            female.setEnabled(true);
            other.setEnabled(true);
        }
    }
}