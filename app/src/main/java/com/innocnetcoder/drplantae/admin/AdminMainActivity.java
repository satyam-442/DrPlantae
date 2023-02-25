package com.innocnetcoder.drplantae.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.innocnetcoder.drplantae.R;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
    }

    public void goToAddPlantPage(View view) {
        Intent intent = new Intent(this, AddPlantActivity.class);
        startActivity(intent);
    }

    public void goToAddAccessoryPage(View view) {
        Intent intent = new Intent(this, AddAccessoryActivity.class);
        startActivity(intent);
    }

    public void goToViewOrdersPage(View view) {
        Intent intent = new Intent(this, AdminViewOrdersActivity.class);
        startActivity(intent);
    }
}