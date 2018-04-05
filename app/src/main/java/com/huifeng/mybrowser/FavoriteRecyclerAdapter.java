package com.huifeng.mybrowser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huifeng.mybrowser.Models.FavoriteModel;

import java.util.ArrayList;

/**
 * Created by huife on 2018-04-02.
 */

public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteItemViewHolder> {

    ArrayList<FavoriteModel> list = new ArrayList<>();

    public void updateFavorList(ArrayList<FavoriteModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public FavoriteRecyclerAdapter(ArrayList<FavoriteModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public FavoriteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
       return new FavoriteItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteItemViewHolder holder, int position) {
        holder.container.setTag(list.get(position));
        holder.title.setText(list.get(position).getTitle());
        holder.url.setText(list.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FavoriteItemViewHolder extends RecyclerView.ViewHolder{
        LinearLayout container;
        TextView title, url;

        public FavoriteItemViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.favor_container);
            title = itemView.findViewById(R.id.favor_title);
            url = itemView.findViewById(R.id.favor_url);
        }
    }
}
