package com.example.mrt;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mrt.utilities.LRBarcodeDetector;


public class CreatePODActivity extends AppCompatActivity {

    private LRBarcodeDetector lrDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pod);
        lrDetector = new LRBarcodeDetector(this);
    }


    public void scanBarCode(View view) {
        final TextView barcodeTextView = findViewById(R.id.barcode_content);
        String lrNO = lrDetector.detectLRNo();
            if (!lrNO.isEmpty()) {
                barcodeTextView.setText("CODE Data: " + lrNO);
            } else {
                barcodeTextView.setText("No Code found!");
                barcodeTextView.setTextColor(Color.RED);
            }
    }
}
