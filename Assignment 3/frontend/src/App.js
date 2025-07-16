import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import UserPage from './user/UserPage';
import AdminPage from './user/AdminPage';
import HistoricalEnergyPage from './components/HistoricalEnergyPage';
import LoginContainer from './components/LoginContainer';
import RegisterForm from './components/RegisterForm';
import ChatPage from './components/ChatPage';
import ChatPageUser from './components/ChatPageUser';
import ProtectedRoute from './components/ProtectedRoute';

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />


                <Route path="/login" element={<LoginContainer />} />
                <Route path="/register" element={<RegisterForm />} />


                <Route element={<ProtectedRoute allowedRoles={["USER"]} />}>
                    <Route path="/user" element={<UserPage />} />
                    <Route path="/user-chat" element={<ChatPageUser />} />
                </Route>


                <Route element={<ProtectedRoute allowedRoles={["ADMIN"]} />}>
                    <Route path="/admin" element={<AdminPage />} />
                </Route>


                <Route path="/historical-energy" element={<HistoricalEnergyPage />} />
                <Route path="/chat/:userId" element={<ChatPage />} />
            </Routes>
        </Router>
    );
};

export default App;
