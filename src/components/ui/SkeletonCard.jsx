import React from 'react';
import './SkeletonCard.css';

const SkeletonCard = ({ count = 1 }) => {
  return (
    <>
      {Array.from({ length: count }).map((_, index) => (
        <div key={index} className="skeleton-card">
          <div className="skel-top">
            <div className="skel-line w-1/2 h-lg"></div>
            <div className="skel-pill"></div>
          </div>
          <div className="skel-mid">
            <div className="skel-line w-1/3"></div>
            <div className="skel-line w-1/4"></div>
          </div>
          <div className="skel-bottom">
            <div className="skel-line w-1/3 h-lg"></div>
            <div className="skel-btn"></div>
          </div>
        </div>
      ))}
    </>
  );
};

export default SkeletonCard;
