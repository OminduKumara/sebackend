package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.CreateRoomDto;
import com.backend.mybungalow.dto.RoomDto;
import com.backend.mybungalow.dto.UpdateRoomDto;
import com.backend.mybungalow.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
@Validated

public class RoomController {
    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<RoomDto> create(@Valid @RequestBody CreateRoomDto dto) {
        RoomDto created = service.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> update(@PathVariable Long id, @Valid @RequestBody UpdateRoomDto dto) {
        RoomDto updated = service.updateRoom(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRoom(id));
    }


    @GetMapping
    public ResponseEntity<Page<RoomDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        Sort sortObj = Sort.by(Sort.Order.desc("id"));
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            sortObj = Sort.by(Sort.Direction.fromString(parts.length > 1 ? parts[1] : "desc"), parts[0]);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return ResponseEntity.ok(service.listRooms(pageable));

    }
}
