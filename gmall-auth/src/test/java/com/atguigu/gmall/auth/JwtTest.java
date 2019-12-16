package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
	private static final String pubKeyPath = "D:\\ideaworkspace\\rsa\\rsa.pub";

    private static final String priKeyPath = "D:\\ideaworkspace\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJsaW5neXUiLCJleHAiOjE1NzY1MDMwMTh9.agb5g-oAbyl_jAlffu18tIdc8sSO2mnMmbxbEk2ZAmIqkQ7Medr3KUgkROEnm4twGrSOTvEMP9kT4bDNZ5ovmoz_H2IJ8SiwL5TkOBzi9fDYfbebZhFfh4lofxZrqaQfatQG1699c5f2xxoAg_E9SZ1ZHA7L333Hjc1lnQpZ4Wrw9PZPb4sHgPU8aSBTCveQ84kIxMkoIhNR9E0Xd7U1rN9TrueX3nqFhwox1S5McS5ZfltY_Z4xvs4v3t799gteHH6Z7lxiOqRxl2BVkUqc0g4MffyNPBwCIXdw6TX8Bt0zqdb7JH2iuxUSi83S6E1StjqQ99V-qyRzMYk27Mynrg";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}