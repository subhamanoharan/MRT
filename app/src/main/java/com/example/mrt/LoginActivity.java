package com.example.mrt;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.example.mrt.utilities.LoginCallback;
import com.example.mrt.utilities.LoginManager;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements LoginCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLogin(View view) {
        EditText userNameView = findViewById(R.id.username);
        EditText passwordView = findViewById(R.id.password);

        String username = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        new LoginManager().login(username, password, this);
    }

    @Override
    public void onLoginSuccess(JSONObject response) {
        Toast.makeText(this, "SUCCESS", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginFailure(NetworkResponse networkResponse) {
        Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
    }
}
