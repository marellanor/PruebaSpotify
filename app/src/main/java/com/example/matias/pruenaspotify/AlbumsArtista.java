package com.example.matias.pruenaspotify;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Objetos.AlbunArtista;
import Objetos.Artistas;

/**
 * Created by Matias on 18/05/2017.
 */

public class AlbumsArtista  extends AppCompatActivity {

    AlbunArtista Albuns;
    ArrayList<AlbunArtista> dataAlbuns;

    String NomArtis, IdArtis;

    TextView nom_ar, id_ar;

    private String URL_BUSCAR_ALBUMS_1 = "https://api.spotify.com/v1/artists/";
    private String URL_BUSCAR_ALBUMS_2 = "/albums?market=ES&album_type=single&limit=2";

    File filepath = Environment.getExternalStorageDirectory();
    File dir = new File(filepath.getAbsolutePath() + "/AlbunesArtista");

    private RequestQueue requestQueue;
    private StringRequest StringRequestQueue;

    Context mContext;

    ArrayList<String> NombreAlbunes = new ArrayList<>();
    ArrayList<String> FotosAlbunes = new ArrayList<>();
    ArrayList<String> IdsAlbuns =  new ArrayList<>();

    ProgressDialog dialog;

    ListView listaAlbunes;

    ListAdapter adapter;

    Button agregar_Favoritos;


