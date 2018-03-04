package com.proyecto.enpro.enpro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        nombre = (EditText) findViewById(R.id.codigo);
        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Si no cuentas con un codigo de ingreso, " +
                        "puedes registrarte", Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences mPref = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String stado = mPref.getString("status", "false");
        if (stado.equals("true")){
            Intent i = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(i);
            finish();
        }
    }

    public void sendForm(View view){
        Intent i = new Intent(getApplicationContext(), FormularioActivity.class);
        startActivity(i);
        finish();
    }

    public void sendLogin(View view) {
        if (nombre.getText().toString().trim().length() != 0){
            RequestQueue queue = Volley.newRequestQueue(this);
            String nom = nombre.getText().toString().trim();
            String url = "http://blackcrozz.com/enpro-api/ingresar";
            JSONObject obj = new JSONObject();
            try {
                obj.put("username",nom);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")){
                            JSONObject contentObject = response.getJSONObject("content");
                            String content = contentObject.getString("username");
                            String intereseschaside = contentObject.getString("intereseschaside_id");
                            Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                            SharedPreferences pref = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("user", content);
                            editor.putString("status", "true");
                            editor.putString("chaside", intereseschaside);
                            editor.commit();
                            startActivity(i);
                            finish();
                        }else
                            Toast.makeText(getApplicationContext(), "Usuario no registrado", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Ups! Sucedio un problema, revisa tu conexion a internet", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            queue.add(objectRequest);
        }else
            Toast.makeText(this, "Llena tu codigo de ingreso", Toast.LENGTH_SHORT).show();
    }
}
