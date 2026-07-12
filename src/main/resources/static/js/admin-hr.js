// --- 1. AUTENTIKASI & UTILITAS ---
const token = sessionStorage.getItem('hris_token');
const role = sessionStorage.getItem('hris_role');

if (!token || role !== 'ROLE_HRD') {
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
const formJadwal = document.getElementById('form-generate-jadwal');

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
    
    if (tabId === 'tab-dashboard') document.getElementById('header-title').textContent = 'Dasbor Analitik';
    else if (tabId === 'tab-karyawan') document.getElementById('header-title').textContent = 'Direktori Karyawan';
    else document.getElementById('header-title').textContent = 'Manajemen Jadwal';

    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
        btn.classList.add('text-indigo-300', 'hover:text-white', 'hover:bg-white/10');
    });
    const activeBtn = document.getElementById('btn-' + tabId);
    activeBtn.classList.remove('text-indigo-300', 'hover:text-white', 'hover:bg-white/10');
    activeBtn.classList.add('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
}

// FUNGSI MANAJEMEN MODAL (TAMBAH/EDIT & DETAIL)
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

function closeDetailModal() {
    const mc = document.getElementById('modal-container-detail');
    mc.classList.remove('scale-100', 'opacity-100');
    mc.classList.add('scale-95', 'opacity-0');
    setTimeout(() => {
        document.getElementById('detail-modal').classList.add('hidden');
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
        const amanNama = escapeHTML(k.nama);
        const amanDept = escapeHTML(k.departemen);

        const amanUser = escapeHTML(k.username || 'N/A');
        const initial = amanNama.substring(0, 2).toUpperCase();

        const hasPhoto = k.fotoUrl && k.fotoUrl.trim() !== "";
        const avatarContent = hasPhoto 
            ? `<img src="${k.fotoUrl}" class="w-full h-full object-cover">` 
            : initial;
        const avatarStyle = hasPhoto 
            ? "border-0" 
            : "border-2 border-[#dce4f0] text-[#5c43d6] font-black text-xs";

        tableBody.innerHTML += `
            <tr class="hover:bg-white/50 transition-colors border-b border-white/30 last:border-0 relative z-10">
                <td class="px-6 py-5">
                    <div class="flex items-center gap-4">
                        <div class="neu-icon w-10 h-10 rounded-xl bg-white flex items-center justify-center shrink-0 overflow-hidden ${avatarStyle}">
                            ${avatarContent}
                        </div>
                        <div>
                            <div class="font-black text-slate-800 text-sm">${amanNama}</div>
                            <!-- PERBAIKAN: Memisahkan ID yang uppercase, dengan Username yang dibiarkan natural (normal-case) -->
                            <div class="text-[10px] text-slate-500 tracking-widest mt-0.5"><span class="uppercase">ID: #${k.id}</span> &bull; <span class="normal-case">@${amanUser}</span></div>
                        </div>
                    </div>
                </td>
                <td class="px-6 py-5"><span class="px-3 py-1.5 bg-white border border-white/50 text-slate-600 rounded-xl text-[10px] font-black tracking-widest uppercase shadow-sm">${amanDept}</span></td>
                <td class="px-6 py-5 text-slate-800 font-black">${formatRupiah(k.gaji)}</td>
                <td class="px-6 py-5 text-right space-x-2 relative z-20">
                    <button onclick="viewKaryawan(${k.id})" class="neu-button bg-white text-emerald-600 px-4 py-2.5 rounded-xl font-bold text-[10px] uppercase tracking-wider cursor-pointer">Detail</button>
                    <button onclick="editKaryawan(${k.id})" class="neu-button bg-white text-indigo-600 px-4 py-2.5 rounded-xl font-bold text-[10px] uppercase tracking-wider cursor-pointer">Edit</button>
                    <button onclick="deleteKaryawan(${k.id})" class="neu-button bg-white text-rose-500 px-4 py-2.5 rounded-xl font-bold text-[10px] uppercase tracking-wider cursor-pointer">Hapus</button>
                </td>
            </tr>
        `;
    });
}

