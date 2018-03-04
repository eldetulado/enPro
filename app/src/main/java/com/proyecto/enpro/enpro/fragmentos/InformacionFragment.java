package com.proyecto.enpro.enpro.fragmentos;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class InformacionFragment extends Fragment {

    View view;
    TextView userCod, userName, userLastName, userFnac, userSexo, userSchool, testCha, testKud;

    public InformacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_informacion, container, false);
        userCod = (TextView) view.findViewById(R.id.user);
        userName = (TextView) view.findViewById(R.id.userName);
        userLastName = (TextView) view.findViewById(R.id.userLastName);
        userFnac = (TextView) view.findViewById(R.id.userFnac);
        userSexo = (TextView) view.findViewById(R.id.userSexo);
        userSchool = (TextView) view.findViewById(R.id.userSchool);
        testCha = (TextView) view.findViewById(R.id.testChaside);
        testKud = (TextView) view.findViewById(R.id.testKuder);
        String name = recuperarNombre();
        conexionUsuario(name);
        return view;
    }

    private String recuperarNombre() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        String nombre = prefs.getString("user", null);
        return nombre;
    }

    private void conexionUsuario(String name) {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://blackcrozz.com/enpro-api/ingresar";
        Log.e("usuario", name);
        final JSONObject user = new JSONObject();
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
                        JSONObject contentArray = response.getJSONObject("content");
                        userCod.setText(contentArray.getString("username"));
                        userName.setText(contentArray.getString("nombres"));
                        userLastName.setText(contentArray.getString("apellidos"));
                        userSchool.setText(contentArray.getString("colegio"));
                        userFnac.setText(contentArray.getString("fnac"));
                        if (contentArray.getString("intereseschaside_id")
                                .compareTo("null")!=0)
                            testCha.setText("Realizado");
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

}
