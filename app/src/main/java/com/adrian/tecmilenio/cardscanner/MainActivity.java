package com.adrian.tecmilenio.cardscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

public class MainActivity extends Activity {

    protected static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_SCAN = 100;
    private static final int REQUEST_AUTOTEST = 200;

    private TextView mResultLabel;
    private ImageView mResultImage;
    private ImageView mResultCardTypeImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultLabel = (TextView) findViewById(R.id.result);
        mResultImage = (ImageView) findViewById(R.id.result_image);
        mResultCardTypeImage = (ImageView) findViewById(R.id.result_card_type_image);

    }


    public void onScan(View pressed) {
        Intent intent = new Intent(this, CardIOActivity.class)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true)
                .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);

        try {
            int unblurDigits = 4;
            intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurDigits);
        } catch(NumberFormatException ignored) {}

        startActivityForResult(intent, REQUEST_SCAN);
    }

    @Override
    public void onStop() {
        super.onStop();

        mResultLabel.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

        String outStr = "";
        Bitmap cardTypeImage = null;

        if ((requestCode == REQUEST_SCAN || requestCode == REQUEST_AUTOTEST) && data != null
                && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard result = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
            if (result != null) {
                outStr += "Numero de Tarjeta: " + result.getRedactedCardNumber() + "\n";

                CardType cardType = result.getCardType();
                cardTypeImage = cardType.imageBitmap(this);
                outStr += "Tipo: " + cardType.name() + " cardType.getDisplayName(null)="
                        + cardType.getDisplayName(null) + "\n";

                outStr += "Expiración: " + result.expiryMonth + "/" + result.expiryYear + "\n";
                outStr += "CVV: " + result.cvv + "\n";
                outStr += "Codigo Postal: " + result.postalCode + "\n";

                outStr += "Nombre: " + result.cardholderName + "\n";
            }


        }

        Bitmap card = CardIOActivity.getCapturedCardImage(data);
        mResultImage.setImageBitmap(card);
        mResultCardTypeImage.setImageBitmap(cardTypeImage);

        Log.i(TAG, "Set result: " + outStr);
        mResultLabel.setText(outStr);

    }
}
