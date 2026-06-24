"use client";

import React from 'react';
import Link from 'next/link';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="footer-editorial">
      <div className="container">
        <div className="footer-top">
          <h2 className="footer-heading">BUILD<br/><span className="brand-accent">BETTER.</span></h2>
          <div className="footer-links-grid">
            <div className="footer-col">
              <span className="label">DIRECTORY</span>
              <Link href="/search">Find Trades</Link>
              <Link href="/workers">Agencies</Link>
            </div>
            <div className="footer-col">
              <span className="label">PLATFORM</span>
              <Link href="/login">Sign In</Link>
              <Link href="/register">Create Account</Link>
            </div>
            <div className="footer-col">
              <span className="label">LEGAL</span>
              <a href="#">Terms</a>
              <a href="#">Privacy</a>
            </div>
          </div>
        </div>
        <div className="footer-bottom">
          <p className="copyright">&copy; {new Date().getFullYear()} HANDYMANHUB. ALL RIGHTS RESERVED.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
