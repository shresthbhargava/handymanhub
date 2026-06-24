"use client";

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { useApi } from '../../hooks/useApi';
import apiClient from '../../api/client';
import NetworkCanvas from '../../components/ui/NetworkCanvas';
import { ChevronDown } from 'lucide-react';
import '../../views/Landing.css';

export default function LandingPage() {
  const router = useRouter();
  
  const { data: skillsData, request: fetchSkills } = useApi(() => apiClient.get('/api/v1/skills'));
  
  const [selectedSkill, setSelectedSkill] = useState('');
  const [pincode, setPincode] = useState('');
  const [openFaq, setOpenFaq] = useState(null);

  useEffect(() => {
    fetchSkills().catch(console.error);
  }, [fetchSkills]);
  // Add this line after the useEffect block:
  const skills = Array.isArray(skillsData) ? skillsData : skillsData?.content || [];

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

      {/* Unique Infinite Marquee */}
      <div className="marquee-cinematic">
        <div className="marquee-content">
          <span>ELECTRICIANS • PLUMBERS • CARPENTERS • PAINTERS • MECHANICS • TECHNICIANS • </span>
          <span>ELECTRICIANS • PLUMBERS • CARPENTERS • PAINTERS • MECHANICS • TECHNICIANS • </span>
          <span>ELECTRICIANS • PLUMBERS • CARPENTERS • PAINTERS • MECHANICS • TECHNICIANS • </span>
        </div>
      </div>

      {/* Frameless FAQ Section */}
      <section className="faq-frameless-section container">
        <div className="faq-frameless-header">
          <h2 className="faq-frameless-title">FREQUENTLY ASKED.</h2>
        </div>
        <div className="faq-frameless-list">
          {[
            {
              q: "How do I know a worker is trustworthy?",
              a: "Every worker is verified by our team before listing. We enforce strict background checks for absolute peace of mind."
            },
            {
              q: "What if the worker doesn't show up?",
              a: "You can cancel any booking before work begins. Payments are only finalized when you are satisfied."
            },
            {
              q: "How is pricing decided?",
              a: "Workers set their own daily rates. Transparency is built-in — no hidden fees or surprise charges."
            },
            {
              q: "Which cities are currently supported?",
              a: "HandymanHub operates exclusively in Delhi NCR, with upcoming expansions to major metropolitan zones."
            }
          ].map((faq, index) => (
            <div 
              key={index} 
              className={`faq-frameless-item clickable ${openFaq === index ? 'active' : ''}`}
              onClick={() => setOpenFaq(openFaq === index ? null : index)}
            >
              <div className="faq-frameless-question">
                <span>{faq.q}</span>
                <ChevronDown className="faq-frameless-icon" size={24} strokeWidth={1.5} />
              </div>
              <motion.div 
                className="faq-frameless-answer"
                initial={false}
                animate={{ height: openFaq === index ? 'auto' : 0, opacity: openFaq === index ? 1 : 0 }}
                transition={{ duration: 0.4, ease: [0.16, 1, 0.3, 1] }}
                style={{ overflow: 'hidden' }}
              >
                <div className="faq-answer-inner">{faq.a}</div>
              </motion.div>
            </div>
          ))}
        </div>
      </section>

    </div>
  );
}
