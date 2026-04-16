# GEMINI.md - Context AI Project Overview

This file provides instructional context for Gemini CLI when working on the **Context AI** project.

## Project Overview
**Context AI** is a programming learning and interview assistant. It leverages Large Language Models (LLMs) and Retrieval-Augmented Generation (RAG) to provide contextually aware answers based on provided technical documents.

### Key Technologies
- **Backend:** 
  - Java 17, Spring Boot 4.0.5
  - [LangChain4j](https://github.com/langchain4j/langchain4j) for LLM orchestration.
  - Aliyun DashScope (Qwen) for Chat and Embedding models.
  - Server-Sent Events (SSE) for real-time streaming responses.
- **Frontend:** 
  - Vue 3 (Composition API)
  - Vite for build and development.
  - CSS for a modern, responsive chat interface.
- **RAG System:** 
  - Documents are loaded from `src/main/resources/rag`.
  - In-memory embedding store for fast retrieval during development.

## Project Structure
- `src/main/java/org/example/contentaipro/`: Backend source code.
  - `ai/`: Contains AI service interfaces, controllers, and factories.
  - `RAG/`: Configuration for document loading, splitting, and embedding.
- `src/main/front/`: Vue 3 frontend application.
- `src/main/resources/`: Configuration files (`application.yml`) and RAG source documents.

## Building and Running

### Backend
1. Ensure Java 17 is installed.
2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *Note: Ensure your Aliyun DashScope API key is configured (likely in `application-local.yml` or as an environment variable).*

### Frontend
1. Navigate to the frontend directory:
   ```bash
   cd src/main/front
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

## Development Conventions

### Backend
- **Streaming:** Use `Flux<ServerSentEvent<String>>` in controllers for real-time AI responses.
- **AI Services:** Use LangChain4j's `AiServices` with `@SystemMessage` (linked to resources like `demo.txt`) and `@MemoryId` for stateful conversations.
- **RAG:** New documents should be placed in `src/main/resources/rag` to be automatically ingested at startup.

### Frontend
- **State Management:** Simple chat history is persisted in `localStorage`.
- **SSE Handling:** Use the `fetch` API with a `ReadableStream` to handle streaming data from the backend.
- **Styling:** Follow the established CSS variables and flexbox layouts in `App.vue` and `style.css`.

## API Endpoints
- `GET /myai/chat?memoryId={id}&message={text}`: Primary streaming chat endpoint (SSE).

## TODOs / Future Enhancements
- [ ] Migrate from in-memory embedding store to a persistent vector database (e.g., Milvus, Pinecone).
- [ ] Add user authentication.
- [ ] Support for more document formats in RAG (PDF, Docx).
- [ ] Enhanced code snippet highlighting in the frontend.
