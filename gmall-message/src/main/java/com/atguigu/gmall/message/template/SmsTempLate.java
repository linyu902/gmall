package com.atguigu.gmall.message.template;

import com.atguigu.gmall.message.utils.HttpUtils;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class SmsTempLate {
//	@Value("${gmall.host}")
	String host = "http://dingxin.market.alicloudapi.com";
//	@Value("${gmall.path}")
	String path = "/dx/sendSms";
//	@Value("${gmall.method}")
	String method = "POST";
//	@Value("${gmall.appcode}")
	String appcode = "d8795f7a8bcb4fe79f2683b59ac70c5b";
	public boolean sendMesage(String phoneNum,String code) {
		//System.err.println(host);
		Map<String, String> headers = new HashMap<String, String>();
		// 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", phoneNum);
		querys.put("param", "code:"+code);
		querys.put("tpl_id", "TP1711063");
		Map<String, String> bodys = new HashMap<String, String>();

		try {
			/**
			 * 重要提示如下: HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 */
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
			
			//System.out.println(response.toString());
			// 获取response的body
			System.out.println(EntityUtils.toString(response.getEntity()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
}