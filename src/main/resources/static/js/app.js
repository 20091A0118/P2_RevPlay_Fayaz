/* ========================================
   RevPlay - Simplified Frontend
   Listener: Browse + Play songs/albums
   Artist: Upload songs + Manage albums
   ======================================== */

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
    repeat: false,
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
    if (state.repeat) {
        playCurrent();
    } else {
        playNext();
    }
});

// Maps URL paths to navigate() page names
function pathToPage(pathname) {
    const map = {
        '/home': 'home', '/songs': 'songs', '/albums': 'albums',
        '/podcasts': 'podcasts', '/playlists': 'playlists', '/favorites': 'favorites',
        '/profile': 'profile', '/upload': 'upload', '/upload-podcast': 'upload-podcast',
        '/my-songs': 'my-songs', '/my-albums': 'my-albums', '/my-podcasts': 'my-podcasts'
    };
    return map[pathname] || 'home';
}

// Handle browser back/forward buttons
window.addEventListener('popstate', () => {
    const page = pathToPage(window.location.pathname);
    navigate(page, true);
});

window.addEventListener('DOMContentLoaded', () => {
    const publicPaths = ['/', '/login', '/register'];
    const currentPath = window.location.pathname;

    if (!state.token && !publicPaths.includes(currentPath)) {
        window.location.href = '/';
        return;
    }

    if (state.token && (currentPath === '/login' || currentPath === '/register')) {
        window.location.href = '/home';
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

        // Show/hide sidebar items based on role
        const isArtist = state.artistId != null;
        document.querySelectorAll('.artist-only').forEach(el => el.style.display = isArtist ? 'flex' : 'none');

        const page = pathToPage(currentPath);
        navigate(page, true);
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
        headers: { 'Content-Type': 'application/json' }
    };
    const token = localStorage.getItem('token');
    if (token) opts.headers['Authorization'] = 'Bearer ' + token;
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch('/api' + url, opts);
    if (!res.ok && res.status === 401) {
        console.warn('Unauthorized or token expired');
    }
    if (!res.ok && res.status === 404) return null;
    return res.json();
}

// ==================== NAVIGATION ====================
async function navigate(page, skipPush) {
    const area = document.getElementById('content-area');
    if (!area) return;

    if (!skipPush) {
        const urlPath = '/' + (page === 'home' ? 'home' : page);
        history.pushState({ page }, '', urlPath);
    }

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
            case 'podcasts': await renderPodcasts(); break;
            case 'playlists': await renderPlaylists(); break;
            case 'favorites': await renderFavorites(); break;
            case 'upload': await renderUploadSong(); break;
            case 'upload-podcast': await renderUploadPodcast(); break;
            case 'my-songs': await renderMySongs(); break;
            case 'my-albums': await renderMyAlbums(); break;
            case 'my-podcasts': await renderMyPodcasts(); break;
            case 'profile': await renderProfile(); break;
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

    const err = document.getElementById('error-msg');
    if (err) err.style.display = 'none';
    const succ = document.getElementById('success-msg');
    if (succ) succ.style.display = 'none';

    const userFields = document.getElementById('user-register-fields');
    const artistFields = document.getElementById('artist-register-fields');
    if (userFields) userFields.style.display = (form === 'register' && state.authType === 'user') ? 'block' : 'none';
    if (artistFields) artistFields.style.display = (form === 'register' && state.authType === 'artist') ? 'block' : 'none';

    if (userFields) {
        userFields.querySelectorAll('input').forEach(input => input.required = (state.authType === 'user' && form === 'register'));
    }
    if (artistFields) {
        artistFields.querySelectorAll('input, textarea').forEach(input => input.required = (state.authType === 'artist' && form === 'register'));
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
        window.location.href = '/home';
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
            window.location.href = '/home';
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
    window.location.href = '/';
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
    const role = state.authType;

    if (container.style.display === 'none') {
        const endpoint = role === 'user' ? `/auth/user/security-question?email=${email}` : `/auth/artist/security-question?email=${email}`;
        const data = await api(endpoint);
        if (data && data.success) {
            document.getElementById('security-q-label').textContent = `Question: ${data.securityQuestion || 'No question set'}`;
            if (data.passwordHint) {
                document.getElementById('security-q-label').innerHTML += `<br><small style="color:#555">Hint: ${data.passwordHint}</small>`;
            }
            container.style.display = 'block';
            btn.textContent = 'Reset Password';
            document.getElementById('forgot-email').disabled = true;
        } else {
            showToast(data ? data.message : 'User not found', 'error');
        }
    } else {
        const securityAnswer = document.getElementById('forgot-answer').value;
        const newPassword = document.getElementById('forgot-new-pass').value;
        if (!securityAnswer || !newPassword) { showToast('Please fill all fields', 'error'); return; }
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

// ==================== SEARCH ====================
async function handleSearch(e) {
    if (e.key === 'Enter') {
        const q = e.target.value.trim();
        if (!q) return;
        const area = document.getElementById('content-area');
        area.innerHTML = '<div class="loading"><div class="spinner"></div></div>';
        const songs = await api('/songs/search?q=' + encodeURIComponent(q));
        area.innerHTML = `<div class="page-header"><div><h2>Search: "${q}"</h2><p class="subtitle">${(songs || []).length} results found</p></div></div>`;
        area.innerHTML += renderSongList(songs || []);
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
    const colors = ['#2563eb', '#16a34a', '#3b82f6', '#059669', '#0ea5e9', '#6366f1'];
    const c = colors[(song.songId || 0) % colors.length];
    return `background:${c}22`;
}

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

function renderSongList(songs) {
    if (!songs || songs.length === 0) {
        return `<div class="empty-state"><span class="icon">🎵</span><h3>No songs found</h3><p>Try exploring or uploading some music</p></div>`;
    }
    let html = '<div class="song-list">';
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
            <div class="song-duration">${formatDuration(song.durationSeconds)}</div>
            <div class="song-actions" onclick="event.stopPropagation()">
                <button class="song-action-btn" title="Add to Playlist" onclick="showAddToPlaylistModal(${song.songId})" style="color:var(--text-secondary)">➕</button>
                <button class="song-action-btn" title="Favorite" onclick="toggleFavorite(${song.songId}, this)" style="color:var(--accent)">🤍</button>
                ${song.fileUrl ? `<a href="${song.fileUrl}" class="song-action-btn" title="Download" download style="color:var(--text-secondary)">⬇️</a>` : ''}
                ${state.artistId && song.artistId == state.artistId ? `<button class="song-action-btn" onclick="deleteSong(${song.songId})" style="color:var(--red)" title="Delete">🗑️</button>` : ''}
            </div>
        </div>`;
    });
    html += '</div>';
    return html;
}

// ==================== PAGE RENDERERS ====================
async function renderHome() {
    const area = document.getElementById('content-area');
    const isArtist = state.artistId != null;

    if (isArtist) {
        // Artist home: quick links
        const songs = await api('/songs/artist/' + state.artistId);
        let html = `<div>
            <div class="page-header"><div><h2>Welcome, ${state.userName} 👋</h2><p class="subtitle">Manage your music from here</p></div></div>
            <div class="cards-grid">
                <div class="card" onclick="navigate('upload')">
                    <div class="card-image" style="background:#dbeafe">⬆️</div>
                    <div class="card-title">Upload Song</div>
                    <div class="card-subtitle">Add new music</div>
                </div>
                <div class="card" onclick="navigate('my-songs')">
                    <div class="card-image" style="background:#dcfce7">🎵</div>
                    <div class="card-title">My Songs</div>
                    <div class="card-subtitle">${(songs || []).length} tracks</div>
                </div>
                <div class="card" onclick="navigate('my-albums')">
                    <div class="card-image" style="background:#e0e7ff">💿</div>
                    <div class="card-title">My Albums</div>
                    <div class="card-subtitle">Manage albums</div>
                </div>
            </div>`;
        if (songs && songs.length > 0) {
            html += '<div class="section-header"><h3>Your Songs</h3></div>';
            html += renderSongList(songs);
        }
        html += '</div>';
        area.innerHTML = html;
    } else {
        // Listener home: show all songs
        const songs = await api('/songs');
        let html = `<div>
            <div class="page-header"><div><h2>Welcome, ${state.userName} 👋</h2><p class="subtitle">Browse and play music</p></div></div>
            <div class="section-header"><h3>🎶 All Songs</h3></div>
            ${renderSongList(songs || [])}
        </div>`;
        area.innerHTML = html;
    }
}

async function renderSongs() {
    const songs = await api('/songs');
    const area = document.getElementById('content-area');
    area.innerHTML = `<div>
        <div class="page-header"><div><h2>All Songs</h2><p class="subtitle">${(songs || []).length} tracks available</p></div></div>
        ${renderSongList(songs || [])}
    </div>`;
}

async function renderAlbums() {
    const albums = await api('/albums');
    const area = document.getElementById('content-area');
    let html = `<div><div class="page-header"><h2>Albums</h2></div><div class="cards-grid">`;
    if (albums) albums.forEach(album => {
        html += `<div class="card" onclick="viewAlbum(${album.albumId})">
            ${cardImageSimple(album.coverImageUrl, '💿', 'background:#e0e7ff')}
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
    area.innerHTML = `<div>
        <div class="page-header"><div><h2>💿 ${album.title}</h2><p class="subtitle">${album.artistName || ''} • ${album.releaseDate || ''}</p></div></div>
        <p style="color:var(--text-secondary);margin-bottom:18px">${album.description || ''}</p>
        ${renderSongList(songs || [])}
    </div>`;
}

// ==================== FAVORITES ====================
async function toggleFavorite(songId, btnEl) {
    if (!state.userId) { showToast('Please login as a listener to save favorites', 'error'); return; }

    // Check current state (simplistic approach: just hit the add endpoint, if it fails maybe it's already there)
    // To be precise, we should check if it's already favorited. But we can toggle purely visually for now and rely on backend.
    const check = await api(`/favorites/${state.userId}/${songId}`);
    const isFav = check && check.isFavorite;

    if (isFav) {
        await api(`/favorites/${state.userId}/${songId}`, 'DELETE');
        btnEl.textContent = '🤍';
        showToast('Removed from favorites');
    } else {
        await api(`/favorites/${state.userId}/${songId}`, 'POST');
        btnEl.textContent = '❤️';
        showToast('Added to favorites');
    }
}

async function renderFavorites() {
    if (!state.userId) {
        document.getElementById('content-area').innerHTML = '<div class="empty-state"><h3>Listeners Only</h3><p>Please login as a listener to view favorites.</p></div>';
        return;
    }
    const songs = await api('/favorites/' + state.userId);
    const area = document.getElementById('content-area');

    // We should pre-mark all these songs as favorites visually
    // In a full implementation, we'd pass favorited status to renderSongList. 
    // For now, renderSongList shows '🤍' by default, we'll let it be for simplicity, 
    // or the user can click it to toggle.

    area.innerHTML = `<div>
        <div class="page-header"><div><h2>🤍 Favorites</h2><p class="subtitle">Your saved tracks</p></div></div>
        ${renderSongList(songs || [])}
    </div>`;
}

// ==================== PLAYLISTS ====================
async function renderPlaylists() {
    if (!state.userId) {
        document.getElementById('content-area').innerHTML = '<div class="empty-state"><h3>Listeners Only</h3><p>Please login as a listener to view playlists.</p></div>';
        return;
    }
    const playlists = await api('/playlists/user/' + state.userId);
    const area = document.getElementById('content-area');
    let html = `<div>
        <div class="page-header">
            <div><h2>📋 My Playlists</h2></div>
            <button class="btn btn-primary btn-sm" onclick="showCreatePlaylistModal()">➕ New Playlist</button>
        </div>`;

    if (playlists && playlists.length > 0) {
        html += '<div class="cards-grid">';
        playlists.forEach(p => {
            html += `<div class="card" onclick="viewPlaylist(${p.playlistId})">
                ${cardImageSimple('', '📋', 'background:#f0fdf4')}
                <div class="card-title">${p.name}</div>
                <button class="btn btn-secondary btn-sm" style="margin-top:6px;width:100%" onclick="event.stopPropagation(); deletePlaylist(${p.playlistId})">🗑️ Delete</button>
            </div>`;
        });
        html += '</div>';
    } else {
        html += '<div class="empty-state"><h3>No Playlists</h3><p>Create a playlist to organize your favorite songs.</p></div>';
    }
    html += '</div>';
    area.innerHTML = html;
}

function showCreatePlaylistModal() {
    openModal('Create Playlist', `
        <form onsubmit="createPlaylist(event)">
            <div class="form-group"><label>Playlist Name</label><input type="text" id="playlist-name" required></div>
            <button type="submit" class="btn btn-primary">Create</button>
        </form>
    `);
}

async function createPlaylist(e) {
    e.preventDefault();
    const name = document.getElementById('playlist-name').value;
    const res = await api('/playlists', 'POST', { userId: state.userId, name: name });
    closeModal();
    if (res && res.success) {
        showToast('Playlist created');
        navigate('playlists');
    } else {
        showToast('Failed to create playlist', 'error');
    }
}

async function viewPlaylist(playlistId) {
    const [playlist, songs] = await Promise.all([
        api('/playlists/' + playlistId),
        api('/playlists/' + playlistId + '/songs')
    ]);
    const area = document.getElementById('content-area');
    if (!playlist) { area.innerHTML = '<div class="empty-state"><h3>Playlist not found</h3></div>'; return; }

    area.innerHTML = `<div>
        <div class="page-header"><div><h2>📋 ${playlist.name}</h2></div></div>
        ${renderSongList(songs || [])}
    </div>`;
}

async function deletePlaylist(playlistId) {
    if (!confirm('Delete this playlist?')) return;
    const res = await api('/playlists/' + playlistId, 'DELETE');
    if (res && res.success) {
        showToast('Playlist deleted');
        navigate('playlists');
    } else {
        showToast('Failed to delete playlist', 'error');
    }
}

async function showAddToPlaylistModal(songId) {
    if (!state.userId) { showToast('Please login as listener', 'error'); return; }
    const playlists = await api('/playlists/user/' + state.userId);
    if (!playlists || playlists.length === 0) {
        showToast('You have no playlists. Create one first!', 'error');
        return;
    }

    let opts = playlists.map(p => `<option value="${p.playlistId}">${p.name}</option>`).join('');
    openModal('Add to Playlist', `
        <form onsubmit="addSongToPlaylist(event, ${songId})">
            <div class="form-group">
                <label>Select Playlist</label>
                <select id="select-playlist" required>${opts}</select>
            </div>
            <button type="submit" class="btn btn-primary">Add</button>
        </form>
    `);
}

async function addSongToPlaylist(e, songId) {
    e.preventDefault();
    const playlistId = document.getElementById('select-playlist').value;
    const res = await api(`/playlists/${playlistId}/songs/${songId}`, 'POST');
    closeModal();
    if (res && res.success) {
        showToast('Song added to playlist');
    } else {
        showToast('Failed to add song to playlist', 'error');
    }
}

// ==================== PODCASTS (Listener) ====================
async function renderPodcasts() {
    const podcasts = await api('/podcasts');
    const area = document.getElementById('content-area');
    let html = `<div><div class="page-header"><h2>🎙️ Podcasts</h2></div><div class="cards-grid">`;
    if (podcasts) podcasts.forEach(p => {
        html += `<div class="card" onclick="viewPodcast(${p.podcastId})">
            ${cardImageSimple(p.coverImageUrl, '🎙️', 'background:#ffedd5')}
            <div class="card-title">${p.title}</div>
            <div class="card-subtitle">${p.artistName || 'Unknown Host'}</div>
        </div>`;
    });
    html += '</div></div>';
    area.innerHTML = html;
}

// Modify viewPodcast to show both viewer and artist specific controls if they own it
async function viewPodcast(podcastId) {
    const [podcast, episodes] = await Promise.all([
        api('/podcasts/' + podcastId),
        api('/podcasts/' + podcastId + '/episodes')
    ]);
    const area = document.getElementById('content-area');
    if (!podcast) { area.innerHTML = '<div class="empty-state"><h3>Podcast not found</h3></div>'; return; }

    let html = `<div>
        <div class="page-header">
            <div><h2>🎙️ ${podcast.title}</h2><p class="subtitle">Hosted by ${podcast.artistName || 'Unknown'}</p></div>`;

    if (state.artistId && podcast.artistId == state.artistId) {
        html += `<button class="btn btn-primary btn-sm" onclick="showUploadEpisodeModal(${podcast.podcastId})">➕ Add Episode</button>`;
    }

    html += `</div>
        <p style="color:var(--text-secondary);margin-bottom:18px">${podcast.description || ''}</p>`;

    if (!episodes || episodes.length === 0) {
        html += '<div class="empty-state"><h3>No Episodes</h3><p>This podcast has no episodes yet.</p></div>';
    } else {
        html += '<div class="song-list">';
        episodes.forEach((ep, i) => {
            html += `
            <div class="song-row" onclick="playEpisodeFromList(${JSON.stringify(episodes).replace(/"/g, '&quot;')}, ${i})">
                <div>
                    <span class="num">${i + 1}</span>
                    <span class="play-icon">▶</span>
                </div>
                <div class="song-info">
                    ${ep.coverImageUrl ? `<div class="song-thumb"><img src="${ep.coverImageUrl}" alt="cover" style="width:100%;height:100%;object-fit:cover;border-radius:inherit"></div>` : `<div class="song-thumb" style="background:#ffedd5">🎙️</div>`}
                    <div class="song-details">
                        <div class="song-title">${ep.title || 'Unknown'}</div>
                        <div class="song-artist">${ep.releaseDate || ''}</div>
                    </div>
                </div>
                <div class="song-album">${formatDuration(ep.durationSeconds)}</div>
                <div class="song-actions" onclick="event.stopPropagation()">
                    ${ep.fileUrl ? `<a href="${ep.fileUrl}" class="song-action-btn" title="Download" download style="color:var(--text-secondary)">⬇️</a>` : ''}
                    ${state.artistId && podcast.artistId == state.artistId ? `<button class="song-action-btn" onclick="deleteEpisode(${ep.episodeId})" style="color:var(--red)" title="Delete">🗑️</button>` : ''}
                </div>
            </div>`;
        });
        html += '</div>';
    }
    html += '</div>';
    area.innerHTML = html;
}

function playEpisodeFromList(episodes, index) {
    // Map episode format to queue format so the player can use it
    const songs = episodes.map(ep => ({
        songId: null, // Podcasts don't record plays the same way or use a different endpoint
        title: ep.title,
        artistName: 'Episode',
        fileUrl: ep.fileUrl,
        coverImageUrl: ep.coverImageUrl,
        durationSeconds: ep.durationSeconds,
        episodeId: ep.episodeId // Custom property for episodes
    }));
    state.queue = songs;
    state.queueIndex = index;
    playCurrent();

    // Attempt to register play count for episode
    const currentEp = songs[index];
    if (currentEp.episodeId) {
        api(`/podcasts/episodes/${currentEp.episodeId}/play`, 'POST').catch(e => console.error(e));
    }
}

async function deleteEpisode(episodeId) {
    if (!confirm('Delete this episode?')) return;
    const res = await api('/podcasts/episodes/' + episodeId + '?artistId=' + state.artistId, 'DELETE');
    if (res && res.success) {
        showToast('Episode deleted');
        // Let user hit back or simply naive-reload:
        const prev = window.location.pathname;
        navigate(pathToPage(prev), true);
    } else {
        showToast('Failed to delete episode', 'error');
    }
}

// ==================== ARTIST PAGES ====================
async function renderUploadSong() {
    const [genres, albums] = await Promise.all([api('/genres'), api('/albums/artist/' + state.artistId)]);
    const area = document.getElementById('content-area');

    let cleanGenres = genres ? genres.filter(g => !g.genreName.includes('-') || g.genreName.length < 20) : [];
    if (cleanGenres.length === 0) cleanGenres = genres || [];

    let genreOpts = cleanGenres.map(g => `<option value="${g.genreId}">${g.genreName}</option>`).join('');
    genreOpts += '<option value="__new__">➕ Create New Genre</option>';

    let albumOpts = '<option value="">No Album (Single)</option>';
    if (albums) albumOpts += albums.map(a => `<option value="${a.albumId}">${a.title}</option>`).join('');

    area.innerHTML = `<div>
        <div class="page-header"><h2>⬆️ Upload Song</h2></div>
        <div style="max-width:480px">
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
            <div class="form-group"><label>Cover Image (optional)</label>
                <input type="file" id="song-cover-file" accept="image/*"
                       style="padding:10px;background:#f5f5f5;border:2px dashed #e0e0e0;border-radius:8px;cursor:pointer;width:100%"></div>
            <div class="form-group"><label>Audio File</label>
                <input type="file" id="song-file" accept="audio/*" required
                       style="padding:10px;background:#f5f5f5;border:2px dashed #e0e0e0;border-radius:8px;cursor:pointer;width:100%"
                       onchange="previewAudioFile(this)">
                <div id="audio-preview" style="margin-top:6px;color:#555;font-size:13px"></div></div>
            <button type="submit" class="btn btn-primary" id="upload-btn">Upload Song</button>
        </form></div></div>`;
}

function previewAudioFile(input) {
    const preview = document.getElementById('audio-preview');
    if (input.files && input.files[0]) {
        const file = input.files[0];
        const sizeMB = (file.size / (1024 * 1024)).toFixed(1);
        preview.innerHTML = `📁 <strong>${file.name}</strong> (${sizeMB} MB)`;
        const tempAudio = new Audio();
        tempAudio.src = URL.createObjectURL(file);
        tempAudio.onloadedmetadata = () => {
            const dur = Math.round(tempAudio.duration);
            preview.innerHTML += ` — Duration: ${formatDuration(dur)}`;
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

    uploadBtn.textContent = 'Uploading cover image...';
    const coverImageUrl = await uploadImageFile(document.getElementById('song-cover-file'));

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
    area.innerHTML = `<div>
        <div class="page-header">
            <div><h2>🎵 My Songs</h2><p class="subtitle">${(songs || []).length} tracks</p></div>
            <button class="btn btn-primary btn-sm" onclick="navigate('upload')">⬆️ Upload</button>
        </div>
        ${renderSongList(songs || [])}
    </div>`;
}

async function renderMyAlbums() {
    const albums = await api('/albums/artist/' + state.artistId);
    const area = document.getElementById('content-area');
    let html = `<div>
        <div class="page-header">
            <div><h2>💿 My Albums</h2></div>
            <button class="btn btn-primary btn-sm" onclick="showCreateAlbumModal()">➕ New Album</button>
        </div>`;

    if (albums && albums.length > 0) {
        html += '<div class="cards-grid">';
        albums.forEach(a => {
            html += `<div class="card" onclick="viewAlbum(${a.albumId})">
                ${cardImageSimple(a.coverImageUrl, '💿', 'background:#e0e7ff')}
                <div class="card-title">${a.title}</div>
                <div class="card-subtitle">${a.releaseDate || ''}</div>
                ${state.artistId && a.artistId == state.artistId ? `<button class="btn btn-secondary btn-sm" style="margin-top:6px;width:100%" onclick="event.stopPropagation(); deleteAlbum(${a.albumId})">🗑️ Delete</button>` : ''}
            </div>`;
        });
        html += '</div>';
    } else {
        html += '<div class="empty-state"><span class="icon">💿</span><h3>No albums yet</h3><p>Create an album to organize your songs</p></div>';
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
            <div class="form-group"><label>Cover Image (optional)</label>
                <input type="file" id="album-cover-file" accept="image/*"
                       style="padding:10px;background:#f5f5f5;border:2px dashed #e0e0e0;border-radius:8px;cursor:pointer;width:100%"></div>
            <button type="submit" class="btn btn-primary" id="album-submit-btn">Create Album</button>
        </form>
    `);
}

async function createAlbum(e) {
    e.preventDefault();
    const submitBtn = document.getElementById('album-submit-btn');
    submitBtn.textContent = 'Creating...';
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

// ==================== PROFILE ====================
async function renderProfile() {
    const id = state.userId || state.artistId;
    const isArtist = state.authType === 'artist';
    const url = isArtist ? '/artists/' + id : '/users/' + id;
    const data = await api(url);
    const area = document.getElementById('content-area');
    if (!data) { area.innerHTML = '<div class="empty-state"><h3>Profile not found</h3></div>'; return; }

    let html = `<div><div class="page-header"><h2>👤 Profile</h2></div>`;
    if (isArtist) {
        html += `<div class="stats-row">
            <div class="stat-card"><div class="stat-value">${data.stageName || '-'}</div><div class="stat-label">Stage Name</div></div>
            <div class="stat-card"><div class="stat-value">${data.genre || '-'}</div><div class="stat-label">Genre</div></div>
        </div>
        <p style="color:var(--text-secondary);margin-bottom:16px">${data.bio || ''}</p>`;
    } else {
        html += `<div class="stats-row">
            <div class="stat-card"><div class="stat-value">${data.fullName || '-'}</div><div class="stat-label">Name</div></div>
            <div class="stat-card"><div class="stat-value">${data.email || '-'}</div><div class="stat-label">Email</div></div>
        </div>`;
    }
    html += '</div>';
    area.innerHTML = html;
}

// ==================== MUSIC PLAYER ====================
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
        if (state.userId) api('/songs/' + id + '/play?userId=' + state.userId, 'POST');
    }
}

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

    document.getElementById('player-title').textContent = song.title || 'Unknown';
    document.getElementById('player-artist').textContent = song.artistName || 'Unknown Artist';
    document.getElementById('player-thumb').className = 'player-thumb playing';
    document.getElementById('btn-play-pause').textContent = '⏸';
    document.getElementById('progress-fill').style.width = '0%';
    document.getElementById('current-time').textContent = '0:00';
    document.getElementById('total-time').textContent = formatDuration(song.durationSeconds || 0);

    const thumb = document.getElementById('player-thumb');
    if (song.coverImageUrl) {
        thumb.innerHTML = `<img src="${song.coverImageUrl}" alt="cover" style="width:100%;height:100%;object-fit:cover;border-radius:inherit">`;
    } else {
        thumb.innerHTML = '🎵';
    }

    if (song.songId) {
        api(`/songs/${song.songId}/play?userId=${state.userId || ''}`, 'POST');
    }

    const fileUrl = song.fileUrl || '';
    if (fileUrl) {
        state.audio.src = fileUrl;
        state.audio.load();
        const playPromise = state.audio.play();
        if (playPromise) {
            playPromise.catch(err => {
                console.warn('Playback failed:', err.message);
                showToast('Could not play audio — click play button.', 'error');
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
    state.repeat = !state.repeat;
    document.getElementById('btn-repeat').classList.toggle('active', state.repeat);
    showToast(state.repeat ? 'Repeat on' : 'Repeat off');
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

// ==================== DELETE ====================
async function deleteSong(songId) {
    if (!confirm('Delete this song?')) return;
    const res = await api(`/songs/${songId}?artistId=${state.artistId}`, 'DELETE');
    if (res && res.success) {
        showToast('Song deleted');
        navigate('my-songs');
    } else {
        showToast('Failed to delete song', 'error');
    }
}

async function deleteAlbum(albumId) {
    if (!confirm('Delete this album?')) return;
    const res = await api(`/albums/${albumId}?artistId=${state.artistId}`, 'DELETE');
    if (res && res.success) {
        showToast('Album deleted');
        navigate('my-albums');
    } else {
        showToast('Failed to delete album', 'error');
    }
}

// ==================== PODCASTS (Artist) ====================
async function renderMyPodcasts() {
    const podcasts = await api('/podcasts/artist/' + state.artistId);
    const area = document.getElementById('content-area');
    let html = `<div>
        <div class="page-header">
            <div><h2>🎙️ My Podcasts</h2></div>
            <button class="btn btn-primary btn-sm" onclick="showCreatePodcastModal()">➕ New Podcast</button>
        </div>`;

    if (podcasts && podcasts.length > 0) {
        html += '<div class="cards-grid">';
        podcasts.forEach(p => {
            html += `<div class="card" onclick="viewPodcast(${p.podcastId})">
                ${cardImageSimple(p.coverImageUrl, '🎙️', 'background:#ffedd5')}
                <div class="card-title">${p.title}</div>
                <button class="btn btn-secondary btn-sm" style="margin-top:6px;width:100%" onclick="event.stopPropagation(); deletePodcast(${p.podcastId})">🗑️ Delete</button>
            </div>`;
        });
        html += '</div>';
    } else {
        html += '<div class="empty-state"><span class="icon">🎙️</span><h3>No podcasts yet</h3><p>Create a podcast to start publishing episodes</p></div>';
    }
    html += '</div>';
    area.innerHTML = html;
}

function showCreatePodcastModal() {
    openModal('Create Podcast', `
        <form onsubmit="createPodcast(event)">
            <div class="form-group"><label>Title</label><input type="text" id="podcast-title" required></div>
            <div class="form-group"><label>Description</label><textarea id="podcast-desc" placeholder="Podcast description"></textarea></div>
            <div class="form-group"><label>Cover Image (optional)</label>
                <input type="file" id="podcast-cover-file" accept="image/*"
                       style="padding:10px;background:#f5f5f5;border:2px dashed #e0e0e0;border-radius:8px;cursor:pointer;width:100%"></div>
            <button type="submit" class="btn btn-primary" id="podcast-submit-btn">Create Podcast</button>
        </form>
    `);
}

async function createPodcast(e) {
    e.preventDefault();
    const btn = document.getElementById('podcast-submit-btn');
    btn.textContent = 'Creating...';
    btn.disabled = true;

    const coverImageUrl = await uploadImageFile(document.getElementById('podcast-cover-file'));
    const podcast = {
        artistId: parseInt(state.artistId),
        title: document.getElementById('podcast-title').value,
        description: document.getElementById('podcast-desc').value,
        coverImageUrl: coverImageUrl || ''
    };

    const res = await api('/podcasts', 'POST', podcast);
    closeModal();
    if (res && res.success) {
        showToast('Podcast created! 🎙️');
        navigate('my-podcasts');
    } else {
        showToast('Failed to create podcast', 'error');
    }
}

async function deletePodcast(podcastId) {
    if (!confirm('Delete this podcast and all its episodes?')) return;
    const res = await api(`/podcasts/${podcastId}?artistId=${state.artistId}`, 'DELETE');
    if (res && res.success) {
        showToast('Podcast deleted');
        navigate('my-podcasts');
    } else {
        showToast('Failed to delete podcast', 'error');
    }
}

function showUploadEpisodeModal(podcastId) {
    openModal('Upload Episode', `
        <form onsubmit="uploadEpisode(event, ${podcastId})">
            <div class="form-group"><label>Title</label><input type="text" id="ep-title" required></div>
            <div class="form-group"><label>Release Date</label><input type="date" id="ep-date" required></div>
            <div class="form-group"><label>Cover Image (optional)</label>
                <input type="file" id="ep-cover-file" accept="image/*"
                       style="padding:10px;background:#f5f5f5;border:2px dashed #e0e0e0;border-radius:8px;cursor:pointer;width:100%"></div>
            <div class="form-group"><label>Audio File</label>
                <input type="file" id="ep-file" accept="audio/*" required
                       style="padding:10px;background:#f5f5f5;border:2px dashed #e0e0e0;border-radius:8px;cursor:pointer;width:100%"
                       onchange="previewAudioFile(this)">
                <div id="audio-preview" style="margin-top:6px;color:#555;font-size:13px"></div></div>
            <button type="submit" class="btn btn-primary" id="ep-upload-btn">Upload Episode</button>
        </form>
    `);
}

async function uploadEpisode(e, podcastId) {
    e.preventDefault();
    const uploadBtn = document.getElementById('ep-upload-btn');
    const fileInput = document.getElementById('ep-file');

    if (!fileInput.files || !fileInput.files[0]) {
        showToast('Please select an audio file', 'error');
        return;
    }

    uploadBtn.textContent = 'Uploading file...';
    uploadBtn.disabled = true;

    // Upload audio
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
            showToast('File upload failed', 'error');
            uploadBtn.textContent = 'Upload Episode';
            uploadBtn.disabled = false;
            return;
        }
    } catch (err) {
        showToast('File upload failed', 'error');
        uploadBtn.textContent = 'Upload Episode';
        uploadBtn.disabled = false;
        return;
    }

    uploadBtn.textContent = 'Uploading cover...';
    const coverImageUrl = await uploadImageFile(document.getElementById('ep-cover-file'));

    uploadBtn.textContent = 'Saving episode...';
    const duration = parseInt(fileInput.dataset.duration) || 0;

    const ep = {
        title: document.getElementById('ep-title').value,
        releaseDate: document.getElementById('ep-date').value,
        durationSeconds: duration,
        fileUrl: fileUrl,
        coverImageUrl: coverImageUrl
    };

    const res = await api('/podcasts/' + podcastId + '/episodes?artistId=' + state.artistId, 'POST', ep);
    closeModal();
    if (res && res.success) {
        showToast('Episode uploaded! 🎙️');
        viewPodcast(podcastId); // Refresh the podcast view
    } else {
        showToast('Upload failed', 'error');
        uploadBtn.textContent = 'Upload Episode';
        uploadBtn.disabled = false;
    }
}

async function renderUploadPodcast() {
    renderMyPodcasts(); // For the direct sidebar route, just reuse my-podcasts view where they can click New Podcast
}
