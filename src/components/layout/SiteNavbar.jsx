"use client";

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '../../context/AuthContext';
import { motion } from 'framer-motion';
import './Navbar.css';

const Navbar = () => {
  const { isAuthenticated, logout } = useAuth();
  const pathname = usePathname();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  return (
    <motion.nav
      className="navbar-editorial"
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
    >
      <div className="container nav-content">
        <Link href="/" className="brand-logo">
          <span className="brand-text">HANDYMAN<span className="brand-accent">HUB.</span></span>
        </Link>
        <div className="nav-links">
          <Link href="/search" className={pathname === '/search' ? 'active' : ''}>SEARCH</Link>
          <Link href="/workers" className={pathname === '/workers' ? 'active' : ''}>WORKERS</Link>
          {mounted && (
            <>
              {isAuthenticated ? (
                <>
                  <Link href="/dashboard" className={pathname === '/dashboard' ? 'active' : ''}>DASHBOARD</Link>
                  <button onClick={logout} className="nav-logout">LOGOUT</button>
                </>
              ) : (
                <Link href="/login" className="btn btn-primary btn-cta">SIGN IN</Link>
              )}
            </>
          )}
        </div>
      </div>
    </motion.nav>
  );
};

export default Navbar;