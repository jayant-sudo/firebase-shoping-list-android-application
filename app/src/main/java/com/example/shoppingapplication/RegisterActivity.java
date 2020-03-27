package com.example.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
  private EditText email,password;
  private Button register;
  private TextView alreadyAccount;
  private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=(Button)findViewById(R.id.registerButton);
        alreadyAccount=findViewById(R.id.alreadyAccount);



        auth=FirebaseAuth.getInstance();

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
               finish();

            }
        });
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);


                final  AlertDialog dialog=builder.create();

                String em=email.getText().toString().trim();
                String pass=password.getText().toString().trim();

                if(!TextUtils.isEmpty(em) && !TextUtils.isEmpty(pass))
                {
                    builder.setMessage("Loading....");
                    auth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(
                            RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                              if(task.isSuccessful())
                              {
                                  dialog.dismiss();
                                  Toast.makeText(getApplicationContext(), "Register Successfully..", Toast.LENGTH_SHORT).show();

                                  startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                  finish();
                              }

                                }
                            }
                    ).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Email and password already exists or may be not valid!!...please try again!!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Please fill all the fields..",Toast.LENGTH_LONG)
                            .show();
                }
                dialog.show();

            }

        });

    }
}
