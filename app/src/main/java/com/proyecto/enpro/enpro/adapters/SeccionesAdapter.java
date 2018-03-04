package com.proyecto.enpro.enpro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SeccionesAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> listaFragment = new ArrayList<>();
    private final List<String> listaTitulos = new ArrayList<>();

    public SeccionesAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String titulo){
        listaFragment.add(fragment);
        listaTitulos.add(titulo);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return listaTitulos.get(position);

    }

    @Override
    public Fragment getItem(int position) {
        return listaFragment.get(position);
    }

    @Override
    public int getCount() {
        return listaFragment.size();
    }
}
