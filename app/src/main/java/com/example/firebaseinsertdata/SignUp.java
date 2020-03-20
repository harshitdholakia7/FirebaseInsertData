package com.example.firebaseinsertdata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.Semail_id)EditText Semail_id;
    @BindView(R.id.Susername)EditText Susername;
    @BindView(R.id.Spassword)EditText Spassword;
    @BindView(R.id.signup)Button signup;
    @BindView(R.id.signin)Button signin;
    DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        users= FirebaseDatabase.getInstance().getReference("Users");
        signup.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.signup:
                String strUsername=Susername.getText().toString();
                String strPassword=Spassword.getText().toString();
                String strEmaile=Semail_id.getText().toString();

//                final User user=new User(strUsername,strPassword,strEmaile);
//                users.addListenerForSingleValueEvent(new ValueEventListener()
//                {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//                    {
//                        if (dataSnapshot.child(user.getUsername()).exists())
//                        {
//                            Toast.makeText(SignUp.this, "The Username is already exists", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            users.child(user.getUsername()).setValue(user);
//                            Toast.makeText(SignUp.this, "Success Register", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError)
//                    {
//
//                    }
//                });

                break;

            case R.id.signin:
                startActivity(new Intent(getApplicationContext(),SignIn.class));
                break;
        }
    }
}
