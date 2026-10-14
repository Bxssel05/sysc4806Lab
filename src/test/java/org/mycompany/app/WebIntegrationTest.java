package org.mycompany.app;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
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
    void creationResponsesContainSavedValuesAndLocations() {
        RestTestClient client = RestTestClient.bindTo(mvc).build();
        var bookResponse = client.post().uri("/api/addressbooks")
                .contentType(MediaType.APPLICATION_JSON).body("{}")
                .exchange().expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(AddressBook.class).returnResult();
        AddressBook book = bookResponse.getResponseBody();
        assertThat(book).isNotNull();
        assertThat(book.getId()).isPositive();
        assertThat(book.getBuddies()).isEmpty();
        assertThat(bookResponse.getResponseHeaders().getLocation())
                .hasToString("/api/addressbooks/" + book.getId());

        client.get().uri("/api/addressbooks/" + book.getId())
                .exchange().expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.id").isEqualTo(book.getId())
                .jsonPath("$.buddies").isEmpty();

        var buddyResponse = client.post().uri("/api/buddies")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\":\"Alan Turing\",\"phoneNumber\":\"555-0102\"}")
                .exchange().expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(BuddyInfo.class).returnResult();
        BuddyInfo buddy = buddyResponse.getResponseBody();
        assertThat(buddy).isNotNull();
        assertThat(buddy.getId()).isPositive();
        assertThat(buddy.getName()).isEqualTo("Alan Turing");
        assertThat(buddy.getPhoneNumber()).isEqualTo("555-0102");
        assertThat(buddyResponse.getResponseHeaders().getLocation())
                .hasToString("/api/buddies/" + buddy.getId());
    }

    @Test
    void createsAssociatesAndRendersPersistedBuddiesAcrossRequests() throws Exception {
        long book = create("/api/addressbooks", "{}").get("id").asLong();
        mvc.perform(get("/addressbooks/" + book + "/view"))
                .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("no buddies yet")));
        long first = create("/api/buddies", "{\"name\":\"Ada Lovelace\",\"phoneNumber\":\"555-0100\"}").get("id").asLong();
        long second = create("/api/buddies", "{\"name\":\"Grace Hopper\",\"phoneNumber\":\"555-0101\"}").get("id").asLong();
        for (long buddy : new long[]{first, second, first}) {
            mvc.perform(post("/api/addressbooks/" + book + "/buddies/" + buddy)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(book))
                    .andExpect(jsonPath("$.buddies[*].id", org.hamcrest.Matchers.hasItem((int) buddy)));
        }
        mvc.perform(get("/api/addressbooks/" + book)).andExpect(status().isOk())
                .andExpect(jsonPath("$.buddies.length()").value(2))
                .andExpect(jsonPath("$.buddies[*].phoneNumber", org.hamcrest.Matchers.containsInAnyOrder("555-0100", "555-0101")))
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
