"use client";

import React from 'react';
import Link from 'next/link';
import { Wrench, Calendar, PlusSquare, Users, User } from 'lucide-react';
import './DashboardLayout.css';

const DashboardLayout = ({ children }) => {
  return (
    <div className="dashboard-editorial">
      <aside className="sidebar-editorial">
        <Link href="/" className="sidebar-brand">
          <span className="brand-text">HANDYMAN<span className="brand-accent">HUB.</span></span>
        </Link>

        <nav className="sidebar-nav">
          <div className="nav-group">
            <span className="label">PORTAL</span>
            <div className="sidebar-item active">
              <Calendar size={18} strokeWidth={2.5} />
              <span>BOOKINGS</span>
            </div>
            <div className="sidebar-item">
              <PlusSquare size={18} strokeWidth={2.5} />
              <span>NEW BOOKING</span>
            </div>
          </div>
          <div className="nav-group">
            <span className="label">DIRECTORY</span>
            <Link href="/workers" className="sidebar-item">
              <Users size={18} strokeWidth={2.5} />
              <span>WORKERS</span>
            </Link>
          </div>
          <div className="nav-group mt-auto">
            <div className="sidebar-item">
              <User size={18} strokeWidth={2.5} />
              <span>PROFILE</span>
            </div>
          </div>
        </nav>
      </aside>
      <main className="main-editorial">
        {children}
      </main>
    </div>
  );
};

export default DashboardLayout;
