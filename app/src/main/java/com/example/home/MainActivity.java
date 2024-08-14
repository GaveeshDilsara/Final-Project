package com.example.home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Setup Drawer Layout and Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Setup Navigation Drawer Listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Setup Bottom Navigation View
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Default Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new FragmentHome()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.home:
                            selectedFragment = new FragmentHome();
                            break;

                        case R.id.recording:
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                            } else {
                                Intent intent = new Intent(MainActivity.this, VideoRecordingActivity.class);
                                startActivity(intent);
                            }
                            return true;

                        case R.id.sos:
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                requestSmsPermission();
                            } else {
                                sendEmergencyMessage();
                                selectedFragment = new SOSFragment();  // Navigate to SOSFragment
                            }
                            break;

                        case R.id.call24:
                            selectedFragment = new Call247Fragment();
                            break;

                        case R.id.add_friends:
                            selectedFragment = new AddFriendsFragment();
                            break;
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, selectedFragment)
                                .addToBackStack(null)
                                .commit();
                    }

                    return true;
                }
            };

    private void handleNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new FragmentHome())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.history:
                Toast.makeText(MainActivity.this, "History Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.contactlist:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new AddFriendsFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutUs.class);
                startActivity(intent);
                break;

            case R.id.login:
                Toast.makeText(MainActivity.this, "Log In Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.share:
                Toast.makeText(MainActivity.this, "Share Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rate_us:
                Toast.makeText(MainActivity.this, "Rate Us Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.self_defence:
                Toast.makeText(MainActivity.this, "Self Defence Selected", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, VideoRecordingActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendEmergencyMessage();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new SOSFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendEmergencyMessage() {
        SharedPreferences sharedPreferences = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);

        String contact1 = sharedPreferences.getString("contact1", "");
        String contact2 = sharedPreferences.getString("contact2", "");
        String contact3 = sharedPreferences.getString("contact3", "");

        String message = "Emergency! Please help me immediately.";

        if (!contact1.isEmpty()) {
            sendSms(contact1, message);
        }
        if (!contact2.isEmpty()) {
            sendSms(contact2, message);
        }
        if (!contact3.isEmpty()) {
            sendSms(contact3, message);
        }

        Toast.makeText(this, "Emergency message sent to contacts", Toast.LENGTH_SHORT).show();
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
