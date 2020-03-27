package com.example.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
  private EditText email,password;
  private TextView register_txt;
  private Button loginButton;
  private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register_txt=findViewById(R.id.register_txt);
        loginButton=findViewById(R.id.loginbutton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
               builder.setMessage("Loading....");
               final AlertDialog dialog=builder.create();
           String em=email.getText().toString().trim();
           String pass=password.getText().toString().trim();

           if(!TextUtils.isEmpty(em) && !TextUtils.isEmpty(pass)) {
               auth.signInWithEmailAndPassword(em, pass).addOnSuccessListener(LoginActivity.this,
                       new OnSuccessListener<AuthResult>() {

                           @Override
                           public void onSuccess(AuthResult authResult) {
                               dialog.dismiss();
                               Toast.makeText(getApplicationContext(), "welcome...", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                           }

                       }).addOnFailureListener(LoginActivity.this,
                       new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               dialog.dismiss();
                               Toast.makeText(getApplicationContext(), "Email and password does not match", Toast.LENGTH_SHORT).show();
                           }

                       });
               dialog.show();
           }
           else{
               Toast.makeText(getApplicationContext(),"please enter valid email and password",
                       Toast.LENGTH_LONG).show();
           }


            }

        });

        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
             finish();
            }
        });
    }
}
