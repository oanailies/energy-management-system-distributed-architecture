import React, { useState, useEffect } from "react";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";
import "../styles/DeviceManager.css";

const DeviceManager = () => {
    const [devices, setDevices] = useState([]);
    const [device, setDevice] = useState({
        name: "",
        description: "",
        address: "",
        maxHourlyConsumption: 0,
    });
    const [editingDevice, setEditingDevice] = useState(null);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const token = localStorage.getItem("token");
    let userId = null;
    let userRole = null;

    if (token) {
        try {
            const decodedToken = jwtDecode(token);
            userId = decodedToken.id;
            userRole = decodedToken.role.includes("ROLE_") ? decodedToken.role : `ROLE_${decodedToken.role}`;
            console.log("User Role:", userRole);
        } catch (error) {
            console.error("Error decoding token:", error);
        }
    }

    useEffect(() => {
        if (!token) {
            navigate("/login");
        } else {
            fetchDevices();
        }
    }, [token, navigate]);

    const fetchDevices = async () => {
        try {
            const response = await axios.get("http://devices.localhost/api/devices", {
                headers: { Authorization: `Bearer ${token}` },
            });
            setDevices(response.data);
        } catch (error) {
            setError("Error fetching devices!");
            console.error("Error fetching devices!", error);
        }
    };

    const addDevice = async () => {
        if (!device.name || !device.address || device.maxHourlyConsumption <= 0) {
            setError("All fields are required!");
            return;
        }

        try {
            const response = await axios.post("http://devices.localhost/api/devices", device, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setDevices([...devices, response.data]);
            setDevice({
                name: "",
                description: "",
                address: "",
                maxHourlyConsumption: 0,
            });
        } catch (error) {
            setError("Error adding device!");
            console.error("Error adding device!", error);
        }
    };

    const updateDevice = async () => {
        try {
            const response = await axios.put(`http://devices.localhost/api/devices/${editingDevice.id}`, device, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setDevices(devices.map((dev) => (dev.id === editingDevice.id ? response.data : dev)));
            setDevice({
                name: "",
                description: "",
                address: "",
                maxHourlyConsumption: 0,
            });
            setEditingDevice(null);
        } catch (error) {
            setError("Error updating device!");
            console.error("Error updating device!", error);
        }
    };

    const deleteDevice = async (id) => {
        try {
            await axios.delete(`http://devices.localhost/api/devices/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setDevices(devices.filter((dev) => dev.id !== id));
        } catch (error) {
            setError("Error deleting device!");
            console.error("Error deleting device!", error);
        }
    };

    const unassignDevice = async (deviceId) => {
        try {
            await axios.delete(`http://devices.localhost/api/devices/unassign/${deviceId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchDevices();
        } catch (error) {
            setError("Error unassigning device!");
            console.error("Error unassigning device!", error);
        }
    };

    return (
        <div className="container">
            <h2>Device Manager</h2>
            {error && <div className="error-message">{error}</div>}

            {userRole === "ROLE_ADMIN" && (
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
                    <div className="button-group">
                        {editingDevice ? (
                            <button onClick={updateDevice}>Update Device</button>
                        ) : (
                            <button onClick={addDevice}>Add Device</button>
                        )}
                    </div>
                </div>
            )}

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
                {devices.map((dev) => (
                    <tr key={dev.id}>
                        <td>{dev.name}</td>
                        <td>{dev.description}</td>
                        <td>{dev.address}</td>
                        <td>{dev.maxHourlyConsumption}</td>
                        <td>{dev.userId ? <span>User ID: {dev.userId}</span> : <span>Available</span>}</td>
                        <td>
                            {userRole === "ROLE_ADMIN" && (
                                <>
                                    {dev.userId && (
                                        <button className="disconnect-button" onClick={() => unassignDevice(dev.id)}>
                                            Disconnect User
                                        </button>
                                    )}
                                    <button className="edit-button" onClick={() => setEditingDevice(dev)}>Edit</button>
                                    <button className="delete-button" onClick={() => deleteDevice(dev.id)}>Delete</button>
                                </>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default DeviceManager;
