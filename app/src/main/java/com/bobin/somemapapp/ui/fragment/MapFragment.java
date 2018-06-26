package com.bobin.somemapapp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bobin.somemapapp.R;
import com.bobin.somemapapp.model.CameraBounds;
import com.bobin.somemapapp.model.DepositionPointClusterItem;
import com.bobin.somemapapp.model.MapCoordinates;
import com.bobin.somemapapp.model.tables.DepositionPoint;
import com.bobin.somemapapp.presenter.MapPresenter;
import com.bobin.somemapapp.ui.activity.DepositionPointsChangedListener;
import com.bobin.somemapapp.ui.view.MapView;
import com.bobin.somemapapp.utils.GoogleMapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;

import java.util.List;

public class MapFragment
        extends MvpAppCompatFragment
        implements OnMapReadyCallback,
        MapView,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnMarkerClickListener {

    @InjectPresenter
    MapPresenter presenter;

    private GoogleMap map;
    private ClusterManager<DepositionPointClusterItem> clusterManager;
    private DepositionPointsChangedListener depositionPointsChangedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FragmentActivity activity = getActivity();
        if (activity instanceof DepositionPointsChangedListener) {
            depositionPointsChangedListener = (DepositionPointsChangedListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity == null)
            return;

        FragmentManager fm = activity.getSupportFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setAlgorithm(new GridBasedAlgorithm<>());
        clusterManager.setAnimation(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        presenter.mapIsReady(getActivity());

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveCanceledListener(this);
        map.setOnMarkerClickListener(this);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.751244, 37.618423), 15));
    }

    @Override
    public void showDialog(String title, String text) {
        Context context = getContext();
        if (context == null)
            return;
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void setMyLocationButtonEnabled(Boolean value) {
        if (map != null) {
            map.setMyLocationEnabled(value);
            map.getUiSettings().setMyLocationButtonEnabled(value);
        }
    }

    @Override
    public void showPins(List<DepositionPoint> pins) {
        clearPins();
        Log.d("MapFragment", "adding pins: " + pins.size());
        for (DepositionPoint pin : pins) {
            addPin(new MapCoordinates(pin.getLatitude(), pin.getLongitude()));
        }
        depositionPointsChangedListener.onChangeDepositionPoints(pins);
    }

    @Override
    public void addPin(MapCoordinates coordinates) {
        if (clusterManager != null)
            clusterManager.addItem(new DepositionPointClusterItem(coordinates));
    }

    @Override
    public void clearPins() {
        if (clusterManager != null)
            clusterManager.clearItems();
    }

    @Override
    public void addRadius(MapCoordinates center, double radius) {
        Circle currentCircle = map.addCircle(
                new CircleOptions()
                        .center(GoogleMapUtils.toLatLng(center))
                        .radius(radius));
    }

    @Override
    public void onCameraIdle() {
        Log.d("MapFragment", "onCameraIdle");
        clusterManager.onCameraIdle();
        cameraStops();
    }

    @Override
    public void onCameraMoveCanceled() {
        Log.d("MapFragment", "onCameraMoveCanceled");
        cameraStops();
    }

    private void cameraStops() {
        if (map == null)
            return;

        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        CameraBounds cameraBounds = new CameraBounds(
                GoogleMapUtils.toCoordinates(visibleRegion.farLeft),
                GoogleMapUtils.toCoordinates(visibleRegion.nearRight),
                GoogleMapUtils.toCoordinates(visibleRegion.latLngBounds.getCenter()));

        presenter.mapCameraStops(cameraBounds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        depositionPointsChangedListener = null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return clusterManager.onMarkerClick(marker);
    }
}
