"use client";

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../context/AuthContext';
import { useApi } from '../hooks/useApi';
import apiClient from '../api/client';
import StatusBadge from '../components/ui/StatusBadge';
import Toast from '../components/ui/Toast';
import { motion } from 'framer-motion';
import './Dashboard.css';

const Dashboard = () => {
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  const [view, setView] = useState('bookings');
  const [toastMsg, setToastMsg] = useState('');

  const { data: bookingsData, loading: bookingsLoading, request: fetchBookings } = useApi(() => apiClient.get('/api/v1/bookings'));
  
  // New Booking State
  const [bookingType, setBookingType] = useState('worker');
  const [skillId, setSkillId] = useState('');
  const [entityId, setEntityId] = useState('');
  const [date, setDate] = useState('');
  const [durationDays, setDurationDays] = useState(1);
  const [address, setAddress] = useState('');
  const [notes, setNotes] = useState('');
  const [pincode, setPincode] = useState('');

  const { loading: createLoading, request: createBooking } = useApi((data) => apiClient.post('/api/v1/bookings', data));
  const { data: skillsData, request: fetchSkills } = useApi(() => apiClient.get('/api/v1/skills'));
  const { request: updateStatus } = useApi((id, status) => apiClient.patch(`/api/v1/bookings/${id}/status?status=${status}`));
  
  const { data: contractorsData, request: fetchContractors } = useApi(() => apiClient.get('/api/v1/contractors/verified'));

// AFTER
  const { data: workersSearchData, loading: workersSearchLoading, request: searchWorkers } = useApi((s, p) => {
    const params = new URLSearchParams();
    if (p) params.set('pincode', p);
    params.set('available', 'true');
    return apiClient.get(`/api/v1/workers?${params.toString()}`);
  });

  const skills = Array.isArray(skillsData) ? skillsData : skillsData?.content || [];
  const contractors = Array.isArray(contractorsData) ? contractorsData : contractorsData?.content || [];
  const searchedWorkers = Array.isArray(workersSearchData) ? workersSearchData : workersSearchData?.content || [];

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    } else {
      fetchBookings().catch(console.error);
      fetchSkills().catch(console.error);
      fetchContractors().catch(console.error);
    }
  }, [isAuthenticated, router, fetchBookings, fetchSkills, fetchContractors]);

  const handleSearchWorkers = (e) => {
    e.preventDefault();
    if (skillId || pincode) {
      searchWorkers(skillId, pincode).catch(console.error);
    }
  };

  const handleCreateBooking = async (e) => {
    e.preventDefault();
    if (!user || !user.customerId) {
      setToastMsg('ERROR: CUSTOMER ID MISSING.');
      return;
    }
    if (!entityId) {
      setToastMsg('ERROR: PLEASE SELECT A PROFESSIONAL OR AGENCY.');
      return;
    }
    try {
      const payload = { 
        customerId: user.customerId,
        skillId: skillId || null, 
        date, 
        scheduledDate: date,
        durationDays: Number(durationDays),
        address,
        notes
      };
      if (bookingType === 'worker') payload.workerId = entityId;
      else payload.contractorId = entityId;
      
      await createBooking(payload);
      setToastMsg('BOOKING CREATED.');
      setView('bookings');
      setSkillId(''); setEntityId(''); setDate(''); setAddress(''); setNotes(''); setDurationDays(1); setPincode('');
      fetchBookings(); // Refresh bookings
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.error || err.message || 'ERROR CREATING BOOKING.';
      setToastMsg(msg);
    }
  };

  const handleCancel = async (id) => {
    try {
      await updateStatus(id, 'CANCELLED');
      setToastMsg('BOOKING CANCELLED.');
      fetchBookings();
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.error || err.message || 'ERROR CANCELLING BOOKING.';
      setToastMsg(msg);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const d = new Date(dateString);
    return d.toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' });
  };

  // Get tomorrow's date for minimum date constraint
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split('T')[0];

  return (
    <div className="dashboard-view-editorial">
      <Toast message={toastMsg} onClose={() => setToastMsg('')} />
      
      <div className="dashboard-header-editorial">
        <motion.h1 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          {view === 'bookings' ? 'YOUR BOOKINGS.' : view === 'new' ? 'NEW BOOKING.' : 'PROFILE.'}
        </motion.h1>
        <div className="view-toggles-editorial">
          <button className={`view-toggle-btn ${view === 'bookings' ? 'active' : ''}`} onClick={() => setView('bookings')}>
            MY BOOKINGS
          </button>
          <button className={`view-toggle-btn ${view === 'new' ? 'active' : ''}`} onClick={() => setView('new')}>
            NEW BOOKING
          </button>
          <button className={`view-toggle-btn ${view === 'profile' ? 'active' : ''}`} onClick={() => setView('profile')}>
            PROFILE
          </button>
        </div>
      </div>

      <div className="dashboard-body-editorial">
        {view === 'bookings' && (
          <motion.div 
            className="bookings-table-wrapper"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
            {bookingsLoading ? <div className="label">LOADING...</div> : (
              <table className="editorial-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>DATE</th>
                    <th>DURATION</th>
                    <th>PROFESSIONAL</th>
                    <th>ADDRESS</th>
                    <th>STATUS</th>
                    <th>ACTION</th>
                  </tr>
                </thead>
                <tbody>
                  {bookingsData?.content?.length > 0 ? bookingsData.content.map(booking => (
                    <tr key={booking.id}>
                      <td className="mono">#{String(booking.id).substring(0,6)}</td>
                      <td className="mono">{formatDate(booking.scheduledDate || booking.date)}</td>
                      <td className="mono">{booking.durationDays || 1} DAYS</td>
                      <td>{booking.workerName || booking.contractorName || 'ASSIGNING...'}</td>
                      <td className="text-muted">{booking.address}</td>
                      <td><StatusBadge status={booking.status} /></td>
                      <td>
                        {(booking.status === 'PENDING' || booking.status === 'CONFIRMED') && (
                          <button 
                            className="btn-text-danger"
                            onClick={() => handleCancel(booking.id)}
                          >
                            CANCEL
                          </button>
                        )}
                      </td>
                    </tr>
                  )) : (
                    <tr>
                      <td colSpan="7" className="empty-cell">NO BOOKINGS FOUND.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            )}
          </motion.div>
        )}

        {view === 'new' && (
          <motion.div 
            className="new-booking-editorial"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
            <form className="form-editorial" onSubmit={handleCreateBooking}>
              <div className="form-group-editorial">
                <label className="label">TYPE</label>
                <div className="type-toggles-stark">
                  <button 
                    type="button" 
                    className={`stark-toggle ${bookingType === 'worker' ? 'active' : ''}`}
                    onClick={() => { setBookingType('worker'); setEntityId(''); }}
                  >
                    INDIVIDUAL
                  </button>
                  <button 
                    type="button" 
                    className={`stark-toggle ${bookingType === 'contractor' ? 'active' : ''}`}
                    onClick={() => { setBookingType('contractor'); setEntityId(''); }}
                  >
                    AGENCY
                  </button>
                </div>
              </div>

              {bookingType === 'worker' ? (
                <div className="worker-search-section" style={{ padding: '16px', border: '1px solid var(--border-color)', marginBottom: '24px' }}>
                  <h3 style={{ marginBottom: '16px', fontSize: '0.875rem', letterSpacing: '0.1em' }}>SEARCH WORKERS</h3>
                  <div className="form-row" style={{ display: 'flex', gap: '16px', alignItems: 'flex-end' }}>
                    <div className="form-group-editorial" style={{ flex: 1, marginBottom: 0 }}>
                      <label className="label">TRADE SKILL</label>
                      <select value={skillId} onChange={(e) => setSkillId(e.target.value)}>
                        <option value="">ALL SKILLS</option>
                        {skills.map(s => <option key={s.id} value={s.id}>{s.name.toUpperCase()}</option>)}
                      </select>
                    </div>
                    <div className="form-group-editorial" style={{ flex: 1, marginBottom: 0 }}>
                      <label className="label">PINCODE</label>
                      <input 
                        type="text" 
                        value={pincode} 
                        onChange={(e) => setPincode(e.target.value)} 
                        placeholder="ENTER PINCODE" 
                      />
                    </div>
                    <button type="button" className="btn btn-secondary" onClick={handleSearchWorkers} disabled={workersSearchLoading}>
                      {workersSearchLoading ? 'SEARCHING...' : 'SEARCH'}
                    </button>
                  </div>
                  
                  {searchedWorkers.length > 0 && (
                    <div className="worker-results" style={{ marginTop: '16px', maxHeight: '200px', overflowY: 'auto' }}>
                      <label className="label">SELECT A WORKER</label>
                      <div className="radio-list">
                        {searchedWorkers.map(w => (
                          <label key={w.id} style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '8px 0', borderBottom: '1px solid var(--border-color-light)' }}>
                            <input type="radio" name="workerSelect" value={w.id} checked={entityId === String(w.id)} onChange={(e) => setEntityId(e.target.value)} required />
                            
                            <span>{w.name} - ₹{w.dailyRate}/DAY</span>
                          </label>
                        ))}
                      </div>
                    </div>
                  )}
                  {workersSearchData && searchedWorkers.length === 0 && (
                     <p style={{ marginTop: '16px', fontSize: '0.875rem' }}>No workers found for this criteria.</p>
                  )}
                </div>
              ) : (
                <div className="contractor-select-section" style={{ padding: '16px', border: '1px solid var(--border-color)', marginBottom: '24px' }}>
                  <h3 style={{ marginBottom: '16px', fontSize: '0.875rem', letterSpacing: '0.1em' }}>SELECT AGENCY</h3>
                  {contractors.length > 0 ? (
                    <div className="radio-list" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                      {contractors.map(c => (
                        <label key={c.id} style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '8px 0', borderBottom: '1px solid var(--border-color-light)' }}>
                          <input type="radio" name="contractorSelect" value={c.id} checked={entityId === String(c.id)} onChange={(e) => setEntityId(e.target.value)} required />
                          <span>{c.name.toUpperCase()} (CREW: {c.crewSize || '10+'})</span>
                        </label>
                      ))}
                    </div>
                  ) : (
                    <p style={{ fontSize: '0.875rem' }}>No agencies available.</p>
                  )}
                </div>
              )}

              <div className="form-row" style={{ display: 'flex', gap: '16px' }}>
                <div className="form-group-editorial" style={{ flex: 1 }}>
                  <label className="label">SCHEDULED DATE</label>
                  <input type="date" min={minDate} className="date-input" required value={date} onChange={(e) => setDate(e.target.value)} />
                </div>
                <div className="form-group-editorial" style={{ flex: 1 }}>
                  <label className="label">DURATION (DAYS)</label>
                  <input type="number" min="1" required value={durationDays} onChange={(e) => setDurationDays(e.target.value)} />
                </div>
              </div>
              
              <div className="form-group-editorial">
                <label className="label">ADDRESS</label>
                <textarea rows={2} required value={address} onChange={(e) => setAddress(e.target.value)} placeholder="FULL ADDRESS" style={{ resize: 'vertical' }} />
              </div>

              <div className="form-group-editorial">
                <label className="label">NOTES (OPTIONAL)</label>
                <textarea rows={2} value={notes} onChange={(e) => setNotes(e.target.value)} placeholder="ANY SPECIFIC REQUIREMENTS?" style={{ resize: 'vertical' }} />
              </div>
              
              <button type="submit" className="btn btn-primary" style={{ marginTop: '32px' }} disabled={createLoading}>
                {createLoading ? 'SUBMITTING...' : 'CONFIRM BOOKING'}
              </button>
            </form>
          </motion.div>
        )}

        {view === 'profile' && (
          <motion.div 
            className="profile-editorial"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
            <div className="profile-card" style={{ padding: '32px', border: '1px solid var(--border-color)' }}>
              <div style={{ marginBottom: '24px' }}>
                <label className="label" style={{ color: 'var(--text-secondary)' }}>FULL NAME</label>
                <div style={{ fontSize: '1.25rem', letterSpacing: '0.05em' }}>{user?.name?.toUpperCase()}</div>
              </div>
              <div style={{ marginBottom: '24px' }}>
                <label className="label" style={{ color: 'var(--text-secondary)' }}>EMAIL ADDRESS</label>
                <div style={{ fontSize: '1.1rem', letterSpacing: '0.05em', color: 'var(--brand-accent)' }}>{user?.email}</div>
              </div>
              <div style={{ marginBottom: '24px' }}>
                <label className="label" style={{ color: 'var(--text-secondary)' }}>ROLE</label>
                <div style={{ fontSize: '1.1rem', letterSpacing: '0.05em' }}>{user?.role}</div>
              </div>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
