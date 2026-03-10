
// ==================== STATE ====================
const state = {
    authType: localStorage.getItem('authType') || 'user',
    userId: localStorage.getItem('userId'),
    artistId: localStorage.getItem('artistId'),
    userName: localStorage.getItem('userName'),
    token: localStorage.getItem('token'),
    isPlaying: false,
    currentSong: null,
    queue: [],
    queueIndex: -1,
    shuffle: false,
    repeat: 'off', // 'off' | 'all' | 'one'
    volume: 0.7,
    audio: new Audio()
};

// Configure audio element
state.audio.volume = state.volume;
state.audio.addEventListener('timeupdate', () => {
    if (!state.currentSong) return;
    const current = state.audio.currentTime;
    const duration = state.audio.duration || state.currentSong.durationSeconds || 1;
    const pct = (current / duration) * 100;
    const fill = document.getElementById('progress-fill');
    if (fill) fill.style.width = pct + '%';
    const curTimeEl = document.getElementById('current-time');
    if (curTimeEl) curTimeEl.textContent = formatTime(current);
    const totTimeEl = document.getElementById('total-time');
    if (totTimeEl) totTimeEl.textContent = formatTime(duration);
});

state.audio.addEventListener('ended', () => {
    if (state.repeat === 'one') {
        playCurrent();
    } else if (state.repeat === 'all') {
        // If at end of queue, wrap to beginning
        if (state.queueIndex >= state.queue.length - 1) {
            state.queueIndex = 0;
            playCurrent();
        } else {
            playNext();
        }
    } else {
        // repeat === 'off'
        if (state.queueIndex < state.queue.length - 1) {
            playNext();
        } else {
            // Stop at end of queue
            state.isPlaying = false;
            document.getElementById('btn-play-pause').textContent = '▶';
            document.getElementById('player-thumb').className = 'player-thumb';
        }
    }
});

// Sync UI on load
// Maps URL paths to navigate() page names
function pathToPage(pathname) {
    const map = {
        '/home': 'home', '/songs': 'songs', '/albums': 'albums',
        '/artists': 'artists', '/genres': 'genres', '/podcasts': 'podcasts',
        '/playlists': 'playlists', '/favorites': 'favorites',
        '/history': 'history', '/profile': 'profile',
        '/upload': 'upload', '/my-songs': 'my-songs',
        '/my-albums': 'my-albums', '/my-podcasts': 'my-podcasts',
        '/stats': 'stats'
    };
    return map[pathname] || 'home';
}

// Handle browser back/forward buttons
window.addEventListener('popstate', (event) => {
    const publicPaths = ['/', '/login', '/register'];
    const currentPath = window.location.pathname;

    // Prevent traversing back to login/home if logged in
    if (state.token && publicPaths.includes(currentPath)) {
        // Since we are forcing them forward, replace current view
        history.forward();
        return;
    }

    const page = pathToPage(currentPath);
    navigate(page, true); // true = skip pushState
});

window.addEventListener('DOMContentLoaded', () => {
    const publicPaths = ['/', '/login', '/register'];
    const currentPath = window.location.pathname;

    // Route Guard: redirect to root if no token and trying to access protected route
    if (!state.token && !publicPaths.includes(currentPath)) {
        window.location.replace('/');
        return;
    }

    // If user is logged in but on /login or /register, redirect to /home
    if (state.token && (currentPath === '/login' || currentPath === '/register')) {
        window.location.replace('/home');
        return;
    }

    if (state.userId || state.artistId) {
        const initial = state.userName ? state.userName[0].toUpperCase() : 'U';
        const avatar = document.getElementById('user-avatar');
        if (avatar) avatar.textContent = initial;
        const displayName = document.getElementById('user-display-name');
        if (displayName) displayName.textContent = state.userName;

        const playerBar = document.getElementById('player-bar');
        if (playerBar) playerBar.style.display = 'grid';

        // Hide/show sidebar items
        const isArtist = state.artistId != null;
        document.querySelectorAll('.artist-only').forEach(el => el.style.display = isArtist ? 'flex' : 'none');
        const playlists = document.getElementById('sidebar-playlists');
        if (playlists) playlists.style.display = isArtist ? 'none' : 'flex';
        const favorites = document.getElementById('sidebar-favorites');
        if (favorites) favorites.style.display = isArtist ? 'none' : 'flex';
        const historyEl = document.getElementById('sidebar-history');
        if (historyEl) historyEl.style.display = isArtist ? 'none' : 'flex';
        const userSection = document.getElementById('user-sidebar-section');
        if (userSection) userSection.style.display = isArtist ? 'none' : 'block';

        // Navigate to the page indicated by the current URL
        const page = pathToPage(currentPath);
        navigate(page, true); // true = skip pushState on initial load
    }
});

