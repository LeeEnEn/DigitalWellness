package com.example.digitalwellness;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}