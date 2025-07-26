// chat.js
class ChatClient {
    constructor() {
        this.socket = null;
        this.roomId = null;
        this.username = null;
    }

    connect(roomId, username) {
        this.roomId = roomId;
        this.username = username;

        // 建立WebSocket连接
        this.socket = new WebSocket('ws://localhost:8080/ws/chat');

        this.socket.onopen = () => {
            console.log('WebSocket连接已建立');
            // 发送加入房间消息
            this.sendJoinMessage();
        };

        this.socket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.handleMessage(data);
        };

        this.socket.onclose = () => {
            console.log('WebSocket连接已关闭');
        };

        this.socket.onerror = (error) => {
            console.error('WebSocket错误:', error);
        };
    }

    sendJoinMessage() {
        const joinMessage = {
            type: 'join',
            roomId: this.roomId,
            username: this.username
        };
        this.socket.send(JSON.stringify(joinMessage));
    }

    sendMessage(content) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            const message = {
                type: 'message',
                roomId: this.roomId,
                username: this.username,
                content: content
            };
            this.socket.send(JSON.stringify(message));
        }
    }

    handleMessage(data) {
        switch (data.type) {
            case 'userJoined':
                this.displaySystemMessage(`${data.username} 加入了聊天室`);
                break;
            case 'userLeft':
                this.displaySystemMessage(`${data.username} 离开了聊天室`);
                break;
            case 'message':
                this.displayChatMessage(data.username, data.content, data.timestamp);
                break;
            default:
                console.log('未知消息类型:', data);
        }
    }

    displaySystemMessage(message) {
        const chatContainer = document.getElementById('chat-messages');
        const messageElement = document.createElement('div');
        messageElement.className = 'system-message';
        messageElement.textContent = message;
        chatContainer.appendChild(messageElement);
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

    displayChatMessage(username, content, timestamp) {
        const chatContainer = document.getElementById('chat-messages');
        const messageElement = document.createElement('div');
        messageElement.className = 'chat-message';
        messageElement.innerHTML = `
            <div class="message-header">
                <span class="username">${username}</span>
                <span class="timestamp">${new Date(timestamp).toLocaleTimeString()}</span>
            </div>
            <div class="message-content">${content}</div>
        `;
        chatContainer.appendChild(messageElement);
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

    disconnect() {
        if (this.socket) {
            const leaveMessage = {
                type: 'leave',
                roomId: this.roomId,
                username: this.username
            };
            this.socket.send(JSON.stringify(leaveMessage));
            this.socket.close();
        }
    }
}

// 使用示例
const chatClient = new ChatClient();

// 创建房间按钮事件
document.getElementById('create-room-btn').addEventListener('click', async () => {
    try {
        const response = await fetch('/api/chat/create', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + getJwtToken(), // 从存储中获取JWT token
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();
        if (data.success) {
            document.getElementById('room-id-display').textContent = data.roomId;
            // 自动连接到新创建的房间
            chatClient.connect(data.roomId, getCurrentUsername());
        } else {
            alert('创建房间失败: ' + data.message);
        }
    } catch (error) {
        console.error('创建房间错误:', error);
        alert('创建房间时发生错误');
    }
});

// 加入房间按钮事件
document.getElementById('join-room-btn').addEventListener('click', () => {
    const roomId = document.getElementById('room-id-input').value;
    if (roomId) {
        chatClient.connect(roomId, getCurrentUsername());
    } else {
        alert('请输入房间ID');
    }
});
