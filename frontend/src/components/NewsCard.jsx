import React from 'react';
import { Clock, User, ChevronRight } from 'lucide-react';

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
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

export default function NewsCard({ news, onClick }) {
  return (
    <article className="news-card" onClick={() => onClick(news.id)}>
      <div>
        <div className="badge-group">
          {news.categories && news.categories.map((cat) => (
            <span key={cat.id} className={getBadgeClass(cat.name)}>
              {cat.name}
            </span>
          ))}
        </div>
        <h2 className="news-card-title">{news.title}</h2>
        <p className="news-card-summary">{news.summary}</p>
      </div>

      <div className="news-card-footer">
        <span className="meta-author">{news.author || 'Staff Reporter'}</span>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          <Clock size={14} />
          <span>{formatDate(news.publishedAt)}</span>
        </div>
      </div>
    </article>
  );
}
