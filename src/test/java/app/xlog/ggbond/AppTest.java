package app.xlog.ggbond;


import app.xlog.ggbond.payments.ScanCode.IScanCodeApi;
import app.xlog.ggbond.payments.ScanCode.model.PrePayResponse;
import app.xlog.ggbond.utils.NoVerifySSLClient;
import cn.hutool.crypto.SecureUtil;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;


/**
 * Unit test for simple App.
 */

@SpringBootTest
public class AppTest {
    public AppTest() throws NoSuchAlgorithmException, KeyManagementException {
    }

    private final OkHttpClient client = NoVerifySSLClient.getClient();

    // 易支付获取签名信息
    @Test
    public void testYZFSign() {
        String sign = "clientip=169.254.87.226&money=0.3&name=测试一下&notify_url=https://www.weixin.qq.com/wxpay/pay.php&out_trade_no=zsbz20240529&pid=2769&type=alipay" + "Uxy57Z1Y1JK2y92VjVXV27a717j9V17U";
        String signMD5 = SecureUtil.md5(sign);
        System.out.println(signMD5);
    }

    // 易支付测试扫码支付
    @Test
    public void testYZFScanCodePayment() throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("pid", "2769")
                .add("type", "alipay")
                .add("out_trade_no", "zsbz20240528")
                .add("notify_url", "https://www.weixin.qq.com/wxpay/pay.php")
                .add("name", "测试一下")
                .add("money", "0.3")
                .add("clientip", "169.254.87.226")
                .add("sign", "8d5ee6d7c8f4673f50aac36690414d69")
                .add("sign_type", "MD5")
                .build();

        Request request = new Request.Builder()
                .url("https://pay.gm-pay.net/mapi.php")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        }
    }

    // 测试通过Retrofit进行扫码支付
    @Test
    public void testPrePayment() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pay.gm-pay.net/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        IScanCodeApi scanCodeApi = retrofit.create(IScanCodeApi.class);
        Call<PrePayResponse> call = scanCodeApi.prePayment(
                "2769",
                "alipay",
                "zsbz20240529",
                "https://www.weixin.qq.com/wxpay/pay.php",
                "测试一下",
                "0.3",
                "169.254.87.226",
                "0dedeeec7c591b11b35bf3c922edebb3",
                "MD5");

        retrofit2.Response<PrePayResponse> prePayResponse = call.execute();
        System.out.println(prePayResponse.body());
    }
}
