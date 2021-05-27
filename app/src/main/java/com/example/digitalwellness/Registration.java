package com.example.digitalwellness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseHelper firebase = new FirebaseHelper();
        //firebase.setRef("Registration");

        EditText editEmail = (EditText) findViewById(R.id.regEmail);
        EditText editPassword = (EditText) findViewById(R.id.regPassword);
        EditText firstName = (EditText) findViewById(R.id.regFirstName);
        EditText lastName = (EditText) findViewById(R.id.regLastName);
        Button regButton = findViewById(R.id.regButton);

        regButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = firstName.getText().toString() + " " + lastName.getText().toString();
                firebase.registerEmailAndPassword(editEmail.getText().toString(),
                        editPassword.getText().toString(), name, Registration.this);
            }
        });
    }
}