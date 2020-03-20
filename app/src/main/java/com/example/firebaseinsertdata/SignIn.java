package com.example.firebaseinsertdata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.username)EditText username;
    @BindView(R.id.password)EditText password;
    @BindView(R.id.signin)Button signin;
    @BindView(R.id.signup)Button signup;

    DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        users=FirebaseDatabase.getInstance().getReference("Users");
        signin.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.signin:
                signIn(username.getText().toString(),password.getText().toString());
                break;

            case R.id.signup:
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                break;
        }
    }

    private void signIn(final String username, final String password)
    {
        users.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                 if (dataSnapshot.child(username).exists())
                 {
                     User login=dataSnapshot.child(username).getValue(User.class);
                     if (login.getPassword().equals(password))
                     {
                         Toast.makeText(SignIn.this, "Success Login", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(getApplicationContext(),MainActivity.class));
                     }
                     else
                     {
                         Toast.makeText(SignIn.this, "Password is Wrong", Toast.LENGTH_SHORT).show();
                     }
                 }
                 else
                 {
                     Log.e("## UserName",username);
                     Toast.makeText(SignIn.this, "User not Register", Toast.LENGTH_SHORT).show();
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
