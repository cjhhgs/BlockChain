package com.jhchen.controller;


import com.jhchen.domain.modul.Account;
import com.jhchen.utils.JSONUtil;
import org.junit.Test;

public class test {

    @Test
    public void test(){

        Account account = JSONUtil.loadJSONObject("account.txt", Account.class);
        System.out.println(account);
        String s1 = "{\"body\":[{\"finished\":false,\"publicKey\":\"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEzMSWqIxuUd1JX5pAQfH6J8OQ7gmMJjxxJsZqc54m+Qs/Bv5Zpkzo+hCiBNWw82fzptcSFYZrpYSj3gNEWLCF2w==\",\"sign\":\"MEYCIQCVX3fKzFBmLavyf0TpmwcoMpo6ZyqHn5nv5ke6sCfujgIhAI6oDgElJ2MqVq7/Wc5Q39ytz9BfGeelckw+j9dhdoIa\",\"transaction\":{\"amount\":\"12\",\"receive\":\"81Tb9QqqgRZjAFWKfvRXMDE5rLMdVwXHFW\",\"send\":\"18Tb9QqqgRZjAFWKfvRXMDE5rLMdVwXHFW\"}}],\"nonce\":11707984,\"preID\":\"0\",\"targetBits\":22,\"timestamp\":1677332553}\n";
        String s2 = "{\"body\":[{\"finished\":false,\"publicKey\":\"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEzMSWqIxuUd1JX5pAQfH6J8OQ7gmMJjxxJsZqc54m+Qs/Bv5Zpkzo+hCiBNWw82fzptcSFYZrpYSj3gNEWLCF2w==\",\"sign\":\"MEYCIQCVX3fKzFBmLavyf0TpmwcoMpo6ZyqHn5nv5ke6sCfujgIhAI6oDgElJ2MqVq7/Wc5Q39ytz9BfGeelckw+j9dhdoIa\",\"transaction\":{\"amount\":\"12\",\"receive\":\"81Tb9QqqgRZjAFWKfvRXMDE5rLMdVwXHFW\",\"send\":\"18Tb9QqqgRZjAFWKfvRXMDE5rLMdVwXHFW\"}}],\"iD\":\"b995657884335e7195c16496536184bff05069652c755e231cfbbd2e0b550d85\",\"nonce\":11707984,\"preID\":\"0\",\"targetBits\":22,\"timestamp\":1677332553}\n";
    }

}
