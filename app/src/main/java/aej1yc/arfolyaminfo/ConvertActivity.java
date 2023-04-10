package aej1yc.arfolyaminfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConvertActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        Spinner fromCurrencySP = findViewById(R.id.fromCurrency);
        fromCurrencySP.setSelection(((ArrayAdapter) fromCurrencySP.getAdapter()).getPosition("EUR"));

        Spinner toCurrencySP = findViewById(R.id.toCurrency);
        toCurrencySP.setSelection(((ArrayAdapter) toCurrencySP.getAdapter()).getPosition("HUF"));

        CollectionReference collRef = mFirestore.collection("currency_pair");
        DocumentReference docRef = collRef.document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    CurrencyPair data = new CurrencyPair(doc.getData());
                    fromCurrencySP.setSelection(((ArrayAdapter) fromCurrencySP.getAdapter()).getPosition(
                            data.getFromCurrency()));
                    toCurrencySP.setSelection(((ArrayAdapter) toCurrencySP.getAdapter()).getPosition(
                            data.getToCurrency()));
                }
            } else {
                task.getException().printStackTrace();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                CollectionReference coll = mFirestore.collection("currency_pair");
                DocumentReference doc = coll.document(mAuth.getCurrentUser().getUid());
                doc.delete();
                Toast.makeText(this, getString(R.string.reset_success), Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                this.finish();
                return true;
            case R.id.logout:
                mAuth.signOut();
                Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void exchange(View view) {
        EditText fromAmountET = findViewById(R.id.fromAmount);
        String fromAmountStr = fromAmountET.getText().toString();
        if (fromAmountStr.isEmpty()) {
            new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_no_amount_specified))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }
        double fromAmount;
        try {
            fromAmount = Double.parseDouble(fromAmountStr);
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(view.getContext(), R.style.animated_dialog)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_invalid_number))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        Spinner fromCurrencySP = findViewById(R.id.fromCurrency);
        String fromCurrency = fromCurrencySP.getSelectedItem().toString();

        Spinner toCurrencySP = findViewById(R.id.toCurrency);
        String toCurrency = toCurrencySP.getSelectedItem().toString();

        TextView resultTV = findViewById(R.id.result);

        CollectionReference coll = mFirestore.collection("currency_pair");
        DocumentReference doc = coll.document(mAuth.getCurrentUser().getUid());
        doc.set(new CurrencyPair(fromCurrency, toCurrency));

        new RetrieveExchangeRateTask(
                this, view.getContext(), resultTV, fromAmount, fromCurrency, toCurrency).execute();
    }
}