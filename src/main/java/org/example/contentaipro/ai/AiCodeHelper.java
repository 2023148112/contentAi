package org.example.contentaipro.ai;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j//自动生成log对象
public class AiCodeHelper {
    @Resource
    private ChatModel qwenchatmodel;
    public String chat(String message){
        UserMessage userMessage=UserMessage.from(message);//AI聊天模型需要的输入不是普通字符串，而是一个 Message对象。
        ChatResponse chatResponse=qwenchatmodel.chat(userMessage);
        AiMessage aiMessage=chatResponse.aiMessage();//aiMessage是一个对象不是一个字符串
        log.info("Ai 输出: "+aiMessage.toString());//打印 整个对象信息

        return aiMessage.text();//只讲aiMessage中的文本信息打印出来；
    }
}
