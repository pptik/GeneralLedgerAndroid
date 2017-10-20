package id.pptik.org.generalledger.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by Hafid on 9/27/2017.
 */

public class Barang {
    public static String TAG = "[Barang]";

    @SerializedName("flag")
    private int flag;
    @SerializedName("nama")
    private String nama;
    @SerializedName("harga")
    private double harga;
    private Bitmap pic;
    private byte[] byteArr;
    private File filephoto;
    private String internalURI;
    private boolean uploadFlag;
    private boolean uploadDetail;

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

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public byte[] getByteArr() {
        return byteArr;
    }

    public void setByteArr(byte[] byteArr) {
        this.byteArr = byteArr;
    }

    public File getFilephoto() {
        return filephoto;
    }

    public void setFilephoto(File filephoto) {
        this.filephoto = filephoto;
    }

    public boolean isUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(boolean uploadFlag) {
        this.uploadFlag = uploadFlag;
    }

    public String getInternalURI() {
        return internalURI;
    }

    public void setInternalURI(String internalURI) {
        this.internalURI = internalURI;
    }

    public boolean isUploadDetail() {
        return uploadDetail;
    }

    public void setUploadDetail(boolean uploadDetail) {
        this.uploadDetail = uploadDetail;
    }
}