    ArrayList<String> NombresArtistas_favoritos = new ArrayList<>();
    ArrayList<String> NombresFotos_favoritos = new ArrayList<>();
    ArrayList<String> IdsArtistas_favoritos =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albuns_artista);

        mContext = this;
        dataAlbuns = new ArrayList<>();

        dir.mkdirs();

        // Crear nueva cola de peticiones
        requestQueue= Volley.newRequestQueue(mContext);

        try{
            NomArtis = getIntent().getStringExtra("NombreArtista");
            IdArtis = getIntent().getStringExtra("IdArtista");
            Log.d("ID ARTISTA","ID DEL ARTISTA: " + IdArtis.toString());

            nom_ar.setText(NomArtis);
        }catch (Exception e){
            e.printStackTrace();
        }


        nom_ar = (TextView) findViewById(R.id.nombre_artista);
        id_ar = (TextView) findViewById(R.id.id_artista);

        listaAlbunes = (ListView) findViewById(R.id.lista_albunes);


        agregar_Favoritos = (Button) findViewById(R.id.agregar_favoritos);

        agregar_Favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Nombre Ar","Nombre Artista: " + NombresArtistas_favoritos.toString());
                Log.d("NOMBRE FO","NOMBRE FOTO FAVORITO: " + NombresFotos_favoritos.toString());
                Log.d("ID FAVO","ID FAVORITOS: " + IdsArtistas_favoritos.toString() );

                String NombAlbunes_favoritos = "";
                String NombFotos_favoritos = "";
                String IdsAlbunes_favoritos = "";


                for(int i = 0; i<NombresArtistas_favoritos.size(); i++){

                    NombAlbunes_favoritos = NombAlbunes_favoritos + NombresArtistas_favoritos.get(i).toString() + ",";
                    NombFotos_favoritos = NombFotos_favoritos + NombresFotos_favoritos.get(i).toString() + ",";
                    IdsAlbunes_favoritos = IdsAlbunes_favoritos + IdsArtistas_favoritos.get(i).toString() + ",";

                }

                NombAlbunes_favoritos = NombAlbunes_favoritos.substring(0,NombAlbunes_favoritos.length() - 1);
                NombFotos_favoritos = NombFotos_favoritos.substring(0, NombFotos_favoritos.length() - 1);
                IdsAlbunes_favoritos = IdsAlbunes_favoritos.substring(0, IdsAlbunes_favoritos.length() -1);

                Log.d("Nomb Albun","Nomb Album Guardado: " + NombAlbunes_favoritos.toString());
                Log.d("Nomb Fo","Nomb Foto Guardado: " + NombFotos_favoritos.toString());
                Log.d("Id Albu","Id Albuns Guardado " + IdsAlbunes_favoritos.toString());


                //Genero un Shared Preferences para guardar los valores de los Albunes Favoritos.
                SharedPreferences prefs = getSharedPreferences("AlbunsFavoritos",Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("EstadoFavorito", "ok");
                editor.putString("NombresAlbun",NombAlbunes_favoritos );
                editor.putString("NombresFotoAlbun", NombFotos_favoritos);
                editor.putString("IdsAlbun", IdsAlbunes_favoritos);
                editor.putString("NombreArtista", NomArtis);
                editor.commit();

                AlbumsArtista.this.finish();

            }
        });

        try{
            String estadoFavorito = getIntent().getStringExtra("EstadoFavorito");

            if(estadoFavorito != null){

                SharedPreferences prefs = getSharedPreferences("AlbunsFavoritos",Context.MODE_PRIVATE);

                String nom_alb = prefs.getString("NombresAlbun", "nok");
                String nom_foto_alb = prefs.getString("NombresFotoAlbun", "nok");
                String id_foto_alb = prefs.getString("IdsAlbun", "nok");
                String nom_artista = prefs.getString("NombreArtista", "NombreArtista");

                nom_ar.setText(nom_artista);

                String[] temp1 = nom_alb.split(",");
                String[] temp2 = nom_foto_alb.split(",");
                String[] temp3 = id_foto_alb.split(",");

                Log.d("TEMP1","TEMP1 : " + nom_alb.toString());
                Log.d("TEMP2","TEMP2 : " + nom_foto_alb.toString());
                Log.d("TEMP3","TEMP3 : " + id_foto_alb.toString());

                dataAlbuns = new ArrayList<>();
                for(int x = 0; x<temp1.length; x++){

                    Albuns = new AlbunArtista();

                    Albuns.setNombreTitulo(temp1[x].toString());
                    Albuns.setNombreAlbum(temp2[x].toString());
                    Albuns.setIdAlbum(temp3[x].toString());
                    Albuns.setUrlAlbun(temp2[x].toString());

                    dataAlbuns.add(Albuns);
                }

                adapter = new ListViewAdapter(AlbumsArtista.this, R.layout.activity_principal, dataAlbuns);
                listaAlbunes.setAdapter(adapter);



            }else{

                ObtieneAlbunesArtista(IdArtis);
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    //Clase Volley que obtiene los albunes correspondiente al Artista seleccionado.
    private void ObtieneAlbunesArtista(String IdArtista){

        String URL_BUSCAR = URL_BUSCAR_ALBUMS_1 + IdArtista + URL_BUSCAR_ALBUMS_2;


        StringRequestQueue = new StringRequest(Request.Method.GET, URL_BUSCAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("respo albums", "respusta albums: " + response);

                        ObtieneDatos(response);

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Error", error.toString());

            }

        });

        //Agrego la solicitud a la cola.
        requestQueue.add(StringRequestQueue);

    }

    //Parseo el Json que devuelve la clase Volley.
    private void  ObtieneDatos(String jsonString){

        try {
            JSONObject json = new JSONObject(jsonString);

            JSONArray items = json.getJSONArray("items");

            for(int i = 0; i<items.length(); i++){

                JSONObject temp_json = items.getJSONObject(i);

                //Obtengo del nombre del Album segun el tag 'name'
                NombreAlbunes.add(temp_json.getString("name"));
                //Obtengo el Id del Albun segun el tag 'id'
                IdsAlbuns.add(temp_json.getString("id"));

                //Entro al Array de Imagenes para obtener la de menor resolucion.
                JSONArray temp_jsonarray = temp_json.getJSONArray("images");
                if(temp_jsonarray != null && temp_jsonarray.length() > 0){

                    JSONObject imagen_temp = temp_jsonarray.getJSONObject(2);
                    FotosAlbunes.add(imagen_temp.getString("url"));
                }else{

                    //Debo posicionar la foto sin imagen.
                    FotosAlbunes.add("sinfoto");

                }
            }

            Log.d("NOM ALBU","NOMBRES ALBUNES ARRAY: " + NombreAlbunes.toString());
            Log.d("URL FOTOS","URL FOTOS ARRAY: " + FotosAlbunes.toString());


            //Seteo los objetos en la Clase AlbunArtista
            for(int j=0; j<NombreAlbunes.size(); j++ ){

                Albuns = new AlbunArtista();

                Albuns.setNombreAlbum(NombreAlbunes.get(j).toString().substring(0,3) + j);
                Albuns.setUrlAlbun(FotosAlbunes.get(j).toString());
                Albuns.setIdAlbum(IdsAlbuns.get(j).toString());

                Albuns.setNombreTitulo(NombreAlbunes.get(j).toString());

                dataAlbuns.add(Albuns);

            }

            new DownloadImage().execute();


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

            dialog = new ProgressDialog(AlbumsArtista.this);
            dialog.setTitle("Obteniendo Albunes");
            dialog.setMessage("Cargando...");
            dialog.setIndeterminate(false);
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            Bitmap bitmap = null;

            for(int k = 0; k<FotosAlbunes.size(); k++){

                if (!FotosAlbunes.get(k).toString().equals("sinfoto")) {

                    URL_FINAL = FotosAlbunes.get(k).toString();

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
                File image = new File(dir, "album_" + NombreAlbunes.get(k).toString().substring(0,3)+ k + ".jpg");

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

            adapter = new ListViewAdapter(AlbumsArtista.this, R.layout.activity_principal, dataAlbuns);
            listaAlbunes.setAdapter(adapter);

            dialog.dismiss();
        }
    }


    // Adapter de la Lista de Artistas.
    public class ListViewAdapter extends ArrayAdapter<AlbunArtista> {

        private Context activity;
        private List<AlbunArtista> friendList;

        public ListViewAdapter(Context context, int resource, List<AlbunArtista> objects) {
            super(context, resource, objects);
            this.activity = context;
            this.friendList = objects;
        }

        @Override
        public int getCount() {
            return friendList.size();
        }

        @Override
        public AlbunArtista getItem(int position) {
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
                convertView = inflater.inflate(R.layout.elementos_albunes, parent, false);
                holder = new ListViewAdapter.ViewHolder(convertView);
                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {

                                                       //Acá va el codigo


                                                   }
                                               }
                );
            } else {
                holder = (ListViewAdapter.ViewHolder) convertView.getTag();
                convertView = inflater.inflate(R.layout.elementos_albunes, parent, false);
                holder = new ListViewAdapter.ViewHolder(convertView);
                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       //Acá va el codigo


                                                   }
                                               }
                );
            }

            holder.nombre_artista.setText(getItem(position).getNombreTitulo());

            String nombreDeFotoTemp = getItem(position).getUrlAlbun();
            Log.d("NOMBRE ART TEMP", "NOMBRE ARTISTA TEMO: " + nombreDeFotoTemp.toString());

            try{

                Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/AlbunesArtista/album_" + getItem(position).getNombreAlbum() +".jpg");
                holder.foto_artista.setImageBitmap(bmp);
            }catch (Exception e){
                e.printStackTrace();
            }

            holder.check_albun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){

                        //Guardo los chequeados
                        //Toast.makeText(AlbumsArtista.this, "check: " + getItem(position).getIdAlbum().toString(), Toast.LENGTH_SHORT).show();

                        NombresArtistas_favoritos.add(getItem(position).getNombreTitulo().toString());
                        NombresFotos_favoritos.add(getItem(position).getNombreAlbum().toString());
                        IdsArtistas_favoritos.add(getItem(position).getIdAlbum().toString());

                    }else{

                        //Elimino los que fueron descheckeados
                        NombresArtistas_favoritos.remove(getItem(position).getNombreTitulo().toString());
                        NombresFotos_favoritos.remove(getItem(position).getNombreAlbum().toString());
                        IdsArtistas_favoritos.remove(getItem(position).getIdAlbum().toString());

                    }

                }
            });

            return convertView;
        }

        private class ViewHolder {

            private TextView nombre_artista;
            private TextView id_artista;
            private ImageView foto_artista;

            private CheckBox check_albun;

            public ViewHolder(View v) {

                nombre_artista = (TextView) v.findViewById(R.id.nombre_artista);
                id_artista = (TextView) v.findViewById(R.id.id_artista);
                foto_artista = (ImageView) v.findViewById(R.id.foto_artista_lista);
                check_albun = (CheckBox) v.findViewById(R.id.check_albun);

            }
        }
    }

}
