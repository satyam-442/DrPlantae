package com.innocnetcoder.drplantae.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.innocnetcoder.drplantae.AddressActivity;
import com.innocnetcoder.drplantae.EditProfileActivity;
import com.innocnetcoder.drplantae.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    ImageView profileImage;
    TextView profileName, profileAddress, profileAbout, profileEmail, profilePhone;
    String currentUserId;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef;
    MaterialButton editProfileBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = db.collection("Users");

        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        profileAddress = view.findViewById(R.id.profileAddress);
        profileAbout = view.findViewById(R.id.profileAbout);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePhone = view.findViewById(R.id.profilePhone);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

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

                    if (address.equals("NA")){
                        profileAddress.setText("Please update the address");
                        profileAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), AddressActivity.class);
                                startActivity(intent);
                            }
                        });
                    } else {
                        profileAddress.setText(address);
                    }

                    profileName.setText(fname + " " +lname);
                    profileEmail.setText(email);
                    profilePhone.setText(phone);
                    profileAbout.setText(about);
                    //profileAddress.setText(address);

                    if (image.equals("default")) {
                        Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile_dummy).into(profileImage);
                        Picasso.with(getActivity()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_dummy).into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                                Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile_dummy).into(profileImage);
                            }
                        });
                    }
                    //loading(false);
                } else {
                    Toast.makeText(getContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }
}