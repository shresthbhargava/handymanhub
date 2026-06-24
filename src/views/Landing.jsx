"use client";
import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { useApi } from '../hooks/useApi';
import apiClient from '../api/client';
import { ArrowRight } from 'lucide-react';
import NetworkCanvas from '../components/ui/NetworkCanvas';
import './Landing.css';

const Landing = () => {
  const router = useRouter();
  
  const { data: skillsData, request: fetchSkills } = useApi(() => apiClient.get('/api/v1/skills'));
  const [selectedSkill, setSelectedSkill] = useState('');
  const [pincode, setPincode] = useState('');

  useEffect(() => {
    fetchSkills().catch(console.error);
  }, [fetchSkills]);

  const handleSearch = (e) => {
    e.preventDefault();
    router.push(`/search?skillId=${selectedSkill}&pincode=${pincode}`);
  };

  const staggerContainer = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: { staggerChildren: 0.1 }
    }
  };

  const itemFadeUp = {
    hidden: { opacity: 0, y: 50 },
    show: { opacity: 1, y: 0, transition: { duration: 0.8, ease: [0.16, 1, 0.3, 1] } }
  };

const skills = Array.isArray(skillsData) ? skillsData : skillsData?.content || [];
const categories = [
  { name: 'ELECTRICAL', id: skills.find(s => s.name.includes('Elect'))?.id || 1, img: '...' },
  { name: 'PLUMBING',   id: skills.find(s => s.name.includes('Plumb'))?.id || 2, img: '...' },
  { name: 'CARPENTRY',  id: skills.find(s => s.name.includes('Carpent'))?.id || 3, img: '...' },
  { name: 'MASONRY',    id: skills.find(s => s.name.includes('Civil'))?.id || 4, img: '...' },
];

  return (
    <div className="landing-cinematic">
      {/* Immersive Asymmetric Hero */}
      <section className="hero-interactive">
        <div className="hero-split-layout container">
          
          {/* Left: Content */}
          <motion.div 
            className="hero-text-interactive"
            variants={staggerContainer}
            initial="hidden"
            animate="show"
          >
            <motion.div variants={itemFadeUp} className="overline clickable">
              THE NEXT GEN WORKFORCE
            </motion.div>
            <motion.h1 variants={itemFadeUp} className="hero-title-cinematic">
              GLOBAL STANDARDS.<br />
              <span className="brand-accent">LOCAL EXPERTISE.</span>
            </motion.h1>
            
            <motion.form variants={itemFadeUp} className="search-form-cinematic glass-panel clickable" onSubmit={handleSearch}>
              <div className="search-inputs">
                <select 
                  value={selectedSkill} 
                  onChange={(e) => setSelectedSkill(e.target.value)}
                  required
                  className="clickable"
                >
                  <option value="" disabled>SELECT TRADE</option>
                  {skills.map(skill => (
                    <option key={skill.id} value={skill.id}>{skill.name.toUpperCase()}</option>
                  ))}
                </select>
                <div className="divider"></div>
                <input 
                  type="text" 
                  placeholder="ENTER PINCODE" 
                  value={pincode}
                  onChange={(e) => setPincode(e.target.value)}
                  required
                  className="clickable"
                />
              </div>
              <button type="submit" className="btn btn-primary clickable">
                INITIALIZE SEARCH
              </button>
            </motion.form>
          </motion.div>

          {/* Right: Interactive Canvas */}
          <motion.div 
            className="hero-canvas-interactive"
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 1.5, ease: [0.16, 1, 0.3, 1] }}
          >
            <div className="canvas-wrapper clickable">
              <NetworkCanvas />
              <div className="canvas-glow"></div>
            </div>
          </motion.div>

        </div>
      </section>

    </div>
  );
};

export default Landing;
