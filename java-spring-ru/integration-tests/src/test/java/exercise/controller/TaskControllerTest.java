package exercise.controller;

import exercise.exception.ResourceNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import org.instancio.Instancio;
import org.instancio.Select;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.time.LocalDate;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import exercise.repository.TaskRepository;
import exercise.model.Task;

// BEGIN
@SpringBootTest
@AutoConfigureMockMvc
// END
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    private final LocalDate date = LocalDate.now();

    @Test
    public void testWelcomePage() throws Exception {
        var result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).contains("Welcome to Spring!");
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }


    // BEGIN
    @Test
    @SneakyThrows
    public void shouldReturnTask() {
        var task = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getTitle), () -> faker.beer().brand())
                .supply(Select.field(Task::getDescription), () -> faker.lorem().sentence())
                .create();
        taskRepository.save(task);

        var result = mockMvc
                .perform(get("/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isEqualTo(om.writeValueAsString(task));
    }

    @Test
    @SneakyThrows
    public void shouldReturnTaskNotFound() {
        var notExistingTaskId = taskRepository.count() + 1;
        var result = mockMvc
                .perform(get("/tasks/" + notExistingTaskId))
                .andExpect(status().isNotFound())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).isEqualTo("Task with id " + notExistingTaskId + " not found");
    }

    @Test
    @SneakyThrows
    public void shouldCreateTask() {
        var title = faker.beer().brand();
        var description = faker.lorem().sentence();
        var task = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getTitle), () -> title)
                .supply(Select.field(Task::getDescription), () -> description)
                .create();
        taskRepository.save(task);

        var request = post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(task));

        var result = mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isEqualTo(om.writeValueAsString(task));

        task = taskRepository.findById(task.getId()).get();
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getDescription()).isEqualTo(description);
        assertThat(task.getCreatedAt()).isEqualTo(date);
        assertThat(task.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    @SneakyThrows
    public void shouldUpdateTask() {
        var initTitle = faker.beer().brand();
        var initDescription = faker.lorem().sentence();
        var task = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getTitle), () -> initTitle)
                .supply(Select.field(Task::getDescription), () -> initDescription)
                .create();
        taskRepository.save(task);

        var updTitle = faker.beer().brand();
        var updDescription = faker.lorem().sentence();
        var requestBody = new HashMap<String, String>();
        requestBody.put("title", updTitle);
        requestBody.put("description", updDescription);
        var request = put("/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestBody));

        var result = mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var responseBody = result.getResponse().getContentAsString();

        assertThatJson(responseBody)
                .isObject()
                .containsAllEntriesOf(requestBody);

        task = taskRepository.findById(task.getId()).get();
        assertThat(task.getTitle()).isEqualTo(updTitle);
        assertThat(task.getDescription()).isEqualTo(updDescription);
        assertThat(task.getCreatedAt()).isEqualTo(date);
        assertThat(task.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    @SneakyThrows
    public void shouldDeleteTask() {
        var task = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getTitle), () -> faker.beer().brand())
                .supply(Select.field(Task::getDescription), () -> faker.lorem().sentence())
                .create();
        taskRepository.save(task);

        mockMvc
                .perform(delete("/tasks/" + task.getId()))
                .andExpect(status().isOk());

        var maybeTask = taskRepository.findById(task.getId());
        assertThat(maybeTask.isPresent()).isFalse();
    }
    // END
}
