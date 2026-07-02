import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApi } from '../../hooks/useApi';
import apiClient from '../../api/client';
import Toast from './Toast';
import { X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import './BookingModal.css';

const BookingModal = ({ isOpen, onClose, worker }) => {
  const { user } = useAuth();
  const [scheduledDate, setScheduledDate] = useState('');
  const [durationDays, setDurationDays] = useState(1);
  const [address, setAddress] = useState('');
  const [notes, setNotes] = useState('');
  const [toastMsg, setToastMsg] = useState('');
  const [skillId, setSkillId] = useState('');

  const { data: skillsData, request: fetchSkills } = useApi(() => apiClient.get('/api/v1/skills'));
  const skills = Array.isArray(skillsData) ? skillsData : skillsData?.content || [];
  const { loading: createLoading, request: createBooking } = useApi((data) => apiClient.post('/api/v1/bookings', data));

  useEffect(() => {
    if (isOpen) {
      fetchSkills().catch(console.error);
      setScheduledDate('');
      setDurationDays(1);
      setAddress('');
      setNotes('');
      setToastMsg('');
      setSkillId('');
    }
  }, [isOpen]);

  if (!isOpen || !worker) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!user || !user.customerId) {
      setToastMsg('ERROR: USER NOT LOGGED IN OR CUSTOMER ID MISSING.');
      return;
    }

    try {
      await createBooking({
        customerId: user.customerId,
        workerId: worker.id,
        skillId: skillId || null,
        scheduledDate: scheduledDate,
        durationDays: Number(durationDays),
        address,
        notes
      });
      setToastMsg('BOOKING CREATED SUCCESSFULLY.');
      setTimeout(() => {
        onClose();
      }, 1500);
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.error || err.message || 'ERROR CREATING BOOKING.';
      setToastMsg(msg);
    }
  };

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split('T')[0];

  return (
      <AnimatePresence>
        <div className="booking-modal-overlay">
          <Toast message={toastMsg} onClose={() => setToastMsg('')} />
          <motion.div
              className="booking-modal-content"
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
          >
            <button className="modal-close" onClick={onClose}>
              <X size={24} />
            </button>

            <div className="modal-header">
              <h2>BOOK {worker.name.toUpperCase()}</h2>
              <p className="mono brand-accent">{worker.pincode} • ₹{worker.dailyRate}/DAY</p>
            </div>

            <form onSubmit={handleSubmit} className="form-editorial">
              <div className="form-group-editorial">
                <label className="label">TRADE SKILL</label>
                <select required value={skillId} onChange={(e) => setSkillId(e.target.value)}>
                  <option value="">SELECT SKILL</option>
                  {skills.map(s => <option key={s.id} value={s.id}>{s.name.toUpperCase()}</option>)}
                </select>
              </div>

              <div className="form-row" style={{ display: 'flex', gap: '16px' }}>
                <div className="form-group-editorial" style={{ flex: 1 }}>
                  <label className="label">SCHEDULED DATE</label>
                  <input
                      type="date"
                      required
                      min={minDate}
                      value={scheduledDate}
                      onChange={(e) => setScheduledDate(e.target.value)}
                  />
                </div>
                <div className="form-group-editorial" style={{ flex: 1 }}>
                  <label className="label">DURATION (DAYS)</label>
                  <input
                      type="number"
                      required
                      min="1"
                      value={durationDays}
                      onChange={(e) => setDurationDays(e.target.value)}
                  />
                </div>
              </div>

              <div className="form-group-editorial">
                <label className="label">ADDRESS</label>
                <textarea
                    required
                    rows={2}
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    placeholder="ENTER FULL ADDRESS"
                    style={{ resize: 'vertical' }}
                />
              </div>

              <div className="form-group-editorial">
                <label className="label">NOTES (OPTIONAL)</label>
                <textarea
                    rows={2}
                    value={notes}
                    onChange={(e) => setNotes(e.target.value)}
                    placeholder="ANY SPECIFIC REQUIREMENTS?"
                    style={{ resize: 'vertical' }}
                />
              </div>

              <button type="submit" className="btn btn-primary btn-block" disabled={createLoading} style={{ marginTop: '24px' }}>
                {createLoading ? 'CONFIRMING...' : 'CONFIRM BOOKING'}
              </button>
            </form>
          </motion.div>
        </div>
      </AnimatePresence>
  );
};

export default BookingModal;