package xs.test2.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnumControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private EnumController enumController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enumController).build();
    }

    @Test
    void getGenders_returnsAllGenders() throws Exception {
        mockMvc.perform(get("/api/enum/gender"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].value").exists())
                .andExpect(jsonPath("$[0].label").exists())
                .andExpect(jsonPath("$[1].value").exists())
                .andExpect(jsonPath("$[2].value").exists())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getIdentifierTypes_returnsAllTypes() throws Exception {
        mockMvc.perform(get("/api/enum/identifier-type"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].value").exists())
                .andExpect(jsonPath("$[0].label").exists())
                .andExpect(jsonPath("$[1].value").exists())
                .andExpect(jsonPath("$[2].value").exists())
                .andExpect(jsonPath("$[3].value").exists())
                .andExpect(jsonPath("$.length()").value(4));
    }
}
