package com.walkiperu.www.walki;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import entidades.User;
import entidades.Usuario;
import firebase.connection.FirebaseConn;
import servicios.ActivityLoginService;

import static firebase.connection.FirebaseConn.firebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_phone;
    private EditText et_name;
    private EditText et_lastName;
    private EditText et_email;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String user;
    private String userPass;

    private User userEntity;
    private Uri filePatch;
    private ImageView imgFotoUser;
    private static final int GALLERY_INTENT = 1;
    private final  int PICK_REQUES_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        et_phone = (EditText)findViewById(R.id.et_phone_reg);
        et_name = (EditText)findViewById(R.id.et_name_reg);
        et_lastName = (EditText)findViewById(R.id.et_lastname_reg);
        et_email = (EditText)findViewById(R.id.et_email_reg);
        imgFotoUser = (ImageView)this.findViewById(R.id.img_photo_reg);


        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        user = (String)getIntent().getSerializableExtra("usuario");
        userPass =  (String)getIntent().getSerializableExtra("pass");



    }

    @Override
    protected void onStart() {
        super.onStart();

        et_phone.setText(userPass);
        et_phone.setEnabled(false);
    }


    public void register(View view){

        if(et_name.getText().toString().isEmpty() || et_lastName.getText().toString().isEmpty()
                || et_email.getText().toString().isEmpty() || filePatch == null){
            if(filePatch == null){
                Toast.makeText(this, "Porfavor seleccione una foto ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(RegisterActivity.this, "Porfavor ingrese datos completos", Toast.LENGTH_SHORT).show();
            }

        } else{

            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Registrando...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(user,userPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        create(progressDialog);
                    }else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }


    }




    public void create(final ProgressDialog progressDialog){

        storageReference = firebaseStorage.getReference();

        final StorageReference  storageReference1 = storageReference.child("foto_users").child(firebaseAuth.getUid());



        storageReference1.putFile(filePatch).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if(!task.isSuccessful()){

                    throw new Exception();

                }

                return storageReference1.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                     @Override
             public void onComplete(@NonNull Task<Uri> task) {

                 if(task.isSuccessful()){

                     //SUBIR DATOS E IMAGEN DEL USUARIO

                     Uri downloadLink = task.getResult();

                     SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                     String now = ISO_8601_FORMAT.format(new Date());


                     userEntity = new User();

                     userEntity.setPhone(userPass);
                     userEntity.setName(et_name.getText().toString());
                     userEntity.setLastName(et_lastName.getText().toString());
                     userEntity.setEmail(et_email.getText().toString());
                     userEntity.setUid(firebaseAuth.getUid());
                     userEntity.setDateReg(now);
                     userEntity.setUrlPhoto(downloadLink.toString());

                    firebaseFirestore.collection("users").add(userEntity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                lanzarActivityUserMain();
                                progressDialog.dismiss();
                            }
                        }
                    });

                 }



             }




        });

    }




    public void lanzarActivityUserMain(){
        Intent lanzador =  new Intent(this,UserMain.class);
        startActivity(lanzador);
        finish();
    }



    public void upPhoto(View view){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Seleccione una imagen para su perfil"),PICK_REQUES_CODE);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_REQUES_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){

            filePatch = data.getData();

            Bitmap bitmapImage;

            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePatch);
                imgFotoUser.setImageBitmap(bitmapImage);
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }
}
