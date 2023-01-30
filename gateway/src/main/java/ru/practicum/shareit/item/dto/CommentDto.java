package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    @NotBlank
    @Size(min = 1)
    private String text;
    private Integer itemId;
    private String authorName;
    private LocalDateTime created;
}
