package com.lyl.chattapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button registerButton;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        username = (EditText) findViewById(R.id.RegisterUserName);
        email = (EditText) findViewById(R.id.RegisterEmail);
        password= (EditText) findViewById(R.id.RegisterPassword);
        registerButton = (Button) findViewById(R.id.RegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

               if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                   Toast.makeText(RegisterActivity.this,"Boş Alan Birakmayiniz" ,Toast.LENGTH_SHORT).show();
               }

               else if (txt_password.length() < 6){
                   Toast.makeText(RegisterActivity.this,"Şifre en az 7 karakter olmalidir",Toast.LENGTH_SHORT).show();
               }

               else {
                   register(txt_username,txt_email,txt_password);
               }
            }
        });
    }

    private void register(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseuser = auth.getCurrentUser();
                    String userId = firebaseuser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id",userId);
                    hashMap.put("username",username);
                    hashMap.put("imageUrl","default");
                    hashMap.put("search", username.toLowerCase());

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Email veya Şifren ile kayıt olamadin !! ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}