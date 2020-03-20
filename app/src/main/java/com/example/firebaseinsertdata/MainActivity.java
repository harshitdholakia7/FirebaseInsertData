package com.example.firebaseinsertdata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    @BindView(R.id.name)EditText name;
    @BindView(R.id.save)Button save;
    @BindView(R.id.text_name)TextView text_name;
    @BindView(R.id.text_password)TextView text_password;
    @BindView(R.id.download)Button download;
    @BindView(R.id.choose)Button choose;
    @BindView(R.id.showUpload)TextView showUpload;
    @BindView(R.id.imageView)ImageView imageView;

    private static final int REQUEST_CAMERA=0;
    private static final int SELECT_FILE=1;
    private static final int REQUEST_CODE_WRITE_STORAGE = 1;

    public String[]permission={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    Uri selectedImageUri;
    Data data;
    long maxid;
    DatabaseReference databaseReference;
    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        save.setOnClickListener(this);
        download.setOnClickListener(this);
        choose.setOnClickListener(this);
        showUpload.setOnClickListener(this);

        data=new Data();
        storageReference= FirebaseStorage.getInstance().getReference("Data");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Data");
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    maxid = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.save:
                if (uploadTask!=null && uploadTask.isInProgress())
                {
                    Toast.makeText(this, "Upload in Progess", Toast.LENGTH_LONG).show();
                }
                else
                {
                    FileUpload();
                }
//                String uname=name.getText().toString().trim();
//                data.setName(uname);
//                string="Users "+(maxid+1);
//                reference.child(string).setValue(data);
//                reference.push().setValue(data);


//                reference.child("users").child(uname).child("username").setValue(data);
//                Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
//
//                name.setText("");
//                name.requestFocus();
                break;

            case R.id.showUpload:
                startActivity(new Intent(MainActivity.this,ImagesActivity.class));
                break;
//            case R.id.download:
//                databaseReference=FirebaseDatabase.getInstance().getReference().child("Data").child("Users 1");
//                databaseReference.addValueEventListener(new ValueEventListener()
//                {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//                    {
//                        String name=dataSnapshot.child("name").getValue().toString();
//                        String password=dataSnapshot.child("password").getValue().toString();
//                        text_name.setText(name);
//                        text_password.setText(password);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//                break;

            case R.id.choose:
                getpermission();
                break;
        }
    }

    private void FileUpload()
    {
        String imgid=System.currentTimeMillis()+"."+getExtension(selectedImageUri);
        StorageReference Ref=storageReference.child(imgid);
        uploadTask=Ref.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        data.setName(name.getText().toString().trim());
                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                data.setImageid(uri.toString());
                                String uploadId=databaseReference.push().getKey();
                                databaseReference.child(uploadId).setValue(data);
                                Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void getpermission()
    {
        int hasWriteStoragePermission = 0;

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permission,REQUEST_CODE_WRITE_STORAGE);
            }
        }
        else
        {
            SelectImage();
        }
    }
    private void SelectImage()
    {
        final CharSequence[] items={"Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);

                }
               else if (items[i].equals("Cancel"))
               {
                    dialogInterface.dismiss();
               }
            }
        });
        builder.show();

    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);

        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if(resultCode== Activity.RESULT_OK)
                {
                    Bundle bundle = data.getExtras();
                    final Bitmap bmp = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bmp);
                }
                break;
            case SELECT_FILE:
                if(resultCode== Activity.RESULT_OK)
                {
                    selectedImageUri = data.getData();
                    Picasso.with(this).load(selectedImageUri).into(imageView);
//                    imageView.setImageURI(selectedImageUri);
                }
                break;
        }
    }
}
