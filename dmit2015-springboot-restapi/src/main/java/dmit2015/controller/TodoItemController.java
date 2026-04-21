package dmit2015.controller;

import dmit2015.entity.TodoItem;
import dmit2015.repository.TodoItemRepository;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/TodoItems")
public class TodoItemController {

    private final TodoItemRepository todoItemRepository;

    public TodoItemController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @PostMapping
    public void createNewItem(@RequestBody  TodoItem todoItem) {
        todoItemRepository.save(todoItem);
    }

    @GetMapping("{id}")
    public TodoItem getItemById(@PathVariable Long id) {
        return todoItemRepository.findById(id).orElseThrow();
    }

    @GetMapping
    public List<TodoItem> getItems() {
        return todoItemRepository.findAll();
    }

}
