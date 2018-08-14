package com.example.fhjua.webservicesmaps;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText txtLatitud;
    private EditText txtLongitud;
    private TextView viewResult;
    private Button btnDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLatitud = (EditText) findViewById(R.id.txt_latitud);
        txtLongitud = (EditText) findViewById(R.id.txt_longitud);
        viewResult = (TextView) findViewById(R.id.view_result);
        btnDatos = (Button) findViewById(R.id.btn_envia_datos);

        btnDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObtenerWebService hiloConexion = new ObtenerWebService();
                //Mandamos los parámetros que se nesecitan
                hiloConexion.execute(txtLatitud.getText().toString(), txtLongitud.getText().toString());
            }
        });
    }

    public class ObtenerWebService extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            viewResult.setText("");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            viewResult.setText(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected String doInBackground(String... voids) {
            //Guardamos la url a la cual se va a conectar
            String cadena = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
            //Creamos la cadena con los parametros recibidos de latitud y longitud
            cadena = cadena + voids[0] + "," + voids[1] + "&sensor=false";

            URL url = null;
            String devuelve = "";

            try {
                url = new URL(cadena);
                //Abrimos la conexion
                HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                conection.setRequestProperty("User-Agent", "Mozilla/5.0" + "(Linux; Android 1.5; es-ES) Ejemplo HTTP");

                int respuesta = conection.getResponseCode();
                StringBuilder result  = new StringBuilder();

                //Si la conexion es exitosa
                if (respuesta == HttpURLConnection.HTTP_OK){
                    //Preparo la cadena de entrada
                    InputStream in = new BufferedInputStream(conection.getInputStream());
                    //Lo introducimos en un BufferedReader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null){
                        result.append(line); //Pasamos toda la entrada al StringBuilder
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("results"); //results es el nombre del campo en el JSON

                    //Vamos obteniendo todos los campos que nos interesan
                    //En este caso obtenemos la primera direccion de los resultados
                    String direccion = "SIN DATOS PARA ESA LONGITUD Y LATITUD";
                    if (resultJSON.length()>0){
                        direccion = resultJSON.getJSONObject(0).getString("formatted_addresss");
                    }
                    devuelve = "Direccion: " + direccion;
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }

            return devuelve;
        }
    }
}
