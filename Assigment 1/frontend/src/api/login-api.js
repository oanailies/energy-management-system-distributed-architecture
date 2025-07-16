
const API_BASE_URL = 'http://localhost:8080/api/users';

export const registerUser = async (name, email, password) => {
    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ name, email, password }),
        });

        if (response.ok) {
            const userData = await response.json();
            return { success: true, data: userData };
        } else {
            const errorMessage = await response.text();
            return { success: false, message: errorMessage };
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
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        });

        if (response.ok) {
            const userData = await response.json();
            return { success: true, data: userData };
        } else {
            const errorMessage = await response.text();
            return { success: false, message: errorMessage };
        }
    } catch (error) {
        console.error("API call error:", error);
        return { success: false, message: "Network error" };
    }
};
