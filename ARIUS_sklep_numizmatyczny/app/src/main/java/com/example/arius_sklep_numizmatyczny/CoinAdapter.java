package com.example.arius_sklep_numizmatyczny;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * Adapter monet
 */
public class CoinAdapter extends ArrayAdapter<String> {
    Context context;

    /**
     * Lista monet
     */
    Coin[] coins;

    CoinAdapter(Context ctx, Coin[] coins, String[] names){
        super(ctx, R.layout.single_coin, R.id.name_text, names);

        this.context = ctx;
        this.coins = coins;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View coin = inflater.inflate(R.layout.single_coin, parent, false);

        ImageView image = coin.findViewById(R.id.coin_image);
        TextView name = coin.findViewById(R.id.name_text);
        TextView year = coin.findViewById(R.id.year_text);
        TextView price = coin.findViewById(R.id.price_text);

        /**
         * Obiekt obecnej monety
         */
        Coin currentCoin = coins[position];

        // Ustaw odpowiedni obrazek na podstawie nazwy pliku
        switch(currentCoin.imageURL){
            case "us.jpg":
                image.setImageResource(R.drawable.us);
                break;
            case "british_queen.jpg":
                image.setImageResource(R.drawable.british_queen);
                break;
            case "australian_kangaroo.jpg":
                image.setImageResource(R.drawable.australian_kangaroo);
                break;
            case "canadian_maple_leaf.jpg":
                image.setImageResource(R.drawable.canadian_maple_leaf);
                break;
            case "chinese_panda.jpg":
                image.setImageResource(R.drawable.chinese_panda);
                break;
            case "mexican_libertad.jpg":
                image.setImageResource(R.drawable.mexican_libertad);
                break;
            default:
                image.setImageResource(R.drawable.missing_texture);
                break;
        }
        name.setText(currentCoin.name);
        year.setText(String.valueOf(currentCoin.yearIssued));
        price.setText(String.format("%.2f", currentCoin.price) + " zł");

        return coin;
    }
}
