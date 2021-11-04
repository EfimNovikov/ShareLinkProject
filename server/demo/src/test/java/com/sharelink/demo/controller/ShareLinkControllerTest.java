package com.sharelink.demo.controller;

import com.google.gson.Gson;
import com.sharelink.demo.dto.NewShareObjectDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class ShareLinkControllerTest {

    // TODO: add tests for sessions

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void newShareCreationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/newShare/").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new NewShareObjectDTO("test"))))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareObject", is("test")))
                .andExpect(jsonPath("$.displayCode", notNullValue()))
                .andExpect(jsonPath("$.createdTime", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Disabled("Disabled until production")
    @Test
    public void reCaptchaEmptyTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/newShare/").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new NewShareObjectDTO("test"))))
                .andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void getSharesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getShares/"))
                .andExpect(status().isOk());
    }

    @Test
    public void getShareTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getShare/0000"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getShare/-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareObject", nullValue()))
                .andExpect(jsonPath("$.displayCode", nullValue()))
                .andExpect(jsonPath("$.createdTime", nullValue()))
                .andExpect(jsonPath("$.id", is(0)));
    }

    @Test
    public void searchSanityCheckTestAndResultTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/search/").param("term", "0"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/search/").param("term", "malicious input"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }


    @Test
    public void modifyShareTestOnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/changeShare/10052").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new NewShareObjectDTO("test"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteShareTestOnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/deleteShare/10052"))
                .andExpect(status().isForbidden());
    }
}