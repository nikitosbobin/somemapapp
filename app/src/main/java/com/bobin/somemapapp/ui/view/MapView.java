package com.bobin.somemapapp.ui.view;

import com.arellomobile.mvp.MvpView;
import com.bobin.somemapapp.model.MapCoordinates;
import com.bobin.somemapapp.model.response.DepositionPointResponse;

import java.util.List;

public interface MapView extends MvpView {

    void showDialog(String title, String text);
    void setMyLocationButtonEnabled(Boolean value);
    void showPins(List<DepositionPointResponse> pins);
    void addPin(MapCoordinates coordinates);
    void clearPins();
    void addRadius(MapCoordinates center, double radius);
}

