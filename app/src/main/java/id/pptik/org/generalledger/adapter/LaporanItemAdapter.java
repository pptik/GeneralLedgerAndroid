package id.pptik.org.generalledger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pptik.org.generalledger.R;
import id.pptik.org.generalledger.config.Constants;
import id.pptik.org.generalledger.model.Barang;
import id.pptik.org.generalledger.model.LaporanItem;
import id.pptik.org.generalledger.tools.ImageTools;
import id.pptik.org.generalledger.tools.LocaleFormat;

/**
 * Created by Hafid on 9/29/2017.
 */

public class LaporanItemAdapter extends RecyclerView.Adapter<LaporanItemAdapter.MyViewHolder> {

    private List<LaporanItem> laporanItems;
    private Context context;

    public LaporanItemAdapter(Context context, List<LaporanItem> barangList){
        this.context = context;
        this.laporanItems = barangList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barang_full, parent, false);
        return new LaporanItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LaporanItem barang = laporanItems.get(position);
        if (barang.getFlag()==1){
            holder.namaBarang.setVisibility(View.GONE);
            holder.harga.setText(LocaleFormat.doubleToRupiah(barang.getHarga()));
            Glide.with(context).load(barang.getNama()).into(holder.foto_l);
        } else {
            holder.foto_l.setVisibility(View.GONE);
            holder.namaBarang.setText(barang.getNama());
            holder.harga.setText(LocaleFormat.doubleToRupiah(barang.getHarga()));
        }
    }

    @Override
    public int getItemCount() {
        return laporanItems.size();
    }

    public double sumPrice(){
        double sum = 0;
        for (int i = 0; i < laporanItems.size(); i++){
            sum = sum + laporanItems.get(i).getHarga();
        }
        return sum;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo_l)
        ImageView foto_l;
        @BindView(R.id.harga)
        TextView harga;
        @BindView(R.id.namaBarang)
        TextView namaBarang;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}

