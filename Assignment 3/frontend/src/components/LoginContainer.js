import React from 'react';
import LoginForm from './LoginForm';
import { Link } from 'react-router-dom'; 
import '../styles/Login.css';

const LoginContainer = () => {
    return (
        <div className="container-login">
            <div className="login-form">
                <h2 className="login-title">Login</h2>
                <LoginForm />
                <div className="register-link">
                    <p>Don't have an account? <Link to="/register">Sign up here</Link></p>
                </div>
            </div>
        </div>
    );
};

export default LoginContainer;
