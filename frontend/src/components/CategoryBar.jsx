import React from 'react';

export default function CategoryBar({ categories, selectedCategoryId, onSelectCategory }) {
  return (
    <div className="category-nav-wrapper">
      <div className="container">
        <nav className="category-nav">
          <button
            className={`category-pill ${selectedCategoryId === null ? 'active' : ''}`}
            onClick={() => onSelectCategory(null)}
          >
            All News
          </button>
          {categories.map((cat) => (
            <button
              key={cat.id}
              className={`category-pill ${selectedCategoryId === cat.id ? 'active' : ''}`}
              onClick={() => onSelectCategory(cat.id)}
            >
              {cat.name}
            </button>
          ))}
        </nav>
      </div>
    </div>
  );
}
