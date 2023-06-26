package org.sjjok00.chatgptcodeoptimization;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ChatGptConfigurable implements Configurable {

    private JTextField openAiApiKeyTextField;
    private JTextField overrideEndpointTextField;
    private JPanel mainPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "My Plugin Configuration";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        // 创建配置界面的UI组件
        mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel label = new JLabel("OpenAI Api Key:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(label, gbc);

        openAiApiKeyTextField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(openAiApiKeyTextField, gbc);

        JLabel endpointLabel = new JLabel("Override Api Endpoint:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(endpointLabel, gbc);

        overrideEndpointTextField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(overrideEndpointTextField, gbc);

        // 添加一个占位的空组件，填满剩余的空间
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        gbc.fill = 1;
        mainPanel.add(new JPanel(), gbc);

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        // 检查配置是否被修改
        String savedOpenAiApiKey = ChatGptSettings.getInstance().getOpenAiApiKey();
        String currentOpenAiApiKey = openAiApiKeyTextField.getText();

        String savedOverrideEndpoint = ChatGptSettings.getInstance().getOverrideEndpoint();
        String currentOverrideEndpoint = overrideEndpointTextField.getText();
        return !savedOpenAiApiKey.equals(currentOpenAiApiKey) || !savedOverrideEndpoint.equals(currentOverrideEndpoint);
    }

    @Override
    public void apply() throws ConfigurationException {
        // 将配置参数保存起来
        String openAiApiKey = openAiApiKeyTextField.getText();
        String overrideEndpoint = overrideEndpointTextField.getText();
        ChatGptSettings.getInstance().setOpenAiApiKey(openAiApiKey);
        ChatGptSettings.getInstance().setOverrideEndpoint(overrideEndpoint);
    }

    @Override
    public void reset() {
        // 重置配置参数为保存的值
        String openAiApiKey = ChatGptSettings.getInstance().getOpenAiApiKey();
        openAiApiKeyTextField.setText(openAiApiKey);
        String overrideEndpoint = ChatGptSettings.getInstance().getOverrideEndpoint();
        overrideEndpointTextField.setText(overrideEndpoint);
    }
}
