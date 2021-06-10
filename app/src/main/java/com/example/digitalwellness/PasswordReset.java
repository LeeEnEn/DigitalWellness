package com.example.digitalwellness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordReset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        FirebaseHelper firebase = new FirebaseHelper();

        EditText resetPass = (EditText) findViewById(R.id.reset_password);
        Button submit = (Button) findViewById(R.id.enter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.resetPassword(resetPass.getText().toString(), getApplicationContext());
                Intent intent = new Intent(PasswordReset.this, StartMenu.class);
                startActivity(intent);
            }
        });
    }
}