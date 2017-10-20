package id.pptik.org.generalledger.sendandreceiveparam;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hafid on 10/13/2017.
 */

public class InsertReport {
    @SerializedName("idPengguna")
    private String idPengguna;
    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("flag")
    private int flag;
    @SerializedName("nama")
    private String nama;
    @SerializedName("harga")
    private double harga;

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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }
}
