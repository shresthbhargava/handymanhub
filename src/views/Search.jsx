"use client";

import React, { useState, useEffect, Suspense } from 'react';
import { useSearchParams, useRouter, usePathname } from 'next/navigation';
import dynamic from 'next/dynamic';

const MapComponent = dynamic(() => import('../components/ui/MapComponent'), { ssr: false });
import { useApi } from '../hooks/useApi';
import apiClient from '../api/client';
import WorkerCard from '../components/ui/WorkerCard';
import SkeletonCard from '../components/ui/SkeletonCard';
import { motion } from 'framer-motion';
import './Search.css';

const SearchContent = () => {
  const searchParams = useSearchParams();
  const router = useRouter();
  const pathname = usePathname();

  const initialSkill = searchParams.get('skillId') || '';
  const initialPincode = searchParams.get('pincode') || '';

  const [skillId, setSkillId] = useState(initialSkill);
  const [pincode, setPincode] = useState(initialPincode);
  const [isAvailableOnly, setIsAvailableOnly] = useState(false);
  const [maxRate, setMaxRate] = useState(5000);

  const { data: skillsData, request: fetchSkills } = useApi(() => apiClient.get('/api/v1/skills'));
  const skills = Array.isArray(skillsData) ? skillsData : skillsData?.content || [];
  const { data: searchData, loading, request: searchWorkers } = useApi((s, p) => {
  const params = new URLSearchParams();
  if (s) params.set('skillId', s);
  if (p) params.set('pincode', p);
  return apiClient.get(`/api/v1/workers/search?${params.toString()}`);
});

  useEffect(() => {
    fetchSkills().catch(console.error);
  }, [fetchSkills]);

  useEffect(() => {
    if (skillId || pincode) {
      searchWorkers(skillId, pincode).catch(console.error);
    }
  }, [skillId, pincode, searchWorkers]);

  const handleApplyFilters = (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (skillId) params.set('skillId', skillId);
    if (pincode) params.set('pincode', pincode);
    router.push(`${pathname}?${params.toString()}`);
  };

  const workers = searchData?.content || [];

  const filteredWorkers = workers.filter(w => {
    if (isAvailableOnly && !w.isAvailable) return false;
    if (w.dailyRate > maxRate) return false;
    return true;
  });

  return (
    <div className="search-page-editorial">
      <div className="search-hero">
        <div className="container">
          <motion.h1
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="search-title"
          >
            FIND <span className="brand-accent">TALENT.</span>
          </motion.h1>
        </div>
      </div>

      <div className="container search-layout-editorial">
        <aside className="search-sidebar-editorial">
          <form onSubmit={handleApplyFilters} className="filter-form-editorial">
            <div className="filter-group-editorial">
              <span className="label">TRADE</span>
              <div className="radio-group-editorial">
                <label className={`radio-editorial ${skillId === '' ? 'active' : ''}`}>
                  <input type="radio" name="skill" value="" checked={skillId === ''} onChange={() => setSkillId('')} />
                  <span>ALL SKILLS</span>
                </label>
                {(Array.isArray(skillsData) ? skillsData : skillsData?.content || []).map(skill => (
                  <label key={skill.id} className={`radio-editorial ${skillId === String(skill.id) ? 'active' : ''}`}>
                    <input type="radio" name="skill" value={skill.id} checked={skillId === String(skill.id)} onChange={(e) => setSkillId(e.target.value)} />
                    <span>{skill.name.toUpperCase()}</span>
                  </label>
                ))}
              </div>
            </div>

            <div className="filter-group-editorial">
              <span className="label">LOCATION</span>
              <input
                type="text"
                value={pincode}
                onChange={(e) => setPincode(e.target.value)}
                placeholder="PINCODE"
              />
            </div>

            <div className="filter-group-editorial">
              <span className="label">MAX RATE (₹{maxRate})</span>
              <input
                type="range"
                min="500"
                max="10000"
                step="500"
                value={maxRate}
                onChange={(e) => setMaxRate(Number(e.target.value))}
                className="stark-slider"
              />
            </div>

            <div className="filter-group-editorial">
              <label className={`checkbox-editorial ${isAvailableOnly ? 'active' : ''}`}>
                <input
                  type="checkbox"
                  checked={isAvailableOnly}
                  onChange={(e) => setIsAvailableOnly(e.target.checked)}
                />
                <div className="stark-checkbox"></div>
                <span>AVAILABLE NOW</span>
              </label>
            </div>

            <button type="submit" className="btn btn-primary btn-block">
              APPLY FILTERS
            </button>
          </form>
        </aside>

        <main className="search-main-editorial">
          <div className="search-header-editorial">
            <span className="results-count mono">
              {filteredWorkers.length} RESULTS {pincode && `IN ${pincode}`}
            </span>
            <select className="sort-select-editorial">
              <option value="recommended">RECOMMENDED</option>
              <option value="price_low">PRICE: LOW TO HIGH</option>
              <option value="price_high">PRICE: HIGH TO LOW</option>
            </select>
          </div>

          <div className="search-layout-split" style={{ display: 'flex', gap: '32px', marginTop: '32px' }}>
            <div className="results-list" style={{ flex: 1 }}>
              <div className="results-grid-editorial" style={{ gridTemplateColumns: '1fr' }}>
                {loading ? (
                  <SkeletonCard count={3} />
                ) : filteredWorkers.length > 0 ? (
                  filteredWorkers.map((worker, idx) => (
                    <WorkerCard key={worker.id} worker={worker} index={idx} />
                  ))
                ) : (
                  <div className="empty-state-editorial">
                    <h3>NO WORKERS FOUND.</h3>
                    <p>Try adjusting your search criteria.</p>
                    <button className="btn btn-outline" onClick={() => { setSkillId(''); setPincode(''); }}>
                      RESET
                    </button>
                  </div>
                )}
              </div>
            </div>
            <div className="map-container" style={{ flex: 1, display: 'block' }}>
              <MapComponent workers={filteredWorkers} />
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

const Search = () => {
  return (
    <Suspense fallback={<div>LOADING COMMAND CENTER...</div>}>
      <SearchContent />
    </Suspense>
  );
};

export default Search;
