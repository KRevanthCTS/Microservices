import api from '../api/client'

const userService = {
  getStoredId: () => {
    const id = localStorage.getItem('userId');
    return id ? parseInt(id, 10) : null;
  },

  getMe: async () => {
    const userId = userService.getStoredId();
    const response = await api.get(`/api/users/Customer/${userId}`);
    return response.data;
  },

  getTransactions: async () => {
    const userId = userService.getStoredId();
    const response = await api.get(`/api/users/transactions/user/${userId}`);
    return response.data;
  },

  getOffersForMyTier: async () => {
    const userId = userService.getStoredId();
    const profileResponse = await api.get(`/api/users/Customer/${userId}`);
    const tier = profileResponse.data.loyaltyTier || 'SILVER';
    // Matches your Java spelling: /teir/
    const response = await api.get(`/api/users/offers/teir/${tier}`);
    return response.data;
  },

  claimPoints: async (activityCode, points, note) => {
    const userId = userService.getStoredId();
    const response = await api.post(`/api/users/claim/user/${userId}`, {
      activationcode: activityCode,
      points: Number(points),
      note: note || "Claimed via web"
    });
    return response.data;
  },

  redeemOffer: async (offerId, store) => {
    const userId = userService.getStoredId();
    // Path: /redeem/offer/{offerId}/user/{userId}
    const response = await api.post(`/api/users/redeem/offer/${offerId}/user/${userId}`, {
      store: store,
      offerId: parseInt(offerId, 10)
    });
    return response.data;
  },

  getRedemptions: async () => {
    const userId = userService.getStoredId();
    const response = await api.get(`/api/users/redemptions/user/${userId}`);
    return response.data;
  }
};

export default userService;