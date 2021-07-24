package com.gstregistration.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.gstregistration.R;
import com.gstregistration.webViewFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ConstraintLayout container_application_form = root.findViewById(R.id.container_application_form);
        ConstraintLayout container_gst_registration = root.findViewById(R.id.container_gst_registration);
        ConstraintLayout container_benefits = root.findViewById(R.id.container_benefits);
        ConstraintLayout container_convert_u2u = root.findViewById(R.id.container_convert_u2u);

        container_application_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new webViewFragment("https://app.myonlineca.org/gstapp-registration/");
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.nav_host_fragment, fragment, fragment.getTag())
                        .addToBackStack(null)
                        .commit();
            }
        });


        container_gst_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new webViewFragment("https://app.myonlineca.org/gstapp-return/");
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.nav_host_fragment, fragment, fragment.getTag())
                        .addToBackStack(null)
                        .commit();
            }
        });


        container_benefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new webViewFragment("https://app.myonlineca.org/gstapp-support-2/");
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.nav_host_fragment, fragment, fragment.getTag())
                        .addToBackStack(null)
                        .commit();
            }
        });
//        https://app.myonlineca.org/gstapp-ammendment/
//        https://app.myonlineca.org/gstapp-support-2/


        container_convert_u2u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new webViewFragment("https://app.myonlineca.org/gstapp-ammendment/");
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.nav_host_fragment, fragment, fragment.getTag())
                        .addToBackStack(null)
                        .commit();
            }
        });


        return root;
    }
}
