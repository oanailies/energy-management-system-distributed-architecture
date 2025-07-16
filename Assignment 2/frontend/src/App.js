
import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LoginContainer from './components/LoginContainer'; 
import RegisterForm from './components/RegisterForm'; 
import AdminPage from './user/AdminPage';
import UserPage from './user/UserPage';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<LoginContainer />} />
                <Route path="/register" element={<RegisterForm />} /> {}
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/user" element={<UserPage />} />
            </Routes>
        </Router>
    );
}

export default App;
