import React from 'react';
import { Newspaper, User, LogIn, LogOut, ShieldCheck, PenTool } from 'lucide-react';

export default function Navbar({ user, onOpenAuth, onLogout, onHomeClick, onOpenAdmin, onOpenWriter }) {
  const isAdmin = user && user.role === 'ROLE_ADMIN';
  const isWriter = user && (user.role === 'ROLE_WRITER' || user.role === 'ROLE_ADMIN');

  return (
    <header className="header">
      <div className="container header-inner">
        <div className="logo" onClick={onHomeClick}>
          <div className="logo-icon">
            <Newspaper size={24} />
          </div>
          <div className="logo-text">
            Global<span>News</span>
          </div>
        </div>

        <div className="auth-bar">
          {isAdmin && (
            <button className="btn btn-outline" onClick={onOpenAdmin} style={{ borderColor: 'var(--primary-light)', color: 'var(--primary)' }}>
              <ShieldCheck size={16} />
              <span>Editor Panel</span>
            </button>
          )}

          {isWriter && (
            <button className="btn btn-primary" onClick={onOpenWriter} style={{ background: '#059669' }}>
              <PenTool size={15} />
              <span>Write Article</span>
            </button>
          )}

          {user ? (
            <>
              <div className="user-badge">
                <User size={15} />
                <span>{user.username}</span>
                <span style={{ fontSize: '0.7rem', textTransform: 'uppercase', background: 'white', padding: '0.1rem 0.4rem', borderRadius: '0.2rem', marginLeft: '0.2rem', border: '1px solid var(--border)' }}>
                  {user.role.replace('ROLE_', '')}
                </span>
              </div>
              <button className="btn btn-outline" onClick={onLogout} title="Log out">
                <LogOut size={16} />
                <span>Logout</span>
              </button>
            </>
          ) : (
            <button className="btn btn-primary" onClick={onOpenAuth}>
              <LogIn size={16} />
              <span>Reader Sign In</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
}
