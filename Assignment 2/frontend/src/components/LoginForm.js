import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser } from '../api/login-api';

const LoginForm = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();

        const response = await loginUser(email, password);

        if (response.success) {
            const userData = response.data;
            console.log("Login successful", userData);

            localStorage.setItem("user", JSON.stringify(userData));

            if (userData.user.role === "ADMIN") {
                navigate("/admin");
            } else if (userData.user.role === "USER") {
                navigate("/user");
            } else {
                setError("Unknown role.");
            }
        } else {
            setError(response.message);
            setSuccess('');
        }
    };

    return (
        <form onSubmit={handleLogin}>
            <div className="login-field">
                <label htmlFor="email">Email:</label>
                <input
                    type="email"
                    id="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="Enter your email"
                    required
                />
            </div>
            <div className="login-field">
                <label htmlFor="password">Password:</label>
                <input
                    type="password"
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    required
                />
            </div>
            {error && <div className="errors">{error}</div>}
            {success && <div className="success">{success}</div>}
            <button type="submit" className="button-login">Login</button>
        </form>
    );
};

export default LoginForm;
