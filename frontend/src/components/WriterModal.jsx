import React, { useState, useEffect } from 'react';
import { X, PenTool, Send, Clock, CheckCircle2, AlertCircle } from 'lucide-react';
import { submitArticle, getMySubmissions } from '../services/api';

export default function WriterModal({ isOpen, onClose, token, categories, onArticleSubmitted }) {
  const [activeTab, setActiveTab] = useState('write'); // 'write' | 'history'
  const [title, setTitle] = useState('');
  const [summary, setSummary] = useState('');
  const [content, setContent] = useState('');
  const [selectedCategoryIds, setSelectedCategoryIds] = useState([]);
  const [mySubmissions, setMySubmissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (isOpen && activeTab === 'history') {
      loadHistory();
    }
  }, [isOpen, activeTab]);

  const loadHistory = async () => {
    setLoading(true);
    try {
      const data = await getMySubmissions(token);
      setMySubmissions(data);
    } catch (err) {
      setError(err.message || 'Failed to load your submissions');
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryToggle = (id) => {
    if (selectedCategoryIds.includes(id)) {
      setSelectedCategoryIds(selectedCategoryIds.filter(cId => cId !== id));
    } else {
      setSelectedCategoryIds([...selectedCategoryIds, id]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (selectedCategoryIds.length === 0) {
      setError('Please select at least one category for your article.');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await submitArticle({
        title,
        summary,
        content,
        categoryIds: selectedCategoryIds
      }, token);

      setSuccess('Article submitted to the Main Editor for review! You will see it in "My Submissions".');
      setTitle('');
      setSummary('');
      setContent('');
      setSelectedCategoryIds([]);
      if (onArticleSubmitted) onArticleSubmitted();
    } catch (err) {
      setError(err.message || 'Failed to submit article');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" style={{ maxWidth: '650px' }} onClick={(e) => e.stopPropagation()}>
        <button
          onClick={onClose}
          style={{ position: 'absolute', top: '1.25rem', right: '1.25rem', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}
        >
          <X size={20} />
        </button>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '1rem' }}>
          <PenTool size={24} color="var(--primary)" />
          <h2 style={{ fontSize: '1.35rem', fontWeight: 800 }}>Writer Editorial Portal</h2>
        </div>

        <div className="modal-tabs">
          <button
            className={`modal-tab ${activeTab === 'write' ? 'active' : ''}`}
            onClick={() => { setActiveTab('write'); setError(''); setSuccess(''); }}
          >
            Submit New Article
          </button>
          <button
            className={`modal-tab ${activeTab === 'history' ? 'active' : ''}`}
            onClick={() => { setActiveTab('history'); setError(''); setSuccess(''); }}
          >
            My Submissions ({mySubmissions.length})
          </button>
        </div>

        {error && <div className="error-banner">{error}</div>}
        {success && <div style={{ background: '#ecfdf5', color: '#047857', padding: '0.6rem 0.8rem', borderRadius: '0.4rem', fontSize: '0.85rem', marginBottom: '1rem' }}>{success}</div>}

        {activeTab === 'write' ? (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Headline / Title *</label>
              <input
                type="text"
                className="form-input"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g. Breakthrough Discovery in Renewable Energy"
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Summary / Teaser</label>
              <input
                type="text"
                className="form-input"
                value={summary}
                onChange={(e) => setSummary(e.target.value)}
                placeholder="Brief one-sentence overview..."
              />
            </div>

            <div className="form-group">
              <label className="form-label">Select Related Categories (Can pick multiple) *</label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginTop: '0.35rem' }}>
                {categories.map((cat) => {
                  const isChecked = selectedCategoryIds.includes(cat.id);
                  return (
                    <button
                      key={cat.id}
                      type="button"
                      onClick={() => handleCategoryToggle(cat.id)}
                      className={`category-pill ${isChecked ? 'active' : ''}`}
                      style={{ fontSize: '0.8rem', padding: '0.35rem 0.75rem' }}
                    >
                      {cat.name} {isChecked ? '✓' : '+'}
                    </button>
                  );
                })}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Complete Story / Content *</label>
              <textarea
                className="comment-textarea"
                style={{ minHeight: '140px' }}
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="Write the complete news article here..."
                required
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              style={{ width: '100%', justifyContent: 'center', padding: '0.75rem' }}
            >
              <Send size={16} />
              <span>{loading ? 'Submitting...' : 'Submit to Main Editor for Review'}</span>
            </button>
          </form>
        ) : (
          <div style={{ maxHeight: '350px', overflowY: 'auto' }}>
            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Loading your articles...</p>
            ) : mySubmissions.length === 0 ? (
              <p style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-muted)' }}>
                You have not submitted any articles yet.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
                {mySubmissions.map((item) => (
                  <div key={item.id} style={{ border: '1px solid var(--border)', borderRadius: '0.5rem', padding: '1rem', background: '#ffffff' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.4rem' }}>
                      <h4 style={{ fontWeight: 700, fontSize: '1rem' }}>{item.title}</h4>
                      {item.status === 'PUBLISHED' && (
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem', background: '#ecfdf5', color: '#047857', padding: '0.2rem 0.5rem', borderRadius: '0.3rem', fontSize: '0.75rem', fontWeight: 700 }}>
                          <CheckCircle2 size={13} /> PUBLISHED
                        </span>
                      )}
                      {item.status === 'PENDING_APPROVAL' && (
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem', background: '#fffbeb', color: '#b45309', padding: '0.2rem 0.5rem', borderRadius: '0.3rem', fontSize: '0.75rem', fontWeight: 700 }}>
                          <Clock size={13} /> PENDING APPROVAL
                        </span>
                      )}
                      {item.status === 'REJECTED' && (
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem', background: '#fef2f2', color: '#b91c1c', padding: '0.2rem 0.5rem', borderRadius: '0.3rem', fontSize: '0.75rem', fontWeight: 700 }}>
                          <AlertCircle size={13} /> REJECTED
                        </span>
                      )}
                    </div>
                    <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{item.summary}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
