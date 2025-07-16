import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/DeviceManager.css';

const DeviceManager = () => {
    const [devices, setDevices] = useState([]);
    const [device, setDevice] = useState({
        name: '',
        description: '',
        address: '',
        maxHourlyConsumption: 0
    });
    const [editingDevice, setEditingDevice] = useState(null);

    const API_BASE_URL = 'http://devices.localhost/api/devices';

    useEffect(() => {
        fetchDevices();
    }, []);

    const fetchDevices = async () => {
        try {
            const response = await axios.get(API_BASE_URL);
            setDevices(response.data);
        } catch (error) {
            console.error("Error fetching devices:", error.response?.data || error.message);
        }
    };

    const addDevice = async () => {
        try {
            const response = await axios.post(API_BASE_URL, device);
            setDevices([...devices, response.data]);
            setDevice({
                name: '',
                description: '',
                address: '',
                maxHourlyConsumption: 0
            });
        } catch (error) {
            console.error("Error adding device:", error.response?.data || error.message);
        }
    };

    const updateDevice = async () => {
        try {
            const response = await axios.put(`${API_BASE_URL}/${editingDevice.id}`, device);
            setDevices(devices.map(dev => (dev.id === editingDevice.id ? response.data : dev)));
            setDevice({
                name: '',
                description: '',
                address: '',
                maxHourlyConsumption: 0
            });
            setEditingDevice(null);
        } catch (error) {
            console.error("Error updating device:", error.response?.data || error.message);
        }
    };

    const deleteDevice = async (id) => {
        try {
            await axios.delete(`${API_BASE_URL}/${id}`);
            setDevices(devices.filter(dev => dev.id !== id));
        } catch (error) {
            console.error("Error deleting device:", error.response?.data || error.message);
        }
    };

    const handleEdit = (dev) => {
        setEditingDevice(dev);
        setDevice({
            name: dev.name,
            description: dev.description,
            address: dev.address,
            maxHourlyConsumption: dev.maxHourlyConsumption
        });
    };

    const unassignDevice = async (deviceId) => {
        try {
            await axios.delete(`${API_BASE_URL}/unassign/${deviceId}`);
            fetchDevices();
        } catch (error) {
            console.error("Error unassigning device:", error.response?.data || error.message);
        }
    };

    return (
        <div className="container">
            <h2>Device Manager</h2>
            <div className="form-group">
                <input
                    type="text"
                    placeholder="Device Name"
                    value={device.name}
                    onChange={(e) => setDevice({ ...device, name: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Description"
                    value={device.description}
                    onChange={(e) => setDevice({ ...device, description: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Address"
                    value={device.address}
                    onChange={(e) => setDevice({ ...device, address: e.target.value })}
                />
                <input
                    type="number"
                    placeholder="Max Hourly Consumption"
                    value={device.maxHourlyConsumption}
                    onChange={(e) => setDevice({ ...device, maxHourlyConsumption: parseFloat(e.target.value) })}
                />
            </div>
            <div className="button-group">
                {editingDevice ? (
                    <button onClick={updateDevice}>Update Device</button>
                ) : (
                    <button onClick={addDevice}>Add Device</button>
                )}
            </div>

            <table className="device-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Address</th>
                        <th>Max Hourly Consumption</th>
                        <th>User Associated</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {devices.map(dev => (
                        <tr key={dev.id}>
                            <td>{dev.name}</td>
                            <td>{dev.description}</td>
                            <td>{dev.address}</td>
                            <td>{dev.maxHourlyConsumption}</td>
                            <td>
                                {dev.userId ? (
                                    <span>User ID: {dev.userId}</span>
                                ) : (
                                    <span>Available</span>
                                )}
                            </td>
                            <td>
                                {dev.userId ? (
                                    <button className="disconnect-button" onClick={() => unassignDevice(dev.id)}>
                                        Disconnect User
                                    </button>
                                ) : null}
                                <button className="edit-button" onClick={() => handleEdit(dev)}>Edit</button>
                                <button className="delete-button" onClick={() => deleteDevice(dev.id)}>Delete</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default DeviceManager;
