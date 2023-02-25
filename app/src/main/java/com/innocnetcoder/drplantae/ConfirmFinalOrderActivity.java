package com.innocnetcoder.drplantae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    TextInputLayout txtConfirmName, txtConfirmEmail, txtConfirmPhone, txtConfirmAddress, txtConfirmCity , txtConfirmDate;
    Button btnConfirmFinalOrder, btnConfirmDate;
    FirebaseAuth mAuth;
    DatabaseReference confirmOrderRef;
    String currentUserId, totalAmount = "";
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("tPrice");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        confirmOrderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUserId);

        txtConfirmName = findViewById(R.id.shipment_name);
        txtConfirmPhone = findViewById(R.id.shipment_phone);
        txtConfirmEmail = findViewById(R.id.shipment_email);
        txtConfirmAddress = findViewById(R.id.shipment_address);
        txtConfirmCity = findViewById(R.id.shipment_city);
        txtConfirmDate = findViewById(R.id.shipment_dateEdt);

        btnConfirmDate = findViewById(R.id.shipment_dateBtn);
        btnConfirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ConfirmFinalOrderActivity.this,
                        android.R.style.Animation_Dialog,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                month = month + 1;
                //Log.d(TAG,"onDateSet: " + month + "/" + dayOfMonth + "/" + year);
                final String proDate = month + "/" + dayOfMonth + "/" + year;
                txtConfirmDate.getEditText().setText(proDate);
            }
        };

        btnConfirmFinalOrder = findViewById(R.id.confirm_order_btn);
        btnConfirmFinalOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckDetails();
            }
        });

        /*if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.checkSelfPermission(getActivity(),new String[]{Manifest.permission.READ_SMS,Manifest.permission.READ_SMS},);
        }*/
    }

    private void CheckDetails() {

        String cusName = txtConfirmName.getEditText().getText().toString();
        String cusPhone = txtConfirmPhone.getEditText().getText().toString();
        String cusAddress = txtConfirmAddress.getEditText().getText().toString();
        String cusCity = txtConfirmCity.getEditText().getText().toString();

        if (TextUtils.isEmpty(cusName) && TextUtils.isEmpty(cusPhone) && TextUtils.isEmpty(cusAddress) && TextUtils.isEmpty(cusCity))
        {
            Toast.makeText(getApplicationContext(), "Field's are empty!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //ConfirmOrder();
            final String saveCurrentTime, saveCurrentDate;
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calForDate.getTime());

            HashMap<String,Object> orderMap = new HashMap<>();
            orderMap.put("totalAmount",totalAmount);
            orderMap.put("name",cusName);
            orderMap.put("phone",cusPhone);
            orderMap.put("address",cusAddress);
            orderMap.put("city",cusCity);
            orderMap.put("date",saveCurrentDate);
            orderMap.put("time",saveCurrentTime);
            orderMap.put("uid",currentUserId);
            //orderMap.put("requiredOn",proDate);
            orderMap.put("state","not shipped");
            confirmOrderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Cart List")
                                .child("User View")
                                .child(currentUserId)
                                .child("Products")
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(), "Order Placed!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                }
            });
        }
    }
}