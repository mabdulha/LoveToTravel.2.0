package ie.com.lovetotravel20.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import ie.com.lovetotravel20.R;
import ie.com.lovetotravel20.authentication.Login;


public class Base extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public DatabaseReference mDatabaseRef;

    //https://stackoverflow.com/questions/18977187/how-to-hide-soft-keyboard-when-activity-starts
    //This is the reference for this code to hide the keyboard

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}