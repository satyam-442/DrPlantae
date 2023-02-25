package com.innocnetcoder.drplantae.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.innocnetcoder.drplantae.Interfaces.ItemClickListener;
import com.innocnetcoder.drplantae.R;
import com.innocnetcoder.drplantae.ViewHolder.CartViewHolder;
import com.innocnetcoder.drplantae.fragments.CartFragment;
import com.innocnetcoder.drplantae.modal.Cart;
import com.squareup.picasso.Picasso;

public class AdminUserProductsActivity extends AppCompatActivity {

    RecyclerView productListUser;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference userProductRef;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        productListUser = findViewById(R.id.product_list);
        productListUser.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productListUser.setLayoutManager(layoutManager);

        userId = getIntent().getStringExtra("uid");

        userProductRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(userId)
                .child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(userProductRef,Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, AdminCartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, AdminCartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminCartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
                cartViewHolder.cart_name_layout.setText(cart.getPnamee());
                cartViewHolder.cart_quantity_layout.setText(cart.getQuantityy());
                cartViewHolder.cart_price_layout.setText(cart.getPricee());
                Picasso.with(getApplicationContext()).load(cart.getImagee()).into(cartViewHolder.cart_image_layout);
            }

            @NonNull
            @Override
            public AdminCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_item_layout,parent,false);
                return new AdminCartViewHolder(view);
            }
        };
        productListUser.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminCartViewHolder extends RecyclerView.ViewHolder{

        TextView cart_name_layout, cart_price_layout, cart_quantity_layout;
        ImageView cart_image_layout;

        public AdminCartViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_name_layout = itemView.findViewById(R.id.cart_name_layout);
            cart_price_layout = itemView.findViewById(R.id.cart_price_layout);
            cart_quantity_layout = itemView.findViewById(R.id.cart_quantity_layout);
            cart_image_layout = itemView.findViewById(R.id.cart_image_layout);
        }
    }
}