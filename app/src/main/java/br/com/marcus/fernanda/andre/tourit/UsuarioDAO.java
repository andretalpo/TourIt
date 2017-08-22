package br.com.marcus.fernanda.andre.tourit;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by André on 22/08/2017.
 */

public class UsuarioDAO {

    private DatabaseReference database;
    private Usuario usuario;

    public UsuarioDAO(String idUsuario){//passar duas strings para deixar busca universal
        this.database = FirebaseDatabase.getInstance().getReference();
//        database.child("Usuarios").orderByChild("idGoogle").equalTo(idUsuario).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                    usuario = childSnapshot.getValue(Usuario.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //Sei la
//            }
//        });
    }

    public Usuario consultarUsuario(){
        database.child("Usuarios").orderByChild("idGoogle").equalTo("jtVK0vfoPeOcf0uCgMI5CQRdel92").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    usuario = childSnapshot.getValue(Usuario.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Sei la
            }
        });
        return usuario;
    }
}
