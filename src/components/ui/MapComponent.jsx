"use client";

import React, { useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';

// Fix for default marker icons in leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

// Custom Neon Marker
const neonIcon = new L.DivIcon({
  className: 'neon-marker',
  html: '<div style="width: 16px; height: 16px; background: #FF6B00; border-radius: 50%; box-shadow: 0 0 15px #FF6B00, 0 0 30px #FF6B00;"></div>',
  iconSize: [16, 16],
  iconAnchor: [8, 8]
});

const MapComponent = ({ workers }) => {
  // Center roughly on India
  const defaultCenter = [20.5937, 78.9629];
  
  return (
    <div style={{ height: '500px', width: '100%', borderRadius: '8px', overflow: 'hidden', border: '1px solid var(--border)' }}>
      <MapContainer center={defaultCenter} zoom={5} style={{ height: '100%', width: '100%' }}>
        <TileLayer
          attribution='&copy; <a href="https://carto.com/">CartoDB</a>'
          url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
        />
        {workers.map((worker, index) => {
          // Since we don't have real lat/lng in the API, we'll assign random coordinates within India for demo purposes
          // or use pincode to geocode. For this visual demo, we'll generate stable pseudo-random coords based on worker id
          const seed = worker.id || index;
          const lat = 10 + (seed % 20); // 10 to 30
          const lng = 70 + (seed % 20); // 70 to 90
          return (
            <Marker key={worker.id} position={[lat, lng]} icon={neonIcon}>
              <Popup>
                <div style={{ color: '#0A0B10', fontWeight: 'bold' }}>
                  {worker.name.toUpperCase()} <br/>
                  {worker.skills?.map(s => s.name).join(', ')} <br/>
                  ₹{worker.dailyRate}/DAY
                </div>
              </Popup>
            </Marker>
          );
        })}
      </MapContainer>
    </div>
  );
};

export default MapComponent;
