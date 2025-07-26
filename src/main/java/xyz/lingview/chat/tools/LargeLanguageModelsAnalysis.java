package xyz.lingview.chat.tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LargeLanguageModelsAnalysis {

    public static String callDashscopeAPI(String apiKey, String apiUrl, String model, String systemContent, String userQuestion) throws IOException, InterruptedException {
        int retryCount = 0;
        int maxRetries = 3;
        long delayBetweenRetries = 2000;

        while (retryCount <= maxRetries) {
            try {
                if (retryCount > 0) {
                    Thread.sleep(delayBetweenRetries);
                }

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonInputString = String.format(
                        "{"
                                + "\"model\": \"%s\","
                                + "\"stream\": false,"
                                + "\"enable_thinking\": false,"
                                + "\"messages\": ["
                                + "  {\"role\": \"system\", \"content\": \"%s\"},"
                                + "  {\"role\": \"user\", \"content\": \"%s\"}"
                                + "]"
                                + "}",
                        model,
                        escapeJson(systemContent),
                        escapeJson(userQuestion));

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    in.close();
                    return response.toString();
                } else {
                    InputStream errorStream = connection.getErrorStream();
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(errorStream != null ? errorStream : connection.getInputStream(), StandardCharsets.UTF_8)
                    );
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        error.append(line).append("\n");
                    }
                    System.err.println("请求失败，HTTP状态码: " + responseCode + ", 错误详情:\n" + error.toString());

                    if (responseCode == 429 || responseCode == 503 || responseCode == 500) {
                        retryCount++;
                        System.out.println("触发限流或服务不可用，正在进行第 " + retryCount + " 次重试...");
                        continue;
                    } else {
                        throw new IOException("请求失败，状态码：" + responseCode + ", 响应内容：" + error.toString());
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("线程被中断: " + e.getMessage());
                throw new IOException("请求被中断", e);
            } catch (Exception e) {
                System.err.println("请求调用异常: " + e.getMessage());
                retryCount++;
                System.out.println("正在进行第 " + retryCount + " 次重试...");
                Thread.sleep(delayBetweenRetries);
            }
        }

        throw new IOException("请求失败，已达到最大重试次数: " + maxRetries);
    }

    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
}
