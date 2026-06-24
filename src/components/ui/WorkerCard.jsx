"use client";

import React from 'react';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { ArrowUpRight } from 'lucide-react';
import './WorkerCard.css';

const WorkerCard = ({ worker, index = 0 }) => {
  const router = useRouter();

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
          alt={worker.firstName} 
        />
        {worker.isAvailable ? (
          <div className="status-dot available"></div>
        ) : (
          <div className="status-dot unavailable"></div>
        )}
      </div>
      <div className="worker-card-content">
        <div className="worker-header">
          <h3 className="worker-name">{worker.firstName} {worker.lastName}</h3>
          <span className="worker-rate mono">₹{worker.dailyRate}/DAY</span>
        </div>
        <div className="worker-meta">
          <span className="worker-skill">{worker.skill?.name?.toUpperCase()}</span>
          <span className="worker-exp">{worker.experienceYears} YRS EXP</span>
        </div>
        <div className="worker-action">
          <button 
            className="btn-text-editorial"
            onClick={() => router.push('/dashboard')}
          >
            BOOK NOW <ArrowUpRight size={16} />
          </button>
        </div>
      </div>
    </motion.div>
  );
};

export default WorkerCard;
