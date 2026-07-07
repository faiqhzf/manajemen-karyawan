

// --- 1. AUTENTIKASI & UTILITAS ---
const token = sessionStorage.getItem('hris_token');
const role = sessionStorage.getItem('hris_role');

if (!token || role !== 'ROLE_HRD') {
    sessionStorage.clear();
    window.location.href = '/login.html';
}

// Utilitas Keamanan XSS
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

const API_URL = '/api/karyawan';
let globalKaryawanData = [];

// Elemen DOM
const form = document.getElementById('karyawan-form');
const tableBody = document.getElementById('table-body');
const cutiContainer = document.getElementById('cuti-feed-container');
const statusBadge = document.getElementById('status-badge');
const statusIcon = document.getElementById('status-icon');
const statusText = document.getElementById('status-text');
const searchInput = document.getElementById('search-input');
const filterDepartemen = document.getElementById('filter-departemen');
const sortData = document.getElementById('sort-data');

// --- 2. FUNGSI UI & INTERAKSI ---
function showStatus(message, isError = false) {
    statusText.textContent = message;
    if(isError) {
        statusBadge.className = "neu-icon fixed top-8 right-8 px-6 py-4 rounded-2xl text-sm font-black bg-rose-100 text-rose-800 z-[9999] transform transition-all flex items-center gap-3";
        statusIcon.innerHTML = `<svg class="w-6 h-6 shrink-0" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>`;
    } else {
        statusBadge.className = "neu-icon fixed top-8 right-8 px-6 py-4 rounded-2xl text-sm font-black bg-emerald-100 text-emerald-800 z-[9999] transform transition-all flex items-center gap-3";
        statusIcon.innerHTML = `<svg class="w-6 h-6 shrink-0" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>`;
    }
    statusBadge.classList.remove('hidden', 'translate-y-4', 'opacity-0');
    setTimeout(() => {
        statusBadge.classList.add('translate-y-4', 'opacity-0');
        setTimeout(() => statusBadge.classList.add('hidden'), 300);
    }, 3500);
}

let isLeftSidebarExpanded = false;
function toggleLeftSidebar() {
    const sidebar = document.getElementById('sidebar-left');
    const header = document.getElementById('sidebar-left-header');
    const texts = document.querySelectorAll('.sidebar-left-text');
    const btns = document.querySelectorAll('.sidebar-left-toggle-btn');
    isLeftSidebarExpanded = !isLeftSidebarExpanded;

    if (isLeftSidebarExpanded) {
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

let isRightSidebarExpanded = true;
function toggleRightSidebar() {
    const sidebar = document.getElementById('sidebar-right');
    const header = document.getElementById('sidebar-right-header');
    const texts = document.querySelectorAll('.sidebar-right-text');
    const feed = document.getElementById('cuti-feed-container');
    const arrow = document.getElementById('sidebar-right-arrow');
    const collapsedIndicator = document.getElementById('sidebar-right-collapsed');

    isRightSidebarExpanded = !isRightSidebarExpanded;

    if (isRightSidebarExpanded) {
        sidebar.classList.replace('w-24', 'w-[380px]');
        header.classList.remove('justify-center', 'px-0');
        header.classList.add('justify-between', 'px-8'); 
        texts.forEach(t => { t.classList.remove('hidden', 'opacity-0'); t.classList.add('block', 'opacity-100'); });
        feed.classList.remove('hidden', 'opacity-0');
        feed.classList.add('block', 'opacity-100');
        collapsedIndicator.classList.replace('flex', 'hidden');
        arrow.classList.remove('rotate-180');
    } else {
        sidebar.classList.replace('w-[380px]', 'w-24');
        header.classList.remove('justify-between', 'px-8');
        header.classList.add('justify-center', 'px-0');
        texts.forEach(t => { t.classList.remove('block', 'opacity-100'); t.classList.add('hidden', 'opacity-0'); });
        feed.classList.remove('block', 'opacity-100');
        feed.classList.add('hidden', 'opacity-0');
        collapsedIndicator.classList.replace('hidden', 'flex');
        arrow.classList.add('rotate-180'); 
    }
}

function switchTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.add('hidden'));
    document.getElementById(tabId).classList.remove('hidden');
    document.getElementById('header-title').textContent = tabId === 'tab-dashboard' ? 'Dasbor Analitik' : 'Direktori Karyawan';

    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
        btn.classList.add('text-indigo-300', 'hover:text-white', 'hover:bg-white/10');
    });
    const activeBtn = document.getElementById('btn-' + tabId);
    activeBtn.classList.remove('text-indigo-300', 'hover:text-white', 'hover:bg-white/10');
    activeBtn.classList.add('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
}

function openFormModal() {
    document.getElementById('form-modal').classList.remove('hidden');
    setTimeout(() => {
        const mc = document.getElementById('modal-container');
        mc.classList.remove('scale-95', 'opacity-0');
        mc.classList.add('scale-100', 'opacity-100');
    }, 10);
}

