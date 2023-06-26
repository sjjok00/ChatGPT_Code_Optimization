package org.sjjok00.chatgptcodeoptimization;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "org.sjjok00.chatgptcodeoptimization.MyPluginSettings", storages = {@Storage("code_optimization.xml")})

public class ChatGptSettings implements PersistentStateComponent<ChatGptSettings> {
    private String openAiApiKey;
    private String overrideEndpoint;

    public static synchronized ChatGptSettings getInstance() {
        ChatGptSettings instance = ApplicationManager.getApplication().getComponent(ChatGptSettings.class);
        if (instance == null) {
            return new ChatGptSettings();
        }
        return instance;
    }

    public String getOpenAiApiKey() {
        if (openAiApiKey == null) {
            this.openAiApiKey = "";
        }
        return openAiApiKey;
    }

    public String getOverrideEndpoint() {
        if (overrideEndpoint == null) {
            this.overrideEndpoint = "";
        }
        return overrideEndpoint;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }
    public void setOverrideEndpoint(String overrideEndpoint) {
        this.overrideEndpoint = overrideEndpoint;
    }

    /**
     * @return a component state. All properties, public and annotated fields are serialized.
     * Only values which differ from the default (i.e. the value of newly instantiated class) are serialized.
     * {@code null} value indicates that the returned state won't be stored, as a result previously stored state will be used.
     * @see XmlSerializer
     */
    @Override
    public @Nullable ChatGptSettings getState() {
        return this;
    }

    /**
     * This method is called when a new component state is loaded.
     * The method can and will be called several times if config files are externally changed while the IDE is running.
     * <p>
     * State object should be used directly, defensive copying is not required.
     *
     * @param state loaded component state
     * @see XmlSerializerUtil#copyBean(Object, Object)
     */
    @Override
    public void loadState(@NotNull ChatGptSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }


}
