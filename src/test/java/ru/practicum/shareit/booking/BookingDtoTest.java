package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serializeTest() throws IOException {
        User user = new User(1, "testName", "test@mail.ru");
        Item item = new Item(1, "item", "descrItem", true, user.getId(), null);
        BookingDto dto = new BookingDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item,
                user, BookingStatus.APPROVED);

        JsonContent<BookingDto> res = json.write(dto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(res).extractingJsonPathNumberValue("$.item.id").isEqualTo(dto.getItem().getId());
    }

}
