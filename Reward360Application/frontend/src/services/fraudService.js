import api from '../api/client';

const fraudService = {
    // Get Transactions with filters
    getTransactions: async (params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        const response = await api.get(`/api/v1/transactions?${queryString}`);
        return response.data;
    },

    // Get Single Transaction
    getTransaction: async (id) => {
        const response = await api.get(`/api/v1/transactions/${id}`);
        return response.data;
    },

    // Block Transaction
    blockTransaction: async (id, reason) => {
        const response = await api.post(`/api/v1/transactions/${id}/block`, { reason });
        return response.data;
    },

    // Approve Transaction
    approveTransaction: async (id, reason) => {
        const response = await api.post(`/api/v1/transactions/${id}/review`, { reason }); // Using review endpoint as approve/review logic
        return response.data;
    },

    // Clear Transaction (Mark as cleared)
    clearTransaction: async (id) => {
        const response = await api.post(`/api/v1/transactions/${id}/clear`);
        return response.data;
    },

    // Mark for Review
    markForReview: async (id) => {
        const response = await api.post(`/api/v1/transactions/${id}/review`);
        return response.data;
    }
};

export default fraudService;
