import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import userService from '../services/userService';

const UserContext = createContext(null);

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) throw new Error('useUser must be used within a UserProvider');
  return context;
};

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [offers, setOffers] = useState([]);
  const [redemptions, setRedemptions] = useState([]);
  const [loading, setLoading] = useState(true);

  const isAuthenticated = () => !!localStorage.getItem('userId');

  // isSilent prevents the full-page "Loading..." splash during background updates
  const refreshAll = useCallback(async (isSilent = false) => {
    if (!isAuthenticated()) {
      setLoading(false);
      return;
    }

    if (!isSilent) setLoading(true); 

    try {
      const [userData, transData, offersData, redeemData] = await Promise.all([
        userService.getMe(),
        userService.getTransactions(),
        userService.getOffersForMyTier(),
        userService.getRedemptions()
      ]);

      setUser(userData);
      setTransactions(transData);
      setOffers(offersData);
      setRedemptions(redeemData);
    } catch (err) {
      console.error('UserContext: Sync failed', err);
    } finally {
      setLoading(false);
    }
  }, []);

  // Use this in Login.jsx to prevent seeing the previous user's data
  const loginUser = async () => {
    setUser(null);
    setTransactions([]);
    setOffers([]);
    setRedemptions([]);
    await refreshAll(false);
  };

  const claimPoints = async (code, pts, note) => {
    await userService.claimPoints(code, pts, note);
    await refreshAll(true); // Silent background refresh
  };

  const redeemOffer = async (offerId, store) => {
    await userService.redeemOffer(offerId, store);
    await refreshAll(true); // Silent background refresh
  };

  useEffect(() => {
    refreshAll();
  }, [refreshAll]);

  const value = { user, transactions, offers, redemptions, loading, refreshAll, claimPoints, redeemOffer, loginUser };

  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
};