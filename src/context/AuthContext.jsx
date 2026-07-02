"use client";

import React, { createContext, useContext, useState } from 'react';
import client, { setAuthToken, clearAuthToken } from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setTokenState] = useState(null);

  const isAuthenticated = !!token;

  const login = async (email, password) => {
    const res = await client.post('/api/v1/auth/login', { email, password });
    const token = res.data.token;
    setAuthToken(token);
    
    try {
      const customersRes = await client.get('/api/v1/customers');
      const customers = customersRes.data?.content || customersRes.data || [];
      const me = customers.find(c => c.email === res.data.email);
      if (me) res.data.customerId = me.id;
    } catch (e) {
      console.error("Failed to fetch customer ID", e);
    }

    setTokenState(token);
    setUser({ 
      name: res.data.name, 
      email: res.data.email, 
      role: res.data.role,
      customerId: res.data.customerId 
    });
    return res.data;
  };

  const register = async (name, email, password) => {
    const res = await client.post('/api/v1/auth/register', { name, email, password });
    const token = res.data.token;
    setAuthToken(token);

    try {
      const customersRes = await client.get('/api/v1/customers');
      const customers = customersRes.data?.content || customersRes.data || [];
      const me = customers.find(c => c.email === res.data.email);
      if (me) res.data.customerId = me.id;
    } catch (e) {
      console.error("Failed to fetch customer ID", e);
    }

    setTokenState(token);
    setUser({ 
      name: res.data.name, 
      email: res.data.email, 
      role: res.data.role,
      customerId: res.data.customerId
    });
    return res.data;
  };

  const logout = () => {
    clearAuthToken();
    setTokenState(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);