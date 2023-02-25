package com.innocnetcoder.drplantae;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    TextView selectImageText;
    ImageView profileImage;
    TextInputLayout firstNameEditText, lastNameEditText, addressEditText, aboutEditText;
    String currentUserId;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef;
    MaterialButton editProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = db.collection("Users");

        selectImageText = findViewById(R.id.selectImageText);
        profileImage = findViewById(R.id.profileImage);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        aboutEditText = findViewById(R.id.aboutEditText);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        usersRef.document(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                if (snapshot.exists()) {
                    String fname = snapshot.getString("FirstName");
                    String lname = snapshot.getString("LastName");
                    String email = snapshot.getString("Email");
                    String image = snapshot.getString("Image");
                    String phone = snapshot.getString("Phone");
                    String about = snapshot.getString("About");
                    String address = snapshot.getString("Address");

                    firstNameEditText.getEditText().setText(fname);
                    lastNameEditText.getEditText().setText(lname);
                    addressEditText.getEditText().setText(address);
                    aboutEditText.getEditText().setText(about);
                    //profileAddress.setText(address);

                    if (image.equals("default")) {
                        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile_dummy).into(profileImage);
                        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_dummy).into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile_dummy).into(profileImage);
                            }
                        });
                    }
                    //loading(false);
                } else {
                    Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                }

            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}