package com.example.thehollowbar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AskFor extends AppCompatActivity {

    private TextView productQuantity;
    private int quantity = 0;
    private ImageButton imageButtonCart;
    private ImageView imgProduct;
    private ImageView imgFlyingProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for);

        productQuantity = findViewById(R.id.productQuantity);
        Button buttonAdd = findViewById(R.id.btnAddition);
        Button buttonSubtract = findViewById(R.id.btnSubtraction);
        Button buttonAddToCart = findViewById(R.id.buttonNextClient3);
        imageButtonCart = findViewById(R.id.imageButtonCart);
        imgProduct = findViewById(R.id.imgProduct);

        imgFlyingProduct = findViewById(R.id.imgFlyingObject);
        imgFlyingProduct.setVisibility(View.INVISIBLE);

        buttonAdd.setOnClickListener(v -> add());
        buttonSubtract.setOnClickListener(v -> subtract());

        buttonAddToCart.setOnClickListener(v -> animateProductToCart());
    }

    private void add() {
        quantity++;
        updateQuantity();
    }

    private void subtract() {
        if (quantity > 1) {
            quantity--;
        }
        updateQuantity();
    }

    private void updateQuantity() {
        productQuantity.setText(String.valueOf(quantity));
    }

    private void animateProductToCart() {
        imgFlyingProduct.setVisibility(View.VISIBLE);

        int[] cartLocation = new int[2];
        imageButtonCart.getLocationInWindow(cartLocation);

        imgFlyingProduct.setTranslationX(0);
        imgFlyingProduct.setTranslationY(0);

        float deltaX = cartLocation[0] - imgFlyingProduct.getWidth() / 2 + imageButtonCart.getWidth() / 2;
        float deltaY = cartLocation[1] - imgFlyingProduct.getHeight() / 2 + imageButtonCart.getHeight() / 2;

        ObjectAnimator moveToCartX = ObjectAnimator.ofFloat(imgFlyingProduct, View.TRANSLATION_X, deltaX);
        ObjectAnimator moveToCartY = ObjectAnimator.ofFloat(imgFlyingProduct, View.TRANSLATION_Y, deltaY);

        moveToCartX.setDuration(3000);
        moveToCartY.setDuration(3000);
        moveToCartX.setInterpolator(new DecelerateInterpolator());
        moveToCartY.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveToCartX, moveToCartY);
        animatorSet.start();

        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                imgFlyingProduct.setVisibility(View.INVISIBLE);
            }
        });
    }

}
