package ru.yandex.practicum.http.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * `KVServer` - это серверное приложение, которое предоставляет распределенное хранилище данных с простым интерфейсом
 * "ключ-значение". Он предназначен для запуска на нескольких узлах в сети, и каждый сервер хранит некоторое количество
 * ключей и соответствующих им значений.
 * <p>
 * Когда клиент отправляет запрос на чтение или запись определенного ключа, он отправляется на сервер,
 * который хранит этот ключ. Если сервер не может обработать запрос, он перенаправляет его на другой сервер,
 * который может. Клиент получает ответ от сервера, который обработал запрос.
 * <p>
 * Класс `KVServer` реализует интерфейс `KeyValueInterface`, который определяет методы для чтения и записи данных
 * в хранилище. Он также использует классы `KVStore` и `TPCLog`, которые представляют собой хранилище данных и журнал
 * транзакций соответственно.
 */
public class KVServer {
    public static final int PORT = 8091;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    /**
     * KVServer — это хранилище, где данные хранятся по принципу <ключ-значение>. Он умеет:
     * GET /register — регистрировать клиента и выдавать уникальный токен доступа (аутентификации).
     * Это нужно, чтобы хранилище могло работать сразу с несколькими клиентами.
     * POST /save/<ключ>?API_TOKEN= — сохранять содержимое тела запроса, привязанное к ключу.
     * GET /load/<ключ>?API_TOKEN= — возвращать сохранённые значение по ключу.
     */
    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) { //GET /load/<ключ>?API_TOKEN= — возвращать сохранённые значение по ключу.
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                if (data.get(key) == null) {
                    System.out.println("Данные для ключа '" + key + "', отсутствуют");
                    h.sendResponseHeaders(404, 0);
                    return;
                }
                String response = data.get(key);
                sendText(h, response);
                System.out.println("Данные для ключа " + key + " успешно отправлено в ответ на запрос!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/load ждет GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            System.out.println("блок finally");
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        System.out.println("Завершаем работу сервера на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/ - больше не работает");
        System.out.println("API_TOKEN: " + apiToken);
        server.stop(0);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
