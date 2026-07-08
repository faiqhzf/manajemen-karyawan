// ==========================================
// --- 1. AUTENTIKASI & UTILITAS ---
// ==========================================
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

// ==========================================
// --- 2. FUNGSI UI & INTERAKSI ---
// ==========================================
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
    } else if (tabId === 'tab-cuti') {
        document.getElementById('header-title').textContent = 'Manajemen Kehadiran';
        document.getElementById('header-subtitle').textContent = 'Riwayat Cuti & Perizinan';
    } else {
        document.getElementById('header-title').textContent = 'Absensi Harian';
        document.getElementById('header-subtitle').textContent = 'Perekaman Lokasi & Waktu';
    }

    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
        btn.classList.add('text-blue-200', 'hover:text-white', 'hover:bg-white/10');
    });
    
    const activeBtn = document.getElementById('btn-' + tabId);
    activeBtn.classList.remove('text-blue-200', 'hover:text-white', 'hover:bg-white/10');
    activeBtn.classList.add('text-white', 'bg-white/20', 'shadow-inner', 'border', 'border-white/10');
}

function openModal(modalId, containerId) {
    document.getElementById(modalId).classList.remove('hidden');
    setTimeout(() => {
        const mc = document.getElementById(containerId);
        mc.classList.remove('scale-95', 'opacity-0');
        mc.classList.add('scale-100', 'opacity-100');
    }, 10);
}

function closeModal(modalId, containerId, formId) {
    const mc = document.getElementById(containerId);
    mc.classList.remove('scale-100', 'opacity-100');
    mc.classList.add('scale-95', 'opacity-0');
    setTimeout(() => {
        document.getElementById(modalId).classList.add('hidden');
        if(formId) document.getElementById(formId).reset();
    }, 200);
}

const openModalCuti = () => openModal('modal-cuti', 'modal-container-cuti');
const closeModalCuti = () => closeModal('modal-cuti', 'modal-container-cuti', 'form-pengajuan-cuti');

const openModalEditProfil = () => openModal('modal-edit-profil', 'modal-container-profil');
const closeModalEditProfil = () => closeModal('modal-edit-profil', 'modal-container-profil', 'form-edit-profil');

const openModalPassword = () => openModal('modal-password', 'modal-container-password');
const closeModalPassword = () => closeModal('modal-password', 'modal-container-password', 'form-ganti-password');

const openModalUploadFoto = () => openModal('modal-foto', 'modal-container-foto');
const closeModalUploadFoto = () => closeModal('modal-foto', 'modal-container-foto', 'form-upload-foto');

// ==========================================
// --- 3. LOGIKA DATA PROFIL & CUTI ---
// ==========================================
async function fetchMyProfile() {
    try {
        const response = await fetch('/api/karyawan/me', {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        document.getElementById('loading-state').classList.add('hidden');

        if (response.ok) {
            const data = await response.json();
            
            document.getElementById('profil-inisial').textContent = data.nama.substring(0, 2).toUpperCase();
            document.getElementById('profil-nama').textContent = data.nama;
            document.getElementById('profil-id').textContent = data.id;
            document.getElementById('profil-dept').textContent = data.departemen;
            document.getElementById('profil-gaji').textContent = formatRupiah(data.gaji);
            document.getElementById('profil-telepon').textContent = data.noTelepon ? escapeHTML(data.noTelepon) : "Belum diatur";
            document.getElementById('header-badge-dept').textContent = `DIVISI: ${data.departemen.toUpperCase()}`;
            
            const imgEl = document.getElementById('profil-foto');
            if (data.fotoUrl) {
                imgEl.src = data.fotoUrl;
                imgEl.classList.remove('hidden');
                document.getElementById('profil-inisial').classList.add('hidden');
            }
            
            document.getElementById('edit-nama').value = data.nama;
            document.getElementById('edit-telepon').value = data.noTelepon || '';

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
    } catch (error) {}
}

// ==========================================
// --- 4. LISTENER FORMULIR PROFIL & CUTI ---
// ==========================================

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
                showStatus(err.error || 'Penolakan sistem: Periksa validitas.', true);
            }
        } catch(error) { showStatus('Koneksi terputus.', true); } 
        finally { btnSubmit.innerHTML = originalText; btnSubmit.disabled = false; }
    });
}

