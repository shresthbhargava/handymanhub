"use client";

import React from 'react';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { ArrowUpRight } from 'lucide-react';
import './WorkerCard.css';

const WorkerCard = ({ worker, index = 0, onBookClick }) => {
  const router = useRouter();

  const handleBook = () => {
    if (onBookClick) {
      onBookClick(worker);
    } else {
      router.push('/dashboard');
    }
  };

  return (
    <motion.div 
      className="worker-card-editorial"
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: "-50px" }}
      transition={{ duration: 0.5, delay: index * 0.1, ease: [0.22, 1, 0.36, 1] }}
      whileHover={{ y: -5 }}
    >
      <div className="worker-card-img">
        {/* Authentic Indian craftsmanship look */}
        <img 
          src={`https://images.unsplash.com/photo-1560060136-2e864ee055ce?q=80&w=800&auto=format&fit=crop&ixlib=rb-4.0.3&sig=${worker.id}`} 
          alt={worker.name}
        />
        {worker.available ? (
          <div className="status-dot available"></div>
        ) : (
          <div className="status-dot unavailable"></div>
        )}
      </div>
      <div className="worker-card-content">
        <div className="worker-header">
          <h3 className="worker-name" style={{ color: 'white', fontWeight: 'bold' }}>{worker.name}</h3>
          <span className="worker-rate mono" style={{ color: 'white', fontWeight: 'bold' }}>₹{worker.dailyRate}/DAY</span>
        </div>
        <div className="worker-meta">
          <span className="worker-skill" style={{ color: 'var(--brand-accent)' }}>{worker.pincode}</span>
        </div>
        
        {worker.contractor?.name && (
            <div className="worker-contractor" style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
              AGENCY: {worker.contractor.name.toUpperCase()}
            </div>
        )}
        <div className="worker-action" style={{ marginTop: '16px' }}>
          <button 
            className="btn-text-editorial"
            onClick={handleBook}
          >
            BOOK NOW <ArrowUpRight size={16} />
          </button>
        </div>
      </div>
    </motion.div>
  );
};

export default WorkerCard;
