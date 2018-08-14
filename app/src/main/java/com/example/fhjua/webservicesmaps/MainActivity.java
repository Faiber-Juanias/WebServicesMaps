package com.example.fhjua.webservicesmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText txtLatitud;
    private EditText txtLongitud;
    private TextView viewResult;
    private Button btnDatos;
    private LocationManager locationManager;
    private Location location;
    private ObtenerWebService hiloConexion;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLatitud = (EditText) findViewById(R.id.txt_latitud);
        txtLongitud = (EditText) findViewById(R.id.txt_longitud);
        viewResult = (TextView) findViewById(R.id.view_result);
        btnDatos = (Button) findViewById(R.id.btn_envia);

        /**
         * Tenemos un objeto de la clase LocationManajer y a este le asignamos un
         * servicio de localizacion
         */
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /**
         * Valida si la version de la API en la cual se esta trabajando es mayor o igual a la API de nivel 23
         * Si la condicion se cumple se le pide al usuario los permisos necesarios
         */
        int sdk = Build.VERSION.SDK_INT;
        //Si la version del SDK del dispositivo es mayor al SDK nivel 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Si no se aceptan los permisos
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }else {
                //Almacenamos en un objeto de tipo Location la ultima posicion almacenada del GPS
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }else{
            //Almacenamos en un objeto de tipo Location la ultima posicion almacenada del GPS
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        mostrarLocalizacion(location);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mostrarLocalizacion(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);


        /*
        //-----------atos de los proveedores----------
        //Almaceno en una lista todos los proveedores de servicios
        List<String> listaProveedores = locationManager.getAllProviders();
        //Almaceno en un objeto de la clase LocationProvider un determinado proveedor de la lista
        LocationProvider proveedorUno = locationManager.getProvider(listaProveedores.get(0));
        //Obtengo la presicion del proveedor selecconado
        int presicioProveedor = proveedorUno.getAccuracy();
        //Determino si el proveedor tiene soporte para altitud
        boolean tieneAltitud = proveedorUno.supportsAltitude();

        //Establesco criterios para buscar un proveedor
        Criteria criteria = new Criteria();
        //Establesco el tipo de afinidad el cual quiero que tenga el proveedor a buscar
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //Guardo el mejor proveedor que haya concordado con el criterio puesto anteriormente
        String mejorProveedor = locationManager.getBestProvider(criteria, true);
        //----------------------------------------------
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }else {
                locationManager.removeUpdates(locationListener);
            }
        }else{
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
        }
    }

    public void mostrarLocalizacion(Location loc) {
        if (loc != null){
            hiloConexion = new ObtenerWebService();
            //Mandamos los parámetros que se nesecitan pasando la longitud y la latitud que trae el objeto de tipo Location
            hiloConexion.execute(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));
        }else{
            Toast.makeText(this, "El objeto Location es null.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

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

            Log.i("CADENA", cadena);

            URL url = null;
            String devuelve = "Sin contenido.";

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
                        direccion = resultJSON.getJSONObject(0).getString("formatted_address");
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