const formEditProfil = document.getElementById('form-edit-profil');
if(formEditProfil) {
    formEditProfil.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            nama: document.getElementById('edit-nama').value,
            noTelepon: document.getElementById('edit-telepon').value
        };

        try {
            const response = await fetch('/api/karyawan/me', {
                method: 'PUT',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (response.ok) {
                closeModalEditProfil();
                showStatus('Profil berhasil diperbarui.');
                fetchMyProfile();
            } else { showStatus('Gagal memperbarui profil.', true); }
        } catch(error) { showStatus('Terjadi kesalahan jaringan.', true); }
    });
}

const formGantiPassword = document.getElementById('form-ganti-password');
if(formGantiPassword) {
    formGantiPassword.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            passwordLama: document.getElementById('pass-lama').value,
            passwordBaru: document.getElementById('pass-baru').value
        };

        try {
            const response = await fetch('/api/karyawan/me/password', {
                method: 'PUT',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (response.ok) {
                closeModalPassword();
                showStatus('Kata sandi berhasil diubah.');
            } else { showStatus('Kata sandi lama tidak valid.', true); }
        } catch(error) { showStatus('Terjadi kesalahan jaringan.', true); }
    });
}

const formUploadFoto = document.getElementById('form-upload-foto');
if(formUploadFoto) {
    formUploadFoto.addEventListener('submit', async (e) => {
        e.preventDefault();
        const fileInput = document.getElementById('file-foto');
        if (!fileInput.files[0]) return;

        const formData = new FormData();
        formData.append('file', fileInput.files[0]);

        try {
            const response = await fetch('/api/karyawan/me/foto', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` },
                body: formData
            });
            if (response.ok) {
                closeModalUploadFoto();
                showStatus('Foto profil berhasil diperbarui.');
                fetchMyProfile();
            } else { showStatus('Gagal mengunggah foto.', true); }
        } catch(error) { showStatus('Terjadi kesalahan saat mengunggah.', true); }
    });
}

// ==========================================
// --- 5. LOGIKA KALENDER ABSENSI & GPS ---
// ==========================================
let calendar;
let globalJadwalHariIni = null; 

// ==========================================
// --- 6. LOGIKA "KERJA KERJA KERJA" (GEOFENCING & AUDIO) ---
// ==========================================
const DEMO_MODE = true; // Ubah ke 'false' jika sudah di-deploy ke server rill
const KOORDINAT_KANTOR = { lat: -7.027, lng: 107.630 }; 
const BATAS_RADIUS_METER = 100;
let kuotaCutiTersisa = 0; 

function cekKeterlambatanMendadak() {
    if (!globalJadwalHariIni || globalJadwalHariIni.status !== 'BELUM_MULAI') return;

    const modalKerja = document.getElementById('modal-kerja');
    if (!modalKerja.classList.contains('hidden')) return;

    const now = new Date();
    const currentWaktu = now.getHours() * 60 + now.getMinutes(); 
    
    const shiftParts = globalJadwalHariIni.jamMasukShift.split(':');
    const shiftWaktu = parseInt(shiftParts[0]) * 60 + parseInt(shiftParts[1]);

    if (currentWaktu > shiftWaktu) {
        modalKerja.classList.remove('hidden');
        document.getElementById('audio-kerja').play().catch(e => console.log("Autoplay butuh interaksi klik"));
    }
}

function initCalendar() {
    const calendarEl = document.getElementById('calendar');
    if (!calendarEl) return;

    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: { left: 'prev,next today', center: 'title', right: 'dayGridMonth,timeGridWeek' },
        height: 'auto',
        events: async function(info, successCallback, failureCallback) {
            try {
                const response = await fetch('/api/jadwal/me', { headers: { 'Authorization': `Bearer ${token}` } });
                if (response.ok) {
                    const data = await response.json();
                    
                    const localDate = new Date();
                    const year = localDate.getFullYear();
                    const month = String(localDate.getMonth() + 1).padStart(2, '0');
                    const day = String(localDate.getDate()).padStart(2, '0');
                    const todayStr = `${year}-${month}-${day}`;
                    
                    globalJadwalHariIni = data.find(j => j.tanggal === todayStr);

                    const events = data.map(j => ({
                        id: j.id,
                        title: j.status === 'BELUM_MULAI' ? `Shift: ${j.jamMasukShift}` : j.status,
                        start: `${j.tanggal}T${j.jamMasukShift}`,
                        end: `${j.tanggal}T${j.jamPulangShift}`,
                        backgroundColor: j.status === 'HADIR' ? '#10b981' : (j.status === 'TERLAMBAT' ? '#f59e0b' : '#3b82f6'),
                        borderColor: 'transparent'
                    }));
                    successCallback(events);
                } else {
                    successCallback([]);
                }
            } catch (error) { failureCallback(error); }
        }
    });
    calendar.render();
};

const originalSwitchTab = switchTab;
switchTab = function(tabId) {
    originalSwitchTab(tabId); 
    if (tabId === 'tab-absensi') {
        setTimeout(() => { if(calendar) calendar.render(); }, 100); 
    }
};

setInterval(() => {
    const now = new Date();
    const clockEl = document.getElementById('realtime-clock');
    if(clockEl) clockEl.textContent = now.toLocaleTimeString('id-ID', { hour12: false });

    // Cek keterlambatan di latar belakang setiap 1 detik
    cekKeterlambatanMendadak();
}, 1000);

function hitungJarakBumi(lat1, lon1, lat2, lon2) {
    const R = 6371e3; 
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c; 
}

function verifikasiHadirKerja() {
    if (!navigator.geolocation) {
        alert("Browser Anda tidak mendukung pelacakan lokasi.");
        return;
    }

    document.getElementById('kerja-teks').textContent = "Melacak kordinat satelit Anda...";

    navigator.geolocation.getCurrentPosition(
        async (position) => {
            const latKaryawan = position.coords.latitude;
            const lngKaryawan = position.coords.longitude;
            
            let jarakMeter = hitungJarakBumi(latKaryawan, lngKaryawan, KOORDINAT_KANTOR.lat, KOORDINAT_KANTOR.lng);

            if (DEMO_MODE) jarakMeter = 0; 

            if (jarakMeter <= BATAS_RADIUS_METER) {
                document.getElementById('audio-kerja').pause();
                document.getElementById('audio-kerja').currentTime = 0;
                document.getElementById('audio-ketawa').pause();
                document.getElementById('audio-ketawa').currentTime = 0;

                document.getElementById('modal-kerja').classList.add('hidden');
                
                if(globalJadwalHariIni) globalJadwalHariIni.status = 'DIPROSES'; 
                lakukanClockIn(position.coords); 
            } else {
                document.getElementById('kerja-teks').innerHTML = `<span class="text-rose-600 font-black">PENOLAKAN:</span> Jarak Anda <b>${Math.round(jarakMeter)} Meter</b> dari kantor. Segera masuk area kantor untuk menekan tombol Hadir!`;
            }
        },
        (error) => {
            document.getElementById('kerja-teks').innerHTML = `<span class="text-rose-600 font-black">AKSES DITOLAK:</span> Anda wajib mengizinkan akses Lokasi (GPS) di browser.`;
        },
        { enableHighAccuracy: true, timeout: 10000 }
    );
}

function ajukanIzinMendadak(jenis) {
    if (kuotaCutiTersisa > 0) {
        // Interupsi absolut untuk seluruh saluran audio
        document.getElementById('audio-kerja').pause();
        document.getElementById('audio-kerja').currentTime = 0;
        document.getElementById('audio-ketawa').pause();
        document.getElementById('audio-ketawa').currentTime = 0;

        document.getElementById('modal-kerja').classList.add('hidden');
        openModalCuti();
        document.getElementById('alasan-cuti').value = `Pengajuan ${jenis} Mendadak`;
        if(globalJadwalHariIni) globalJadwalHariIni.status = 'DIPROSES'; 
    } else {
        document.getElementById('audio-kerja').pause();
        

        const audioKetawa = document.getElementById('audio-ketawa');
        audioKetawa.currentTime = 0; 
        audioKetawa.play().catch(e => {});

        document.getElementById('kerja-judul').textContent = "KUOTA CUTI HABIS!";
        document.getElementById('gif-kerja').src = "/images/cat-laugh.gif";
        document.getElementById('kerja-teks').innerHTML = `Anda sudah tidak memiliki jatah kuota Izin/Cuti tahunan. <br><br><b>Pilihannya hanya satu: BERANGKAT KERJA SEKARANG!</b>`;
    }
}

async function lakukanClockIn(coords) {
    const btn = document.getElementById('btn-clockin');
    btn.innerHTML = 'Menyimpan...';
    btn.disabled = true;

    if (!coords) {
        if (!navigator.geolocation) return showStatus('GPS tidak didukung.', true);
        navigator.geolocation.getCurrentPosition(
            async (pos) => eksekusiAPIClockIn(`${pos.coords.latitude},${pos.coords.longitude}`, btn),
            (err) => { btn.innerHTML = 'Rekam Kehadiran'; btn.disabled = false; showStatus('Akses lokasi ditolak.', true); },
            { enableHighAccuracy: true, timeout: 10000 }
        );
    } else {
        await eksekusiAPIClockIn(`${coords.latitude},${coords.longitude}`, btn);
    }
}

async function eksekusiAPIClockIn(koordinat, btn) {
    try {
        const response = await fetch('/api/jadwal/clock-in', {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify({ lokasi: koordinat })
        });

        if(response.ok) {
            showStatus('Kehadiran berhasil dicatat.');
            btn.classList.replace('bg-blue-600', 'bg-slate-200');
            btn.classList.replace('text-white', 'text-slate-400');
            btn.innerHTML = 'Selesai (Terekam)';
            
            const btnOut = document.getElementById('btn-clockout');
            btnOut.classList.replace('bg-slate-200', 'bg-rose-500');
            btnOut.classList.replace('text-slate-400', 'text-white');
            btnOut.disabled = false;
            
            if(calendar) calendar.refetchEvents();
        } else {
            const err = await response.json();
            showStatus(err.error || 'Gagal merekam absensi.', true);
            btn.innerHTML = 'Coba Lagi (Clock-In)';
            btn.disabled = false;
            if(globalJadwalHariIni) globalJadwalHariIni.status = 'BELUM_MULAI';
        }
    } catch (error) {
        showStatus('Gagal menghubungi peladen.', true);
        btn.innerHTML = 'Coba Lagi (Clock-In)';
        btn.disabled = false;
        if(globalJadwalHariIni) globalJadwalHariIni.status = 'BELUM_MULAI';
    }
}

async function lakukanClockOut() {
    const btnOut = document.getElementById('btn-clockout');
    btnOut.innerHTML = 'Memproses...';
    btnOut.disabled = true;

    try {
        const response = await fetch('/api/jadwal/clock-out', {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if(response.ok) {
            showStatus('Sesi kerja diakhiri.');
            btnOut.classList.replace('bg-rose-500', 'bg-slate-200');
            btnOut.classList.replace('text-white', 'text-slate-400');
            btnOut.innerHTML = 'Selesai (Clock-Out)';
            if(calendar) calendar.refetchEvents();
        } else {
            const err = await response.json();
            showStatus(err.error || 'Gagal Clock-Out.', true);
            btnOut.innerHTML = 'Akhiri Sesi (Clock-Out)';
            btnOut.disabled = false;
        }
    } catch (error) {
        showStatus('Kesalahan jaringan.', true);
        btnOut.innerHTML = 'Akhiri Sesi (Clock-Out)';
        btnOut.disabled = false;
    }
}

// ==========================================
// PEMANGGILAN INISIALISASI
// ==========================================
fetchMyProfile();
initCalendar();