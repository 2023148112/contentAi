<template>
  <div class="chat-container">
    <!-- Sidebar -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h1 class="sidebar-title">Context AI</h1>
        <p class="sidebar-subtitle">编程学习 & 求职面试助手</p>
      </div>

      <button class="new-chat-btn" @click="createNewChat">
        + 新建对话
      </button>

      <div class="chat-history">
        <div
            v-for="chat in chatHistory"
            :key="chat.id"
            class="chat-item"
            :class="{ active: chat.id === currentChatId }"
            @click="switchChat(chat.id)"
        >
          <div class="chat-item-title">{{ chat.title }}</div>
          <div class="chat-item-time">{{ formatTime(chat.lastMessageTime) }}</div>
        </div>
      </div>
    </div>

    <!-- Main Chat Area -->
    <div class="main-chat">
      <div class="chat-header">
        <h2 class="chat-title">{{ currentChatTitle }}</h2>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <div v-if="currentMessages.length === 0" class="welcome-message">
          <div class="message assistant">
            <div class="message-avatar">亲</div>
            <div class="message-content">
              你好！我是你的私人小助手，专门帮助解答编程学习和求职面试相关的问题。有什么可以帮你的吗？
            </div>
          </div>
        </div>

        <div
            v-for="message in currentMessages"
            :key="message.id"
            class="message"
            :class="message.role"
        >
          <div class="message-avatar">
            {{ message.role === 'user' ? '你' : '亲' }}
          </div>
          <div class="message-content" v-html="formatMessage(message.content)"></div>
        </div>

        <!-- Typing indicator -->
        <div v-if="isTyping" class="message assistant">
          <div class="message-avatar">Context AI</div>
          <div class="typing-indicator">
            <div class="typing-dots">
              <div class="typing-dot"></div>
              <div class="typing-dot"></div>
              <div class="typing-dot"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input-container">
        <div class="chat-input-wrapper">
          <textarea
              v-model="inputMessage"
              class="chat-input"
              placeholder="输入你的问题..."
              @keydown="handleKeyDown"
              :disabled="isLoading"
              ref="inputRef"
          ></textarea>
          <button
              class="send-btn"
              @click="sendMessage"
              :disabled="!inputMessage.trim() || isLoading"
          >
            <svg v-if="!isLoading" class="send-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
            </svg>
            <div v-else class="loading"></div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import axios from 'axios'

