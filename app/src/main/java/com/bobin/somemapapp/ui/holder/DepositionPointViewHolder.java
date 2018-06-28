package com.bobin.somemapapp.ui.holder;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobin.somemapapp.R;
import com.bobin.somemapapp.model.tables.DepositionPoint;
import com.bobin.somemapapp.ui.adapter.DepositionPointsListAdapter;
import com.bobin.somemapapp.utils.GoogleMapUtils;
import com.bobin.somemapapp.utils.ViewUtils;

import org.w3c.dom.Text;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("ALL")
public class DepositionPointViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.partner_name)
    TextView partnerName;
    @BindView(R.id.full_address)
    TextView fullAddress;
    @BindView(R.id.point_icon)
    ImageView pointIcon;
    @BindView(R.id.distance)
    TextView distance;
    private DepositionPoint point;
    private int position;
    private DepositionPointsListAdapter.PointClickListener clickListener;

    public DepositionPointViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public DepositionPointViewHolder withClickListener(
            DepositionPointsListAdapter.PointClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public static int layoutId = R.layout.item_deposition_point;

    public void bind(DepositionPoint point, String icon, int position, int meters) {
        this.point = point;
        this.position = position;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            pointIcon.setTransitionName(point.getPartnerName() + UUID.randomUUID());

        partnerName.setText(point.getPartnerName());
        fullAddress.setText(point.getFullAddress());
        distance.setVisibility(meters != -1 ? View.VISIBLE : View.GONE);
        distance.setText(GoogleMapUtils.distanceToString(distance.getContext(), meters));
        ViewUtils.glideRoundImage(pointIcon, icon);
    }

    public int getItemPosition() {
        return position;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null)
            clickListener.onClickPoint(point, pointIcon);
    }
}
