const API_BASE_URL = 'http://users.localhost/api/users';

export const registerUser = async (name, email, password) => {
    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password }),
        });

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else {
            return { success: false, message: data.message || "Registration failed" };
        }
    } catch (error) {
        console.error("API call error:", error);
        return { success: false, message: "Network error" };
    }
};

export const loginUser = async (email, password) => {
    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem("token", data.token);
            localStorage.setItem("user", JSON.stringify(data.user));

            return { success: true, data };
        } else {
            return { success: false, message: data.message || "Login failed" };
        }
    } catch (error) {
        console.error("API call error:", error);
        return { success: false, message: "Network error" };
    }
};


export const authFetch = async (url, method = "GET", body = null) => {
    const token = localStorage.getItem("token");

    if (!token) {
        console.error(" No token found in local storage");
        return { success: false, message: "Unauthorized" };
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };

    try {
        const response = await fetch(url, {
            method,
            headers,
            body: body ? JSON.stringify(body) : null,
        });

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else {
            return { success: false, message: data.message || "Request failed" };
        }
    } catch (error) {
        console.error("API call error:", error);
        return { success: false, message: "Network error" };
    }
};
