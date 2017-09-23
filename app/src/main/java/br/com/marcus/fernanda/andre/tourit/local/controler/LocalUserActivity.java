package br.com.marcus.fernanda.andre.tourit.local.controler;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import br.com.marcus.fernanda.andre.tourit.R;

public class LocalUserActivity extends AppCompatActivity {

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_user);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalListFragment localFragment = new LocalListFragment();

        transaction.replace(R.id.localFragmentContentLocalUserActivity, localFragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout = (FrameLayout) findViewById(R.id.localFragmentContentLocalUserActivity);
        frameLayout.invalidate();
        frameLayout.requestLayout();
    }
}
