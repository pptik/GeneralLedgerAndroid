package id.pptik.org.generalledger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import id.pptik.org.generalledger.adapter.BarangAdapter;
import id.pptik.org.generalledger.config.Constants;
import id.pptik.org.generalledger.model.Barang;
import id.pptik.org.generalledger.sendandreceiveparam.InsertReport;
import id.pptik.org.generalledger.tools.AMQPClient;
import id.pptik.org.generalledger.tools.FTPClientCon;
import id.pptik.org.generalledger.tools.GerenalLedgerRestAPI;
import id.pptik.org.generalledger.tools.LocaleFormat;
import id.pptik.org.generalledger.tools.SessionManager;

public class ReportPhotoActivity extends AppCompatActivity {
    public static String TAG = "[ReportPhotoActivity]";

    @BindView(R.id.simpanGambar)
    Button saveInfo;
    @BindView(R.id.takePicture)
    ImageButton takePicture;
    @BindView(R.id.takenPic)
    ImageView takenPic;
    @BindView(R.id.recycler_view)
    RecyclerView daftarBarang;
    @BindView(R.id.price)
    EditText harga;
    @BindView(R.id.tambahkan)
    Button tambahkan;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    ProgressDialog dialog;
    ProgressDialog dialog2;

    private Uri imageUri;
    private Bitmap bitmap;
    private byte[] byteArr;
    private String filename;

