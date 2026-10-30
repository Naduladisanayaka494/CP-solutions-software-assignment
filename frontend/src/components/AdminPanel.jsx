import React, { useState, useEffect } from 'react';
import { X, Plus, Edit2, Trash2, CheckCircle, XCircle, ShieldCheck, Tag, FileText } from 'lucide-react';
import { getCategories, createCategory, updateCategory, deleteCategory, getPendingArticles, approveArticle, rejectArticle } from '../services/api';

export default function AdminPanel({ isOpen, onClose, token, onCategoriesChanged, onArticleApproved }) {
  const [activeTab, setActiveTab] = useState('categories'); // 'categories' | 'pending'
  const [categories, setCategories] = useState([]);
  const [pendingArticles, setPendingArticles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Category Form State
  const [catName, setCatName] = useState('');
  const [catDesc, setCatDesc] = useState('');
  const [editingCatId, setEditingCatId] = useState(null);

  useEffect(() => {
    if (isOpen) {
      loadData();
    }
  }, [isOpen, activeTab]);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      if (activeTab === 'categories') {
        const cats = await getCategories();
        setCategories(cats);
      } else {
        const articles = await getPendingArticles(token);
        setPendingArticles(articles);
      }
    } catch (err) {
      setError(err.message || 'Failed to load admin data');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveCategory = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      if (editingCatId) {
        await updateCategory(editingCatId, catName, catDesc, token);
        setSuccess('Category updated successfully!');
      } else {
        await createCategory(catName, catDesc, token);
        setSuccess('Category created successfully!');
      }
      setCatName('');
      setCatDesc('');
      setEditingCatId(null);
      loadData();
      if (onCategoriesChanged) onCategoriesChanged();
    } catch (err) {
      setError(err.message || 'Failed to save category');
    }
  };

  const handleEditClick = (cat) => {
    setEditingCatId(cat.id);
    setCatName(cat.name);
    setCatDesc(cat.description || '');
    setError('');
    setSuccess('');
  };

  const handleCancelEdit = () => {
    setEditingCatId(null);
    setCatName('');
    setCatDesc('');
  };

  const handleDeleteCategory = async (id) => {
    if (!window.confirm('Are you sure you want to delete this category?')) return;
    setError('');
    setSuccess('');
    try {
      await deleteCategory(id, token);
      setSuccess('Category deleted successfully!');
      loadData();
      if (onCategoriesChanged) onCategoriesChanged();
    } catch (err) {
      setError(err.message || 'Failed to delete category');
    }
  };

  const handleApprove = async (id) => {
    setError('');
    setSuccess('');
    try {
      await approveArticle(id, token);
      setSuccess('Article approved and published to website!');
      setPendingArticles(pendingArticles.filter(a => a.id !== id));
      if (onArticleApproved) onArticleApproved();
    } catch (err) {
      setError(err.message || 'Failed to approve article');
    }
  };

  const handleReject = async (id) => {
    setError('');
    setSuccess('');
    try {
      await rejectArticle(id, token);
      setSuccess('Article rejected.');
      setPendingArticles(pendingArticles.filter(a => a.id !== id));
    } catch (err) {
      setError(err.message || 'Failed to reject article');
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" style={{ maxWidth: '780px' }} onClick={(e) => e.stopPropagation()}>
        <button
          onClick={onClose}
          style={{ position: 'absolute', top: '1.25rem', right: '1.25rem', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}
        >
          <X size={20} />
        </button>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '1rem' }}>
          <ShieldCheck size={26} color="var(--primary)" />
          <h2 style={{ fontSize: '1.4rem', fontWeight: 800 }}>Main Editor & Admin Control Panel</h2>
        </div>

        <div className="modal-tabs">
          <button
            className={`modal-tab ${activeTab === 'categories' ? 'active' : ''}`}
            onClick={() => setActiveTab('categories')}
          >
            <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.4rem' }}>
              <Tag size={16} />
              <span>Category Management</span>
            </span>
          </button>
          <button
            className={`modal-tab ${activeTab === 'pending' ? 'active' : ''}`}
            onClick={() => setActiveTab('pending')}
          >
            <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.4rem' }}>
              <FileText size={16} />
              <span>Pending Articles Queue</span>
            </span>
          </button>
        </div>

        {error && <div className="error-banner">{error}</div>}
        {success && <div style={{ background: '#ecfdf5', color: '#047857', padding: '0.6rem 0.8rem', borderRadius: '0.4rem', fontSize: '0.85rem', marginBottom: '1rem' }}>{success}</div>}

        {/* 1. Category Management Tab */}
        {activeTab === 'categories' && (
          <div>
            <form onSubmit={handleSaveCategory} style={{ background: '#f8fafc', padding: '1rem', borderRadius: '0.5rem', marginBottom: '1.5rem', border: '1px solid var(--border)' }}>
              <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem' }}>
                {editingCatId ? 'Edit Category' : 'Create New Category'}
              </h3>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr auto', gap: '0.75rem', alignItems: 'end' }}>
                <div>
                  <label className="form-label">Category Name</label>
                  <input
                    type="text"
                    className="form-input"
                    value={catName}
                    onChange={(e) => setCatName(e.target.value)}
                    placeholder="e.g. Science"
                    required
                  />
                </div>
                <div>
                  <label className="form-label">Description</label>
                  <input
                    type="text"
                    className="form-input"
                    value={catDesc}
                    onChange={(e) => setCatDesc(e.target.value)}
                    placeholder="Short description..."
                  />
                </div>
                <div style={{ display: 'flex', gap: '0.4rem' }}>
                  <button type="submit" className="btn btn-primary" style={{ padding: '0.65rem 1rem' }}>
                    <Plus size={15} />
                    <span>{editingCatId ? 'Save' : 'Add'}</span>
                  </button>
                  {editingCatId && (
                    <button type="button" className="btn btn-outline" onClick={handleCancelEdit} style={{ padding: '0.65rem 0.85rem' }}>
                      Cancel
                    </button>
                  )}
                </div>
              </div>
            </form>

            <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem' }}>Existing Categories</h3>
            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Loading categories...</p>
            ) : (
              <div style={{ maxHeight: '240px', overflowY: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.9rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid var(--border)', textAlign: 'left' }}>
                      <th style={{ padding: '0.5rem' }}>ID</th>
                      <th style={{ padding: '0.5rem' }}>Name</th>
                      <th style={{ padding: '0.5rem' }}>Description</th>
                      <th style={{ padding: '0.5rem', textAlign: 'right' }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {categories.map((c) => (
                      <tr key={c.id} style={{ borderBottom: '1px solid var(--border)' }}>
                        <td style={{ padding: '0.5rem' }}>{c.id}</td>
                        <td style={{ padding: '0.5rem', fontWeight: 600 }}>{c.name}</td>
                        <td style={{ padding: '0.5rem', color: 'var(--text-secondary)' }}>{c.description || '-'}</td>
                        <td style={{ padding: '0.5rem', textAlign: 'right' }}>
                          <button
                            className="btn btn-ghost"
                            onClick={() => handleEditClick(c)}
                            title="Edit"
                            style={{ padding: '0.3rem' }}
                          >
                            <Edit2 size={15} />
                          </button>
                          <button
                            className="btn btn-ghost"
                            onClick={() => handleDeleteCategory(c.id)}
                            title="Delete"
                            style={{ padding: '0.3rem', color: 'var(--accent)' }}
                          >
                            <Trash2 size={15} />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* 2. Pending Articles Queue Tab */}
        {activeTab === 'pending' && (
          <div>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem' }}>
              Articles Awaiting Editor Approval
            </h3>
            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Checking pending queue...</p>
            ) : pendingArticles.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '2.5rem 0', color: 'var(--text-muted)' }}>
                No articles currently pending review. All submissions are up to date!
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxHeight: '350px', overflowY: 'auto' }}>
                {pendingArticles.map((article) => (
                  <div key={article.id} style={{ background: '#ffffff', border: '1px solid var(--border)', borderRadius: '0.5rem', padding: '1.25rem' }}>
                    <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '1rem', marginBottom: '0.5rem' }}>
                      <div>
                        <div className="badge-group" style={{ marginBottom: '0.4rem' }}>
                          {article.categories && article.categories.map((cat) => (
                            <span key={cat.id} className="badge badge-default">{cat.name}</span>
                          ))}
                        </div>
                        <h4 style={{ fontSize: '1.1rem', fontWeight: 700 }}>{article.title}</h4>
                      </div>

                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button
                          className="btn btn-primary"
                          onClick={() => handleApprove(article.id)}
                          style={{ background: '#059669', padding: '0.45rem 0.85rem' }}
                        >
                          <CheckCircle size={15} />
                          <span>Approve & Publish</span>
                        </button>
                        <button
                          className="btn btn-outline"
                          onClick={() => handleReject(article.id)}
                          style={{ color: '#dc2626', borderColor: '#fca5a5', padding: '0.45rem 0.85rem' }}
                        >
                          <XCircle size={15} />
                          <span>Reject</span>
                        </button>
                      </div>
                    </div>

                    <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
                      {article.summary}
                    </p>

                    <div style={{ fontSize: '0.775rem', color: 'var(--text-muted)' }}>
                      Submitted by: <strong>{article.author}</strong>
                    </div>
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
