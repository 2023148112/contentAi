package org.example.contentaipro.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeService {
    @SystemMessage(fromResource="demo.txt")
    String chat(String message);

    //流式
    @SystemMessage(fromResource="demo.txt")
    Flux<String> chatStream(@MemoryId int memoryid, @UserMessage String message);
}