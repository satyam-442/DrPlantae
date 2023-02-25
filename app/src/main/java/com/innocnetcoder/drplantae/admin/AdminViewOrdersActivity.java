package com.innocnetcoder.drplantae.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.innocnetcoder.drplantae.R;
import com.innocnetcoder.drplantae.modal.AdminOrders;

public class AdminViewOrdersActivity extends AppCompatActivity {

    RecyclerView orderProList;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference ordersRef, adminOrdersRef;
    FirebaseAuth mAuth;
    String currentUserID;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);

        loadingBar = new ProgressDialog(this);

        orderProList = findViewById(R.id.order_list);

        mAuth = FirebaseAuth.getInstance();
        //currentUserID = mAuth.getCurrentUser().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        adminOrdersRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View");
        orderProList.setHasFixedSize(true);
        orderProList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadingBar.setTitle("please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(ordersRef, AdminOrders.class).build();
        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, @SuppressLint("RecyclerView") final int i, @NonNull final AdminOrders adminOrders) {
                holder.userName.setText("Name:- " + adminOrders.getNamee());
                holder.userPhone.setText(adminOrders.getPhonee());
                holder.userTotalPrice.setText(adminOrders.getTotalAmountt());
                holder.userDateTime.setText("Ordered on :- " + adminOrders.getDatee() + "  " + adminOrders.getTimee());
                holder.userShipAddress.setText("Address :- " + adminOrders.getAddresss() + " , " + adminOrders.getCityy());

                loadingBar.dismiss();

                holder.showAllProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid = getRef(i).getKey();
                        Intent intent = new Intent(AdminViewOrdersActivity.this, AdminUserProductsActivity.class);
                        //intent.putExtra("uid",adminOrders.getUidd());
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "YES",
                                        "NO"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminViewOrdersActivity.this);
                        builder.setTitle("Is this product shipped?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0) {
                                    String uid = getRef(i).getKey();
                                    String currUid = adminOrders.getUidd();
                                    //AddToDatabase(currUid);
                                    RemoveOrderThroughId(currUid);
                                } else {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                return new AdminOrdersViewHolder(view);
            }
        };
        orderProList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrderThroughId(String uid) {
        ordersRef.child(uid).removeValue();
        adminOrdersRef.child(uid).removeValue();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userPhone, userTotalPrice, userDateTime, userShipAddress;
        public Button showAllProduct;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_username_layout);
            userPhone = itemView.findViewById(R.id.order_phone_layout);
            userTotalPrice = itemView.findViewById(R.id.order_price_layout);
            userShipAddress = itemView.findViewById(R.id.order_address_city_layout);
            userDateTime = itemView.findViewById(R.id.order_date_time_layout);

            showAllProduct = itemView.findViewById(R.id.show_all_product);
        }
    }
}