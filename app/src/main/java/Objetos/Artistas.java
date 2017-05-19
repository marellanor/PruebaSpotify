package Objetos;

import java.io.Serializable;

/**
 * Created by Matias on 17/05/2017.
 */

public class Artistas implements Serializable{



    private String NombreArtista;
    private String IdArtista;
    private String UrlFoto;

    private String NombreTitulo;



    public Artistas(){

    }

    public Artistas(String nombreartista, String idartista, String urlfoto, String nombretitulo ){


        this.NombreArtista = nombreartista;
        this.IdArtista = idartista;
        this.UrlFoto = urlfoto;

        this.NombreTitulo = nombretitulo;

    }


    public String getNombreArtista() {
        return NombreArtista;
    }

    public void setNombreArtista(String nombreArtista) {
        NombreArtista = nombreArtista;
    }

    public String getIdArtista() {
        return IdArtista;
    }

    public void setIdArtista(String idArtista) {
        IdArtista = idArtista;
    }

    public String getUrlFoto() {
        return UrlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        UrlFoto = urlFoto;
    }


    public String getNombreTitulo() {
        return NombreTitulo;
    }

    public void setNombreTitulo(String nombreTitulo) {
        NombreTitulo = nombreTitulo;
    }
}
