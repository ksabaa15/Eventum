package ch.epfl.sdp.ui.main.map;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;

import ch.epfl.sdp.Event;
import ch.epfl.sdp.R;
import ch.epfl.sdp.auth.Authenticator;
import ch.epfl.sdp.databinding.FragmentMapBinding;
import ch.epfl.sdp.db.Database;
import ch.epfl.sdp.db.DatabaseObject;
import ch.epfl.sdp.map.LocationService;
import ch.epfl.sdp.map.MapManager;
import ch.epfl.sdp.platforms.google.map.GoogleLocationService;
import ch.epfl.sdp.platforms.google.map.GoogleMapManager;
import ch.epfl.sdp.ui.main.FilterSettingsViewModel;
import ch.epfl.sdp.ui.main.swipe.EventDetailFragment;

import static ch.epfl.sdp.ObjectUtils.verifyNotNull;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private MapViewModel mViewModel;
    private final MapViewModel.MapViewModelFactory mFactoryMap;
    private FilterSettingsViewModel.FilterSettingsViewModelFactory mFactoryFilterSettings;
    private FragmentMapBinding mBinding;

    private MapView mMapView;
    private float mZoomLevel = 12;

    @VisibleForTesting
    public MapFragment(@NonNull MapManager mapManager, @NonNull LocationService locationService, @NonNull Database database, @NonNull Authenticator authenticator) {
        verifyNotNull(mapManager, database, locationService);
        mFactoryMap = new MapViewModel.MapViewModelFactory();
        mFactoryMap.setMapManager(mapManager);
        mFactoryMap.setLocationService(locationService);
        mFactoryFilterSettings = new FilterSettingsViewModel.FilterSettingsViewModelFactory();
        mFactoryFilterSettings.setDatabase(database);
        mFactoryFilterSettings.setLocationService(locationService);
        mFactoryFilterSettings.setAuthenticator(authenticator);
    }

    public MapFragment() {
        mFactoryMap = new MapViewModel.MapViewModelFactory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMapBinding.inflate(inflater, container, false);
        mMapView = mBinding.getRoot().findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        LocationService locationService =
                new GoogleLocationService((LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE));
        mFactoryMap.setLocationService(locationService);

        mMapView.getMapAsync(googleMap -> {
            googleMap.setOnMarkerClickListener(this);
            googleMap.setMyLocationEnabled(true);

            mFactoryMap.setMapManager(new GoogleMapManager(googleMap));
            mViewModel = new ViewModelProvider(this, mFactoryMap).get(MapViewModel.class);

            FilterSettingsViewModel filterSettingsViewModel =
                    new ViewModelProvider(requireActivity(), mFactoryFilterSettings).get(FilterSettingsViewModel.class);

            filterSettingsViewModel.getFilteredEvents().observe(getViewLifecycleOwner(), events -> {
                mViewModel.clearEvents();
                for(DatabaseObject<Event> event: events)
                    mViewModel.addEvent(event.getObject());
            });

            mViewModel.centerCamera(getContext(), mZoomLevel);
        });

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(this.getId(), new EventDetailFragment(mViewModel.getEventFromMarker(marker),this))
                .commit();
        return true;
    }
}