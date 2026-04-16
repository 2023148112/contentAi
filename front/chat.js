export function createChatSSE(memoryId, message, handlers = {}) {
    const { onOpen, onMessage, onDone, onError } = handlers

    const url =
        `http://localhost:8080/chat?memoryId=${encodeURIComponent(memoryId)}` +
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