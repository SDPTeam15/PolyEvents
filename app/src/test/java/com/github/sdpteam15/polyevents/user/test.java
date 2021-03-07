package com.github.sdpteam15.polyevents.user;

import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class test {
    @Test
    public void t(){
        assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt("1a");
        });
    }
}
