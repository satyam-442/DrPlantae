package com.innocnetcoder.drplantae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.innocnetcoder.drplantae.modal.Plants;
import com.squareup.picasso.Picasso;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView productListRec;
    TextView noDataText;
    ProgressDialog dialog;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference plantsRef;
    String plantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        plantName = getIntent().getStringExtra("plantName");

        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        plantsRef = db.collection("Plants");

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
        plantsRef.whereEqualTo("PlantType", plantName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                if (snapshot.isEmpty()) {
                    //noFactText.setVisibility(View.VISIBLE);
                    noDataText.setVisibility(View.VISIBLE);
                    productListRec.setVisibility(View.GONE);
                    dialog.dismiss();
                } else {
                    Query query = plantsRef.whereEqualTo("PlantType", plantName.toUpperCase())/*.orderBy("OwnerName", Query.Direction.ASCENDING)*/;
                    FirestoreRecyclerOptions<Plants> options = new FirestoreRecyclerOptions.Builder<Plants>().setQuery(query, Plants.class).build();
                    FirestoreRecyclerAdapter<Plants, PlantsViewHolder> fireAdapter = new FirestoreRecyclerAdapter<Plants, PlantsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull PlantsViewHolder holder, int position, @NonNull Plants model) {
                            holder.plantNameCL.setText("Name: "+model.getPlantName());
                            holder.plantDescCL.setText("Desc: "+model.getPlantDescription());
                            holder.plantSizeCL.setText("Size: "+model.getPlantSize());
                            holder.plantPriceCL.setText("Price: "+model.getPlantPrice());
                            holder.plantTypeCL.setText("Type: "+model.getPlantType());
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
                                    Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                                    intent.putExtra("plantID", model.getPlantID());
                                    intent.putExtra("imageUrl", model.getImageOne());
                                    startActivity(intent);
                                }
                            });
                            dialog.dismiss();
                        }

                        @NonNull
                        @Override
                        public PlantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
                            return new PlantsViewHolder(view);
                        }
                    };
                    productListRec.setAdapter(fireAdapter);
                    fireAdapter.startListening();
                }
            }
        });
    }

    static class PlantsViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageCL;
        TextView plantNameCL, plantDescCL, plantTypeCL, plantSizeCL, plantPriceCL;
        public PlantsViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageCL = itemView.findViewById(R.id.productImageCL);

            plantNameCL = itemView.findViewById(R.id.plantNameCL);
            plantDescCL = itemView.findViewById(R.id.plantDescCL);
            plantPriceCL = itemView.findViewById(R.id.plantPriceCL);
            plantTypeCL = itemView.findViewById(R.id.plantTypeCL);
            plantSizeCL = itemView.findViewById(R.id.plantSizeCL);
        }
    }
    
}