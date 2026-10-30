import React, { useState } from 'react';
import { ArrowLeft, Clock, MessageSquare, Send, User, Lock } from 'lucide-react';
import { postComment } from '../services/api';

function getBadgeClass(name) {
  const lower = (name || '').toLowerCase();
  if (lower.includes('sport')) return 'badge badge-sports';
  if (lower.includes('entertain')) return 'badge badge-entertainment';
  if (lower.includes('tech')) return 'badge badge-technology';
  return 'badge badge-default';
}

function formatDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

export default function NewsDetail({ article, onBack, user, onOpenAuth, onCommentAdded }) {
  const [commentText, setCommentText] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmitComment = async (e) => {
    e.preventDefault();
    if (!commentText.trim()) return;

    if (!user) {
      onOpenAuth();
      return;
    }

    setSubmitting(true);
    setError('');
    try {
      const newComment = await postComment(article.id, commentText, user.token);
      setCommentText('');
      if (onCommentAdded) {
        onCommentAdded(newComment);
      }
    } catch (err) {
      setError(err.message || 'Failed to submit comment');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container">
      <button className="back-btn" onClick={onBack}>
        <ArrowLeft size={16} />
        <span>Back to all headlines</span>
      </button>

      <article className="article-container">
        <header className="article-header">
          <div className="badge-group">
            {article.categories && article.categories.map((cat) => (
              <span key={cat.id} className={getBadgeClass(cat.name)}>
                {cat.name}
              </span>
            ))}
          </div>

          <h1 className="article-title">{article.title}</h1>

          <div className="article-meta">
            <span style={{ fontWeight: 600 }}>By {article.author || 'Staff Reporter'}</span>
            <span>•</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <Clock size={15} />
              <span>{formatDate(article.publishedAt)}</span>
            </div>
          </div>
        </header>

        <div className="article-body">
          {article.content.split('\n\n').map((paragraph, idx) => (
            <p key={idx} style={{ marginBottom: '1.25rem' }}>
              {paragraph}
            </p>
          ))}
        </div>

        {/* Reader Comments Section */}
        <section className="comments-section">
          <h2 className="comments-title">
            <MessageSquare size={20} />
            <span>Reader Discussion ({article.comments ? article.comments.length : 0})</span>
          </h2>

          {/* Comment Form */}
          <form className="comment-form" onSubmit={handleSubmitComment}>
            {error && <div className="error-banner">{error}</div>}

            <textarea
              className="comment-textarea"
              placeholder={user ? "Join the conversation and share your thoughts..." : "Please sign in to leave a comment..."}
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              disabled={submitting}
              required
            />

            <div className="comment-form-footer">
              {user ? (
                <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                  Commenting as <strong>{user.username}</strong>
                </span>
              ) : (
                <button
                  type="button"
                  className="btn btn-ghost"
                  onClick={onOpenAuth}
                  style={{ fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}
                >
                  <Lock size={14} />
                  <span>Sign in required to post</span>
                </button>
              )}

              <button
                type="submit"
                className="btn btn-primary"
                disabled={submitting || !commentText.trim()}
              >
                <Send size={15} />
                <span>{submitting ? 'Posting...' : 'Post Comment'}</span>
              </button>
            </div>
          </form>

          {/* Comments List */}
          <div className="comment-list">
            {(!article.comments || article.comments.length === 0) ? (
              <p style={{ color: 'var(--text-muted)', fontStyle: 'italic', padding: '1rem 0' }}>
                No comments yet. Be the first reader to share your perspective!
              </p>
            ) : (
              article.comments.map((comment) => (
                <div key={comment.id} className="comment-card">
                  <div className="comment-author-bar">
                    <div className="avatar-circle">
                      {(comment.authorName || 'R')[0].toUpperCase()}
                    </div>
                    <div>
                      <div className="comment-author-name">{comment.authorName}</div>
                      <div className="comment-time">{formatDate(comment.createdAt)}</div>
                    </div>
                  </div>
                  <p className="comment-text">{comment.commentText}</p>
                </div>
              ))
            )}
          </div>
        </section>
      </article>
    </div>
  );
}
