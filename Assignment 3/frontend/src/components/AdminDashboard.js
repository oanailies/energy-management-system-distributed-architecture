
import React from 'react';
import DeviceManager from './DeviceManager';
import UserManager from './UserManager';
import '../styles/AdminDashboard.css'; 

const AdminDashboard = () => {
    return (
        <div className="dashboard-container">
            <DeviceManager />
            <UserManager />
        </div>
    );
};

export default AdminDashboard;
