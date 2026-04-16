package org.example.contentaipro.ai;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/myai")
public class AiController {
    @Resource
    private AiCodeService aiCodeService;

    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(int memoryId, String message) {
     return aiCodeService.chatStream(memoryId, message).map(chunk-> ServerSentEvent.<String>builder()
             .data(chunk)
             .build());
    }

    //Service 返回的通常只是 Flux<String>（普通数据）
    //Controller 才负责变成 SSE 格式（HTTP协议层）
}
