package org.sjjok00.chatgptcodeoptimization;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "org.sjjok00.chatgptcodeoptimization.MyPluginSettings", storages = {@Storage("code_optimization.xml")})

public class ChatGptSettings implements PersistentStateComponent<ChatGptSettings> {
    private static ChatGptSettings instance;

    private String openAiApiKey;

    private ChatGptSettings() {
        // 私有构造函数，防止外部实例化
    }

    public static synchronized ChatGptSettings getInstance() {
        if (instance == null) {
            instance = new ChatGptSettings();
        }
        return instance;
    }

    public String getOpenAiApiKey() {
        if (openAiApiKey == null) {
            this.openAiApiKey = "";
        }
        return openAiApiKey;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
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
