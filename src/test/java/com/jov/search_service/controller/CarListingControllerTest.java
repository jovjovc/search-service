package com.jov.search_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jov.search_service.dto.CarListingDto;
import com.jov.search_service.dto.CarListingSearchRequest;
import com.jov.search_service.model.CarListing;
import com.jov.search_service.service.CarListingSearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarListingController.class)
class CarListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarListingSearchService carListingSearchService;

    @Autowired
    private ObjectMapper objectMapper;

    private final CarListingDto carListingDto = new CarListingDto( "Toyota", "Corolla");;

    private final CarListing carListing = new CarListing("1", "Toyota", "Corolla");


    @Test
    void testGetAllCars_shouldReturnPage() throws Exception {
        Page<CarListingDto> page = new PageImpl<>(List.of(carListingDto));
        Mockito.when(carListingSearchService.searchAllCars(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/car-listings")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "createdAt")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[0].model").value("Corolla"));
    }

    @Test
    void testSearchCarsByRequest_shouldReturnList() throws Exception {
        CarListingSearchRequest request = new CarListingSearchRequest("Toyota", "Corolla", "Blue", 2025);
        Mockito.when(carListingSearchService.searchCarsByRequest(any(CarListingSearchRequest.class)))
                .thenReturn(List.of(carListing));

        mockMvc.perform(post("/api/car-listings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Corolla"));
    }

    @Test
    void testSearchCarsByRequest_shouldReturnBadRequest() throws Exception {
        // invalid: negative year
        CarListingSearchRequest invalidRequest = new CarListingSearchRequest("Toyota", "Corolla", "Blue", -2025);

        mockMvc.perform(post("/api/car-listings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("year : must be greater than 0\n"));
    }

    @Test
    void testFullTextSearchCars_shouldReturnPage() throws Exception {
        Page<CarListingDto> page = new PageImpl<>(List.of(carListingDto));
        Mockito.when(carListingSearchService.fullTextSearchCars(eq("Toyota"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/car-listings/search")
                        .param("q", "Toyota")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[0].model").value("Corolla"));
    }

    @Test
    void testSearchByMake_shouldReturnList() throws Exception {
        Mockito.when(carListingSearchService.seachByMake("Toyota"))
                .thenReturn(List.of(carListingDto));

        mockMvc.perform(get("/api/car-listings/search/make/{make}", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].make").value("Toyota"));
    }
}