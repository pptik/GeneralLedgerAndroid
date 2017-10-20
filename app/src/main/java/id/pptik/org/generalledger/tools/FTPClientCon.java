package id.pptik.org.generalledger.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;

import id.pptik.org.generalledger.config.Constants;

/**
 * Created by Hafid on 10/5/2017.
 */

public class FTPClientCon {
    public static final String TAG = "[FTPClient]";
    private FTPClient mFTPClient = null;

    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            mFTPClient = new FTPClient();
            mFTPClient.connect(InetAddress.getByName(host), port);
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                boolean status = mFTPClient.login(username, password);
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                Log.i(TAG, "Koneksi FTP Sukses" + status);
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error: could not connect to host " + host);
        }
        return false;
    }

    public boolean ftpDisconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    public boolean ftpUpload(String srcFilePath, String desFileName) {
        boolean status = false;
        try {
            Bitmap b = null;

            File f = new File(srcFilePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > Constants.MAX_IMAGE_SIZE || o.outWidth > Constants.MAX_IMAGE_SIZE)
            {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(Constants.MAX_IMAGE_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Log.i(TAG, "Resized");

            FileInputStream srcFileStream = new FileInputStream(f);
            b = BitmapFactory.decodeStream(srcFileStream, null, o2);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            bos.close();

            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
            status = mFTPClient.storeFile("GENERALLEDGER/" + desFileName, bs);
            Log.i(TAG, "ReplyString " + mFTPClient.getReplyString());
            Log.i(TAG, "ReplyCode" + mFTPClient.getReplyCode());
            bs.close();

            srcFileStream.close();

            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}
