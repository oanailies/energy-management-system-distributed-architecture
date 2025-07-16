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
    const [adminId, setAdminId] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (userData && userData.id) {
            setUserId(userData.id);
            setUserName(userData.name);
        } else {
            navigate('/login');
        }
    }, [navigate]);

    useEffect(() => {
        const fetchAdmin = async () => {
            const token = localStorage.getItem("token");

            if (!token) {
                console.error(" No token found");
                setError("Unauthorized request.");
                return;
            }

            try {
                const response = await axios.get('http://users.localhost/api/users/admin', {
                    headers: { Authorization: `Bearer ${token}` }
                });

                console.log("Admin API Response:", response.data);

                if (response.data && response.data.id) {
                    setAdminId(response.data.id);
                } else {
                    console.warn(" No admin found in database!");
                    setError('Admin not found.');
                }
            } catch (error) {
                console.error(' Error fetching admin:', error);
                setError('Could not load admin.');
            }
        };

        fetchAdmin();
    }, []);

    useEffect(() => {
        const fetchDevicesForUser = async () => {
            if (!userId) return;

            const token = localStorage.getItem("token");

            if (!token) {
                console.error("No token found");
                setError("Unauthorized request.");
                return;
            }

            try {
                const userDevicesResponse = await axios.get(
                    `http://devices.localhost/api/devices/user/${userId}/devices`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );

                const deviceIds = userDevicesResponse.data.map((device) => device.deviceId);

                const deviceDetailsPromises = deviceIds.map((id) =>
                    axios.get(`http://devices.localhost/api/devices/${id}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                );

                const devicesDetailsResponses = await Promise.all(deviceDetailsPromises);
                const devicesDetails = devicesDetailsResponses.map((response) => response.data);
                setUserDevices(devicesDetails);
                setLoading(false);
            } catch (error) {
                console.error(' Error loading user devices:', error);
                setError('Failed to load devices for the user.');
                setLoading(false);
            }
        };

        fetchDevicesForUser();
    }, [userId]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        navigate('/login');
    };

    const goToChatWithAdmin = () => {
        navigate('/user-chat');
    };

    return (
        <div className="user-page">
            <div className="header">
                <h1 className="user-title">Welcome, {userName}!</h1>
                <button onClick={handleLogout} className="logout-button">Logout</button>
            </div>

            <button
                onClick={() => navigate('/historical-energy')}
                className="chart-page-button"
            >
                View Historical Energy Charts
            </button>

            <button
                onClick={goToChatWithAdmin}
                className="chat-button"
            >
                Chat cu Admin
            </button>

            {loading && <p className="loading-message">Loading devices...</p>}
            {error && <p className="error-message">{error}</p>}
            {userDevices.length === 0 && !loading && (
                <p className="no-devices-message">You have no associated devices.</p>
            )}

            <ul className="user-devices-list">
                {userDevices.map((device) => (
                    <li key={device.id} className="device-item">
                        <h3 className="device-id">Device ID: {device.id}</h3>
                        <div className="device-details">
                            <p><strong>Name:</strong> {device.name}</p>
                            <p><strong>Description:</strong> {device.description}</p>
                            <p><strong>Address:</strong> {device.address}</p>
                            <p><strong>Max Hourly Consumption:</strong> {device.maxHourlyConsumption} kWh</p>

                            <NotificationComponent deviceId={device.id} />

                            <button className="start-button">Start Device</button>
                            <button className="unassign-button">Unassign Device</button>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default UserPage;
