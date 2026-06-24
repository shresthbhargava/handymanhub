"use client";

import React, { useEffect } from 'react';
import SiteNavbar from '../../components/layout/SiteNavbar';
import Footer from '../../components/layout/Footer';

export default function MainLayout({ children }) {
  useEffect(() => {
    if (typeof window !== 'undefined' && 'serviceWorker' in navigator) {
      navigator.serviceWorker.getRegistrations().then((registrations) => {
        for (let registration of registrations) {
          registration.unregister();
        }
      });
    }
  }, []);

  return (
    <>
      <SiteNavbar />
      {children}
      <Footer />
    </>
  );
}