export default {
  name: 'App',
  setup() {
    // Reactive data
    const inputMessage = ref('')
    const isLoading = ref(false)
    const isTyping = ref(false)
    const currentChatId = ref(null)
    const currentChatTitle = ref('新对话')
    const messagesContainer = ref(null)
    const inputRef = ref(null)

    // Chat history and messages
    const chatHistory = ref([])
    const currentMessages = ref([])

    // Generate unique chat ID (must be within Java int range: -2,147,483,648 to 2,147,483,647)
    const generateChatId = () => {
      // Use a smaller range to ensure we stay within int limits
      // Generate a random number between 1 and 2,000,000,000 (well within int range)
      return Math.floor(Math.random() * 2000000000) + 1
    }

    // Create new chat
    const createNewChat = () => {
      const newChatId = generateChatId()
      const newChat = {
        id: newChatId,
        title: '新对话',
        lastMessageTime: new Date(),
        messages: []
      }

      chatHistory.value.unshift(newChat)
      currentChatId.value = newChatId
      currentChatTitle.value = '新对话'
      currentMessages.value = []

      // Save to localStorage
      saveChatHistory()
    }

    // Switch chat
    const switchChat = (chatId) => {
      const chat = chatHistory.value.find(c => c.id === chatId)
      if (chat) {
        currentChatId.value = chatId
        currentChatTitle.value = chat.title
        currentMessages.value = chat.messages || []
      }
    }

    // Send message
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || isLoading.value) return

      const message = inputMessage.value.trim()
      inputMessage.value = ''

      // Add user message
      const userMessage = {
        id: Date.now(),
        role: 'user',
        content: message,
        timestamp: new Date()
      }

      currentMessages.value.push(userMessage)

      // Update chat title if it's the first message
      if (currentMessages.value.length === 1) {
        currentChatTitle.value = message.length > 20 ? message.substring(0, 20) + '...' : message
        updateCurrentChatTitle()
      }

      // Add assistant message placeholder
      const assistantMessage = {
        id: Date.now() + 1,
        role: 'assistant',
        content: '',
        timestamp: new Date()
      }

      currentMessages.value.push(assistantMessage)
      isTyping.value = true
      isLoading.value = true

      // Scroll to bottom
      await nextTick()
      scrollToBottom()

      try {
        // Call SSE endpoint
        await streamChatResponse(message)
      } catch (error) {
        console.error('Error sending message:', error)
        assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
        isTyping.value = false
        isLoading.value = false
      }

      // Save messages
      saveCurrentChat()
    }

    // Stream chat response using SSE
    const streamChatResponse = async (message) => {
      const chatId = currentChatId.value || generateChatId()
      const BASE_URL = 'https://contentai-production-0e73.up.railway.app'
      const url = `${BASE_URL}/api/myai/chat?memoryId=${chatId}&message=${encodeURIComponent(message)}`
      try {
        const response = await fetch(url, {
          method: 'GET',
         const response = await fetch(url, {
         method: 'GET'
})
        })

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }

        if (!response.body) {
          throw new Error('Response body is null')
        }

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let assistantMessage = currentMessages.value[currentMessages.value.length - 1]

        isTyping.value = false

        while (true) {
          const { done, value } = await reader.read()

          if (done) break

          const chunk = decoder.decode(value, { stream: true })
          const lines = chunk.split('\n')

          for (const line of lines) {
            if (line.startsWith('data: ')) {
              const data = line.slice(6)
              if (data.trim() && data !== '[DONE]') {
                assistantMessage.content += data
                await nextTick()
                scrollToBottom()
              }
            } else if (line.startsWith('data:')) {
              // Handle cases where there's no space after 'data:'
              const data = line.slice(5)
              if (data.trim() && data !== '[DONE]') {
                assistantMessage.content += data
                await nextTick()
                scrollToBottom()
              }
            } else if (line.trim() && !line.startsWith('event:') && !line.startsWith('id:') && !line.startsWith('data:')) {
              // Handle raw data without SSE format
              if (line.trim() && line !== '[DONE]') {
                assistantMessage.content += line
                await nextTick()
                scrollToBottom()
              }
            }
          }
        }

        isLoading.value = false
        saveCurrentChat()

      } catch (error) {
        console.error('SSE Error:', error)
        const assistantMessage = currentMessages.value[currentMessages.value.length - 1]
        assistantMessage.content = '抱歉，连接出现问题，请稍后重试。'
        isTyping.value = false
        isLoading.value = false
        saveCurrentChat()
      }
    }

    // Handle key down events
    const handleKeyDown = (event) => {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        sendMessage()
      }
    }

    // Format message content
    const formatMessage = (content) => {
      if (!content) return ''

      // Convert line breaks to HTML
      return content
          .replace(/\n/g, '<br>')
          .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
          .replace(/\*(.*?)\*/g, '<em>$1</em>')
          .replace(/`(.*?)`/g, '<code>$1</code>')
    }

    // Format time
    const formatTime = (date) => {
      // Ensure date is a Date object
      const dateObj = date instanceof Date ? date : new Date(date)

      // Check if date is valid
      if (isNaN(dateObj.getTime())) {
        return '未知时间'
      }

      const now = new Date()
      const diff = now - dateObj
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`

      return dateObj.toLocaleDateString('zh-CN')
    }

    // Scroll to bottom
    const scrollToBottom = () => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // Update current chat title
    const updateCurrentChatTitle = () => {
      const chat = chatHistory.value.find(c => c.id === currentChatId.value)
      if (chat) {
        chat.title = currentChatTitle.value
        chat.lastMessageTime = new Date()
        saveChatHistory()
      }
    }

    // Save current chat
    const saveCurrentChat = () => {
      const chat = chatHistory.value.find(c => c.id === currentChatId.value)
      if (chat) {
        chat.messages = [...currentMessages.value]
        chat.lastMessageTime = new Date()
        saveChatHistory()
      }
    }

    // Save chat history to localStorage
    const saveChatHistory = () => {
      localStorage.setItem('yangge-chat-history', JSON.stringify(chatHistory.value))
    }

    // Load chat history from localStorage
    const loadChatHistory = () => {
      const saved = localStorage.getItem('yangge-chat-history')
      if (saved) {
        try {
          chatHistory.value = JSON.parse(saved)

          // Convert date strings back to Date objects
          chatHistory.value.forEach(chat => {
            if (chat.lastMessageTime && typeof chat.lastMessageTime === 'string') {
              chat.lastMessageTime = new Date(chat.lastMessageTime)
            }

            // Also convert message timestamps
            if (chat.messages) {
              chat.messages.forEach(message => {
                if (message.timestamp && typeof message.timestamp === 'string') {
                  message.timestamp = new Date(message.timestamp)
                }
              })
            }
          })

          if (chatHistory.value.length > 0) {
            currentChatId.value = chatHistory.value[0].id
            currentChatTitle.value = chatHistory.value[0].title
            currentMessages.value = chatHistory.value[0].messages || []
          }
        } catch (error) {
          console.error('Error loading chat history:', error)
        }
      }
    }

    // Watch for message changes to auto-scroll
    watch(currentMessages, () => {
      nextTick(() => {
        scrollToBottom()
      })
    }, { deep: true })

    // Initialize on mount
    onMounted(() => {
      loadChatHistory()
      if (chatHistory.value.length === 0) {
        createNewChat()
      }

      // Focus input
      if (inputRef.value) {
        inputRef.value.focus()
      }
    })

    return {
      inputMessage,
      isLoading,
      isTyping,
      currentChatId,
      currentChatTitle,
      chatHistory,
      currentMessages,
      messagesContainer,
      inputRef,
      createNewChat,
      switchChat,
      sendMessage,
      handleKeyDown,
      formatMessage,
      formatTime
    }
  }
}
</script>
