package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.KaryawanResponseDTO;
import com.karyawan.karyawan.service.KaryawanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class KaryawanControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KaryawanService karyawanService;

    @Test
    @WithMockUser(roles = "HRD")
    void whenHrdAccessGetAllKaryawan_thenStatusIsOk() throws Exception {
        when(karyawanService.getAllKaryawan()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/karyawan"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "KARYAWAN")
    void whenKaryawanAccessGetAllKaryawan_thenStatusIsForbidden() throws Exception {
        mockMvc.perform(get("/api/karyawan"))
               .andExpect(status().isForbidden());
    }

   @Test
    @WithMockUser(username = "faiq_hudzaifah", roles = "KARYAWAN")
    void whenKaryawanAccessMyProfile_thenStatusIsOk() throws Exception {
        
        KaryawanResponseDTO mockResponse = new KaryawanResponseDTO(
                1L, 
                "Faiq Hudzaifah", 
                "IT", 
                BigDecimal.valueOf(10000000), 
                "081234567890",
                "faiq_hudzaifah", 
                "/uploads/profil-photos/faiq_hudzaifah.jpg",
                12 
        );
        when(karyawanService.getKaryawanByUsername("faiq_hudzaifah")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/karyawan/me"))
               .andExpect(status().isOk());
    }
    
    @Test
    void whenUnauthenticatedAccess_thenStatusIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/karyawan"))
               .andExpect(status().isUnauthorized());
    }
}