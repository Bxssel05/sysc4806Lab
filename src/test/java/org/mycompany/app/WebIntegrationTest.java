package org.mycompany.app;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JsonMapper json;

    private JsonNode create(String path, String body) throws Exception {
        return json.readTree(mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    @Test
    void createsAssociatesAndRendersPersistedBuddiesAcrossRequests() throws Exception {
        long book = create("/api/addressbooks", "{}").get("id").asLong();
        mvc.perform(get("/addressbooks/" + book + "/view"))
                .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("no buddies yet")));
        long first = create("/api/buddies", "{\"name\":\"Ada Lovelace\",\"phoneNumber\":\"555-0100\"}").get("id").asLong();
        long second = create("/api/buddies", "{\"name\":\"Grace Hopper\",\"phoneNumber\":\"555-0101\"}").get("id").asLong();
        for (long buddy : new long[]{first, second, first}) {
            mvc.perform(post("/api/addressbooks/" + book + "/buddies/" + buddy)).andExpect(status().isOk());
        }
        mvc.perform(get("/api/addressbooks/" + book)).andExpect(status().isOk())
                .andExpect(jsonPath("$.buddies.length()").value(2))
                .andExpect(jsonPath("$.buddies[*].name", org.hamcrest.Matchers.containsInAnyOrder("Ada Lovelace", "Grace Hopper")));
        String html = mvc.perform(get("/addressbooks/" + book + "/view"))
                .andExpect(status().isOk()).andExpect(view().name("addressbook"))
                .andReturn().getResponse().getContentAsString();
        assertThat(html).contains("Ada Lovelace", "Grace Hopper", "555-0100", "555-0101");
    }

    @Test
    void missingIdsReturn404() throws Exception {
        mvc.perform(get("/api/addressbooks/9223372036854775807")).andExpect(status().isNotFound());
        mvc.perform(post("/api/addressbooks/9223372036854775807/buddies/1")).andExpect(status().isNotFound());
        long book = create("/api/addressbooks", "{}").get("id").asLong();
        mvc.perform(post("/api/addressbooks/" + book + "/buddies/9223372036854775807")).andExpect(status().isNotFound());
        mvc.perform(get("/addressbooks/9223372036854775807/view"))
                .andExpect(status().isNotFound()).andExpect(content().string(org.hamcrest.Matchers.containsString("Address book not found")));
    }
}
