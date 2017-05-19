package com.example.matias.pruenaspotify;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Objetos.Artistas;

public class Principal extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    boolean permission = false;

    private RequestQueue requestQueue;

    private String URL_ARTISTAS_P1 = "https://api.spotify.com/v1/search?q=";
    private String URL_ARTISTAS_P2 = "&type=artist";

    private StringRequest StringRequestQueue;

    Context mContext;

    EditText ed_buscar;
    Button b_buscar;
    ImageView im_foto;

    String suma = "";

    ArrayList<String> ArtistasBuscados;
    ArrayList<String> FotoAlbunArtista;
    ArrayList<String> IdArtistaBuscado;


    Artistas artistas;
    ArrayList<Artistas> dataArtistas;

    ListAdapter adapter;

    ListView listaArtistas;

    ProgressDialog dialog;

    File filepath = Environment.getExternalStorageDirectory();
    File dir = new File(filepath.getAbsolutePath() + "/ArtistaBuscado");

    String nomArtis, idArtis;

    int contadorPeticiones = 0;

    Button ver_favoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mContext = this;
        dataArtistas = new ArrayList<>();

        // Crear nueva cola de peticiones
        requestQueue= Volley.newRequestQueue(mContext);

        dir.mkdirs();

        ed_buscar = (EditText) findViewById(R.id.ed_buscar);
        b_buscar = (Button) findViewById(R.id.b_buscar);

        im_foto = (ImageView) findViewById(R.id.fotoartista);

        listaArtistas = (ListView) findViewById(R.id.listartistas);

        ver_favoritos = (Button) findViewById(R.id.ver_favoritos);

        b_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else{
                    connected = false;
                }

                if(connected){

                    listaArtistas.setAdapter(null);
                    dataArtistas = new ArrayList<>();

                    if(ed_buscar.getText().toString().equals("")){
                        Toast.makeText(mContext, "Debe Ingresar un Artista a Buscar", Toast.LENGTH_SHORT).show();
                    }else{

                        suma = "";

                        ArtistasBuscados = new ArrayList<String>();
                        FotoAlbunArtista = new ArrayList<String>();
                        IdArtistaBuscado = new ArrayList<String>();

                        //Ejecuto el metodo para buscar el artista.
                        ObtenerArtista(ed_buscar.getText().toString());

                    }

                }else{

                    Toast.makeText(mContext, "No hay conexión a internet", Toast.LENGTH_SHORT).show();

                }





            }
        });

        ver_favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences prefs = getSharedPreferences("AlbunsFavoritos",Context.MODE_PRIVATE);

                String estadoFavoritos = prefs.getString("EstadoFavorito", "nok");
                
                if(estadoFavoritos.equals("ok")){
                    
                    //Envio hacia mostrar lista de favoritos
                    Intent intent = new Intent(Principal.this, AlbumsArtista.class);
                    intent.putExtra("EstadoFavorito", "si");
                    startActivity(intent);
                    
                }else{
                    Toast.makeText(Principal.this, "No existe lista de favoritos.", Toast.LENGTH_SHORT).show();
                }


            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            permission = checkAndRequestPermissions();
        }


    }

    private void ObtenerArtista(String artistaBuscado){

        String artista =  artistaBuscado.replace(" ", "%20");
        String URL_FINAL = URL_ARTISTAS_P1 + artista + URL_ARTISTAS_P2;

        StringRequestQueue = new StringRequest(Request.Method.GET, URL_FINAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("respo", "respusta: " + response);

                        ObtieneDatos(response);

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Error", error.toString());

            }

        });

        requestQueue.add(StringRequestQueue);

    }


    private void ObtieneDatos(String jsonString){


        try {
            JSONObject json = new JSONObject(jsonString);

            JSONObject artist = json.getJSONObject("artists");
            JSONArray items = artist.getJSONArray("items");

            for(int i = 0; i<items.length(); i++){

                JSONObject temp_json = items.getJSONObject(i);
                suma =  suma + "-"+ temp_json.getString("name");

                //Obtengo del nombre del artista segun el tag 'name'
                ArtistasBuscados.add(temp_json.getString("name"));
                //Obtengo el Id del artista segun el tag 'id'
                IdArtistaBuscado.add(temp_json.getString("id"));

                //Entro al Array de Imagenes para obtener la de menor resolucion.
                JSONArray temp_jsonarray = temp_json.getJSONArray("images");
                if(temp_jsonarray != null && temp_jsonarray.length() > 0){

                    JSONObject imagen_temp = temp_jsonarray.getJSONObject(2);
                    FotoAlbunArtista.add(imagen_temp.getString("url"));
                }else{

                    //Debo posicionar la foto sin imagen.
                    FotoAlbunArtista.add("sinfoto");

                }
            }

            //t_buscar.setText(suma);

            Log.d("ARTISTA","LISTA ARTISTA : " + ArtistasBuscados.toString());
            Log.d("ID ARTIS","LISTA ID ARTISTA: " + IdArtistaBuscado.toString());
            Log.d("FOTO ARTIS","LISTA FOTOS ARTISTAS: " + FotoAlbunArtista.toString());


            for(int j=0; j<ArtistasBuscados.size(); j++ ){

                artistas = new Artistas();

                artistas.setNombreArtista(ArtistasBuscados.get(j).toString().substring(0,3) + j);
                artistas.setIdArtista(IdArtistaBuscado.get(j).toString());
                artistas.setUrlFoto(FotoAlbunArtista.get(j).toString());

                artistas.setNombreTitulo(ArtistasBuscados.get(j).toString());

                dataArtistas.add(artistas);

            }

            new DownloadImage().execute();

            //DescargaFotos();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // Descargar Imagenes
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        String URL_FINAL = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(Principal.this);
            dialog.setTitle("Obteniendo Artistas");
            dialog.setMessage("Cargando...");
            dialog.setIndeterminate(false);
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            Bitmap bitmap = null;

            for(int k = 0; k<FotoAlbunArtista.size(); k++){

                if (!FotoAlbunArtista.get(k).toString().equals("sinfoto")) {

                    URL_FINAL = FotoAlbunArtista.get(k).toString();

                }else{
                    //URL_FINAL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQPTulhh3ajvGx0g3Et6nXbl5oqn-QWScGSfkVwCE_3UBjg5mCMSyvNJPo";
                    URL_FINAL = "http://www.brightlightfoundation.org/recipient_images/no-image.png";
                }

                try {
                    // Download Image from URL
                    InputStream input = new java.net.URL(URL_FINAL).openStream();
                    // Decode Bitmap
                    bitmap = BitmapFactory.decodeStream(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //File image = new File(dir, "artista_" + ArtistasBuscados.get(k).toString() + ".jpg");
                File image = new File(dir, "artista_" + ArtistasBuscados.get(k).toString().substring(0,3)+ k + ".jpg");

                // Encode the file as a PNG image.
                FileOutputStream outStream;
                try {

                    outStream = new FileOutputStream(image);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                    outStream.flush();
                    outStream.close();
                    //success = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            Log.d("asdasdasdsa","adsadasd");

            adapter = new ListViewAdapter(Principal.this, R.layout.activity_principal, dataArtistas);
            listaArtistas.setAdapter(adapter);

            dialog.dismiss();
        }
    }


    // Adapter de la Lista de Artistas.
    public class ListViewAdapter extends ArrayAdapter<Artistas> {

        private Context activity;
        private List<Artistas> friendList;

        public ListViewAdapter(Context context, int resource, List<Artistas> objects) {
            super(context, resource, objects);
            this.activity = context;
            this.friendList = objects;
        }

        @Override
        public int getCount() {
            return friendList.size();
        }

        @Override
        public Artistas getItem(int position) {
            return friendList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ListViewAdapter.ViewHolder holder;
            final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.elementos_artistas, parent, false);
                holder = new ListViewAdapter.ViewHolder(convertView);
                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {

                                                       //Acá va el codigo

                                                       ArrayList<Artistas> dArtis = new ArrayList<Artistas>();
                                                       dArtis.add(getItem(position));

                                                       for(Artistas ar: dArtis){

                                                           nomArtis = ar.getNombreTitulo();
                                                           idArtis = ar.getIdArtista();
                                                       }

                                                       boolean connected = false;
                                                       ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                                                       if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                                               connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                                           //we are connected to a network
                                                           connected = true;
                                                       }
                                                       else{
                                                           connected = false;
                                                       }

                                                       if(connected){
                                                           PasarDatosABuscar(v);
                                                       }else{
                                                           Toast.makeText(Principal.this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
                                                       }



                                                   }
                                               }
                );
            } else {
                holder = (ListViewAdapter.ViewHolder) convertView.getTag();
                convertView = inflater.inflate(R.layout.elementos_artistas, parent, false);
                holder = new ListViewAdapter.ViewHolder(convertView);
                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       //Acá va el codigo

                                                       ArrayList<Artistas> dArtis = new ArrayList<Artistas>();
                                                       dArtis.add(getItem(position));

                                                       for(Artistas ar: dArtis){

                                                           nomArtis = ar.getNombreTitulo();
                                                           idArtis = ar.getIdArtista();
                                                       }

                                                       boolean connected = false;
                                                       ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                                                       if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                                               connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                                           //we are connected to a network
                                                           connected = true;
                                                       }
                                                       else{
                                                           connected = false;
                                                       }

                                                       if(connected){
                                                           PasarDatosABuscar(v);
                                                       }else{
                                                           Toast.makeText(Principal.this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
                                                       }



                                                   }
                                               }
                );
            }

            holder.nombre_artista.setText(getItem(position).getNombreTitulo());
            //holder.id_artista.setText(getItem(position).getIdArtista());

            String nombreDeFotoTemp = getItem(position).getNombreArtista();
            Log.d("NOMBRE ART TEMP", "NOMBRE ARTISTA TEMO: " + nombreDeFotoTemp.toString());

            try{

                File imgFile = new  File("/sdcard/Images/test_image.jpg");


                Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/ArtistaBuscado/artista_" + getItem(position).getNombreArtista() +".jpg");
                holder.foto_artista.setImageBitmap(bmp);
            }catch (Exception e){
                e.printStackTrace();
            }




            return convertView;
        }

        private class ViewHolder {

            private TextView nombre_artista;
            private TextView id_artista;
            private ImageView foto_artista;

            public ViewHolder(View v) {

                nombre_artista = (TextView) v.findViewById(R.id.nombre_artista);
                id_artista = (TextView) v.findViewById(R.id.id_artista);
                foto_artista = (ImageView) v.findViewById(R.id.foto_artista_lista);

            }
        }
    }

    private void PasarDatosABuscar(View v){


        Intent intent = new Intent(Principal.this, AlbumsArtista.class);
        intent.putExtra("NombreArtista", nomArtis);
        intent.putExtra("IdArtista", idArtis);
        startActivity(intent);

    }



    //Permisos para Android Superior a 6
    public boolean checkAndRequestPermissions() {

        int permissionPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionFineLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionReadExternal = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }// <uses-permission android:name="android.permission.ACCESS_COARSE_UPDATES" />
        if (permissionReadExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            Log.d("shiet","shiet");

            return false;

        }

        return true;
    }


}