function viewKaryawan(id) {
    const k = globalKaryawanData.find(emp => emp.id === id);
    if (!k) return;

    document.getElementById('detail-id').textContent = k.id;
    document.getElementById('detail-nama').textContent = k.nama;
    document.getElementById('detail-username').textContent = k.username || 'N/A';
    document.getElementById('detail-dept').textContent = k.departemen;
    document.getElementById('detail-telepon').textContent = k.noTelepon || 'Belum diatur';
    document.getElementById('detail-gaji').textContent = formatRupiah(k.gaji);

    const imgEl = document.getElementById('detail-foto');
    const initialEl = document.getElementById('detail-inisial');

    if (k.fotoUrl) {
        imgEl.src = k.fotoUrl;
        imgEl.classList.remove('hidden');
        initialEl.classList.add('hidden');
    } else {
        imgEl.classList.add('hidden');
        initialEl.classList.remove('hidden');
        initialEl.textContent = k.nama.substring(0, 2).toUpperCase();
    }

    document.getElementById('detail-modal').classList.remove('hidden');
    setTimeout(() => {
        const mc = document.getElementById('modal-container-detail');
        mc.classList.remove('scale-95', 'opacity-0');
        mc.classList.add('scale-100', 'opacity-100');
    }, 10);
}

function editKaryawan(id) {
    const k = globalKaryawanData.find(emp => emp.id === id);
    if (!k) return;

    document.getElementById('karyawan-id').value = k.id;
    document.getElementById('nama').value = k.nama;
    document.getElementById('departemen').value = k.departemen;
    document.getElementById('gaji').value = k.gaji;
    document.getElementById('username').value = k.username || '';
    document.getElementById('password').value = ''; 
    document.getElementById('kuota-cuti').value = k.kuotaCuti !== undefined ? k.kuotaCuti : 12;
    
    document.getElementById('form-title').textContent = `Edit Pegawai #${k.id}`;
    openFormModal();
}

if(form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('karyawan-id').value;
        const inputUsername = document.getElementById('username');
        const saranContainer = document.getElementById('saran-username');
        
        const payload = {
            nama: document.getElementById('nama').value.trim(),
            departemen: document.getElementById('departemen').value,
            gaji: parseFloat(document.getElementById('gaji').value),
            kuotaCuti: parseInt(document.getElementById('kuota-cuti').value),
            username: inputUsername.value.trim(),
            password: document.getElementById('password').value
        };
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_URL}/${id}` : API_URL;

        try {
            const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }, body: JSON.stringify(payload) });
            if (res.ok) {
                showStatus('Data berhasil disinkronisasi');
                saranContainer.classList.add('hidden'); // Sembunyikan saran jika berhasil
                closeFormModal();
                fetchKaryawan(); 
            } else {
                showStatus('Username sudah terpakai.', true);
                
                // LOGIKA GENERATOR SARAN USERNAME
                const baseName = payload.username.toLowerCase().replace(/[^a-z0-9]/g, ''); // Bersihkan karakter aneh
                const randNum = Math.floor(100 + Math.random() * 900); // 3 digit acak
                const deptLower = payload.departemen.toLowerCase();
                
                // Buat 3 opsi username alternatif
                const saran1 = `${baseName}${randNum}`;
                const saran2 = `${baseName}_${deptLower}`;
                const saran3 = `${baseName}.${new Date().getFullYear()}`;

                saranContainer.innerHTML = `
                    <span class="w-full text-[9px] font-bold text-rose-500">Coba gunakan rekomendasi ini:</span>
                    <button type="button" onclick="document.getElementById('username').value='${saran1}'" class="px-2 py-1 bg-indigo-100 text-indigo-700 text-[10px] font-black rounded-md hover:bg-indigo-200">${saran1}</button>
                    <button type="button" onclick="document.getElementById('username').value='${saran2}'" class="px-2 py-1 bg-indigo-100 text-indigo-700 text-[10px] font-black rounded-md hover:bg-indigo-200">${saran2}</button>
                    <button type="button" onclick="document.getElementById('username').value='${saran3}'" class="px-2 py-1 bg-indigo-100 text-indigo-700 text-[10px] font-black rounded-md hover:bg-indigo-200">${saran3}</button>
                `;
                saranContainer.classList.remove('hidden');
            }
        } catch (error) { showStatus('Gagal memproses permintaan', true); }
    });
}
async function deleteKaryawan(id) {
    if (!confirm(`Tindakan ini tidak bisa dibatalkan! Hapus data pegawai #${id}?`)) return;
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

            // PERBAIKAN: Penyortiran Ganda (Menunggu di atas -> Terbaru di atas)
            data.sort((a, b) => {
                if (a.status === 'MENUNGGU' && b.status !== 'MENUNGGU') return -1;
                if (a.status !== 'MENUNGGU' && b.status === 'MENUNGGU') return 1;
                return b.id - a.id; // Urutkan dari ID terbesar (terbaru) ke terkecil
            }).forEach(c => {
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
                        <button onclick="prosesCuti(${c.id}, 'DISETUJUI')" class="neu-button flex-1 bg-[#5c43d6] text-white text-[10px] font-black uppercase tracking-widest py-2.5 rounded-xl cursor-pointer">Setuju</button>
                        <button onclick="prosesCuti(${c.id}, 'DITOLAK')" class="neu-button flex-1 bg-white text-slate-500 text-[10px] font-black uppercase tracking-widest py-2.5 rounded-xl cursor-pointer">Tolak</button>
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
            const err = await res.json();
            showStatus(err.message || err.error || 'Validasi gagal memproses cuti.', true);
        }
    } catch (error) {
        showStatus('Terjadi kegagalan jaringan', true);
    }
}

