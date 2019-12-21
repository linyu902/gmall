package com.atguigu.gmall.order.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id;

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCYpo4G+xmcwACzJnngdpEJJynyqPBChgZiwquXRiR6y4HT4NQAsEWvf64i+mWhflRGvqpj0sX/sEftF/a9sLP4CIHfZtCsS0ZQSScwWWPKQ1Dwy2RvhvVnegQWuFEqSvcErjS/mjAklCfGo//2oco96SUla3Tf7q5wf4asQ7fKoInJka+ZZVi/cTJusyS8cFjBsO7/2TmzNetiGNTsU4kaTqEC2quGFcLbADurspOkqjaY5qujmfExKuU1KSjUw/todZcRBzKv1kc4wQiwoGL0UhTmSJrDVpK1xjiYa5cJpQoDrECWSkYp0zxp828UkFcexA1xs6WcZe5YjlV93RLNAgMBAAECggEBAIwNQ4FkH7uQ7RNw2aD3J1oedH4OQWDHVlSGBqhj0lRXImYigF+hWk4J9tpgoZ3pKak+hVXQq5hLQ7Jjqh2fYX7PT1iIHCv3ZCuE8k+Js+R+nmRON3ebesvVRqsRsGX9IWVJ71tbO5BT3aqCDPVauxLSNSJ/1FsfwSu11Pl3p8he1wAkVIJc8mrwPqsp36d+7KU8dmaPHWJoZgohzJ6jLOv6sNoD9lU4rIE8C7dfIYdXh7P5l28fQZX4tz0H3PqVUAMUPPZpwTYzuIop9lYDZuWE168VeLoG0JdvjVJ7OdqmfjZYGqhw5F1dAkCG04y1QjL10RT6arH+hy+3DzOplIECgYEAzfeJx8LiOnvaBwAaG5laSvGxheFgvsZAIeSFNn7hu/a81qKW05XfCe6hsindUOk3fohwV17byyfEFXR3IPFLGRgJtVO3Gc4zfSjfP7lwYsWRn6fGu6fipfOocgSb4EvfS1wFyUvHpfqXyouiSa7doGGZ9zqk49SFN5EZDGDmqZcCgYEAvbtvOdPZaFiyYJYSYcHQnuSq9W+l1QsubZWFoA+BjmcdC0aQEd7Y2AM4viaBF8nIfOLQvujRj2XvRTzQ+8CnBnNq7YwZxaMlysCT/QKolrFbyVt4kuGwGVErpvXepePrPetEkkPJAjfyJdsCLRR1v/frQv4yC5JLjqnDxBg5izsCgYBTKrQgzGmLEf2cvAbOxCJ4hpWXKMVm4yh8RwkfUyaVPnFL/cUHVMoF+ayjA1KRXEqDNlaegPghVUJRI4EC9UvMqy8oX9BchC24SSu0UI235G0D2bxeg9D6D6QhvgivDhPxGwxfFtFcDQ8Dw1RZilmFbEO+V5jDQko0gMtreylIqwKBgQCuWo1WxdCXLFMXoTFZNmOBcL+N9kCda74JpAr80OMMusMc9oY0deYtc6B0VSvycVLVORX9KjRRo6nipYWipCKZMf1EOKgCT+/Nx3T3X7cPbALeIjnGOLG8QjRsJKurJKyr5QTJqJMSf9j9Yqo4RQIEHE7M7I2Oxqy6eFVXYa+XuwKBgQCpfBZtz3WUzkBwcyHptfrhBX3E/sIJYOjl6nYPTpMlWI4JQpPj48mgw9r86MbXoWkYdE1HAusEK0RebQKybmx2xJtb3LZjQmSBfUvn9LVVwoCc/F6WFQevCYCMTVg/nmEP3Rk7Y5TWUtkYupfzmX5OGS+AN66t1s8Cff+rki35Ug==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqvLDnhWvysSJrfRtyF/RKnhZ9JeRXimEkzgdtW08HrmwobNAjbYFJbss+iFDb2wklos76fdf8JJ41hasJAZ6gqhP3u+ElxXdBW9XnnL6EHZv8J0UKZ/aFgpXOMsGkiINxqfn26zLwD4SGPk2KBjYxmxmiuFrcB+vEaY9eeJraMCI9HLAFLMqHG6cgKxuMcdnKj34yv1EYyAqKr7tJJAvT5ZspotcPgQ0WdvfZlVw3ElXMWnVvnHP0d44vJ41nntw9bB+kyft2Ro1FCKFYrjOem7SAttSxik1Q1BSg4nKP9Ch/GiiYhBDLzTThkpcN7DXBS/lMMHh7DFMCsXyvr67KwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type;

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl;

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
