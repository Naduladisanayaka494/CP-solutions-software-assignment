import React from 'react';
import { Newspaper, User, LogIn, LogOut } from 'lucide-react';

export default function Navbar({ user, onOpenAuth, onLogout, onHomeClick }) {
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
          {user ? (
            <>
              <div className="user-badge">
                <User size={15} />
                <span>{user.username}</span>
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
