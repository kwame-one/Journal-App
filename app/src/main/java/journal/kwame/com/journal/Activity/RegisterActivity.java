package journal.kwame.com.journal.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import journal.kwame.com.journal.R;
import journal.kwame.com.journal.Utils.UserPreference;

/**
 * Created by user on 6/29/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private EditText email, password;
    private Button btnRegister;
    private TextView login;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private UserPreference userPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        userPreference = new UserPreference(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btnRegister);
        login = findViewById(R.id.login);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Signing up. Please wait...");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register() {
        String username = email.getText().toString();
        String secret = password.getText().toString();

        if (username.trim().length() == 0 || secret.trim().length() == 0) {
            Toast.makeText(RegisterActivity.this, "Please provide email and password", Toast.LENGTH_SHORT).show();
        }else {
            dialog.show();
            auth.createUserWithEmailAndPassword(username, secret)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                userPreference.saveUserDetails(user);
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                dialog.dismiss();
                                startActivity(intent);
                                finish();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "email already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }
}
