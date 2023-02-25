package com.innocnetcoder.drplantae;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.innocnetcoder.drplantae.modal.Plants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView product_image_details;
    String plantID, currentUserId, state = "Normal", imageUrl;
    TextView product_name_details, product_description_details, productTypeCard, productSizeCard, product_price_details, addToCartTxt;
    ElegantNumberButton numberBtn;

    FirebaseAuth mAuth;
    DatabaseReference cartRefList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        plantID = getIntent().getStringExtra("plantID");
        imageUrl = getIntent().getStringExtra("imageUrl");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        cartRefList = FirebaseDatabase.getInstance().getReference().child("Cart List");

        product_image_details = findViewById(R.id.product_image_details);
        product_name_details = findViewById(R.id.product_name_details);
        product_description_details = findViewById(R.id.product_description_details);
        productTypeCard = findViewById(R.id.productTypeCard);
        productSizeCard = findViewById(R.id.productSizeCard);
        product_price_details = findViewById(R.id.product_price_details);
        addToCartTxt = findViewById(R.id.addToCartTxt);
        numberBtn = findViewById(R.id.numberBtn);

        addToCartTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state.equals("Order Shipped") || state.equals("Order Placed")) {
                    Toast.makeText(getApplicationContext(), "Please wait till your product get shipped!", Toast.LENGTH_LONG).show();
                } else {
                    addingToCartList();
                }
            }
        });

        getProductDetails(plantID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", plantID);
        cartMap.put("pname", product_name_details.getText().toString());
        cartMap.put("price", product_price_details.getText().toString());
        cartMap.put("image", imageUrl);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("uid", currentUserId);
        cartMap.put("quantity", numberBtn.getNumber());
        cartMap.put("discount", "");
        cartRefList.child("User View")
                .child(currentUserId)
                .child("Products")
                .child(plantID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   cartRefList.child("Admin View")
                                                           .child(currentUserId).child("Products")
                                                           .child(plantID)
                                                           .updateChildren(cartMap)
                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                      @Override
                                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                                          if (task.isSuccessful()) {
                                                                                              Toast.makeText(getApplicationContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                                                                                              finish();
                                                                                          }
                                                                                      }
                                                                                  }
                                                           );
                                               }
                                           }
                                       }
                );
    }

    private void getProductDetails(String productId) {
        DocumentReference productRef = db.collection("Plants").document(productId);
        productRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot dataSnapshot) {
                Plants product = dataSnapshot.toObject(Plants.class);
                product_name_details.setText(product.getPlantName());
                product_description_details.setText(product.getPlantDescription());
                product_price_details.setText(product.getPlantPrice());
                productSizeCard.setText("Size: "+product.getPlantSize());
                productTypeCard.setText("Type: "+product.getPlantType());
                Picasso.with(getApplicationContext()).load(product.getImageOne()).into(product_image_details);
            }
        });
    }

    private void CheckOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUserId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String stateO = dataSnapshot.child("state").getValue().toString();
                    if (stateO.equals("shipped")) {
                        state = "Order Shipped";
                    } else if (stateO.equals("not shipped")) {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}