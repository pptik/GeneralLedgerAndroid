package id.pptik.org.generalledger.sendandreceiveparam;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hafid on 10/17/2017.
 */

public class FetchLaporan {
    @SerializedName("idPengguna")
    private String idPengguna;
    @SerializedName("tanggal")
    private String tanggal;

    public FetchLaporan() {
    }

    public String getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(String idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
