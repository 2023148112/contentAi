contentAi

A full-stack AI assistant system for programming learning and interview preparation, focusing on LLM applications, Retrieval-Augmented Generation (RAG), and scalable retrieval system design.

Overview
This project adopts a front-end & back-end separated architecture with independent deployment, simulating ChatGPT-style interaction while emphasizing LLM system engineering and RAG pipeline design.
1 Frontend handles interaction and session management
2 Backend handles LLM orchestration, context construction, and streaming inference
3 Frontend is deployed on Vercel, while the backend runs as an independent Spring Boot service via HTTP / SSE

Tech Stack
Frontend
Vue.js (Vue3 + Vite)
Axios

Backend
Spring Boot
LangChain4j

Core
LLM-based generation
RAG pipeline (retrieval + context injection)
Server-Sent Events (streaming inference)

Focus:
Impact of retrieval quality on generation
Context construction strategies
Integration of memory and retrieval in multi-turn dialogue

Features
Streaming chat (SSE)
Conversational memory (multi-turn dialogue)
Retrieval-augmented responses (RAG)
Multi-session management
Local chat history

API
GET /api/myai/chat?memoryId={id}&message={message}
Streaming response via SSE

Research-Oriented Notes
Focuses on retrieval–generation interaction in LLM systems
Implements a lightweight RAG pipeline, extensible to large-scale retrieval systems
Emphasizes retrieval quality and context construction

Future Work
Integrate a vector database for scalable retrieval
Improve embedding and similarity search strategies
Explore applications such as Text-to-SQL

Deployment
Frontend: deployed on Vercel
Backend: independently deployed Spring Boot service
Communication via HTTP / SSE
