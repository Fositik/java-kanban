package ru.yandex.practicum.http.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.*;

public class KVTaskClient {
    private final String serverURL; //Конструктор принимает URL к серверу хранилища и регистрируется.
    private final String API_TOKEN; // При регистрации выдаётся токен (API_TOKEN), который нужен при работе с сервером.

    private final HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String serverURL) throws IOException, URISyntaxException, InterruptedException {
        this.serverURL = serverURL;
        URI uri = new URI(this.serverURL + "/register");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        //Класс, описывающий запрос, который необходимо отправить - HttpResponse.BodyHandler<T>
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        API_TOKEN = response.body();
    }

    //Метод void put(String key, String json) должен сохранять
    // состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
    public void put(String key, String json) {
        try {
            URI uri = URI.create(this.serverURL + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder()
                    // возвращает обработчик, позволяющий преобразовать тело ответа из набора байтов в строку.
                    .POST(HttpRequest.BodyPublishers.ofString(json)) // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(UTF_8));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //Метод String load(String key) должен возвращать состояние менеджера задач
    // через запрос GET /load/<ключ>?API_TOKEN=.
    public String load(String key) {
        try {
            URI uri = URI.create(this.serverURL + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder()
                    .GET() // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(UTF_8));
            if (response.body() != null) {
                return response.body(); // выводим тело ответа
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Во время запроса произошла ошибка";
        }
        return null;
    }
}
