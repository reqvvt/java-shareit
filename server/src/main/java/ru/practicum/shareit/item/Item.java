package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    @Column(name = "owner_id")
    private Integer ownerId;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    public Item(Integer id, String name, String description, Boolean available, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.itemRequest = itemRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(available, item.available) && Objects.equals(ownerId, item.ownerId) && Objects.equals(itemRequest, item.itemRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, ownerId, itemRequest);
    }
}
