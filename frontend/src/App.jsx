import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import CategoryBar from './components/CategoryBar';
import NewsCard from './components/NewsCard';
import NewsDetail from './components/NewsDetail';
import AuthModal from './components/AuthModal';
import { getCategories, getNews, getNewsDetail } from './services/api';

export default function App() {
  const [categories, setCategories] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(null);
  const [newsList, setNewsList] = useState([]);
  const [selectedArticleId, setSelectedArticleId] = useState(null);
  const [articleDetail, setArticleDetail] = useState(null);
  const [loading, setLoading] = useState(false);
  const [articleLoading, setArticleLoading] = useState(false);
  const [error, setError] = useState('');

  // Reader Auth State
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('news_reader_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [authModalOpen, setAuthModalOpen] = useState(false);

  // Load Categories on mount
  useEffect(() => {
    async function loadInitialData() {
      try {
        const catData = await getCategories();
        setCategories(catData);
      } catch (err) {
        console.error('Failed to load categories', err);
        setError('Cannot connect to backend server. Make sure Spring Boot is running on port 8080.');
      }
    }
    loadInitialData();
  }, []);

  // Load News whenever selected category changes
  useEffect(() => {
    async function fetchHeadlines() {
      setLoading(true);
      setError('');
      try {
        const data = await getNews(selectedCategoryId);
        setNewsList(data);
      } catch (err) {
        console.error('Failed to load news headlines', err);
        setError('Failed to fetch news headlines.');
      } finally {
        setLoading(false);
      }
    }
    fetchHeadlines();
  }, [selectedCategoryId]);

  // Load article detail when selectedArticleId changes
  useEffect(() => {
    if (!selectedArticleId) {
      setArticleDetail(null);
      return;
    }

    async function fetchDetail() {
      setArticleLoading(true);
      try {
        const data = await getNewsDetail(selectedArticleId);
        setArticleDetail(data);
      } catch (err) {
        console.error('Failed to load article detail', err);
      } finally {
        setArticleLoading(false);
      }
    }
    fetchDetail();
  }, [selectedArticleId]);

  const handleAuthSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem('news_reader_user', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('news_reader_user');
  };

  const handleSelectCategory = (catId) => {
    setSelectedCategoryId(catId);
    setSelectedArticleId(null); // Return to headlines list when switching category
  };

  const handleCommentAdded = (newComment) => {
    if (articleDetail) {
      setArticleDetail({
        ...articleDetail,
        comments: [newComment, ...(articleDetail.comments || [])]
      });
    }
  };

  const activeCategoryName = selectedCategoryId === null 
    ? 'All News' 
    : categories.find(c => c.id === selectedCategoryId)?.name || 'News';

  return (
    <div>
      <Navbar
        user={user}
        onOpenAuth={() => setAuthModalOpen(true)}
        onLogout={handleLogout}
        onHomeClick={() => { setSelectedArticleId(null); setSelectedCategoryId(null); }}
      />

      <CategoryBar
        categories={categories}
        selectedCategoryId={selectedCategoryId}
        onSelectCategory={handleSelectCategory}
      />

      <main className="container">
        {error && (
          <div className="error-banner" style={{ marginTop: '1.5rem' }}>
            {error}
          </div>
        )}

        {/* If viewing a single news article */}
        {selectedArticleId ? (
          articleLoading ? (
            <div style={{ textAlign: 'center', padding: '4rem 0', color: 'var(--text-muted)' }}>
              Loading complete story...
            </div>
          ) : articleDetail ? (
            <NewsDetail
              article={articleDetail}
              onBack={() => setSelectedArticleId(null)}
              user={user}
              onOpenAuth={() => setAuthModalOpen(true)}
              onCommentAdded={handleCommentAdded}
            />
          ) : (
            <div style={{ textAlign: 'center', padding: '4rem 0' }}>
              Article not found.
            </div>
          )
        ) : (
          /* Headlines Listing */
          <>
            <div className="section-header">
              <h1 className="section-title">{activeCategoryName} Headlines</h1>
              <span className="news-count">
                {newsList.length} {newsList.length === 1 ? 'story' : 'stories'}
              </span>
            </div>

            {loading ? (
              <div style={{ textAlign: 'center', padding: '4rem 0', color: 'var(--text-muted)' }}>
                Loading headlines...
              </div>
            ) : newsList.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '4rem 0', color: 'var(--text-muted)' }}>
                No news items found in this category.
              </div>
            ) : (
              <div className="news-grid">
                {newsList.map((item) => (
                  <NewsCard
                    key={item.id}
                    news={item}
                    onClick={(id) => setSelectedArticleId(id)}
                  />
                ))}
              </div>
            )}
          </>
        )}
      </main>

      <AuthModal
        isOpen={authModalOpen}
        onClose={() => setAuthModalOpen(false)}
        onAuthSuccess={handleAuthSuccess}
      />
    </div>
  );
}
