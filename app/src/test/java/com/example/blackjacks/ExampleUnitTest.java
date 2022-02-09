package com.example.blackjacks;

import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;

import com.example.blackjacks.ui.*;
import com.example.blackjacks.ui.login.LoginActivity;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testLogin(){
        Intent i = new Intent(String.valueOf(LoginActivity.class));
        //startActivity(i);
        //new LoginActivity();
    }
}