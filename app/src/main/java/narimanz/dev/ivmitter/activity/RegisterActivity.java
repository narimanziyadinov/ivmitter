package narimanz.dev.ivmitter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import narimanz.dev.ivmitter.R;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button btnRegister;


    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.bar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finalUsername = username.getText().toString();
                String finalEmail = email.getText().toString();
                String finalPassword = password.getText().toString();

                if (TextUtils.isEmpty(finalUsername) || TextUtils.isEmpty(finalEmail) || TextUtils.isEmpty(finalPassword)){
                    Toast.makeText(RegisterActivity.this, "Пустые поля", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (finalPassword.length()<6){
                        Toast.makeText(RegisterActivity.this, "Пароль не должен быть меньше 6", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        registerUser(finalUsername, finalEmail, finalPassword);
                    }
                }
            }
        });
    }

    private void registerUser(final String username, String mail, String password){
        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", username);
                    hashMap.put("imageUrl", "default");
                    hashMap.put("status", "offline");
                    hashMap.put("search",username.toLowerCase());

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, MainMessengerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this, "Ошибка ввода данных", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
