// Copyright (c) UrbanGate
package com.urbangate.app.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = VersionController.class)
class VersionControllerTest {

  @Autowired private MockMvc mockMvc;

//  @Test
  void returnsVersionPayload() throws Exception {
    mockMvc
        .perform(get("/api/v1/version"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.service").value("urbangate-backend"))
        .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
        .andExpect(jsonPath("$.time").isNotEmpty());
  }
}
