package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.title_login));

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        mAuth=FirebaseAuth.getInstance();
    }

    public void signUpClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void signInClicked(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if(firstDataValidation(email, password))
            login(email, password);
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        else etEmail.setError(task.getException().getMessage());
                    }
                });
    }

    private boolean firstDataValidation(String email, String password) {
        Boolean e = validateEmail(email);
        Boolean p = validatePassword(password);
        return e && p;
    }

    private Boolean validatePassword(String password) {
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

    private Boolean validateEmail(String email) {
        if(email.equals("")){
            etEmail.setError(getString(R.string.err_field_required));
            return false;
        }
        return true;
    }
}