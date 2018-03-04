package com.proyecto.enpro.enpro.fragmentos;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChasideFragment extends Fragment {

    TextView question;
    Button btnSi;
    Button btnNo;
    View view;
    List<Integer> resultado = new ArrayList<>();
    List<String> preguntaList = new ArrayList<>();

    public ChasideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String name = recuperarNombre();
        String estado = recuperarEstado();
        if (estado.compareTo("null")==0){
            view = inflater.inflate(R.layout.fragment_chaside, container, false);
            question = (TextView) view.findViewById(R.id.pregunta);
            btnSi = (Button) view.findViewById(R.id.btnSi);
            btnNo = (Button) view.findViewById(R.id.btnNo);
            conexionUsuario(name);
        }else{
            view = inflater.inflate(R.layout.test_warning, container, false);
        }

        return view;
    }


    private void conexionUsuario(String name) {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://blackcrozz.com/enpro-api/chaside";
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
                        JSONArray contenArray = response.getJSONArray("content");
                        for (int i = 0; i < contenArray.length(); i++){
                            JSONObject obj = contenArray.getJSONObject(i);
                            String pregunta = obj.getString("preg");
                            preguntaList.add(pregunta);
                        }
                        hacerPreguntas(0);
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

    private void hacerPreguntas(final int i) {
        if (i == 98){
            question.setText("Finalizaste el examen");
            btnSi.setVisibility(View.INVISIBLE);
            btnNo.setVisibility(View.INVISIBLE);
            enviarDatos(resultado);
        }else{
            question.setText(preguntaList.get(i));
            btnSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resultado.add(i + 1);
                    hacerPreguntas(i + 1);
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hacerPreguntas(i + 1);
                }
            });
        }

    }

    private void enviarDatos(List<Integer> resultado) {
        String name = recuperarNombre();
        JSONObject datos = new JSONObject();
        final JSONArray resp = new JSONArray(resultado);
        try {
            datos.put("username", name);
            datos.put("respuestas", resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://blackcrozz.com/enpro-api/enviarchaside";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datos, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.compareTo("success") == 0){
                        Toast.makeText(getContext(), "Felicidades tus resultados ya estan listos.",
                                Toast.LENGTH_SHORT).show();
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
        SharedPreferences shared = getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("chaside", "otro");
        editor.apply();
        editor.commit();
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

}
