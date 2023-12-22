package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    Intent intent;
    FirebaseStorage storage;
    StorageReference gsReference;
    SharedPreferences sharedPreferences;
    ImageView image;
    TextView tvUsername, tvNum;
    FirebaseFirestore db;
    String username, email, userId;
    ActivityResultLauncher<Intent> activityDataResult;
    int numActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle(R.string.title_profile);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        image = findViewById(R.id.imgProfile);
        tvNum = findViewById(R.id.tvNumUserActivities);
        tvUsername = findViewById(R.id.tvUsernameTitle);

        bottomNavigationView = findViewById(R.id.bottomNavTab3);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.it_profile);

        email = sharedPreferences.getString("email", "");

        findUserActivities();
        findUser();

        activityDataResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            if(!result.getData().getStringExtra("newUsername").equals(username))
                                updateUserUsername(result.getData().getStringExtra("newUsername"));
                        }
                    }
                });
    }

    private void updateUserUsername(String newUsername) {
        username = newUsername;
        tvUsername.setText(username);
        db.collection("User").document(userId).update("username", newUsername);
        Toast.makeText(this, getString(R.string.toast_update_username), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itConfiguration:
                Intent intent = new Intent(this, ConfigurationActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                activityDataResult.launch(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.it_home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.it_profile:  break;
            case R.id.it_ranking:
                intent = new Intent(this, RankingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public int chooseImage(){
        int num;
        if(numActivities <= 5) num = 7;
        else if(numActivities > 5 && numActivities <= 10) num=6;
        else if(numActivities>10 && numActivities <= 15) num=5;
        else if(numActivities>15 && numActivities<=20) num=4;
        else if(numActivities>20 && numActivities<=30) num=3;
        else if(numActivities>30 && numActivities<=40) num=2;
        else num=1;
        return num;
    }

    private void putImage() {
        int img = chooseImage();
        String url = "gs://practica4mariaperegrina.appspot.com/imgProfile"+img+".jpg";
        gsReference = storage.getReferenceFromUrl(url);
        Glide.with(this).load(gsReference).into(image);
    }

    private void findUserActivities() {
        db.collection("Activity")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                if((Boolean) document.getData().get("done"))
                                    numActivities ++;
                            }
                            tvNum.setText(getString(R.string.tv_num_activities_1)+" "+numActivities+" "+getString(R.string.tv_num_activities_2));
                            putImage();
                        }
                        else{
                            Log.v("ERROR-FindUserActivities", task.getException().getMessage());
                        }
                    }
                });
    }

    private void findUser() {
        db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if (task.getResult().size() == 1)
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    username = document.getData().get("username").toString();
                                    tvUsername.setText(username);
                                    userId = document.getId();
                                }
                        }
                        else{
                            Log.v("ERROR-FindUser", task.getException().getMessage());
                        }

                    }
                });
    }
}