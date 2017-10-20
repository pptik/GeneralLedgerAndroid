package id.pptik.org.generalledger;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import id.pptik.org.generalledger.config.Constants;
import id.pptik.org.generalledger.tools.GerenalLedgerRestAPI;

public class SignUpActivity extends AppCompatActivity {
    public static String TAG = "[SignUpActivity]";

    @BindView(R.id.namaPedagang)
    EditText namaPedagang;
    @BindView(R.id.noHp)
    EditText noHp;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.rePassword)
    EditText retypePassword;
    @BindView(R.id.alamatKios)
    EditText alamatKios;
    @BindView(R.id.jenisKelamin)
    Spinner jenisKelamin;
    @BindView(R.id.submitInfo)
    Button submitData;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.jenis_kelamin));
        jenisKelamin.setAdapter(adapter);
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        RequestParams params = new RequestParams();
        String nama = namaPedagang.getText().toString();
        String nomor_hp = noHp.getText().toString();
        String password_user = password.getText().toString();
        String retype_password_user = retypePassword.getText().toString();
        String alamat_kios = alamatKios.getText().toString();
        String jenis_kelamin = jenisKelamin.getSelectedItem().toString();

        if (validate(nomor_hp, password_user, retype_password_user)) {
            params.put("noHp", nomor_hp);
            params.put("password", password_user);
            params.put("nama", nama);
            params.put("jenisKelamin", jenis_kelamin);
            params.put("alamatKios", alamat_kios);
            GerenalLedgerRestAPI.post(Constants.REST_REGISTER_USER, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            finish();
                        } else {
                            String message = response.getString("message");
                            Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Snackbar.make(mainLayout, "Jaringan sedang bermasalah!", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean validate(String nomor_hp, String password, String repassword) {
        if (nomor_hp.equals(null) || nomor_hp.equals("")) {
            Snackbar.make(mainLayout, "Mohon dilengkapi form Nomor Hp!", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(repassword)) {
            Snackbar.make(mainLayout, "Pastikan password kembali!", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
