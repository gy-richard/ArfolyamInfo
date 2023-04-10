package aej1yc.arfolyaminfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        EditText emailET = findViewById(R.id.email);
        String email = emailET.getText().toString();

        EditText passwordET = findViewById(R.id.password);
        String password = passwordET.getText().toString();

        if (email.isEmpty()) {
            new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_no_email_provided))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if (password.isEmpty()) {
            new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_no_password_provided))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ConvertActivity.class));
                this.finish();
            } else {
                new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                        .setTitle(getString(R.string.error))
                        .setMessage(String.format(getString(R.string.error_login_failed),
                                        task.getException().getMessage()))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}