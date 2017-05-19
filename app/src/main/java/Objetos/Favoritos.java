package Objetos;

import java.io.Serializable;

/**
 * Created by Matias on 19/05/2017.
 */

public class Favoritos implements Serializable {


    private String NombreAlbun;
    private String NombreFoto;
    private String IdAlbun;


    public Favoritos(){

    }

    public Favoritos(String nombrealbun, String nombrefoto, String idalbum){

        this.NombreAlbun = nombrealbun;
        this.NombreFoto = nombrefoto;
        this.IdAlbun = idalbum;
    }


    public String getNombreAlbun() {
        return NombreAlbun;
    }

    public void setNombreAlbun(String nombreAlbun) {
        NombreAlbun = nombreAlbun;
    }

    public String getNombreFoto() {
        return NombreFoto;
    }

    public void setNombreFoto(String nombreFoto) {
        NombreFoto = nombreFoto;
    }

    public String getIdAlbun() {
        return IdAlbun;
    }

    public void setIdAlbun(String idAlbun) {
        IdAlbun = idAlbun;
    }
}
