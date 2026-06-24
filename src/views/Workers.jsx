"use client";

import React, { useState, useEffect } from 'react';
import { useApi } from '../hooks/useApi';
import apiClient from '../api/client';
import WorkerCard from '../components/ui/WorkerCard';
import SkeletonCard from '../components/ui/SkeletonCard';
import { motion } from 'framer-motion';
import './Workers.css';

const Workers = () => {
  const [page, setPage] = useState(0);
  const size = 12;
  
  const { data: workersData, loading: workersLoading, request: fetchWorkers } = useApi((p, s) => apiClient.get(`/api/v1/workers?page=${p}&size=${s}`));
  const { data: contractorsData, loading: contractorsLoading, request: fetchContractors } = useApi(() => apiClient.get('/api/v1/contractors/verified'));

  useEffect(() => {
    fetchWorkers(page, size).catch(console.error);
    fetchContractors().catch(console.error);
  }, [page, fetchWorkers, fetchContractors]);

  const workers = workersData?.content || [];
  const contractors = contractorsData || [];

  return (
    <div className="workers-page-editorial">
      <div className="container">
        
        {/* Agencies Section */}
        <section className="agencies-section-editorial">
          <motion.div 
            className="section-header-editorial"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <h2>VERIFIED AGENCIES.</h2>
          </motion.div>
          <div className="agencies-grid-editorial">
            {contractorsLoading ? (
              <SkeletonCard count={3} />
            ) : contractors.length > 0 ? (
              contractors.map((contractor, idx) => (
                <motion.div 
                  key={contractor.id} 
                  className="agency-card-editorial"
                  initial={{ opacity: 0, y: 20 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ delay: idx * 0.1 }}
                >
                  <div className="agency-top">
                    <h3>{contractor.name.toUpperCase()}</h3>
                    <div className="status-dot available"></div>
                  </div>
                  <div className="agency-meta">
                    <span className="label">CREW SIZE</span>
                    <span className="mono">{contractor.crewSize || '10+'} PROS</span>
                  </div>
                  <div className="agency-action">
                    <button className="btn-text-editorial">VIEW AGENCY &rarr;</button>
                  </div>
                </motion.div>
              ))
            ) : (
              <div className="empty-state-editorial">
                <p>NO VERIFIED AGENCIES AVAILABLE.</p>
              </div>
            )}
          </div>
        </section>

        {/* Workers Section */}
        <section className="all-workers-section-editorial">
          <div className="section-header-editorial border-top">
            <h2>ALL PROFESSIONALS.</h2>
          </div>
          
          <div className="results-grid-editorial">
            {workersLoading && page === 0 ? (
              <SkeletonCard count={6} />
            ) : workers.length > 0 ? (
              workers.map((worker, idx) => (
                <WorkerCard key={worker.id} worker={worker} index={idx} />
              ))
            ) : (
              <div className="empty-state-editorial">
                <p>NO PROFESSIONALS FOUND.</p>
              </div>
            )}
          </div>

          <div className="load-more-container-editorial">
            <button 
              className="btn btn-secondary" 
              onClick={() => setPage(p => p + 1)}
              disabled={workersLoading}
            >
              {workersLoading ? 'LOADING...' : 'LOAD MORE RESULTS'}
            </button>
          </div>
        </section>
      </div>
    </div>
  );
};

export default Workers;
