package id.pptik.org.generalledger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pptik.org.generalledger.R;
import id.pptik.org.generalledger.model.Barang;
import id.pptik.org.generalledger.tools.LocaleFormat;

/**
 * Created by Hafid on 9/27/2017.
 */

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.MyViewHolder>{
    public static String TAG = "[PerawatAdapter]";

    private List<Barang> barangList;
    private Context context;

    public BarangAdapter(Context context, List<Barang> barangList){
        this.context = context;
        this.barangList = barangList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barang_photo, parent, false);
        return new BarangAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Barang barang = barangList.get(position);
        if (position%2==0) {
            holder.foto_l.setImageBitmap(barang.getPic());
            holder.foto_r.setVisibility(View.GONE);
        } else {
            holder.foto_r.setImageBitmap(barang.getPic());
            holder.foto_l.setVisibility(View.GONE);
        }
        holder.harga.setText(LocaleFormat.doubleToRupiah(barang.getHarga()));
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo_l)
        ImageView foto_l;
        @BindView(R.id.photo_r)
        ImageView foto_r;
        @BindView(R.id.harga)
        TextView harga;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
