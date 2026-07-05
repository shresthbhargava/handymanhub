"use client";

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '../context/AuthContext';
import Toast from '../components/ui/Toast';
import { motion } from 'framer-motion';
import './Auth.css';

const Auth = ({ defaultIsLogin = true }) => {
  const [isLogin, setIsLogin] = useState(defaultIsLogin);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [toastError, setToastError] = useState(null);

  const { login, register } = useAuth();
  const router = useRouter();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setToastError(null);
    setLoading(true);
    try {
      if (isLogin) {
               await login(email, password);
              const data = await login(email, password);
              if (data?.role === 'ADMIN') {
                   router.push('/admin');
                  return;
                }
      } else {
               await register(name, email, password);
              const data = await register(name, email, password);
              if (data?.role === 'ADMIN') {
                   router.push('/admin');
                  return;
                }
      }
      router.push('/dashboard');
    } catch (err) {
      setToastError(
          isLogin
              ? 'LOGIN FAILED. CHECK CREDENTIALS.'
              : 'REGISTRATION FAILED. TRY AGAIN.'
      );
    } finally {
      setLoading(false);
    }
  };
  

  return (
    <div className="auth-split-editorial">
      <Toast message={toastError} onClose={() => setToastError(null)} />

      <div className="auth-left">
        <div className="auth-image-wrapper">
          <img
            src="https://images.unsplash.com/photo-1591543620767-582b2e76369e?q=80&w=1200&auto=format&fit=crop"
            alt="Indian Craftsmanship"
          />
          <div className="auth-image-overlay"></div>
        </div>
        <div className="auth-quote">
          <h2>"CRAFT IS NOT A COMPROMISE."</h2>
          <span className="mono">EST. 2024</span>
        </div>
      </div>

      <div className="auth-right">
        <div className="auth-content-editorial">
          <div className="auth-header-editorial">
            <Link href="/" className="auth-brand-link">
              <span className="brand-text">HANDYMAN<span className="brand-accent">HUB.</span></span>
            </Link>
          </div>

          <motion.div
            className="auth-form-wrapper"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
            key={isLogin ? 'login' : 'register'}
          >
            <h1 className="auth-title">{isLogin ? 'SIGN IN.' : 'JOIN US.'}</h1>

            <form className="form-editorial" onSubmit={handleSubmit}>
              {!isLogin && (
                <div className="form-group-editorial">
                  <label className="label" htmlFor="name">FULL NAME</label>
                  <input
                    type="text"
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                  />
                </div>
              )}

              <div className="form-group-editorial">
                <label className="label" htmlFor="email">EMAIL ADDRESS</label>
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>

              <div className="form-group-editorial">
                <label className="label" htmlFor="password">PASSWORD</label>
                <input
                  type="password"
                  id="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>

              <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
                {loading ? 'PROCESSING...' : (isLogin ? 'AUTHENTICATE' : 'CREATE ACCOUNT')}
              </button>
            </form>

            <div className="auth-switch-editorial">
              <p>
                {isLogin ? "NEW HERE? " : "ALREADY A MEMBER? "}
                <button type="button" className="switch-btn-editorial" onClick={() => setIsLogin(!isLogin)}>
                  {isLogin ? 'REGISTER' : 'SIGN IN'}
                </button>
              </p>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default Auth;