package aej1yc.arfolyaminfo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String NOTIFICATION_CHANNEL_ID = "arfolyaminfo";
    private static final String NOTIFICATION_CHANNEL_NAME = "ÁrfolyamInfo";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
    }

    public void register(View view) {
        EditText emailET = findViewById(R.id.email);
        String email = emailET.getText().toString();

        EditText passwordET = findViewById(R.id.password);
        String password = passwordET.getText().toString();

        EditText passwordConfirmET = findViewById(R.id.passwordConfirm);
        String passwordConfirm = passwordConfirmET.getText().toString();

        if (email.isEmpty()) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_no_email_provided))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_email_invalid))
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

        if (password.length() < 8) {
            new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_password_too_short))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_passwords_do_not_match))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, getString(R.string.signup_successful), Toast.LENGTH_SHORT).show();

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.welcome_notification_title))
                        .setContentText(getString(R.string.welcome_notification_body))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                Notification notification = builder.build();
                manager.notify(1, notification);

                startActivity(new Intent(this, ConvertActivity.class));
                this.finish();
            } else {
                new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                        .setTitle(getString(R.string.error))
                        .setMessage(String.format(getString(R.string.error_signup_failed),
                                        task.getException().getMessage()))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}