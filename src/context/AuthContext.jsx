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
    setAuthToken(res.data.token);
    setTokenState(res.data.token);
    setUser({ name: res.data.name, email: res.data.email, role: res.data.role });
    return res.data;
  };

  const register = async (name, email, password) => {
    const res = await client.post('/api/v1/auth/register', { name, email, password });
    setAuthToken(res.data.token);
    setTokenState(res.data.token);
    setUser({ name: res.data.name, email: res.data.email, role: res.data.role });
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