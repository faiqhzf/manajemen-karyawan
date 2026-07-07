package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.KaryawanResponseDTO;
import com.karyawan.karyawan.exception.ResourceNotFoundException;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.repository.KaryawanRepository;
import com.karyawan.karyawan.repository.PenggunaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KaryawanServiceTest {

    @Mock
    private KaryawanRepository karyawanRepository;

    @Mock
    private PenggunaRepository penggunaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private KaryawanService karyawanService;

    @Test
    void getKaryawanById_Success() {
        Karyawan mockKaryawan = new Karyawan();

        mockKaryawan.setId(1); 
        mockKaryawan.setNama("Faiq Hudzaifah");
        mockKaryawan.setDepartemen("IT");

        when(karyawanRepository.findById(1)).thenReturn(Optional.of(mockKaryawan));

        // Menerima hasil DTO
        KaryawanResponseDTO result = karyawanService.getKaryawanById(1L);

        assertNotNull(result);
        
        // Membaca Java Record secara langsung (native pattern)
        assertEquals("Faiq Hudzaifah", result.nama());
        assertEquals("IT", result.departemen());
        
        verify(karyawanRepository, times(1)).findById(1);
    }

    @Test
    void getKaryawanById_NotFound_ShouldThrowException() {
        when(karyawanRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            karyawanService.getKaryawanById(99L);
        });

        verify(karyawanRepository, times(1)).findById(99);
    }
}