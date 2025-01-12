const API_BASE_URL = "http://localhost:5000/api";

// Helper function for making API requests
async function apiRequest(endpoint, method = "GET", body = null, token = null) {
    const headers = {
        "Content-Type": "application/json",
    };

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null,
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || "Something went wrong");
    }

    return response.json();
}

// Authentication
export async function register(userData) {
    return apiRequest("auth/register", "POST", userData);
}

export async function login(credentials) {
    return apiRequest("auth/login", "POST", credentials);
}

// Coins
export async function getCoins() {
    return apiRequest("coins", "GET");
}

export async function getCoinDetails(coinId) {
    return apiRequest(`coins/${coinId}`, "GET");
}

// Cart
export async function addToCart(coinId, token) {
    return apiRequest("cart/add", "POST", { coinId }, token);
}

export async function removeFromCart(coinId, token) {
    return apiRequest("cart/remove", "POST", { coinId }, token);
}