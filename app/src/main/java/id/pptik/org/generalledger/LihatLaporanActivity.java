package id.pptik.org.generalledger;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import id.pptik.org.generalledger.adapter.BarangAdapterFull;
import id.pptik.org.generalledger.adapter.LaporanItemAdapter;
import id.pptik.org.generalledger.config.Constants;
import id.pptik.org.generalledger.model.Barang;
import id.pptik.org.generalledger.model.LaporanItem;
import id.pptik.org.generalledger.model.User;
import id.pptik.org.generalledger.sendandreceiveparam.FetchLaporan;
import id.pptik.org.generalledger.tools.AMQPClient;
import id.pptik.org.generalledger.tools.GerenalLedgerRestAPI;
import id.pptik.org.generalledger.tools.LocaleFormat;
import id.pptik.org.generalledger.tools.SessionManager;

public class LihatLaporanActivity extends AppCompatActivity {
    public static String TAG = "[LihatLaporanActivity]";

    @BindView(R.id.recycler_view)
    RecyclerView daftarBarang;
    @BindView(R.id.selectDate)
    ImageButton btnSelectDate;
    @BindView(R.id.tanggalText)
    TextView tglText;
    @BindView(R.id.total)
    EditText total;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    ProgressDialog progressDialog;

    private LaporanItemAdapter barangAdapterFull;
    private List<LaporanItem> barangList = new ArrayList<>();
    private SessionManager sessionManager;
    private AMQPClient amqpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_laporan);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(this, getApplicationContext());
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(LihatLaporanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tglText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        prepareListBarang(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mcurrentTime.get(Calendar.YEAR), mcurrentTime.get(Calendar.MONTH), mcurrentTime.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle("Pilih tanggal pelayanan");
                datePickerDialog.show();
                tglText.setText(day + "/" + (month + 1) + "/" + year);
            }
        });

        barangAdapterFull = new LaporanItemAdapter(getApplicationContext(), barangList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        daftarBarang.setLayoutManager(mLayoutManager);
        daftarBarang.setItemAnimator(new DefaultItemAnimator());
        daftarBarang.setAdapter(barangAdapterFull);
        barangAdapterFull.notifyDataSetChanged();

        /*
        Handler incomingMessage = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, msg.getData().getString("result_response"));
                try {
                    barangList.clear();
                    barangAdapterFull.notifyDataSetChanged();
                    Gson g = new Gson();
                    String message = msg.getData().getString("result_response");
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray laporanItemsJSON = jsonObject.getJSONArray("transactionRecap");
                        for (int i = 0; i < laporanItemsJSON.length(); i++) {
                            LaporanItem item = g.fromJson(laporanItemsJSON.get(i).toString(), LaporanItem.class);
                            barangList.add(item);
                        }
                        total.setText(LocaleFormat.doubleToRupiah(barangAdapterFull.sumPrice()));
                    }
                    barangAdapterFull.notifyDataSetChanged();
                    Snackbar.make(mainLayout, jsonObject.getString("message"), Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        };
        amqpClient = new AMQPClient(sessionManager.getUserDetail());
        try {
            amqpClient.subscribe(Constants.RMQ_GET_LAPORAN_TRANSACTION_ROUTE, incomingMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "OnResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "OnPause");
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "OnDestroy");
        super.onDestroy();
        /*
        try {
            amqpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * load list of transaction using RMQ
     */
    public void prepareListBarangRMQ(String tgl) {
        try {
            total.setText("0");
            RequestParams requestParams = new RequestParams();
            User user = sessionManager.getUserDetail();
            FetchLaporan params = new FetchLaporan();
            params.setIdPengguna(user.getId());
            params.setTanggal(tgl);
            Gson g = new Gson();
            String message = g.toJson(params, FetchLaporan.class);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            amqpClient.addMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * load list of transaction using REST API
     */
    public void prepareListBarang(String tgl) {
        barangList.clear();
        total.setText("0");
        RequestParams requestParams = new RequestParams();
        User user = sessionManager.getUserDetail();
        requestParams.put("idPengguna", user.getId());
        requestParams.put("tanggal", tgl);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        GerenalLedgerRestAPI.post(Constants.REST_GET_BARANG, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        Gson g = new Gson();
                        JSONArray laporanItemsJSON = response.getJSONArray("transactionRecap");
                        for (int i = 0; i < laporanItemsJSON.length(); i++) {
                            LaporanItem item = g.fromJson(laporanItemsJSON.get(i).toString(), LaporanItem.class);
                            barangList.add(item);
                        }
                        total.setText(LocaleFormat.doubleToRupiah(barangAdapterFull.sumPrice()));
                    } else {
                        Snackbar.make(mainLayout, response.getString("message"), Snackbar.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                barangAdapterFull.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                barangAdapterFull.notifyDataSetChanged();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }
}
