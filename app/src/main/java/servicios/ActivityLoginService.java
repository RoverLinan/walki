package servicios;

import android.text.TextUtils;
import android.widget.EditText;

public class ActivityLoginService {



    public ActivityLoginService(){

    }


    public boolean validarName(EditText e){


        if(TextUtils.isEmpty(e.getText().toString())){
            return false;
        }

        return true;
    }

    public boolean validarLastName(EditText e){

        if(TextUtils.isEmpty(e.getText().toString())){
            return false;
        }

        return true;

    }

    public boolean validarEmail(EditText e){

        if(TextUtils.isEmpty(e.getText().toString())){
            return false;
        }

        return true;

    }





}
