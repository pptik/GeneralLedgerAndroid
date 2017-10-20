package id.pptik.org.generalledger;

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
import android.widget.EditText;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import id.pptik.org.generalledger.adapter.BarangStrAdapter;
import id.pptik.org.generalledger.config.Constants;
import id.pptik.org.generalledger.model.Barang;
import id.pptik.org.generalledger.model.LaporanItem;
import id.pptik.org.generalledger.model.User;
import id.pptik.org.generalledger.sendandreceiveparam.InsertReport;
import id.pptik.org.generalledger.tools.AMQPClient;
import id.pptik.org.generalledger.tools.GerenalLedgerRestAPI;
import id.pptik.org.generalledger.tools.LocaleFormat;
import id.pptik.org.generalledger.tools.SessionManager;

public class ReportStringActivity extends AppCompatActivity {
    public static final String TAG = "[ReportStringActivity]";

    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.namaBarang)
    EditText namaBarang;
    @BindView(R.id.price)
    EditText hargaBarang;
    @BindView(R.id.tambahkan)
    Button btnTambahkan;
    @BindView(R.id.recycler_view)
    RecyclerView daftarBarang;
    @BindView(R.id.simpanGambar)
    Button simpanGambar;
    ProgressDialog progressDialog;

    private Barang barang;
    private List<Barang> barangList = new ArrayList<>();
    private BarangStrAdapter barangStrAdapter;
    private SessionManager sessionManager;
    private AMQPClient amqpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_string);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(this, getApplicationContext());
        barangStrAdapter = new BarangStrAdapter(getApplicationContext(), barangList);
        btnTambahkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                klikTambahkan();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        daftarBarang.setLayoutManager(mLayoutManager);
        daftarBarang.setItemAnimator(new DefaultItemAnimator());
        daftarBarang.setAdapter(barangStrAdapter);
        barangStrAdapter.notifyDataSetChanged();
        simpanGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCompleteReport();
            }
        });

        /**
         *

        Handler incomingMessage = new Handler() {
            int message_counter = 0;

            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, msg.getData().getString("result_response"));
                message_counter++;
                if (message_counter == barangList.size()) {
                    barangList.clear();
                    barangStrAdapter.notifyDataSetChanged();
                    message_counter = 0;
                    Snackbar.make(mainLayout, "Data telah tersimpan di server...", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        amqpClient = new AMQPClient(sessionManager.getUserDetail());
        try {
            amqpClient.subscribe(Constants.RMQ_INSERT_TRANSACTION_ROUTE, incomingMessage);
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
        //amqpConnection.cancel(true);
    }

    private void collectData() {
        barang = new Barang();
        barang.setNama(namaBarang.getText().toString());
        barang.setFlag(0);
        barang.setHarga(Double.parseDouble(hargaBarang.getText().toString()));
    }

    private void tambahkanBarang() {
        barangList.add(barang);
        barang = null;
        namaBarang.setText("");
        hargaBarang.setText("");
        barangStrAdapter.notifyDataSetChanged();
    }

    private boolean validateBarang(String nama_barang, String harga_barang) {
        if (nama_barang.equals("") || harga_barang.equals("")) {
            return true;
        }
        return false;
    }

    private void klikTambahkan() {
        if (!validateBarang(namaBarang.getText().toString(), hargaBarang.getText().toString())) {
            collectData();
            tambahkanBarang();
        } else {
            Snackbar.make(mainLayout, "Mohon untuk menginput item terlebih dahulu!", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * post REPORT using REST API in Array form
     */
    private void postCompleteReport(){
        RequestParams params = new RequestParams();
        Gson g = new Gson();
        ArrayList<InsertReport> allReport = new ArrayList<>();
        for (int i = 0; i < barangList.size(); i++){
            InsertReport paramss = new InsertReport();
            paramss.setIdPengguna(sessionManager.getUserDetail().getId());
            paramss.setTanggal(LocaleFormat.dateNow());
            paramss.setFlag(barangList.get(i).getFlag());
            paramss.setNama(barangList.get(i).getNama());
            paramss.setHarga(barangList.get(i).getHarga());
            allReport.add(paramss);
        }
        String message = g.toJson(allReport);
        Log.i(TAG, message);
        params.put("arrayItem", message);
        progressDialog.setMessage("Sedang menyimpan data!");
        if (!progressDialog.isShowing()) progressDialog.show();
        GerenalLedgerRestAPI.post(Constants.REST_POST_REPORT_MULTIPLE,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                try {
                    if (response.getBoolean("success")) {
                        Snackbar.make(mainLayout, response.getString("message"), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(mainLayout, response.getString("message"), Snackbar.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    barangList.clear();
                    barangStrAdapter.notifyDataSetChanged();
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Snackbar.make(mainLayout, "Jaringan bermasalah!", Snackbar.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    /*
     * post Report using RABBITMQ
     */
    private void postReportRMQ() {
        for (int i = 0; i < barangList.size(); i++) {
            try {
                InsertReport paramss = new InsertReport();
                Gson g = new Gson();
                paramss.setHarga(barangList.get(i).getHarga());
                paramss.setIdPengguna(sessionManager.getUserDetail().getId());
                paramss.setNama(barangList.get(i).getNama());
                paramss.setFlag(barangList.get(i).getFlag());
                paramss.setTanggal(LocaleFormat.dateNow());
                String message = g.toJson(paramss, InsertReport.class);
                amqpClient.addMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Post REPORT using REST API as a single object
     */
    private void postReportREST() {
        for (int i = 0; i < barangList.size(); i++) {
            RequestParams params = new RequestParams();
            params.put("idPengguna", sessionManager.getUserDetail().getId());
            params.put("tanggal", LocaleFormat.dateNow());
            params.put("flag", barangList.get(i).getFlag());
            params.put("nama", barangList.get(i).getNama());
            params.put("harga", barangList.get(i).getHarga());
            GerenalLedgerRestAPI.post(Constants.REST_POST_REPORT, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Snackbar.make(mainLayout, response.getString("message"), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mainLayout, response.getString("message"), Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }
            });
        }
    }
}