function closeFormModal() {
    const mc = document.getElementById('modal-container');
    mc.classList.remove('scale-100', 'opacity-100');
    mc.classList.add('scale-95', 'opacity-0');
    setTimeout(() => {
        document.getElementById('form-modal').classList.add('hidden');
        form.reset();
        document.getElementById('karyawan-id').value = '';
        document.getElementById('form-title').textContent = 'Tambah Pegawai';
    }, 200);
}

// --- 3. LOGIKA DATA KARYAWAN ---
async function fetchKaryawan() {
    try {
        const response = await fetch(API_URL, { headers: { 'Authorization': `Bearer ${token}` } });
        if (response.ok) {
            const data = await response.json();
            globalKaryawanData = data; 
            
            document.getElementById('metric-total-karyawan').textContent = data.length;
            document.getElementById('metric-total-dept').textContent = new Set(data.map(k => k.departemen)).size;
            document.getElementById('metric-total-gaji').textContent = formatRupiah(data.reduce((sum, k) => sum + k.gaji, 0));
            
            applyFiltersAndSort(); 
        }
    } catch (error) { showStatus('Gagal memuat data karyawan', true); }
}

function applyFiltersAndSort() {
    const keyword = searchInput.value.toLowerCase();
    const dept = filterDepartemen.value;
    const sortMethod = sortData.value;

    let result = globalKaryawanData.filter(k => {
        const matchName = k.nama.toLowerCase().includes(keyword); 
        const matchDept = dept === "" || k.departemen === dept;
        return matchName && matchDept;
    });

    result.sort((a, b) => {
        if (sortMethod === 'terbaru') return b.id - a.id;
        if (sortMethod === 'terlama') return a.id - b.id;
        if (sortMethod === 'gaji_tertinggi') return b.gaji - a.gaji;
        if (sortMethod === 'gaji_terendah') return a.gaji - b.gaji;
        return 0;
    });

    renderKaryawanTable(result);
}

if(searchInput) searchInput.addEventListener('input', applyFiltersAndSort);
if(filterDepartemen) filterDepartemen.addEventListener('change', applyFiltersAndSort);
if(sortData) sortData.addEventListener('change', applyFiltersAndSort);

function renderKaryawanTable(data) {
    tableBody.innerHTML = '';
    if (data.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="4" class="px-6 py-8 text-center text-slate-400 font-bold">Tidak ada data pegawai yang sesuai.</td></tr>`;
        return;
    }

    data.forEach(k => {
        // Menerapkan XSS Escaping pada Data Dinamis
        const amanNama = escapeHTML(k.nama);
        const amanDept = escapeHTML(k.departemen);
        const amanUser = escapeHTML(k.username || 'N/A');
        const initial = amanNama.substring(0, 2).toUpperCase();

        tableBody.innerHTML += `
            <tr class="hover:bg-white/50 transition-colors border-b border-white/30 last:border-0">
                <td class="px-6 py-5">
                    <div class="flex items-center gap-4">
                        <div class="neu-icon w-10 h-10 rounded-xl bg-white text-[#5c43d6] font-black text-xs flex items-center justify-center shrink-0 border-2 border-[#dce4f0]">${initial}</div>
                        <div>
                            <div class="font-black text-slate-800 text-sm">${amanNama}</div>
                            <div class="text-[10px] text-slate-500 uppercase tracking-widest mt-0.5">ID: #${k.id} &bull; @${amanUser}</div>
                        </div>
                    </div>
                </td>
                <td class="px-6 py-5"><span class="px-3 py-1.5 bg-white border border-white/50 text-slate-600 rounded-xl text-[10px] font-black tracking-widest uppercase shadow-sm">${amanDept}</span></td>
                <td class="px-6 py-5 text-slate-800 font-black">${formatRupiah(k.gaji)}</td>
                <td class="px-6 py-5 text-right space-x-2">
                    <button onclick="editKaryawan(${k.id}, '${amanNama.replace(/'/g, "\\'")}', '${amanDept.replace(/'/g, "\\'")}', ${k.gaji})" class="neu-button bg-white text-indigo-600 px-4 py-2.5 rounded-xl font-bold text-[10px] uppercase tracking-wider">Edit</button>
                    <button onclick="deleteKaryawan(${k.id})" class="neu-button bg-white text-rose-500 px-4 py-2.5 rounded-xl font-bold text-[10px] uppercase tracking-wider">Hapus</button>
                </td>
            </tr>
        `;
    });
}

if(form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('karyawan-id').value;
        const payload = {
            nama: document.getElementById('nama').value.trim(),
            departemen: document.getElementById('departemen').value,
            gaji: parseFloat(document.getElementById('gaji').value),
            username: document.getElementById('username').value.trim(),
            password: document.getElementById('password').value
        };
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_URL}/${id}` : API_URL;

        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(payload) });
            if (res.ok) {
                showStatus('Data berhasil disinkronisasi');
                closeFormModal();
                fetchKaryawan(); 
            } else {
                showStatus('Gagal. Periksa kemungkinan username ganda.', true);
            }
        } catch (error) { showStatus('Gagal memproses permintaan', true); }
    });
}

