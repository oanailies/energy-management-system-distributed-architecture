import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/UserPage.css';

const UserPage = () => {
    const [userDevices, setUserDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [userId, setUserId] = useState(null);
    const [userName, setUserName] = useState('');
    const [deviceDetails, setDeviceDetails] = useState({});
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
        if (userId) {
            const fetchUserDevices = async () => {
                try {
                    const response = await axios.get(`http://localhost:8081/api/devices/user/${userId}/devices`);
                    setUserDevices(response.data);
                    setLoading(false);
                } catch (error) {
                    setError('Error fetching devices.');
                    setLoading(false);
                }
            };

            fetchUserDevices();
        }
    }, [userId]);

    const fetchDeviceDetails = async (deviceId) => {
        try {
            const response = await axios.get(`http://localhost:8081/api/devices/${deviceId}`);
            setDeviceDetails((prevDetails) => ({
                ...prevDetails,
                [deviceId]: response.data,
            }));
        } catch (error) {
            console.error('Error fetching device details:', error);
        }
    };

    useEffect(() => {
        if (userDevices.length > 0) {
            userDevices.forEach((device) => {
                fetchDeviceDetails(device.deviceId);
            });
        }
    }, [userDevices]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/login');
    };

    const handleUnassignDevice = async (deviceId) => {
        try {
            const response = await axios.delete(`http://localhost:8081/api/devices/unassign/${userId}/${deviceId}`);
            if (response.status === 200) {
                setUserDevices(userDevices.filter(device => device.deviceId !== deviceId));
                setDeviceDetails(prevDetails => {
                    const updatedDetails = { ...prevDetails };
                    delete updatedDetails[deviceId];
                    return updatedDetails;
                });
            } else {
                setError('Error unassigning device.');
            }
        } catch (error) {
            console.error('Error unassigning device:', error);
            setError('Error unassigning device.');
        }
    };

    useEffect(() => {
        const handleBeforeUnload = (e) => {
            if (!localStorage.getItem('user')) {
                const confirmationMessage = "You are not logged in. Are you sure you want to leave?";
                e.returnValue = confirmationMessage; // Standard for most browsers
                return confirmationMessage; // For some browsers like Chrome
            }
        };

        window.addEventListener('beforeunload', handleBeforeUnload);

        return () => {
            window.removeEventListener('beforeunload', handleBeforeUnload);
        };
    }, []);

    return (
        <div className="user-page">
            <div className="header">
                <h1 className="user-title">Welcome, {userName}!</h1>
                <button onClick={handleLogout} className="logout-button">Logout</button>
            </div>

            {loading && <p className="loading-message">Loading devices...</p>}
            {error && <p className="error-message">{error}</p>}
            {userDevices.length === 0 && !loading && (
                <p className="no-devices-message">You have no associated devices.</p>
            )}

            <ul className="user-devices-list">
                {userDevices.map((device) => (
                    <li key={device.deviceId} className="device-item">
                        <h3 className="device-id">Device ID: {device.deviceId}</h3>
                        {deviceDetails[device.deviceId] ? (
                            <div className="device-details">
                                <p><strong>Name:</strong> {deviceDetails[device.deviceId].name}</p>
                                <p><strong>Description:</strong> {deviceDetails[device.deviceId].description}</p>
                                <p><strong>Address:</strong> {deviceDetails[device.deviceId].address}</p>
                                <p><strong>Max Hourly Consumption:</strong> {deviceDetails[device.deviceId].maxHourlyConsumption} kWh</p>
                                <button
                                    onClick={() => handleUnassignDevice(device.deviceId)}
                                    className="unassign-button"
                                >
                                    Unassign Device
                                </button>
                            </div>
                        ) : (
                            <p>Loading device details...</p>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default UserPage;
