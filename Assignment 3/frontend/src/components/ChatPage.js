import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import '../styles/ChatPage.css';

const ChatPageAdmin = () => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [currentUserId, setCurrentUserId] = useState(null);
    const [selectedUser, setSelectedUser] = useState(null);
    const [users, setUsers] = useState([]);
    const [userTyping, setUserTyping] = useState(false);
    const typingTimeoutRef = useRef(null);

    const stompClientRef = useRef(null);
    const tokenRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
            return;
        }
        try {
            const decoded = jwtDecode(token);
            if (decoded.role !== 'ADMIN') {
                navigate('/login');
                return;
            }
            setCurrentUserId(decoded.id);
            tokenRef.current = token;
            fetchUsers(token);
            connectWebSocket(token, decoded.id);
        } catch (error) {
            navigate('/login');
        }
    }, [navigate]);

    const connectWebSocket = (token, adminId) => {
        if (!adminId) return;

        const socket = new SockJS(`http://chat.localhost/ws?token=${token}`);


        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            connectHeaders: { Authorization: `Bearer ${token}` },
        });

        client.onConnect = () => {
            stompClientRef.current = client;

            client.subscribe("/topic/admin-messages", (msg) => {
                const parsedMsg = JSON.parse(msg.body);
                if (parsedMsg.senderId !== currentUserId) {
                    addUniqueMessage(parsedMsg);
                }
            });

            client.subscribe(`/user/${adminId}/queue/messages`, (msg) => {
                const parsedMsg = JSON.parse(msg.body);
                if (parsedMsg.senderId !== currentUserId) {
                    addUniqueMessage(parsedMsg);
                }
            });

            client.subscribe("/topic/typing", (typingMsg) => {
                const receivedTyping = JSON.parse(typingMsg.body);
                if (selectedUser && receivedTyping.senderId === selectedUser.id) {
                    setUserTyping(true);
                    if (typingTimeoutRef.current) {
                        clearTimeout(typingTimeoutRef.current);
                    }
                    typingTimeoutRef.current = setTimeout(() => {
                        setUserTyping(false);
                    }, 3000);
                }
            });
        };

        client.onDisconnect = () => {
            setTimeout(() => connectWebSocket(token, adminId), 5000);
        };

        client.activate();
    };

    const fetchUsers = async (token) => {
        try {
            const response = await axios.get('http://users.localhost/api/users/roleUser', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUsers(response.data);
        } catch (error) {}
    };

    const fetchChatHistory = async (userId) => {
        try {
            const token = tokenRef.current;
            if (!token) return;
            const response = await axios.get(
                `http://chat.localhost/api/chat/conversation/${currentUserId}/${userId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setMessages(response.data);
        } catch (error) {}
    };

    useEffect(() => {
        if (selectedUser) {
            fetchChatHistory(selectedUser.id);
        }
    }, [selectedUser]);

    useEffect(() => {
        const chatContainer = document.querySelector(".chat-messages");
        if (chatContainer) {
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }
    }, [messages, userTyping]);

    const sendMessage = () => {
        if (!stompClientRef.current?.connected || !selectedUser) return;

        const chatMessage = {
            senderId: currentUserId,
            recipientId: selectedUser.id,
            content: newMessage.trim(),
            timestamp: new Date().toISOString()
        };

        stompClientRef.current.publish({
            destination: '/app/send',
            body: JSON.stringify(chatMessage),
            headers: { Authorization: `Bearer ${tokenRef.current}` }
        });

        setNewMessage('');
    };


    const handleTyping = () => {
        if (!stompClientRef.current?.connected || !selectedUser) return;
        stompClientRef.current.publish({
            destination: '/app/typing',
            body: JSON.stringify({
                senderId: currentUserId,
                recipientId: selectedUser.id
            }),
            headers: { Authorization: `Bearer ${tokenRef.current}` }
        });
    };

    const addUniqueMessage = (newMsg) => {
        setMessages((prev) => {
            if (prev.some(msg =>
                msg.senderId === newMsg.senderId &&
                msg.content === newMsg.content &&
                new Date(msg.timestamp).getTime() === new Date(newMsg.timestamp).getTime()
            )) {
                return prev;
            }
            return [...prev, newMsg];
        });
    };


    return (
        <div className="chat-container">
            <h2>Admin Chat</h2>
            <div className="user-list">
                <h3>Select a user:</h3>
                <ul>
                    {users.map((user) => (
                        <li
                            key={user.id}
                            className={selectedUser?.id === user.id ? 'selected' : ''}
                            onClick={() => setSelectedUser(user)}
                        >
                            {user.name}
                        </li>
                    ))}
                </ul>
            </div>
            {selectedUser && (
                <>
                    <h3>Chat with {selectedUser.name}</h3>
                    <div className="chat-messages">
                        {messages.map((msg, index) => (
                            <div key={index} className={`chat-message ${msg.senderId === currentUserId ? 'sent' : 'received'}`}>
                                <strong>{msg.senderId === currentUserId ? 'Me' : selectedUser.name}:</strong> {msg.content}
                                <span className="timestamp">{new Date(msg.timestamp).toLocaleTimeString()}</span>
                            </div>
                        ))}
                        {userTyping && <div className="chat-message typing-indicator"><em>{selectedUser.name} is typing...</em></div>}
                    </div>
                    <input type="text" value={newMessage} onChange={(e) => { setNewMessage(e.target.value); handleTyping(); }} />
                    <button onClick={sendMessage}>Send</button>
                </>
            )}
        </div>
    );
};

export default ChatPageAdmin;
