package org.example.contentaipro.ai;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//+@Bean
// AiCodeHelperServiceFactory 是一个配置类，但它的作用是“定义一个工厂方法来创建 Bean”

public class AiCodeHelperServiceFactory {
    @Resource
    private ChatModel qwengChatModel;

    @Resource
    private ContentRetriever contentRetriever;
    private EmbeddingStore<TextSegment> qwengTextSegmentStore;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Bean
    public AiCodeService aiCodeService() {
        //只要接口里有方法，build() 生成的对象就会一起实现。 动态代理生成实现类
        ChatMemory chatMemory=MessageWindowChatMemory.withMaxMessages(10);
        AiCodeService aiCodeService = AiServices.builder(AiCodeService.class)
                .chatModel(qwengChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(chatMemory)
                .chatMemoryProvider(momoryId->MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever)
                .build();
        return aiCodeService;


       // return AiServices.create(AiCodeService.class,qwengChatModel);
    }

}