function editKaryawan(id, nama, departemen, gaji) {
    document.getElementById('karyawan-id').value = id;
    document.getElementById('nama').value = nama;
    document.getElementById('departemen').value = departemen;
    document.getElementById('gaji').value = gaji;
    document.getElementById('form-title').textContent = `Edit Pegawai #${id}`;
    openFormModal();
}

async function deleteKaryawan(id) {
    if (!confirm(`Hapus data #${id}?`)) return;
    try {
        const res = await fetch(`${API_URL}/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` }});
        if (res.ok) { fetchKaryawan(); showStatus('Data berhasil dihapus'); }
    } catch (error) { showStatus('Penghapusan gagal.', true) }
}

// --- 4. LOGIKA DATA CUTI ---
async function fetchSemuaCuti() {
    try {
        const response = await fetch('/api/cuti', { headers: { 'Authorization': `Bearer ${token}` } });
        if (response.ok) {
            const data = await response.json();
            cutiContainer.innerHTML = '';
            let pendingCount = 0;

            if (data.length === 0) {
                cutiContainer.innerHTML = `<div class="text-center p-6 text-slate-400 font-bold text-sm">Tidak ada pesan masuk.</div>`;
                return;
            }

            data.sort((a, b) => a.status === 'MENUNGGU' ? -1 : 1).forEach(c => {
                // Menerapkan XSS Escaping pada Data Dinamis Cuti
                const amanUser = escapeHTML(c.usernameKaryawan);
                const amanAlasan = escapeHTML(c.alasan);
                const amanTglMulai = escapeHTML(c.tanggalMulai);
                const amanTglSelesai = escapeHTML(c.tanggalSelesai);
                const amanStatus = escapeHTML(c.status);

                const isPending = amanStatus === 'MENUNGGU';
                if (isPending) pendingCount++;

                const statusStyle = isPending 
                    ? 'bg-amber-100 text-amber-700' 
                    : (amanStatus === 'DISETUJUI' ? 'bg-emerald-100 text-emerald-700' : 'bg-rose-100 text-rose-700');
                
                const actionHTML = isPending ? `
                    <div class="flex gap-2 mt-4 pt-3 border-t border-slate-100/50">
                        <button onclick="prosesCuti(${c.id}, 'DISETUJUI')" class="neu-button flex-1 bg-[#5c43d6] text-white text-[10px] font-black uppercase tracking-widest py-2.5 rounded-xl">Setuju</button>
                        <button onclick="prosesCuti(${c.id}, 'DITOLAK')" class="neu-button flex-1 bg-white text-slate-500 text-[10px] font-black uppercase tracking-widest py-2.5 rounded-xl">Tolak</button>
                    </div>
                ` : '';

                cutiContainer.innerHTML += `
                    <div class="inner-glass-card rounded-3xl p-5 mb-5">
                        <div class="flex justify-between items-start mb-3">
                            <div class="flex items-center gap-3">
                                <div class="neu-icon w-8 h-8 rounded-full bg-white text-indigo-600 flex items-center justify-center font-black text-xs shrink-0 border border-[#dce4f0]">@</div>
                                <div>
                                    <p class="font-black text-slate-800 text-sm leading-none">${amanUser}</p>
                                    <span class="text-[9px] text-slate-500 font-bold uppercase tracking-widest">${amanTglMulai}</span>
                                </div>
                            </div>
                            <span class="px-2.5 py-1 rounded-lg text-[9px] font-black tracking-widest uppercase ${statusStyle} shrink-0">${amanStatus}</span>
                        </div>
                        <p class="text-xs font-bold text-slate-600 neu-pressed p-4 rounded-xl line-clamp-3 leading-relaxed">"${amanAlasan}"</p>
                        <div class="mt-4 mb-1 text-[9px] font-black text-slate-400 uppercase tracking-widest text-center">
                            S/D TANGGAL: <span class="text-slate-600">${amanTglSelesai}</span>
                        </div>
                        ${actionHTML}
                    </div>
                `;
            });

            document.getElementById('badge-cuti-pending').textContent = pendingCount + ' Baru';
            document.getElementById('badge-cuti-pending-mini').textContent = pendingCount;
        }
    } catch (error) {}
}

async function prosesCuti(id, status) {
    try {
        const res = await fetch(`/api/cuti/${id}/status`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: status })
        });
        if (res.ok) {
            showStatus('Respon pesan terkirim.');
            fetchSemuaCuti(); 
        } else {
            showStatus('Gagal merespon cuti', true);
        }
    } catch (error) {
        showStatus('Terjadi kegagalan jaringan', true);
    }
}


fetchKaryawan();
fetchSemuaCuti();