// --- 5. LOGIKA GENERATE JADWAL ---
if(formJadwal) {
    formJadwal.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('btn-generate-jadwal');
        btn.innerHTML = 'Memproses...';
        btn.disabled = true;

        const tanggalMulai = document.getElementById('tgl-mulai-jadwal').value;

        try {
            const res = await fetch('/api/jadwal/generate', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify({ tanggalMulai: tanggalMulai })
            });

            if (res.ok) {
                showStatus('Jadwal mingguan berhasil didistribusikan ke seluruh pegawai.');
                formJadwal.reset();
            } else {
                showStatus('Gagal memproses jadwal.', true);
            }
        } catch (error) {
            showStatus('Terjadi kesalahan jaringan', true);
        } finally {
            btn.innerHTML = 'Generate Jadwal Massal';
            btn.disabled = false;
        }
    });
}

// Eksekusi Panggilan Awal
fetchKaryawan();
fetchSemuaCuti();

// --- 6. LOGIKA PEMANTAUAN ABSENSI HARIAN ---
async function fetchAbsensiHarian() {
    try {
        // Mengambil jadwal untuk hari ini (server time)
        const res = await fetch('/api/jadwal/harian', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (res.ok) {
            const data = await res.json();
            const tbody = document.getElementById('tabel-absensi-harian');
            tbody.innerHTML = '';
            
            // Set Tanggal Header UI
            const today = new Date().toLocaleDateString('id-ID', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
            document.getElementById('label-tanggal-absensi').textContent = `LIVE HARI INI: ${today}`;

            if (data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="5" class="px-6 py-8 text-center text-slate-400 font-bold">Jadwal belum didistribusikan untuk hari ini.</td></tr>`;
                return;
            }

            let countHadir = 0, countTelat = 0, countKosong = 0;

            data.forEach(j => {
                const safeUser = escapeHTML(j.usernameKaryawan);
                const shift = `${j.jamMasukShift || '-'} s/d ${j.jamPulangShift || '-'}`;
                const inTime = j.waktuCheckIn ? j.waktuCheckIn.substring(0, 8) : '<span class="text-slate-300">Belum</span>';
                const outTime = j.waktuCheckOut ? j.waktuCheckOut.substring(0, 8) : '<span class="text-slate-300">Belum</span>';
                const status = j.status;

                let statusStyle = '';
                if (status === 'HADIR') { statusStyle = 'bg-emerald-100 text-emerald-700 border-emerald-200'; countHadir++; }
                else if (status === 'TERLAMBAT') { statusStyle = 'bg-amber-100 text-amber-700 border-amber-200'; countTelat++; }
                else { statusStyle = 'bg-slate-100 text-slate-500 border-slate-200'; countKosong++; }

                tbody.innerHTML += `
                    <tr class="hover:bg-white/50 transition-colors border-b border-white/30 last:border-0">
                        <td class="px-6 py-4 font-black text-indigo-700">@${safeUser}</td>
                        <td class="px-6 py-4 text-xs">${shift}</td>
                        <td class="px-6 py-4 font-black">${inTime}</td>
                        <td class="px-6 py-4 font-black">${outTime}</td>
                        <td class="px-6 py-4 text-right">
                            <span class="px-3 py-1.5 rounded-lg text-[9px] font-black tracking-widest uppercase border shadow-sm ${statusStyle}">
                                ${status.replace('_', ' ')}
                            </span>
                        </td>
                    </tr>
                `;
            });

            // Update Statistik Dashboard
            document.getElementById('stat-hadir').textContent = countHadir;
            document.getElementById('stat-telat').textContent = countTelat;
            document.getElementById('stat-kosong').textContent = countKosong;
        }
    } catch (error) {
        console.error("Gagal memuat data absensi harian", error);
    }
}

// Tambahkan pemanggilan fetchAbsensiHarian() di fungsi switchTab agar data diperbarui (refresh) setiap kali HRD membuka tab Jadwal
const originalSwitchTabAdmin = switchTab;
switchTab = function(tabId) {
    originalSwitchTabAdmin(tabId);
    if (tabId === 'tab-jadwal') {
        fetchAbsensiHarian();
    }
};

// ==========================================
// --- 7. KALENDER MASTER & INTERVENSI JADWAL ---
// ==========================================
let adminCalendar;

async function initAdminCalendar() {
    const calendarEl = document.getElementById('admin-calendar');
    if (!calendarEl) return;

    adminCalendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: { left: 'prev,next today', center: 'title', right: 'dayGridMonth,timeGridWeek,listWeek' },
        height: 'auto',
        themeSystem: 'standard',
        events: async function(info, successCallback, failureCallback) {
            try {
                const response = await fetch('/api/jadwal/semua', { headers: { 'Authorization': `Bearer ${token}` } });
                if (response.ok) {
                    const data = await response.json();
                    
                    const events = data.map(j => ({
                        id: j.id,
                        title: `@${j.usernameKaryawan} (${j.jamMasukShift.substring(0,5)})`,
                        start: `${j.tanggal}T${j.jamMasukShift}`,
                        end: `${j.tanggal}T${j.jamPulangShift}`,
                        backgroundColor: j.status === 'HADIR' ? '#10b981' : (j.status === 'BELUM_MULAI' ? '#3b82f6' : '#f59e0b'),
                        borderColor: 'transparent',
                        extendedProps: {
                            username: j.usernameKaryawan,
                            tanggal: j.tanggal,
                            jamMasuk: j.jamMasukShift,
                            jamPulang: j.jamPulangShift,
                            status: j.status
                        }
                    }));
                    successCallback(events);
                } else {
                    successCallback([]);
                }
            } catch (error) { failureCallback(error); }
        },
        eventClick: function(info) {
            // Logika saat HRD mengklik jadwal seseorang
            const props = info.event.extendedProps;
            document.getElementById('intervensi-id').value = info.event.id;
            document.getElementById('intervensi-nama').textContent = `@${props.username}`;
            document.getElementById('intervensi-tgl').textContent = `Tanggal: ${props.tanggal}`;
            document.getElementById('intervensi-masuk').value = props.jamMasuk;
            document.getElementById('intervensi-pulang').value = props.jamPulang;
            document.getElementById('intervensi-status').value = props.status;
            
            document.getElementById('modal-intervensi').classList.remove('hidden');
            setTimeout(() => {
                const mc = document.getElementById('container-intervensi');
                mc.classList.remove('scale-95', 'opacity-0');
                mc.classList.add('scale-100', 'opacity-100');
            }, 10);
        }
    });
    adminCalendar.render();
}

function closeModalIntervensi() {
    const mc = document.getElementById('container-intervensi');
    mc.classList.remove('scale-100', 'opacity-100');
    mc.classList.add('scale-95', 'opacity-0');
    setTimeout(() => {
        document.getElementById('modal-intervensi').classList.add('hidden');
    }, 200);
}

// Logika Simpan Intervensi
const formIntervensi = document.getElementById('form-intervensi');
if(formIntervensi) {
    formIntervensi.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('intervensi-id').value;
        const payload = {
            jamMasuk: document.getElementById('intervensi-masuk').value + ":00", // Format HH:mm:ss
            jamPulang: document.getElementById('intervensi-pulang').value + ":00",
            status: document.getElementById('intervensi-status').value
        };

        try {
            const res = await fetch(`/api/jadwal/${id}/intervensi`, {
                method: 'PUT',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showStatus('Jadwal berhasil dimodifikasi.');
                closeModalIntervensi();
                if(adminCalendar) adminCalendar.refetchEvents();
                fetchAbsensiHarian(); // Refresh tabel live monitoring
            } else { showStatus('Gagal memodifikasi jadwal.', true); }
        } catch (error) { showStatus('Terjadi kegagalan jaringan', true); }
    });
}

// Logika Hapus Jadwal
async function hapusJadwalSpesifik() {
    const id = document.getElementById('intervensi-id').value;
    if (!confirm('Hapus jadwal shift ini secara permanen?')) return;
    
    try {
        const res = await fetch(`/api/jadwal/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
        if (res.ok) {
            showStatus('Jadwal berhasil dihapus (Karyawan Diliburkan).');
            closeModalIntervensi();
            if(adminCalendar) adminCalendar.refetchEvents();
            fetchAbsensiHarian();
        } else { showStatus('Gagal menghapus jadwal.', true); }
    } catch (error) { showStatus('Terjadi kegagalan jaringan', true); }
}

// Panggil initAdminCalendar saat tab jadwal dibuka
const superSwitchTabAdmin = switchTab;
switchTab = function(tabId) {
    superSwitchTabAdmin(tabId);
    if (tabId === 'tab-jadwal') {
        if(!adminCalendar) initAdminCalendar();
        else adminCalendar.render();
    }
};