package com.example.digitalwellness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
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

        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.resetPassword(resetPass.getText().toString(), getApplicationContext());
                Intent intent = new Intent(PasswordReset.this, StartMenu.class);
                startActivity(intent);
            }
        });
    }

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            (new Handler()).postDelayed(this::finish, 500);

        }
        return super.onOptionsItemSelected(item);
    }
}