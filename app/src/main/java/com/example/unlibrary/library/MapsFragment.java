/*
 * MapsFragment
 *
 * November 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.library;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentMapsBinding;
import com.example.unlibrary.util.PushNotificationSender;
import com.example.unlibrary.util.SendNotificationInterface;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Fragment to display Google Map and Autocomplete fragment to choose a handoff location
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = MapsFragment.class.getSimpleName();
    private static final Float ZOOM_LEVEL = 10.0f;
    GoogleMap mMap;
    FragmentMapsBinding mBinding;
    LibraryViewModel mViewModel;
    LatLng mLatLng;
    private final PushNotificationSender mSender = new PushNotificationSender();

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = mViewModel.getHandoffLocation().getValue();

        if (latLng == null) {
            latLng = new LatLng(53.5461, -113.4938);
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
    }

    /**
     * Sets up Places SDK, ViewModel, and Binding
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        mBinding = FragmentMapsBinding.inflate(inflater, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), getString(R.string.api_key));
        }
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.confirmButton).navigate(navDirections));

        mBinding.confirmButton.setOnClickListener(v -> {
            if (mViewModel.getHandoffLocation().getValue() != null) {
                mViewModel.updateHandoffLocation(mLatLng);
            } else {
                SendNotificationInterface send = (target, title, body) -> mSender.generateNotification(v, target, title, body);
                mViewModel.acceptSelectedRequester(mLatLng, send);
            }
        });

        return mBinding.getRoot();
    }

    /**
     * Sets up autocomplete fragment and the map fragment
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Map Fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                mBinding.confirmButton.setVisibility(View.VISIBLE);
                mLatLng = place.getLatLng();
            }

            @Override
            public void onError(@NotNull Status status) {
                Log.e(TAG, "Failed to get place: " + status.toString());
            }
        });
    }
}
