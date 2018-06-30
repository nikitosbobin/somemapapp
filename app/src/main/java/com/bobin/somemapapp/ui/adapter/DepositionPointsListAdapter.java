package com.bobin.somemapapp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobin.somemapapp.infrastructure.PointWatchedService;
import com.bobin.somemapapp.model.MapCoordinates;
import com.bobin.somemapapp.model.tables.DepositionPoint;
import com.bobin.somemapapp.ui.holder.DepositionPointViewHolder;
import com.bobin.somemapapp.utils.GoogleMapUtils;

import java.util.HashMap;
import java.util.List;

public class DepositionPointsListAdapter extends RecyclerView.Adapter<DepositionPointViewHolder> {

    private List<DepositionPoint> points;
    private HashMap<String, String> icons;
    private MapCoordinates userLocation;
    private PointClickListener clickListener;
    private PointWatchedService watchedService;

    public DepositionPointsListAdapter(PointWatchedService watchedService) {
        this.watchedService = watchedService;
    }

    @NonNull
    @Override
    public DepositionPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                        int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                DepositionPointViewHolder.layoutId,
                parent,
                false);
        return new DepositionPointViewHolder(view).withClickListener(clickListener);
    }

    public void setClickListener(PointClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull DepositionPointViewHolder holder,
                                 int position) {
        DepositionPoint point = getPoint(position);
        int meters = -1;
        if (userLocation != null)
            meters = (int) GoogleMapUtils.distanceBetween(userLocation, point.getMapCoordinates());
        holder.bind(point, icons.get(point.getPartnerName()), position, meters, watchedService.isWatched(point.getExternalId()));
    }

    @Override
    public int getItemCount() {
        return points == null ? 0 : points.size();
    }

    private DepositionPoint getPoint(int position) {
        return points.get(position);
    }

    public void setDataset(List<DepositionPoint> points, HashMap<String, String> icons, MapCoordinates userLocation) {
        this.points = points;
        this.icons = icons;
        this.userLocation = userLocation;
        notifyDataSetChanged();
    }

    public interface PointClickListener {
        void onClickPoint(DepositionPoint point, View iconView, int position);
    }
}
