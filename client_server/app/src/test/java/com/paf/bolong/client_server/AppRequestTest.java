package com.paf.bolong.client_server;

import org.junit.Test;

public class AppRequestTest {
    @Test
    public void json() throws Exception {
        System.out.print(AppRequest.json("48.82628", "2.34519", "100"));
    }
}

