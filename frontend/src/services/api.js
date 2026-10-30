const API_BASE = 'http://localhost:8080/api';

export async function getCategories() {
  const res = await fetch(`${API_BASE}/categories`);
  if (!res.ok) throw new Error('Failed to load categories');
  return res.json();
}

export async function getNews(categoryId = null) {
  const url = categoryId 
    ? `${API_BASE}/news?categoryId=${categoryId}` 
    : `${API_BASE}/news`;
  const res = await fetch(url);
  if (!res.ok) throw new Error('Failed to load news headlines');
  return res.json();
}

export async function getNewsDetail(id) {
  const res = await fetch(`${API_BASE}/news/${id}`);
  if (!res.ok) throw new Error('Failed to load article details');
  return res.json();
}

export async function postComment(newsId, commentText, token) {
  const headers = {
    'Content-Type': 'application/json'
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE}/news/${newsId}/comments`, {
    method: 'POST',
    headers,
    body: JSON.stringify({ commentText })
  });

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || 'Failed to submit comment');
  }
  return res.json();
}

export async function login(username, password) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || 'Invalid username or password');
  }
  return res.json();
}

export async function register(username, email, password) {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, email, password })
  });

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.error || errorData.message || 'Registration failed');
  }
  return res.json();
}
