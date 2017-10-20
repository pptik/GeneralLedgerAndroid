package id.pptik.org.generalledger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pptik.org.generalledger.R;
import id.pptik.org.generalledger.model.Barang;
import id.pptik.org.generalledger.tools.LocaleFormat;

/**
 * Created by Hafid on 9/28/2017.
 */

public class BarangStrAdapter extends RecyclerView.Adapter<BarangStrAdapter.MyViewHolder>{
    public static String TAG = "[PerawatAdapter]";

    private List<Barang> barangList;
    private Context context;

    public BarangStrAdapter(Context context, List<Barang> barangList){
        this.context = context;
        this.barangList = barangList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barang_string, parent, false);
        return new BarangStrAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Barang barang = barangList.get(position);
        holder.namaBarang.setText(barang.getNama());
        holder.harga.setText(LocaleFormat.doubleToRupiah(barang.getHarga()));
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.namaBarang)
        TextView namaBarang;
        @BindView(R.id.harga)
        TextView harga;
        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
