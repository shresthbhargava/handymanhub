"use client";

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../context/AuthContext';
import { useApi } from '../hooks/useApi';
import apiClient from '../api/client';
import Toast from '../components/ui/Toast';
import { motion } from 'framer-motion';
import './Admin.css';

const Admin = () => {
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('OVERVIEW');
  const [toastMsg, setToastMsg] = useState('');

  // Overview Stats
  const [totalBookings, setTotalBookings] = useState(0);
  const [pendingBookings, setPendingBookings] = useState(0);
  const [verifiedContractors, setVerifiedContractors] = useState(0);
  const [totalWorkers, setTotalWorkers] = useState(0);
  
  // Data States
  const [unverifiedContractorsList, setUnverifiedContractorsList] = useState([]);
  const [verifiedContractorsList, setVerifiedContractorsList] = useState([]);
  const [workersList, setWorkersList] = useState([]);
  const [workersPage, setWorkersPage] = useState(0);
  const [bookingsList, setBookingsList] = useState([]);
  const [bookingsPage, setBookingsPage] = useState(0);
  const [bookingStatusFilter, setBookingStatusFilter] = useState('');

  const [loadingOverview, setLoadingOverview] = useState(true);
  const [loadingContractors, setLoadingContractors] = useState(true);
  const [loadingWorkers, setLoadingWorkers] = useState(true);
  const [loadingBookings, setLoadingBookings] = useState(true);
  
  // Actions
  const { request: verifyContractorReq } = useApi((id) => apiClient.patch(`/api/v1/contractors/${id}/verify`));
  const { request: toggleWorkerAvailabilityReq } = useApi((id) => apiClient.patch(`/api/v1/workers/${id}/availability`));
  const { request: updateBookingStatusReq } = useApi((id, status) => apiClient.patch(`/api/v1/bookings/${id}/status?status=${status}`));

  useEffect(() => {
    // Check auth and user role
    if (!isAuthenticated || user?.role !== 'ADMIN') {
      router.push('/');
    }
  }, [isAuthenticated, user, router]);

  // Load Overview Data
  const loadOverview = async () => {
    setLoadingOverview(true);
    try {
      const [bkRes, bkPendingRes, conRes, workRes] = await Promise.all([
        apiClient.get('/api/v1/bookings'),
        apiClient.get('/api/v1/bookings?status=PENDING'),
        apiClient.get('/api/v1/contractors/verified'),
        apiClient.get('/api/v1/workers')
      ]);
      setTotalBookings(bkRes.data.totalElements || bkRes.data.content?.length || 0);
      setPendingBookings(bkPendingRes.data.length || bkPendingRes.data.content?.length || 0);
      setVerifiedContractors(conRes.data.length || conRes.data.content?.length || 0);
      setTotalWorkers(workRes.data.totalElements || workRes.data.content?.length || 0);
    } catch (e) {
      console.error(e);
      setToastMsg('Failed to load overview stats.');
    } finally {
      setLoadingOverview(false);
    }
  };

  const loadContractors = async () => {
    setLoadingContractors(true);
    try {
      const [allRes, verRes] = await Promise.all([
        apiClient.get('/api/v1/contractors'),
        apiClient.get('/api/v1/contractors/verified')
      ]);
      const all = allRes.data.content || allRes.data || [];
      const verified = verRes.data.content || verRes.data || [];
      const unverified = all.filter(c => !c.verified);
      setUnverifiedContractorsList(unverified);
      setVerifiedContractorsList(verified);
    } catch (e) {
      console.error(e);
      setToastMsg('Failed to load contractors.');
    } finally {
      setLoadingContractors(false);
    }
  };

  const loadWorkers = async (page = 0, append = false) => {
    setLoadingWorkers(true);
    try {
      const res = await apiClient.get(`/api/v1/workers?page=${page}&size=20`);
      const content = res.data.content || [];
      if (append) {
        setWorkersList(prev => [...prev, ...content]);
      } else {
        setWorkersList(content);
      }
      setWorkersPage(page);
    } catch (e) {
      console.error(e);
      setToastMsg('Failed to load workers.');
    } finally {
      setLoadingWorkers(false);
    }
  };

  const loadBookings = async (page = 0, append = false) => {
    setLoadingBookings(true);
    try {
      const res = await apiClient.get(`/api/v1/bookings?page=${page}&size=20`);
      const content = res.data.content || [];
      if (append) {
        setBookingsList(prev => [...prev, ...content]);
      } else {
        setBookingsList(content);
      }
      setBookingsPage(page);
    } catch (e) {
      console.error(e);
      setToastMsg('Failed to load bookings.');
    } finally {
      setLoadingBookings(false);
    }
  };

  useEffect(() => {
    if (user?.role === 'ADMIN') {
      if (activeTab === 'OVERVIEW') loadOverview();
      if (activeTab === 'CONTRACTORS') loadContractors();
      if (activeTab === 'WORKERS') loadWorkers(0, false);
      if (activeTab === 'BOOKINGS') loadBookings(0, false);
    }
  }, [activeTab, user]);

  const handleVerifyContractor = async (id) => {
    try {
      await verifyContractorReq(id);
      setToastMsg('Contractor verified successfully.');
      loadContractors(); // reload lists
    } catch (err) {
      setToastMsg(err.response?.data?.message || 'Failed to verify contractor.');
    }
  };

  const handleToggleWorkerAvailability = async (id) => {
    try {
      await toggleWorkerAvailabilityReq(id);
      setToastMsg('Worker availability updated.');
      loadWorkers(0, false); // Reload to get fresh state
    } catch (err) {
      setToastMsg(err.response?.data?.message || 'Failed to update availability.');
    }
  };

  const handleUpdateBookingStatus = async (id, status) => {
    try {
      await updateBookingStatusReq(id, status);
      setToastMsg(`Booking status updated to ${status}.`);
      loadBookings(0, false);
    } catch (err) {
      setToastMsg(err.response?.data?.message || 'Failed to update booking status.');
    }
  };

  if (!user || user.role !== 'ADMIN') return null;

  return (
    <div className="admin-layout">
      <Toast message={toastMsg} onClose={() => setToastMsg('')} />
      
      <aside className="admin-sidebar">
        <div className="admin-brand">
          ADMIN<span className="brand-accent">HUB.</span>
        </div>
        <nav className="admin-nav">
          {['OVERVIEW', 'CONTRACTORS', 'WORKERS', 'BOOKINGS', 'USERS'].map(tab => (
            <button 
              key={tab}
              className={`admin-nav-btn ${activeTab === tab ? 'active' : ''}`}
              onClick={() => setActiveTab(tab)}
            >
              {tab}
            </button>
          ))}
        </nav>
      </aside>

      <main className="admin-main">
        <header className="admin-header">
          <h1>{activeTab}</h1>
        </header>

        {activeTab === 'OVERVIEW' && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="admin-overview-grid">
            {loadingOverview ? <p>Loading stats...</p> : (
              <>
                <div className="stat-card">
                  <span className="stat-label">TOTAL BOOKINGS</span>
                  <span className="stat-value">{totalBookings}</span>
                </div>
                <div className="stat-card">
                  <span className="stat-label">PENDING BOOKINGS</span>
                  <span className="stat-value">{pendingBookings}</span>
                </div>
                <div className="stat-card">
                  <span className="stat-label">VERIFIED AGENCIES</span>
                  <span className="stat-value">{verifiedContractors}</span>
                </div>
                <div className="stat-card">
                  <span className="stat-label">TOTAL PROFESSIONALS</span>
                  <span className="stat-value">{totalWorkers}</span>
                </div>
              </>
            )}
          </motion.div>
        )}

        {activeTab === 'CONTRACTORS' && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
            {loadingContractors ? <p>Loading contractors...</p> : (
              <>
                <h3 style={{ marginBottom: '16px', letterSpacing: '0.05em', color: 'var(--text-secondary)' }}>ACTION REQUIRED ({unverifiedContractorsList.length})</h3>
                <div className="admin-table-wrapper">
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>NAME</th>
                        <th>COMPANY</th>
                        <th>EMAIL</th>
                        <th>PHONE</th>
                        <th>ACTION</th>
                      </tr>
                    </thead>
                    <tbody>
                      {unverifiedContractorsList.length > 0 ? unverifiedContractorsList.map(c => (
                        <tr key={c.id}>
                          <td>{c.name}</td>
                          <td>{c.companyName || 'N/A'}</td>
                          <td>{c.email}</td>
                          <td>{c.phone}</td>
                          <td>
                            <button className="admin-btn-outline" onClick={() => handleVerifyContractor(c.id)}>
                              VERIFY
                            </button>
                          </td>
                        </tr>
                      )) : (
                        <tr><td colSpan="5">No unverified contractors.</td></tr>
                      )}
                    </tbody>
                  </table>
                </div>

                <h3 style={{ marginBottom: '16px', letterSpacing: '0.05em', color: 'var(--text-secondary)' }}>VERIFIED AGENCIES ({verifiedContractorsList.length})</h3>
                <div className="admin-table-wrapper">
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>NAME</th>
                        <th>COMPANY</th>
                        <th>EMAIL</th>
                        <th>PHONE</th>
                        <th>STATUS</th>
                      </tr>
                    </thead>
                    <tbody>
                      {verifiedContractorsList.length > 0 ? verifiedContractorsList.map(c => (
                        <tr key={c.id}>
                          <td>{c.name}</td>
                          <td>{c.companyName || 'N/A'}</td>
                          <td>{c.email}</td>
                          <td>{c.phone}</td>
                          <td><span className="admin-badge verified">VERIFIED</span></td>
                        </tr>
                      )) : (
                        <tr><td colSpan="5">No verified contractors yet.</td></tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </>
            )}
          </motion.div>
        )}

        {activeTab === 'WORKERS' && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
            {loadingWorkers && workersPage === 0 ? <p>Loading workers...</p> : (
              <>
                <div className="admin-table-wrapper">
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>NAME</th>
                        <th>PHONE</th>
                        <th>PINCODE</th>
                        <th>RATE</th>
                        <th>AGENCY</th>
                        <th>STATUS</th>
                        <th>ACTION</th>
                      </tr>
                    </thead>
                    <tbody>
                      {workersList.map(w => (
                        <tr key={w.id}>
                          <td>{w.name}</td>
                          <td>{w.phone}</td>
                          <td>{w.pincode}</td>
                          <td>₹{w.dailyRate}/DAY</td>
                          <td>{w.contractor?.name || 'INDEPENDENT'}</td>
                          <td>
                            <span className={`admin-badge ${w.available ? 'verified' : 'unverified'}`}>
                              {w.available ? 'AVAILABLE' : 'UNAVAILABLE'}
                            </span>
                          </td>
                          <td>
                            <button className="admin-btn-outline" onClick={() => handleToggleWorkerAvailability(w.id)}>
                              TOGGLE AVAILABILITY
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                <button className="admin-btn-outline" onClick={() => loadWorkers(workersPage + 1, true)}>
                  LOAD MORE
                </button>
              </>
            )}
          </motion.div>
        )}

        {activeTab === 'BOOKINGS' && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
            <div style={{ marginBottom: '16px', display: 'flex', gap: '16px', alignItems: 'center' }}>
              <span className="stat-label">FILTER STATUS:</span>
              <select 
                className="admin-select"
                value={bookingStatusFilter} 
                onChange={(e) => setBookingStatusFilter(e.target.value)}
              >
                <option value="">ALL</option>
                <option value="PENDING">PENDING</option>
                <option value="CONFIRMED">CONFIRMED</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
            </div>
            
            {loadingBookings && bookingsPage === 0 ? <p>Loading bookings...</p> : (
              <>
                <div className="admin-table-wrapper">
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>CUSTOMER ID</th>
                        <th>PRO / AGENCY</th>
                        <th>DATE</th>
                        <th>DURATION</th>
                        <th>STATUS</th>
                        <th>ACTION</th>
                      </tr>
                    </thead>
                    <tbody>
                      {bookingsList
                        .filter(b => bookingStatusFilter ? b.status === bookingStatusFilter : true)
                        .map(b => (
                        <tr key={b.id}>
                          <td>#{String(b.id).substring(0,6)}</td>
                          <td className="mono">{b.customerId || 'N/A'}</td>
                          <td>{b.workerName || b.contractorName || 'UNASSIGNED'}</td>
                          <td>{new Date(b.scheduledDate || b.date).toLocaleDateString()}</td>
                          <td>{b.durationDays || 1} DAYS</td>
                          <td>
                            <span className={`admin-badge ${b.status === 'COMPLETED' ? 'verified' : b.status === 'PENDING' ? 'unverified' : ''}`}>
                              {b.status}
                            </span>
                          </td>
                          <td>
                            <select 
                              className="admin-select" 
                              value={b.status} 
                              onChange={(e) => handleUpdateBookingStatus(b.id, e.target.value)}
                            >
                              <option value="PENDING">PENDING</option>
                              <option value="CONFIRMED">CONFIRMED</option>
                              <option value="IN_PROGRESS">IN_PROGRESS</option>
                              <option value="COMPLETED">COMPLETED</option>
                              <option value="CANCELLED">CANCELLED</option>
                            </select>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                <button className="admin-btn-outline" onClick={() => loadBookings(bookingsPage + 1, true)}>
                  LOAD MORE
                </button>
              </>
            )}
          </motion.div>
        )}

        {activeTab === 'USERS' && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="placeholder-card">
            <h3>User management coming soon</h3>
            <p>This endpoint is currently under development.</p>
          </motion.div>
        )}
      </main>
    </div>
  );
};

export default Admin;
