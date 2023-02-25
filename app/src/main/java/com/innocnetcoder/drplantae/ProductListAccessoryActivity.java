package com.innocnetcoder.drplantae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.innocnetcoder.drplantae.modal.Accessory;
import com.innocnetcoder.drplantae.modal.Plants;
import com.squareup.picasso.Picasso;

public class ProductListAccessoryActivity extends AppCompatActivity {

    RecyclerView productListRec;
    TextView noDataText;
    ProgressDialog dialog;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference plantsRef;
    String accessoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_accessory);

        accessoryName = getIntent().getStringExtra("accessoryName");

        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        plantsRef = db.collection("Accessory");

        noDataText = findViewById(R.id.noDataText);

        productListRec = findViewById(R.id.productListRec);
        productListRec.setHasFixedSize(true);
        productListRec.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.setMessage("please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        startListen();
    }

    private void startListen() {
        plantsRef.whereEqualTo("AccessoryType", accessoryName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                if (snapshot.isEmpty()) {
                    //noFactText.setVisibility(View.VISIBLE);
                    noDataText.setVisibility(View.VISIBLE);
                    productListRec.setVisibility(View.GONE);
                    dialog.dismiss();
                } else {
                    Query query = plantsRef.whereEqualTo("AccessoryType", accessoryName.toUpperCase())/*.orderBy("OwnerName", Query.Direction.ASCENDING)*/;
                    FirestoreRecyclerOptions<Accessory> options = new FirestoreRecyclerOptions.Builder<Accessory>().setQuery(query, Accessory.class).build();
                    FirestoreRecyclerAdapter<Accessory, ProductListActivity.PlantsViewHolder> fireAdapter = new FirestoreRecyclerAdapter<Accessory, ProductListActivity.PlantsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ProductListActivity.PlantsViewHolder holder, int position, @NonNull Accessory model) {
                            holder.plantNameCL.setText("Name: "+model.getAccessoryName());
                            holder.plantDescCL.setText("Desc: "+model.getAccessoryDescription());
                            holder.plantSizeCL.setText("Size: "+model.getAccessorySize());
                            holder.plantPriceCL.setText("Price: "+model.getAccessoryPrice());
                            holder.plantTypeCL.setText("Type: "+model.getAccessoryType());
                            Picasso.with(getApplicationContext()).load(model.getImageOne()).into(holder.productImageCL);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    /*ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("pid",model.getProdID());
                                    bundle.putString("uid",currentUserId);
                                    bundle.putString("imageUrl",model.getProdImage());
                                    productDetailsFragment.setArguments(bundle);
                                    ((MainActivity) getActivity()).replaceFragment(productDetailsFragment, "fragmentC");*/
                                    /*Intent intent = new Intent(getContext(), PropertyDetailActivity.class);
                                    intent.putExtra("propertyID", model.getPropertyID());
                                    startActivity(intent);*/
                                    Toast.makeText(ProductListAccessoryActivity.this, "Will be added to cart!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }

                        @NonNull
                        @Override
                        public ProductListActivity.PlantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
                            return new ProductListActivity.PlantsViewHolder(view);
                        }
                    };
                    productListRec.setAdapter(fireAdapter);
                    fireAdapter.startListening();
                }
            }
        });
    }
}