    private BarangAdapter adapter;
    private Barang barang;
    private List<Barang> barangList = new ArrayList<>();
    private SessionManager sessionManager;
    private AMQPClient amqpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_photo);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        dialog2 = new ProgressDialog(this);
        sessionManager = new SessionManager(this, getApplicationContext());

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        tambahkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectAllItem();
            }
        });
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPhotosFirst();
            }
        });
        adapter = new BarangAdapter(getApplicationContext(), barangList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        daftarBarang.setLayoutManager(mLayoutManager);
        daftarBarang.setItemAnimator(new DefaultItemAnimator());
        daftarBarang.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //amqpConnection.execute(Constants.RMQ_INSERT_TRANSACTION_ROUTE);

        /*
        Handler incomingMessage = new Handler() {
            int message_counter = 0;
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, msg.getData().getString("result_response"));
                message_counter++;
                if (message_counter == barangList.size()){
                    barangList.clear();
                    adapter.notifyDataSetChanged();
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
        if (saveInfo.isEnabled()) {
            super.onBackPressed();
            finish();
        }
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

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String uniqueId = "" + System.currentTimeMillis();
        filename = sessionManager.getUserDetail().getId() + "_" + uniqueId + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), filename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, Constants.TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    getContentResolver().notifyChange(imageUri, null);
                    ContentResolver cr = getContentResolver();
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, imageUri);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, out);
                        byteArr = out.toByteArray();
                        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal memuat!", Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
                }
        }
    }

    public void addItem() {
        barangList.add(barang);
        adapter.notifyDataSetChanged();
        barang = null;
        bitmap = null;
    }

    private void collectData() {
        barang = new Barang();
        barang.setFlag(1);
        if (harga.getText().equals("")) {
            barang.setHarga(0);
        } else {
            barang.setHarga(Double.parseDouble(harga.getText().toString()));
        }
        barang.setNama(filename);
        barang.setByteArr(byteArr);
        barang.setPic(bitmap);
        barang.setInternalURI(imageUri.getPath());
        Log.i(TAG, "Uri " + imageUri.toString());
        barang.setFilephoto(new File(imageUri.getPath()));
        harga.setText("");
    }

    private void collectAllItem() {
        if (validate(bitmap, barang, harga.getText().toString())) {
            Snackbar.make(mainLayout, "Mohon untuk menginput item terlebih dahulu!", Snackbar.LENGTH_LONG).show();
        } else {
            collectData();
            addItem();
        }
    }

    private boolean validate(Bitmap bitmap, Barang barang, String price) {
        if ((bitmap == null && barang == null) || price.equals("")) {
            return true;
        }
        return false;
    }

    private void postPhotosFirst() {
        new AsyncTask<Integer, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Sedang mengupload...");
                dialog.show();
                saveInfo.setEnabled(false);
            }

            @Override
            protected Boolean doInBackground(Integer... params) {

                FTPClientCon ftpClientCon = new FTPClientCon();
                boolean connection = ftpClientCon.ftpConnect(Constants.FTP_BASE_ADDRESS, Constants.FTP_USER, Constants.FTP_PASSWORD, Constants.FTP_PORT);
                if (connection) {
                    for (int i = 0; i < barangList.size(); i++) {
                        boolean stats = false;
                        stats = ftpClientCon.ftpUpload(barangList.get((i)).getInternalURI(), barangList.get((i)).getNama());
                        if (stats) {
                            barangList.get(i).setUploadFlag(true);
                            Log.i(TAG, "upload file " + i + " berhasil");
                        } else {
                            Log.i(TAG, "upload file " + i + " gagal");
                        }
                    }
                    ftpClientCon.ftpDisconnect();
                }
                Log.i(TAG, "UPLOAD SELESAI");
                return connection;
            }

            protected void onPostExecute(Boolean result) {
                /*
                 * for (int i = 0; i < barangList.size(); i++) {
                 *    postReport(i);
                 * }
                 */
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (result) {
                    postCompleteReport();
                } else {
                    Snackbar.make(mainLayout, "Gagal mengupload file ke server!", Snackbar.LENGTH_SHORT).show();
                }
                saveInfo.setEnabled(true);
            }
        }.execute();
    }

    /*
     * Post report as a multi object
     */
    private void postCompleteReport() {
        RequestParams params = new RequestParams();
        Gson g = new Gson();
        ArrayList<InsertReport> allReport = new ArrayList<>();
        for (int i = 0; i < barangList.size(); i++) {
            InsertReport paramss = new InsertReport();
            paramss.setIdPengguna(sessionManager.getUserDetail().getId());
            paramss.setTanggal(LocaleFormat.dateNow());
            paramss.setFlag(barangList.get(i).getFlag());
            paramss.setHarga(barangList.get(i).getHarga());
            paramss.setNama(Constants.FTP_HTTP_FILEADDRESS + barangList.get(i).getNama());
            allReport.add(paramss);
        }
        String message = g.toJson(allReport);
        Log.i(TAG, message);
        params.put("arrayItem", message);
        dialog.setMessage("Sedang menyimpan data!");
        if (!dialog.isShowing()) dialog.show();
        GerenalLedgerRestAPI.post(Constants.REST_POST_REPORT_MULTIPLE, params, new JsonHttpResponseHandler() {
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
                    adapter.notifyDataSetChanged();
                    if (dialog.isShowing()) dialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Snackbar.make(mainLayout, "Jaringan bermasalah!", Snackbar.LENGTH_SHORT).show();
                if (dialog.isShowing()) dialog.dismiss();
            }
        });
    }

    /*
     * post report via RABBIT MQ
     */
    private void postReportRMQ(final int i) {
        try {
            Gson g = new Gson();
            InsertReport params = new InsertReport();
            params.setIdPengguna(sessionManager.getUserDetail().getId());
            params.setTanggal(LocaleFormat.dateNow());
            params.setFlag(barangList.get(i).getFlag());
            params.setNama(Constants.FTP_HTTP_FILEADDRESS + barangList.get(i).getNama());
            Log.i(TAG, barangList.get(i).getNama());
            params.setHarga(barangList.get(i).getHarga());
            String message = g.toJson(params, InsertReport.class);

            amqpClient.addMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * Post report as a single object
     */
    private void postReport(final int i) {
        RequestParams params = new RequestParams();
        params.put("idPengguna", sessionManager.getUserDetail().getId());
        params.put("tanggal", LocaleFormat.dateNow());
        params.put("flag", barangList.get(i).getFlag());
        params.put("nama", Constants.FTP_HTTP_FILEADDRESS + barangList.get(i).getNama());
        Log.i(TAG, barangList.get(i).getNama());
        params.put("harga", barangList.get(i).getHarga());
        if (barangList.get(i).isUploadFlag()) {
            GerenalLedgerRestAPI.post(Constants.REST_POST_REPORT, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG, response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            Snackbar.make(mainLayout, response.getString("message"), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mainLayout, response.getString("message"), Snackbar.LENGTH_SHORT).show();
                        }
                        barangList.get(i).setUploadDetail(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!dialog2.isShowing()) {
                        dialog2.setMessage("Sedang menyimpan data ke server!");
                        dialog2.show();
                    }

                    boolean trigger = true;
                    for (int i = 0; i < barangList.size(); i++) {
                        if (!barangList.get(i).isUploadDetail()) {
                            trigger = false;
                        }
                    }
                    if (trigger) {
                        Snackbar.make(mainLayout, "Data telah tersimpan di server...", Snackbar.LENGTH_LONG).show();
                        barangList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    if (dialog2.isShowing()) dialog2.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

            });
        }
    }

}