
// --- 1. AUTENTIKASI & UTILITAS ---
const token = sessionStorage.getItem('hris_token');
const role = sessionStorage.getItem('hris_role');

if (!token || role !== 'ROLE_KARYAWAN') {
    sessionStorage.clear();
    window.location.href = '/login.html';
}

const escapeHTML = (str) => {
    if (str === null || str === undefined) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
};

const formatRupiah = (angka) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(angka);
const statusBadge = document.getElementById('status-badge');
const statusIcon = document.getElementById('status-icon');
const statusText = document.getElementById('status-text');

// --- 2. FUNGSI UI & INTERAKSI ---
function handleLogout() {
    sessionStorage.clear();
    window.location.href = '/login.html';
}

function showStatus(message, isError = false) {
    statusText.textContent = message;
    if(isError) {
        statusBadge.className = "neu-icon fixed top-8 right-8 px-6 py-4 rounded-2xl text-sm font-black bg-rose-100 text-rose-800 z-[100] transform transition-all flex items-center gap-3";
        statusIcon.innerHTML = `<svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>`;
    } else {
        statusBadge.className = "neu-icon fixed top-8 right-8 px-6 py-4 rounded-2xl text-sm font-black bg-emerald-100 text-emerald-800 z-[100] transform transition-all flex items-center gap-3";
        statusIcon.innerHTML = `<svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>`;
    }
    statusBadge.classList.remove('hidden', 'translate-y-4', 'opacity-0');
    setTimeout(() => {
        statusBadge.classList.add('translate-y-4', 'opacity-0');
        setTimeout(() => statusBadge.classList.add('hidden'), 300);
    }, 3500);
}

let isSidebarExpanded = false;
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const header = document.getElementById('sidebar-header');
    const texts = document.querySelectorAll('.sidebar-text');
    const btns = document.querySelectorAll('.sidebar-toggle-btn');
    isSidebarExpanded = !isSidebarExpanded;

    if (isSidebarExpanded) {
        sidebar.classList.replace('w-24', 'w-64');
        header.classList.replace('justify-center', 'justify-start');
        texts.forEach(t => { t.classList.remove('hidden', 'opacity-0'); t.classList.add('inline-block', 'opacity-100'); });
        btns.forEach(btn => { btn.classList.remove('w-12', 'mx-auto', 'justify-center'); btn.classList.add('w-full', 'justify-start', 'px-5'); });
    } else {
        sidebar.classList.replace('w-64', 'w-24');
        header.classList.replace('justify-start', 'justify-center');
        texts.forEach(t => { t.classList.remove('inline-block', 'opacity-100'); t.classList.add('hidden', 'opacity-0'); });
        btns.forEach(btn => { btn.classList.remove('w-full', 'justify-start', 'px-5'); btn.classList.add('w-12', 'mx-auto', 'justify-center'); });
    }
}

function switchTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.add('hidden'));
    document.getElementById(tabId).classList.remove('hidden');
    
    if (tabId === 'tab-profil') {
        document.getElementById('header-title').textContent = 'Profil Pribadi';
        document.getElementById('header-subtitle').textContent = 'Data Demografi & Organisasi';
    } else {
        document.getElementById('header-title').textContent = 'Manajemen Kehadiran';
        document.getElementById('header-subtitle').textContent = 'Riwayat Cuti & Perizinan';
    }

    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
        btn.classList.add('text-blue-200', 'hover:text-white', 'hover:bg-white/10');
    });
    
    const activeBtn = document.getElementById('btn-' + tabId);
    activeBtn.classList.remove('text-blue-200', 'hover:text-white', 'hover:bg-white/10');
    activeBtn.classList.add('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
}

function openModalCuti() {
    const modal = document.getElementById('modal-cuti');
    const container = document.getElementById('modal-container');
    modal.classList.remove('hidden');
    setTimeout(() => {
        container.classList.remove('scale-95', 'opacity-0');
        container.classList.add('scale-100', 'opacity-100');
    }, 10);
}

function closeModalCuti() {
    const modal = document.getElementById('modal-cuti');
    const container = document.getElementById('modal-container');
    container.classList.remove('scale-100', 'opacity-100');
    container.classList.add('scale-95', 'opacity-0');
    setTimeout(() => {
        modal.classList.add('hidden');
        document.getElementById('form-pengajuan-cuti').reset();
    }, 200);
}