function formatTime(secs) {
    if (!secs || isNaN(secs)) return '0:00';
    const m = Math.floor(secs / 60);
    const s = Math.floor(secs % 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
}

// ==================== API HELPER ====================
async function api(url, method = 'GET', body = null) {
    const opts = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const token = localStorage.getItem('token');
    if (token) {
        opts.headers['Authorization'] = 'Bearer ' + token;
    }

    if (body) opts.body = JSON.stringify(body);
    const res = await fetch('/api' + url, opts);
    if (!res.ok && res.status === 401) {
        // Handle unauthorized (token expired or invalid)
        console.warn('Unauthorized or token expired');
        // Optionally redirect to login if not already there
        // if (!window.location.pathname.includes('/login')) window.location.href = '/login';
    }
    if (!res.ok && res.status === 404) return null;
    return res.json();
}
// ==================== NAVIGATION ====================
async function navigate(page, skipPush) {
    const area = document.getElementById('content-area');
    if (!area) return;

    const publicPaths = ['home', 'login', 'register', '/'];
    // Redundant guard for protected SPA paths without tokens
    if (!state.token && !publicPaths.includes(page) && page !== '') {
        logout();
        return;
    }

    // Update browser URL bar (unless this is initial load or popstate)
    if (!skipPush) {
        const urlPath = '/' + (page === 'home' ? 'home' : page);
        history.pushState({ page }, '', urlPath);
    }

    // Apply page-specific background image class to BODY for fixed positioning
    const bgMap = {
        'home': 'page-bg-home', 'songs': 'page-bg-songs', 'albums': 'page-bg-albums',
        'artists': 'page-bg-artists', 'upload': 'page-bg-artist-upload', 'my-songs': 'page-bg-artist-music',
        'my-albums': 'page-bg-artist-music', 'my-podcasts': 'page-bg-artist-music', 'stats': 'page-bg-artist-analytics',
        'genres': 'page-bg-songs', 'podcasts': 'page-bg-home',
        'playlists': 'page-bg-songs', 'favorites': 'page-bg-songs',
        'history': 'page-bg-home', 'profile': 'page-bg-home'
    };
    // Remove all page-bg-* classes from body
    document.body.className = document.body.className.replace(/page-bg-\S+/g, '').trim();
    // Add the appropriate one to body
    const bgClass = bgMap[page] || 'page-bg-home';
    document.body.classList.add(bgClass);

    // Show loading state
    area.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

    // Highlight active sidebar item
    document.querySelectorAll('.sidebar-item').forEach(item => {
        const onclick = item.getAttribute('onclick');
        if (onclick && onclick.includes(`'${page}'`)) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    try {
        switch (page) {
            case 'home': await renderHome(); break;
            case 'songs': await renderSongs(); break;
            case 'albums': await renderAlbums(); break;
            case 'artists': await renderArtists(); break;
            case 'genres': await renderGenres(); break;
            case 'podcasts': await renderPodcasts(); break;
            case 'playlists': await renderPlaylists(); break;
            case 'favorites': await renderFavorites(); break;
            case 'history': await renderHistory(); break;
            case 'profile': await renderProfile(); break;
            case 'upload': await renderUploadSong(); break;
            case 'my-songs': await renderMySongs(); break;
            case 'my-albums': await renderMyAlbums(); break;
            case 'my-podcasts': await renderMyPodcasts(); break;
            case 'stats': await renderArtistStats(); break;
            default: await renderHome();
        }
    } catch (err) {
        console.error('Navigation error:', err);
        area.innerHTML = `<div class="empty-state"><h3>Error loading page</h3><p>${err.message}</p></div>`;
    }
}

// ==================== TOAST ====================
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `${type === 'success' ? '✅' : '❌'} ${message}`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// ==================== AUTH ====================
function switchAuthType(type) {
    state.authType = type;
    localStorage.setItem('authType', type);
    document.querySelectorAll('.auth-tab').forEach((t, i) => {
        t.classList.toggle('active', (i === 0 && type === 'user') || (i === 1 && type === 'artist'));
    });

    // Determine which form is currently visible and maintain it
    const loginForm = document.getElementById('login-form');
    const isLoginVisible = loginForm && loginForm.style.display !== 'none';
    const regForm = document.getElementById('register-form');
    const isRegVisible = regForm && regForm.style.display !== 'none';

    if (isRegVisible) {
        toggleAuthForm('register');
    } else {
        toggleAuthForm('login');
    }
}

function toggleAuthForm(form) {
    const loginForm = document.getElementById('login-form');
    const regForm = document.getElementById('register-form');
    const forgotForm = document.getElementById('forgot-form');

    if (loginForm) loginForm.style.display = form === 'login' ? 'block' : 'none';
    if (regForm) regForm.style.display = form === 'register' ? 'block' : 'none';
    if (forgotForm) forgotForm.style.display = form === 'forgot' ? 'block' : 'none';

    // Clear messages
    const err = document.getElementById('error-msg');
    if (err) err.style.display = 'none';
    const succ = document.getElementById('success-msg');
    if (succ) succ.style.display = 'none';

    // Toggle role-specific registration fields
    const userFields = document.getElementById('user-register-fields');
    const artistFields = document.getElementById('artist-register-fields');
    if (userFields) userFields.style.display = (form === 'register' && state.authType === 'user') ? 'block' : 'none';
    if (artistFields) artistFields.style.display = (form === 'register' && state.authType === 'artist') ? 'block' : 'none';

    // Ensure inputs are required only when visible
    if (userFields) {
        userFields.querySelectorAll('input').forEach(input => input.required = (state.authType === 'user' && form === 'register'));
    }
    if (artistFields) {
        const optionalIds = ['reg-instagram', 'reg-youtube', 'reg-spotify'];
        artistFields.querySelectorAll('input, textarea').forEach(input => {
            if (optionalIds.includes(input.id)) {
                input.required = false;
            } else {
                input.required = (state.authType === 'artist' && form === 'register');
            }
        });
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    const endpoint = state.authType === 'user' ? '/auth/user/login' : '/auth/artist/login';
    const data = await api(endpoint, 'POST', { email, password });

    if (data && data.success) {
        localStorage.setItem('authType', state.authType);
        localStorage.setItem('token', data.token);
        if (state.authType === 'user') {
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('userName', data.fullName);
        } else {
            localStorage.setItem('artistId', data.artistId);
            localStorage.setItem('userName', data.userName || data.stageName);
        }
        window.location.replace('/home');
    } else {
        const err = document.getElementById('error-msg');
        if (err) {
            err.textContent = data ? data.message : 'Login failed';
            err.style.display = 'block';
        }
    }
}

async function handleRegister(e) {
    e.preventDefault();
    let data;

    if (state.authType === 'user') {
        const user = {
            fullName: document.getElementById('reg-name').value,
            email: document.getElementById('reg-email').value,
            passwordHash: document.getElementById('reg-password').value,
            phone: document.getElementById('reg-phone').value,
            securityQuestion: document.getElementById('reg-security-q').value,
            securityAnswerHash: document.getElementById('reg-security-a').value,
            passwordHint: document.getElementById('reg-hint').value
        };
        data = await api('/auth/user/register', 'POST', user);
    } else {
        const artist = {
            stageName: document.getElementById('reg-stagename').value,
            email: document.getElementById('reg-artist-email').value,
            passwordHash: document.getElementById('reg-artist-password').value,
            genre: document.getElementById('reg-genre').value,
            bio: document.getElementById('reg-bio').value,
            instagramLink: document.getElementById('reg-instagram').value,
            youtubeLink: document.getElementById('reg-youtube').value,
            spotifyLink: document.getElementById('reg-spotify').value,
            securityQuestion: document.getElementById('reg-artist-security-q').value,
            securityAnswerHash: document.getElementById('reg-artist-security-a').value,
            passwordHint: document.getElementById('reg-artist-hint').value
        };
        data = await api('/auth/artist/register', 'POST', artist);
    }

    if (data && data.success) {
        if (data.token) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('authType', state.authType);
            if (state.authType === 'user') {
                localStorage.setItem('userId', data.userId);
                localStorage.setItem('userName', data.fullName);
            } else {
                localStorage.setItem('artistId', data.artistId);
                localStorage.setItem('userName', data.userName || data.stageName);
            }
            window.location.replace('/home');
            return;
        }
        const succ = document.getElementById('success-msg');
        if (succ) {
            succ.textContent = 'Registration successful! Please sign in.';
            succ.style.display = 'block';
        }
        toggleAuthForm('login');
    } else {
        const err = document.getElementById('error-msg');
        if (err) {
            err.textContent = data ? data.message : 'Registration failed';
            err.style.display = 'block';
        }
    }
}

function logout() {
    localStorage.clear();
    window.location.replace('/');
}

function showForgotPassword() {
    toggleAuthForm('forgot');
    document.getElementById('security-q-container').style.display = 'none';
    document.getElementById('forgot-btn').textContent = 'Get Security Question';
    document.getElementById('forgot-email').disabled = false;
}

async function handleForgotPassword(e) {
    e.preventDefault();
    const email = document.getElementById('forgot-email').value;
    const container = document.getElementById('security-q-container');
    const btn = document.getElementById('forgot-btn');
    const role = state.authType; // 'user' or 'artist'

    if (container.style.display === 'none') {
        // Step 1: Get security question
        const endpoint = role === 'user' ? `/auth/user/security-question?email=${email}` : `/auth/artist/security-question?email=${email}`;
        const data = await api(endpoint);

        if (data && data.success) {
            document.getElementById('security-q-label').textContent = `Question: ${data.securityQuestion || 'No question set'}`;
            if (data.passwordHint) {
                document.getElementById('security-q-label').innerHTML += `<br><small style="color:var(--text-secondary)">Hint: ${data.passwordHint}</small>`;
            }
            container.style.display = 'block';
            btn.textContent = 'Reset Password';
            document.getElementById('forgot-email').disabled = true;
        } else {
            showToast(data ? data.message : 'User not found', 'error');
        }
    } else {
        // Step 2: Submit answer and new password
        const securityAnswer = document.getElementById('forgot-answer').value;
        const newPassword = document.getElementById('forgot-new-pass').value;

        if (!securityAnswer || !newPassword) {
            showToast('Please fill all fields', 'error');
            return;
        }

        const endpoint = role === 'user' ? '/auth/user/forgot-password' : '/auth/artist/forgot-password';
        const data = await api(endpoint, 'POST', { email, securityAnswer, newPassword });

        if (data && data.success) {
            showToast('Password reset successful!', 'success');
            toggleAuthForm('login');
        } else {
            showToast(data ? data.message : 'Reset failed', 'error');
        }
    }
}

// ==================== PLAYER LOGIC ====================
function playSongFromModel(id, title, artist, url) {
    state.currentSong = { songId: id, title: title, artistName: artist, fileUrl: url };
    state.isPlaying = true;

    const titleEl = document.getElementById('player-title');
    if (titleEl) titleEl.textContent = title;
    const artistEl = document.getElementById('player-artist');
    if (artistEl) artistEl.textContent = artist;
    const playBtn = document.getElementById('btn-play-pause');
    if (playBtn) playBtn.textContent = '⏸';
    const playerBar = document.getElementById('player-bar');
    if (playerBar) playerBar.style.display = 'grid';

    if (url) {
        state.audio.src = url;
        state.audio.load();
        state.audio.play().catch(e => console.error("Playback failed", e));

        // Record play history
        if (state.userId) api('/songs/' + id + '/play?userId=' + state.userId, 'POST');
    }
}

function togglePlayPause() {
    if (!state.currentSong) return;
    const btn = document.getElementById('btn-play-pause');
    if (state.audio.paused) {
        state.audio.play();
        if (btn) btn.textContent = '⏸';
    } else {
        state.audio.pause();
        if (btn) btn.textContent = '▶';
    }
}

function setVolume(e) {
    const bar = e.currentTarget;
    const rect = bar.getBoundingClientRect();
    const pct = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width));
    state.volume = pct;
    state.audio.volume = pct;
    const fill = document.getElementById('volume-fill');
    if (fill) fill.style.width = (pct * 100) + '%';
}

function seekTo(e) {
    if (!state.currentSong || !state.audio.duration) return;
    const bar = e.currentTarget;
    const rect = bar.getBoundingClientRect();
    const pct = (e.clientX - rect.left) / rect.width;
    state.audio.currentTime = pct * state.audio.duration;
}

// Modal Helpers
function openModal(title, bodyHtml) {
    const modal = document.getElementById('modal-overlay');
    if (!modal) return;
    document.getElementById('modal-title').textContent = title;
    document.getElementById('modal-body').innerHTML = bodyHtml;
    modal.classList.add('active');
}

function closeModal() {
    const modal = document.getElementById('modal-overlay');
    if (modal) modal.classList.remove('active');
}

async function handleSearch(e) {
    if (e.key === 'Enter') {
        const q = e.target.value.trim();
        if (!q) return;
        const area = document.getElementById('content-area');
        area.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

        // Search songs, artists, and albums in parallel
        const [songs, artists, allAlbums] = await Promise.all([
            api('/songs/search?q=' + encodeURIComponent(q)),
            api('/artists/search?q=' + encodeURIComponent(q)),
            api('/albums')
        ]);

        // Filter albums client-side by keyword
        const qLower = q.toLowerCase();
        const albums = (allAlbums || []).filter(a =>
            (a.title && a.title.toLowerCase().includes(qLower)) ||
            (a.artistName && a.artistName.toLowerCase().includes(qLower))
        );

        const totalResults = (songs || []).length + (artists || []).length + albums.length;

        let html = `<div class="animate-in"><div class="page-header"><div><h2>🔍 Search: "${q}"</h2><p class="subtitle">${totalResults} results found</p></div></div>`;

        // Songs section
        if (songs && songs.length > 0) {
            html += `<div class="section-header"><h3>🎶 Songs (${songs.length})</h3></div>`;
            html += renderSongList(songs);
        }

        // Artists section
        if (artists && artists.length > 0) {
            html += `<div class="section-header" style="margin-top:24px"><h3>🎤 Artists (${artists.length})</h3></div>`;
            html += '<div class="cards-grid">';
            artists.forEach(artist => {
                html += `<div class="card" onclick="viewArtist(${artist.artistId})">
                    <div class="card-image" style="background:linear-gradient(135deg, #fce7f3, #f9a8d4);border-radius:50%">
                        ${artist.profilePicture ? `<img src="${artist.profilePicture}" alt="pfp" style="width:100%;height:100%;object-fit:cover;border-radius:50%">` : '🎤'}
                    </div>
                    <div class="card-title">${artist.stageName}</div>
                    <div class="card-subtitle">${artist.genre || ''}</div>
                </div>`;
            });
            html += '</div>';
        }

        // Albums section
        if (albums.length > 0) {
            html += `<div class="section-header" style="margin-top:24px"><h3>💿 Albums (${albums.length})</h3></div>`;
            html += '<div class="cards-grid">';
            albums.forEach(album => {
                html += `<div class="card" onclick="viewAlbum(${album.albumId})">
                    ${cardImageSimple(album.coverImageUrl, '💿', 'background:linear-gradient(135deg, #dbeafe, #c7d2fe)')}
                    <div class="card-title">${album.title}</div>
                    <div class="card-subtitle">${album.artistName || 'Unknown'} • ${album.releaseDate || ''}</div>
                </div>`;
            });
            html += '</div>';
        }

        // No results
        if (totalResults === 0) {
            html += '<div class="empty-state"><span class="icon">🔍</span><h3>No results found</h3><p>Try different keywords</p></div>';
        }

        html += '</div>';
        area.innerHTML = html;
    }
}

// ==================== RENDERERS ====================
function formatDuration(secs) {
    if (!secs) return '0:00';
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
}

function songIcon(song) {
    const colors = ['#7c3aed', '#e879f9', '#3b82f6', '#22c55e', '#f97316', '#ef4444'];
    const c = colors[(song.songId || 0) % colors.length];
    return `background:linear-gradient(135deg, ${c}33, ${c}66)`;
}

// Card image with optional actual image or fallback emoji
function cardImage(imageUrl, emoji, fallbackStyle) {
    if (imageUrl) {
        return `<div class="card-image"><img src="${imageUrl}" alt="cover" style="width:100%;height:100%;object-fit:cover;border-radius:inherit"></div>`;
    }
    return `<div class="card-image" style="${fallbackStyle || ''}">${emoji || '🎵'}</div>`;
}

// Simplified card image (no hover effects)
function cardImageSimple(imageUrl, emoji, fallbackStyle) {
    if (imageUrl) {
        return `<div class="card-image"><img src="${imageUrl}" alt="cover" style="width:100%;height:100%;object-fit:cover;border-radius:inherit"></div>`;
    }
    return `<div class="card-image" style="${fallbackStyle || ''}">${emoji || '🎵'}</div>`;
}

function songThumbHtml(song) {
    if (song.coverImageUrl) {
        return `<div class="song-thumb"><img src="${song.coverImageUrl}" alt="cover" style="width:100%;height:100%;object-fit:cover;border-radius:inherit"></div>`;
    }
    return `<div class="song-thumb" style="${songIcon(song)}">🎵</div>`;
}

function renderSongList(songs, showNum = true, options = {}) {
    if (!songs || songs.length === 0) {
        return `<div class="empty-state"><span class="icon">🎵</span><h3>No songs found</h3><p>Try exploring or uploading some music</p></div>`;
    }
    let html = '';

    // Play All / Shuffle Play buttons
    if (songs.length > 0) {
        html += `<div style="display:flex;gap:10px;margin-bottom:16px">
            <button class="btn btn-primary btn-sm" onclick="playSongFromList(${JSON.stringify(songs).replace(/"/g, '&quot;')}, 0)">▶ Play All</button>
            <button class="btn btn-secondary btn-sm" onclick="shufflePlayList(${JSON.stringify(songs).replace(/"/g, '&quot;')})">🔀 Shuffle Play</button>
        </div>`;
    }

    html += '<div class="song-list">';
    songs.forEach((song, i) => {
        html += `
        <div class="song-row" onclick="playSongFromList(${JSON.stringify(songs).replace(/"/g, '&quot;')}, ${i})">
            <div>
                <span class="num">${i + 1}</span>
                <span class="play-icon">▶</span>
            </div>
            <div class="song-info">
                ${songThumbHtml(song)}
                <div class="song-details">
                    <div class="song-title">${song.title || 'Unknown'}</div>
                    <div class="song-artist">${song.artistName || 'Unknown Artist'}</div>
                </div>
            </div>
            <div class="song-album">${song.albumTitle || song.genreName || '-'}</div>
            <div class="song-actions" onclick="event.stopPropagation()">
                <button class="song-action-btn" onclick="showSongDetail(${song.songId})" title="Song Details" style="color:var(--text-secondary)">ℹ️</button>
                ${song.fileUrl ? `<a href="${song.fileUrl}" class="song-action-btn" style="background:#3b82f633;padding:4px 8px;border-radius:6px;color:#3b82f6" title="Download" download>⬇️</a>` : ''}
                <button class="song-action-btn" onclick="toggleFavoriteSong(${song.songId})" title="Favorite">🤍</button>
                <button class="song-action-btn" onclick="showAddToPlaylistModal(${song.songId})" style="color:var(--accent-light)" title="Add to playlist">➕</button>
                ${options.playlistId ? `<button class="song-action-btn" onclick="removeFromPlaylist(${options.playlistId}, ${song.songId})" style="color:#ef4444" title="Remove from playlist">❌</button>` : ''}
                ${state.artistId && song.artistId == state.artistId ? `<button class="song-action-btn" onclick="deleteSong(${song.songId})" style="color:var(--text-muted)" title="Delete">🗑️</button>` : ''}
            </div>
        </div>`;
    });
    html += '</div>';
    return html;
}

async function renderHome() {
    const area = document.getElementById('content-area');
    const [topSongs, songs, albums] = await Promise.all([
        api('/songs/top?limit=10'),
        api('/songs'),
        api('/albums')
    ]);

    let html = `<div class="animate-in">`;
    html += `<div class="page-header"><div><h2>Welcome back, ${state.userName} 👋</h2><p class="subtitle">Discover new music and enjoy your favorites</p></div></div>`;

    // Top Played
    if (topSongs && topSongs.length > 0) {
        html += '<div class="section-header"><h3>🔥 Trending Now</h3></div>';
        html += '<div class="cards-grid">';
        topSongs.slice(0, 6).forEach(song => {
            html += `<div class="card" onclick="playSongFromList(${JSON.stringify(topSongs).replace(/"/g, '&quot;')}, ${topSongs.indexOf(song)})">
                ${cardImage(song.coverImageUrl, '🎵', songIcon(song))}
                <div class="card-title">${song.title}</div>
                <div class="card-subtitle">${song.artistName || 'Unknown'} • ${song.playCount || 0} plays</div>
            </div>`;
        });
        html += '</div>';
    }

    // Recently Added
    if (songs && songs.length > 0) {
        html += '<div class="section-header"><h3>🆕 Recently Added</h3><a onclick="navigate(\'songs\')">See all</a></div>';
        html += renderSongList(songs.slice(0, 5));
    }

    // Albums
    if (albums && albums.length > 0) {
        html += '<div class="section-header" style="margin-top:24px"><h3>💿 Albums</h3><a onclick="navigate(\'albums\')">See all</a></div>';
        html += '<div class="cards-grid">';
        albums.slice(0, 6).forEach(album => {
            html += `<div class="card" onclick="viewAlbum(${album.albumId})">
                ${cardImageSimple(album.coverImageUrl, '💿', 'background:linear-gradient(135deg, #dbeafe, #c7d2fe)')}
                <div class="card-title">${album.title}</div>
                <div class="card-subtitle">${album.artistName || 'Unknown Artist'}</div>
            </div>`;
        });
        html += '</div>';
    }

    // Podcasts section
    const podcasts = await api('/podcasts');
    if (podcasts && podcasts.length > 0) {
        html += '<div class="section-header" style="margin-top:24px"><h3>🎙️ Explore Podcasts</h3><a onclick="navigate(\'podcasts\')">See all</a></div>';
        html += '<div class="cards-grid">';
        podcasts.slice(0, 6).forEach(p => {
            html += `<div class="card" onclick="viewPodcast(${p.podcastId})">
                ${cardImageSimple(p.coverImageUrl, '🎙️', 'background:linear-gradient(135deg, #ccfbf1, #99f6e4)')}
                <div class="card-title">${p.title}</div>
                <div class="card-subtitle">${p.hostName || ''}</div>
            </div>`;
        });
        html += '</div>';
    }

    html += '</div>';
    area.innerHTML = html;
}

async function renderSongs() {
    const songs = await api('/songs');
    const area = document.getElementById('content-area');
    area.innerHTML = `<div class="animate-in">
        <div class="page-header"><div><h2>All Songs</h2><p class="subtitle">${songs.length} tracks available</p></div></div>
        ${renderSongList(songs)}
    </div>`;
}

async function renderAlbums() {
    const albums = await api('/albums');
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in"><div class="page-header"><h2>Albums</h2></div><div class="cards-grid">`;
    if (albums) albums.forEach(album => {
        html += `<div class="card" onclick="viewAlbum(${album.albumId})">
            ${cardImageSimple(album.coverImageUrl, '💿', 'background:linear-gradient(135deg, #dbeafe, #c7d2fe)')}
            <div class="card-title">${album.title}</div>
            <div class="card-subtitle">${album.artistName || 'Unknown'} • ${album.releaseDate || ''}</div>
        </div>`;
    });
    html += '</div></div>';
    area.innerHTML = html;
}

async function viewAlbum(albumId) {
    const [album, songs] = await Promise.all([api('/albums/' + albumId), api('/albums/' + albumId + '/songs')]);
    const area = document.getElementById('content-area');
    if (!album) { area.innerHTML = '<div class="empty-state"><h3>Album not found</h3></div>'; return; }
    const isOwner = state.artistId && album.artistId == state.artistId;
    area.innerHTML = `<div class="animate-in">
        <div class="page-header" style="display:flex;align-items:flex-end;gap:20px">
            <div style="width:160px;height:160px;border-radius:12px;overflow:hidden;flex-shrink:0;background:linear-gradient(135deg,#dbeafe,#c7d2fe);display:flex;align-items:center;justify-content:center;font-size:48px">
                ${album.coverImageUrl ? `<img src="${album.coverImageUrl}" style="width:100%;height:100%;object-fit:cover">` : '💿'}
            </div>
            <div style="padding-bottom:10px">
                <p class="subtitle" style="margin:0;text-transform:uppercase;font-size:12px;letter-spacing:1px">ALBUM</p>
                <h2 style="margin:4px 0">${album.title}</h2>
                <p class="subtitle" style="margin:0">${album.artistName || 'Unknown Artist'} • ${album.releaseDate || ''} • ${(songs || []).length} tracks</p>
                ${isOwner ? `<div style="margin-top:10px;display:flex;gap:8px">
                    <button class="btn btn-primary btn-sm" onclick="showAddSongToAlbumModal(${albumId})">➕ Add Songs</button>
                    <button class="btn btn-secondary btn-sm" onclick="showEditAlbumModal(${albumId})">✏️ Edit</button>
                </div>` : ''}
            </div>
        </div>
        <p style="color:var(--text-secondary);margin:16px 0 20px;line-height:1.6">${album.description || ''}</p>
        ${isOwner ? renderAlbumSongListWithRemove(songs || [], albumId) : renderSongList(songs || [])}
    </div>`;
}

function renderAlbumSongListWithRemove(songs, albumId) {
    if (!songs || songs.length === 0) {
        return '<div class="empty-state"><span class="icon">🎵</span><h3>No songs in this album</h3><p>Add songs to get started</p></div>';
    }
    let html = '<div class="song-list">';
    songs.forEach((song, i) => {
        html += `<div class="song-row" onclick="playSongFromList(${JSON.stringify(songs).replace(/"/g, '&quot;')}, ${i})">
            <div><span class="num">${i + 1}</span><span class="play-icon">▶</span></div>
            <div class="song-info">
                ${songThumbHtml(song)}
                <div class="song-details">
                    <div class="song-title">${song.title || 'Unknown'}</div>
                    <div class="song-artist">${song.artistName || 'Unknown Artist'}</div>
                </div>
            </div>
            <div class="song-album">${song.genreName || '-'}</div>
            <div class="song-duration">${formatDuration(song.durationSeconds)}</div>
            <div class="song-actions" onclick="event.stopPropagation()">
                <button class="song-action-btn" onclick="removeSongFromAlbum(${song.songId}, ${albumId})" style="color:#ef4444" title="Remove from Album">❌</button>
            </div>
        </div>`;
    });
    html += '</div>';
    return html;
}

async function renderArtists() {
    const artists = await api('/artists');
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in"><div class="page-header"><h2>Artists</h2></div><div class="cards-grid">`;
    if (artists) artists.forEach(artist => {
        html += `<div class="card" onclick="viewArtist(${artist.artistId})">
            <div class="card-image" style="background:linear-gradient(135deg, #fce7f3, #f9a8d4);border-radius:50%;overflow:hidden">
                ${artist.profilePicture ? `<img src="${artist.profilePicture}" alt="${artist.stageName}" style="width:100%;height:100%;object-fit:cover;border-radius:50%">` : '🎤'}
            </div>
            <div class="card-title">${artist.stageName}</div>
            <div class="card-subtitle">${artist.genre || ''}</div>
        </div>`;
    });
    html += '</div></div>';
    area.innerHTML = html;
}

async function viewArtist(artistId) {
    const [artist, songs, albums] = await Promise.all([
        api('/artists/' + artistId), api('/songs/artist/' + artistId), api('/albums/artist/' + artistId)
    ]);
    const area = document.getElementById('content-area');
    if (!artist) return;
    let html = `<div class="animate-in">
        ${artist.bannerImage ? `<div class="artist-banner" style="height:250px; margin-bottom:-60px; border-radius:15px; overflow:hidden;"><img src="${artist.bannerImage}" style="width:100%; height:100%; object-fit:cover;"></div>` : ''}
        <div class="page-header" style="display:flex; align-items:flex-end; gap:20px; padding: 0 20px; position:relative; z-index:1;">
            <div class="artist-pfp" style="width:120px; height:120px; border-radius:50%; border:4px solid var(--bg-primary); overflow:hidden; background:var(--bg-secondary); display:flex; align-items:center; justify-content:center; font-size:40px;">
                ${artist.profilePicture ? `<img src="${artist.profilePicture}" style="width:100%; height:100%; object-fit:cover;">` : '🎤'}
            </div>
            <div style="padding-bottom:10px;">
                <h2 style="margin:0;">${artist.stageName}</h2>
                <p class="subtitle" style="margin:5px 0;">${artist.genre || ''}</p>
            </div>
        </div>
        <div style="padding: 0 20px; margin-top: 20px;">
            <p style="color:var(--text-secondary); margin-bottom:20px; line-height:1.6;">${artist.bio || ''}</p>
        </div>`;

    if (artist.instagramLink || artist.twitterLink || artist.youtubeLink || artist.spotifyLink || artist.websiteLink) {
        html += '<div style="display:flex; gap:12px; margin: 0 20px 20px; flex-wrap: wrap;">';
        if (artist.instagramLink) html += `<a href="${artist.instagramLink}" target="_blank" class="social-link-btn">Instagram</a>`;
        if (artist.twitterLink) html += `<a href="${artist.twitterLink}" target="_blank" class="social-link-btn">Twitter</a>`;
        if (artist.youtubeLink) html += `<a href="${artist.youtubeLink}" target="_blank" class="social-link-btn">YouTube</a>`;
        if (artist.spotifyLink) html += `<a href="${artist.spotifyLink}" target="_blank" class="social-link-btn">Spotify</a>`;
        if (artist.websiteLink) html += `<a href="${artist.websiteLink}" target="_blank" class="social-link-btn">Website</a>`;
        html += '</div>';
    }

    if (albums && albums.length > 0) {
        html += '<div class="section-header"><h3>Albums</h3></div><div class="cards-grid">';
        albums.forEach(a => {
            html += `<div class="card" onclick="viewAlbum(${a.albumId})">${cardImageSimple(a.coverImageUrl, '💿', 'background:linear-gradient(135deg, #dbeafe, #c7d2fe)')}<div class="card-title">${a.title}</div></div>`;
        });
        html += '</div>';
    }

    html += '<div class="section-header" style="margin-top:20px"><h3>Songs</h3></div>';
    html += renderSongList(songs || []);
    html += '</div>';
    area.innerHTML = html;
}

async function renderGenres() {
    const genres = await api('/genres');
    const area = document.getElementById('content-area');
    const genreColors = ['#7c3aed', '#e879f9', '#3b82f6', '#22c55e', '#f97316', '#ef4444', '#ec4899', '#14b8a6'];
    let html = `<div class="animate-in"><div class="page-header"><h2>Genres</h2></div><div class="cards-grid">`;
    if (genres) genres.forEach((g, i) => {
        const c = genreColors[i % genreColors.length];
        html += `<div class="card" onclick="viewGenreSongs(${g.genreId}, '${g.genreName}')">
            <div class="card-image" style="background:linear-gradient(135deg, ${c}, ${c}99)">🎸</div>
            <div class="card-title">${g.genreName}</div>
        </div>`;
    });
    html += '</div></div>';
    area.innerHTML = html;
}

async function viewGenreSongs(genreId, name) {
    const songs = await api('/songs/genre/' + genreId);
    const area = document.getElementById('content-area');
    area.innerHTML = `<div class="animate-in">
        <div class="page-header"><div><h2>🎸 ${name}</h2><p class="subtitle">${(songs || []).length} songs</p></div></div>
        ${renderSongList(songs || [])}
    </div>`;
}

async function renderPodcasts() {
    console.log('Fetching all podcasts...');
    const podcasts = await api('/podcasts');
    console.log('Podcasts received:', podcasts);
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in"><div class="page-header"><h2>🎙️ Podcasts</h2></div>`;

    if (Array.isArray(podcasts)) {
        if (podcasts.length > 0) {
            html += '<div class="cards-grid">';
            podcasts.forEach(p => {
                html += `<div class="card" onclick="viewPodcast(${p.podcastId})">
                    ${cardImageSimple(p.coverImageUrl, '🎙️', 'background:linear-gradient(135deg, #ccfbf1, #99f6e4)')}
                    <div class="card-title">${p.title}</div>
                    <div class="card-subtitle">${p.hostName || ''} • ${p.category || ''}</div>
                </div>`;
            });
            html += '</div>';
        } else {
            console.warn('No podcasts found in the response.');
            html += '<div class="empty-state"><span class="icon">🎙️</span><h3>No podcasts found</h3><p>Be the first to create one!</p></div>';
        }
    } else {
        html += '<div class="empty-state"><span class="icon">❌</span><h3>Failed to load podcasts</h3><p>Please check your connection and try again.</p></div>';
        console.error('Expected array for podcasts, but got:', podcasts);
    }

    html += '</div>';
    area.innerHTML = html;
}

async function viewPodcast(podcastId) {
    const [podcast, episodes] = await Promise.all([
        api('/podcasts/' + podcastId), api('/podcasts/' + podcastId + '/episodes')
    ]);
    const area = document.getElementById('content-area');
    if (!podcast) {
        area.innerHTML = '<div class="empty-state"><h3>Podcast not found</h3></div>';
        return;
    }
    let html = `<div class="animate-in">
        <div class="page-header"><div><h2>🎙️ ${podcast.title}</h2><p class="subtitle">${podcast.hostName || ''} • ${podcast.category || ''}</p></div></div>
        <p style="color:var(--text-secondary);margin-bottom:20px">${podcast.description || ''}</p>
        <div class="section-header" style="display:flex; justify-content:space-between; align-items:center;">
            <h3>Episodes</h3>
            <div style="display:flex; gap:8px;">
                ${(state.artistId && podcast.artistId == state.artistId) ? `<button class="btn btn-primary btn-sm" onclick="showAddEpisodeModal(${podcastId})">➕ Add Episode</button>` : ''}
                ${(state.artistId && podcast.artistId == state.artistId) ? `<button class="btn btn-secondary btn-sm" onclick="deletePodcast(${podcastId})">🗑️ Delete Podcast</button>` : ''}
            </div>
        </div>`;

    if (Array.isArray(episodes) && episodes.length > 0) {
        html += '<div class="song-list">';
        episodes.forEach((ep, i) => {
            html += `<div class="song-row" onclick="playEpisode(${ep.episodeId}, '${(ep.title || '').replace(/'/g, "\\'")}', '${(podcast.title || '').replace(/'/g, "\\'")}', '${(ep.fileUrl || '').replace(/'/g, "\\'")}')">
                <div><span class="num">${i + 1}</span><span class="play-icon">▶</span></div>
                <div class="song-info"><div class="song-thumb" style="background:linear-gradient(135deg, #ccfbf1, #99f6e4)">🎙️</div>
                <div class="song-details"><div class="song-title">${ep.title}</div><div class="song-artist">${ep.releaseDate || ''}</div></div></div>
                <div class="song-album">${ep.playCount || 0} plays</div>
                <div class="song-duration">${formatDuration(ep.durationSeconds)}</div>
                <div class="song-actions" onclick="event.stopPropagation()">
                    ${ep.fileUrl ? `<a href="${ep.fileUrl}" download="${(ep.title || 'episode').replace(/[^a-z0-9]/gi, '_')}.mp3" class="song-action-btn" title="Download">⬇️</a>` : ''}
                    ${state.artistId && podcast.artistId == state.artistId ? `<button class="song-action-btn" onclick="deleteEpisode(${ep.episodeId}, ${podcastId})" title="Delete">🗑️</button>` : ''}
                </div>
            </div>`;
        });
        html += '</div>';
    } else {
        html += '<div class="empty-state"><h3>No episodes yet</h3></div>';
        if (!Array.isArray(episodes)) console.error('Expected array for episodes, got:', episodes);
    }
    html += '</div>';
    area.innerHTML = html;
}

async function playEpisode(episodeId, episodeTitle, podcastTitle, fileUrl) {
    // Register play in backend
    api('/podcasts/episodes/' + episodeId + '/play', 'POST');

    // Set up as a playable item in the player bar
    state.currentSong = {
        title: episodeTitle || 'Episode',
        artistName: podcastTitle || 'Podcast',
        fileUrl: fileUrl || '',
        durationSeconds: 0
    };
    state.isPlaying = true;
    state.queue = [state.currentSong];
    state.queueIndex = 0;

    // Update player bar UI
    document.getElementById('player-title').textContent = episodeTitle || 'Episode';
    document.getElementById('player-artist').textContent = podcastTitle || 'Podcast';
    document.getElementById('player-thumb').className = 'player-thumb playing';
    document.getElementById('player-thumb').innerHTML = '🎙️';
    document.getElementById('btn-play-pause').textContent = '⏸';

    // Play real audio
    if (fileUrl) {
        state.audio.src = fileUrl;
        state.audio.load();
        state.audio.play().catch(() => {
            showToast('Could not play audio', 'error');
        });
    } else {
        showToast('No audio URL for this episode', 'error');
    }
}

// ==================== USER LIBRARY ====================
async function renderPlaylists() {
    if (!state.userId) return;
    const [myPlaylists, publicPlaylists] = await Promise.all([
        api('/playlists/user/' + state.userId),
        api('/playlists/public')
    ]);
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in">
        <div class="page-header"><div><h2>My Playlists</h2><p class="subtitle">${(myPlaylists || []).length} playlists</p></div>
        <button class="btn btn-primary btn-sm" onclick="showCreatePlaylistModal()">➕ Create Playlist</button></div>`;

    if (myPlaylists && myPlaylists.length > 0) {
        html += '<div class="cards-grid">';
        myPlaylists.forEach(p => {
            const songCount = p.songCount || (p.songs ? p.songs.length : 0);
            html += `<div class="card" onclick="viewPlaylist(${p.playlistId})">
                <div class="card-image" style="background:linear-gradient(135deg, #4f46e5, #818cf8)">📋</div>
                <div class="card-title">${p.name}</div>
                <div class="card-subtitle">${songCount} songs • ${p.privacyStatus || 'PUBLIC'}</div>
            </div>`;
        });
        html += '</div>';
    } else {
        html += '<div class="empty-state"><span class="icon">📋</span><h3>No playlists yet</h3><p>Create a playlist to start organizing your music</p></div>';
    }

    // Public playlists from other users
    const otherPublic = (publicPlaylists || []).filter(p => p.userId != state.userId);
    const followedIds = getFollowedPlaylists();

    // Show followed playlists first
    const followedPlaylists = otherPublic.filter(p => followedIds.includes(p.playlistId));
    if (followedPlaylists.length > 0) {
        html += `<div class="section-header" style="margin-top:28px"><h3>⭐ Following (${followedPlaylists.length})</h3></div>`;
        html += '<div class="cards-grid">';
        followedPlaylists.forEach(p => {
            const songCount = p.songCount || (p.songs ? p.songs.length : 0);
            html += `<div class="card" onclick="viewPlaylist(${p.playlistId})">
                <div class="card-image" style="background:linear-gradient(135deg, #f59e0b, #fbbf24)">⭐</div>
                <div class="card-title">${p.name}</div>
                <div class="card-subtitle">${songCount} songs • by User #${p.userId}</div>
            </div>`;
        });
        html += '</div>';
    }

    // Explore section
    if (otherPublic.length > 0) {
        html += `<div class="section-header" style="margin-top:28px"><h3>🌐 Explore Public Playlists</h3></div>`;
        html += '<div class="cards-grid">';
        otherPublic.forEach(p => {
            const songCount = p.songCount || (p.songs ? p.songs.length : 0);
            const isFollowed = followedIds.includes(p.playlistId);
            html += `<div class="card" style="position:relative" onclick="viewPlaylist(${p.playlistId})">
                <div class="card-image" style="background:linear-gradient(135deg, #0891b2, #67e8f9)">🌐</div>
                <div class="card-title">${p.name}</div>
                <div class="card-subtitle">${songCount} songs</div>
                <div onclick="event.stopPropagation()" style="margin-top:4px">
                    ${isFollowed
                    ? `<button class="btn btn-secondary btn-sm" style="font-size:11px;padding:2px 8px" onclick="unfollowPlaylist(${p.playlistId})">✔ Following</button>`
                    : `<button class="btn btn-primary btn-sm" style="font-size:11px;padding:2px 8px" onclick="followPlaylist(${p.playlistId})">➕ Follow</button>`
                }
                </div>
            </div>`;
        });
        html += '</div>';
    }

    html += '</div>';
    area.innerHTML = html;
}

async function viewPlaylist(playlistId) {
    const [playlist, songs] = await Promise.all([
        api('/playlists/' + playlistId), api('/playlists/' + playlistId + '/songs')
    ]);
    const area = document.getElementById('content-area');
    if (!playlist) return;
    const isOwner = playlist.userId == state.userId;
    const followedIds = getFollowedPlaylists();
    const isFollowed = followedIds.includes(playlist.playlistId);
    area.innerHTML = `<div class="animate-in">
        <div class="page-header"><div><h2>📋 ${playlist.name}</h2>
        <p class="subtitle">${playlist.description || ''} • ${(songs || []).length} songs • ${playlist.privacyStatus || 'PUBLIC'}</p></div>
        <div style="display:flex;gap:8px">
            ${isOwner ? `<button class="btn btn-secondary btn-sm" onclick="showEditPlaylistModal(${playlistId})">✏️ Edit</button>` : ''}
            ${isOwner ? `<button class="btn btn-secondary btn-sm" onclick="deletePlaylist(${playlistId})">🗑️ Delete</button>` : ''}
            ${!isOwner && !isFollowed ? `<button class="btn btn-primary btn-sm" onclick="followPlaylist(${playlistId})">➕ Follow</button>` : ''}
            ${!isOwner && isFollowed ? `<button class="btn btn-secondary btn-sm" onclick="unfollowPlaylist(${playlistId})">✔ Following</button>` : ''}
        </div></div>
        ${renderSongList(songs || [], true, { playlistId: isOwner ? playlistId : null })}
    </div>`;
}

async function deletePlaylist(playlistId) {
    if (!confirm('Delete this playlist?')) return;
    await api('/playlists/' + playlistId, 'DELETE');
    showToast('Playlist deleted');
    navigate('playlists');
}

function showCreatePlaylistModal() {
    openModal('Create Playlist', `
        <form onsubmit="createPlaylist(event)">
            <div class="form-group"><label>Name</label><input type="text" id="pl-name" required placeholder="My Playlist"></div>
            <div class="form-group"><label>Description</label><input type="text" id="pl-desc" placeholder="Optional description"></div>
            <div class="form-group"><label>Privacy</label>
                <select id="pl-privacy"><option value="PUBLIC">Public</option><option value="PRIVATE">Private</option></select></div>
            <button type="submit" class="btn btn-primary">Create</button>
        </form>
    `);
}

async function createPlaylist(e) {
    e.preventDefault();
    const playlist = {
        userId: state.userId,
        name: document.getElementById('pl-name').value,
        description: document.getElementById('pl-desc').value,
        privacyStatus: document.getElementById('pl-privacy').value
    };
    await api('/playlists', 'POST', playlist);
    closeModal();
    showToast('Playlist created!');
    navigate('playlists');
}

async function showAddToPlaylistModal(songId) {
    if (!state.userId) { showToast('Login as user to add to playlist', 'error'); return; }
    const playlists = await api('/playlists/user/' + state.userId);
    let html = '';
    if (playlists && playlists.length > 0) {
        playlists.forEach(p => {
            html += `<div class="song-row" style="grid-template-columns:1fr;cursor:pointer" onclick="addToPlaylist(${p.playlistId}, ${songId})">
                <div class="song-info"><div class="song-thumb" style="background:linear-gradient(135deg,#4f46e5,#818cf8)">📋</div>
                <div class="song-details"><div class="song-title">${p.name}</div></div></div>
            </div>`;
        });
    } else {
        html = '<div class="empty-state"><h3>No playlists</h3><p>Create a playlist first</p></div>';
    }
    openModal('Add to Playlist', html);
}

async function addToPlaylist(playlistId, songId) {
    const data = await api(`/playlists/${playlistId}/songs/${songId}`, 'POST');
    closeModal();
    showToast(data && data.success ? 'Added to playlist!' : 'Already in playlist', data && data.success ? 'success' : 'error');
}

async function renderFavorites() {
    if (!state.userId) return;
    const songs = await api('/favorites/' + state.userId);
    const area = document.getElementById('content-area');
    area.innerHTML = `<div class="animate-in">
        <div class="page-header"><div><h2>❤️ Favorites</h2><p class="subtitle">${(songs || []).length} liked songs</p></div></div>
        ${renderSongList(songs || [])}
    </div>`;
}

async function toggleFavoriteSong(songId) {
    if (!state.userId) { showToast('Login as user to favorite', 'error'); return; }
    const check = await api(`/favorites/${state.userId}/${songId}`);
    if (check && check.isFavorite) {
        const res = await api(`/favorites/${state.userId}/${songId}`, 'DELETE');
        if (res && res.success) {
            showToast('Removed from favorites');
        } else {
            showToast('Failed to remove from favorites', 'error');
        }
    } else {
        const res = await api(`/favorites/${state.userId}/${songId}`, 'POST');
        if (res && res.success) {
            showToast('Added to favorites! ❤️');
        } else {
            showToast('Failed to add to favorites', 'error');
        }
    }
}

async function renderHistory() {
    if (!state.userId) return;
    const history = await api('/history/' + state.userId);
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in">
        <div class="page-header"><div><h2>📜 Listening History</h2><p class="subtitle">${(history || []).length} songs played</p></div>
        <button class="btn btn-secondary btn-sm" onclick="clearHistory()">🗑️ Clear</button></div>`;

    if (history && history.length > 0) {
        // Show last 50 songs played
        const recentHistory = history.slice(0, 50);
        html += '<div class="song-list">';
        recentHistory.forEach((h, i) => {
            const time = h.playedAt ? new Date(h.playedAt).toLocaleString() : '';
            html += `<div class="song-row" style="grid-template-columns:40px 1fr 1fr 80px;cursor:pointer" onclick="playHistorySong(${h.songId})">
                <div><span class="num">${i + 1}</span><span class="play-icon">▶</span></div>
                <div class="song-info"><div class="song-thumb" style="background:linear-gradient(135deg,#7c3aed33,#7c3aed66)">🎵</div>
                <div class="song-details"><div class="song-title">${h.songTitle || 'Song #' + h.songId}</div>
                <div class="song-artist">${h.artistName || ''}</div></div></div>
                <div class="song-album">${time}</div>
                <div class="song-actions" onclick="event.stopPropagation()">
                    <button class="song-action-btn" onclick="showSongDetail(${h.songId})" title="Song Details">ℹ️</button>
                    <button class="song-action-btn" onclick="toggleFavoriteSong(${h.songId})" title="Favorite">🤍</button>
                    <button class="song-action-btn" onclick="showAddToPlaylistModal(${h.songId})" title="Add to Playlist">➕</button>
                </div>
            </div>`;
        });
        html += '</div>';
        if (history.length > 50) {
            html += `<p style="text-align:center;color:var(--text-muted);margin-top:16px">Showing 50 of ${history.length} entries</p>`;
        }
    } else {
        html += '<div class="empty-state"><span class="icon">📜</span><h3>No history yet</h3><p>Start listening to build your history</p></div>';
    }
    html += '</div>';
    area.innerHTML = html;
}

async function clearHistory() {
    if (!confirm('Clear all listening history?')) return;
    await api('/history/' + state.userId, 'DELETE');
    showToast('History cleared');
    renderHistory();
}

// ==================== PROFILE ====================
async function renderProfile() {
    const id = state.userId || state.artistId;
    const isArtist = state.authType === 'artist';
    const url = isArtist ? '/artists/' + id : '/users/' + id;
    const data = await api(url);
    const area = document.getElementById('content-area');
    if (!data) { area.innerHTML = '<div class="empty-state"><h3>Profile not found</h3></div>'; return; }

    let html = `<div class="animate-in profile-container">
        <div class="page-header" style="justify-content: space-between;">
            <div>
                <h2>👤 ${isArtist ? 'Artist' : 'User'} Profile</h2>
                <p class="subtitle">Manage your account and personal details</p>
            </div>
            <div style="display:flex; gap:10px; flex-wrap:wrap;">
                <button class="btn btn-primary" onclick="showEditProfileModal(${JSON.stringify(data).replace(/"/g, '&quot;')})">✏️ Edit Profile</button>
                <button class="btn btn-secondary" onclick="showUpdatePasswordModal()">🔒 Change Password</button>
                ${isArtist ? `<button class="btn btn-secondary" onclick="viewArtist(${data.artistId})">👁️ View as Listener</button>` : ''}
            </div>
        </div>`;

    if (isArtist && data.bannerImage) {
        html += `<div class="profile-banner" style="height: 350px; border-radius: 15px; margin-bottom: -60px; overflow: hidden; position: relative;">
            <img src="${data.bannerImage}" style="width: 100%; height: 100%; object-fit: cover; object-position: center;">
        </div>`;
    }

    html += `<div style="display: flex; gap: 24px; align-items: flex-end; padding: 0 20px; margin-bottom: 24px; position: relative; z-index: 1;">
        <div class="profile-pic-large" style="width: 120px; height: 120px; border-radius: 50%; border: 4px solid var(--bg-primary); overflow: hidden; background: var(--bg-secondary); display: flex; align-items: center; justify-content: center; font-size: 40px;">
            ${data.profilePicture ? `<img src="${data.profilePicture}" style="width: 100%; height: 100%; object-fit: cover;">` : (isArtist ? '🎤' : '👤')}
        </div>
        <div style="padding-bottom: 10px;">
            <h1 style="margin: 0; text-shadow: 0 2px 8px rgba(0,0,0,0.8);">${isArtist ? data.stageName : data.fullName}</h1>
            <p style="color: var(--text-secondary); margin: 4px 0; text-shadow: 0 1px 4px rgba(0,0,0,0.8);">${data.email}</p>
        </div>
    </div>`;

    if (isArtist) {
        const stats = await api('/artists/' + id + '/stats');
        html += `<div class="stats-row">
            <div class="stat-card"><div class="stat-value">${data.genre || '-'}</div><div class="stat-label">Genre</div></div>
            <div class="stat-card"><div class="stat-value">${stats ? stats.songCount : 0}</div><div class="stat-label">Songs</div></div>
            <div class="stat-card"><div class="stat-value">${stats ? stats.totalPlays : 0}</div><div class="stat-label">Total Plays</div></div>
            <div class="stat-card"><div class="stat-value">${stats ? stats.totalFavorites : 0}</div><div class="stat-label">Favorites</div></div>
        </div>
        <div class="profile-section">
            <h3>Bio</h3>
            <p style="color:var(--text-secondary); line-height: 1.6;">${data.bio || 'No bio yet.'}</p>
        </div>`;

        if (data.instagramLink || data.twitterLink || data.youtubeLink || data.spotifyLink || data.websiteLink) {
            html += `<div class="profile-section" style="margin-top: 20px;">
                <h3>Social Media & Links</h3>
                <div style="display:flex; gap:12px; margin-top:10px; flex-wrap: wrap;">
                    ${data.instagramLink ? `<a href="${data.instagramLink}" target="_blank" class="social-link-btn">Instagram</a>` : ''}
                    ${data.twitterLink ? `<a href="${data.twitterLink}" target="_blank" class="social-link-btn">Twitter</a>` : ''}
                    ${data.youtubeLink ? `<a href="${data.youtubeLink}" target="_blank" class="social-link-btn">YouTube</a>` : ''}
                    ${data.spotifyLink ? `<a href="${data.spotifyLink}" target="_blank" class="social-link-btn">Spotify</a>` : ''}
                    ${data.websiteLink ? `<a href="${data.websiteLink}" target="_blank" class="social-link-btn">Website</a>` : ''}
                </div>
            </div>`;
        }
    } else {
        const stats = await api('/users/' + id + '/stats');
        // Calculate listening time from history
        const history = await api('/history/' + id);
        const listeningMinutes = history ? Math.round((history.length * 3.5)) : 0; // ~3.5 min avg per song
        const listeningDisplay = listeningMinutes >= 60 ? `${Math.floor(listeningMinutes / 60)}h ${listeningMinutes % 60}m` : `${listeningMinutes}m`;
        html += `<div class="stats-row">
            <div class="stat-card"><div class="stat-value">${stats ? stats.playlistCount : 0}</div><div class="stat-label">Playlists</div></div>
            <div class="stat-card"><div class="stat-value">${stats ? stats.favoriteCount : 0}</div><div class="stat-label">Favorites</div></div>
            <div class="stat-card"><div class="stat-value">${listeningDisplay}</div><div class="stat-label">Listening Time</div></div>
        </div>
        <div class="profile-section">
            <h3>Bio</h3>
            <p style="color:var(--text-secondary); line-height: 1.6;">${data.bio || 'No bio yet.'}</p>
        </div>
        <div class="profile-section" style="margin-top:16px">
            <h3>Contact</h3>
            <p style="color:var(--text-secondary)">${data.phone ? '📱 ' + data.phone : 'No phone number added'}</p>
            <p style="color:var(--text-secondary)">${data.email ? '📧 ' + data.email : ''}</p>
        </div>`;
    }
    html += '</div>';
    area.innerHTML = html;
}

function showEditProfileModal(data) {
    const isArtist = state.authType === 'artist';
    let body = `
        <form onsubmit="handleUpdateProfile(event)">
            <div class="form-group">
                <label>${isArtist ? 'Stage Name' : 'Full Name'}</label>
                <input type="text" id="edit-name" value="${isArtist ? data.stageName : data.fullName}" required>
            </div>
            ${!isArtist ? `
            <div class="form-group">
                <label>Phone</label>
                <input type="text" id="edit-phone" value="${data.phone || ''}">
            </div>` : `
            <div class="form-group">
                <label>Genre</label>
                <input type="text" id="edit-genre" value="${data.genre || ''}">
            </div>`}
            <div class="form-group">
                <label>Bio</label>
                <textarea id="edit-bio" style="height: 100px;">${data.bio || ''}</textarea>
            </div>
            <div class="form-group">
                <label>Profile Picture</label>
                <input type="file" id="edit-pfp-file" accept="image/*" class="file-input-styled">
                ${data.profilePicture ? `<p class="hint">Current: ${data.profilePicture.split('/').pop()}</p>` : ''}
            </div>
            ${isArtist ? `
            <div class="form-group">
                <label>Banner Image</label>
                <input type="file" id="edit-banner-file" accept="image/*" class="file-input-styled">
                ${data.bannerImage ? `<p class="hint">Current: ${data.bannerImage.split('/').pop()}</p>` : ''}
            </div>
            <fieldset style="border: 1px solid var(--border); padding: 15px; border-radius: 8px; margin-bottom: 15px;">
                <legend style="padding: 0 10px; font-weight: bold;">Social Links</legend>
                <div class="form-group"><label>Instagram</label><input type="text" id="edit-instagram" value="${data.instagramLink || ''}"></div>
                <div class="form-group"><label>Twitter</label><input type="text" id="edit-twitter" value="${data.twitterLink || ''}"></div>
                <div class="form-group"><label>YouTube</label><input type="text" id="edit-youtube" value="${data.youtubeLink || ''}"></div>
                <div class="form-group"><label>Spotify</label><input type="text" id="edit-spotify" value="${data.spotifyLink || ''}"></div>
                <div class="form-group"><label>Website</label><input type="text" id="edit-website" value="${data.websiteLink || ''}"></div>
            </fieldset>` : ''}
            <button type="submit" class="btn btn-primary" id="update-profile-btn">Save Changes</button>
        </form>
    `;
    openModal('Edit Profile', body);
}

async function handleUpdateProfile(e) {
    e.preventDefault();
    const btn = document.getElementById('update-profile-btn');
    btn.disabled = true;
    btn.textContent = 'Updating...';

    const isArtist = state.authType === 'artist';
    const id = isArtist ? state.artistId : state.userId;
    const url = isArtist ? '/artists/' + id : '/users/' + id;

    // Handle Image Uploads First
    const pfpFile = document.getElementById('edit-pfp-file');
    let pfpUrl = null;
    if (pfpFile.files.length > 0) {
        pfpUrl = await uploadImageFile(pfpFile);
    }

    let bannerUrl = null;
    if (isArtist) {
        const bannerFile = document.getElementById('edit-banner-file');
        if (bannerFile.files.length > 0) {
            bannerUrl = await uploadImageFile(bannerFile);
        }
    }

    const payload = {};
    if (isArtist) {
        payload.stageName = document.getElementById('edit-name').value;
        payload.genre = document.getElementById('edit-genre').value;
        payload.bio = document.getElementById('edit-bio').value;
        payload.instagramLink = document.getElementById('edit-instagram').value;
        payload.twitterLink = document.getElementById('edit-twitter').value;
        payload.youtubeLink = document.getElementById('edit-youtube').value;
        payload.spotifyLink = document.getElementById('edit-spotify').value;
        payload.websiteLink = document.getElementById('edit-website').value;
        if (pfpUrl) payload.profilePicture = pfpUrl;
        if (bannerUrl) payload.bannerImage = bannerUrl;
    } else {
        payload.fullName = document.getElementById('edit-name').value;
        payload.phone = document.getElementById('edit-phone').value;
        payload.bio = document.getElementById('edit-bio').value;
        if (pfpUrl) payload.profilePicture = pfpUrl;
    }

    const res = await api(url, 'PUT', payload);
    if (res && res.success) {
        showToast('Profile updated successfully!');
        if (payload.fullName || payload.stageName) {
            const newName = isArtist ? payload.stageName : payload.fullName;
            state.userName = newName;
            localStorage.setItem('userName', newName);
            const nameEl = document.getElementById('user-display-name');
            if (nameEl) nameEl.textContent = newName;
        }
        closeModal();
        renderProfile();
    } else {
        showToast('Failed to update profile', 'error');
        btn.disabled = false;
        btn.textContent = 'Save Changes';
    }
}

function showUpdatePasswordModal() {
    openModal('Change Password', `
        <form onsubmit="updatePassword(event)">
            <div class="form-group"><label>Current Password</label><input type="password" id="old-pass" required></div>
            <div class="form-group"><label>New Password</label><input type="password" id="new-pass" required></div>
            <button type="submit" class="btn btn-primary">Update Password</button>
        </form>
    `);
}

async function updatePassword(e) {
    e.preventDefault();
    const endpoint = state.authType === 'artist'
        ? `/artists/${state.artistId}/password`
        : `/users/${state.userId}/password`;

    const data = await api(endpoint, 'PUT', {
        oldPassword: document.getElementById('old-pass').value,
        newPassword: document.getElementById('new-pass').value
    });
    closeModal();
    showToast(data && data.success ? 'Password updated!' : 'Old password incorrect', data && data.success ? 'success' : 'error');
}

async function renderUploadSong() {
    const [genres, albums] = await Promise.all([api('/genres'), api('/albums/artist/' + state.artistId)]);
    const area = document.getElementById('content-area');

    // Filter out test genres (those with UUIDs), then add all clean genres
    let cleanGenres = genres ? genres.filter(g => !g.genreName.includes('-') || g.genreName.length < 20) : [];
    if (cleanGenres.length === 0) cleanGenres = genres || [];

    let genreOpts = cleanGenres.map(g => `<option value="${g.genreId}">${g.genreName}</option>`).join('');
    genreOpts += '<option value="__new__">➕ Create New Genre</option>';

    let albumOpts = '<option value="">No Album (Single)</option>';
    if (albums) albumOpts += albums.map(a => `<option value="${a.albumId}">${a.title}</option>`).join('');

    area.innerHTML = `<div class="animate-in">
        <div class="page-header"><h2>⬆️ Upload Song</h2></div>
        <div style="max-width:500px">
        <form onsubmit="uploadSong(event)">
            <div class="form-group"><label>Title</label><input type="text" id="song-title" required placeholder="Song title"></div>
            <div class="form-group"><label>Genre</label>
                <select id="song-genre" onchange="toggleNewGenreInput()">${genreOpts}</select></div>
            <div class="form-group" id="new-genre-group" style="display:none">
                <label>New Genre Name</label>
                <input type="text" id="new-genre-name" placeholder="e.g. Pop, Classical, EDM"></div>
            <div class="form-group"><label>Album</label>
                <select id="song-album">${albumOpts}</select></div>
            <div class="form-group"><label>Release Date</label>
                <input type="date" id="song-release" required></div>
            <div class="form-group"><label>🖼️ Cover Image (optional)</label>
                <input type="file" id="song-cover-file" accept="image/*"
                       style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)">
                <div id="cover-preview" style="margin-top:8px;color:var(--text-secondary);font-size:13px"></div></div>
            <div class="form-group"><label>🎵 Select Audio File</label>
                <input type="file" id="song-file" accept="audio/*" required
                       style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)"
                       onchange="previewAudioFile(this)">
                <div id="audio-preview" style="margin-top:8px;color:var(--text-secondary);font-size:13px"></div></div>
            <button type="submit" class="btn btn-primary" id="upload-btn">Upload Song</button>
        </form></div></div>`;
}

function previewAudioFile(input) {
    const preview = document.getElementById('audio-preview');
    if (input.files && input.files[0]) {
        const file = input.files[0];
        const sizeMB = (file.size / (1024 * 1024)).toFixed(1);
        preview.innerHTML = `📁 <strong>${file.name}</strong> (${sizeMB} MB)`;

        // Auto-detect duration using HTML5 Audio
        const tempAudio = new Audio();
        tempAudio.src = URL.createObjectURL(file);
        tempAudio.onloadedmetadata = () => {
            const dur = Math.round(tempAudio.duration);
            preview.innerHTML += ` — Duration: ${formatDuration(dur)}`;
            // Store duration for use in upload
            input.dataset.duration = dur;
            URL.revokeObjectURL(tempAudio.src);
        };
    }
}

function toggleNewGenreInput() {
    const select = document.getElementById('song-genre');
    const group = document.getElementById('new-genre-group');
    group.style.display = select.value === '__new__' ? 'block' : 'none';
}

async function uploadImageFile(inputElement) {
    if (!inputElement || !inputElement.files || !inputElement.files[0]) return '';
    const formData = new FormData();
    formData.append('file', inputElement.files[0]);
    try {
        const token = localStorage.getItem('token');
        const headers = {};
        if (token) headers['Authorization'] = 'Bearer ' + token;

        const res = await fetch('/api/upload/image', { method: 'POST', body: formData, headers: headers });
        const data = await res.json();
        return data.success ? data.fileUrl : '';
    } catch (err) {
        console.error("Image upload failed", err);
        return '';
    }
}

async function uploadSong(e) {
    e.preventDefault();
    const uploadBtn = document.getElementById('upload-btn');
    const fileInput = document.getElementById('song-file');

    if (!fileInput.files || !fileInput.files[0]) {
        showToast('Please select an audio file', 'error');
        return;
    }

    let genreId = document.getElementById('song-genre').value;

    // Create new genre if selected
    if (genreId === '__new__') {
        const newName = document.getElementById('new-genre-name').value.trim();
        if (!newName) { showToast('Enter a genre name', 'error'); return; }
        const genreData = await api('/genres', 'POST', { genreName: newName });
        if (genreData && genreData.genreId) {
            genreId = genreData.genreId;
        } else {
            showToast('Failed to create genre', 'error'); return;
        }
    }

    // Step 1: Upload the audio file
    uploadBtn.textContent = 'Uploading file...';
    uploadBtn.disabled = true;
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    let fileUrl = '';
    try {
        const token = localStorage.getItem('token');
        const headers = {};
        if (token) headers['Authorization'] = 'Bearer ' + token;

        const uploadRes = await fetch('/api/upload/audio', { method: 'POST', body: formData, headers: headers });
        const uploadData = await uploadRes.json();
        if (uploadData.success) {
            fileUrl = uploadData.fileUrl;
        } else {
            showToast('File upload failed: ' + (uploadData.message || ''), 'error');
            uploadBtn.textContent = 'Upload Song';
            uploadBtn.disabled = false;
            return;
        }
    } catch (err) {
        showToast('File upload failed', 'error');
        uploadBtn.textContent = 'Upload Song';
        uploadBtn.disabled = false;
        return;
    }

    // Step 2: Upload cover image if provided
    uploadBtn.textContent = 'Uploading cover image...';
    const coverImageUrl = await uploadImageFile(document.getElementById('song-cover-file'));

    // Step 3: Create the song record with the uploaded file URL
    uploadBtn.textContent = 'Saving song...';
    const duration = parseInt(fileInput.dataset.duration) || 0;
    const song = {
        title: document.getElementById('song-title').value,
        artistId: state.artistId,
        genreId: parseInt(genreId),
        albumId: document.getElementById('song-album').value ? parseInt(document.getElementById('song-album').value) : null,
        durationSeconds: duration,
        releaseDate: document.getElementById('song-release').value,
        fileUrl: fileUrl,
        coverImageUrl: coverImageUrl
    };
    const data = await api('/songs', 'POST', song);
    if (data && data.success) {
        showToast('Song uploaded! 🎵');
        // Fetch the newly added songs and auto-play the latest
        const mySongs = await api('/songs/artist/' + state.artistId);
        if (mySongs && mySongs.length > 0) {
            playSongFromList(mySongs, 0);
        }
        navigate('my-songs');
    } else {
        showToast('Upload failed', 'error');
        uploadBtn.textContent = 'Upload Song';
        uploadBtn.disabled = false;
    }
}

async function renderMySongs() {
    const songs = await api('/songs/artist/' + state.artistId);
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in">
        <div class="page-header" style="justify-content: flex-start; gap: 24px;">
            <div style="min-width: 120px;">
                <h2 style="font-size: 28px; line-height: 1.2; margin: 0;">🎵 My<br>Songs</h2>
                <p class="subtitle" style="margin-top: 4px;">${(songs || []).length} tracks</p>
            </div>
            <button class="btn btn-primary" style="flex: 1; padding: 12px; border-radius: 8px; font-weight: bold; font-size: 14px;" onclick="navigate('upload')">⬆️ Upload</button>
        </div>`;

    if (songs && songs.length > 0) {
        html += '<div class="song-list">';
        songs.forEach((song, i) => {
            const isPublic = song.isActive !== 'N';
            const visBadge = isPublic
                ? '<span style="background:#22c55e33;color:#22c55e;padding:2px 8px;border-radius:6px;font-size:11px">Public</span>'
                : '<span style="background:#ef444433;color:#ef4444;padding:2px 8px;border-radius:6px;font-size:11px">Unlisted</span>';
            html += `<div class="song-row" onclick="playSongFromList(${JSON.stringify(songs).replace(/"/g, '&quot;')}, ${i})">
                <div><span class="num">${i + 1}</span><span class="play-icon">▶</span></div>
                <div class="song-info">
                    ${songThumbHtml(song)}
                    <div class="song-details">
                        <div class="song-title">${song.title || 'Unknown'} ${visBadge}</div>
                        <div class="song-artist">${song.genreName || '-'} • ${song.playCount || 0} plays</div>
                    </div>
                </div>
                <div class="song-album">${song.albumTitle || 'Single'}</div>
                <div class="song-actions" onclick="event.stopPropagation()">
                    <button class="song-action-btn" onclick="showEditSongModal(${song.songId})" title="Edit Song">✏️</button>
                    <button class="song-action-btn" onclick="toggleSongVisibility(${song.songId}, '${song.isActive || 'Y'}')" title="Toggle Visibility">${isPublic ? '👁️' : '🙈'}</button>
                    <button class="song-action-btn" onclick="deleteSong(${song.songId})" style="color:var(--text-muted)" title="Delete">🗑️</button>
                </div>
            </div>`;
        });
        html += '</div>';
    } else {
        html += '<div class="empty-state"><span class="icon">🎵</span><h3>No songs yet</h3><p>Upload your first song!</p></div>';
    }
    html += '</div>';
    area.innerHTML = html;
}

async function renderMyAlbums() {
    const albums = await api('/albums/artist/' + state.artistId);
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in">
        <div class="page-header" style="justify-content: flex-start; gap: 24px;">
            <div style="min-width: 120px;">
                <h2 style="font-size: 28px; line-height: 1.2; margin: 0;">💿 My<br>Albums</h2>
            </div>
            <button class="btn btn-primary" style="flex: 1; padding: 12px; border-radius: 8px; font-weight: bold; font-size: 14px;" onclick="showCreateAlbumModal()">➕ New Album</button>
        </div>`;

    if (albums && albums.length > 0) {
        html += '<div class="cards-grid">';
        albums.forEach(a => {
            html += `<div class="card" onclick="viewAlbum(${a.albumId})">
                ${cardImageSimple(a.coverImageUrl, '💿', 'background:linear-gradient(135deg,#dbeafe,#c7d2fe)')}
                <div class="card-title">${a.title}</div>
                <div class="card-subtitle">${a.releaseDate || ''}</div>
                <div style="display:flex;gap:4px;margin-top:8px" onclick="event.stopPropagation()">
                    <button class="btn btn-secondary btn-xs" onclick="showEditAlbumModal(${a.albumId})">✏️ Edit</button>
                    <button class="btn btn-secondary btn-xs" onclick="deleteAlbum(${a.albumId})">🗑️ Delete</button>
                </div>
            </div>`;
        });
        html += '</div>';
    } else {
        html += '<div class="empty-state"><span class="icon">💿</span><h3>No albums yet</h3></div>';
    }
    html += '</div>';
    area.innerHTML = html;
}

