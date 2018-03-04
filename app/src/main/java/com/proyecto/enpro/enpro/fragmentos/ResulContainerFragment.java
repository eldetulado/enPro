package com.proyecto.enpro.enpro.fragmentos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyecto.enpro.enpro.GreenFragment;
import com.proyecto.enpro.enpro.R;
import com.proyecto.enpro.enpro.Utilidades;
import com.proyecto.enpro.enpro.adapters.SeccionesAdapter;

public class ResulContainerFragment extends Fragment {

    AppBarLayout appBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    View vista;

    public ResulContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_contenedor, container, false);

        if (Utilidades.rotacion == 0){
            View parent = (View) container.getParent();
            if (appBar==null){
                appBar = (AppBarLayout) parent.findViewById(R.id.bar);
                tabLayout = new TabLayout(getContext());
                tabLayout.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
                appBar.addView(tabLayout);

                viewPager = (ViewPager) vista.findViewById(R.id.viewPager);
                llenarViewPager(viewPager);

                viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                });

                tabLayout.setupWithViewPager(viewPager);
            }
        }else
            Utilidades.rotacion = 1;

        return vista;
    }

    private void llenarViewPager(ViewPager viewPager) {
        SeccionesAdapter adapter = new SeccionesAdapter(getFragmentManager());
        adapter.addFragment(new ResChasideFragment(), "Resultado");
        adapter.addFragment(new GreenFragment(), "Resultado");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Utilidades.rotacion == 0)
            appBar.removeView(tabLayout);
    }
}
