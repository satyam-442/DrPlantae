package com.innocnetcoder.drplantae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.innocnetcoder.drplantae.fragments.AboutUsFragment;
import com.innocnetcoder.drplantae.fragments.CartFragment;
import com.innocnetcoder.drplantae.fragments.ContactUsFragment;
import com.innocnetcoder.drplantae.fragments.HomeFragment;
import com.innocnetcoder.drplantae.fragments.OrdersFragment;
import com.innocnetcoder.drplantae.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navView;
    View content;
    Toolbar toolbar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference usersRef;
    String userId;
    ImageView header_profile_image;
    TextView navHeaderName, navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = findViewById(R.id.container);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = mAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");

        navView = findViewById(R.id.navView);
        View headerView = navView.inflateHeaderView(R.layout.nav_header);
        header_profile_image = headerView.findViewById(R.id.header_profile_image);
        navHeaderName = headerView.findViewById(R.id.navHeaderName);
        navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);

        navView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        navView.setCheckedItem(R.id.nav_home);
        //setFactsMainFragment();
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack("fragmentA")
                    .replace(R.id.container, new HomeFragment(), "fragmentA")
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String address = snapshot.child("Address").getValue().toString();

                    if (address.equals("NA")){
                        Snackbar.make(content, "Address not updated", Snackbar.LENGTH_LONG).setAction("Do it", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
                                startActivity(intent);
                                //Toast.makeText(MainActivity.this, "update address", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Intent intent = new Intent(getApplicationContext(),AddBidsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                setHomeFragment();
                break;
            case R.id.nav_profile:
                setProfileFragment();
                break;
            /*case R.id.nav_booking:
                setOrdersFragment();
                break;*/
            case R.id.nav_cart:
                setCartFragment();
                break;
            case R.id.nav_about_us:
                setAboutFragment();
                break;
            case R.id.nav_contactus:
                setContactFragment();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                sendUserToLogin();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setCartFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new CartFragment()).commit();
        getSupportActionBar().setTitle("Cart");
    }

    private void setOrdersFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new OrdersFragment()).commit();
        getSupportActionBar().setTitle("My Orders");
    }

    private void setHomeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
        getSupportActionBar().setTitle("Home");
    }

    private void setProfileFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();
        getSupportActionBar().setTitle("Profile");
    }

    private void setAboutFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new AboutUsFragment()).commit();
        getSupportActionBar().setTitle("About Us");
    }

    private void setContactFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new ContactUsFragment()).commit();
        getSupportActionBar().setTitle("Contact Us");
    }

    private void sendUserToLogin() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void replaceFragment(Fragment fragment, String tag) {
        //Get current fragment placed in container
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        //Prevent adding same fragment on top
        if (currentFragment.getClass() == fragment.getClass()) {
            return;
        }
        //If fragment is already on stack, we can pop back stack to prevent stack infinite growth
        if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
            getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        //Otherwise, just replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int fragmentsInStack = getSupportFragmentManager().getBackStackEntryCount();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentsInStack > 1) {
            // If we have more than one fragment, pop back stack
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().show();
        } else if (fragmentsInStack == 1) {
            // Finish activity, if only one fragment left, to prevent leaving empty screen
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void setDrawer_Locked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void setDrawer_UnLocked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
}