package br.com.alura.owasp.retrofit;

import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.io.IOException;

@Component
public class GoogleWebClient {

    private static final String SECRET = System.getenv("RECAPTCHA_SECRET");

    public boolean verifica(String recaptcha) throws IOException {
        Call<Resposta> token = new RetrofitInicializador().getGoogleService().enviaToken(SECRET, recaptcha);

        return token.execute().body().isSuccess();
    }
}
