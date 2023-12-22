package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class ConfigurationActivity extends AppCompatActivity {
    ConfigurationFragment configurationFragment;
    EditUsernameFragment editUsernameFragment;
    FragmentManager fm;
    String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        setTitle(R.string.title_configuration);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        configurationFragment = ConfigurationFragment.newInstance(username, email);
        ft.replace(R.id.fgConfiguration, configurationFragment);
        ft.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment retainedFragment = fm.findFragmentById(R.id.fgConfiguration);
        if(retainedFragment instanceof EditUsernameFragment){
            FragmentTransaction ft = fm.beginTransaction();
            configurationFragment = ConfigurationFragment.newInstance(username, email);
            ft.replace(R.id.fgConfiguration, configurationFragment);
            ft.commit();
        }
        else finish();
        return true;
    }

    public void logout(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void showEditUsernameFragment(String username){
        editUsernameFragment = EditUsernameFragment.newInstance(username);
        FragmentTransaction ft = fm.beginTransaction();

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE)
            ft.replace(R.id.fgEditUsername, editUsernameFragment);
        else
            ft.replace(R.id.fgConfiguration, editUsernameFragment);
        ft.commit();
    }

    public void updateUsername(String newUsername){
        Intent result = new Intent();
        result.putExtra("newUsername", newUsername);
        setResult(RESULT_OK, result);
        finish();
    }
}