function showCreateAlbumModal() {
    openModal('Create Album', `
        <form onsubmit="createAlbum(event)">
            <div class="form-group"><label>Title</label><input type="text" id="album-title" required></div>
            <div class="form-group"><label>Release Date</label><input type="date" id="album-date" required></div>
            <div class="form-group"><label>Description</label><textarea id="album-desc" placeholder="Album description"></textarea></div>
            <div class="form-group"><label>🖼️ Cover Image (optional)</label>
                <input type="file" id="album-cover-file" accept="image/*"
                       style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)"></div>
            <button type="submit" class="btn btn-primary" id="album-submit-btn">Create Album</button>
        </form>
    `);
}

async function createAlbum(e) {
    e.preventDefault();
    const submitBtn = document.getElementById('album-submit-btn');
    submitBtn.textContent = 'Uploading...';
    submitBtn.disabled = true;
    const coverImageUrl = await uploadImageFile(document.getElementById('album-cover-file'));
    const album = {
        artistId: parseInt(state.artistId),
        title: document.getElementById('album-title').value,
        releaseDate: document.getElementById('album-date').value,
        description: document.getElementById('album-desc').value,
        coverImageUrl: coverImageUrl || ''
    };
    await api('/albums', 'POST', album);
    closeModal();
    showToast('Album created! 💿');
    navigate('my-albums');
}

