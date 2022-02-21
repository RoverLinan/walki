package firebase.connection;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseConn  {

    public static FirebaseAuth firebaseAuth;


    public static void iniciarConn(){

        firebaseAuth = FirebaseAuth.getInstance();;
    }


    public static void logOutFirebase(){

        firebaseAuth.signOut();
    }



}
