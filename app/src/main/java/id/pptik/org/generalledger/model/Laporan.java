package id.pptik.org.generalledger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hafid on 9/27/2017.
 */

public class Laporan {
    @SerializedName("id_trx")
    private int id_trx;
    @SerializedName("id_pdg")
    private int id_pdg;
    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("barang")
    private List<Barang> barang;

    public int getId_trx() {
        return id_trx;
    }

    public void setId_trx(int id_trx) {
        this.id_trx = id_trx;
    }

    public int getId_pdg() {
        return id_pdg;
    }

    public void setId_pdg(int id_pdg) {
        this.id_pdg = id_pdg;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public List<Barang> getBarang() {
        return barang;
    }

    public void setBarang(List<Barang> barang) {
        this.barang = barang;
    }
}
