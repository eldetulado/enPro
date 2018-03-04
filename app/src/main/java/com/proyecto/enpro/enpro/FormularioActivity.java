package com.proyecto.enpro.enpro;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

public class FormularioActivity extends AppCompatActivity {
    String erores, nom, ape, col, fecha;
    int sexo = 3;
    EditText nombre, apellido, colegio, fnac;
    RadioGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_formulario);

        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apelido);
        colegio = (EditText) findViewById(R.id.colegio);
        fnac = (EditText) findViewById(R.id.fnac);
        group = (RadioGroup) findViewById(R.id.contenedorSexo);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.Masculino:
                        sexo = 0;
                        break;
                    case R.id.Femenino:
                        sexo = 1;
                        break;
                }
            }
        });

    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public void mostrar(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selected = year+"-"+twoDigits(month + 1)+"-"+twoDigits(dayOfMonth);
                fnac.setText(selected);
            }
        }, 2000, 1, 1);
        dialog.show();
    }

    public void sendData(View view) {
        nom = nombre.getText().toString().trim();
        ape = apellido.getText().toString().trim();
        col = colegio.getText().toString().trim();
        fecha = fnac.getText().toString().trim();

        if (validar(nom, ape, col, fecha) == 5){
            JSONObject estudiante = new JSONObject();
            try {
                estudiante.put("nombres",nom);
                estudiante.put("apellidos",ape);
                estudiante.put("colegio",col);
                estudiante.put("fnac",fecha);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mandar(estudiante);
        }else{
            Toast.makeText(getApplicationContext(), erores, Toast.LENGTH_SHORT).show();
        }
    }

    private void mandar(JSONObject estudiante) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://blackcrozz.com/enpro-api/registrar";
        JsonObjectRequest obj = new JsonObjectRequest(Request.Method.POST, url, estudiante, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    String content = response.getString("content");
                    if (status.equals("success")){
                        Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                        Toast.makeText(getApplicationContext(), "Codigo de usuario " + content, Toast.LENGTH_LONG).show();
                        SharedPreferences pref = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("user", content);
                        editor.putString("status", "true");
                        editor.putString("chaside", "null");
                        editor.commit();
                        startActivity(i);
                        finish();
                    }else
                        Toast.makeText(getApplicationContext(), "Ocurrio un problema", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Ups! Sucedio un problema", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return  params;
            }
        };
        queue.add(obj);
    }

    private int validar(String nombre, String apellido, String colegio,
                        String fnac) {
        int c = 0;
        erores = "";
        if (nombre.length() != 0)
            c++;
        else
            erores += "Llena el nombre\n";
        if (apellido.length() != 0)
            c++;
        else
            erores += "Llena el apellido\n";
        if (colegio.length() != 0)
            c++;
        else
            erores += "Llena el colegio\n";
        if (fnac.length() != 0)
            c++;
        else
            erores += "Llena la fecha de nacimiento\n";
        if (sexo == 0 || sexo == 1)
            c++;
        else
            erores += "Elije tu sexo\n";
        return c;
    }
}
