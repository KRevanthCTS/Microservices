import api from '../api/client';

const analyticsService = {
    // Get KPIs
    getKPIs: async () => {
        const response = await api.get('/api/analytics/kpis');
        return response.data;
    },

    // Get Trends
    getTrend: async (metric) => {
        const response = await api.get(`/api/analytics/trends?metric=${metric}`);
        return response.data;
    },

    // Generate Report
    generateReport: async (metric) => {
        const response = await api.get(`/api/analytics/report?metric=${metric}`);
        return response.data;
    },

    // Get Reports History
    getReportsHistory: async () => {
        const response = await api.get('/api/analytics/reports');
        return response.data;
    },

    // Get Users History
    getUsersHistory: async () => {
        const response = await api.get('/api/analytics/users');
        return response.data;
    },

    // Get Offers History
    getOffersHistory: async () => {
        const response = await api.get('/api/analytics/offers');
        return response.data;
    },

    // Get Redemptions History
    getRedemptionsHistory: async () => {
        const response = await api.get('/api/analytics/redemptions');
        return response.data;
    }
};

export default analyticsService;
