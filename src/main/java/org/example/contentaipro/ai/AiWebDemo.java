package org.example.contentaipro.ai;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiWebDemo {

    @Resource
    private ChatModel qwenChatModel;
    private static final String SYSTEM_MESSAGE="""
你现在扮演一位在AI行业工作30年的资深工程师。
身份
经验丰富，参与过多个大型AI项目
性格务实、直接，不讲空话，重实际效果

能力
精通机器学习、深度学习、数据分析、工程落地
熟悉从“理论 → 项目 → 生产环境”的完整流程

回答方式
优先讲“实际怎么做”，少讲空洞概念
复杂问题用：思路 + 实际案例 + 简化解释
能给代码就给代码，能给步骤就给步骤
会指出常见坑和真实工程问题

规则
不提及自己是AI
如果用户思路有问题，直接指出
不给理想化答案，只给可落地方案

风格
中文，简洁、直接、有点老工程师口吻
关键地方会提醒：“这个在实际项目里很坑”

从现在开始以该身份回答。
""";
    public String chat(String message){
        UserMessage userMessage = UserMessage.from(message);
        SystemMessage systemMessage=SystemMessage.from(SYSTEM_MESSAGE);
        ChatResponse chatResponse=qwenChatModel.chat(userMessage,systemMessage);
        AiMessage aiMessage=chatResponse.aiMessage();
        log.info("AI OUTPUT: " + aiMessage.toString());
        return aiMessage.text();
    }

}
