package io.digdag.plugin.chatwork;

import io.digdag.client.config.Config;
import io.digdag.spi.*;
import io.digdag.util.BaseOperator;
import okhttp3.*;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChatWorkOperatorFactory
        implements OperatorFactory {
    private final TemplateEngine templateEngine;

    private static final OkHttpClient singletonInstance = new OkHttpClient();

    public ChatWorkOperatorFactory(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String getType() {
        return "chatwork";
    }

    @Override
    public Operator newOperator(OperatorContext context) {
        return new ChatWorkOperator(context);
    }

    private class ChatWorkOperator
            extends BaseOperator {
        public ChatWorkOperator(OperatorContext context) {
            super(context);
        }

        @Override
        public TaskResult runTask() {
            Config params = request.getConfig().mergeDefault(
                    request.getConfig().getNestedOrGetEmpty("chatwork"));

            String message = workspace.templateCommand(templateEngine, params, "message", UTF_8);
            String apiToken = params.get("api_token", String.class);
            String roomId = params.get("room_id", String.class);
            this.postToChatWork(apiToken, roomId, message);

            return TaskResult.empty(request);
        }

        private void postToChatWork(String apiToken, String roomId, String message) {
            RequestBody body = new FormBody.Builder()
                    .add("body", message)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.chatwork.com/v2/rooms/" + roomId + "/messages")
                    .header("X-ChatWorkToken", apiToken)
                    .post(body)
                    .build();

            Call call = singletonInstance.newCall(request);

            try (Response response = call.execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("posting to chatwork failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                singletonInstance.connectionPool().evictAll();
            }
        }
    }
}
