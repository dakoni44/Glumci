package org.ftninformatika.glumci.adapteri;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.glumci.R;
import org.ftninformatika.glumci.database.DatabaseHelper;
import org.ftninformatika.glumci.database.model.Glumac;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private DatabaseHelper databaseHelper;
    private OnItemClickListener listener;
    Context context;


    public MyAdapter(DatabaseHelper databaseHelper, OnItemClickListener listener) {
        this.databaseHelper = databaseHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);

        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            holder.tvIme.setText(getDatabaseHelper().getmGlumacDao().queryForAll().get(position).getmIme());
            holder.tvPrezime.setText(getDatabaseHelper().getmGlumacDao().queryForAll().get(position).getmPrezime());
            holder.rbMaleOcene.setRating(getDatabaseHelper().getmGlumacDao().queryForAll().get(position).getmRating());


            Uri mUri = Uri.parse(getDatabaseHelper().getmGlumacDao().queryForAll().get(position).getImage());
            holder.ivMalaSlika.setImageURI(mUri);


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        int list = 0;
        try {
            list = getDatabaseHelper().getmGlumacDao().queryForAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView tvIme;
        private TextView tvPrezime;
        private RatingBar rbMaleOcene;
        private ImageView ivMalaSlika;

        private OnItemClickListener vhListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener vhListener) {
            super(itemView);

            tvIme = itemView.findViewById(R.id.tvIme);
            tvPrezime = itemView.findViewById(R.id.tvPrezime);
            rbMaleOcene = itemView.findViewById(R.id.rbMaleOcene);
            ivMalaSlika = itemView.findViewById(R.id.ivMalaSlika);
            this.vhListener = vhListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            vhListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {

            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
