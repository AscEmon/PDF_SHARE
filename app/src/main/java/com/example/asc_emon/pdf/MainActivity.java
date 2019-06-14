package com.example.asc_emon.pdf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

Button SelectFile,Upload;
TextView textView;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Uri pdfUri;
    ProgressDialog progressDialog;

    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage=FirebaseStorage.getInstance();//an object create foe storage
        database=FirebaseDatabase.getInstance();//an object create for database


         SelectFile=findViewById(R.id.insert);
         Upload=findViewById(R.id.upload);
         textView=findViewById(R.id.notify);



         SelectFile.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                 {

                     selectPdf();
                 }
                 else
                 {
                     ActivityCompat.requestPermissions(MainActivity.this,new String []{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                 }

             }
         });
         Upload.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 if(pdfUri!=null)
                 {
                     UploadFile(pdfUri);
                 }
                 else
                 {


                     Toast.makeText(MainActivity.this," Select a file ....",Toast.LENGTH_LONG).show();
                 }
             }
         });


    }

    private void UploadFile(Uri pdfUri) {

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Uploading File ...");
        progressDialog.show();

       final  String fileName =System.currentTimeMillis()+"";
        StorageReference storageReference =storage.getReference();

        storageReference.child("Uploads").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url = uri.getResult();

                        Toast.makeText(MainActivity.this, "Upload Success," +
                                url.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                       
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Upload Error: " +
                        e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0] ==PackageManager.PERMISSION_GRANTED)
        {
            selectPdf();


        }
        else
        {
            Toast.makeText(MainActivity.this,"Please Provide Permission ....",Toast.LENGTH_LONG).show();
        }

    }

    private void selectPdf() {

        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            pdfUri=data.getData();//return the path of selected file
            textView.setText("A file is selected "+data.getData().getLastPathSegment());
        }
        else
        {
            Toast.makeText(MainActivity.this,"Please Select a file ....",Toast.LENGTH_LONG).show();

        }
    }
}