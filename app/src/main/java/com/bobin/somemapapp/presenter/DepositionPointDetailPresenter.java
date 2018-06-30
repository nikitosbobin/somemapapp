package com.bobin.somemapapp.presenter;

import android.net.Uri;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bobin.somemapapp.MapApp;
import com.bobin.somemapapp.infrastructure.PartnersService;
import com.bobin.somemapapp.infrastructure.PartnersServiceImpl;
import com.bobin.somemapapp.infrastructure.PointWatchedService;
import com.bobin.somemapapp.infrastructure.PointWatchedServiceImpl;
import com.bobin.somemapapp.model.MapCoordinates;
import com.bobin.somemapapp.model.tables.DepositionPartner;
import com.bobin.somemapapp.model.tables.Limit;
import com.bobin.somemapapp.network.api.TinkoffApiFactory;
import com.bobin.somemapapp.storage.KeyValueStorageImpl;
import com.bobin.somemapapp.storage.PartnersCacheImpl;
import com.bobin.somemapapp.ui.view.DepositionPointDetailView;
import com.bobin.somemapapp.utils.GoogleMapUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class DepositionPointDetailPresenter extends MvpPresenter<DepositionPointDetailView> {
    private PartnersService partnersService;
    private CompositeDisposable compositeDisposable;
    private DepositionPartner partner;
    private PointWatchedService watchedService;

    public DepositionPointDetailPresenter() {
        partnersService = new PartnersServiceImpl(new TinkoffApiFactory().createApi(), new PartnersCacheImpl(new KeyValueStorageImpl(MapApp.context)));
        compositeDisposable = new CompositeDisposable();
        watchedService = new PointWatchedServiceImpl();
    }

    public Limit getLimit(int id) {
        return partner.getLimits().get(id);
    }

    public void onStart(String partnerId, MapCoordinates pointLocation, MapCoordinates userPosition) {
        if (userPosition != null) {
            int meters = (int) GoogleMapUtils.distanceBetween(pointLocation, userPosition);
            getViewState().showDistance(meters);
        }

        Disposable subscribe = partnersService.getPartnerById(partnerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        partner -> {
                            this.partner = partner;
                            getViewState().showPartner(partner);
                        },
                        t -> getViewState().finishActivity());
        compositeDisposable.add(subscribe);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    public void goToWebSite() {
        if (partner != null) {
            String url = partner.getUrl();
            getViewState().openBrowser(Uri.parse(url));
        }
    }

    public void setPointWatched(String pointId) {
        watchedService.setWatched(pointId);
    }
}
