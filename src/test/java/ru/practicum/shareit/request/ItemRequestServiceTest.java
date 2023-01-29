package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebMvcTest(ItemRequestService.class)
@AutoConfigureMockMvc
class ItemRequestServiceTest {
    ItemRequestService itemRequestService;
    @MockBean
    ItemRequestRepository itemRequestRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    User user;
    User user2;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;
    ItemRequestDto itemRequestDto;
    ItemRequestResponse itemRequestResponse;

    @BeforeEach
    void init() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User(1, "testName", "test@mail.ru");
        user2 = new User(2, "testName2", "test2@mail.ru");
        itemRequest = new ItemRequest(1, "descr", user, LocalDateTime.now());
        itemRequest2 = new ItemRequest(2, "desc2", user2, LocalDateTime.now().plusHours(2));
        itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
        itemRequestResponse = new ItemRequestResponse(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getCreated(), null);
    }

    @Test
    void create() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto res = itemRequestService.create(user.getId(), itemRequestDto);

        assertNotNull(res);
        assertEquals(itemRequestDto.getId(), res.getId());
        assertEquals(itemRequestDto.getDescription(), res.getDescription());
    }

    @Test
    void createNotFoundException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(user.getId(), itemRequestDto));
    }

    @Test
    void getAll() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestResponse> res = itemRequestService.getAll(user.getId());

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void getAllOtherUser() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest2));

        List<ItemRequestResponse> res = itemRequestService.getAllByOtherUsers(user.getId(), 0, 2);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void getById() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestResponse res = itemRequestService.getById(user.getId(), itemRequest.getId());

        assertNotNull(res);
        assertEquals(itemRequestDto.getId(), res.getId());
        assertEquals(itemRequestDto.getDescription(), res.getDescription());
    }

    @Test
    void getByIdNotFoundException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(user.getId(), itemRequest.getId()));
    }
}
