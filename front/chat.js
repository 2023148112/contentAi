export function createChatSSE(memoryId, message, handlers = {}) {
    const { onOpen, onMessage, onDone, onError } = handlers
    
    const BASE_URL = 'https://contentai-production-0e73.up.railway.app'

const url =
  `${BASE_URL}/api/myai/chat?memoryId=${encodeURIComponent(memoryId)}` +
  `&message=${encodeURIComponent(message)}`

    const eventSource = new EventSource(url)

    eventSource.onopen = () => {
        onOpen && onOpen()
    }

    eventSource.onmessage = (event) => {
        const data = event.data

        if (data === '[DONE]') {
            onDone && onDone()
            eventSource.close()
            return
        }

        onMessage && onMessage(data)
    }

    eventSource.onerror = () => {
        onError && onError()
        eventSource.close()
    }

    return eventSource
}
//发送请求 → 一点一点返回
