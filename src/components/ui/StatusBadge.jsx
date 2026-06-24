import React from 'react';

const StatusBadge = ({ status }) => {
  let style = {
    padding: '6px 12px',
    fontSize: '0.75rem',
    fontWeight: '800',
    textTransform: 'uppercase',
    letterSpacing: '0.1em',
    display: 'inline-block',
    border: '1px solid var(--border-light)',
    backgroundColor: 'transparent',
    color: 'var(--text-secondary)'
  };

  switch(status) {
    case 'PENDING':
      style.borderColor = 'var(--warning)';
      style.color = 'var(--warning)';
      break;
    case 'CONFIRMED':
      style.borderColor = '#2979FF';
      style.color = '#2979FF';
      break;
    case 'IN_PROGRESS':
      style.backgroundColor = 'var(--accent)';
      style.borderColor = 'var(--accent)';
      style.color = '#FFFFFF';
      break;
    case 'COMPLETED':
      style.backgroundColor = 'var(--success)';
      style.borderColor = 'var(--success)';
      style.color = '#000000';
      break;
    case 'CANCELLED':
    default:
      style.backgroundColor = 'var(--bg-elevated)';
      style.borderColor = 'var(--border)';
      style.color = 'var(--text-muted)';
      break;
  }

  return <span style={style}>{status}</span>;
};

export default StatusBadge;
