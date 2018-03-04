package com.proyecto.enpro.enpro.fragmentos;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.proyecto.enpro.enpro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResChasideFragment extends Fragment {

    View view;
    TextView parrafo, tituloIntereses, tituloAptitudes, parrafoAptitudes, listaIntereses, listaAptitudes;

    public ResChasideFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String name = recuperarNombre();
        String estado = recuperarEstado();

        if (!(estado.compareTo("null")==0)){
            view = inflater.inflate(R.layout.fragment_res_chaside, container, false);
            parrafo = (TextView) view.findViewById(R.id.textoSaludo);
            tituloIntereses = (TextView) view.findViewById(R.id.tituloIntereses);
            tituloAptitudes = (TextView) view.findViewById(R.id.tituloAptitudes);
            parrafoAptitudes = (TextView) view.findViewById(R.id.parrafoAptitudes);
            listaIntereses = (TextView) view.findViewById(R.id.listaIntereses);
            listaAptitudes = (TextView) view.findViewById(R.id.listaAptitudes);
            conexionUsuario(name);
        }else{
            view = inflater.inflate(R.layout.resul_warning, container, false);
        }

        return view;
    }

    private String recuperarNombre() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        String nombre = prefs.getString("user", null);
        return nombre;
    }

    private String recuperarEstado() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        String chaside = prefs.getString("chaside", null);
        return chaside;
    }

    private void conexionUsuario(String name) {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://blackcrozz.com/enpro-api/ingresar";
        Log.e("usuario", name);
        JSONObject user = new JSONObject();
        try {
            user.put("username", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, user, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.compareTo("success") == 0){
                        JSONObject content = response.getJSONObject("content");
                        String nombreUser = content.getString("nombres");
                        JSONObject interesesChaside = content.getJSONObject("intereseschaside");
                        String nombreInteres = interesesChaside.getString("nombre");
                        String interesAreas = interesesChaside.getString("intereses");
                        JSONObject aptitudesChaside = content.getJSONObject("aptitudeschaside");
                        String nomAptitud = aptitudesChaside.getString("nombre");
                        String aptitudesAreas = aptitudesChaside.getString("aptitudes");
                        asignarValores(nombreUser, nombreInteres, interesAreas, nomAptitud, aptitudesAreas);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Ups! Sucedio un problema, revisa tu conexion a internet",
                        Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        queue.add(request);
    }

    private void asignarValores(String nombreUser, String nombreInteres,
                                String interesAreas, String nomAptitud, String aptitudesAreas) {

        String chasideIntereses = interesAreas.replace(",","\n - ");
        chasideIntereses = " - " + chasideIntereses;
        String chasideAptitudes = aptitudesAreas.replace(",","\n - ");
        chasideAptitudes = " - " + chasideAptitudes;
        String texto = "Hola " + nombreUser + ", de acuerdo a los resultados obtenidos, " +
                "se demuestra que tus intereses son hacia: ";
        parrafo.setText(texto);
        tituloIntereses.setText(nombreInteres);
        listaIntereses.setText(chasideIntereses);
        texto = "Este test tambien indica las aptitudes que tienes para:";
        parrafoAptitudes.setText(texto);
        listaAptitudes.setText(chasideAptitudes);
        tituloAptitudes.setText(nomAptitud);


    }

}
