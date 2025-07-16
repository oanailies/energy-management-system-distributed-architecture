import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/UserPage.css';
import NotificationComponent from '../components/NotificationComponent';

const UserPage = () => {
    const [userDevices, setUserDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [userId, setUserId] = useState(null);
    const [userName, setUserName] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (userData && userData.user) {
            setUserId(userData.user.id);
            setUserName(userData.user.name);
        } else {
            navigate('/login');
        }
    }, [navigate]);


    useEffect(() => {
        const fetchDevicesForUser = async () => {
            if (!userId) return;

            try {
                const userDevicesResponse = await axios.get(
                    `http://devices.localhost/api/devices/user/${userId}/devices`
                );
                const deviceIds = userDevicesResponse.data.map((device) => device.deviceId);

                const deviceDetailsPromises = deviceIds.map((id) =>
                    axios.get(`http://devices.localhost/api/devices/${id}`)
                );
                const devicesDetailsResponses = await Promise.all(deviceDetailsPromises);

                const devicesDetails = devicesDetailsResponses.map((response) => response.data);
                setUserDevices(devicesDetails);
                setLoading(false);
            } catch (error) {
                console.error('Error loading user devices:', error);
                setError('Failed to load devices for the user.');
                setLoading(false);
            }
        };

        fetchDevicesForUser();
    }, [userId]);

    // Logout function
    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/login');
    };

    const handleUnassignDevice = async (deviceId) => {
        try {
            const response = await axios.delete(
                `http://devices.localhost/api/devices/unassign/${userId}/${deviceId}`
            );
            if (response.status === 200) {
                setUserDevices((prevDevices) => prevDevices.filter((device) => device.id !== deviceId));
            } else {
                setError('Failed to unassign device.');
            }
        } catch (error) {
            console.error('Error unassigning device:', error);
            setError('Failed to unassign device.');
        }
    };

    // Start the device
    const handleStartDevice = async (deviceId) => {
        try {
            const response = await fetch(
                `http://sender.localhost/api/producer/start?deviceId=${deviceId}`,
                { method: 'POST' }
            );
            if (response.ok) {
                alert(`Device ${deviceId} has been successfully started!`);
            } else {
                alert(`Failed to start device ${deviceId}.`);
            }
        } catch (error) {
            alert('Network error! Please check your connection.');
        }
    };

    return (
        <div className="user-page">
            {/* Header with user info and logout button */}
            <div className="header">
                <h1 className="user-title">Welcome, {userName}!</h1>
                <button onClick={handleLogout} className="logout-button">
                    Logout
                </button>
            </div>

            {/* Loading or error messages */}
            {loading && <p className="loading-message">Loading devices...</p>}
            {error && <p className="error-message">{error}</p>}
            {userDevices.length === 0 && !loading && (
                <p className="no-devices-message">You have no associated devices.</p>
            )}

            {/* Device list */}
            <ul className="user-devices-list">
                {userDevices.map((device) => (
                    <li key={device.id} className="device-item">
                        <h3 className="device-id">Device ID: {device.id}</h3>
                        <div className="device-details">
                            <p>
                                <strong>Name:</strong> {device.name}
                            </p>
                            <p>
                                <strong>Description:</strong> {device.description}
                            </p>
                            <p>
                                <strong>Address:</strong> {device.address}
                            </p>
                            <p>
                                <strong>Max Hourly Consumption:</strong> {device.maxHourlyConsumption} kWh
                            </p>
                            {/* Notification Component for WebSocket notifications */}
                            <NotificationComponent deviceId={device.id} />
                            {/* Action buttons */}
                            <button
                                onClick={() => handleStartDevice(device.id)}
                                className="start-button"
                            >
                                Start Device
                            </button>
                            <button
                                onClick={() => handleUnassignDevice(device.id)}
                                className="unassign-button"
                            >
                                Unassign Device
                            </button>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default UserPage;
