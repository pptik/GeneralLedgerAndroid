package id.pptik.org.generalledger.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hafid on 9/27/2017.
 */

public class User {
    public static final String TAG = "[User]";

    @SerializedName("_id")
    private String id;
    @SerializedName("nama")
    private String nama_pedagang;
    @SerializedName("noHp")
    private String nohp;
    @SerializedName("password")
    private String password;
    @SerializedName("alamatKios")
    private String alamat_kios;
    @SerializedName("jenisKelamin")
    private String jenis_kelamin;
    @SerializedName("joinDate")
    private String tanggalDaftar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_pedagang() {
        return nama_pedagang;
    }

    public void setNama_pedagang(String nama_pedagang) {
        this.nama_pedagang = nama_pedagang;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlamat_kios() {
        return alamat_kios;
    }

    public void setAlamat_kios(String alamat_kios) {
        this.alamat_kios = alamat_kios;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(String tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }
}
