package Objetos;

import java.io.Serializable;

/**
 * Created by Matias on 19/05/2017.
 */

public class AlbunArtista implements Serializable {


    private String NombreAlbum;
    private String UrlAlbun;
    private String IdAlbum;

    private String NombreTitulo;


    public AlbunArtista(){

    }

    public AlbunArtista(String nombrealbum, String urlalbum, String idalbum, String nombretitulo){

        this.NombreAlbum = nombrealbum;
        this.UrlAlbun = urlalbum;
        this.IdAlbum = idalbum;


        this.NombreTitulo = nombretitulo;
    }

    public String getNombreAlbum() {
        return NombreAlbum;
    }

    public void setNombreAlbum(String nombreAlbum) {
        NombreAlbum = nombreAlbum;
    }

    public String getUrlAlbun() {
        return UrlAlbun;
    }

    public void setUrlAlbun(String urlAlbun) {
        UrlAlbun = urlAlbun;
    }

    public String getIdAlbum() {
        return IdAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        IdAlbum = idAlbum;
    }


    public String getNombreTitulo() {
        return NombreTitulo;
    }

    public void setNombreTitulo(String nombreTitulo) {
        NombreTitulo = nombreTitulo;
    }
}
