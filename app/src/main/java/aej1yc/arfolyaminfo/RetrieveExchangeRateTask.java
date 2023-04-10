package aej1yc.arfolyaminfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RetrieveExchangeRateTask extends AsyncTask<Void, Void, Double> {
    private WeakReference<Activity> activity;
    private WeakReference<Context> ctx;
    private WeakReference<TextView> resultTV;
    private double fromAmount;
    private String fromCurrency;
    private String toCurrency;
    ProgressDialog pd;
    private Exception error;

    public RetrieveExchangeRateTask(
            Activity activity,
            Context ctx,
            TextView resultTV,
            double fromAmount,
            String fromCurrency,
            String toCurrency) {
        this.activity = new WeakReference<>(activity);
        this.ctx = new WeakReference<>(ctx);
        this.resultTV = new WeakReference<>(resultTV);
        this.fromAmount = fromAmount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    protected void onPreExecute() {
        pd = ProgressDialog.show(
                this.activity.get(), "", this.ctx.get().getString(R.string.retrieving_exchange_rate), false);
    }

    protected Double doInBackground(Void... params) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("https://api.exchangerate.host/convert?from=%s&to=%s", this.fromCurrency,
                            this.toCurrency))
                    .build();
            String body;
            try (Response response = client.newCall(request).execute()) {
                body = response.body().string();
            }
            Gson gson = new Gson();
            Map<String, Object> data = gson.fromJson(body, Map.class);

            return (double) data.get("result");
        } catch (IOException | NullPointerException e) {
            this.error = e;
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(Double exchangeRate) {
        pd.dismiss();

        if (exchangeRate != null) {
            String fromAmountStr = new DecimalFormat("@@@").format(this.fromAmount);

            double toAmount = this.fromAmount * exchangeRate;
            String toAmountStr = new DecimalFormat("@@@").format(toAmount);

            this.resultTV.get().setText(String.format(
                    "%s %s = %s %s",
                    fromAmountStr, this.fromCurrency, toAmountStr, this.toCurrency));
        } else {
            this.error.printStackTrace();
            new AlertDialog.Builder(this.ctx.get())
                    .setTitle(this.ctx.get().getString(R.string.error))
                    .setMessage(this.ctx.get().getString(R.string.error_getting_exchange_rate))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
