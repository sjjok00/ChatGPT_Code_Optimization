package org.sjjok00.chatgptcodeoptimization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilCore;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatGptAction extends AnAction {

    private static final String OPENAI_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    private static final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));

    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";
    private static final OkHttpClient client = new OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).proxy(proxy).build();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();

        if (!selectionModel.hasSelection()) {
            Messages.showErrorDialog("Please select some code to optimize.", "Selection Error");
            return;
        }

        String selectedCode = selectionModel.getSelectedText();

        if (selectedCode == null || selectedCode.isEmpty()) {
            Messages.showErrorDialog("Selected code is empty or invalid.", "Selection Error");
            return;
        }

        String optimizedCode;
        try {
            optimizedCode = optimizeCode(selectedCode);
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Optimization Error");
            return;
        }

        if (optimizedCode == null || optimizedCode.isEmpty()) {
            Messages.showErrorDialog("Failed to optimize code.", "Optimization Error");
            return;
        }

        replaceSelectedCode(editor, optimizedCode);
    }

    /**
     * ChatGPT
     *
     * @param code
     * @return
     */
    private String optimizeCode(String code) {
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject requestBody = new JSONObject();
        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(ROLE_SYSTEM, "You are a helpful assistant, who can help optimize and correct Java code."));
        messageList.add(new Message("Optimize this code:\n" + code));
        requestBody.put("messages", messageList);
        requestBody.put("max_tokens", 1024);
        requestBody.put("model", "gpt-3.5-turbo");

        RequestBody body = RequestBody.create(requestBody.toString(), mediaType);

        String openAiApiKey = ChatGptSettings.getInstance().getOpenAiApiKey();
        if (StringUtils.isBlank(openAiApiKey)) {
            throw new RuntimeException("Please config OpenAI API Key in 'Setting > ChatGPT Code Optimization' first.");
        }

        String endpoint = ChatGptSettings.getInstance().getOverrideEndpoint();
        if (StringUtils.isBlank(endpoint)) {
            endpoint = OPENAI_API_ENDPOINT;
        }

        Request request = new Request.Builder()
                .url(endpoint)
                .header("Authorization", "Bearer " + openAiApiKey)
                .post(body)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            JSONObject json = JSON.parseObject(responseBody);
            if (!json.containsKey("choices")) {
                throw new RuntimeException(json.getJSONObject("error").getString("message"));
            }
            JSONArray choices = json.getJSONArray("choices");

            if (choices.size() == 0) {
                return null;
            }

            JSONObject result = choices.getJSONObject(0);
            System.out.println(result);
            String text = result.getJSONObject("message").getString("content");
            if (StringUtils.isNotBlank(text)) {
                if (text.contains("```java")) {
                    text = text.replaceFirst("```java", "```");
                }
                text = text.split("```")[1];
            }
            return text.trim();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void replaceSelectedCode(Editor editor, String optimizedCode) {
        Project project = editor.getProject();

        if (project == null) {
            Messages.showErrorDialog("Failed to get project.", "Project Error");
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());

        if (psiFile == null) {
            Messages.showErrorDialog("Failed to get PSI file.", "PSI File Error");
            return;
        }

        Document document = editor.getDocument();
        Runnable replaceRunnable = () ->
        {
            SelectionModel selectionModel = editor.getSelectionModel();
            int startOffset = selectionModel.getSelectionStart();
            int endOffset = selectionModel.getSelectionEnd();
            document.replaceString(startOffset, endOffset, optimizedCode);

            int startOffset2 = selectionModel.getSelectionStart();
            int endOffset2 = selectionModel.getSelectionEnd();

            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
            codeStyleManager.reformatText(psiFile, startOffset2, endOffset2);

            PsiDocumentManager.getInstance(project).commitDocument(document);
            PsiUtilCore.ensureValid(psiFile);
        };

        WriteCommandAction.runWriteCommandAction(project, replaceRunnable);
    }

    private UUID generateUUID() {
        byte[] randomBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f;
        randomBytes[6] |= 0x40;
        randomBytes[8] &= 0x3f;
        randomBytes[8] |= 0x80;
        return UUID.nameUUIDFromBytes(randomBytes);
    }

    class Message {
        public String role;

        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public Message(String content) {
            this.role = ROLE_USER;
            this.content = content;
        }
    }
}