async function renderMyPodcasts() {
    const podcasts = await api('/podcasts/artist/' + state.artistId);
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in">
        <div class="page-header" style="justify-content: flex-start; gap: 24px;">
            <div style="min-width: 120px;">
                <h2 style="font-size: 28px; line-height: 1.2; margin: 0;">🎙️ Podcasts</h2>
            </div>
            <button class="btn btn-primary" style="flex: 1; padding: 12px; border-radius: 8px; font-weight: bold; font-size: 14px;" onclick="showCreatePodcastModal()">➕ New Podcast</button>
        </div>`;

    if (Array.isArray(podcasts)) {
        if (podcasts.length > 0) {
            html += '<div class="cards-grid">';
            podcasts.forEach(p => {
                html += `<div class="card" onclick="viewPodcast(${p.podcastId})">
                    ${cardImageSimple(p.coverImageUrl, '🎙️', 'background:linear-gradient(135deg,#0d9488,#5eead4)')}
                    <div class="card-title">${p.title}</div>
                    <div class="card-subtitle">${p.hostName || ''}</div>
                </div>`;
            });
            html += '</div>';
        } else {
            html += '<div class="empty-state"><span class="icon">🎙️</span><h3>No podcasts yet</h3></div>';
        }
    } else {
        html += '<div class="empty-state"><span class="icon">❌</span><h3>Failed to load your podcasts</h3></div>';
        console.error('Expected array for my-podcasts, but got:', podcasts);
    }

    html += '</div>';
    area.innerHTML = html;
}

function showCreatePodcastModal() {
    openModal('Create Podcast', `
        <form onsubmit="createPodcast(event)">
            <div class="form-group"><label>Title</label><input type="text" id="pod-title" required></div>
            <div class="form-group"><label>Host Name</label><input type="text" id="pod-host" required></div>
            <div class="form-group"><label>Category</label><input type="text" id="pod-category" placeholder="e.g. Technology, Music"></div>
            <div class="form-group"><label>Description</label><textarea id="pod-desc"></textarea></div>
            <div class="form-group"><label>🖼️ Cover Image (optional)</label>
                <input type="file" id="pod-cover-file" accept="image/*"
                       style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)"></div>
            <button type="submit" class="btn btn-primary" id="pod-submit-btn">Create Podcast</button>
        </form>
    `);
}

async function createPodcast(e) {
    e.preventDefault();
    if (!state.artistId) {
        showToast('Session error: Artist ID missing. Please re-login.', 'error');
        return;
    }
    const submitBtn = document.getElementById('pod-submit-btn');
    submitBtn.textContent = 'Uploading...';
    submitBtn.disabled = true;
    const coverImageUrl = await uploadImageFile(document.getElementById('pod-cover-file'));
    const podcast = {
        title: document.getElementById('pod-title').value,
        artistId: parseInt(state.artistId),
        hostName: document.getElementById('pod-host').value,
        category: document.getElementById('pod-category').value,
        description: document.getElementById('pod-desc').value,
        coverImageUrl: coverImageUrl
    };
    const res = await api('/podcasts', 'POST', podcast);
    if (res && res.success) {
        showToast('Podcast created! 🎙️');
        closeModal();
        navigate('my-podcasts');
    } else {
        showToast('Failed to create podcast', 'error');
        submitBtn.textContent = 'Create Podcast';
        submitBtn.disabled = false;
    }
}

function showAddEpisodeModal(podcastId) {
    openModal('Add Episode', `
        <form onsubmit="addEpisode(event, ${podcastId})">
            <div class="form-group"><label>Title</label><input type="text" id="ep-title" required></div>
            <div class="form-group"><label>Release Date</label><input type="date" id="ep-date" required></div>
            <div class="form-group"><label>🎵 Select Audio File</label>
                <input type="file" id="ep-file" accept="audio/*" required
                       style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)"
                       onchange="previewAudioFile(this)">
                <div id="audio-preview" style="margin-top:8px;color:var(--text-secondary);font-size:13px"></div></div>
            <div class="form-group"><label>🖼️ Select Cover Image (Optional)</label>
                <input type="file" id="ep-cover" accept="image/*"
                       style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)">
            </div>
            <button type="submit" class="btn btn-primary" id="ep-submit-btn">Upload Episode</button>
        </form>
    `);
}

async function addEpisode(e, podcastId) {
    e.preventDefault();
    const submitBtn = document.getElementById('ep-submit-btn');
    const fileInput = document.getElementById('ep-file');

    submitBtn.textContent = 'Uploading files...';
    submitBtn.disabled = true;

    let fileUrl = '';
    const audioFile = fileInput.files[0];
    if (audioFile) {
        const formData = new FormData();
        formData.append('file', audioFile);
        try {
            const token = localStorage.getItem('token');
            const headers = {};
            if (token) headers['Authorization'] = 'Bearer ' + token;
            const uploadRes = await fetch('/api/upload/audio', { method: 'POST', body: formData, headers: headers });
            const uploadData = await uploadRes.json();
            if (uploadData.success) fileUrl = uploadData.fileUrl;
        } catch (err) { console.error('Audio upload failed', err); }
    }

    let coverUrl = '';
    const coverInput = document.getElementById('ep-cover');
    if (coverInput && coverInput.files[0]) {
        coverUrl = await uploadImageFile(coverInput);
    }

    submitBtn.textContent = 'Saving episode...';
    const duration = parseInt(fileInput.dataset.duration) || 0;
    const ep = {
        title: document.getElementById('ep-title').value,
        releaseDate: document.getElementById('ep-date').value,
        durationSeconds: duration,
        fileUrl: fileUrl,
        coverImageUrl: coverUrl
    };

    const res = await api('/podcasts/' + podcastId + '/episodes?artistId=' + state.artistId, 'POST', ep);
    if (res && res.success) {
        showToast('Episode uploaded! 🎙️');
        closeModal();
        viewPodcast(podcastId);
    } else {
        showToast('Failed to upload episode', 'error');
        submitBtn.textContent = 'Upload Episode';
        submitBtn.disabled = false;
    }
}

async function renderArtistStats() {
    const [stats, popularSongs, favorites, topListeners] = await Promise.all([
        api('/artists/' + state.artistId + '/stats'),
        api('/artists/' + state.artistId + '/songs-by-popularity'),
        api('/artists/' + state.artistId + '/favorites'),
        api('/artists/' + state.artistId + '/top-listeners')
    ]);
    const area = document.getElementById('content-area');
    let html = `<div class="animate-in">
        <div class="page-header"><h2>📊 Analytics & Insights</h2><p class="subtitle">Track your performance and audience</p></div>
        <div class="stats-row">
            <div class="stat-card"><div class="stat-value">${stats ? stats.songCount : 0}</div><div class="stat-label">Total Songs</div></div>
            <div class="stat-card"><div class="stat-value">${stats ? stats.totalPlays : 0}</div><div class="stat-label">Total Plays</div></div>
            <div class="stat-card"><div class="stat-value">${stats ? stats.totalFavorites : 0}</div><div class="stat-label">Total Favorites</div></div>
        </div>`;

    // Songs by Popularity
    if (popularSongs && popularSongs.length > 0) {
        html += `<div class="section-header" style="margin-top:28px"><h3>🔥 Songs by Popularity</h3></div>`;
        html += '<div class="song-list">';
        popularSongs.forEach((song, i) => {
            html += `<div class="song-row" style="grid-template-columns:40px 1fr 100px 100px 60px" onclick="playSongFromList(${JSON.stringify(popularSongs).replace(/"/g, '&quot;')}, ${i})">
                <div><span class="num" style="${i < 3 ? 'color:var(--accent-light);font-weight:bold' : ''}">${i + 1}</span></div>
                <div class="song-info">
                    ${songThumbHtml(song)}
                    <div class="song-details">
                        <div class="song-title">${song.title || 'Unknown'}</div>
                        <div class="song-artist">${song.genreName || '-'}</div>
                    </div>
                </div>
                <div style="color:var(--accent-light);font-weight:600;text-align:center">${song.playCount || 0} plays</div>
                <div style="text-align:center;color:var(--text-secondary)">${song.albumTitle || 'Single'}</div>
                <div style="text-align:center;color:var(--text-muted)">${formatDuration(song.durationSeconds)}</div>
            </div>`;
        });
        html += '</div>';
    }

    // Users who favorited songs
    if (favorites && favorites.length > 0) {
        html += `<div class="section-header" style="margin-top:28px"><h3>❤️ Users Who Favorited Your Songs (${favorites.length})</h3></div>`;
        html += '<div style="background:var(--bg-secondary);border-radius:12px;padding:16px;max-height:300px;overflow-y:auto">';
        html += '<table style="width:100%;border-collapse:collapse">';
        html += '<thead><tr style="color:var(--text-muted);font-size:12px;text-transform:uppercase;border-bottom:1px solid var(--border)">';
        html += '<th style="text-align:left;padding:8px">User</th><th style="text-align:left;padding:8px">Song</th><th style="text-align:left;padding:8px">Date</th></tr></thead><tbody>';
        favorites.slice(0, 50).forEach(f => {
            const date = f.favoritedAt ? new Date(f.favoritedAt).toLocaleDateString() : '';
            html += `<tr style="border-bottom:1px solid var(--border)">
                <td style="padding:10px 8px;color:var(--text-primary)">${f.userName || 'User #' + f.userId}</td>
                <td style="padding:10px 8px;color:var(--text-secondary)">${f.songTitle || 'Song #' + f.songId}</td>
                <td style="padding:10px 8px;color:var(--text-muted);font-size:13px">${date}</td>
            </tr>`;
        });
        html += '</tbody></table></div>';
    } else {
        html += '<div class="section-header" style="margin-top:28px"><h3>❤️ Users Who Favorited Your Songs</h3></div>';
        html += '<div style="color:var(--text-muted);padding:16px">No favorites yet.</div>';
    }

    // Top Listeners
    if (topListeners && topListeners.length > 0) {
        html += `<div class="section-header" style="margin-top:28px"><h3>🎧 Top Listeners (${topListeners.length})</h3></div>`;
        html += '<div style="background:var(--bg-secondary);border-radius:12px;padding:16px;max-height:300px;overflow-y:auto">';
        html += '<table style="width:100%;border-collapse:collapse">';
        html += '<thead><tr style="color:var(--text-muted);font-size:12px;text-transform:uppercase;border-bottom:1px solid var(--border)">';
        html += '<th style="text-align:left;padding:8px">#</th><th style="text-align:left;padding:8px">Listener</th><th style="text-align:right;padding:8px">Plays</th></tr></thead><tbody>';
        topListeners.slice(0, 20).forEach((l, i) => {
            html += `<tr style="border-bottom:1px solid var(--border)">
                <td style="padding:10px 8px;color:${i < 3 ? 'var(--accent-light)' : 'var(--text-muted)'};font-weight:${i < 3 ? 'bold' : 'normal'}">${i + 1}</td>
                <td style="padding:10px 8px;color:var(--text-primary)">${l.userName || 'User #' + l.userId}</td>
                <td style="padding:10px 8px;text-align:right;color:var(--accent-light);font-weight:600">${l.playCount} plays</td>
            </tr>`;
        });
        html += '</tbody></table></div>';
    } else {
        html += '<div class="section-header" style="margin-top:28px"><h3>🎧 Top Listeners</h3></div>';
        html += '<div style="color:var(--text-muted);padding:16px">No listening data yet.</div>';
    }

    // Listening Trends
    html += `<div class="section-header" style="margin-top:28px"><h3>📈 Listening Trends</h3></div>`;
    if (stats && stats.totalPlays > 0) {
        const avgDaily = Math.round((stats.totalPlays || 0) / 30);
        const avgWeekly = Math.round((stats.totalPlays || 0) / 4);
        html += `<div class="stats-row">
            <div class="stat-card"><div class="stat-value">${avgDaily}</div><div class="stat-label">Avg Daily Plays</div></div>
            <div class="stat-card"><div class="stat-value">${avgWeekly}</div><div class="stat-label">Avg Weekly Plays</div></div>
            <div class="stat-card"><div class="stat-value">${stats.totalPlays || 0}</div><div class="stat-label">Monthly Plays</div></div>
        </div>`;
    } else {
        html += '<div style="color:var(--text-muted);padding:16px">No play data available yet. Once listeners start playing your songs, trends will appear here.</div>';
    }

    html += '</div>';
    area.innerHTML = html;
}

// ==================== MUSIC PLAYER ====================
function playSongFromList(songs, index) {
    state.queue = songs;
    state.queueIndex = index;
    playCurrent();
}

function playCurrent() {
    if (state.queueIndex < 0 || state.queueIndex >= state.queue.length) return;
    const song = state.queue[state.queueIndex];
    state.currentSong = song;
    state.isPlaying = true;

    // Update player bar UI
    document.getElementById('player-title').textContent = song.title || 'Unknown';
    document.getElementById('player-artist').textContent = song.artistName || 'Unknown Artist';
    document.getElementById('player-thumb').className = 'player-thumb playing';
    document.getElementById('btn-play-pause').textContent = '⏸';
    document.getElementById('progress-fill').style.width = '0%';
    document.getElementById('current-time').textContent = '0:00';
    document.getElementById('total-time').textContent = formatDuration(song.durationSeconds || 0);

    // Set cover image if available
    const thumb = document.getElementById('player-thumb');
    if (song.coverImageUrl) {
        thumb.innerHTML = `<img src="${song.coverImageUrl}" alt="cover" style="width:100%;height:100%;object-fit:cover;border-radius:inherit">`;
    } else {
        thumb.innerHTML = '🎵';
    }

    // Register play in backend
    if (song.songId) {
        api(`/songs/${song.songId}/play?userId=${state.userId || ''}`, 'POST');
    }

    // Play real audio
    const fileUrl = song.fileUrl || song.preview || '';
    if (fileUrl) {
        state.audio.src = fileUrl;
        state.audio.load();
        const playPromise = state.audio.play();
        if (playPromise) {
            playPromise.catch(err => {
                console.warn('Playback failed:', err.message);
                showToast('Could not play audio — browser blocked autoplay. Click play.', 'error');
                state.isPlaying = false;
                document.getElementById('btn-play-pause').textContent = '▶';
            });
        }
    } else {
        showToast('No audio URL for this song', 'error');
        state.audio.pause();
    }
}

function togglePlayPause() {
    if (!state.currentSong) return;
    if (state.isPlaying) {
        state.audio.pause();
        state.isPlaying = false;
    } else {
        state.audio.play().catch(() => { });
        state.isPlaying = true;
    }
    document.getElementById('btn-play-pause').textContent = state.isPlaying ? '⏸' : '▶';
    document.getElementById('player-thumb').className = state.isPlaying ? 'player-thumb playing' : 'player-thumb';
}

function playNext() {
    if (state.queue.length === 0) return;
    if (state.shuffle) {
        state.queueIndex = Math.floor(Math.random() * state.queue.length);
    } else {
        state.queueIndex = (state.queueIndex + 1) % state.queue.length;
    }
    playCurrent();
}

function playPrev() {
    if (state.queue.length === 0) return;
    // If we're more than 3 seconds in, restart the current song
    if (state.audio.currentTime > 3) {
        state.audio.currentTime = 0;
        return;
    }
    state.queueIndex = state.queueIndex > 0 ? state.queueIndex - 1 : state.queue.length - 1;
    playCurrent();
}

function toggleShuffle() {
    state.shuffle = !state.shuffle;
    document.getElementById('btn-shuffle').classList.toggle('active', state.shuffle);
    showToast(state.shuffle ? 'Shuffle on' : 'Shuffle off');
}

function toggleRepeat() {
    const btn = document.getElementById('btn-repeat');
    if (state.repeat === 'off') {
        state.repeat = 'all';
        btn.textContent = '🔁';
        btn.classList.add('active');
        btn.title = 'Repeat All';
        showToast('Repeat All');
    } else if (state.repeat === 'all') {
        state.repeat = 'one';
        btn.textContent = '🔂';
        btn.classList.add('active');
        btn.title = 'Repeat One';
        showToast('Repeat One');
    } else {
        state.repeat = 'off';
        btn.textContent = '🔁';
        btn.classList.remove('active');
        btn.title = 'Repeat Off';
        showToast('Repeat Off');
    }
}

function seekTo(e) {
    if (!state.currentSong) return;
    const bar = document.getElementById('progress-bar');
    const pct = e.offsetX / bar.offsetWidth;
    const duration = state.audio.duration || state.currentSong.durationSeconds || 1;
    state.audio.currentTime = pct * duration;
}

function setVolume(e) {
    const bar = e.currentTarget;
    const pct = e.offsetX / bar.offsetWidth;
    state.volume = Math.max(0, Math.min(1, pct));
    state.audio.volume = state.volume;
    document.getElementById('volume-fill').style.width = (state.volume * 100) + '%';
}

async function togglePlayerFavorite() {
    if (!state.currentSong || !state.userId) return;
    await toggleFavoriteSong(state.currentSong.songId);
}

// ==================== MODAL ====================
function openModal(title, bodyHtml) {
    document.getElementById('modal-title').textContent = title;
    document.getElementById('modal-body').innerHTML = bodyHtml;
    document.getElementById('modal-overlay').classList.add('active');
}

function closeModal(e) {
    if (e && e.target !== document.getElementById('modal-overlay')) return;
    document.getElementById('modal-overlay').classList.remove('active');
}

async function deleteSong(songId) {
    if (!confirm('Delete this song permanently?')) return;
    const res = await api(`/songs/${songId}?artistId=${state.artistId}`, 'DELETE');
    if (res && res.success) {
        showToast('Song deleted');
        navigate('my-songs');
    } else {
        showToast('Failed to delete song', 'error');
    }
}

async function deleteAlbum(albumId) {
    if (!confirm('Delete this album permanently?')) return;
    const res = await api(`/albums/${albumId}?artistId=${state.artistId}`, 'DELETE');
    if (res && res.success) {
        showToast('Album deleted');
        navigate('my-albums');
    } else {
        showToast('Failed to delete album', 'error');
    }
}

async function deletePodcast(podcastId) {
    if (!confirm('Delete this podcast and all episodes permanently?')) return;
    const res = await api(`/podcasts/${podcastId}?artistId=${state.artistId}`, 'DELETE');
    if (res && res.success) {
        showToast('Podcast deleted');
        navigate('my-podcasts');
    } else {
        showToast('Failed to delete podcast', 'error');
    }
}

async function deleteEpisode(episodeId, podcastId) {
    if (!confirm('Delete this episode permanently?')) return;
    const res = await api(`/podcasts/episodes/${episodeId}?artistId=${state.artistId}`, 'DELETE');
    if (res && res.success) {
        showToast('Episode deleted');
        viewPodcast(podcastId);
    } else {
        showToast('Failed to delete episode', 'error');
    }
}

// ==================== SONG DETAIL MODAL ====================
async function showSongDetail(songId) {
    const song = await api('/songs/' + songId);
    if (!song) { showToast('Song not found', 'error'); return; }
    const releaseDate = song.releaseDate || 'N/A';
    const body = `
        <div style="display:flex;gap:20px;align-items:flex-start">
            <div style="width:120px;height:120px;border-radius:12px;overflow:hidden;flex-shrink:0;background:linear-gradient(135deg,#7c3aed33,#7c3aed66);display:flex;align-items:center;justify-content:center;font-size:36px">
                ${song.coverImageUrl ? `<img src="${song.coverImageUrl}" style="width:100%;height:100%;object-fit:cover">` : '🎵'}
            </div>
            <div style="flex:1">
                <h2 style="margin:0 0 4px">${song.title || 'Unknown'}</h2>
                <p style="color:var(--text-secondary);margin:0 0 16px">${song.artistName || 'Unknown Artist'}</p>
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
                    <div>
                        <div style="color:var(--text-muted);font-size:12px;text-transform:uppercase;margin-bottom:2px">Album</div>
                        <div>${song.albumTitle || 'Single'}</div>
                    </div>
                    <div>
                        <div style="color:var(--text-muted);font-size:12px;text-transform:uppercase;margin-bottom:2px">Genre</div>
                        <div>${song.genreName || '-'}</div>
                    </div>
                    <div>
                        <div style="color:var(--text-muted);font-size:12px;text-transform:uppercase;margin-bottom:2px">Duration</div>
                        <div>${formatDuration(song.durationSeconds)}</div>
                    </div>
                    <div>
                        <div style="color:var(--text-muted);font-size:12px;text-transform:uppercase;margin-bottom:2px">Release Date</div>
                        <div>${releaseDate}</div>
                    </div>
                    <div>
                        <div style="color:var(--text-muted);font-size:12px;text-transform:uppercase;margin-bottom:2px">Play Count</div>
                        <div>${song.playCount || 0} plays</div>
                    </div>
                </div>
            </div>
        </div>
        <div style="display:flex;gap:8px;margin-top:20px">
            <button class="btn btn-primary btn-sm" onclick="playSongFromList([${JSON.stringify(song).replace(/"/g, '&quot;')}], 0); closeModal()">▶ Play Now</button>
            <button class="btn btn-secondary btn-sm" onclick="toggleFavoriteSong(${song.songId}); closeModal()">🤍 Favorite</button>
            <button class="btn btn-secondary btn-sm" onclick="showAddToPlaylistModal(${song.songId}); closeModal()">➕ Add to Playlist</button>
            ${song.artistId ? `<button class="btn btn-secondary btn-sm" onclick="viewArtist(${song.artistId}); closeModal()">🎤 View Artist</button>` : ''}
        </div>
    `;
    openModal('Song Details', body);
}

// ==================== SHUFFLE PLAY LIST ====================
function shufflePlayList(songs) {
    if (!songs || songs.length === 0) return;
    const shuffled = [...songs].sort(() => Math.random() - 0.5);
    state.shuffle = true;
    const btn = document.getElementById('btn-shuffle');
    if (btn) btn.classList.add('active');
    playSongFromList(shuffled, 0);
    showToast('Shuffle play started 🔀');
}

// ==================== REMOVE FROM PLAYLIST ====================
async function removeFromPlaylist(playlistId, songId) {
    if (!confirm('Remove this song from the playlist?')) return;
    const res = await api(`/playlists/${playlistId}/songs/${songId}`, 'DELETE');
    if (res && res.success) {
        showToast('Removed from playlist');
        viewPlaylist(playlistId);
    } else {
        showToast('Failed to remove song', 'error');
    }
}

// ==================== QUEUE MANAGEMENT ====================
function showQueueModal() {
    if (state.queue.length === 0) {
        showToast('Queue is empty — play a song first', 'error');
        return;
    }
    let html = '<div class="song-list" style="max-height:400px;overflow-y:auto">';
    state.queue.forEach((song, i) => {
        const isActive = i === state.queueIndex;
        html += `<div class="song-row" style="grid-template-columns:40px 1fr 80px 40px; ${isActive ? 'background:var(--accent)22;border-left:3px solid var(--accent)' : ''}" onclick="jumpToQueueIndex(${i})">
            <div><span class="num" style="${isActive ? 'color:var(--accent);font-weight:bold' : ''}">${isActive ? '▶' : (i + 1)}</span></div>
            <div class="song-info">
                ${songThumbHtml(song)}
                <div class="song-details">
                    <div class="song-title" style="${isActive ? 'color:var(--accent-light)' : ''}">${song.title || 'Unknown'}</div>
                    <div class="song-artist">${song.artistName || 'Unknown Artist'}</div>
                </div>
            </div>
            <div class="song-duration">${formatDuration(song.durationSeconds)}</div>
            <div class="song-actions" onclick="event.stopPropagation()">
                ${!isActive ? `<button class="song-action-btn" onclick="removeFromQueue(${i})" title="Remove" style="color:#ef4444">✕</button>` : ''}
            </div>
        </div>`;
    });
    html += '</div>';
    html += `<div style="margin-top:12px;display:flex;gap:8px;justify-content:flex-end">
        <button class="btn btn-secondary btn-sm" onclick="clearQueue()">🗑️ Clear Queue</button>
    </div>`;
    openModal(`Queue (${state.queue.length} songs)`, html);
}

function jumpToQueueIndex(index) {
    state.queueIndex = index;
    playCurrent();
    closeModal();
}

function removeFromQueue(index) {
    if (index === state.queueIndex) {
        showToast('Cannot remove currently playing song', 'error');
        return;
    }
    state.queue.splice(index, 1);
    if (index < state.queueIndex) state.queueIndex--;
    showQueueModal(); // Refresh
    showToast('Removed from queue');
}

function clearQueue() {
    state.queue = state.currentSong ? [state.currentSong] : [];
    state.queueIndex = 0;
    closeModal();
    showToast('Queue cleared');
}

// ==================== EDIT PLAYLIST ====================
async function showEditPlaylistModal(playlistId) {
    const playlist = await api('/playlists/' + playlistId);
    if (!playlist) { showToast('Playlist not found', 'error'); return; }
    openModal('Edit Playlist', `
        <form onsubmit="updatePlaylist(event, ${playlistId})">
            <div class="form-group"><label>Name</label><input type="text" id="edit-pl-name" required value="${playlist.name || ''}"></div>
            <div class="form-group"><label>Description</label><input type="text" id="edit-pl-desc" value="${playlist.description || ''}"></div>
            <div class="form-group"><label>Privacy</label>
                <select id="edit-pl-privacy">
                    <option value="PUBLIC" ${playlist.privacyStatus === 'PUBLIC' ? 'selected' : ''}>Public</option>
                    <option value="PRIVATE" ${playlist.privacyStatus === 'PRIVATE' ? 'selected' : ''}>Private</option>
                </select></div>
            <button type="submit" class="btn btn-primary">Save Changes</button>
        </form>
    `);
}

async function updatePlaylist(e, playlistId) {
    e.preventDefault();
    const updated = {
        playlistId: playlistId,
        userId: state.userId,
        name: document.getElementById('edit-pl-name').value,
        description: document.getElementById('edit-pl-desc').value,
        privacyStatus: document.getElementById('edit-pl-privacy').value
    };
    const res = await api('/playlists/' + playlistId, 'PUT', updated);
    if (res && res.success) {
        closeModal();
        showToast('Playlist updated! ✅');
        viewPlaylist(playlistId);
    } else {
        showToast('Failed to update playlist', 'error');
    }
}

// ==================== PLAY HISTORY SONG ====================
async function playHistorySong(songId) {
    const song = await api('/songs/' + songId);
    if (song) {
        playSongFromList([song], 0);
    } else {
        showToast('Song not found', 'error');
    }
}

// ==================== FOLLOW / UNFOLLOW PLAYLISTS ====================
function getFollowedPlaylists() {
    try {
        return JSON.parse(localStorage.getItem('followedPlaylists') || '[]');
    } catch (e) {
        return [];
    }
}

function followPlaylist(playlistId) {
    const followed = getFollowedPlaylists();
    if (!followed.includes(playlistId)) {
        followed.push(playlistId);
        localStorage.setItem('followedPlaylists', JSON.stringify(followed));
    }
    showToast('Following playlist ⭐');
    renderPlaylists(); // Refresh the view
}

function unfollowPlaylist(playlistId) {
    let followed = getFollowedPlaylists();
    followed = followed.filter(id => id !== playlistId);
    localStorage.setItem('followedPlaylists', JSON.stringify(followed));
    showToast('Unfollowed playlist');
    renderPlaylists(); // Refresh the view
}

// ==================== SONG EDIT MODAL ====================
async function showEditSongModal(songId) {
    const [song, genres, albums] = await Promise.all([
        api('/songs/' + songId),
        api('/genres'),
        api('/albums/artist/' + state.artistId)
    ]);
    if (!song) { showToast('Song not found', 'error'); return; }

    let genreOpts = (genres || []).map(g => `<option value="${g.genreId}" ${g.genreId === song.genreId ? 'selected' : ''}>${g.genreName}</option>`).join('');
    let albumOpts = `<option value="">No Album (Single)</option>`;
    if (albums) albumOpts += albums.map(a => `<option value="${a.albumId}" ${a.albumId === song.albumId ? 'selected' : ''}>${a.title}</option>`).join('');

    openModal('Edit Song', `
        <form onsubmit="updateSongInfo(event, ${songId})">
            <div class="form-group"><label>Title</label><input type="text" id="edit-song-title" value="${song.title || ''}" required></div>
            <div class="form-group"><label>Genre</label><select id="edit-song-genre">${genreOpts}</select></div>
            <div class="form-group"><label>Album</label><select id="edit-song-album">${albumOpts}</select></div>
            <div class="form-group"><label>🖼️ Cover Image (optional)</label>
                <input type="file" id="edit-song-cover" accept="image/*"
                    style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)">
                ${song.coverImageUrl ? `<p class="hint">Current: ${song.coverImageUrl.split('/').pop()}</p>` : ''}
            </div>
            <button type="submit" class="btn btn-primary" id="edit-song-btn">Save Changes</button>
        </form>
    `);
}

async function updateSongInfo(e, songId) {
    e.preventDefault();
    const btn = document.getElementById('edit-song-btn');
    btn.disabled = true;
    btn.textContent = 'Updating...';

    let coverImageUrl = await uploadImageFile(document.getElementById('edit-song-cover'));

    const payload = {
        title: document.getElementById('edit-song-title').value,
        genreId: parseInt(document.getElementById('edit-song-genre').value),
        albumId: document.getElementById('edit-song-album').value ? parseInt(document.getElementById('edit-song-album').value) : null
    };
    if (coverImageUrl) payload.coverImageUrl = coverImageUrl;

    const res = await api('/songs/' + songId, 'PUT', payload);
    if (res && res.success) {
        closeModal();
        showToast('Song updated! ✅');
        navigate('my-songs');
    } else {
        showToast('Failed to update song', 'error');
        btn.disabled = false;
        btn.textContent = 'Save Changes';
    }
}

// ==================== ALBUM EDIT MODAL ====================
async function showEditAlbumModal(albumId) {
    const album = await api('/albums/' + albumId);
    if (!album) { showToast('Album not found', 'error'); return; }

    openModal('Edit Album', `
        <form onsubmit="updateAlbumInfo(event, ${albumId})">
            <div class="form-group"><label>Title</label><input type="text" id="edit-album-title" value="${album.title || ''}" required></div>
            <div class="form-group"><label>Description</label><textarea id="edit-album-desc" style="height:80px">${album.description || ''}</textarea></div>
            <div class="form-group"><label>Release Date</label><input type="date" id="edit-album-date" value="${album.releaseDate || ''}"></div>
            <div class="form-group"><label>🖼️ Cover Image</label>
                <input type="file" id="edit-album-cover" accept="image/*"
                    style="padding:12px;background:var(--bg-secondary);border:2px dashed var(--border);border-radius:12px;cursor:pointer;width:100%;color:var(--text-primary)">
                ${album.coverImageUrl ? `<p class="hint">Current: ${album.coverImageUrl.split('/').pop()}</p>` : ''}
            </div>
            <button type="submit" class="btn btn-primary" id="edit-album-btn">Save Changes</button>
        </form>
    `);
}

async function updateAlbumInfo(e, albumId) {
    e.preventDefault();
    const btn = document.getElementById('edit-album-btn');
    btn.disabled = true;
    btn.textContent = 'Updating...';

    let coverImageUrl = await uploadImageFile(document.getElementById('edit-album-cover'));

    const payload = {
        title: document.getElementById('edit-album-title').value,
        description: document.getElementById('edit-album-desc').value,
        releaseDate: document.getElementById('edit-album-date').value
    };
    if (coverImageUrl) payload.coverImageUrl = coverImageUrl;

    const res = await api('/albums/' + albumId, 'PUT', payload);
    if (res && res.success) {
        closeModal();
        showToast('Album updated! ✅');
        navigate('my-albums');
    } else {
        showToast('Failed to update album', 'error');
        btn.disabled = false;
        btn.textContent = 'Save Changes';
    }
}

// ==================== SONG VISIBILITY TOGGLE ====================
async function toggleSongVisibility(songId, currentStatus) {
    const newStatus = currentStatus === 'N' ? 'Y' : 'N';
    const res = await api('/songs/' + songId + '/visibility', 'PUT', { isActive: newStatus });
    if (res && res.success) {
        showToast(newStatus === 'Y' ? 'Song is now Public 👁️' : 'Song is now Unlisted 🙈');
        navigate('my-songs');
    } else {
        showToast('Failed to update visibility', 'error');
    }
}

// ==================== ADD SONG TO ALBUM ====================
async function showAddSongToAlbumModal(albumId) {
    const allSongs = await api('/songs/artist/' + state.artistId);
    // Filter songs that are not in any album or in a different album
    const availableSongs = (allSongs || []).filter(s => !s.albumId || s.albumId !== albumId);

    if (availableSongs.length === 0) {
        openModal('Add Songs to Album', '<div class="empty-state"><h3>No available songs</h3><p>All your songs are already in this album or you have no uploaded songs.</p></div>');
        return;
    }

    let html = '<div class="song-list" style="max-height:400px;overflow-y:auto">';
    availableSongs.forEach(song => {
        html += `<div class="song-row" style="grid-template-columns:1fr auto;cursor:pointer" onclick="addSongToAlbum(${song.songId}, ${albumId})">
            <div class="song-info">
                ${songThumbHtml(song)}
                <div class="song-details">
                    <div class="song-title">${song.title || 'Unknown'}</div>
                    <div class="song-artist">${song.albumTitle ? 'In: ' + song.albumTitle : 'Single'}</div>
                </div>
            </div>
            <button class="btn btn-primary btn-sm" onclick="event.stopPropagation(); addSongToAlbum(${song.songId}, ${albumId})">➕ Add</button>
        </div>`;
    });
    html += '</div>';
    openModal('Add Songs to Album', html);
}

async function addSongToAlbum(songId, albumId) {
    const res = await api('/songs/' + songId, 'PUT', { albumId: albumId });
    if (res && res.success) {
        showToast('Song added to album! ✅');
        closeModal();
        viewAlbum(albumId);
    } else {
        showToast('Failed to add song to album', 'error');
    }
}

async function removeSongFromAlbum(songId, albumId) {
    if (!confirm('Remove this song from the album?')) return;
    const res = await api('/songs/' + songId, 'PUT', { albumId: null });
    if (res && res.success) {
        showToast('Song removed from album');
        viewAlbum(albumId);
    } else {
        showToast('Failed to remove song', 'error');
    }
}
