package id.pptik.org.generalledger.tools;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import id.pptik.org.generalledger.config.Constants;

/**
 * Created by Hafid on 9/26/2017.
 */

public class GerenalLedgerRestAPI {
    public static String TAG = "[GeneralLedgerRestAPI]";

    private static AsyncHttpClient client;
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client = new AsyncHttpClient();
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, String token, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client = new AsyncHttpClient();
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client = new AsyncHttpClient();
        client.post(getAbsoluteUrl(url),params, responseHandler);
    }

    public static void post(String url, String token, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client = new AsyncHttpClient();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Constants.BASE_URL_REST_API + relativeUrl;
    }
}
