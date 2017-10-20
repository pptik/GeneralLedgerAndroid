package id.pptik.org.generalledger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pptik.org.generalledger.tools.SessionManager;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "[DashboardActivity]";

    @BindView(R.id.laporanA)
    Button laporanTertulis;
    @BindView(R.id.laporanB)
    Button laporanFoto;
    @BindView(R.id.lihatLaporan)
    Button lihatLaporan;
    @BindView(R.id.logout)
    Button logout;

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this, getApplicationContext());
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                sessionManager.logout();
            }
        });
        laporanFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ReportPhotoActivity.class);
                startActivity(intent);
            }
        });
        lihatLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LihatLaporanActivity.class);
                startActivity(intent);
            }
        });
        laporanTertulis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ReportStringActivity.class);
                startActivity(intent);
            }
        });
    }
}
