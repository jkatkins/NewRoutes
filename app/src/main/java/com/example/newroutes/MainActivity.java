package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.newroutes.Fragments.HomeFragment;
import com.example.newroutes.Fragments.ProfileFragment;
import com.example.newroutes.Fragments.RoutesFragment;
import com.example.newroutes.Fragments.UsersFragment;
import com.example.newroutes.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    private Toolbar toolbar;
    private FrameLayout flContainer;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting up View binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        flContainer = binding.flContainer;
        bottomNavigationView = binding.bottomNavigation;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String tag = "";
                switch(item.getItemId()) {
                    case R.id.item_new:
                        Intent createIntent = new Intent(MainActivity.this,RouteOptionsActivity.class);
                        startActivity(createIntent);
                        return false;
                    case R.id.item_friends: //TODO fill this out
                        fragment = new UsersFragment();
                        break;
                    case R.id.item_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.item_routes:
                        fragment = new RoutesFragment();
                        break;
                    case R.id.item_profile:
                    default:
                        fragment = new ProfileFragment();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.flContainer);
                if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
                    return false;
                } else {
                    fragmentManager.beginTransaction().replace(flContainer.getId(), fragment).commit();
                    return true;
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.item_home);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // ...
        mFunctions = FirebaseFunctions.getInstance();

    }

    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }


}