package com.example.unlibrary.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentMapsBinding;
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

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    Place mPlace;
    FragmentMapsBinding mBinding;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng edmonton = new LatLng(53.5461, -113.4938);
        mMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(edmonton));
        googleMap.setMinZoomPreference(10);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), getString(R.string.maps_api_key));
        }
        mBinding = FragmentMapsBinding.inflate(inflater, container, false);
        mBinding.confirmButton.setOnClickListener(v -> {
            MapsFragmentDirections.ActionMapsFragmentToLibraryBookDetailsFragment action =
                            MapsFragmentDirections.actionMapsFragmentToLibraryBookDetailsFragment();
            action.setLocationName(mPlace.getName());
            action.setLat((float) mPlace.getLatLng().latitude);
            action.setLon((float) mPlace.getLatLng().longitude);
            Navigation.findNavController(mBinding.confirmButton).navigate(action);
        });
        return mBinding.getRoot();
    }

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
                mPlace = place;
                mBinding.confirmButton.setVisibility(View.VISIBLE);
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
            }
        });
    }
}
