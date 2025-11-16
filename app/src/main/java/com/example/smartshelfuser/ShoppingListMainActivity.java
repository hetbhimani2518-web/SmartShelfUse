package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

public class ShoppingListMainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialButton buttonShowShoppingList, buttonShowRowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_list_main);

        toolbar = findViewById(R.id.toolbarShoppingList);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Shopping Lists");
        }

        buttonShowShoppingList = findViewById(R.id.buttonShowShoppingList);
        buttonShowRowList = findViewById(R.id.buttonShowRowList);

        setupComponentSwitchListeners();

        if (savedInstanceState == null) {
            replaceFragment(ShoppingListComponentFragment.newInstance());
            updateButtonStyles(buttonShowShoppingList);
        }
    }

    private void setupComponentSwitchListeners() {
        buttonShowShoppingList.setOnClickListener(v -> {
            replaceFragment(ShoppingListComponentFragment.newInstance());
            updateButtonStyles(buttonShowShoppingList);
        });

        buttonShowRowList.setOnClickListener(v -> {
            replaceFragment(RowListComponentFragment.newInstance());
            updateButtonStyles(buttonShowRowList);
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_shopping_components, fragment);
        fragmentTransaction.commit();
    }

    private void updateButtonStyles(MaterialButton clickedButton) {

        setToOutlined(buttonShowShoppingList);
        setToOutlined(buttonShowRowList);

        setToFilled(clickedButton);
    }

    private void setToOutlined(MaterialButton button) {

//        button.setStrokeColorResource(R.color.colorAccent);
//        button.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.button_outline_stroke_width));
        button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.textColor));
        button.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private void setToFilled(MaterialButton button) {
        button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
        button.setTextColor(ContextCompat.getColor(this, R.color.textColor));
//        button.setStrokeWidth(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
