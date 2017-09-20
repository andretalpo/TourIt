package br.com.marcus.fernanda.andre.tourit.local.controler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;

public class LocalDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_local_details);
        setSupportActionBar(toolbar);

        Local local = (Local) getIntent().getSerializableExtra("local");
        setTitle(local.getNome());

        //Conversão de byte array para bitmap, depois para bitmap drawble(Para que seja possível aplicar no layout)
        byte[] arrayFoto = getIntent().getByteArrayExtra("arrayFoto");
        Bitmap bmp = BitmapFactory.decodeByteArray(arrayFoto, 0, arrayFoto.length);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_local_details);
        toolbarLayout.setBackground(bitmapDrawable);

        TextView enderecoTextView = (TextView) findViewById(R.id.enderecoLocalDetailsTextView);
        TextView tipoTextView = (TextView) findViewById(R.id.tipoLocalDetailsTextView);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.localDetailsRatingBar);

        enderecoTextView.setText(local.getEndereco());
        ratingBar.setRating(local.getNota());
        for (String tipo : local.getTipo()) {
            tipoTextView.append(tipo + " ");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.adicionarLocalDetailsFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
