package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{
    User user;
    String activityKey;
    TextView tvActivity, tvInfo;
    Button btnOne, btnTwo; //One: Accept - Done / Two: New - Cancel
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;
    Intent intent;
    RequestQueue queue;
    SharedPreferences sharedPreferences;
    String urlRandom = "https://www.boredapi.com/api/activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_activity);

        user = new User();
        tvActivity = findViewById(R.id.tvActivity);
        tvInfo = findViewById(R.id.tvYourActivity);
        btnOne = findViewById(R.id.btnOne);
        btnTwo = findViewById(R.id.btnTwo);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        queue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottomNavTab);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.it_home);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        else{
            prepareUserInformation(currentUser.getEmail());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", currentUser.getEmail());
            editor.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.it_home: break;
            case R.id.it_profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.it_ranking:
                intent = new Intent(this, RankingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void prepareUserInformation(String userEmail){
        user.setEmail(userEmail);
        findUserActivities();
    }

    private void findUserActivities() {
        db.collection("Activity")
                .whereEqualTo("userEmail", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            String key = "";
                            ArrayList<Activity> aux = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                aux.add(new Activity(document.getId(),
                                        document.getData().get("key").toString(),
                                        (Boolean) document.getData().get("done")));
                                if(!(Boolean) document.getData().get("done")){
                                    user.setActive(true);
                                    key = document.getData().get("key").toString();
                                }
                            }
                            user.setActivities(aux);
                            showScreen(key);
                        }
                        else
                            Log.v("ERROR-FindUserActivities", task.getException().getMessage());
                    }
                });
    }

    private void showScreen(String key){
        if(user.getActive())
            userHaveActivityScreen(key);
        else
            chooseActivityScreen();
    }

    private void userHaveActivityScreen(String key) {
        tvInfo.setVisibility(View.VISIBLE);
        btnOne.setText(R.string.btn_done);
        btnTwo.setText(R.string.btn_cancel);
        getActivityByKey(key);
    }

    private void chooseActivityScreen(){
        tvInfo.setVisibility(View.INVISIBLE);
        btnOne.setText(R.string.btn_accept);
        btnTwo.setText(R.string.btn_new);
        newActivity();
    }

    public void newActivity(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRandom, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tvActivity.setText(response.getString("activity"));
                            activityKey = response.getString("key");
                        } catch (JSONException e) {
                            Log.v("ERROR-API", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ERROR-API", error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getActivityByKey(String key){
        String url = "https://www.boredapi.com/api/activity?key="+key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tvActivity.setText(response.getString("activity"));
                        } catch (JSONException e) {
                            Log.v("ERROR-API", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ERROR-API", error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void updateData(String documentId) {
        db.collection("Activity").document(documentId).update("done", true);
    }

    private void userAcceptActivity(){
        Map<String, Object> activity = new HashMap<>();
        activity.put("userEmail", user.getEmail());
        activity.put("key", activityKey);
        activity.put("done", false);

        db.collection("Activity").add(activity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, getString(R.string.toast_new_activity_ok), Toast.LENGTH_SHORT).show();
                        Activity a = new Activity(documentReference.getId(), activityKey, false);
                        user.addActivity(a);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("ERROR-UserAcceptActivity", e.getMessage());
                    }
                });
    }

    public void clickedButtonOne(View view) {
        if(user.getActive()){
            for(Activity a : user.getActivities()){
                if(!a.getDone()){
                    updateData(a.getId());
                    a.setDone(true);
                    user.setActive(false);
                    chooseActivityScreen();
                    return;
                }
            }
        }
        else{
            userAcceptActivity();
            user.setActive(true);
            userHaveActivityScreen(activityKey);
        }
    }

    public void clickedButtonTwo(View view) {
        if(user.getActive()){
            for(Activity a : user.getActivities()){
                if(!a.getDone()){
                    removeActivity(a.getId());
                    user.deleteActivity(a);
                    user.setActive(false);
                    chooseActivityScreen();
                }
            }
        }
        else newActivity();
    }

    private void removeActivity(String documentId){
        db.collection("Activity").document(documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, getString(R.string.toast_delete_ok), Toast.LENGTH_SHORT).show();
                    }
                }) .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("ERROR-RemoveActivity", e.getMessage());
                    }
                });
    }
}