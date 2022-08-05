package cn.hippo4j.monitor.es;

import cn.hippo4j.common.config.ApplicationContextHolder;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Create by yuyang
 * 2022/8/4 16:26
 */
@Slf4j
public class EsClientHolder {

    private static String host;
    private static String scheme;
    private static String userName;
    private static String password;

    private static RestHighLevelClient client;

    private static RestHighLevelClient initRestClient() {
        try {
            Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
            host = environment.getProperty("es.thread-pool-state.host");
            scheme = environment.getProperty("es.thread-pool-state.schema");
            userName = environment.getProperty("es.thread-pool-state.userName");
            password = environment.getProperty("es.thread-pool-state.password");

            List<HttpHost> hosts = parseHosts();

            if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)) {
                client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{})));
            } else {
                client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{}))
                        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(getCredentialsProvider())));
            }

            log.info("[ES RestHighLevelClient] success to connect es！host:{},scheme:{}", host, scheme);
            return client;
        } catch (Exception ex) {
            log.error("[ES RestHighLevelClient] fail to connect es! cause:{}", Throwables.getStackTraceAsString(ex));
        }
        return null;
    }

    private static BasicCredentialsProvider getCredentialsProvider() {
        if (!Strings.isNullOrEmpty(userName) && !Strings.isNullOrEmpty(password)) {
            final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(userName, password));
            return credentialsProvider;
        }
        return null;
    }

    public static RestHighLevelClient getClient() {
        return null == client ? initRestClient() : client;
    }

    private static List<HttpHost> parseHosts() {
        String[] hostAndPorts = host.split(",");
        List<HttpHost> hosts = Lists.newArrayList();
        for (String hostAndPort : hostAndPorts) {
            hostAndPort = hostAndPort.trim();
            hosts.add(new HttpHost(hostAndPort.split(":")[0], Integer.parseInt(hostAndPort.split(":")[1]), scheme));
        }
        return hosts;
    }

}
