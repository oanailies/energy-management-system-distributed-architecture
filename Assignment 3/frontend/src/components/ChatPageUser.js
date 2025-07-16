import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import '../styles/ChatPage.css';

const ChatPageUser = () => {
    const [stompClient, setStompClient] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [currentUserId, setCurrentUserId] = useState(null);
    const [userTyping, setUserTyping] = useState(false);
    const navigate = useNavigate();
    const adminId = 1;
    const tokenRef = useRef(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
            return;
        }
        try {
            const decoded = jwtDecode(token);
            if (decoded.role !== 'USER') {
                navigate('/login');
                return;
            }
            setCurrentUserId(decoded.id);
            tokenRef.current = token;
            fetchChatHistory(decoded.id, token);
            connectWebSocket(token, decoded.id);
        } catch (error) {
            navigate('/login');
        }
    }, [navigate]);

    useEffect(() => {
        const chatContainer = document.querySelector(".chat-messages");
        if (chatContainer) {
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }
    }, [messages, userTyping]);

    const fetchChatHistory = async (userId, token) => {
        try {
            const response = await axios.get(
                `http://chat.localhost/api/chat/conversation/${userId}/${adminId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setMessages(response.data);
        } catch (error) {}
    };

    const connectWebSocket = (token, userId) => {
        if (!userId) return;
        const socket = new SockJS(`http://chat.localhost/ws?token=${encodeURIComponent(token)}`);
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            connectHeaders: { Authorization: `Bearer ${token}` },
        });

        client.onConnect = () => {
            setStompClient(client);

            client.subscribe(`/user/${userId}/queue/messages`, (msg) => {
                const parsedMsg = JSON.parse(msg.body);
                addUniqueMessage(parsedMsg);
            });

            client.subscribe("/topic/admin-messages", (msg) => {
                const parsedMsg = JSON.parse(msg.body);
                if (parsedMsg.senderId === adminId || parsedMsg.recipientId === userId) {
                    addUniqueMessage(parsedMsg);
                }
            });

            client.subscribe("/topic/typing", (msg) => {
                const receivedTyping = JSON.parse(msg.body);
                if (receivedTyping.senderId === adminId) {
                    setUserTyping(true);
                    setTimeout(() => setUserTyping(false), 3000);
                }
            });
        };

        client.onDisconnect = () => {
            setTimeout(() => connectWebSocket(token, userId), 5000);
        };

        client.activate();
    };

    const sendMessage = () => {
        if (!stompClient || !stompClient.connected) return;
        if (!newMessage.trim()) return;

        const chatMessage = {
            senderId: currentUserId,
            recipientId: adminId,
            content: newMessage.trim(),
            timestamp: new Date().toISOString()
        };

        stompClient.publish({
            destination: '/app/send',
            body: JSON.stringify(chatMessage),
            headers: { Authorization: `Bearer ${tokenRef.current}` }
        });

        addUniqueMessage(chatMessage);
        setNewMessage('');
    };

    const handleTyping = () => {
        if (!stompClient || !stompClient.connected) return;
        stompClient.publish({
            destination: '/app/typing',
            body: JSON.stringify({
                senderId: currentUserId,
                recipientId: adminId
            }),
            headers: { Authorization: `Bearer ${tokenRef.current}` }
        });
    };

    const addUniqueMessage = (newMsg) => {
        setMessages((prev) => {
            if (!prev.some((msg) => msg.timestamp === newMsg.timestamp)) {
                return [...prev, newMsg];
            }
            return prev;
        });
    };

    return (
        <div className="chat-container">
            <h2>Chat with the Administrator</h2>
            <div className="chat-messages">
                {messages.map((msg, index) => (
                    <div
                        key={index}
                        className={`chat-message ${msg.senderId === currentUserId ? 'sent' : 'received'}`}
                    >
                        <strong>{msg.senderId === currentUserId ? 'Me' : 'Admin'}:</strong> {msg.content}
                        <span className="timestamp">{new Date(msg.timestamp).toLocaleTimeString()}</span>
                    </div>
                ))}
                {userTyping && <div className="chat-message typing-indicator"><em>Administrator is typing...</em></div>}
            </div>
            <input type="text" value={newMessage} onChange={(e) => { setNewMessage(e.target.value); handleTyping(); }} />
            <button onClick={sendMessage}>Send</button>
        </div>
    );
};

export default ChatPageUser;
