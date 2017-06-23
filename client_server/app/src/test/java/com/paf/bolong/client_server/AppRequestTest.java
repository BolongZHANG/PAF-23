package com.paf.bolong.client_server;

import org.junit.Test;

public class AppRequestTest {
    @Test
    public void json() throws Exception {
        System.out.print(AppRequest.jsonVolumes(48.826891, 2.346764, 48.825856, 2.345192));
    }
}

