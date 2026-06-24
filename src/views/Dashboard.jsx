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
  
  const [bookingType, setBookingType] = useState('worker');
  const [skillId, setSkillId] = useState('');
  const [entityId, setEntityId] = useState('');
  const [date, setDate] = useState('');
  const [address, setAddress] = useState('');
  const { loading: createLoading, request: createBooking } = useApi((data) => apiClient.post('/api/v1/bookings', data));
  const { data: skillsData, request: fetchSkills } = useApi(() => apiClient.get('/api/v1/skills'));
  const { request: updateStatus } = useApi((id, status) => apiClient.patch(`/api/v1/bookings/${id}/status?status=${status}`));

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    } else {
      fetchBookings().catch(console.error);
      fetchSkills().catch(console.error);
    }
  }, [isAuthenticated, router, fetchBookings, fetchSkills]);

  const handleCreateBooking = async (e) => {
    e.preventDefault();
    try {
      const payload = { skillId, date, address };
      if (bookingType === 'worker') payload.workerId = entityId;
      else payload.contractorId = entityId;
      
      await createBooking(payload);
      setToastMsg('BOOKING CREATED.');
      setView('bookings');
      setSkillId(''); setEntityId(''); setDate(''); setAddress('');
    } catch (err) {
      setToastMsg('ERROR CREATING BOOKING.');
    }
  };

  const handleCancel = async (id) => {
    try {
      await updateStatus(id, 'CANCELLED');
      setToastMsg('BOOKING CANCELLED.');
      fetchBookings();
    } catch (err) {
      setToastMsg('ERROR CANCELLING BOOKING.');
    }
  };

  return (
    <div className="dashboard-view-editorial">
      <Toast message={toastMsg} onClose={() => setToastMsg('')} />
      
      <div className="dashboard-header-editorial">
        <motion.h1 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          {view === 'bookings' ? 'YOUR BOOKINGS.' : 'NEW BOOKING.'}
        </motion.h1>
        <div className="view-toggles-editorial">
          <button className={`view-toggle-btn ${view === 'bookings' ? 'active' : ''}`} onClick={() => setView('bookings')}>
            BOOKINGS
          </button>
          <button className={`view-toggle-btn ${view === 'new' ? 'active' : ''}`} onClick={() => setView('new')}>
            NEW
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
                      <td className="mono">{new Date(booking.date).toLocaleDateString()}</td>
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
                      <td colSpan="6" className="empty-cell">NO BOOKINGS FOUND.</td>
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
                    onClick={() => setBookingType('worker')}
                  >
                    INDIVIDUAL
                  </button>
                  <button 
                    type="button" 
                    className={`stark-toggle ${bookingType === 'contractor' ? 'active' : ''}`}
                    onClick={() => setBookingType('contractor')}
                  >
                    AGENCY
                  </button>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group-editorial">
                  <label className="label">TRADE SKILL</label>
                  <select required value={skillId} onChange={(e) => setSkillId(e.target.value)}>
                    <option value="" disabled>SELECT SKILL</option>
                    {skills.map(s => <option key={s.id} value={s.id}>{s.name.toUpperCase()}</option>)}
                  </select>
                </div>
                <div className="form-group-editorial">
                  <label className="label">SPECIFIC ID (OPTIONAL)</label>
                  <input 
                    type="text" 
                    value={entityId} 
                    onChange={(e) => setEntityId(e.target.value)} 
                    placeholder="ENTER ID" 
                  />
                </div>
              </div>

              <div className="form-group-editorial">
                <label className="label">DATE</label>
                <input type="date" className="date-input" required value={date} onChange={(e) => setDate(e.target.value)} />
              </div>
              
              <div className="form-group-editorial">
                <label className="label">ADDRESS</label>
                <input type="text" required value={address} onChange={(e) => setAddress(e.target.value)} placeholder="FULL ADDRESS" />
              </div>
              
              <button type="submit" className="btn btn-primary" style={{ marginTop: '32px' }} disabled={createLoading}>
                {createLoading ? 'SUBMITTING...' : 'CONFIRM BOOKING'}
              </button>
            </form>
          </motion.div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
