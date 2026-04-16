package org.example.contentaipro.RAG;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;

@Configuration
public class RagConfig {
    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    @Bean
    public EmbeddingStore<TextSegment> qwenTextSegmentStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean//定义：怎么“根据问题找相关内容”
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> qwenTextSegmentStore) {
        //加载文档
            List<Document> documents = ClassPathDocumentLoader.loadDocuments(
                "rag",
                new TextDocumentParser()
        );

        //文档切割 还未进行切割
        DocumentByParagraphSplitter documentByParagraphSplitter=new DocumentByParagraphSplitter(1000,200);

        //制作一个入库器
        EmbeddingStoreIngestor ingestor=EmbeddingStoreIngestor.builder()
                .documentSplitter(documentByParagraphSplitter)//切割
                .textSegmentTransformer(textSegment ->
                        TextSegment.from(textSegment.metadata().getString("file_name")+"\n"
                        +textSegment.text(),textSegment.metadata()))
                .embeddingModel(qwenEmbeddingModel).embeddingStore(qwenTextSegmentStore).build();
// embedding 大致流程
//        1. 读取一个 Document
//        2. 用 documentSplitter 切成多个 TextSegment
//        3. 对每个 TextSegment 应用 transformer
//        4. 用 embeddingModel 把每个 TextSegment 转成向量
//        5. 把 TextSegment + 向量 存进 embeddingStore
        try {
            if (!documents.isEmpty()) {
                ingestor.ingest(documents);//进行入库器
            }
        } catch (Exception e) {
            System.err.println("RAG Ingestion failed: " + e.getMessage());
            System.err.println("Please ensure the Text Embedding service is activated in Aliyun DashScope console.");
        }

        //自定义内容加载器
        EmbeddingStoreContentRetriever contentRetriever=EmbeddingStoreContentRetriever.builder()
                //从里面检索
                .embeddingStore(qwenTextSegmentStore).embeddingModel(qwenEmbeddingModel).maxResults(5)
                .minScore(0.8)
                .build();

        return contentRetriever;//contentRetriever 是一个用来检索内容的工具

    }
}
//
//【入库阶段】
//代码文件
//↓
//Document
//↓
//切分（TextSegment）
//        ↓
//embedding（向量）
//        ↓
//存入 embeddingStore
//
//----------------------------
//
//        【检索阶段】
//用户问题
//↓
//embedding
//↓
//去 embeddingStore 搜索
//↓
//返回最相关的内容（Content）
