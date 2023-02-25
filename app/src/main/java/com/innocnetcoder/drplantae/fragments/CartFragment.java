package com.innocnetcoder.drplantae.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.innocnetcoder.drplantae.ConfirmFinalOrderActivity;
import com.innocnetcoder.drplantae.ProductDetailsActivity;
import com.innocnetcoder.drplantae.R;
import com.innocnetcoder.drplantae.modal.Cart;
import com.squareup.picasso.Picasso;

public class CartFragment extends Fragment {

    LinearLayout emptyCartLinearLayout, hasCartLinearLayout, nameLayout, stateLayout;
    RelativeLayout totalLayout;
    RecyclerView productsInCartRecView;
    TextView totalCheckoutPriceTxt, checkoutBtnTxt, backToProductBtn, totalCartProPrice, msg1, totalCartProName;
    ProgressDialog dialog;
    MaterialCardView checkoutBtnCard;
    DatabaseReference cartListMainRef, cartAdmin, cartUser;
    String currentUserId;
    FirebaseAuth mAuth;
    int overAllTotalPrice = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        dialog = new ProgressDialog(getActivity());
        cartListMainRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(currentUserId).child("Products");

        cartAdmin = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(currentUserId).child("Products");
        cartUser = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(currentUserId).child("Products");

        nameLayout = view.findViewById(R.id.nameLayout);
        stateLayout = view.findViewById(R.id.stateLayout);
        totalLayout = view.findViewById(R.id.totalLayout);

        msg1 = view.findViewById(R.id.msg1);
        totalCartProName = view.findViewById(R.id.total_cart_product_name);
        emptyCartLinearLayout = view.findViewById(R.id.emptyCartLinearLayout);
        hasCartLinearLayout = view.findViewById(R.id.hasCartLinearLayout);

        productsInCartRecView = view.findViewById(R.id.productsInCartRecView);
        productsInCartRecView.setHasFixedSize(true);
        productsInCartRecView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalCheckoutPriceTxt = view.findViewById(R.id.totalCheckoutPriceTxt);
        checkoutBtnCard = view.findViewById(R.id.checkoutBtnCard);
        checkoutBtnTxt = view.findViewById(R.id.checkoutBtnTxt);
        checkoutBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "TOTAL AMOUNT IS " + String.valueOf(overAllTotalPrice), Toast.LENGTH_LONG).show();
                /*ConfirmFinalOrderFragment confirmFinalOrderFragment = new ConfirmFinalOrderFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Total Price",String.valueOf(overAllTotalPrice));
                confirmFinalOrderFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container,confirmFinalOrderFragment);
                transaction.addToBackStack(null);
                transaction.commit();*/
                Intent intent = new Intent(getContext(), ConfirmFinalOrderActivity.class);
                intent.putExtra("tPrice", String.valueOf(overAllTotalPrice));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.setMessage("please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        CheckOrderState();
        cartListMainRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    overAllTotalPrice = 0;
                    DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                            .child(currentUserId)
                            .child("Products");
                    FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                            .setQuery(cartListRef, Cart.class)
                            .build();
                    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                            cartViewHolder.txtProductName.setText(cart.getPnamee());
                            cartViewHolder.txtProductQuantity.setText(cart.getQuantityy());
                            cartViewHolder.txtProductPrice.setText(cart.getPricee());
                            Picasso.with(getActivity()).load(cart.getImagee()).into(cartViewHolder.imageProductImage);
                            int oneTypeProductTPrice = Integer.parseInt(cart.getPricee()) * Integer.parseInt(cart.getQuantityy());
                            overAllTotalPrice = overAllTotalPrice + oneTypeProductTPrice;
                            totalCheckoutPriceTxt.setText("Rs. " + String.valueOf(overAllTotalPrice) + " /-");
                            dialog.dismiss();
                            //overAllTotalPrice = oneTypeProductTPrice;

                            cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Edit",
                                                    "Delete"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Choose Your Option:");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            if (i == 0) {
                                                /*ProductDetailsFragment homeFragment = new ProductDetailsFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("pid", cart.getPidd());
                                                bundle.putString("imageUrl", cart.getImagee());
                                                homeFragment.setArguments(bundle);
                                                ((MainActivity) getActivity()).replaceFragment(homeFragment, "fragmentC");*/
                                                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                                                intent.putExtra("plantID", cart.getPidd());
                                                intent.putExtra("imageUrl", cart.getImagee());
                                                startActivity(intent);
                                            }
                                            if (i == 1) {
                                                cartAdmin.child(cart.getPidd()).removeValue();
                                                cartUser.child(cart.getPidd()).removeValue();
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_item_layout, parent, false);
                            CartViewHolder holder = new CartViewHolder(view);
                            return holder;
                        }
                    };
                    productsInCartRecView.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    emptyCartLinearLayout.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtProductPrice, txtProductQuantity;
        ImageView imageProductImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.cart_name_layout);
            txtProductPrice = itemView.findViewById(R.id.cart_price_layout);
            txtProductQuantity = itemView.findViewById(R.id.cart_quantity_layout);
            imageProductImage = itemView.findViewById(R.id.cart_image_layout);
        }
    }

    private void CheckOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUserId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String state = dataSnapshot.child("state").getValue().toString();
                    String nameP = dataSnapshot.child("name").getValue().toString();

                    if (state.equals("shipped")) {
                        nameLayout.setVisibility(View.VISIBLE);
                        totalCartProName.setText(nameP);
                        productsInCartRecView.setVisibility(View.GONE);
                        msg1.setVisibility(View.GONE);
                        checkoutBtnTxt.setVisibility(View.GONE);
                        totalLayout.setVisibility(View.GONE);
                        checkoutBtnCard.setVisibility(View.GONE);
                    } else if (state.equals("not shipped")) {
                        nameLayout.setVisibility(View.GONE);
                        stateLayout.setVisibility(View.GONE);
                        productsInCartRecView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        checkoutBtnTxt.setVisibility(View.GONE);
                        totalLayout.setVisibility(View.GONE);
                        checkoutBtnCard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}