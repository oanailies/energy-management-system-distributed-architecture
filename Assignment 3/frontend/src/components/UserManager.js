import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

import { useNavigate } from 'react-router-dom';
import '../styles/UserManager.css';

const UserManager = () => {
    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);
    const [user, setUser] = useState({ name: '', email: '', password: '' });
    const [editingUser, setEditingUser] = useState(null);
    const [selectedDevice, setSelectedDevice] = useState(null);
    const [selectedUserId, setSelectedUserId] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const token = localStorage.getItem('token');
    let userId = null;
    let userRole = null;

    if (token) {
        try {
            const decodedToken = jwtDecode(token);
            userId = decodedToken.id;
            userRole = decodedToken.role;
        } catch (error) {
            console.error('Error decoding token:', error);
        }
    }

    useEffect(() => {
        if (!token) {
            navigate('/login');
        } else {
            fetchUsers();
            fetchDevices();
        }
    }, [token, navigate]);

    const fetchUsers = async () => {
        try {
            const response = await axios.get('http://users.localhost/api/users/roleUser', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUsers(response.data);
        } catch (error) {
            setError("Error fetching users!");
            console.error("Error fetching users!", error);
        }
    };

    const fetchDevices = async () => {
        try {
            const response = await axios.get('http://devices.localhost/api/devices', {
                headers: { Authorization: `Bearer ${token}` }
            });
            const availableDevices = response.data.filter(device => !device.assigned);
            setDevices(availableDevices);
        } catch (error) {
            setError("Error fetching devices!");
            console.error("Error fetching devices!", error);
        }
    };

    const updateUser = async () => {
        try {
            const response = await axios.put(
                `http://users.localhost/api/users/${editingUser.id}`,
                user,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setUsers(users.map(u => (u.id === editingUser.id ? response.data : u)));
            setUser({ name: '', email: '', password: '' });
            setEditingUser(null);
        } catch (error) {
            setError("Error updating user!");
            console.error("Error updating user!", error);
        }
    };

    const deleteUser = async (id) => {
        try {
            await axios.delete(`http://users.localhost/api/users/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUsers(users.filter(u => u.id !== id));
        } catch (error) {
            setError("Error deleting user!");
            console.error("Error deleting user!", error);
        }
    };

    const assignDeviceToUser = async () => {
        if (!selectedDevice || !selectedUserId) {
            setError('Please select both a user and a device');
            return;
        }

        try {
            const response = await axios.post(
                `http://devices.localhost/api/devices/assign`,
                { userId: selectedUserId, deviceId: selectedDevice.id },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            if (response.status === 200) {
                alert('Device assigned successfully!');
                fetchDevices();
            }
        } catch (error) {
            setError("Error assigning device: " + error.response?.data?.message || "Device is already assigned.");
            console.error('Error assigning device:', error);
        }
    };

    const handleEdit = (u) => {
        setEditingUser(u);
        setUser({ name: u.name, email: u.email, password: '' });
    };

    const goToChat = (userId) => {
        navigate(`/chat/${userId}`);
    };

    return (
        <div className="user-container">
            <h2>User Manager</h2>

            {error && <div className="error-message">{error}</div>}

            <div className="form-group">
                <input
                    type="text"
                    placeholder="User Name"
                    value={user.name}
                    onChange={(e) => setUser({ ...user, name: e.target.value })}
                />
                <input
                    type="email"
                    placeholder="Email"
                    value={user.email}
                    onChange={(e) => setUser({ ...user, email: e.target.value })}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={user.password}
                    onChange={(e) => setUser({ ...user, password: e.target.value })}
                />
            </div>

            <div className="button-group">
                {editingUser ? <button onClick={updateUser}>Update User</button> : null}
            </div>

            <h3>Assign Device to User</h3>
            <div className="form-group">
                <select value={selectedUserId} onChange={(e) => setSelectedUserId(e.target.value)}>
                    <option value="">Select User</option>
                    {users.map(u => (
                        <option key={u.id} value={u.id}>{u.name}</option>
                    ))}
                </select>
            </div>

            <div className="form-group">
                <select
                    value={selectedDevice ? selectedDevice.id : ''}
                    onChange={(e) => setSelectedDevice(devices.find(d => d.id === parseInt(e.target.value)))}
                >
                    <option value="">Select Device</option>
                    {devices.map(d => (
                        <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                </select>
            </div>

            <div className="button-group">
                <button onClick={assignDeviceToUser}>Assign Device</button>
            </div>

            <table className="user-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map(u => (
                    <tr key={u.id}>
                        <td>{u.name}</td>
                        <td>{u.email}</td>
                        <td>
                            <button className="edit-button" onClick={() => handleEdit(u)}>Edit</button>
                            <button className="delete-button" onClick={() => deleteUser(u.id)}>Delete</button>
                            <button className="chat-button" onClick={() => goToChat(u.id)}>Chat</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default UserManager;
