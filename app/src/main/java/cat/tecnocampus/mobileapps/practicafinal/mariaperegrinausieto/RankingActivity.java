package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RankingActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    FirebaseFirestore db;
    Intent intent;
    RecyclerView ranking;
    UserAdapter userAdapter;
    RecyclerView.LayoutManager layoutManager;
    BottomNavigationView bottomNavigationView;

    ArrayList<User> users = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        setTitle(R.string.title_ranking);

        ranking = findViewById(R.id.rvRanking);
        layoutManager = new LinearLayoutManager(this);
        ranking.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(users);
        ranking.setAdapter(userAdapter);

        bottomNavigationView = findViewById(R.id.bottomNavTab2);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.it_ranking);

        db = FirebaseFirestore.getInstance();
        getData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.it_home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.it_profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.it_ranking:
                break;
        }
        return true;
    }

    public void getData(){
        db.collection("Activity").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String email = document.getData().get("userEmail").toString();
                                if((Boolean)document.getData().get("done")){
                                    int index = userPosition(email);
                                    if(index != -1)
                                        users.get(index).addActivity(new Activity());
                                    else{
                                        User u = new User();
                                        u.setEmail(email);
                                        u.addActivity(new Activity());
                                        users.add(u);
                                    }
                                }
                            }
                            Collections.sort(users);
                            userAdapter.notifyDataSetChanged();
                        }
                        else{
                            Log.v("ERROR-GetData:", task.getException().getMessage());
                        }
                    }
                });
    }

    private int userPosition(String email){
        for(int i=0; i< users.size(); i++){
            if(users.get(i).getEmail().equals(email))
                return i;
        }
        return -1;
    }
}