import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const NotificationComponent = ({ deviceId }) => {
    const [messages, setMessages] = useState([]);

    useEffect(() => {
        const sock = new SockJS('http://receiver.localhost/ws');
        const stompClient = new Client({
            webSocketFactory: () => sock,
            onConnect: () => {
                console.log('Connected to WebSocket');
                stompClient.subscribe(`/topic/${deviceId}`, (message) => {
                    try {
                        const parsedMessage = JSON.parse(message.body);
                        setMessages((prevMessages) => [...prevMessages, parsedMessage.message]);
                    } catch (error) {
                        console.error('Invalid JSON received:', message.body);
                    }
                });
            },
            onDisconnect: () => console.log('Disconnected from WebSocket'),
        });

        stompClient.activate();

        return () => {
            stompClient.deactivate();
        };
    }, [deviceId]);

    return (
        <div>
            <h3>Notifications for Device ID: {deviceId}</h3>
            <ul>
                {messages.map((msg, index) => (
                    <li key={index}>{msg}</li>
                ))}
            </ul>
        </div>
    );
};

export default NotificationComponent;
