package com.backend.mybungalow.service.impl;

import com.backend.mybungalow.dto.CreateRoomDto;
import com.backend.mybungalow.dto.RoomDto;
import com.backend.mybungalow.dto.UpdateRoomDto;
import com.backend.mybungalow.exception.ResourceNotFoundException;
import com.backend.mybungalow.model.Room;
import com.backend.mybungalow.repository.RoomRepository;
import com.backend.mybungalow.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {
    private final RoomRepository repository;

    public RoomServiceImpl(RoomRepository repository) {
        this.repository = repository;
    }

    @Override
    public RoomDto createRoom(CreateRoomDto dto) {
        Room r = Room.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .capacity(dto.getCapacity())
                .status(dto.getStatus())
                .build();
        Room saved = repository.save(r);
        return toDto(saved);
    }

    @Override
    public RoomDto updateRoom(Long id, UpdateRoomDto dto) {
        Room r = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        Consumer<Runnable> applyIf = runnable -> { runnable.run(); };
        if (dto.getName() != null) r.setName(dto.getName());
        if (dto.getDescription() != null) r.setDescription(dto.getDescription());
        if (dto.getPrice() != null) r.setPrice(dto.getPrice());
        if (dto.getCapacity() != null) r.setCapacity(dto.getCapacity());
        if (dto.getStatus() != null) r.setStatus(dto.getStatus());
        Room updated = repository.save(r);
        return toDto(updated);
    }

    @Override
    public void deleteRoom(Long id) {
        Room r = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        repository.delete(r);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDto getRoom(Long id) {
        Room r = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return toDto(r);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomDto> listRooms(Pageable pageable) {
        Page<Room> page = repository.findAll(pageable);
        return page.map(this::toDto);
    }

    private RoomDto toDto(Room r) {
        RoomDto dto = new RoomDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setDescription(r.getDescription());
        dto.setPrice(r.getPrice());
        dto.setCapacity(r.getCapacity());
        dto.setStatus(r.getStatus());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}
