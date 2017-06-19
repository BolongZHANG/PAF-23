package com.paf.bolong.client_server;

import org.junit.Test;

public class AppRequestTest {
    @Test
    public void json() throws Exception {
        System.out.print(AppRequest.json("48.8520930694", "2.34738897095", "100"));
    }
}

