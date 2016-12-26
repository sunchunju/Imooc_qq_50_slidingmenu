package com.imooc.slidingmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.imooc.slidingmenu.com.imooc.slidingmenu.view.SlidingMenu;

public class MainActivity extends AppCompatActivity {

    SlidingMenu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (SlidingMenu) findViewById(R.id.slidemenu);
    }

    public void toggleMenu(View view){
        menu.toggle();
    }
}
