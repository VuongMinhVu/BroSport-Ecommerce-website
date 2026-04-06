document.addEventListener('DOMContentLoaded', () => {
  // Utility: Debounce
  function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  }

  // Utility: Show Success Toast
  function showSuccessToast() {
    const toast = document.getElementById('toast-success');
    if (!toast) return;
    toast.classList.remove('hidden');
    setTimeout(() => toast.classList.remove('opacity-0'), 10);
    setTimeout(() => {
      toast.classList.add('opacity-0');
      setTimeout(() => toast.classList.add('hidden'), 300);
    }, 2000);
  }

  // --- State & URL Manager ---
  class StateManager {
    constructor() {
      this.filters = {
        keyword: '',
        categories: [],
        brands: [],
        gender: [],
        sports: [],
        minPrice: null,
        maxPrice: null,
        page: 0,
        size: 10
      };
      this.initFromUrl();
      this.syncUI();
    }

    initFromUrl() {
      const params = new URLSearchParams(window.location.search);
      if (params.has('keyword')) this.filters.keyword = params.get('keyword');
      if (params.has('categories')) this.filters.categories = params.get('categories').split(',').filter(Boolean);
      if (params.has('brands')) this.filters.brands = params.get('brands').split(',').filter(Boolean);
      if (params.has('gender')) this.filters.gender = params.get('gender').split(',').filter(Boolean);
      if (params.has('sports')) this.filters.sports = params.get('sports').split(',').filter(Boolean);
      if (params.has('minPrice')) this.filters.minPrice = params.get('minPrice');
      if (params.has('maxPrice')) this.filters.maxPrice = params.get('maxPrice');
      if (params.has('page')) this.filters.page = parseInt(params.get('page')) || 0;
    }

    updateUrl() {
      const params = new URLSearchParams();
      if (this.filters.keyword) params.set('keyword', this.filters.keyword);
      if (this.filters.categories && this.filters.categories.length > 0) params.set('categories', this.filters.categories.join(','));
      if (this.filters.brands && this.filters.brands.length > 0) params.set('brands', this.filters.brands.join(','));
      if (this.filters.gender && this.filters.gender.length > 0) params.set('gender', this.filters.gender.join(','));
      if (this.filters.sports && this.filters.sports.length > 0) params.set('sports', this.filters.sports.join(','));
      if (this.filters.minPrice) params.set('minPrice', this.filters.minPrice);
      if (this.filters.maxPrice && this.filters.maxPrice < 5000000) params.set('maxPrice', this.filters.maxPrice);
      if (this.filters.page > 0) params.set('page', this.filters.page);

      const path = window.location.pathname;
      const newUrl = `${path}${params.toString() ? '?' + params.toString() : ''}`;
      window.history.pushState(this.filters, '', newUrl);
    }

    // update from UI elements
    syncUI() {
      if (this.filters.keyword) {
        const input = document.getElementById('searchInput');
        if (input) input.value = this.filters.keyword;
      }
      if (this.filters.maxPrice) {
        const range = document.getElementById('priceRange');
        if (range) range.value = this.filters.maxPrice;
      }
      if (this.filters.categories) {
        document.querySelectorAll('.category-filter').forEach(cb => {
          cb.checked = this.filters.categories.includes(cb.value);
        });
      }
      if (this.filters.brands) {
        document.querySelectorAll('.brand-filter').forEach(cb => {
          cb.checked = this.filters.brands.includes(cb.value);
        });
      }

      if (this.filters.gender) {
        document.querySelectorAll('.gender-filter').forEach(cb => {
          cb.checked = this.filters.gender.includes(cb.value);
        });
      }

      if (this.filters.sports) {
        document.querySelectorAll('.sports-filter').forEach(cb => {
          cb.checked = this.filters.sports.includes(cb.value);
        });
      }
    }
  }

  // --- API Service ---
  class ProductService {
    constructor() {
      this.abortController = null;
    }

    async fetchProducts(filters) {
      if (this.abortController) {
        this.abortController.abort();
      }
      this.abortController = new AbortController();

      try {
        const params = new URLSearchParams();
        if (filters.keyword) params.append('keyword', filters.keyword);
        if (filters.categories && filters.categories.length > 0) params.append('categories', filters.categories.join(','));
        if (filters.brands && filters.brands.length > 0) params.append('brands', filters.brands.join(','));
        if (filters.gender && filters.gender.length > 0) params.append('gender', filters.gender.join(','));
        if (filters.sports && filters.sports.length > 0) params.append('sports', filters.sports.join(','));
        if (filters.maxPrice) {
          params.append('minPrice', filters.minPrice || 0);
          params.append('maxPrice', filters.maxPrice);
        } else if (filters.minPrice) {
          params.append('minPrice', filters.minPrice);
        }
        params.append('page', filters.page);
        params.append('size', filters.size);

        const response = await fetch(`/api/v1/search/products?${params.toString()}`, {
          signal: this.abortController.signal,
          headers: {
            'Accept': 'application/json'
          }
        });

        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
      } catch (error) {
        if (error.name === 'AbortError') {
          console.log('Fetch aborted');
          return null; // Ignore aborted requests
        }
        throw error;
      }
    }
  }

  // --- UI Component ---
  class ProductUI {
    constructor() {
      this.container = document.getElementById('product-container');
      this.paginationContainer = document.getElementById('pagination-container');
    }

    get isListView() {
      return document.getElementById('btn-list-view')?.classList.contains('text-primary');
    }

    renderLoading() {
      const isList = this.isListView;
      const skeletons = Array(6)
        .fill("")
        .map(
          () => `
            <div class="product-card animate-pulse relative bg-surface rounded-xl overflow-hidden border border-white/5 flex flex-col ${isList ? "md:flex-row md:items-center" : ""}">
              <div class="img-wrapper relative overflow-hidden shrink-0 w-full ${isList ? "md:w-64 md:h-64 border-b md:border-b-0 md:border-r border-white/5" : "aspect-square bg-slate-800"}"></div>
              <div class="p-4 flex-1 flex flex-col justify-center space-y-3 ${isList ? "md:p-6" : ""}">
             <div class="h-6 bg-slate-700 rounded w-3/4"></div>
             <div class="h-4 bg-slate-700 rounded w-1/2"></div>
             <div class="h-8 bg-slate-700 rounded w-1/4 mt-auto"></div>
          </div>
        </div>
      `).join('');
      // Use grid layout depending on list view state
      this.container.className = isList ? 'grid grid-cols-1 gap-6' : 'grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6';
      this.container.innerHTML = skeletons;
    }

    renderError(error) {
      this.container.className = 'grid grid-cols-1';
      this.container.innerHTML = `
          <div class="col-span-full text-center py-12">
            <span class="material-symbols-outlined text-4xl text-red-500 mb-4">error</span>
            <p class="text-slate-400">Failed to load products. Please try again.</p>
          </div>
       `;
    }

    renderEmpty() {
      this.container.className = 'grid grid-cols-1';
      this.container.innerHTML = `
          <div class="col-span-full text-center py-12">
            <span class="material-symbols-outlined text-4xl text-slate-500 mb-4">inventory_2</span>
            <p class="text-slate-400">No products found matching your criteria.</p>
            <button id="clearFiltersBtn" class="mt-4 text-primary hover:underline">Clear all filters</button>
          </div>
       `;
    }
    renderProducts(products) {
      if (!products || products.length === 0) {
        this.renderEmpty();
        return;
      }

      const isList = this.isListView;
      this.container.className = isList
        ? "grid grid-cols-1 gap-6"
        : "grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6";

      this.container.innerHTML = products
        .map((product) => {
          let imageUrl = `https://via.placeholder.com/400x500/2a2118/ffffff?text=${encodeURIComponent(product.name)}`;
          if (product.thumbnail) {
            imageUrl = product.thumbnail;
          } else if (product.productImages && product.productImages.length > 0) {
            imageUrl = product.productImages[0].imageUrl;
          }
          const brand = product.brandName || product.brand || 'Brand';

          return `
          <div class="product-card group relative bg-surface rounded-xl overflow-hidden border border-white/5 hover:border-primary/50 transition-all duration-300 flex flex-col ${isList ? "md:flex-row md:items-center" : ""}">

            <a href="/product/${product.slug}" class="absolute inset-0 z-10 cursor-pointer block"></a>

            <div class="img-wrapper ${isList ? "relative overflow-hidden shrink-0 w-full md:w-64 md:h-64 aspect-square md:aspect-auto border-b md:border-b-0 md:border-r border-white/5" : "aspect-square"} bg-gradient-to-br from-slate-800 to-slate-900 relative overflow-hidden shrink-0 w-full">
              <img src="${imageUrl}" alt="${product.name}" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />

              <div class="quick-view absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none">
                <span class="bg-primary text-white px-4 py-2 rounded-lg font-bold hover:bg-primary/90 transition-colors shadow-lg shadow-primary/30 pointer-events-none">
                  Quick View
                </span>
              </div>
            </div>

            <div class="details-wrapper p-4 ${isList ? "md:p-6" : ""} flex-1 flex flex-col justify-center">
              <div class="flex items-start justify-between mb-2">
                <div>
                  <h3 class="font-bold text-white text-lg leading-tight group-hover:text-primary transition-colors">${product.name}</h3>
                  <p class="text-slate-400 text-sm mt-1">${brand}</p>
                </div>
                <div class="flex items-center gap-1">
                  <span class="material-symbols-outlined text-yellow-400 text-sm">star</span>
                  <span class="text-slate-400 text-sm">4.5</span>
                </div>
              </div>

              <div class="flex items-center justify-between mt-auto pt-4">
                              <span class="text-2xl font-black text-primary">${new Intl.NumberFormat('en-US').format(Math.round(product.showPrice))}$</span>
                              <button data-detail-id="${(product.productDetails && product.productDetails.length > 0) ? product.productDetails[0].id : ''}" class="btn-add-to-cart bg-primary text-white p-2 rounded-lg hover:bg-primary/90 transition-colors shrink-0">
                                <span class="material-symbols-outlined text-lg">add_shopping_cart</span>
                              </button>
                            </div>
            </div>
          </div>
        `;
        })
        .join("");
    }

    renderPagination(pageData) {
      // If we don't have container initialized, we need to create it inside an adjacent parent.
      // Easiest is to search dynamically.
      let container = document.getElementById('pagination-container');
      if (!container) {
        const main = document.querySelector('main .flex-1');
        if (main) {
          container = document.createElement('div');
          container.id = 'pagination-container';
          main.appendChild(container);
        } else {
          return;
        }
      }

      const { pageNo, totalPages } = pageData;

      if (totalPages <= 1) {
        container.innerHTML = '';
        return;
      }

      let html = `<div class="flex items-center justify-center gap-2 mt-16">`;

      // Prev button
      html += `
        <button data-page="${pageNo - 1}" class="pagination-btn flex h-10 w-10 items-center justify-center rounded-lg border border-white/10 bg-surface text-slate-400 hover:bg-primary hover:text-background-dark transition-colors ${pageNo === 0 ? 'pointer-events-none opacity-50' : ''}">
          <span class="material-symbols-outlined text-sm">chevron_left</span>
        </button>
      `;

      // Page numbers (simplified calculation)
      let start = Math.max(0, pageNo - 2);
      let end = Math.min(totalPages - 1, pageNo + 2);

      if (pageNo - 2 < 0 && totalPages > 4) end = 4;
      if (pageNo + 2 >= totalPages && totalPages > 4) start = totalPages - 5;

      if (start > 0) {
        html += `
          <button data-page="0" class="pagination-btn flex h-10 w-10 items-center justify-center rounded-lg border border-white/10 bg-surface text-slate-400 hover:border-primary/50 font-bold text-sm">1</button>
          ${start > 1 ? '<span class="flex h-10 w-10 items-center justify-center text-slate-500 font-bold">...</span>' : ''}
        `;
      }

      for (let i = start; i <= end; i++) {
        html += `
          <button data-page="${i}" class="pagination-btn flex h-10 w-10 items-center justify-center rounded-lg border transition-colors font-bold text-sm ${i === pageNo ? 'bg-primary text-background-dark border-primary' : 'bg-surface text-slate-400 border-white/10 hover:border-primary/50 text-slate-300'}">
            ${i + 1}
          </button>
        `;
      }

      if (end < totalPages - 1) {
        html += `
          ${end < totalPages - 2 ? '<span class="flex h-10 w-10 items-center justify-center text-slate-500 font-bold">...</span>' : ''}
          <button data-page="${totalPages - 1}" class="pagination-btn flex h-10 w-10 items-center justify-center rounded-lg border border-white/10 bg-surface text-slate-400 hover:border-primary/50 font-bold text-sm">${totalPages}</button>
        `;
      }

      // Next button
      html += `
        <button data-page="${pageNo + 1}" class="pagination-btn flex h-10 w-10 items-center justify-center rounded-lg border border-white/10 bg-surface text-slate-400 hover:bg-primary hover:text-background-dark transition-colors ${pageNo >= totalPages - 1 ? 'pointer-events-none opacity-50' : ''}">
          <span class="material-symbols-outlined text-sm">chevron_right</span>
        </button>
      `;

      html += `</div>`;
      container.innerHTML = html;
    }
  }

  // --- Main Application ---
  const state = new StateManager();
  const api = new ProductService();
  const ui = new ProductUI();

  // Keep track if it's the first load
  let isInitialLoad = true;

  const loadProducts = async () => {
    // If it's initial load and there are no specific filters in URL, we could optionally skip fetch,
    // because Thymeleaf already rendered them. But we must update if URL has filters different than server.
    if (!isInitialLoad) {
      ui.renderLoading();
    }

    state.updateUrl();

    try {
      const data = await api.fetchProducts(state.filters);
      if (data) { // null if aborted
        ui.renderProducts(data.content);
        ui.renderPagination(data);
      }
    } catch (error) {
      console.error('Failed to load products:', error);
      ui.renderError(error);
    } finally {
      isInitialLoad = false;
    }
  };

  // If there are url query params, fetch immediately to sync state. 
  // Otherwise, leave the SSR HTML as is until Interaction.
  if (window.location.search) {
    loadProducts();
  }

  // Event Listeners Setup
  const searchInput = document.getElementById('searchInput');
  const priceRange = document.getElementById('priceRange');
  const categoryCheckboxes = document.querySelectorAll('.category-filter');
  const brandCheckboxes = document.querySelectorAll('.brand-filter');
  const genderCheckboxes = document.querySelectorAll('.gender-filter');
  const sportsCheckboxes = document.querySelectorAll('.sports-filter');


  // Helper for multiple select checkbox group
  const handleCheckboxGroup = (checkboxes, filterKey) => {
    checkboxes.forEach(cb => {
      cb.addEventListener('change', (e) => {
        if (!state.filters[filterKey]) state.filters[filterKey] = [];
        if (e.target.checked) {
          if (!state.filters[filterKey].includes(e.target.value)) {
            state.filters[filterKey].push(e.target.value);
          }
        } else {
          state.filters[filterKey] = state.filters[filterKey].filter(val => val !== e.target.value);
        }
        state.filters.page = 0; // reset to first page
        loadProducts();
      });
    });
  };

  handleCheckboxGroup(categoryCheckboxes, 'categories');
  handleCheckboxGroup(brandCheckboxes, 'brands');
  handleCheckboxGroup(genderCheckboxes, 'gender');
  handleCheckboxGroup(sportsCheckboxes, 'sports');

  if (searchInput) {
    searchInput.addEventListener('input', debounce((e) => {
      state.filters.keyword = e.target.value;
      state.filters.page = 0;
      loadProducts();
    }, 400));
  }

  if (priceRange) {
    const priceDisplay = document.getElementById('priceDisplay'); // Lấy element

    // Sự kiện 'input' chạy liên tục khi kéo (không bị debounce để UI mượt)
    priceRange.addEventListener('input', (e) => {
      if (priceDisplay) priceDisplay.textContent = new Intl.NumberFormat('en-US').format(e.target.value) + '$';
    });

    // Gọi API thì vẫn giữ debounce
    priceRange.addEventListener('input', debounce((e) => {
      state.filters.maxPrice = e.target.value;
      state.filters.page = 0;
      loadProducts();
    }, 300));
  }

  // Delegation for pagination clicks and clear button
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.pagination-btn');
    if (btn) {
      e.preventDefault();
      const page = parseInt(btn.getAttribute('data-page'));
      if (!isNaN(page)) {
        state.filters.page = page;
        isInitialLoad = false; // ensure loader shows
        loadProducts();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    }

    const clearBtn = e.target.closest('#clearFiltersBtn');
    if (clearBtn) {
      state.filters = { keyword: '', categories: [], brands: [], gender: [], sports: [], minPrice: null, maxPrice: null, page: 0, size: 10 };
      state.updateUrl();
      state.syncUI();
      isInitialLoad = false;
      loadProducts();
    }

    // Add to cart delegation
    const addToCartBtn = e.target.closest('.btn-add-to-cart');
    if (addToCartBtn) {
      e.preventDefault();
      const detailId = addToCartBtn.getAttribute('data-detail-id');
      if (!detailId) {
        alert('This product is currently unavailable.');
        return;
      }

      // Hardcode userId = 1 for testing purposes
      const userId = 1;

      // 1. Get or create cart for user
      fetch(`/api/v1/carts/user/${userId}`)
        .then(response => {
          if (response.ok) {
            return response.json();
          }
          // If cart not found (or 500 error from backend throw), create a new one
          return fetch('/api/v1/carts', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            body: JSON.stringify({ userId: userId })
          }).then(res => {
            if (!res.ok) throw new Error('Failed to create cart');
            return res.json();
          });
        })
        .then(cart => {
          // 2. Add product to cart details
          return fetch('/api/v1/cart-details', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            body: JSON.stringify({
              cartId: cart.id,
              productDetailId: parseInt(detailId),
              quantity: 1
            })
          });
        })
        .then(response => {
          if (response.ok) {
            showSuccessToast();
            // Update cart count in header
            const cartCountElement = document.querySelector('a[href="/cart"] span.absolute') || document.querySelector('.cart-count');
            if (cartCountElement) {
              const currentCount = parseInt(cartCountElement.textContent) || 0;
              cartCountElement.textContent = currentCount + 1;
            }
          } else {
            alert('Failed to add item to cart. Please try again.');
          }
        })
        .catch(error => {
          console.error('Error adding to cart:', error);
          alert('An error occurred. Please try again later.');
        });
    }
  });

  const resetBtn = document.getElementById('resetFiltersBtn');
  if (resetBtn) {
    resetBtn.addEventListener('click', () => {
      state.filters = { keyword: '', categories: [], brands: [], gender: [], sports: [], minPrice: null, maxPrice: null, page: 0, size: 10 };
      state.updateUrl();
      state.syncUI();
      isInitialLoad = false;
      loadProducts();
    });
  }

  // Handle browser back/forward buttons
  window.addEventListener('popstate', (event) => {
    if (event.state) {
      state.filters = event.state;
    } else {
      state.filters = { keyword: '', categories: [], brands: [], gender: [], sports: [], minPrice: null, maxPrice: null, page: 0, size: 10 };
      state.initFromUrl();
    }
    state.syncUI();
    isInitialLoad = false; // back/forw is not initial load
    loadProducts();
  });
});