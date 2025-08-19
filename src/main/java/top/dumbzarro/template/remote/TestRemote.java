package top.dumbzarro.template.remote;

import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

@Component
public class TestRemote {
    private final HttpClient webClient = HttpClient.newBuilder().build();


}
