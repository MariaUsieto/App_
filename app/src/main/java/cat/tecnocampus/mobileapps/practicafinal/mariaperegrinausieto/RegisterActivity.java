package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText etEmail, etPassword, etUsername;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(getString(R.string.title_register));

        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etUsername = findViewById(R.id.etUsername);

        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void signUpClicked(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        if(firstDataValidation(username, password, email))
            register(username, password, email);
    }

    public void register(String username, String password, String email) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            addUserToDataBase(email, username);
                        else
                            etEmail.setError(task.getException().getMessage());
                    }
                });
    }

    private void addUserToDataBase(String email, String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("username", username);

        db.collection("User")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.toast_register_ok), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("ERROR-AddUser:", e.getMessage());
                    }
                });
    }

    private boolean firstDataValidation(String username, String password, String email) {
        Boolean e =  validateEmail(email);
        Boolean p = validatePassword(password);
        Boolean u = validateUsername(username);
        return e && p && u;
    }

    private boolean validateEmail(String email) {
        if(email.equals("")){
            etEmail.setError(getString(R.string.err_field_required));
            return false;
        }
        return true;
    }

    private boolean validateUsername(String username) {
        Pattern regex = Pattern.compile("^[a-zA-Z0-9]+$");
        if(username.equals(""))
            etUsername.setError(getString(R.string.err_field_required));
        else if(!regex.matcher(username).matches())
                etUsername.setError(getString(R.string.err_incorrect_value));
        else return true;

        return false;
    }

    private boolean validatePassword(String password) {
        if(password.equals("")){
            etPassword.setError(getString(R.string.err_field_required));
            return false;
        }
        else if (password.contains(" ")){
            etPassword.setError(getString(R.string.err_incorrect_value));
            return false;
        }
        return true;
    }
}