"use client";

import React, { useState, useEffect } from 'react';
import { useApi } from '../hooks/useApi';
import apiClient from '../api/client';
import WorkerCard from '../components/ui/WorkerCard';
import SkeletonCard from '../components/ui/SkeletonCard';
import BookingModal from '../components/ui/BookingModal';
import { useAuth } from '../context/AuthContext';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { Search as SearchIcon } from 'lucide-react';
import './Workers.css';

const Workers = () => {
  const [page, setPage] = useState(0);
  const size = 12;
  const router = useRouter();
  const { isAuthenticated } = useAuth();
  
  const [allWorkers, setAllWorkers] = useState([]);
  const [pincodeQuery, setPincodeQuery] = useState('');
  const [isSearching, setIsSearching] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedWorker, setSelectedWorker] = useState(null);
  
  const { data: workersData, loading: workersLoading, request: fetchWorkers } = useApi((p, s) => apiClient.get(`/api/v1/workers?page=${p}&size=${s}`));
  
  const { data: searchData, loading: searchLoading, request: searchWorkers } = useApi((pin) => apiClient.get(`/api/v1/workers?pincode=${pin}&available=true`));
  const { data: contractorsData, loading: contractorsLoading, request: fetchContractors } = useApi(() => apiClient.get('/api/v1/contractors/verified'));

  useEffect(() => {
    if (!isSearching) {
      fetchWorkers(page, size).catch(console.error);
    }
  }, [page, isSearching, fetchWorkers]);

  useEffect(() => {
    fetchContractors().catch(console.error);
  }, [fetchContractors]);

  useEffect(() => {
    if (!isSearching && workersData?.content) {
      if (page === 0) setAllWorkers(workersData.content);
      else setAllWorkers(prev => [...prev, ...workersData.content]);
    }
  }, [workersData, isSearching, page]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (pincodeQuery.trim()) {
      setIsSearching(true);
      searchWorkers(pincodeQuery).then(res => {
        setAllWorkers(res.content || []);
      }).catch(console.error);
    } else {
      setIsSearching(false);
      setPage(0);
      if (workersData?.content) setAllWorkers(workersData.content);
    }
  };

  const handleBookClick = (worker) => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    setSelectedWorker(worker);
    setIsModalOpen(true);
  };

  const workers = allWorkers;
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
          <div className="section-header-editorial border-top" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2>ALL PROFESSIONALS.</h2>
            <form onSubmit={handleSearch} style={{ display: 'flex', gap: '8px' }}>
              <input 
                type="text" 
                placeholder="PINCODE" 
                value={pincodeQuery}
                onChange={(e) => setPincodeQuery(e.target.value)}
                style={{ padding: '8px 16px', background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', color: 'white' }}
              />
              <button type="submit" className="btn btn-primary" style={{ padding: '8px 16px' }}>
                <SearchIcon size={20} />
              </button>
            </form>
          </div>
          
          <div className="results-grid-editorial">
            {(workersLoading && page === 0) || searchLoading ? (
              <SkeletonCard count={6} />
            ) : workers.length > 0 ? (
              workers.map((worker, idx) => (
                <WorkerCard key={worker.id} worker={worker} index={idx} onBookClick={handleBookClick} />
              ))
            ) : (
              <div className="empty-state-editorial">
                <p>NO PROFESSIONALS FOUND.</p>
              </div>
            )}
          </div>

          <div className="load-more-container-editorial">
            {!isSearching && workersData?.last === false && (
              <button 
                className="btn btn-secondary" 
                onClick={() => setPage(p => p + 1)}
                disabled={workersLoading}
              >
                {workersLoading ? 'LOADING...' : 'LOAD MORE RESULTS'}
              </button>
            )}
          </div>
        </section>
      </div>
      
      <BookingModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        worker={selectedWorker} 
      />
    </div>
  );
};

export default Workers;
