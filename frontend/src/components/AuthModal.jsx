import React, { useState } from 'react';
import { X, LogIn, UserPlus } from 'lucide-react';
import { login, register } from '../services/api';

export default function AuthModal({ isOpen, onClose, onAuthSuccess }) {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let data;
      if (isLogin) {
        data = await login(username, password);
      } else {
        data = await register(username, email, password);
      }
      onAuthSuccess(data);
      onClose();
    } catch (err) {
      setError(err.message || 'Authentication error');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickLogin = (u, p) => {
    setUsername(u);
    setPassword(p);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button
          onClick={onClose}
          style={{ position: 'absolute', top: '1.25rem', right: '1.25rem', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}
        >
          <X size={20} />
        </button>

        <div className="modal-tabs">
          <button
            className={`modal-tab ${isLogin ? 'active' : ''}`}
            onClick={() => { setIsLogin(true); setError(''); }}
          >
            Sign In
          </button>
          <button
            className={`modal-tab ${!isLogin ? 'active' : ''}`}
            onClick={() => { setIsLogin(false); setError(''); }}
          >
            Create Reader Account
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}

          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="e.g. admin_editor or reader_kamal"
              required
            />
          </div>

          {!isLogin && (
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input
                type="email"
                className="form-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="kamal@example.com"
                required
              />
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <div style={{ marginTop: '1.5rem' }}>
            <button
              type="submit"
              className="btn btn-primary"
              style={{ width: '100%', justifyContent: 'center', padding: '0.75rem' }}
              disabled={loading}
            >
              {isLogin ? <LogIn size={16} /> : <UserPlus size={16} />}
              <span>{loading ? 'Please wait...' : (isLogin ? 'Sign In' : 'Create Account')}</span>
            </button>
          </div>

          {isLogin && (
            <div style={{ marginTop: '1.25rem', fontSize: '0.8rem', color: 'var(--text-secondary)', background: '#f8fafc', padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid var(--border)' }}>
              <div style={{ fontWeight: 700, marginBottom: '0.35rem' }}>Quick Demo Accounts:</div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                <a href="#admin" onClick={(e) => { e.preventDefault(); handleQuickLogin('admin_editor', 'admin123'); }} style={{ color: 'var(--primary)', textDecoration: 'none' }}>
                  🔑 <strong>Main Editor</strong>: admin_editor / admin123
                </a>
                <a href="#writer" onClick={(e) => { e.preventDefault(); handleQuickLogin('writer_sahan', 'writer123'); }} style={{ color: 'var(--primary)', textDecoration: 'none' }}>
                  ✍️ <strong>Writer</strong>: writer_sahan / writer123
                </a>
                <a href="#reader" onClick={(e) => { e.preventDefault(); handleQuickLogin('reader_kamal', 'password123'); }} style={{ color: 'var(--primary)', textDecoration: 'none' }}>
                  👤 <strong>Reader</strong>: reader_kamal / password123
                </a>
              </div>
            </div>
          )}
        </form>
      </div>
    </div>
  );
}