// --- 3. LOGIKA DATA PROFIL & CUTI ---
async function fetchMyProfile() {
    try {
        const response = await fetch('/api/karyawan/me', {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        document.getElementById('loading-state').classList.add('hidden');

        if (response.ok) {
            const data = await response.json();
            
            // Menggunakan textContent yang aman dari XSS secara bawaan
            document.getElementById('profil-inisial').textContent = data.nama.substring(0, 2).toUpperCase();
            document.getElementById('profil-nama').textContent = data.nama;
            document.getElementById('profil-id').textContent = data.id;
            document.getElementById('profil-dept').textContent = data.departemen;
            document.getElementById('profil-gaji').textContent = formatRupiah(data.gaji);
            document.getElementById('header-badge-dept').textContent = `DIVISI: ${data.departemen.toUpperCase()}`;
            
            document.getElementById('main-content').classList.remove('hidden');
            fetchRiwayatCuti(); 
        } else {
            document.getElementById('error-state').classList.remove('hidden');
            if (response.status === 401 || response.status === 403) handleLogout();
        }
    } catch (error) {
        document.getElementById('loading-state').classList.add('hidden');
        document.getElementById('error-state').classList.remove('hidden');
    }
}

async function fetchRiwayatCuti() {
    try {
        const response = await fetch('/api/cuti/me', { headers: { 'Authorization': `Bearer ${token}` } });
        if (response.ok) {
            const data = await response.json();
            const tbody = document.getElementById('tabel-cuti');
            tbody.innerHTML = '';
            
            if(data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="4" class="px-6 py-8 text-center text-slate-500 font-bold">Tidak terdapat dokumen riwayat cuti.</td></tr>`;
            } else {
                data.forEach(c => {
                    // Menerapkan XSS Escaping pada Data Dinamis Cuti
                    const amanAlasan = escapeHTML(c.alasan);
                    const amanTglMulai = escapeHTML(c.tanggalMulai);
                    const amanTglSelesai = escapeHTML(c.tanggalSelesai);
                    const amanStatus = escapeHTML(c.status);

                    const statusStyle = amanStatus === 'DISETUJUI' ? 'bg-emerald-100/50 text-emerald-700 border-emerald-200' : (amanStatus === 'DITOLAK' ? 'bg-rose-100/50 text-rose-700 border-rose-200' : 'bg-amber-100/50 text-amber-700 border-amber-200');
                    tbody.innerHTML += `
                        <tr class="border-b border-white/40 hover:bg-white/40 transition-colors last:border-0">
                            <td class="px-6 py-5">${amanTglMulai}</td>
                            <td class="px-6 py-5">${amanTglSelesai}</td>
                            <td class="px-6 py-5 truncate max-w-[200px]" title="${amanAlasan}">${amanAlasan}</td>
                            <td class="px-6 py-5 text-right"><span class="px-4 py-2 rounded-xl text-[10px] border font-black tracking-widest uppercase shadow-sm ${statusStyle}">${amanStatus}</span></td>
                        </tr>
                    `;
                });
            }
        }
    } catch (error) { console.error('Gagal memuat cuti', error); }
}

const formCuti = document.getElementById('form-pengajuan-cuti');
if(formCuti) {
    formCuti.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const btnSubmit = document.getElementById('btn-submit-cuti');
        const originalText = btnSubmit.innerHTML;
        btnSubmit.innerHTML = 'Memproses...';
        btnSubmit.disabled = true;

        const payload = {
            tanggalMulai: document.getElementById('tgl-mulai').value,
            tanggalSelesai: document.getElementById('tgl-selesai').value,
            alasan: document.getElementById('alasan-cuti').value
        };

        try {
            const response = await fetch('/api/cuti', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                closeModalCuti();
                showStatus('Pengajuan izin berhasil dikirim.');
                fetchRiwayatCuti(); 
            } else {
                const err = await response.json();
                showStatus(err.error || 'Penolakan sistem: Periksa validitas tanggal.', true);
            }
        } catch(error) {
            showStatus('Koneksi terputus. Silakan coba lagi.', true);
        } finally {
            btnSubmit.innerHTML = originalText;
            btnSubmit.disabled = false;
        }
    });
}

fetchMyProfile();