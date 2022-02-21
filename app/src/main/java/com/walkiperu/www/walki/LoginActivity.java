package com.walkiperu.www.walki;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import entidades.Usuario;
import firebase.connection.FirebaseConn;
import servicios.ActivityLoginService;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    //VISTAS
    private EditText  et_phone;
    private Button b_confirm_phon;

    private EditText et_code_vali;
    private TextView tw_line_vali;

    private Button b_continu;
    private Button b_tuto;

    //atributos
    private String userString;
    private String userPassString;
    private String phone;
    private String codeValidacion;
    private ProgressDialog progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        permissions();
        et_phone = (EditText)findViewById(R.id.et_phone);
        et_code_vali = (EditText)findViewById(R.id.et_code_vali);
        tw_line_vali = (TextView)findViewById(R.id.tw_code_vali);



    }






    public void sendSMSValidation(View view){

        String mensaje = "Bienvenido a Walki, tu codigo de activacion es: ";

        try {

            phone = et_phone.getText().toString();


            if(phone.length() > 7 && phone.length()<12){

                SmsManager sms = SmsManager.getDefault();
                codeValidacion = String.valueOf(generarCodigoValidacion());
                sms.sendTextMessage(phone,"Walki"  ,mensaje+codeValidacion ,null,null);
                et_phone.setEnabled(false);
                et_code_vali.setVisibility(View.VISIBLE);
                tw_line_vali.setVisibility(View.VISIBLE);


            }else{

                Toast.makeText(this, "Numero telefonico incorrecto", Toast.LENGTH_SHORT).show();
                et_phone.setEnabled(true);
            }

        }catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    public void checkNumPhone(View view){

        if(et_code_vali.getText().toString().isEmpty()){
            Toast.makeText(this, "Errror: Codigo de validacion incorrecto.", Toast.LENGTH_SHORT).show();
            et_phone.setEnabled(true);
        }else if(et_code_vali.getText().toString().equals(codeValidacion)) {

            progressBar = new ProgressDialog(this);
            progressBar.setMessage("Iniciando sesión...");
            progressBar.setCancelable(false);
            progressBar.show();



            userString = phone+"@walki.pe";
            userPassString = phone;

            singIn();

        }


    }

    public  void register(){

        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Iniciando sesión...");
        progressBar.show();



        userString = phone+"@walki.pe";
        userPassString = phone;

        singIn();


            FirebaseConn.firebaseAuth.createUserWithEmailAndPassword(userString, userPassString).addOnCompleteListener(
                    this, new OnCompleteListener<AuthResult>() {

                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                progressBar.dismiss();
                                lanzarActivityRegistro();

                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                    singIn();
                                } else {

                                    System.out.println(task.getException().getMessage());
                                }
                            }


                        }
                    });


    }




    public void singIn(){


        FirebaseConn.firebaseAuth.signInWithEmailAndPassword(userString,userPassString).addOnCompleteListener(
                LoginActivity.this,new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.dismiss();

                        if(task.isSuccessful()){
                            lanzarActivityUserMain();
                        }else {
                            lanzarActivityRegistro();
                        }
                    }
                });



    }


    public void lanzarActivityRegistro(){

        Intent lanzador =  new Intent(this,RegisterActivity.class);
        lanzador.putExtra("usuario",userString);
        lanzador.putExtra("pass",userPassString);
        startActivity(lanzador);
        finish();

    }

    public void lanzarActivityUserMain(){
        Intent lanzador =  new Intent(this,UserMain.class);
        startActivity(lanzador);
        finish();
    }



    private int generarCodigoValidacion(){
        Random codigo = new Random();
        return codigo.nextInt(99999);
    }


    public void permissions(){

        // SMS

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{

                    Manifest.permission.SEND_SMS
            },1000);

        }


    }
    
    

}

