package br.com.marcus.fernanda.andre.tourit.local.controler;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.dao.LocalDAO;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

public class LocalDetailsActivity extends AppCompatActivity {

    private Local local;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_local_details);
        setSupportActionBar(toolbar);

        inicializarBotaoAdicionar();

        local = (Local) getIntent().getSerializableExtra("local");
        setTitle(local.getNome());

        if(new LocalDAO(this, MainActivity.idUsuarioGoogle).buscarLocal(local.getIdPlaces()) != null){
            inicializarBotaoExcluir();
        }

        //Conversão de byte array para bitmap, depois para bitmap drawble(Para que seja possível aplicar no layout)
        Bitmap bmp = ImageConverter.convertByteToBitmap(getIntent().getByteArrayExtra("arrayFoto"));
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_local_details);
        toolbarLayout.setBackground(bitmapDrawable);

        local.setFoto(bmp);

        TextView enderecoTextView = (TextView) findViewById(R.id.enderecoLocalDetailsTextView);
        TextView tipoTextView = (TextView) findViewById(R.id.tipoLocalDetailsTextView);
        TextView notaTextView = (TextView) findViewById(R.id.notaLocalDetailsTextView);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.localDetailsRatingBar);

        enderecoTextView.setText(local.getEndereco());
        notaTextView.setText(String.valueOf(local.getNota()));
        ratingBar.setRating(local.getNota());
        for (String tipo : local.getTipo()) {
            tipoTextView.append(tipo + " ");
        }
    }

    private void inicializarBotaoExcluir() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.adicionarLocalDetailsFab);
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete, null));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cor_vermelho)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LocalDAO(LocalDetailsActivity.this, MainActivity.idUsuarioGoogle).deleteLocal(local.getIdPlaces());
                inicializarBotaoAdicionar();
                Toast.makeText(LocalDetailsActivity.this, "Local excluído com sucesso.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inicializarBotaoAdicionar() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.adicionarLocalDetailsFab);
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, null));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LocalDAO(LocalDetailsActivity.this, MainActivity.idUsuarioGoogle).inserirLocalSQLite(local);
                inicializarBotaoExcluir();
                Toast.makeText(LocalDetailsActivity.this, "Local adicionado com sucesso.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
