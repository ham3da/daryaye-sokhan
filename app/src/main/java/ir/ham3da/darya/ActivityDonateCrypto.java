package ir.ham3da.darya;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivityDonateCrypto extends AppCompatActivity
{

    UtilFunctions UtilFunctions1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this, true);
        setContentView(R.layout.activity_donate_crypto);
        UtilFunctions1 = new UtilFunctions(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.donate_with_crypto);

        ImageButton address_btc_copy = findViewById(R.id.address_btc_copy);
        TextView address_btc_txt = findViewById(R.id.address_btc_txt);


        ImageButton address_eth_copy = findViewById(R.id.address_eth_copy);
        TextView address_eth_txt = findViewById(R.id.address_eth_txt);

        ImageButton address_perfect_copy = findViewById(R.id.address_perfect_copy);
        TextView address_perfect_txt = findViewById(R.id.address_perfect_txt);


        address_btc_copy.setOnClickListener(v -> {
            UtilFunctions1.copyText(address_btc_txt.getText().toString());
            Toast.makeText(this, R.string.btc_address_copied, Toast.LENGTH_SHORT).show();
        });

        address_eth_copy.setOnClickListener(v -> {
            UtilFunctions1.copyText(address_eth_txt.getText().toString());
            Toast.makeText(this, R.string.eth_address_copied, Toast.LENGTH_SHORT).show();
        });

        address_perfect_copy.setOnClickListener(v -> {
            UtilFunctions1.copyText(address_perfect_txt.getText().toString());
            Toast.makeText(this, R.string.account_copied, Toast.LENGTH_SHORT).show();
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideDown(this); //fire the slide left animation
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            Bungee.slideDown(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }
}