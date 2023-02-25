package com.innocnetcoder.drplantae.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.innocnetcoder.drplantae.ProductDetailsActivity;
import com.innocnetcoder.drplantae.ProductListAccessoryActivity;
import com.innocnetcoder.drplantae.ProductListActivity;
import com.innocnetcoder.drplantae.R;
import com.innocnetcoder.drplantae.modal.Plants;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    TextView ayurvedaTxt, glovesTxt, shrubsText, creepersText, fertilizersText, seedsText, plantPotTxt;
    RecyclerView popularProductRec;

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference productsRef;
    String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        productsRef = db.collection("Plants");

        popularProductRec = view.findViewById(R.id.popularProductRec);
        popularProductRec.setHasFixedSize(true);
        popularProductRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ayurvedaTxt = view.findViewById(R.id.ayurvedaTxt);
        ayurvedaTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductListActivity.class);
                intent.putExtra("plantName", "AYURVEDA");
                startActivity(intent);
            }
        });

        shrubsText = view.findViewById(R.id.shrubsText);
        shrubsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductListActivity.class);
                intent.putExtra("plantName", "SHRUBS");
                startActivity(intent);
            }
        });

        creepersText = view.findViewById(R.id.creepersText);
        creepersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductListActivity.class);
                intent.putExtra("plantName", "CREEPERS");
                startActivity(intent);
            }
        });

        glovesTxt = view.findViewById(R.id.glovesTxt);
        glovesTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductListAccessoryActivity.class);
                intent.putExtra("accessoryName", "GLOVES");
                startActivity(intent);
            }
        });

        fertilizersText = view.findViewById(R.id.fertilizersText);
        fertilizersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductListAccessoryActivity.class);
                intent.putExtra("accessoryName", "Fertilizer".toUpperCase());
                startActivity(intent);
            }
        });

        seedsText = view.findViewById(R.id.seedsText);
        seedsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductListAccessoryActivity.class);
                intent.putExtra("accessoryName", "Seeds".toUpperCase());
                startActivity(intent);
            }
        });

        plantPotTxt = view.findViewById(R.id.plantPotTxt);
        plantPotTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductListAccessoryActivity.class);
                intent.putExtra("accessoryName", "PlantPot".toUpperCase());
                startActivity(intent);
            }
        });

        popularProductRec = view.findViewById(R.id.popularProductRec);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startListeningForPopularProducts();
    }

    private void startListeningForPopularProducts() {
        productsRef.orderBy("PlantName").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(final QuerySnapshot snapshot) {
                if (snapshot.isEmpty()) {
                    //showErrorMessage();
                    //loading(false);
                } else {
                    popularProductRec.setVisibility(View.VISIBLE);
                    Query query = productsRef.orderBy("PlantName", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<Plants> options = new FirestoreRecyclerOptions.Builder<Plants>().setQuery(query, Plants.class).build();
                    FirestoreRecyclerAdapter<Plants, PopularHolder> fireAdapter = new FirestoreRecyclerAdapter<Plants, PopularHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull PopularHolder holder, int position, @NonNull Plants model) {
                            holder.plantNameCL.setText(model.getPlantName());
                            holder.plantTypeCL.setText(model.getPlantType());
                            holder.plantPriceCL.setText("Rs. " + model.getPlantPrice());
                            holder.setProductImage(getActivity().getBaseContext(), model.getImageOne());

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                                    intent.putExtra("plantID", model.getPlantID());
                                    intent.putExtra("imageUrl", model.getImageOne());
                                    startActivity(intent);
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public PopularHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_layout, parent, false);
                            return new PopularHolder(view);
                        }
                    };
                    popularProductRec.setAdapter(fireAdapter);
                    fireAdapter.startListening();
                }
            }
        });
    }

    class PopularHolder extends RecyclerView.ViewHolder {

        TextView plantNameCL, plantTypeCL, plantPriceCL;
        ImageView plantImageCL;

        public PopularHolder(@NonNull View itemView) {
            super(itemView);

            plantNameCL = itemView.findViewById(R.id.plantNameCL);
            plantTypeCL = itemView.findViewById(R.id.plantTypeCL);
            plantPriceCL = itemView.findViewById(R.id.plantPriceCL);
            plantImageCL = itemView.findViewById(R.id.plantImageCL);
        }

        public void setProductImage(Context ctx, String image) {
            ImageView catImage = itemView.findViewById(R.id.plantImageCL);
            Picasso.with(ctx).load(image).into(catImage);
        }

    }

}