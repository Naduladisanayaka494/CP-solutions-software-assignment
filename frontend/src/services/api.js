const API_BASE = 'http://localhost:8080/api';

function authHeaders(token) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

// ---------------- Categories API ----------------

export async function getCategories() {
  const res = await fetch(`${API_BASE}/categories`);
  if (!res.ok) throw new Error('Failed to load categories');
  return res.json();
}

export async function createCategory(name, description, token) {
  const res = await fetch(`${API_BASE}/categories`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({ name, description })
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.error || errorData.message || 'Failed to create category');
  }
  return res.json();
}

export async function updateCategory(id, name, description, token) {
  const res = await fetch(`${API_BASE}/categories/${id}`, {
    method: 'PUT',
    headers: authHeaders(token),
    body: JSON.stringify({ name, description })
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.error || errorData.message || 'Failed to update category');
  }
  return res.json();
}

export async function deleteCategory(id, token) {
  const res = await fetch(`${API_BASE}/categories/${id}`, {
    method: 'DELETE',
    headers: authHeaders(token)
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.error || errorData.message || 'Failed to delete category');
  }
  return res.json();
}

// ---------------- News API ----------------

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

export async function submitArticle(articleData, token) {
  const res = await fetch(`${API_BASE}/news/submit`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify(articleData)
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || errorData.error || 'Failed to submit article');
  }
  return res.json();
}

export async function getMySubmissions(token) {
  const res = await fetch(`${API_BASE}/news/my-submissions`, {
    headers: authHeaders(token)
  });
  if (!res.ok) throw new Error('Failed to load your submissions');
  return res.json();
}

// ---------------- Admin Editorial API ----------------

export async function getPendingArticles(token) {
  const res = await fetch(`${API_BASE}/admin/news/pending`, {
    headers: authHeaders(token)
  });
  if (!res.ok) throw new Error('Failed to load pending articles');
  return res.json();
}

export async function approveArticle(id, token) {
  const res = await fetch(`${API_BASE}/admin/news/${id}/approve`, {
    method: 'PUT',
    headers: authHeaders(token)
  });
  if (!res.ok) throw new Error('Failed to approve article');
  return res.json();
}

export async function rejectArticle(id, token) {
  const res = await fetch(`${API_BASE}/admin/news/${id}/reject`, {
    method: 'PUT',
    headers: authHeaders(token)
  });
  if (!res.ok) throw new Error('Failed to reject article');
  return res.json();
}

// ---------------- Comments API ----------------

export async function postComment(newsId, commentText, token) {
  const res = await fetch(`${API_BASE}/news/${newsId}/comments`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({ commentText })
  });

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || 'Failed to submit comment');
  }
  return res.json();
}

export async function editComment(commentId, commentText, token) {
  const res = await fetch(`${API_BASE}/comments/${commentId}`, {
    method: 'PUT',
    headers: authHeaders(token),
    body: JSON.stringify({ commentText })
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.error || errorData.message || 'Failed to update comment');
  }
  return res.json();
}

export async function deleteComment(commentId, token) {
  const res = await fetch(`${API_BASE}/comments/${commentId}`, {
    method: 'DELETE',
    headers: authHeaders(token)
  });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.error || errorData.message || 'Failed to delete comment');
  }
  return res.json();
}

// ---------------- Auth API ----------------

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
